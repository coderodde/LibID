package net.coderodde.libid.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.coderodde.libid.NodeExpander;

public final class BidirectionalIterativeDeepeningDepthFirstSearch<N> {

    private final List<N> forwardSearchStack;
    private final List<N> backwardSearchStack;
    private final Set<N> frontier;
    private final NodeExpander<N> forwardExpander;
    private final NodeExpander<N> backwardExpander;
    private boolean found = false;
    private int reachedNodesInForwardSearch          = 0;
    private int previousReachedNodesInForwardSearch  = 0;
    private int reachedNodesInBackwardSearch         = 0;
    private int previousReachedNodesInBackwardSearch = 0;
    
    public BidirectionalIterativeDeepeningDepthFirstSearch() {
        this.forwardSearchStack = null;
        this.backwardSearchStack = null;
        this.frontier = null;
        this.forwardExpander = null;
        this.backwardExpander = null;
    }
    
    private BidirectionalIterativeDeepeningDepthFirstSearch(
        NodeExpander<N> forwardExpander,
        NodeExpander<N> backwardExpander) {
        this.forwardSearchStack = new ArrayList<>();
        this.backwardSearchStack = new ArrayList<>();
        this.frontier = new HashSet<>();
        this.forwardExpander = forwardExpander;
        this.backwardExpander = backwardExpander;
    }
    
    public List<N> search(N source, 
                          N target, 
                          NodeExpander<N> forwardExpander,
                          NodeExpander<N> backwardExpander) {
        BidirectionalIterativeDeepeningDepthFirstSearch<N> state = 
                new BidirectionalIterativeDeepeningDepthFirstSearch<>(
                        forwardExpander,
                        backwardExpander);
        
        for (int depth = 0;; ++depth) {
            // Do a depth limited search in forward direction. Put all nodes at 
            // depth == 0 to the frontier.
            state.depthLimitedSearchForward(source, depth);
            
            if (state.previousReachedNodesInForwardSearch == 
                    state.reachedNodesInForwardSearch) {
                System.out.println("1: " + state.previousReachedNodesInForwardSearch + ", " +
                        state.reachedNodesInForwardSearch);
                throw new IllegalStateException("target not reachable");
            }
            
            state.previousReachedNodesInForwardSearch =
                    state.reachedNodesInForwardSearch;
            
            // Perform a reversed search starting from the target node and 
            // recurring to the depth 'depth'.
            state.depthLimitedSearchBackward(target, depth);
            
            if (state.found) {
                return state.buildPath();
            }
            
            if (state.previousReachedNodesInBackwardSearch ==
                    state.reachedNodesInBackwardSearch) {
                System.out.println("2: " + state.previousReachedNodesInBackwardSearch + ", " +
                        state.reachedNodesInBackwardSearch);
                throw new IllegalStateException("target not reachable");
            }
            
            state.previousReachedNodesInBackwardSearch =
                    state.reachedNodesInBackwardSearch;
            
            // Perform a reversed search once again with depth = 'depth + 1'.
            // We need this in case the shortest path has odd number of arcs.
            state.depthLimitedSearchBackward(target, depth + 1);
            
            if (state.found) {
                return buildPath();
            }
            
            if (state.previousReachedNodesInBackwardSearch ==
                    state.reachedNodesInBackwardSearch) {
                System.out.println("3: " + state.previousReachedNodesInBackwardSearch + ", " +
                        state.reachedNodesInBackwardSearch);
                throw new IllegalStateException("target not reachable");
            }
            
            state.previousReachedNodesInBackwardSearch =
                    state.reachedNodesInBackwardSearch;
            
            state.frontier.clear();
        }
    }
    
    private void depthLimitedSearchForward(N node, int depth) {
        ++reachedNodesInForwardSearch;
        forwardSearchStack.add(node);
        
        if (depth == 0) {
            frontier.add(node);
            return;
        }
        
        for (N child : forwardExpander.expand(node)) {
            depthLimitedSearchForward(child, depth - 1);
        }
    }
    
    private void depthLimitedSearchBackward(N node, int depth) {
        ++reachedNodesInBackwardSearch;
        backwardSearchStack.add(node);
        
        if (depth == 0) {
            found = frontier.contains(node);
            return;
        }
        
        for (N parent : backwardExpander.expand(node)) {
            depthLimitedSearchBackward(parent, depth - 1);
        }
    }
    
    private List<N> buildPath() {
        List<N> path = new ArrayList<>(forwardSearchStack.size() + 
                                       backwardSearchStack.size());
        Collections.<N>reverse(backwardSearchStack);
        path.addAll(forwardSearchStack);
        path.addAll(backwardSearchStack);
        return path;
    }
}
