package net.coderodde.libid.support;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import net.coderodde.libid.SlidingTilePathFinder;
import net.coderodde.libid.SlidingTilePuzzleNode;

public final class IterativeDeepeningDepthFirstSearch 
        implements SlidingTilePathFinder {

    private final SlidingTilePuzzleNode source;
    private final SlidingTilePuzzleNode target;
    private final List<SlidingTilePuzzleNode> path;
    private boolean found;
    private int reachedNodes = 0;
    private int previousReachedNodes = 0;
    
    public IterativeDeepeningDepthFirstSearch() {
        this.source = null;
        this.target = null;
        this.path = null;
    }
    
    private IterativeDeepeningDepthFirstSearch(SlidingTilePuzzleNode source,
                                               SlidingTilePuzzleNode target) {
        this.source = Objects.requireNonNull(source, "source is null");
        this.target = Objects.requireNonNull(target, "target is null");
        this.path = new ArrayList<>();
    }
    
    @Override
    public List<SlidingTilePuzzleNode> 
        search(SlidingTilePuzzleNode source, SlidingTilePuzzleNode target) {
        IterativeDeepeningDepthFirstSearch state = 
                new IterativeDeepeningDepthFirstSearch(source, target);
        
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
    
    private void depthLimitedSearch(SlidingTilePuzzleNode node, int depth) {
        ++reachedNodes;
        
        if (depth == 0 && node.equals(target)) {
            found = true;
            path.add(node);
            return;
        }
        
        if (depth > 0) {
            for (SlidingTilePuzzleNode child : node.getNeighbors()) {
                depthLimitedSearch(child, depth - 1);
                
                if (found) {
                    path.add(node);
                    return;
                }
            }
        }
    }
}
