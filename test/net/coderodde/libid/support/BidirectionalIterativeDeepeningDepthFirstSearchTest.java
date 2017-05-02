package net.coderodde.libid.support;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import net.coderodde.libid.Demo.DirectedGraphNode;
import net.coderodde.libid.Demo.DirectedGraphNodeForwardExpander;
import net.coderodde.libid.Demo.DirectedGraphNodeBackwardExpander;

public class BidirectionalIterativeDeepeningDepthFirstSearchTest {
    
    @Test
    public void testSearchSmall() {
        System.out.println("yo?");
        DirectedGraphNode a = new DirectedGraphNode();
        DirectedGraphNode b1 = new DirectedGraphNode();
        DirectedGraphNode b2 = new DirectedGraphNode();
        DirectedGraphNode c1 = new DirectedGraphNode();
        DirectedGraphNode c2 = new DirectedGraphNode();
        DirectedGraphNode d = new DirectedGraphNode();
        
        a.addChild(b1);
        a.addChild(b2);
        
        b1.addChild(c1);
        b2.addChild(c2);
        
        c1.addChild(d);
        c2.addChild(d);
        
        List<DirectedGraphNode> path = 
                new BidirectionalIterativeDeepeningDepthFirstSearch
                        <DirectedGraphNode>()
                        .search(a,
                                d, 
                                new DirectedGraphNodeForwardExpander(),
                                new DirectedGraphNodeBackwardExpander());
        
        assertEquals(4, path.size());
        System.out.println("yo!");
    }
    
    @Test
    public void testSearchSmall2() {
        DirectedGraphNode a = new DirectedGraphNode();
        DirectedGraphNode b1 = new DirectedGraphNode();
        DirectedGraphNode b2 = new DirectedGraphNode();
        DirectedGraphNode c1 = new DirectedGraphNode();
        DirectedGraphNode c2 = new DirectedGraphNode();
        DirectedGraphNode c3 = new DirectedGraphNode();
        DirectedGraphNode d = new DirectedGraphNode();
        
        a.addChild(b1);
        a.addChild(c1);
        
        b1.addChild(b2);
        b2.addChild(d);
        
        c1.addChild(c2);
        c2.addChild(c3);
        c3.addChild(d);
        
        List<DirectedGraphNode> path = 
                new BidirectionalIterativeDeepeningDepthFirstSearch
                        <DirectedGraphNode>()
                        .search(a,
                                d, 
                                new DirectedGraphNodeForwardExpander(),
                                new DirectedGraphNodeBackwardExpander());
        
        assertEquals(4, path.size());
    }
    
    @Test
    public void testSearchSmallest() {
        DirectedGraphNode a = new DirectedGraphNode();
        DirectedGraphNode b = new DirectedGraphNode();
        DirectedGraphNode c = new DirectedGraphNode();
        
        a.addChild(b);
        b.addChild(c);
        
        List<DirectedGraphNode> path = 
                new BidirectionalIterativeDeepeningDepthFirstSearch
                        <DirectedGraphNode>()
                        .search(a,
                                c, 
                                new DirectedGraphNodeForwardExpander(),
                                new DirectedGraphNodeBackwardExpander());
        
        assertEquals(3, path.size());
    }
}
