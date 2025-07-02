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

    private final Deque<N> forwardSearchStack;
    private final Deque<N> backwardSearchStack;
    private final Set<N> frontier;
    private final Set<N> visitedForward;
    private final Set<N> visitedBackwardStep1;
    private final Set<N> visitedBackwardStep2;
    private final NodeExpander<N> forwardExpander;
    private final NodeExpander<N> backwardExpander;
    private int previousVisitedSizeForward;
    private int previousVisitedSizeBackward;

    public BidirectionalIterativeDeepeningDepthFirstSearch() {
        this.forwardSearchStack   = null;
        this.backwardSearchStack  = null;
        this.frontier             = null;
        this.visitedForward       = null;
        this.visitedBackwardStep1 = null;
        this.visitedBackwardStep2 = null;
        this.forwardExpander      = null;
        this.backwardExpander     = null;
    }

    private BidirectionalIterativeDeepeningDepthFirstSearch(
        N source,
        NodeExpander<N> forwardExpander,
        NodeExpander<N> backwardExpander) {
        
        this.forwardSearchStack   = new ArrayDeque<>();
        this.backwardSearchStack  = new ArrayDeque<>();
        this.frontier             = new HashSet<>();
        this.visitedForward       = new HashSet<>();
        this.visitedBackwardStep1 = new HashSet<>();
        this.visitedBackwardStep2 = new HashSet<>();
        this.forwardExpander      = forwardExpander;
        this.backwardExpander     = backwardExpander;
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
                        source,
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
            state.visitedBackwardStep1.clear();
            
            N meetingNode = 
                    state.depthLimitedSearchBackward(
                            target,
                            forwardDepth,
                            state.visitedBackwardStep1);
            
            if (meetingNode != null) {
                return state.buildPath();
            }
            
            state.visitedBackwardStep2.clear();
            
            meetingNode = 
                    state.depthLimitedSearchBackward(
                            target, 
                            forwardDepth + 1, 
                            state.visitedBackwardStep2);
            
            if (meetingNode != null) {
                return state.buildPath();
            }
            
            if (state.visitedBackwardStep2.size() == 
                state.previousVisitedSizeBackward) {
                return null;
            }
            
            state.forwardSearchStack.clear();
            state.previousVisitedSizeBackward = 
            state.visitedBackwardStep2.size();
        }
    }

    private void depthLimitedSearchForward(N node, int depth) {
        if (visitedForward.contains(node)) {
            return;
        }
        
        visitedForward.add(node);
        forwardSearchStack.addLast(node);
        
        if (depth == 0) {
            if (!forwardSearchStack.isEmpty()) {
                forwardSearchStack.removeLast();
            }
                
            frontier.add(node);
            return;
        }

        for (N child : forwardExpander.expand(node)) {
            depthLimitedSearchForward(child,
                                      depth - 1);
        }
        
        forwardSearchStack.removeLast();
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
            visited.add(node);
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

    private List<N> buildPath() {
        List<N> path = new ArrayList<>(forwardSearchStack .size() + 
                                       backwardSearchStack.size());
        
        path.addAll(forwardSearchStack);
        path.addAll(backwardSearchStack);
        
        return path;
    }
}
