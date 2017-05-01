package net.coderodde.libid;

import java.util.List;
import java.util.Random;
import net.coderodde.libid.support.IterativeDeepeningDepthFirstSearch;

public final class Demo {

    private static final int SWAPS = 10;
    
    public static void main(String[] args) {
        long seed = 1493638334601L; System.currentTimeMillis();
        Random random = new Random(seed);
        SlidingTilePathFinder finderIDDFS = 
                new IterativeDeepeningDepthFirstSearch();
        SlidingTilePuzzleNode target = new SlidingTilePuzzleNode();
        SlidingTilePuzzleNode source = getRandomSourceNode(SWAPS, random);
        
        System.out.println("Seed = " + seed);
        
        long start = System.currentTimeMillis();
        List<SlidingTilePuzzleNode> path1 = finderIDDFS.search(source, target);
        long end = System.currentTimeMillis();
        
        System.out.println("IDDFS path length: " + path1.size() + ". Time: " +
                (end - start) + " milliseconds.");
        
        System.out.println("---");
        
        for (SlidingTilePuzzleNode node : path1) {
            System.out.println(node);
            System.out.println("---");
        }
    }
    
    private static SlidingTilePuzzleNode 
        getRandomSourceNode(int swaps, Random random) {
        SlidingTilePuzzleNode ret = new SlidingTilePuzzleNode();
        SlidingTilePuzzleNode next = null;
        
        while (swaps > 0) {
            int direction = random.nextInt(4);
            
            switch (direction) {
                case 0:
                    next = ret.slideUp();
                    break;
                    
                case 1:
                    next = ret.slideRight();
                    break;
                    
                case 2:
                    next = ret.slideDown();
                    break;
                    
                case 3:
                    next = ret.slideLeft();
                    break;
            }
            
            if (next != null) {
                ret = next;
                next = null;
                --swaps;
            }
        }
        
        return ret;
    }
}
