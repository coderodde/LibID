package net.coderodde.libid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class SlidingTilePuzzleNode {

    private final int[] state;

    private int zeroTileIndex;

    public SlidingTilePuzzleNode() {
        this.state = new int[9];
        
        for (int i = 0; i < state.length - 1; ++i) {
            state[i] = i + 1;
        }

        zeroTileIndex = state.length - 1;
    }

    private SlidingTilePuzzleNode(int[] state, int zeroTileIndex) {
        this.state = state.clone();
        this.zeroTileIndex = zeroTileIndex;
    }

    public SlidingTilePuzzleNode slideUp() {
        int x = getX(zeroTileIndex);
        int y = getY(zeroTileIndex);

        if (y == 0) {
            return null;
        }

        int nextx = x;
        int nexty = y - 1;
        int nextIndex = xyToIndex(nextx, nexty);
        SlidingTilePuzzleNode node = new SlidingTilePuzzleNode(state,   
                                                               nextIndex);
        int tmp = node.state[nextIndex];
        node.state[nextIndex] = node.state[zeroTileIndex];
        node.state[zeroTileIndex] = tmp;
        return node;
    }

    public SlidingTilePuzzleNode slideDown() {
        int x = getX(zeroTileIndex);
        int y = getY(zeroTileIndex);

        if (y == 2) {
            return null;
        }

        int nextx = x;
        int nexty = y + 1;
        int nextIndex = xyToIndex(nextx, nexty);
        SlidingTilePuzzleNode node = new SlidingTilePuzzleNode(state,   
                                                               nextIndex);
        int tmp = node.state[nextIndex];
        node.state[nextIndex] = node.state[zeroTileIndex];
        node.state[zeroTileIndex] = tmp;
        return node;
    }

    public SlidingTilePuzzleNode slideLeft() {
        int x = getX(zeroTileIndex);
        int y = getY(zeroTileIndex);

        if (x == 0) {
            return null;
        }

        int nextx = x - 1;
        int nexty = y;
        int nextIndex = xyToIndex(nextx, nexty);
        SlidingTilePuzzleNode node = new SlidingTilePuzzleNode(state,   
                                                               nextIndex);
        int tmp = node.state[nextIndex];
        node.state[nextIndex] = node.state[zeroTileIndex];
        node.state[zeroTileIndex] = tmp;
        return node;
    }

    public SlidingTilePuzzleNode slideRight() {
        int x = getX(zeroTileIndex);
        int y = getY(zeroTileIndex);

        if (x == 2) {
            return null;
        }

        int nextx = x + 1;
        int nexty = y;
        int nextIndex = xyToIndex(nextx, nexty);
        SlidingTilePuzzleNode node = new SlidingTilePuzzleNode(state, 
                                                               nextIndex);
        int tmp = node.state[nextIndex];
        node.state[nextIndex] = node.state[zeroTileIndex];
        node.state[zeroTileIndex] = tmp;
        return node;
    }

    public Collection<SlidingTilePuzzleNode> getNeighbors() {
        List<SlidingTilePuzzleNode> neighborList = new ArrayList<>(4);
        SlidingTilePuzzleNode node = slideUp();

        if (node != null) {
            neighborList.add(node);
        }

        node = slideDown();

        if (node != null) {
            neighborList.add(node);
        }

        node = slideLeft();

        if (node != null) {
            neighborList.add(node);
        }

        node = slideRight();

        if (node != null) {
            neighborList.add(node);
        }

        return neighborList;
    }
    
    public int readTile(int x, int y) {
        return state[xyToIndex(x, y)];
    }
    
    public void swap(int x1, int y1, int x2, int y2) {
        int index1 = xyToIndex(x1, y1);
        int index2 = xyToIndex(x2, y2);
        int tmp = state[index1];
        state[index1] = state[index2];
        state[index2] = tmp;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        
        if (o == null) {
            return false;
        }
        
        if (!getClass().equals(o.getClass())) {
            return false;
        }
        
        return Arrays.equals(state, ((SlidingTilePuzzleNode) o).state);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(state);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String lineSeparator = "";
        int index = 0;

        for (int y = 0; y < 3; ++y) {
            sb.append(lineSeparator);
            lineSeparator = "\n";
            
            for (int x = 0; x < 3; ++x) {
                sb.append(state[index++]);
            }
        }

        return sb.toString();
    }

    private static int getX(int index) {
        return index % 3;
    }

    private static int getY(int index) {
        return index / 3;
    }

    private static int xyToIndex(int x, int y) {
        return y * 3 + x;
    }
}
