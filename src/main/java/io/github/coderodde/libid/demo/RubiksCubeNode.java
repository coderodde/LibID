package io.github.coderodde.libid.demo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class RubiksCubeNode {

    public enum Axis { X, Y, Z }
    public enum Direction { CLOCKWISE, COUNTER_CLOCKWISE }
    public enum Layer {
        LAYER_1(0), LAYER_2(1), LAYER_3(2);
        private final int index;
        Layer(int index) { this.index = index; }
        public int toIndex() { return index; }
    }

    private final int[][][] data;
    private Set<RubiksCubeNode> neighbors;

    public static record Move(Axis axis, Layer layer, Direction direction) {}

    // Creates a solved cube
    public RubiksCubeNode() {
        this.data = new int[3][3][3];
        int value = 0;
        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 3; y++)
                for (int z = 0; z < 3; z++)
                    data[x][y][z] = value++;
    }
    
    public RubiksCubeNode(RubiksCubeNode copy) {
        this.data = new int[3][3][3];
        
        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 3; y++)
                for (int z = 0; z < 3; z++)
                    data[x][y][z] = copy.data[x][y][z];
    }

    // Used internally to create a rotated cube
    private RubiksCubeNode(int[][][] data) {
        this.data = data;
    }

    public RubiksCubeNode rotate(Axis axis, Layer layer, Direction direction) {
        int[][][] newData = deepCopyData();
        switch (axis) {
            case X -> rotateX(newData, layer.toIndex(), direction);
            case Y -> rotateY(newData, layer.toIndex(), direction);
            case Z -> rotateZ(newData, layer.toIndex(), direction);
        }
        return new RubiksCubeNode(newData);
    }

    public Set<RubiksCubeNode> computeNeighbors() {
        if (neighbors != null)
            return neighbors;

        neighbors = new HashSet<>(18);
        for (Axis axis : Axis.values())
            for (Layer layer : Layer.values())
                for (Direction dir : Direction.values())
                    neighbors.add(rotate(axis, layer, dir));
        return neighbors;
    }

    public boolean isSolved() {
        int expected = 0;
        for (int[][] plane : data)
            for (int[] row : plane)
                for (int cell : row)
                    if (cell != expected++) return false;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RubiksCubeNode other && Arrays.deepEquals(this.data, other.data);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(data);
    }

    private int[][][] deepCopyData() {
        int[][][] copy = new int[3][3][3];
        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 3; y++)
                for (int z = 0; z < 3; z++)
                    copy[x][y][z] = data[x][y][z];
        return copy;
    }

    private void rotateX(int[][][] d, int x, Direction dir) {
        rotateLayer2D(d[x], dir);
    }

    private void rotateY(int[][][] d, int y, Direction dir) {
        int[][] layer = new int[3][3];
        for (int x = 0; x < 3; x++)
            for (int z = 0; z < 3; z++)
                layer[x][z] = d[x][y][z];
        rotateLayer2D(layer, dir);
        for (int x = 0; x < 3; x++)
            for (int z = 0; z < 3; z++)
                d[x][y][z] = layer[x][z];
    }

    private void rotateZ(int[][][] d, int z, Direction dir) {
        int[][] layer = new int[3][3];
        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 3; y++)
                layer[x][y] = d[x][y][z];
        rotateLayer2D(layer, dir);
        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 3; y++)
                d[x][y][z] = layer[x][y];
    }

    private void rotateLayer2D(int[][] layer, Direction dir) {
        if (dir == Direction.CLOCKWISE) {
            transpose(layer);
            reverseRows(layer);
        } else {
            transpose(layer);
            reverseCols(layer);
        }
    }

    private void transpose(int[][] mat) {
        for (int i = 0; i < mat.length; i++)
            for (int j = i + 1; j < mat.length; j++) {
                int tmp = mat[i][j];
                mat[i][j] = mat[j][i];
                mat[j][i] = tmp;
            }
    }

    private void reverseRows(int[][] mat) {
        for (int[] row : mat)
            for (int i = 0, j = row.length - 1; i < j; i++, j--) {
                int tmp = row[i];
                row[i] = row[j];
                row[j] = tmp;
            }
    }

    private void reverseCols(int[][] mat) {
        for (int j = 0; j < mat[0].length; j++)
            for (int i = 0, k = mat.length - 1; i < k; i++, k--) {
                int tmp = mat[i][j];
                mat[i][j] = mat[k][j];
                mat[k][j] = tmp;
            }
    }
}
