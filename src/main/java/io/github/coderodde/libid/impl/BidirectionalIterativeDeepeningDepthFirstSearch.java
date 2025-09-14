package io.github.coderodde.libid.impl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import io.github.coderodde.libid.NodeExpander;
import java.util.Collections;

public final class BidirectionalIterativeDeepeningDepthFirstSearch<N> {

    private final Deque<N> forwardSearchStack;
    private final Deque<N> backwardSearchStack;
    private final Set<N> frontier;
    private final Set<N> visitedForward;
    private final Set<N> visitedBackward;
    private final NodeExpander<N> forwardExpander;
    private final NodeExpander<N> backwardExpander;
    private final Map<N, N> parentForward;

    public BidirectionalIterativeDeepeningDepthFirstSearch() {
        this.forwardSearchStack  = null;
        this.backwardSearchStack = null;
        this.frontier            = null;
        this.visitedForward      = null;
        this.visitedBackward     = null;
        this.forwardExpander     = null;
        this.backwardExpander    = null;
        this.parentForward       = null;
    }

    private BidirectionalIterativeDeepeningDepthFirstSearch(
            NodeExpander<N> forwardExpander,
            NodeExpander<N> backwardExpander) {

        this.forwardSearchStack  = new ArrayDeque<>();
        this.backwardSearchStack = new ArrayDeque<>();
        this.frontier            = new HashSet<>();
        this.visitedForward      = new HashSet<>();
        this.visitedBackward     = new HashSet<>();
        this.forwardExpander     = forwardExpander;
        this.backwardExpander    = backwardExpander;
        this.parentForward       = new HashMap<>();
    }

    public List<N> search(N source,
                          N target,
                          NodeExpander<N> forwardExpander,
                          NodeExpander<N> backwardExpander) {
        
        if (source.equals(target)) {
            return new ArrayList<>(Arrays.asList(source));
        }

        BidirectionalIterativeDeepeningDepthFirstSearch<N> state =
                new BidirectionalIterativeDeepeningDepthFirstSearch<>(
                        forwardExpander, 
                        backwardExpander);

        for (int depth = 0; ; ++depth) {
            state.frontier.clear();
            state.visitedForward.clear();
            state.visitedBackward.clear();
            state.forwardSearchStack.clear();
            state.backwardSearchStack.clear();
            state.parentForward.clear();

            state.depthLimitedSearchForward(source, depth);

            if (state.frontier.isEmpty()) {
                return null;
            }

            N meetingNode = state.depthLimitedSearchBackward(target, 
                                                             depth);
            if (meetingNode != null) {
                return state.buildPathMeet(meetingNode);
            }

            state.visitedBackward.clear();
            state.backwardSearchStack.clear();

            meetingNode = state.depthLimitedSearchBackward(target,
                                                           depth + 1);
            if (meetingNode != null) {
                return state.buildPathMeet(meetingNode);
            }
        }
    }

    // Forward DLS with visited set and parent recording.
    private void depthLimitedSearchForward(N node, int depth) {
        if (!visitedForward.add(node)) {
            return;
        }

        if (depth == 0) {
            frontier.add(node);
            return;
        }

        for (N child : forwardExpander.expand(node)) {
            if (!visitedForward.contains(child)) {
                parentForward.put(child, node);
            }
            
            depthLimitedSearchForward(child, depth - 1);
        }
    }

    private N depthLimitedSearchBackward(N node, int depth) {
        if (!visitedBackward.add(node)) {
            return null;
        }

        backwardSearchStack.addFirst(node);

        if (frontier.contains(node)) {
            return node;
        }

        if (depth == 0) {
            backwardSearchStack.removeFirst();
            return null;
        }

        for (N parent : backwardExpander.expand(node)) {
            N meetingNode = depthLimitedSearchBackward(parent,
                                                       depth - 1);
            if (meetingNode != null) {
                return meetingNode;
            }
        }

        backwardSearchStack.removeFirst();
        return null;
    }

    private List<N> buildPathMeet(N meetingNode) {
        List<N> prefix = buildPathForwardOnly(meetingNode);

        List<N> path = new ArrayList<>(
                prefix.size() + Math.max(0, backwardSearchStack.size() - 1));
        
        path.addAll(prefix);
        backwardSearchStack.removeFirst();
        path.addAll(backwardSearchStack);
        return path;
    }

    private List<N> buildPathForwardOnly(N targetOrMeeting) {
        List<N> path = new ArrayList<>();
        N current = targetOrMeeting;

        while (current != null) {
            path.add(current);
            current = parentForward.get(current);
        }

        Collections.reverse(path);
        return path;
    }
}
