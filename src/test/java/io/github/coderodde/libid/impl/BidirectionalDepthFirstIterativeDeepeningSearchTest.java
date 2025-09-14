package io.github.coderodde.libid.impl;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;
import io.github.coderodde.libid.demo.Demo.GeneralDirectedGraphNode;
import io.github.coderodde.libid.demo.Demo.GeneralDirectedGraphNodeBackwardExpander;
import io.github.coderodde.libid.demo.Demo.GeneralDirectedGraphNodeForwardExpander;

public class BidirectionalDepthFirstIterativeDeepeningSearchTest {

    @Test
    public void testSearchSmall() {
        GeneralDirectedGraphNode a  = new GeneralDirectedGraphNode("a");
        GeneralDirectedGraphNode b1 = new GeneralDirectedGraphNode("b1");
        GeneralDirectedGraphNode b2 = new GeneralDirectedGraphNode("b2");
        GeneralDirectedGraphNode c1 = new GeneralDirectedGraphNode("c1");
        GeneralDirectedGraphNode c2 = new GeneralDirectedGraphNode("c2");
        GeneralDirectedGraphNode d  = new GeneralDirectedGraphNode("d");
        
        a.addChild(b1);
        a.addChild(b2);
        
        b1.addChild(c1);
        b2.addChild(c2);
        
        c1.addChild(d);
        c2.addChild(d);
        
        List<GeneralDirectedGraphNode> path;
        path = new BidirectionalIterativeDeepeningDepthFirstSearch
                <GeneralDirectedGraphNode>()
                .search(a,
                        d,
                        new GeneralDirectedGraphNodeForwardExpander(),
                        new GeneralDirectedGraphNodeBackwardExpander());
        
        System.out.println("testSearchSmall: " + path);
        
        assertEquals(4, path.size());
    }
    
    @Test
    public void testSearchSmall2() {
        GeneralDirectedGraphNode a  = new GeneralDirectedGraphNode("a");
        GeneralDirectedGraphNode b1 = new GeneralDirectedGraphNode("b1");
        GeneralDirectedGraphNode b2 = new GeneralDirectedGraphNode("b2");
        GeneralDirectedGraphNode c1 = new GeneralDirectedGraphNode("c1");
        GeneralDirectedGraphNode c2 = new GeneralDirectedGraphNode("c2");
        GeneralDirectedGraphNode c3 = new GeneralDirectedGraphNode("c3");
        GeneralDirectedGraphNode d  = new GeneralDirectedGraphNode("d");
        
        a.addChild(b1);
        a.addChild(c1);
        
        b1.addChild(b2);
        b2.addChild(d);
        
        c1.addChild(c2);
        c2.addChild(c3);
        c3.addChild(d);
        
        List<GeneralDirectedGraphNode> path = 
                new BidirectionalIterativeDeepeningDepthFirstSearch
                        <GeneralDirectedGraphNode>()
                        .search(a,
                                d, 
                                new GeneralDirectedGraphNodeForwardExpander(),
                                new GeneralDirectedGraphNodeBackwardExpander());

        System.out.println("testSearchSmall2: " + path);
        
        assertEquals(4, path.size());
    }
    
    @Test
    public void testSearchSmallest() {
        GeneralDirectedGraphNode a = new GeneralDirectedGraphNode("a");
        GeneralDirectedGraphNode b = new GeneralDirectedGraphNode("b");
        GeneralDirectedGraphNode c = new GeneralDirectedGraphNode("c");
        
        a.addChild(b);
        b.addChild(c);
        
        List<GeneralDirectedGraphNode> path = 
                new BidirectionalIterativeDeepeningDepthFirstSearch
                        <GeneralDirectedGraphNode>()
                        .search(a,
                                c, 
                                new GeneralDirectedGraphNodeForwardExpander(),
                                new GeneralDirectedGraphNodeBackwardExpander());
        
        assertEquals(3, path.size());
    }
    
    @Test
    public void testOnCycleTargetNotReachable() {
        GeneralDirectedGraphNode s  = new GeneralDirectedGraphNode("s");
        GeneralDirectedGraphNode a1 = new GeneralDirectedGraphNode("a1");
        GeneralDirectedGraphNode a2 = new GeneralDirectedGraphNode("a2");
        GeneralDirectedGraphNode a3 = new GeneralDirectedGraphNode("a3");
        GeneralDirectedGraphNode t  = new GeneralDirectedGraphNode("t");
        
        a1.addChild(a2);
        a2.addChild(a3);
        a3.addChild(a1);
        s.addChild(a1);
        
        new BidirectionalIterativeDeepeningDepthFirstSearch
                <GeneralDirectedGraphNode>()
                .search(s,
                        t,
                        new GeneralDirectedGraphNodeForwardExpander(), 
                        new GeneralDirectedGraphNodeBackwardExpander());
    }
    
    @Test
    public void testSearchOnDisconnected() {
        GeneralDirectedGraphNode a1 = new GeneralDirectedGraphNode("a1");
        GeneralDirectedGraphNode a2 = new GeneralDirectedGraphNode("a2");
        GeneralDirectedGraphNode b1 = new GeneralDirectedGraphNode("b1");
        GeneralDirectedGraphNode b2 = new GeneralDirectedGraphNode("b2");
        
        a1.addChild(a2);
        b1.addChild(b2);
        
        List<GeneralDirectedGraphNode> path = 
        new BidirectionalIterativeDeepeningDepthFirstSearch
                <GeneralDirectedGraphNode>()
                .search(a1, 
                        b1, 
                        new GeneralDirectedGraphNodeForwardExpander(), 
                        new GeneralDirectedGraphNodeBackwardExpander());
        
        assertNull(path);
    }
    
    @Test
    public void testOmitMeetingNode() {
        GeneralDirectedGraphNode a = new GeneralDirectedGraphNode("a");
        GeneralDirectedGraphNode b = new GeneralDirectedGraphNode("b");
        GeneralDirectedGraphNode c = new GeneralDirectedGraphNode("c");
        GeneralDirectedGraphNode d = new GeneralDirectedGraphNode("d");
        
        a.addChild(b);
        b.addChild(c);
        c.addChild(d);
        
        List<GeneralDirectedGraphNode> path = 
                new BidirectionalIterativeDeepeningDepthFirstSearch
                        <GeneralDirectedGraphNode>()
                .search(
                        a, 
                        d,
                        new GeneralDirectedGraphNodeForwardExpander(),
                        new GeneralDirectedGraphNodeBackwardExpander());
        
        assertTrue(Arrays.asList(a, b, c, d).equals(path));
    }
    
    @Test
    public void testOmitStuck() {
        GeneralDirectedGraphNode a = new GeneralDirectedGraphNode("a");
        GeneralDirectedGraphNode b = new GeneralDirectedGraphNode("b");
        GeneralDirectedGraphNode c = new GeneralDirectedGraphNode("c");
        GeneralDirectedGraphNode d = new GeneralDirectedGraphNode("d");
        
        a.addChild(b);
        b.addChild(c);
        c.addChild(d);
        
        d.addChild(c);
        c.addChild(b);
        b.addChild(a);
        
        List<GeneralDirectedGraphNode> path = 
                new BidirectionalIterativeDeepeningDepthFirstSearch
                        <GeneralDirectedGraphNode>()
                .search(
                        a, 
                        d,
                        new GeneralDirectedGraphNodeForwardExpander(),
                        new GeneralDirectedGraphNodeBackwardExpander());
        
            assertEquals(4, path.size());
    }
    
    @Test
    public void testShortPath() {
        GeneralDirectedGraphNode source = 
                new GeneralDirectedGraphNode("source");
        
        GeneralDirectedGraphNode target = 
                new GeneralDirectedGraphNode("target");
        
        source.addChild(target);
        
        Logger logger = Logger.getLogger(this.getClass().getSimpleName());
        logger.log(Level.ALL, "Before computing the path.");
        
        List<GeneralDirectedGraphNode> path = 
                new BidirectionalIterativeDeepeningDepthFirstSearch<
                        GeneralDirectedGraphNode>()
                .search(source,
                        target,
                        new GeneralDirectedGraphNodeForwardExpander(),
                        new GeneralDirectedGraphNodeBackwardExpander());
        
        logger.log(Level.ALL, "After computing the path.");
        
        assertEquals(2, path.size());
        assertEquals(source, path.get(0));
        assertEquals(target, path.get(1));
    }
    
    @Test
    public void testWhenTerminalNodesAreNeighbors() {
        GeneralDirectedGraphNode source = new GeneralDirectedGraphNode("source");
        GeneralDirectedGraphNode target = new GeneralDirectedGraphNode("target");
        
        source.addChild(target);
        target.addChild(target);
        source.addChild(source);
        
        new BidirectionalBreadthFirstSearch<GeneralDirectedGraphNode>()
                .search(source, 
                        target, 
                        new GeneralDirectedGraphNodeForwardExpander(), 
                        new GeneralDirectedGraphNodeForwardExpander());
    }
    
    @Test
    public void testFrontiersDoNotAgreeOnMeetingNode() {
        GeneralDirectedGraphNode s = new GeneralDirectedGraphNode("s");
        GeneralDirectedGraphNode a = new GeneralDirectedGraphNode("a");
        GeneralDirectedGraphNode b = new GeneralDirectedGraphNode("b");
        GeneralDirectedGraphNode t = new GeneralDirectedGraphNode("t");
        
        s.addChild(a);
        a.addChild(b);
        b.addChild(t);
        
        List<GeneralDirectedGraphNode> path = 
        new BidirectionalIterativeDeepeningDepthFirstSearch
                <GeneralDirectedGraphNode>()
                .search(s, 
                        t, 
                        new GeneralDirectedGraphNodeForwardExpander(),
                        new GeneralDirectedGraphNodeBackwardExpander());
    }
    
    @Test
    public void does() {
        GeneralDirectedGraphNode s = new GeneralDirectedGraphNode("s");
        GeneralDirectedGraphNode a = new GeneralDirectedGraphNode("a");
        GeneralDirectedGraphNode b = new GeneralDirectedGraphNode("b");
        GeneralDirectedGraphNode c = new GeneralDirectedGraphNode("c");
        GeneralDirectedGraphNode t = new GeneralDirectedGraphNode("t");
        
        s.addChild(a);
        a.addChild(b);
        b.addChild(c);
        c.addChild(a);
        
        new BidirectionalIterativeDeepeningDepthFirstSearch
                <GeneralDirectedGraphNode>()
                .search(s,
                        t, 
                        new GeneralDirectedGraphNodeForwardExpander(), 
                        new GeneralDirectedGraphNodeBackwardExpander());
    }
}
