
package net.coderodde.libid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import net.coderodde.libid.impl.BidirectionalDepthFirstIterativeDeepeningSearch;
import net.coderodde.libid.impl.BreadthFirstSearch;
import net.coderodde.libid.impl.IterativeDeepeningDepthFirstSearch;
import net.coderodde.libid.Demo.DirectedGraphNodeForwardExpander;
import net.coderodde.libid.Demo.DirectedGraphNodeBackwardExpander;
import net.coderodde.libid.impl.BidirectionalBreadthFirstSearch;
import net.coderodde.libid.impl.GridHeuristicFunction;
import net.coderodde.libid.impl.IterativeDeepeningAStar;
import net.coderodde.libid.impl.ManhattanHeuristicFunction;

public final class Demo {

    public static void main(String[] args) {
        runGridBenchmark();
        System.out.println();
        runRubiksCubeDemo();
        run15PuzzleGraphBenchmark();
        System.out.println();
    }
    
    private static final int MOVES = 50;
    
    private static void runGridBenchmark() {
        final int width = 20;
        final int height = 20;
        DirectedGraphNode[][] grid = getGrid(width, height);
        DirectedGraphNode sourceNode = grid[0][0];
        DirectedGraphNode targetNode = grid[height - 1][width - 1];
        GridHeuristicFunction gridHeuristicFunction = 
                new GridHeuristicFunction();
        DirectedGraphNodeForwardExpander forwardExpander =
                new DirectedGraphNodeForwardExpander();
        DirectedGraphNodeBackwardExpander backwardExpander =
                new DirectedGraphNodeBackwardExpander();
        
        long startTime = System.currentTimeMillis();
        
        List<DirectedGraphNode> path1 = 
                new IterativeDeepeningAStar<DirectedGraphNode>()
                        .search(
                                sourceNode, 
                                targetNode, 
                                new DirectedGraphNodeForwardExpander(), 
                                gridHeuristicFunction);
        
        long endTime = System.currentTimeMillis();
        
        System.out.println(
                "IterativeDeepeningAStar in " + (endTime - startTime) + 
                " milliseconds. Path length: " + path1.size());
        
        startTime = System.currentTimeMillis();
        
        List<DirectedGraphNode> path2 = 
                new BidirectionalDepthFirstIterativeDeepeningSearch
                        <DirectedGraphNode>()
                .search(sourceNode,
                        targetNode, 
                        forwardExpander, 
                        backwardExpander);
        
        endTime = System.currentTimeMillis();
        
        System.out.println(
                "BIDDFS in " + (endTime - startTime) + " milliseconds. " +
                "Path length: " + path2.size());
    }
    
    private static DirectedGraphNode[][] getGrid(int width, int height) {
        DirectedGraphNode[][] grid = new DirectedGraphNode[height][width];
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = new DirectedGraphNode(x, y);
            }
        }
        
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x].addChild(grid[y + 1][x]);
                grid[y + 1][x].addChild(grid[y][x]);
            }
        }
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width - 1; x++) {
                grid[y][x].addChild(grid[y][x + 1]);
                grid[y][x + 1].addChild(grid[y][x]);
            }
        }
        
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width - 1; x++) {
                grid[y][x].addChild(grid[y + 1][x + 1]);
                grid[y + 1][x + 1].addChild(grid[y][x]);
            }
        }
        
        for (int y = 1; y < height; y++) {
            for (int x = 0; x < width - 1; x++) {
                grid[y][x].addChild(grid[y - 1][x + 1]);
                grid[y - 1][x + 1].addChild(grid[y][x]);
            }
        }
        
        return grid;
    }
    
    private static void run15PuzzleGraphBenchmark() {
        System.out.println("*** 15-puzzle graph benchmark ***");
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
        
        BidirectionalDepthFirstIterativeDeepeningSearch
                <SlidingTilePuzzleNode> finder3;
       
        IterativeDeepeningAStar<SlidingTilePuzzleNode> finder4;
        
        BidirectionalBreadthFirstSearch<SlidingTilePuzzleNode> finder5;
        
        // Construct finders:
        finder1 = new BreadthFirstSearch<>();
        finder2 = new IterativeDeepeningDepthFirstSearch<>();
        finder3 = new BidirectionalDepthFirstIterativeDeepeningSearch<>();
        finder4 = new IterativeDeepeningAStar<>();
        finder5 = new BidirectionalBreadthFirstSearch<>();
        
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
        
        start = System.currentTimeMillis();
        
        List<SlidingTilePuzzleNode> path5 = 
                finder5.search(source, target, expander, expander);
        
        end = System.currentTimeMillis();
        
        System.out.println(finder5.getClass().getSimpleName() + " in " +
                (end - start) + " milliseconds. Path length: " + path5.size());
        
        System.out.println("Algorithms agree: " + 
                tilePathsEqual(path1, path2, path3, path4, path5));
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
            
            new BidirectionalDepthFirstIterativeDeepeningSearch
                    <SlidingTilePuzzleNode>()
                    .search(source, target, expander, expander);
        }
    }
    
    private static void runGeneralGraphBenchmark() {
        System.out.println("*** General graph benchmark ***");
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        List<GeneralDirectedGraphNode> nodeList = 
                getRandomDigraph(BENCHMARK_NODES,
                                 BENCHMARK_ARCS,
                                 random);
        
        
        System.out.println("Seed = " + seed);
//        warmupGeneralGraphBenchmark(nodeList, random);
        for (int i = 0; i < 10; i++) {
            benchmarkGeneralGraph(nodeList, random);
            System.out.println("-----");
        }
    }
    
    private static final int BENCHMARK_NODES = 300_000;
    private static final int BENCHMARK_ARCS = 1_900_000;
    private static final int WARMUP_ITERATIONS = 10;
    
    private static void warmupGeneralGraphBenchmark(
            List<GeneralDirectedGraphNode> nodeList, Random random) {
        System.out.println("Warming up...");
        
        BidirectionalDepthFirstIterativeDeepeningSearch<GeneralDirectedGraphNode>
                finder1 = 
                new BidirectionalDepthFirstIterativeDeepeningSearch<>();
        
        IterativeDeepeningDepthFirstSearch<GeneralDirectedGraphNode> finder2 = 
                new IterativeDeepeningDepthFirstSearch<>();
        
        BreadthFirstSearch<GeneralDirectedGraphNode> finder3 = 
                new BreadthFirstSearch<>();
        
        BidirectionalBreadthFirstSearch<GeneralDirectedGraphNode> finder4 =
                new BidirectionalBreadthFirstSearch<>();
        
        NodeExpander<GeneralDirectedGraphNode> forwardExpander = 
                new GeneralDirectedGraphNodeForwardExpander();
        
        NodeExpander<GeneralDirectedGraphNode> backwardExpander =
                new GeneralDirectedGraphNodeBackwardExpander();
        
        for (int iteration = 0; iteration < WARMUP_ITERATIONS; ++iteration) {
            System.out.println("------------------------------------");
            GeneralDirectedGraphNode source = choose(nodeList, random);
            GeneralDirectedGraphNode target = choose(nodeList, random);
            
            long startTime = System.currentTimeMillis();
            
            try {
                finder1.search(source, 
                               target, 
                               forwardExpander, 
                               backwardExpander);
            } catch (Exception ex) {
                
            }
            
            long endTime = System.currentTimeMillis();
            System.out.println(finder1.getClass().getName() + " in " +
                               (endTime - startTime) + " ms.");
            
            startTime = System.currentTimeMillis();
            
            try {
                finder2.search(source, 
                               target, 
                               forwardExpander);
            } catch (Exception ex) {
                
            }
            
            endTime = System.currentTimeMillis();
            System.out.println(finder2.getClass().getName() + " in " + 
                               (endTime - startTime) + " ms.");
            
            startTime = System.currentTimeMillis();
            
            try {
                finder3.search(source, 
                               target, 
                               forwardExpander);
            } catch (Exception ex) {
                
            }
            
            endTime = System.currentTimeMillis();
            System.out.println(finder3.getClass().getName() + " in " + 
                               (endTime - startTime) + " ms.");
            
            startTime = System.currentTimeMillis();
            
            try {
                finder4.search(source, 
                               target, 
                               forwardExpander,
                               backwardExpander);
            } catch (Exception ex) {
                
            }
            
            endTime = System.currentTimeMillis();
            System.out.println(finder4.getClass().getName() + " in " + 
                               (endTime - startTime) + " ms.");
        }
        
        System.out.println("Warming up done!");
    }
    
    private static void benchmarkGeneralGraph(
            List<GeneralDirectedGraphNode> nodeList,
            Random random) {
        LapTimer timer = new LapTimer();
        GeneralDirectedGraphNode source = choose(nodeList, random);
        GeneralDirectedGraphNode target = choose(nodeList, random);
        
        BidirectionalDepthFirstIterativeDeepeningSearch<GeneralDirectedGraphNode>
                finder1 = 
                new BidirectionalDepthFirstIterativeDeepeningSearch<>();
        
        IterativeDeepeningDepthFirstSearch<GeneralDirectedGraphNode> finder2 = 
                new IterativeDeepeningDepthFirstSearch<>();
        
        BreadthFirstSearch<GeneralDirectedGraphNode> finder3 = 
                new BreadthFirstSearch<>();
        
        BidirectionalBreadthFirstSearch<GeneralDirectedGraphNode> finder4 = 
                new BidirectionalBreadthFirstSearch<>();
        
        NodeExpander<GeneralDirectedGraphNode> forwardExpander = 
                new GeneralDirectedGraphNodeForwardExpander();
        
        NodeExpander<GeneralDirectedGraphNode> backwardExpander =
                new GeneralDirectedGraphNodeBackwardExpander();
        
        List<GeneralDirectedGraphNode> path1 = null;
        List<GeneralDirectedGraphNode> path2 = null;
        List<GeneralDirectedGraphNode> path3 = null;
        List<GeneralDirectedGraphNode> path4 = null;
        
        timer.push();
        
        try {
            path1 = finder1.search(source, 
                                   target, 
                                   forwardExpander, 
                                   backwardExpander);
        } catch (Exception ex) {

        }
        
        System.out.println(
                finder1.getClass().getSimpleName() + timer + ". " +
                        ". Path length: " + 
                        (path1 != null ? path1.size() : "infinity"));
            
        timer.push();
        
        try {
            path2 = finder2.search(source, 
                                   target, 
                                   forwardExpander);
        } catch (Exception ex) {

        }
        
        System.out.println(
                finder2.getClass().getSimpleName() + timer + ". Path length: " + 
                (path2 != null ? path2.size() : "infinity"));
        
        timer.push();
        
        try {
            path3 = finder3.search(source, 
                                   target, 
                                   forwardExpander);
        } catch (Exception ex) {

        }
        
        System.out.println(
                finder3.getClass().getName() + timer + ". Path length: " + 
                (path3 != null ? path3.size() : "infinity"));
        
        timer.push();
        
        try {
            path4 = finder4.search(source, 
                                  target, 
                                  forwardExpander,
                                  backwardExpander);
        } catch (Exception ex) {

        }
        
        System.out.println(
                finder4.getClass().getSimpleName() + timer + ". Path length: " + 
                (path3 != null ? path4.size() : "infinity"));
        
        System.out.println("Algorithms agree: " + 
                           generalUndirectedGraphAlgosAgree(path1, 
                                                            path2, 
                                                            path3, 
                                                            path4));
    }
    
    private static boolean 
        undirectedGraphAlgosAgree(List<DirectedGraphNode>... paths) {
        for (int i = 0, j = 1; j < paths.length; i++, j++) {
            if (paths[i].size() != paths[j].size()) {
                return false;
            }
            
            if (!paths[i].get(0).equals(paths[j].get(0))) {
                return false;
            }
            
            int len = paths[0].size();
            
            if (!paths[i].get(len - 1).equals(paths[j].get(len - 1))) {
                return false;
            }
        }
        
        return true;
    }
    
    private static boolean 
        generalUndirectedGraphAlgosAgree(
                List<GeneralDirectedGraphNode>... paths) {
        for (int i = 0, j = 1; j < paths.length; i++, j++) {
            if (paths[i].size() != paths[j].size()) {
                return false;
            }
            
            if (!paths[i].get(0).equals(paths[j].get(0))) {
                return false;
            }
            
            int len = paths[0].size();
            
            if (!paths[i].get(len - 1).equals(paths[j].get(len - 1))) {
                return false;
            }
        }
        
        return true;
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
    
    public static final class GeneralDirectedGraphNode {
        
        private final Set<GeneralDirectedGraphNode> children = new HashSet<>();
        private final Set<GeneralDirectedGraphNode> parents  = new HashSet<>();
        
        public Set<GeneralDirectedGraphNode> getChildren() {
            return Collections.<GeneralDirectedGraphNode>
                    unmodifiableSet(children);
        }
        
        public Set<GeneralDirectedGraphNode> getParents() {
            return Collections.<GeneralDirectedGraphNode>
                    unmodifiableSet(parents);
        }
        
        public void addChild(GeneralDirectedGraphNode other) {
            children.add(other);
            other.parents.add(this);
        }
        
        @Override
        public String toString() {
            return "[GDGN: id = " + super.toString() + "]";
        }
    }
        
    public static class DirectedGraphNode {
        
        private final Set<DirectedGraphNode> children = new HashSet<>();
        private final Set<DirectedGraphNode> parents = new HashSet<>();
        private final int x;
        private final int y;
        
        public DirectedGraphNode() {
            this(0, 0);
        }
        
        public DirectedGraphNode(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
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
        
        @Override
        public int hashCode() {
            return x ^ y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final DirectedGraphNode other = (DirectedGraphNode) obj;
            if (this.x != other.x) {
                return false;
            }
            if (this.y != other.y) {
                return false;
            }
            return true;
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
    
    public static final class GeneralDirectedGraphNodeForwardExpander 
            implements NodeExpander<GeneralDirectedGraphNode> {

        @Override
        public Collection<GeneralDirectedGraphNode> 
        expand(GeneralDirectedGraphNode node) {
            return node.getChildren();
        }
    }
    
    public static final class GeneralDirectedGraphNodeBackwardExpander 
            implements NodeExpander<GeneralDirectedGraphNode> {

        @Override
        public Collection<GeneralDirectedGraphNode> 
        expand(GeneralDirectedGraphNode node) {
            return node.getParents();
        }
    }
    
    private static List<GeneralDirectedGraphNode> getRandomDigraph(int nodes, 
                                                            int arcs,
                                                            Random random) {
        List<GeneralDirectedGraphNode> nodeList = new ArrayList<>(nodes);
        
        IntStream.range(0, nodes)
                 .forEach(i -> {
                     nodeList.add(new GeneralDirectedGraphNode()); 
                 });
        
        System.out.println("Nodes are ready.");
        return getSparseRandomDigraph(nodeList, random);
        
//        while (arcs-- > 0) {
//            GeneralDirectedGraphNode tail = choose(nodeList, random);
//            GeneralDirectedGraphNode head = choose(nodeList, random);
//            tail.addChild(head);
//        }
//        
//        return nodeList;
    }
    
    private static List<GeneralDirectedGraphNode> 
        getSparseRandomDigraph(List<GeneralDirectedGraphNode> nodeList,
                               Random random) {
        int edges = (int)(nodeList.size() * 7);
        
        while (edges > 0) {
            GeneralDirectedGraphNode sourceNode = choose(nodeList, random);
            GeneralDirectedGraphNode targetNode = choose(nodeList, random);
            sourceNode.addChild(targetNode);
            targetNode.addChild(sourceNode);
            edges--;
            
            if (edges % 100_000 == 0) {
                System.out.println(edges);
            }
        }
        
        int actualEdges = 0;
        
        for (GeneralDirectedGraphNode node : nodeList) {
            actualEdges += node.getChildren().size();
        }
        
        System.out.println("Actual edge count: " + actualEdges);
        return nodeList;
    }
    
    private static <T> T choose(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }
    
    private static void runRubiksCubeDemo() {
        final int rotations = 5;
        final long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        RubiksCubeNode targetRubiksCubeNode = new RubiksCubeNode();
        RubiksCubeNode sourceRubiksCubeNode = scramble(targetRubiksCubeNode,
                                                       random,
                                                       rotations);
        
        System.out.println("Seed = " + seed);
        System.out.println("Source node: " + sourceRubiksCubeNode);
        System.out.println("Target node: " + targetRubiksCubeNode);
        
        class RubiksCubeNodeExpander implements NodeExpander<RubiksCubeNode> {

            @Override
            public Collection<RubiksCubeNode> expand(RubiksCubeNode node) {
                return node.computeNeighbors();
            }
        };
        
        NodeExpander<RubiksCubeNode> expander = 
                new RubiksCubeNodeExpander();
        
        //// BIDDFS
        long startTime = System.currentTimeMillis();
        
        List<RubiksCubeNode> path1 = 
                new BidirectionalDepthFirstIterativeDeepeningSearch
                        <RubiksCubeNode>()
                .search(sourceRubiksCubeNode, 
                        targetRubiksCubeNode,
                        expander,
                        expander);
        
        long endTime = System.currentTimeMillis();
        
        System.out.println("BDFID path (" + (endTime - startTime) + " ms):");
        printlnPath(path1, "          ");
     
        //// IDDFS
        startTime = System.currentTimeMillis();
        
        List<RubiksCubeNode> path2 = 
                new IterativeDeepeningDepthFirstSearch<RubiksCubeNode>()
                        .search(sourceRubiksCubeNode, targetRubiksCubeNode, expander);
        
        endTime = System.currentTimeMillis();
        
        System.out.println("IDDFS path (" + (endTime - startTime) + " ms):");
        printlnPath(path2, "          ");
        
        //// Bidirectional BFS:
        startTime = System.currentTimeMillis();
        
        List<RubiksCubeNode> path3 = 
                new BidirectionalBreadthFirstSearch<RubiksCubeNode>()
                .search(sourceRubiksCubeNode, 
                        targetRubiksCubeNode, 
                        expander, 
                        expander);
        
        endTime = System.currentTimeMillis();
        
        System.out.println("Bidirectional BFS path (" + (endTime - startTime) + 
                           " ms):");
        
        printlnPath(path2, "          ");
     
        //// IDDFS
//        startTime = System.currentTimeMillis();
//        
//        List<RubiksCubeNode> path3 = 
//                new IterativeDeepeningDepthFirstSearch<RubiksCubeNode>()
//                .search(sourceRubiksCubeNode, 
//                        targetRubiksCubeNode, 
//                        expander);
//        
//        endTime = System.currentTimeMillis();
//        
//        System.out.println("IDDFS path (" + (endTime - startTime) + 
//                           " ms):");
//        
//        printlnPath(path3, "          ");
//        
//        System.out.println("Algorithms returns correct paths: " + 
//                           (path1.size() == path2.size() &&
//                            path2.size() == path3.size()));
//        
        System.out.println("Algorithms returns correct paths: " + 
                           (path1.size() == path2.size()));
    }
    
    private static RubiksCubeNode scramble(RubiksCubeNode node,
                                           Random random,
                                           int rotations) {
        RubiksCubeNode.Axis[] axes = RubiksCubeNode.Axis.values();
        RubiksCubeNode.Direction[] directions = 
                RubiksCubeNode.Direction.values();
        RubiksCubeNode.Layer[] layers = RubiksCubeNode.Layer.values();
        
        for (int r = 0; r < rotations; r++) {
            RubiksCubeNode.Axis axis = axes[random.nextInt(axes.length)];
            RubiksCubeNode.Direction direction = 
                    directions[random.nextInt(directions.length)];
            RubiksCubeNode.Layer layer = layers[random.nextInt(layers.length)];
            node = node.rotate(axis, layer, direction);
        }
        
        return node;
    }
    
    private static <E> void printlnPath(List<E> list, String title) {
        System.out.print(title);
        System.out.println(list.get(0));
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < title.length(); i++) {
            sb.append(' ');
        }
        
        String intro = sb.toString();
        
        for (int i = 1; i < list.size(); i++) {
            System.out.print(intro);
            System.out.println(list.get(i));
        }
    }
}
