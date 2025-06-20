package io.github.coderodde.libid.impl;

import io.github.coderodde.libid.demo.Demo.DirectedGraphNode;
import io.github.coderodde.libid.IntHeuristicFunction;

/**
 * This class implements a heuristic function for the grid graphs. 
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 20, 2018)
 */
public class GridHeuristicFunction implements IntHeuristicFunction<DirectedGraphNode> {

    @Override
    public int estimate(DirectedGraphNode current, DirectedGraphNode target) {
        int x1 = current.getX();
        int y1 = current.getY();
        int x2 = target.getX();
        int y2 = target.getY();
        int deltaX = Math.abs(x1 - x2);
        int deltaY = Math.abs(y1 - y2);
        return Math.max(deltaX, deltaY);
    }
}
