package io.github.coderodde.libid.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import io.github.coderodde.libid.NodeExpander;
import java.util.HashSet;
import java.util.Set;

public final class IterativeDeepeningDepthFirstSearch<N> {

    private final N target;
    private final List<N> path;
    private final Set<N> visitedSet;
    private final NodeExpander<N> expander;
    private boolean found = false;
    private int previousVisitedSize = 0;
    
    public IterativeDeepeningDepthFirstSearch() {
        this.target     = null;
        this.path       = null;
        this.expander   = null;
        this.visitedSet = null;
    }
    
    private IterativeDeepeningDepthFirstSearch(N target, 
                                               NodeExpander<N> expander) {
        this.target = Objects.requireNonNull(target, "target is null");
        this.path = new ArrayList<>();
        this.expander = expander;
        this.visitedSet = new HashSet<>();
    }
    
    public List<N> search(N source, N target, NodeExpander<N> expander) {
        IterativeDeepeningDepthFirstSearch state = 
                new IterativeDeepeningDepthFirstSearch(target, expander);
        
        for (int depth = 0;; ++depth) {
            state.depthLimitedSearch(source, depth);
            
            if (state.found) {
                Collections.<N>reverse(state.path);
                return state.path;
            }
            
            if (state.previousVisitedSize == state.visitedSet.size()) {
                return null;
            }
            
            state.previousVisitedSize = state.visitedSet.size();
            state.visitedSet.clear();
            state.path.clear();
        }
    }
    
    private void depthLimitedSearch(N node, int depth) {
        if (depth == 0 && node.equals(target)) {
            found = true;
            path.add(node);
            return;
        }
        
        if (visitedSet.contains(node)) {
            return;
        }
        
        visitedSet.add(node);
        
        if (depth > 0) {
            for (N child : expander.expand(node)) {
                if (visitedSet.contains(child)) {
                    System.out.println("1");
                    continue;
                }
                
                depthLimitedSearch(child, depth - 1);
                
                if (found) {
                    path.add(node);
                    return;
                }
            }
        }
    }
}
