package net.coderodde.libid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import net.coderodde.libid.support.BreadthFirstSearch;
import net.coderodde.libid.support.IterativeDeepeningDepthFirstSearch;

public final class Demo {

    private static final int SWAPS = 55;
    private static final int NODES = 5_000;
    private static final int ARCS = 10_000;
    
    public static void main(String[] args) {
        long seed = 1493640492354L; System.currentTimeMillis(); // not reachable! = 1493640492354
        Random random = new Random(seed);
        
        List<DirectedGraphNode> nodeList = getRandomDigraph(NODES,
                                                            ARCS, 
                                                            random);
        
        DirectedGraphNode source = choose(nodeList, random);
        DirectedGraphNode target = choose(nodeList, random);
        
        System.out.println("Seed = " + seed);
        
        long start = System.currentTimeMillis();
        List<DirectedGraphNode> path1 = null;
        
        try {
            path1 = new IterativeDeepeningDepthFirstSearch<DirectedGraphNode>()
                        .search(source, 
                                target, 
                                new DirectedGraphNodeForwardExpander());
        } catch (Exception ex) {
            
        }
        
        long end = System.currentTimeMillis();
        
        System.out.println("IterativeDeepeningDepthFirstSearch in " + 
                (end - start) + " milliseconds. Path length: " + 
                (path1 != null ? path1.size() : "infinity"));
        
        
        start = System.currentTimeMillis();
        List<DirectedGraphNode> path2 = null;
        
        try {
            path2 = new BreadthFirstSearch<DirectedGraphNode>()
                    .search(source, 
                            target, 
                            new DirectedGraphNodeForwardExpander());
        } catch (Exception ex) {
            
        }
        
        end = System.currentTimeMillis();
        
        System.out.println("BreadthFirstSearch in " + 
                (end - start) + " milliseconds. Path length: " + 
                (path2 != null ? path2.size() : "infinity"));
        
//        SlidingTilePathFinder finderIDDFS = 
//                new IterativeDeepeningDepthFirstSearch();
//        SlidingTilePuzzleNode target = new SlidingTilePuzzleNode();
//        SlidingTilePuzzleNode source = getRandomSourceNode(SWAPS, random);
//        
//        System.out.println("Seed = " + seed);
//        
//        long start = System.currentTimeMillis();
//        List<SlidingTilePuzzleNode> path1 = finderIDDFS.search(source, target);
//        long end = System.currentTimeMillis();
//        
//        System.out.println("IDDFS path length: " + path1.size() + ". Time: " +
//                (end - start) + " milliseconds.");
//        
//        System.out.println("---");
//        
//        for (SlidingTilePuzzleNode node : path1) {
//            System.out.println(node);
//            System.out.println("---");
//        }
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
        
    private static final class DirectedGraphNode {
        
        private final Set<DirectedGraphNode> children = new HashSet<>();
        private final Set<DirectedGraphNode> parents = new HashSet<>();
        
        public void addChild(DirectedGraphNode child) {
            children.add(child);
            child.parents.add(this);
        }
        
        public Set<DirectedGraphNode> getChildren() {
            return Collections.<DirectedGraphNode>unmodifiableSet(children);
        }
        
        public Set<DirectedGraphNode> getParents() {
            return Collections.<DirectedGraphNode>unmodifiableSet(parents);
        }
    }
    
    private static final class DirectedGraphNodeForwardExpander 
            implements NodeExpander<DirectedGraphNode> {

        @Override
        public Collection<DirectedGraphNode> expand(DirectedGraphNode node) {
            return node.getChildren();
        }
    }
    
    private static final class DirectedGraphNodeBackwardExpander 
            implements NodeExpander<DirectedGraphNode> {

        @Override
        public Collection<DirectedGraphNode> expand(DirectedGraphNode node) {
            return node.getParents();
        }
    }
    
    private static List<DirectedGraphNode> getRandomDigraph(int nodes, 
                                                            int arcs,
                                                            Random random) {
        List<DirectedGraphNode> nodeList = new ArrayList<>(nodes);
        
        IntStream.range(0, nodes)
                 .forEach(i -> {
                     nodeList.add(new DirectedGraphNode()); 
                 });
        
        while (arcs-- > 0) {
            DirectedGraphNode tail = choose(nodeList, random);
            DirectedGraphNode head = choose(nodeList, random);
            tail.addChild(head);
        }
        
        return nodeList;
    }
    
    private static <T> T choose(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }
}
