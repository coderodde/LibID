package io.github.coderodde.libid.demo;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (May 4, 2019)
 */
public final class RubiksCubeNode {

    public enum Axis {
        X,
        Y,
        Z
    }

    public enum Direction {
        CLOCKWISE,
        COUNTER_CLOCKWISE
    }

    public enum Layer {
        LAYER_1(0),
        LAYER_2(1),
        LAYER_3(2);

        private final int layerNumber;

        private Layer(int layerNumber) {
            this.layerNumber = layerNumber;
        }

        public int toInteger() {
            return layerNumber;
        }
    }

    private final int[][][] data;

    private Set<RubiksCubeNode> neighbors;
    
    public RubiksCubeNode() {
        data = new int[3][3][3];
        
        for (int x = 0, value = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    data[x][y][z] = value++;
                }
            }
        }
    }

    public RubiksCubeNode(RubiksCubeNode other) {
        this.data = new int[3][3][3];

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    data[x][y][z] = other.data[x][y][z];
                }
            }
        }
    }

    public RubiksCubeNode rotate(Axis axis,
                                 Layer layer,
                                 Direction direction) {
        RubiksCubeNode nextNode = new RubiksCubeNode(this);

        switch (axis) {
            case X:
                rotateAroundXAxis(nextNode, direction, layer.toInteger());
                break;

            case Y:
                rotateAroundYAxis(nextNode, direction, layer.toInteger());
                break;

            case Z:
                rotateAroundZAxis(nextNode, direction, layer.toInteger());
                break;

            default:
                throw new IllegalArgumentException("Unknown axis enum.");
        }

        return nextNode;
    }

    public Set<RubiksCubeNode> computeNeighbors() {
        if (neighbors != null) {
            return neighbors;
        }

        neighbors = new HashSet<>(18);

        for (Layer layer : Layer.values()) {
            for (Axis axis : Axis.values()) {
                for (Direction direction : Direction.values()) {
                    neighbors.add(rotate(axis, layer, direction));
                }
            }
        }

        return neighbors;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (!o.getClass().equals(this.getClass())) {
            return false;
        }

        RubiksCubeNode other = (RubiksCubeNode) o;
        
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    if (data[x][y][z] != other.data[x][y][z]) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        
        for (int x = 0, i = 1; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++, i++) {
                    hashCode += i * data[x][y][z];
                }
            }
        }
        
        return hashCode;
    }

    private static void rotateAroundXAxis(RubiksCubeNode node,
                                          Direction direction,
                                          int xLayer) {
        switch (direction) {
            case CLOCKWISE:
                rotateAroundXAxisClockwise(node, xLayer);
                break;

            case COUNTER_CLOCKWISE:
                rotateAroundXAxisCounterClockwise(node, xLayer);
                break;
        }
    }

    private static void rotateAroundYAxis(RubiksCubeNode node,
                                          Direction direction,
                                          int yLayer) {
        switch (direction) {
            case CLOCKWISE:
                rotateAroundYAxisClockwise(node, yLayer);
                break;

            case COUNTER_CLOCKWISE:
                rotateAroundYAxisCounterClockwise(node, yLayer);
                break;
        }
    }

    private static void rotateAroundZAxis(RubiksCubeNode node,
                                          Direction direction,
                                          int zLayer) {
        switch (direction) {
            case CLOCKWISE:
                rotateAroundZAxisClockwise(node, zLayer);
                break;

            case COUNTER_CLOCKWISE:
                rotateAroundZAxisCounterClockwise(node, zLayer);
                break;
        }
    }


    private static void rotateAroundXAxisClockwise(RubiksCubeNode node,
                                                   int xLayer) {
        int[][][] data = node.data;
        int saveValue1 = data[xLayer][0][0];
        int saveValue2 = data[xLayer][1][0];

        data[xLayer][1][0] = data[xLayer][0][1];
        data[xLayer][0][0] = data[xLayer][0][2];
        data[xLayer][0][1] = data[xLayer][1][2];
        data[xLayer][0][2] = data[xLayer][2][2];

        data[xLayer][1][2] = data[xLayer][2][1];
        data[xLayer][2][2] = data[xLayer][2][0];
        data[xLayer][2][1] = saveValue2;
        data[xLayer][2][0] = saveValue1;
    }

    private static void rotateAroundXAxisCounterClockwise(RubiksCubeNode node,
                                                          int xLayer) {
        int[][][] data = node.data;
        int saveValue1 = data[xLayer][0][0];
        int saveValue2 = data[xLayer][1][0];

        data[xLayer][0][0] = data[xLayer][2][0];
        data[xLayer][1][0] = data[xLayer][2][1];
        data[xLayer][2][0] = data[xLayer][2][2];
        data[xLayer][2][1] = data[xLayer][1][2];

        data[xLayer][2][2] = data[xLayer][0][2];
        data[xLayer][1][2] = data[xLayer][0][1];
        data[xLayer][0][2] = saveValue1;
        data[xLayer][0][1] = saveValue2;
    }

    private static void rotateAroundYAxisClockwise(RubiksCubeNode node, 
                                                   int yLayer) {
        int[][][] data = node.data;
        int saveValue1 = data[0][yLayer][0];
        int saveValue2 = data[0][yLayer][1];

        data[0][yLayer][1] = data[1][yLayer][0];
        data[0][yLayer][0] = data[2][yLayer][0];
        data[1][yLayer][0] = data[2][yLayer][1];
        data[2][yLayer][0] = data[2][yLayer][2];

        data[2][yLayer][1] = data[1][yLayer][2];
        data[2][yLayer][2] = data[0][yLayer][2];
        data[1][yLayer][2] = saveValue2;
        data[0][yLayer][2] = saveValue1;
    }

    private static void rotateAroundYAxisCounterClockwise(RubiksCubeNode node,
                                                          int yLayer) {
        int[][][] data = node.data;
        int saveValue1 = data[0][yLayer][0];
        int saveValue2 = data[0][yLayer][1];

        data[0][yLayer][0] = data[0][yLayer][2];
        data[0][yLayer][1] = data[1][yLayer][2];
        data[0][yLayer][2] = data[2][yLayer][2];
        data[1][yLayer][2] = data[2][yLayer][1];

        data[2][yLayer][2] = data[2][yLayer][0];
        data[2][yLayer][1] = data[1][yLayer][0];
        data[2][yLayer][0] = saveValue1;
        data[1][yLayer][0] = saveValue2;
    }

    private static void rotateAroundZAxisClockwise(RubiksCubeNode node,
                                                   int zLayer) {
        int[][][] data = node.data;
        int saveValue1 = data[0][0][zLayer];
        int saveValue2 = data[0][1][zLayer];

        data[0][1][zLayer] = data[1][0][zLayer];
        data[0][0][zLayer] = data[2][0][zLayer];
        data[1][0][zLayer] = data[2][1][zLayer];
        data[2][0][zLayer] = data[2][2][zLayer];

        data[2][1][zLayer] = data[1][2][zLayer];
        data[2][2][zLayer] = data[0][2][zLayer];
        data[1][2][zLayer] = saveValue2;
        data[0][2][zLayer] = saveValue1;
    }

    private static void rotateAroundZAxisCounterClockwise(RubiksCubeNode node,
                                                          int zLayer) {
        int[][][] data = node.data;
        int saveValue1 = data[0][0][zLayer];
        int saveValue2 = data[0][1][zLayer];

        data[0][0][zLayer] = data[0][2][zLayer];
        data[0][1][zLayer] = data[1][2][zLayer];
        data[0][2][zLayer] = data[2][2][zLayer];
        data[1][2][zLayer] = data[2][1][zLayer];

        data[2][2][zLayer] = data[2][0][zLayer];
        data[2][1][zLayer] = data[1][0][zLayer];
        data[2][0][zLayer] = saveValue1;
        data[1][0][zLayer] = saveValue2;
    }
}
