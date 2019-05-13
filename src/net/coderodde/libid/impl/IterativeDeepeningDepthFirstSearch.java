package net.coderodde.libid.impl;

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
        }
    }
    
    private void depthLimitedSearch(N node, int depth) {
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
