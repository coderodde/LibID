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

public class BidirectionalBreadthFirstSearch<N> {
    
    public List<N> search(N source,
                          N target,
                          NodeExpander<N> forwardExpander, 
                          NodeExpander<N> backwardExpander) {
        Deque<N> queueForward = new ArrayDeque<>(Arrays.asList(source));
        Deque<N> queueBackward = new ArrayDeque<>(Arrays.asList(target));

        Map<N, N> parentsForward = new HashMap<>();
        Map<N, N> parentsBackward = new HashMap<>();

        Map<N, Integer> distancesForward = new HashMap<>();
        Map<N, Integer> distancesBackward = new HashMap<>();

        parentsForward.put(source, null);
        parentsBackward.put(target, null);
        
        distancesForward.put(source, 0);
        distancesBackward.put(target, 0);
        
        N touchNode = null;
        int bestPathLength = Integer.MAX_VALUE;
        
        while (!queueForward.isEmpty() && !queueBackward.isEmpty()) {
            if (touchNode != null && 
                    distancesForward.get(queueForward.getFirst()) +
                    distancesBackward.get(queueBackward.getFirst()) >= 
                    bestPathLength) {
                return tracebackPath(touchNode, 
                                     parentsForward,
                                     parentsBackward);
            }
            
            N u = queueForward.removeFirst();
            
            if (parentsBackward.containsKey(u)
                && bestPathLength > 
                    distancesForward.get(u) + distancesBackward.get(u)) {
                bestPathLength = 
                        distancesForward.get(u) + distancesBackward.get(u);
                touchNode = u;
            }
            
            for (N child : forwardExpander.expand(u)) {
                if (!parentsForward.containsKey(child)) {
                    parentsForward.put(child, u);
                    distancesForward.put(child, distancesForward.get(u) + 1);
                    queueForward.addLast(child);
                }
            }
            
            u = queueBackward.removeFirst();
            
            if (parentsForward.containsKey(u) 
                    && bestPathLength > distancesForward.get(u) +
                                        distancesBackward.get(u)) {
                bestPathLength = distancesForward.get(u) +
                                 distancesBackward.get(u);
                touchNode = u;
            }
            
            for (N parent : backwardExpander.expand(u)) {
                if (!parentsBackward.containsKey(parent)) {
                    parentsBackward.put(parent, u);
                    distancesBackward.put(parent, distancesBackward.get(u) + 1);
                    queueBackward.addLast(parent);
                }
            }
        }
        
        return Collections.EMPTY_LIST;
    }
    
    private List<N> tracebackPath(N touchNode,
                                  Map<N, N> parentsForward,
                                  Map<N, N> parentsBackward) {
        List<N> path = new ArrayList<>();
        N u = touchNode;
        
        while (u != null) {
            path.add(u);
            u = parentsForward.get(u);
        }
        
        Collections.reverse(path);
        u = parentsBackward.get(u);
        
        while (u != null) {
            path.add(u);
            u = parentsBackward.get(u);
        }
        
        return path;
    }
}
