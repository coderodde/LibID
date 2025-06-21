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

    private final N source;
    private final Deque<N> backwardSearchStack;
    private final Set<N> frontier;
    private final Set<N> visitedForward;
    private final Set<N> visitedBackward;
    private final NodeExpander<N> forwardExpander;
    private final NodeExpander<N> backwardExpander;
    private int previousVisitedSizeForward;
    private int previousVisitedSizeBackward;

    public BidirectionalIterativeDeepeningDepthFirstSearch() {
        this.source              = null;
        this.backwardSearchStack = null;
        this.frontier            = null;
        this.visitedForward      = null;
        this.visitedBackward     = null;
        this.forwardExpander     = null;
        this.backwardExpander    = null;
    }

    private BidirectionalIterativeDeepeningDepthFirstSearch(
        N source,
        NodeExpander<N> forwardExpander,
        NodeExpander<N> backwardExpander) {
        this.source              = source;
        this.backwardSearchStack = new ArrayDeque<>();
        this.frontier            = new HashSet<>();
        this.visitedForward      = new HashSet<>();
        this.visitedBackward     = new HashSet<>();
        this.forwardExpander     = forwardExpander;
        this.backwardExpander    = backwardExpander;
        this.previousVisitedSizeForward  = 0;
        this.previousVisitedSizeBackward = 0;
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

        for (int forwardDepth = 0;; ++forwardDepth) {
            state.visitedForward.clear();
            
            // Do a depth limited search in forward direction. Put all nodes at 
            // depth == 0 to the frontier.
            state.depthLimitedSearchForward(source, forwardDepth);
            
            if (state.visitedForward.size() == 
                state.previousVisitedSizeForward) {
                return null;
            }
            
            state.previousVisitedSizeForward = state.visitedForward.size();
            state.visitedForward.clear();
            
            for (int backwardDepth = forwardDepth;
                     backwardDepth < forwardDepth + 2;
                     backwardDepth++) {
                
                N meetingNode = state.depthLimitedSearchBackward(target,
                                                                 backwardDepth);

                if (meetingNode != null) {
                    return state.buildPath(meetingNode);
                }

                if (state.visitedForward.size() == 
                    state.previousVisitedSizeForward) {
                    return null;
                }

                state.previousVisitedSizeBackward =
                        state.visitedBackward.size();
                
                state.visitedBackward.clear();
            }
            
            state.frontier.clear();
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
            depthLimitedSearchForward(child, depth - 1);
        }
    }

    private N depthLimitedSearchBackward(N node, int depth) {
        if (visitedBackward.contains(node)) {
            return null;
        }
        
        visitedBackward.add(node);  
        backwardSearchStack.addFirst(node);

        if (depth == 0) {
            if (frontier.contains(node)) {
                return node;
            }

            backwardSearchStack.removeFirst();
            return null;
        }

        for (N parent : backwardExpander.expand(node)) {
            N meetingNode = depthLimitedSearchBackward(parent, depth - 1);

            if (meetingNode != null) {
                return meetingNode;
            } 
        }

        backwardSearchStack.removeFirst();
        return null;
    }

    private List<N> buildPath(N meetingNode) {
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
