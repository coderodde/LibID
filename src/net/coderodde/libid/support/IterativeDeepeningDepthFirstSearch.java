package net.coderodde.libid.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.coderodde.libid.NodeExpander;
import net.coderodde.libid.SlidingTilePuzzleNode;

public final class IterativeDeepeningDepthFirstSearch<N> {

    private final N target;
    private final List<N> path;
    private final NodeExpander<N> expander;
    private boolean found = false;
    private int reachedNodes = 0;
    private int previousReachedNodes = 0;
    
    public IterativeDeepeningDepthFirstSearch() {
        this.target = null;
        this.path = null;
        this.expander = null;
    }
    
    private IterativeDeepeningDepthFirstSearch(N target, 
                                               NodeExpander<N> expander) {
        this.target = Objects.requireNonNull(target, "target is null");
        this.path = new ArrayList<>();
        this.expander = expander;
    }
    
    public List<N> search(N source, N target, NodeExpander<N> expander) {
        IterativeDeepeningDepthFirstSearch state = 
                new IterativeDeepeningDepthFirstSearch(target, expander);
        
        for (int depth = 0;; ++depth) {
            state.depthLimitedSearch(source, depth);
            
            if (state.found) {
                Collections.<SlidingTilePuzzleNode>reverse(state.path);
                return state.path;
            }
            
            if (state.reachedNodes == state.previousReachedNodes) {
                throw new IllegalStateException("target not reachable");
            }
            
            state.previousReachedNodes = state.reachedNodes;
            state.reachedNodes = 0;
        }
    }
    
    private void depthLimitedSearch(N node, int depth) {
        ++reachedNodes;
        
        if (depth == 0 && node.equals(target)) {
            found = true;
            path.add(node);
            return;
        }
        
        if (depth > 0) {
            for (N child : expander.expand(node)) {
                depthLimitedSearch(child, depth - 1);
                
                if (found) {
                    path.add(node);
                    return;
                }
            }
        }
    }
}