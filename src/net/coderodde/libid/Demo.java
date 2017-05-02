package net.coderodde.libid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import net.coderodde.libid.support.BidirectionalIterativeDeepeningDepthFirstSearch;
import net.coderodde.libid.support.BreadthFirstSearch;
import net.coderodde.libid.support.IterativeDeepeningDepthFirstSearch;
import net.coderodde.libid.Demo.DirectedGraphNodeForwardExpander;
import net.coderodde.libid.Demo.DirectedGraphNodeBackwardExpander;
import net.coderodde.libid.support.IterativeDeepeningAStar;
import net.coderodde.libid.support.ManhattanHeuristicFunction;

public final class Demo {

    public static void main(String[] args) {
        run8PuzzleGraphBenchmark();
        System.out.println();
        runGeneralGraphBenchmark();
    }
    
    private static final int MOVES = 40;
    
    private static void run8PuzzleGraphBenchmark() {
        System.out.println("*** 8-puzzle graph benchmark ***");
        long seed =  System.currentTimeMillis();
        Random random = new Random(seed);
        
        warmup8PuzzleGraphBenchmark(random);
        
        System.out.println("Seed = " + seed);
        SlidingTilePuzzleNode target = new SlidingTilePuzzleNode();
        SlidingTilePuzzleNode source = getRandomSourceNode(MOVES, random);
        SlidingTilePuzzleNodeExpander expander =  
                new SlidingTilePuzzleNodeExpander();
        
        // Declare finders:
        BreadthFirstSearch<SlidingTilePuzzleNode> finder1;
        
        IterativeDeepeningDepthFirstSearch<SlidingTilePuzzleNode> finder2;
        
        BidirectionalIterativeDeepeningDepthFirstSearch
                <SlidingTilePuzzleNode> finder3;
       
        IterativeDeepeningAStar<SlidingTilePuzzleNode, Integer> finder4;
        
        // Construct finders:
        finder1 = new BreadthFirstSearch<>();
        finder2 = new IterativeDeepeningDepthFirstSearch<>();
        finder3 = new BidirectionalIterativeDeepeningDepthFirstSearch<>();
        finder4 = new IterativeDeepeningAStar<>();
        
        long start = System.currentTimeMillis();
        List<SlidingTilePuzzleNode> path1 = finder1.search(source,
                                                           target, 
                                                           expander);
        long end = System.currentTimeMillis();
        
        System.out.println(finder1.getClass().getSimpleName() + " in " +
                (end - start) + " milliseconds. Path length: " + path1.size());
        
        start = System.currentTimeMillis();
        List<SlidingTilePuzzleNode> path2 = finder2.search(source,
                                                           target, 
                                                           expander);
        end = System.currentTimeMillis();
        
        System.out.println(finder2.getClass().getSimpleName() + " in " +
                (end - start) + " milliseconds. Path length: " + path2.size());
        
        start = System.currentTimeMillis();
        List<SlidingTilePuzzleNode> path3 = finder3.search(source,
                                                           target, 
                                                           expander,
                                                           expander);
        end = System.currentTimeMillis();
        
        System.out.println(finder3.getClass().getSimpleName() + " in " +
                (end - start) + " milliseconds. Path length: " + path3.size());
        
        start = System.currentTimeMillis();
        List<SlidingTilePuzzleNode> path4 = 
                finder4.search(source,
                               target, 
                               expander,
                               new ManhattanHeuristicFunction());
        end = System.currentTimeMillis();
        
        System.out.println(finder4.getClass().getSimpleName() + " in " +
                (end - start) + " milliseconds. Path length: " + path4.size());
        
        System.out.println("Algorithms agree: " + 
                tilePathsEqual(path1, path2, path3, path4));
    }
    
    private static boolean
         tilePathsEqual(List<SlidingTilePuzzleNode>... paths) {
        for (int i = 0; i < paths.length - 1; ++i) {
            if (paths[i].size() != paths[i + 1].size()) {
                return false;
            }
        }
        
        int lastIndex = paths[0].size() - 1;
        
        for (int i = 0; i < paths.length - 1; ++i) {
            if (!paths[i].get(0).equals(paths[i + 1].get(0))) {
                return false;
            }
        }
        
        for (int i = 0; i < paths.length - 1; ++i) {
            if (!paths[i].get(lastIndex).equals(paths[i + 1].get(lastIndex))) {
                return false;
            }
        }
        
        return true;
    }
    
    private static final int WARMUP_MOVES = 10;
         
    private static void warmup8PuzzleGraphBenchmark(Random random) {
        SlidingTilePuzzleNode target = new SlidingTilePuzzleNode();
        SlidingTilePuzzleNode source = new SlidingTilePuzzleNode();
        NodeExpander<SlidingTilePuzzleNode> expander = 
                new SlidingTilePuzzleNodeExpander();
        
        for (int iteration = 0; iteration < WARMUP_ITERATIONS; ++iteration) {
            source = getRandomSourceNode(WARMUP_MOVES, random);
            new BreadthFirstSearch<SlidingTilePuzzleNode>()
                    .search(source, target, expander);
            
            new IterativeDeepeningDepthFirstSearch<SlidingTilePuzzleNode>()
                    .search(source, target, expander);
            
            new BidirectionalIterativeDeepeningDepthFirstSearch
                    <SlidingTilePuzzleNode>()
                    .search(source, target, expander, expander);
        }
    }
    
    private static void runGeneralGraphBenchmark() {
        System.out.println("*** General graph benchmark ***");
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        List<DirectedGraphNode> nodeList = getRandomDigraph(BENCHMARK_NODES,
                                                            BENCHMARK_ARCS,
                                                            random);
        System.out.println("Seed = " + seed);
        warmupGeneralGraphBenchmark(nodeList, random);
        benchmarkGeneralGraph(nodeList, random);
    }
    
    private static final int BENCHMARK_NODES = 1000;
    private static final int BENCHMARK_ARCS = 400_000;
    private static final int WARMUP_ITERATIONS = 10;
    
    private static void warmupGeneralGraphBenchmark(
            List<DirectedGraphNode> nodeList, Random random) {
        System.out.println("Warming up...");
        
        BidirectionalIterativeDeepeningDepthFirstSearch<DirectedGraphNode>
                finder1 = 
                new BidirectionalIterativeDeepeningDepthFirstSearch<>();
        
        IterativeDeepeningDepthFirstSearch<DirectedGraphNode> finder2 = 
                new IterativeDeepeningDepthFirstSearch<>();
        
        BreadthFirstSearch<DirectedGraphNode> finder3 = 
                new BreadthFirstSearch<>();
        
        NodeExpander<DirectedGraphNode> forwardExpander = 
                new DirectedGraphNodeForwardExpander();
        
        NodeExpander<DirectedGraphNode> backwardExpander =
                new DirectedGraphNodeBackwardExpander();
        
        for (int iteration = 0; iteration < WARMUP_ITERATIONS; ++iteration) {
            DirectedGraphNode source = choose(nodeList, random);
            DirectedGraphNode target = choose(nodeList, random);
            
            try {
                finder1.search(source, 
                               target, 
                               forwardExpander, 
                               backwardExpander);
            } catch (Exception ex) {
                
            }
            
            try {
                finder2.search(source, 
                               target, 
                               forwardExpander);
            } catch (Exception ex) {
                
            }
            
            try {
                finder3.search(source, 
                               target, 
                               forwardExpander);
            } catch (Exception ex) {
                
            }
        }
        
        System.out.println("Warming up done!");
    }
    
    private static void benchmarkGeneralGraph(List<DirectedGraphNode> nodeList,
                                              Random random) {
        DirectedGraphNode source = choose(nodeList, random);
        DirectedGraphNode target = choose(nodeList, random);
        
        BidirectionalIterativeDeepeningDepthFirstSearch<DirectedGraphNode>
                finder1 = 
                new BidirectionalIterativeDeepeningDepthFirstSearch<>();
        
        IterativeDeepeningDepthFirstSearch<DirectedGraphNode> finder2 = 
                new IterativeDeepeningDepthFirstSearch<>();
        
        BreadthFirstSearch<DirectedGraphNode> finder3 = 
                new BreadthFirstSearch<>();
        
        NodeExpander<DirectedGraphNode> forwardExpander = 
                new DirectedGraphNodeForwardExpander();
        
        NodeExpander<DirectedGraphNode> backwardExpander =
                new DirectedGraphNodeBackwardExpander();
        
        List<DirectedGraphNode> path1 = null;
        List<DirectedGraphNode> path2 = null;
        List<DirectedGraphNode> path3 = null;
            
        long start = System.currentTimeMillis();
        
        try {
            path1 = finder1.search(source, 
                                   target, 
                                   forwardExpander, 
                                   backwardExpander);
        } catch (Exception ex) {

        }
        
        long end = System.currentTimeMillis();
        
        System.out.println(
                finder1.getClass().getSimpleName() + " in " + 
                (end - start) + " milliseconds. Path length: " + 
                (path1 != null ? path1.size() : "infinity"));
            
        start = System.currentTimeMillis();
        
        try {
            path2 = finder2.search(source, 
                                   target, 
                                   forwardExpander);
        } catch (Exception ex) {

        }
        
        end = System.currentTimeMillis();
        
        System.out.println(
                finder2.getClass().getSimpleName() + " in " + 
                (end - start) + " milliseconds. Path length: " + 
                (path2 != null ? path2.size() : "infinity"));

        start = System.currentTimeMillis();
        
        try {
            path3 = finder3.search(source, 
                                  target, 
                                  forwardExpander);
        } catch (Exception ex) {

        }
        
        end = System.currentTimeMillis();
        
        System.out.println(
                finder3.getClass().getSimpleName() + " in " + 
                (end - start) + " milliseconds. Path length: " + 
                (path3 != null ? path3.size() : "infinity"));
    }
    
    private static final class SlidingTilePuzzleNodeExpander 
            implements NodeExpander<SlidingTilePuzzleNode> {

        @Override
        public Collection<SlidingTilePuzzleNode> expand(SlidingTilePuzzleNode node) {
            return node.getNeighbors();
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
        
    public static final class DirectedGraphNode {
        
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
    
    public static final class DirectedGraphNodeForwardExpander 
            implements NodeExpander<DirectedGraphNode> {

        @Override
        public Collection<DirectedGraphNode> expand(DirectedGraphNode node) {
            return node.getChildren();
        }
    }
    
    public static final class DirectedGraphNodeBackwardExpander 
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
