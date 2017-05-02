package net.coderodde.libid.support;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.coderodde.libid.NodeExpander;

public class BreadthFirstSearch<N> {

    public List<N> search(N source, N target, NodeExpander<N> expander) {
        Deque<N> queue = new ArrayDeque<>(Arrays.asList(source));
        Map<N, N> parents = new HashMap<>();
        parents.put(source, null);
        
        while (!queue.isEmpty()) {
            N current = queue.removeFirst();
            
            if (current.equals(target)) {
                return tracebackPath(current, parents);
            }
            
            for (N child : expander.expand(current)) {
                if (!parents.containsKey(child)) {
                    parents.put(child, current);
                    queue.addLast(child);
                }
            }
        }
        
        throw new IllegalStateException("target not reachable");
    }
    
    private static <N> List<N> tracebackPath(N target, Map<N, N> parents) {
        List<N> path = new ArrayList<>();
        N current = target;
        
        while (current != null) {
            path.add(current);
            current = parents.get(current);
        }
        
        Collections.<N>reverse(path);
        return path;
    }
}
