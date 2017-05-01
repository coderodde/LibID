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
    }
}
