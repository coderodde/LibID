package net.coderodde.libid;

import java.util.List;

public interface SlidingTilePathFinder {

    public List<SlidingTilePuzzleNode> search(SlidingTilePuzzleNode source,
                                              SlidingTilePuzzleNode target);
}
