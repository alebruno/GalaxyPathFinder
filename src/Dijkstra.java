import java.util.Stack;
import java.util.Vector;
import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;

/**
 * Class to solve the shortest path problem with the Dijkstra's algorithm making use of a Fibonacci heap.
 * @author Alessandro Bruno
 */
public class Dijkstra implements PathFinder {
    private Vector<Vector<DEdge>> directedEdges;
    private int startingNode;
    private int arrivalNode;
    private Stack<Integer> shortestPath;
    private double[] distance;
    private int[] previousNode;
    private boolean[] inPrioList;
    private FibonacciHeap<Integer> prioList;

    /**
     * Class constructor. It solves the shortest path problem from the start to the arrival nodes
     * @param start the starting node
     * @param arrival the arrival node
     * @param dEdges adjacency list representation of the graph
     */
    public Dijkstra(int start, int arrival, Vector<Vector<DEdge>> dEdges)
    {
        // Initialize variables
        directedEdges = dEdges;
        startingNode = start;
        arrivalNode = arrival;
        int numberOfNodes = directedEdges.size();
        distance = new double[numberOfNodes];
        previousNode = new int[numberOfNodes];
        inPrioList = new boolean[numberOfNodes];
        Vector<FibonacciHeapNode<Integer>> fibNodes = new Vector<FibonacciHeapNode<Integer>>(numberOfNodes);

        // Fill the distance array
        for(int i = 0; i < numberOfNodes; i++)
        {
            distance[i] = Double.POSITIVE_INFINITY;
        }
        distance[startingNode] = 0;

        // Initialize the Priority Queue based on Fibonacci Heap and fill it with the nodes.
        // Efficiently keep track of the nodes in the Priority Queue using a boolean array
        prioList = new FibonacciHeap<Integer>();
        for(int i = 0; i < numberOfNodes; i++)
        {
            fibNodes.add(i, new FibonacciHeapNode<Integer>(i));
            previousNode[i] = -1;
            inPrioList[i] = true;
            prioList.insert(fibNodes.elementAt(i), distance[i]);
        }

        // Find the shortest path using the Dijkstra's algorithm
        int currentNode;
        int nextNode;
        while (!prioList.isEmpty())
        {
            // Greedily select the unvisited node with the shortest distance
            currentNode = prioList.removeMin().getData();
            inPrioList[currentNode] = false;

            // Stop if the target is reached
            if (currentNode == arrivalNode) break;

            // Iterate over all the edges departing from that node
            for (DEdge edgeToNext: dEdges.elementAt(currentNode))
            {
                nextNode = edgeToNext.getNodeB();

                // Check if the pointed node is unvisited
                if(inPrioList[nextNode] == true) {

                    // Relax the edge
                    if ((distance[currentNode] + edgeToNext.getCost()) < distance[nextNode]) {
                        distance[nextNode] = distance[currentNode] + edgeToNext.getCost();
                        prioList.decreaseKey(fibNodes.elementAt(nextNode), distance[nextNode]);
                        previousNode[nextNode] = currentNode;
                    }
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