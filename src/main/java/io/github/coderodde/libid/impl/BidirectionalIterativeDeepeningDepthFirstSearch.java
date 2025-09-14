package io.github.coderodde.libid.impl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import io.github.coderodde.libid.NodeExpander;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class BidirectionalIterativeDeepeningDepthFirstSearch<N> {

    public List<N> search(N source, 
                          N target, 
                          NodeExpander<N> forwardExpander,
                          NodeExpander<N> backwardExpander) {
        // Handle the easy case. We need this in order to terminate the 
        // recursion in buildPath. Otherwise, if 'source' and 'target' are the
        // same nodes, a stack overflow will occur.
        if (source.equals(target)) {
            return new ArrayList<>(Arrays.asList(source));
        }
        
        Set<N> frontier         = new HashSet<>();
        Deque<N> backwardStack  = new ArrayDeque<>();
        Map<N, N> parentForward = new HashMap<>();
        
        int previousForward  = -1;
        int previousBackward = -1;
        
        for (int depth = 0; ; ++depth) {
            frontier.clear();
            parentForward.clear();
            
            depthLimitedForward(source,
                                null,
                                depth,
                                new HashSet<>(),
                                frontier, 
                                parentForward,
                                forwardExpander);
            
            if (frontier.isEmpty()) {
                return null;
            }
            
            backwardStack.clear();
            
            N touch = depthLimitedBackward(target,
                                           null,
                                           depth,
                                           new HashSet<>(),
                                           frontier,
                                           backwardStack,
                                           backwardExpander);
            
            if (touch != null) {
                return buildPath(touch,
                                 parentForward,
                                 backwardStack);
            }
            
            backwardStack.clear();
            
            touch = depthLimitedBackward(target,
                                         null,
                                         depth + 1,
                                         new HashSet<>(),
                                         frontier,
                                         backwardStack,
                                         backwardExpander);
            
            if (touch != null) {
                return buildPath(touch,
                                 parentForward,
                                 backwardStack);
            }
        }
    }

    private void depthLimitedForward(N node,
                                     N parent,
                                     int depth,
                                     Set<N> onPath,
                                     Set<N> frontier,
                                     Map<N, N> parents,
                                     NodeExpander<N> expander) {
        if (onPath.contains(node)) {
            return;
        }
        
        onPath.add(node);
        
        if (depth == 0) {
            frontier.add(node);
            onPath.remove(node);
            return;
        }
        
        for (N child : expander.expand(node)) {
            if (parent != null && child.equals(parent)) {
                continue;
            }
            
            parents.putIfAbsent(child, node);
            depthLimitedForward(child,
                                node,
                                depth - 1,
                                onPath,
                                frontier,
                                parents,
                                expander);
        }
        
        onPath.remove(node);
    }

    private N depthLimitedBackward(N node,
                                   N child,
                                   int depth,
                                   Set<N> onPath,
                                   Set<N> frontier,
                                   Deque<N> backwardStack,
                                   NodeExpander<N> expander) {
        if (onPath.contains(node)) {
            return null;
        }
        
        onPath.add(node);
        backwardStack.addFirst(node);

        if (depth == 0) {
            if (frontier.contains(node)) {
                return node;
            }

            backwardStack.removeFirst();
            onPath.remove(node);
            return null;
        }

        for (N parent : expander.expand(node)) {
            if (child != null && parent.equals(child)) {
                continue;
            }
            
            N meetingNode = depthLimitedBackward(parent,
                                                 node,
                                                 depth - 1,
                                                 onPath,
                                                 frontier,
                                                 backwardStack,
                                                 expander);
            if (meetingNode != null) {
                return meetingNode;
            } 
        }

        backwardStack.removeFirst();
        onPath.remove(node);
        return null;
    }

    private List<N> buildPath(N meetingNode,
                              Map<N, N> parentForward,
                              Deque<N> backwardStack) {
        List<N> prefix = new ArrayList<>();
        N current = meetingNode;
        
        while (current != null) {
            prefix.add(current);
            current = parentForward.get(current);
        }
        
        Collections.reverse(prefix);
        List<N> path = new ArrayList<>(prefix.size() + backwardStack.size());
        path.addAll(prefix);
        backwardStack.removeFirst();
        path.addAll(backwardStack);
        return path;
    }
}
