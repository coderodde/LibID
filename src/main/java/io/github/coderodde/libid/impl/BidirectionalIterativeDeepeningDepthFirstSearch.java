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
        
        Set<N> visitedForward   = new HashSet<>();
        Set<N> visitedBackward  = new HashSet<>();
        Set<N> frontier         = new HashSet<>();
        Deque<N> backwardStack  = new ArrayDeque<>();
        Map<N, N> parentForward = new HashMap<>();
        
        int previousForward  = -1;
        int previousBackward = -1;
        
        for (int depth = 0; ; ++depth) {
            visitedForward.clear();
            frontier.clear();
            parentForward.clear();
            
            depthLimitedForward(source,
                                depth,
                                visitedForward,
                                frontier, 
                                parentForward,
                                forwardExpander);
            
            if (visitedForward.size() == previousForward) {
                return null;
            }
            
            previousForward = visitedForward.size();
            
            visitedBackward.clear();
            backwardStack.clear();
            
            N touch = depthLimitedBackward(target,
                                           depth,
                                           visitedBackward,
                                           frontier,
                                           backwardStack,
                                           backwardExpander);
            
            if (touch != null) {
                return buildPath(source,
                                 touch,
                                 parentForward,
                                 backwardStack);
            }
            
            visitedBackward.clear();
            backwardStack.clear();
            touch = depthLimitedBackward(target,
                                         depth + 1,
                                         visitedBackward,
                                         frontier,
                                         backwardStack,
                                         backwardExpander);
            
            if (touch != null) {
                return buildPath(source,
                                 touch,
                                 parentForward,
                                 backwardStack);
            }
            
            if (visitedBackward.size() == previousBackward) {
                return null;
            }
            
            previousBackward = visitedBackward.size();
        }
    }

    private void depthLimitedForward(N node,
                                    int depth,
                                    Set<N> visited,
                                    Set<N> frontier,
                                    Map<N, N> parents,
                                    NodeExpander<N> expander) {
        if (!visited.add(node)) {
            return;
        }
        
        if (depth == 0) {
            frontier.add(node);
            return;
        }
        
        for (N child : expander.expand(node)) {
            parents.putIfAbsent(child, node);
            depthLimitedForward(child,
                                depth - 1,
                                visited,
                                frontier,
                                parents,
                                expander);
        }
    }

    private N depthLimitedBackward(N node,
                                   int depth,
                                   Set<N> visited,
                                   Set<N> frontier,
                                   Deque<N> backwardStack,
                                   NodeExpander<N> expander) {
        if (visited.contains(node)) {
            return null;
        }
        
        backwardStack.addFirst(node);

        if (depth == 0) {
            if (frontier.contains(node)) {
                return node;
            }

            backwardStack.removeFirst();
            return null;
        }
        
        visited.add(node);  

        for (N parent : expander.expand(node)) {
            N meetingNode = depthLimitedBackward(parent,
                                                 depth - 1,
                                                 visited,
                                                 frontier,
                                                 backwardStack,
                                                 expander);
            if (meetingNode != null) {
                return meetingNode;
            } 
        }

        backwardStack.removeFirst();
        return null;
    }

    private List<N> buildPath(N source,
                              N meetingNode,
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
