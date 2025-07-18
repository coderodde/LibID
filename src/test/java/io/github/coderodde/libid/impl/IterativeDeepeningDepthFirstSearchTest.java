package io.github.coderodde.libid.impl;

import io.github.coderodde.libid.demo.Demo;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

public class IterativeDeepeningDepthFirstSearchTest {

    @Test
    public void testSearchSmall() {
        Demo.GeneralDirectedGraphNode a  = new Demo.GeneralDirectedGraphNode("a");
        Demo.GeneralDirectedGraphNode b1 = new Demo.GeneralDirectedGraphNode("b1");
        Demo.GeneralDirectedGraphNode b2 = new Demo.GeneralDirectedGraphNode("b2");
        Demo.GeneralDirectedGraphNode c1 = new Demo.GeneralDirectedGraphNode("c1");
        Demo.GeneralDirectedGraphNode c2 = new Demo.GeneralDirectedGraphNode("c2");
        Demo.GeneralDirectedGraphNode d  = new Demo.GeneralDirectedGraphNode("d");
        
        a.addChild(b1);
        a.addChild(b2);
        
        b1.addChild(c1);
        b2.addChild(c2);
        
        c1.addChild(d);
        c2.addChild(d);
        
        List<Demo.GeneralDirectedGraphNode> path;
        path = new IterativeDeepeningDepthFirstSearch
                <Demo.GeneralDirectedGraphNode>()
                .search(a,
                        d,
                        new Demo.GeneralDirectedGraphNodeForwardExpander());
        
        System.out.println("testSearchSmall: " + path);
        
        assertEquals(4, path.size());
    }
    
    @Test
    public void testSearchSmall2() {
        Demo.GeneralDirectedGraphNode a  = new Demo.GeneralDirectedGraphNode("a");
        Demo.GeneralDirectedGraphNode b1 = new Demo.GeneralDirectedGraphNode("b1");
        Demo.GeneralDirectedGraphNode b2 = new Demo.GeneralDirectedGraphNode("b2");
        Demo.GeneralDirectedGraphNode c1 = new Demo.GeneralDirectedGraphNode("c1");
        Demo.GeneralDirectedGraphNode c2 = new Demo.GeneralDirectedGraphNode("c2");
        Demo.GeneralDirectedGraphNode c3 = new Demo.GeneralDirectedGraphNode("c3");
        Demo.GeneralDirectedGraphNode d  = new Demo.GeneralDirectedGraphNode("d");
        
        a.addChild(b1);
        a.addChild(c1);
        
        b1.addChild(b2);
        b2.addChild(d);
        
        c1.addChild(c2);
        c2.addChild(c3);
        c3.addChild(d);
        
        List<Demo.GeneralDirectedGraphNode> path = 
                new IterativeDeepeningDepthFirstSearch
                        <Demo.GeneralDirectedGraphNode>()
                        .search(a,
                                d, 
                                new Demo.GeneralDirectedGraphNodeForwardExpander());

        System.out.println("testSearchSmall2: " + path);
        
        assertEquals(4, path.size());
    }
    
    @Test
    public void testSearchSmallest() {
        Demo.GeneralDirectedGraphNode a = new Demo.GeneralDirectedGraphNode("a");
        Demo.GeneralDirectedGraphNode b = new Demo.GeneralDirectedGraphNode("b");
        Demo.GeneralDirectedGraphNode c = new Demo.GeneralDirectedGraphNode("c");
        
        a.addChild(b);
        b.addChild(c);
        
        List<Demo.GeneralDirectedGraphNode> path = 
                new IterativeDeepeningDepthFirstSearch
                        <Demo.GeneralDirectedGraphNode>()
                        .search(a,
                                c, 
                                new Demo.GeneralDirectedGraphNodeForwardExpander());
        
        assertEquals(3, path.size());
    }
    
    @Test
    public void testOnCycleTargetNotReachable() {
        Demo.GeneralDirectedGraphNode s  = new Demo.GeneralDirectedGraphNode("s");
        Demo.GeneralDirectedGraphNode a1 = new Demo.GeneralDirectedGraphNode("a1");
        Demo.GeneralDirectedGraphNode a2 = new Demo.GeneralDirectedGraphNode("a2");
        Demo.GeneralDirectedGraphNode a3 = new Demo.GeneralDirectedGraphNode("a3");
        Demo.GeneralDirectedGraphNode t  = new Demo.GeneralDirectedGraphNode("t");
        
        a1.addChild(a2);
        a2.addChild(a3);
        a3.addChild(a1);
        s.addChild(a1);
        
        new IterativeDeepeningDepthFirstSearch
                <Demo.GeneralDirectedGraphNode>()
                .search(s,
                        t,
                        new Demo.GeneralDirectedGraphNodeForwardExpander());
    }
    
    @Test
    public void testSearchOnDisconnected() {
        Demo.GeneralDirectedGraphNode a1 = new Demo.GeneralDirectedGraphNode("a1");
        Demo.GeneralDirectedGraphNode a2 = new Demo.GeneralDirectedGraphNode("a2");
        Demo.GeneralDirectedGraphNode b1 = new Demo.GeneralDirectedGraphNode("b1");
        Demo.GeneralDirectedGraphNode b2 = new Demo.GeneralDirectedGraphNode("b2");
        
        a1.addChild(a2);
        b1.addChild(b2);
        
        List<Demo.GeneralDirectedGraphNode> path = 
        new IterativeDeepeningDepthFirstSearch
                <Demo.GeneralDirectedGraphNode>()
                .search(a1, 
                        b1, 
                        new Demo.GeneralDirectedGraphNodeForwardExpander());
        
        assertNull(path);
    }
    
    @Test
    public void testOmitMeetingNode() {
        Demo.GeneralDirectedGraphNode a = new Demo.GeneralDirectedGraphNode("a");
        Demo.GeneralDirectedGraphNode b = new Demo.GeneralDirectedGraphNode("b");
        Demo.GeneralDirectedGraphNode c = new Demo.GeneralDirectedGraphNode("c");
        Demo.GeneralDirectedGraphNode d = new Demo.GeneralDirectedGraphNode("d");
        
        a.addChild(b);
        b.addChild(c);
        c.addChild(d);
        
        List<Demo.GeneralDirectedGraphNode> path = 
                new IterativeDeepeningDepthFirstSearch
                        <Demo.GeneralDirectedGraphNode>()
                .search(
                        a, 
                        d,
                        new Demo.GeneralDirectedGraphNodeForwardExpander());
        
        assertTrue(Arrays.asList(a, b, c, d).equals(path));
    }
    
    @Test
    public void testOmitStuck() {
        Demo.GeneralDirectedGraphNode a = new Demo.GeneralDirectedGraphNode("a");
        Demo.GeneralDirectedGraphNode b = new Demo.GeneralDirectedGraphNode("b");
        Demo.GeneralDirectedGraphNode c = new Demo.GeneralDirectedGraphNode("c");
        Demo.GeneralDirectedGraphNode d = new Demo.GeneralDirectedGraphNode("d");
        
        a.addChild(b);
        b.addChild(c);
        c.addChild(d);
        
        d.addChild(c);
        c.addChild(b);
        b.addChild(a);
        
        List<Demo.GeneralDirectedGraphNode> path = 
                new IterativeDeepeningDepthFirstSearch
                        <Demo.GeneralDirectedGraphNode>()
                .search(
                        a, 
                        d,
                        new Demo.GeneralDirectedGraphNodeForwardExpander());
        
            assertEquals(4, path.size());
    }
    
    @Test
    public void testShortPath() {
        Demo.GeneralDirectedGraphNode source = 
                new Demo.GeneralDirectedGraphNode("source");
        
        Demo.GeneralDirectedGraphNode target = 
                new Demo.GeneralDirectedGraphNode("target");
        
        source.addChild(target);
        
        Logger logger = Logger.getLogger(this.getClass().getSimpleName());
        logger.log(Level.ALL, "Before computing the path.");
        
        List<Demo.GeneralDirectedGraphNode> path = 
                new IterativeDeepeningDepthFirstSearch<
                        Demo.GeneralDirectedGraphNode>()
                .search(source,
                        target,
                        new Demo.GeneralDirectedGraphNodeForwardExpander());
        
        logger.log(Level.ALL, "After computing the path.");
        
        assertEquals(2, path.size());
        assertEquals(source, path.get(0));
        assertEquals(target, path.get(1));
    }
    
    @Test
    public void testWhenTerminalNodesAreNeighbors() {
        Demo.GeneralDirectedGraphNode source = new Demo.GeneralDirectedGraphNode("source");
        Demo.GeneralDirectedGraphNode target = new Demo.GeneralDirectedGraphNode("target");
        
        source.addChild(target);
        target.addChild(target);
        source.addChild(source);
        
        new BreadthFirstSearch<Demo.GeneralDirectedGraphNode>()
                .search(source, 
                        target, 
                        new Demo.GeneralDirectedGraphNodeForwardExpander());
    }
    
    @Test
    public void testFrontiersDoNotAgreeOnMeetingNode() {
        Demo.GeneralDirectedGraphNode s = new Demo.GeneralDirectedGraphNode("s");
        Demo.GeneralDirectedGraphNode a = new Demo.GeneralDirectedGraphNode("a");
        Demo.GeneralDirectedGraphNode b = new Demo.GeneralDirectedGraphNode("b");
        Demo.GeneralDirectedGraphNode t = new Demo.GeneralDirectedGraphNode("t");
        
        s.addChild(a);
        a.addChild(b);
        b.addChild(t);
        
        List<Demo.GeneralDirectedGraphNode> path = 
        new IterativeDeepeningDepthFirstSearch
                <Demo.GeneralDirectedGraphNode>()
                .search(s, 
                        t, 
                        new Demo.GeneralDirectedGraphNodeForwardExpander());
    }
    
    @Test
    public void does() {
        Demo.GeneralDirectedGraphNode s = new Demo.GeneralDirectedGraphNode("s");
        Demo.GeneralDirectedGraphNode a = new Demo.GeneralDirectedGraphNode("a");
        Demo.GeneralDirectedGraphNode b = new Demo.GeneralDirectedGraphNode("b");
        Demo.GeneralDirectedGraphNode c = new Demo.GeneralDirectedGraphNode("c");
        Demo.GeneralDirectedGraphNode t = new Demo.GeneralDirectedGraphNode("t");
        
        s.addChild(a);
        a.addChild(b);
        b.addChild(c);
        c.addChild(a);
        
        new IterativeDeepeningDepthFirstSearch
                <Demo.GeneralDirectedGraphNode>()
                .search(s,
                        t, 
                        new Demo.GeneralDirectedGraphNodeForwardExpander());
    }    
}
