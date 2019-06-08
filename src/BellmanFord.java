import java.util.Stack;
import java.util.Vector;

/**
 * Class to solve the shortest path problem with the Bellman-Ford algorithm.
 * @author Alessandro Bruno
 */
public class BellmanFord implements PathFinder {
    private Vector<Vector<DEdge>> directedEdges;
    private int startingNode;
    private int arrivalNode;
    private Stack<Integer> shortestPath;
    private double[] distance;
    private int[] previousNode;

    /**
     * Class constructor. It solves the shortest path problem from the start to the arrival nodes
     * @param start the starting node
     * @param arrival the arrival node
     * @param dEdges adjacency list representation of the graph
     */
    public BellmanFord(int start, int arrival, Vector<Vector<DEdge>> dEdges)
    {
        // Initialize variables
        directedEdges = dEdges;
        startingNode = start;
        arrivalNode = arrival;
        int numberOfNodes = directedEdges.size();
        distance = new double[numberOfNodes];
        previousNode = new int[numberOfNodes];

        // Initialize distance array
        for(int i = 0; i < numberOfNodes; i++)
        {
            distance[i] = Double.POSITIVE_INFINITY;
            previousNode[i] = -1;
        }
        distance[startingNode] = 0;

        // Relax all the edges repeatedly (elementary Bellman-Ford algorithm):
        for (int j = 0; j < numberOfNodes; j++)
        {
            for(Vector<DEdge> vectorDEdges: directedEdges)
            {
                for(DEdge thisEdge: vectorDEdges)
                {
                    //Relax
                    if(distance[thisEdge.getNodeA()] + thisEdge.getCost() < distance[thisEdge.getNodeB()])
                    {
                        distance[thisEdge.getNodeB()] = distance[thisEdge.getNodeA()] + thisEdge.getCost();
                        previousNode[thisEdge.getNodeB()] = thisEdge.getNodeA();
                    }
                }
            }

        }

        // Check for negative loops.
        // This check is not relevant for the GalaxyPathFinder application since negative loops can't be found if all the edges are positive.
        for(Vector<DEdge> vectorDEdges: directedEdges)
        {
            for(DEdge thisEdge: vectorDEdges)
            {
                //Relax
                if(distance[thisEdge.getNodeA()] + thisEdge.getCost() < distance[thisEdge.getNodeB()])
                {
                    throw new IllegalArgumentException("Ungültiger Graph.");
                }
            }
        }



    }

    /**
     * This function reconstructs and returns the shortest path.
     * @return the shortest path or null if no shortest path has been found
     */
    public Stack<Integer> getShortestPath()
    {
        shortestPath = new Stack<Integer>();
        if (distance[arrivalNode] == Double.POSITIVE_INFINITY) return null;
        int currentNode = arrivalNode;
        while (currentNode != startingNode)
        {
            shortestPath.push(currentNode);
            currentNode = previousNode[currentNode];
        }
        shortestPath.push(startingNode);
        return shortestPath;
    }

    /**
     * This function returns the length of the shortest path.
     * @return the length of the shortest path or Double.POSITIVE_INFINITY if no path has been found
     */
    public double getLength()
    {
        return distance[arrivalNode];
    }

}
