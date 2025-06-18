package io.github.coderodde.libid;

public interface IntHeuristicFunction<N> {

    public int estimate(N current, N target);
}
