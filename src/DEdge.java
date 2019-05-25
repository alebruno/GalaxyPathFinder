/**
 * Simple class to store the edge of a graph
 */
public class DEdge {
    private int nodeA;
    private int nodeB;
    private double cost;

    /**
     * Class constructor
     * @param a the starting node of the edge
     * @param b the arrival node of the edge
     * @param w the weight of the edge
     */
    public DEdge(int a, int b, double w){
        nodeA = a;
        nodeB = b;
        cost = w;
    }

    /**
     * @return the starting node of the edge
     */
    public int getNodeA() {
        return nodeA;
    }

    /**
     * @return the arrival node of the edge
     */
    public int getNodeB() {
        return nodeB;
    }

    /**
     * @return the cost of the edge
     */
    public double getCost() {
        return cost;
    }
}