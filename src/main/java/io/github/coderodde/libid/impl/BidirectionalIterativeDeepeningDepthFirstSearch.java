package io.github.coderodde.libid.impl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import io.github.coderodde.libid.NodeExpander;

public final class BidirectionalIterativeDeepeningDepthFirstSearch<N> {

    private final Deque<N> backwardSearchStack;
    private final Set<N> frontier;
    private final Set<N> visitedForward;
    private final Set<N> visitedBackward;
    private final NodeExpander<N> forwardExpander;
    private final NodeExpander<N> backwardExpander;
    private int previousVisitedSizeForward;
    private int previousVisitedSizeBackward;

    public BidirectionalIterativeDeepeningDepthFirstSearch() {
        this.backwardSearchStack = null;
        this.frontier            = null;
        this.visitedForward      = null;
        this.visitedBackward     = null;
        this.forwardExpander     = null;
        this.backwardExpander    = null;
    }

    private BidirectionalIterativeDeepeningDepthFirstSearch(
        NodeExpander<N> forwardExpander,
        NodeExpander<N> backwardExpander) {
        
        this.backwardSearchStack = new ArrayDeque<>();
        this.frontier            = new HashSet<>();
        this.visitedForward      = new HashSet<>();
        this.visitedBackward     = new HashSet<>();
        this.forwardExpander     = forwardExpander;
        this.backwardExpander    = backwardExpander;
    }

    public List<N> search(N source, 
                          N target, 
                          NodeExpander<N> forwardExpander,
                          NodeExpander<N> backwardExpander) {
        // Handle the easy case. We need this in order to terminate the 
        // recursion in buildPath.
        if (source.equals(target)) {
            return new ArrayList<>(Arrays.asList(source));
        }

        BidirectionalIterativeDeepeningDepthFirstSearch<N> state = 
                new BidirectionalIterativeDeepeningDepthFirstSearch<>(
                        forwardExpander,
                        backwardExpander);

        // The actual search routine:
        for (int forwardDepth = 0;; ++forwardDepth) {
            state.frontier.clear();
            state.visitedForward.clear();
            
            // Do a depth limited search in forward direction. Put all nodes at 
            // depth == 0 to the frontier.
            state.depthLimitedSearchForward(source,
                                            forwardDepth);
            
            
            if (state.visitedForward.size() == 
                state.previousVisitedSizeForward) {
                // No improvement since the last run of forward search. Return
                // 'null' indicating that there is no path from source to 
                // target:
                return null;
            }
            
            state.previousVisitedSizeForward = state.visitedForward.size();
            state.visitedBackward.clear();
            
            N meetingNode = 
                    state.depthLimitedSearchBackward(
                            target,
                            forwardDepth,
                            state.visitedBackward);
            
            if (meetingNode != null) {
                return state.buildPath(source, 
                                       meetingNode);
            }
            
            state.visitedBackward.clear();
            
            meetingNode = 
                    state.depthLimitedSearchBackward(
                            target, 
                            forwardDepth + 1, 
                            state.visitedBackward);
            
            if (meetingNode != null) {
                return state.buildPath(source,
                                       meetingNode);
            }
            
            if (state.visitedBackward.size() == 
                state.previousVisitedSizeBackward) {
                return null;
            }
            
            state.previousVisitedSizeBackward = 
            state.visitedBackward.size();
        }
    }

    private void depthLimitedSearchForward(N node, int depth) {
        if (visitedForward.contains(node)) {
            return;
        }
        
        visitedForward.add(node);
        
        if (depth == 0) {
            frontier.add(node);
            return;
        }

        for (N child : forwardExpander.expand(node)) {
            depthLimitedSearchForward(child,
                                      depth - 1);
        }
    }

    private N depthLimitedSearchBackward(N node, 
                                         int depth,
                                         Set<N> visited) {
        if (visited.contains(node)) {
            return null;
        }
        
        backwardSearchStack.addFirst(node);

        if (depth == 0) {
            if (frontier.contains(node)) {
                return node;
            }

            backwardSearchStack.removeFirst();
            return null;
        }
        
        visited.add(node);  

        for (N parent : backwardExpander.expand(node)) {
            N meetingNode = depthLimitedSearchBackward(parent,
                                                       depth - 1, 
                                                       visited);
            if (meetingNode != null) {
                return meetingNode;
            } 
        }

        backwardSearchStack.removeFirst();
        return null;
    }

    private List<N> buildPath(N source, N meetingNode) {
        List<N> path = new ArrayList<>();
        List<N> prefixPath = 
                new BidirectionalIterativeDeepeningDepthFirstSearch<N>()
                        .search(source, 
                                meetingNode, 
                                forwardExpander, 
                                backwardExpander);
        path.addAll(prefixPath);
        path.remove(path.size() - 1);
        path.addAll(backwardSearchStack);
        return path;
    }
}
