package net.coderodde.libid;

public interface HeuristicFunction {

    public int estimate(SlidingTilePuzzleNode current,
                        SlidingTilePuzzleNode target);
}
