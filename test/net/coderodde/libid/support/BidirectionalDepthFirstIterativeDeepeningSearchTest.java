package net.coderodde.libid.support;

import net.coderodde.libid.impl.BidirectionalBreadthFirstSearch;
import net.coderodde.libid.impl.BidirectionalDepthFirstIterativeDeepeningSearch;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;
import net.coderodde.libid.Demo.GeneralDirectedGraphNode;
import net.coderodde.libid.Demo.GeneralDirectedGraphNodeBackwardExpander;
import net.coderodde.libid.Demo.GeneralDirectedGraphNodeForwardExpander;
import static org.junit.Assume.assumeTrue;

public class BidirectionalDepthFirstIterativeDeepeningSearchTest {

    @Test
    public void testSearchSmall() {
        GeneralDirectedGraphNode a  = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode b1 = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode b2 = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode c1 = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode c2 = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode d  = new GeneralDirectedGraphNode();
        
        a.addChild(b1);
        a.addChild(b2);
        
        b1.addChild(c1);
        b2.addChild(c2);
        
        c1.addChild(d);
        c2.addChild(d);
        
        List<GeneralDirectedGraphNode> path;
        path = new BidirectionalDepthFirstIterativeDeepeningSearch
                <GeneralDirectedGraphNode>()
                .search(a,
                        d,
                        new GeneralDirectedGraphNodeForwardExpander(),
                        new GeneralDirectedGraphNodeBackwardExpander());
        
        assertEquals(4, path.size());
    }
    
    @Test
    public void testSearchSmall2() {
        GeneralDirectedGraphNode a  = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode b1 = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode b2 = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode c1 = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode c2 = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode c3 = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode d  = new GeneralDirectedGraphNode();
        
        a.addChild(b1);
        a.addChild(c1);
        
        b1.addChild(b2);
        b2.addChild(d);
        
        c1.addChild(c2);
        c2.addChild(c3);
        c3.addChild(d);
        
        List<GeneralDirectedGraphNode> path = 
                new BidirectionalDepthFirstIterativeDeepeningSearch
                        <GeneralDirectedGraphNode>()
                        .search(a,
                                d, 
                                new GeneralDirectedGraphNodeForwardExpander(),
                                new GeneralDirectedGraphNodeBackwardExpander());
        
        assertEquals(4, path.size());
    }
    
    @Test
    public void testSearchSmallest() {
        GeneralDirectedGraphNode a = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode b = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode c = new GeneralDirectedGraphNode();
        
        a.addChild(b);
        b.addChild(c);
        
        List<GeneralDirectedGraphNode> path = 
                new BidirectionalDepthFirstIterativeDeepeningSearch
                        <GeneralDirectedGraphNode>()
                        .search(a,
                                c, 
                                new GeneralDirectedGraphNodeForwardExpander(),
                                new GeneralDirectedGraphNodeBackwardExpander());
        
        assertEquals(3, path.size());
    }
    
    @Test
    public void testSearchOnDisconnected() {
        System.out.println("disco!");
        GeneralDirectedGraphNode a1 = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode a2 = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode b1 = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode b2 = new GeneralDirectedGraphNode();
        
        a1.addChild(a2);
        b1.addChild(b2);
        
        List<GeneralDirectedGraphNode> path = 
        new BidirectionalDepthFirstIterativeDeepeningSearch
                <GeneralDirectedGraphNode>()
                .search(a1, 
                        b1, 
                        new GeneralDirectedGraphNodeForwardExpander(), 
                        new GeneralDirectedGraphNodeBackwardExpander());
        
        assertNull(path);
        System.out.println("yeah!");
    }
    
    @Test
    public void testOmitMeetingNode() {
        GeneralDirectedGraphNode a = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode b = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode c = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode d = new GeneralDirectedGraphNode();
        
        a.addChild(b);
        b.addChild(c);
        c.addChild(d);
        
        List<GeneralDirectedGraphNode> path = 
                new BidirectionalDepthFirstIterativeDeepeningSearch
                        <GeneralDirectedGraphNode>()
                .search(
                        a, 
                        d,
                        new GeneralDirectedGraphNodeForwardExpander(),
                        new GeneralDirectedGraphNodeBackwardExpander());
        
        assumeTrue(Arrays.asList(a, b, d).equals(path));
    }
    
    @Test
    public void testOmitStuck() {
        GeneralDirectedGraphNode a = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode b = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode c = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode d = new GeneralDirectedGraphNode();
        
        a.addChild(b);
        b.addChild(c);
        c.addChild(d);
        
        d.addChild(c);
        c.addChild(b);
        b.addChild(a);
        
        List<GeneralDirectedGraphNode> path = 
                new BidirectionalDepthFirstIterativeDeepeningSearch
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
        GeneralDirectedGraphNode source = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode target = new GeneralDirectedGraphNode();
        source.addChild(target);
        
        Logger logger = Logger.getLogger(this.getClass().getSimpleName());
        logger.log(Level.ALL, "Before computing the path.");
        
        List<GeneralDirectedGraphNode> path = 
                new BidirectionalDepthFirstIterativeDeepeningSearch<
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
        GeneralDirectedGraphNode source = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode target = new GeneralDirectedGraphNode();
        
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
        GeneralDirectedGraphNode s = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode a = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode b = new GeneralDirectedGraphNode();
        GeneralDirectedGraphNode t = new GeneralDirectedGraphNode();
        
        s.addChild(a);
        a.addChild(b);
        b.addChild(t);
        
        List<GeneralDirectedGraphNode> path = 
        new BidirectionalDepthFirstIterativeDeepeningSearch
                <GeneralDirectedGraphNode>()
                .search(s, 
                        t, 
                        new GeneralDirectedGraphNodeForwardExpander(),
                        new GeneralDirectedGraphNodeBackwardExpander());
    }
}
