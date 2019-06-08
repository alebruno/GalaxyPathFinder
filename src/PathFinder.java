import java.util.Stack;

/**
 * Interface of the class to solve the shortest path problem.
 * @author Alessandro Bruno
 */
public interface PathFinder {
    public Stack<Integer> getShortestPath();
    public double getLength();
    public String getAlgorithmName();
}
