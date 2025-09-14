package io.github.coderodde.libid.impl;

import io.github.coderodde.libid.NodeExpander;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class IterativeDeepeningDepthFirstSearch<N> {

    public IterativeDeepeningDepthFirstSearch() {}

    // Public entry point: returns path source->target or null if not found.
    public List<N> search(N source, N target, NodeExpander<N> expander) {
        Objects.requireNonNull(source,   "source is null");
        Objects.requireNonNull(target,   "target is null");
        Objects.requireNonNull(expander, "expander is null");

        List<N> path = new ArrayList<>();
        Set<N> onPath = new HashSet<>();

        for (int depth = 0; ; depth++) {
            path.clear();
            onPath.clear();
            Result r = depthLimitedSearch(source,
                                          target,
                                          expander, 
                                          depth, 
                                          path, 
                                          onPath);

            if (r == Result.FOUND) {
                Collections.reverse(path);
                return path;
            }

            if (r == Result.FAIL) {
                // No nodes were cut off at this depth => there is no deeper
                // frontier. Therefore, target is unreachable from source.
                return null;
            }

            // r == CUTOFF: increase depth and try again.
        }
    }

    private enum Result { 
        FOUND, 
        CUTOFF, 
        FAIL,
    }

    private Result depthLimitedSearch(N node,
                                      N target,
                                      NodeExpander<N> expander,
                                      int depth,
                                      List<N> path,
                                      Set<N> onPath) {
        if (node.equals(target)) {
            path.add(node);
            return Result.FOUND;
        }

        if (depth == 0) {
            // Reached the depth bound; we didn’t find the goal here, but
            // search might succeed at a greater depth.
            return Result.CUTOFF;
        }

        if (!onPath.add(node)) {
            // Cycle on current path.
            return Result.FAIL;
        }

        boolean anyCutoff = false;

        for (N child : expander.expand(node)) {
            // Avoid immediate cycles through the current recursion stack:
            if (onPath.contains(child)) {
                continue;
            }

            Result r = depthLimitedSearch(child, 
                                          target,
                                          expander, 
                                          depth - 1, 
                                          path, 
                                          onPath);

            if (r == Result.FOUND) {
                path.add(node); 
                onPath.remove(node);
                return Result.FOUND;
            } else if (r == Result.CUTOFF) {
                anyCutoff = true;
            }
            // else r == FAIL: continue to next child.
        }

        onPath.remove(node);
        return anyCutoff ? Result.CUTOFF : Result.FAIL;
    }
}