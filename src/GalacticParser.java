import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Vector;

/**
 * Class to parse a JSON file describing the graph of a galaxy.
 * @author Alessandro Bruno
 */
public class GalacticParser {

    private int nNodes;
    private int startingNode = -1;
    private int arrivalNode = -1;
    private String[] nodeLabels = null;
    private Vector<Vector<DEdge>> adjacencyList = null;


    /**
     * Class constructor. Parse the JSON file and store an adjacency list representation of the graph.
     * @param fileName the name of the input file (if in the current directory) or the path to the input file
     * @param startingNodeString the label of the starting node (name of the starting planet)
     * @param arrivalNodeString the label of the arrival node (name of the arrival planet)
     * @throws IOException if the file could not be read
     * @throws ParseException if the file is not in the expected format
     * @throws IllegalArgumentException if the graph contains non-positive edges, if the starting label or the arrival label has not been found or is not unique
     */
    public GalacticParser(String fileName, String startingNodeString, String arrivalNodeString) throws IOException, ParseException, IllegalArgumentException
    {
        JSONParser parser = new JSONParser();
        Reader reader = new FileReader(fileName);
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        JSONArray nodesJsonArray = (JSONArray) jsonObject.get("nodes");
        JSONArray edgesJsonArray = (JSONArray) jsonObject.get("edges");
        nNodes = nodesJsonArray.size();
        nodeLabels = new String[nNodes];

        // Parse the JSON file
        int i = 0;
        String nameBuffer;
        for(Object nodeJsonValue: nodesJsonArray)
        {
            nameBuffer = (String) ((JSONObject) nodeJsonValue).get("label");
            // System.out.println(nameBuffer);
            if (nameBuffer.equals(arrivalNodeString))
            {
                if (arrivalNode == -1) arrivalNode = i; else throw new IllegalArgumentException("Endknoten mehrmals gefunden.");
            }

            if (nameBuffer.equals(startingNodeString))
            {
                if (startingNode == -1) startingNode = i; else throw new IllegalArgumentException("Startknoten mehrmals gefunden.");
            }
            nodeLabels[i] = nameBuffer;
            i++;
        }
        if (arrivalNode == -1) throw new IllegalArgumentException("Endknoten nicht gefunden.");
        if (startingNode == -1) throw new IllegalArgumentException("Startknoten nicht gefunden.");

        // Initialize the adjacency list
        adjacencyList = new Vector<Vector<DEdge>>(nNodes);
        for (int j = 0; j < nNodes; j++)
        {
            adjacencyList.add(j, new Vector<DEdge>());
        }

        // Fill the adjacency list with the edges
        int nodeA;
        int nodeB;
        double costBuffer;
        i = 0;
        for(Object edgeJsonValue: edgesJsonArray)
        {
            nodeA = (int) (long) ((JSONObject) edgeJsonValue).get("source");
            nodeB = (int) (long) ((JSONObject) edgeJsonValue).get("target");
            costBuffer = (double) ((JSONObject) edgeJsonValue).get("cost");
            if (costBuffer <= 0) throw new IllegalArgumentException("Nichtpositive Kantengewichte sind unzulässig.");
            adjacencyList.elementAt(nodeA).add(new DEdge(nodeA, nodeB, costBuffer));
            adjacencyList.elementAt(nodeB).add(new DEdge(nodeB, nodeA, costBuffer));
            i++;
        }
    }

    /**
     * @return the index of the arrival node
     */
    public int getArrivalNode() {
        return arrivalNode;
    }

    /**
     * @return the index of the starting node
     */
    public int getStartingNode() {
        return startingNode;
    }

    /**
     * @return the adjacency list
     */
    public Vector<Vector<DEdge>> getAdjacencyList() {
        return adjacencyList;
    }

    /**
     * @return the array of node labels
     */
    public String[] getNodeLabels() {
        return nodeLabels;
    }
}
