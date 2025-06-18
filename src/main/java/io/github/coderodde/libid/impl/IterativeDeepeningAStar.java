package io.github.coderodde.libid.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import io.github.coderodde.libid.IntHeuristicFunction;
import io.github.coderodde.libid.NodeExpander;

public final class IterativeDeepeningAStar<N> {

    private static final int RUNNING = 0;
    private static final int FOUND = 1;
    
    private final N target;
    private final IntHeuristicFunction<N> heuristicFunction;
    private final NodeExpander<N> expander;
    private final List<N> path;
    
    private int status = RUNNING;
    
    public IterativeDeepeningAStar() {
        this.target = null;
        this.heuristicFunction = null;
        this.expander = null;
        this.path = null;
    }
    
    private IterativeDeepeningAStar(
            N target,
            IntHeuristicFunction<N> heuristicFunction,
            NodeExpander<N> expander) {
        this.target = target;
        this.heuristicFunction = heuristicFunction;
        this.expander = expander;
        this.path = new ArrayList<>();
    }
    
    public List<N> search(N source, 
                          N target, 
                          NodeExpander<N> expander, 
                          IntHeuristicFunction<N> heuristicFunction) {
        IterativeDeepeningAStar<N> state = 
                new IterativeDeepeningAStar<>(target,
                                              heuristicFunction, 
                                              expander);
        
        int bound = heuristicFunction.estimate(source, target);
        
        while (true) {
            int t = state.search(source, 0, bound);
            
            if (state.status == FOUND) {
                Collections.<N>reverse(state.path);
                return state.path;
            }
            
            if (t == Integer.MAX_VALUE) {
                throw new IllegalStateException("Target not reachable");
            }
            
            bound = t;
        }
    }
    
    private int search(N node, int g, int bound) {
        int f = g + heuristicFunction.estimate(node, target);
        
        if (f > bound) {
            return f;
        }
        
        if (node.equals(target)) {
            path.add(target);
            status = FOUND;
            return 0;
        }
        
        int min = Integer.MAX_VALUE;
        
        for (N child : expander.expand(node)) {
            int t = search(child, g + 1, bound);
            
            if (status == FOUND) {
                path.add(node);
                return 0;
            }
            
            min = Math.min(min, t);
        }
        
        return min;
    }
}
