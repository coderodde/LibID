package io.github.coderodde.libid;

import io.github.coderodde.libid.RubiksCubeNode;
import java.util.Collection;
import junit.framework.TestCase;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

/**
 * Tests the {@link RubiksCubeNode}. 
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (May 6, 2019)
 */
public class RubiksCubeNodeTest extends TestCase {

    @Test
    public void testRotation1() {
        RubiksCubeNode node = new RubiksCubeNode();
        node = node.rotate(RubiksCubeNode.Axis.X, 
                           RubiksCubeNode.Layer.LAYER_1,
                           RubiksCubeNode.Direction.CLOCKWISE);
        node = node.rotate(RubiksCubeNode.Axis.Z, 
                           RubiksCubeNode.Layer.LAYER_2, 
                           RubiksCubeNode.Direction.COUNTER_CLOCKWISE);
        RubiksCubeNode another = new RubiksCubeNode(node);
        assertEquals(node, another);
        assertEquals(another, node);
        assertEquals(node.hashCode(), another.hashCode());
        RubiksCubeNode third = new RubiksCubeNode(node);
        third = third.rotate(RubiksCubeNode.Axis.Y, 
                             RubiksCubeNode.Layer.LAYER_3,
                             RubiksCubeNode.Direction.CLOCKWISE);
        assertNotEquals(third, node);
        assertNotEquals(third, another);
        assertNotEquals(node.hashCode(), third.hashCode());
    }
    
    @Test
    public void testNeighbors() {
        RubiksCubeNode source = new RubiksCubeNode();
        Collection<RubiksCubeNode> neighbors = source.computeNeighbors();
        
        for (RubiksCubeNode.Axis axis : RubiksCubeNode.Axis.values()) {
            for (RubiksCubeNode.Direction direction 
                    : RubiksCubeNode.Direction.values()) {
                for (RubiksCubeNode.Layer layer 
                        : RubiksCubeNode.Layer.values()) {
                    RubiksCubeNode node = new RubiksCubeNode();
                    node = node.rotate(axis, layer, direction);
                    assertTrue(neighbors.contains(node));
                }
            }
        }
    }
}
