package net.coderodde.libid.impl;

import net.coderodde.libid.SlidingTilePuzzleNode;
import net.coderodde.libid.IntHeuristicFunction;

public class ManhattanHeuristicFunction 
        implements IntHeuristicFunction<SlidingTilePuzzleNode> {

    private final int[] xs = new int[16];
    private final int[] ys = new int[16];
    
    @Override
    public int estimate(SlidingTilePuzzleNode current, 
                        SlidingTilePuzzleNode target) {
        int cost = 0;
        
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 4; ++x) {
                int number = current.readTile(x, y);
                xs[number] = x;
                ys[number] = y;
            }
        }
        
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 4; ++x) {
                int number = target.readTile(x, y);
                
                if (number == 0) {
                    // Omit the zero tile!
                    continue;
                }
                
                int otherX = xs[number];
                int otherY = ys[number];
                
                cost += Math.abs(otherX - x) + Math.abs(otherY - y);
            }
        }
        
        return cost;
    }
}
