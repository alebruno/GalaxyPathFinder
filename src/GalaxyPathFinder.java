import org.json.simple.parser.ParseException;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;
import java.util.Vector;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Main class of the application GalaxyPathFinder
 * @author Alessandro Bruno
 */
public class GalaxyPathFinder extends JPanel implements ActionListener {
    static private final String newline = "\n";
    JButton openButton, saveButton, runButton;
    JLabel logoImage;
    JTextArea log;
    JFileChooser fOpenDialog, fSaveDialog;
    JTextField startEntry, arrivalEntry;
    JLabel jLabelStart, jLabelArrival;
    JRadioButton fordButton, dijkButton;
    String filePath = "generatedGraph.json";

    /**
     * Constructor class. It creates the GUI of the application and writes a welcome message
     */
    public GalaxyPathFinder() {
        super(new BorderLayout());

        // Create the text fields
        jLabelStart = new JLabel();
        jLabelArrival = new JLabel();
        startEntry = new JTextField();
        arrivalEntry = new JTextField();

        // Set proprieties of text fields
        jLabelStart.setText("Startknoten:");
        jLabelStart.setPreferredSize(new Dimension(90,26));
        jLabelArrival.setText("Endknoten:");
        jLabelArrival.setPreferredSize(new Dimension(90,26));
        startEntry.setText("Erde");
        startEntry.setPreferredSize(new Dimension(100,26));
        arrivalEntry.setText("b3-r7-r4nd7");
        arrivalEntry.setPreferredSize(new Dimension(100, 26));

        // Create and set proprieties of radio buttons to select the algorithm
        fordButton = new JRadioButton("Bellman-Ford-Algorithmus");
        fordButton.setActionCommand("Ford");

        dijkButton = new JRadioButton("Dijkstra-Algorithmus mit Fibonacci-Heap");
        dijkButton.setActionCommand("Dijkstra");
        dijkButton.setSelected(true);

        // Add radio buttons to a button group
        ButtonGroup group = new ButtonGroup();
        group.add(fordButton);
        group.add(dijkButton);

        // Register a listener for the radio buttons.
        fordButton.addActionListener(this);
        dijkButton.addActionListener(this);

        // Create the log
        log = new JTextArea(5,20);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(log);

        // Create a file chooser to select the input file
        fOpenDialog = new JFileChooser();
        FileNameExtensionFilter  JSONExtFilter = new FileNameExtensionFilter("*.json", "json", "JSON","Json");
        fOpenDialog.addChoosableFileFilter(JSONExtFilter);
        fOpenDialog.setFileFilter(JSONExtFilter);

        // Create a file chooser to select the output file. Only .txt files are allowed
        fSaveDialog = new JFileChooser();
        FileNameExtensionFilter  TXTExtFilter = new FileNameExtensionFilter("*.txt", "txt", "JSON","Json");
        fSaveDialog.addChoosableFileFilter(TXTExtFilter);
        fSaveDialog.setAcceptAllFileFilterUsed(false);

        // Set proprieties of buttons and register an action listener for them
        openButton = new JButton(" Datei auswählen", createImageIcon("images/Open16.gif"));
        openButton.addActionListener(this);
        runButton = new JButton("Kürzesten Pfad finden");
        runButton.addActionListener(this);
        saveButton = new JButton("Ausgabe speichern", createImageIcon("images/Save16.gif"));
        saveButton.addActionListener(this);
        logoImage = new JLabel();
        logoImage.setIcon(createImageIcon("images/logo.png"));

        // Set up a panel for logo image
        JPanel logoPanel = new JPanel();
        logoPanel.add(logoImage);

        // Set up a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton);
        buttonPanel.add(runButton);
        buttonPanel.add(saveButton);

        // Set up a panel for the text fields
        JPanel startPanel = new JPanel();
        startPanel.add(jLabelStart);
        startPanel.add(startEntry);
        JPanel arrivalPanel = new JPanel();
        arrivalPanel.add(jLabelArrival);
        arrivalPanel.add(arrivalEntry);
        JPanel startAnsArrivalPanel = new JPanel();
        startAnsArrivalPanel.add(startPanel);
        startAnsArrivalPanel.add(arrivalPanel);

        // Add the radio buttons to a single panel
        JPanel selectorPanel = new JPanel();
        selectorPanel.add(fordButton);
        selectorPanel.add(dijkButton);

        // Put together the input panel
        JPanel inputPanel = new JPanel( new BorderLayout());
        inputPanel.add(startAnsArrivalPanel, BorderLayout.NORTH);
        inputPanel.add(selectorPanel, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add the panels to the layout
        add(logoPanel, BorderLayout.PAGE_START);
        add(inputPanel, BorderLayout.SOUTH);
        add(logScrollPane, BorderLayout.CENTER);

        // Write a welcome message
        log.append("GalaxyPathFinder" + newline);
        log.append("Find deinen Weg in die Galaxie!" + newline);
        log.append("Entwickelt von Alessandro Bruno" + newline);
        log.setCaretPosition(log.getDocument().getLength());
    }

    /**
     * Process the action event
     * @param e the ActionEvent
     */
    public void actionPerformed(ActionEvent e) {

        // Handle open button action.
        if (e.getSource() == openButton) {
            int returnVal = fOpenDialog.showOpenDialog(GalaxyPathFinder.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fOpenDialog.getSelectedFile();

                filePath = file.getPath();

            }
            log.setCaretPosition(log.getDocument().getLength());

            //Handle save button action.
        } else if (e.getSource() == saveButton) {
            int returnVal = fSaveDialog.showSaveDialog(GalaxyPathFinder.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File adaptedFilename;

                // If the chose file name is not ending with .txt, append this extension to it
                if (fSaveDialog.getSelectedFile().toString().endsWith(".txt")) {
                    adaptedFilename = fSaveDialog.getSelectedFile();
                } else {
                    adaptedFilename = new File(fSaveDialog.getSelectedFile().toString() + ".txt");
                }

                // Save file
                try (PrintWriter out = new PrintWriter(adaptedFilename)) {
                    out.println(log.getText());
                } catch (Exception savingException)
                {
                    // Handle exception
                    log.append("Fehler beim Speichern" + newline);
                }
            }
            log.setCaretPosition(log.getDocument().getLength());
        // Handle run button action
        } else if (e.getSource() == runButton)
            try {
                log.setText("Die Datei wird gelesen..." + newline);
                GalacticParser galaxy = new GalacticParser(filePath, startEntry.getText(), arrivalEntry.getText());
                log.setText("");

                // The Bellman-Ford algorithm is selected
                if (fordButton.isSelected()) {
                    log.append("Berechnung des kürzesten Wegs mit dem Bellman-Ford-Algorithmus." + newline);
                    log.append("Datei " + filePath + newline);
                    BellmanFord bfPathFinder = new BellmanFord(galaxy.getStartingNode(), galaxy.getArrivalNode(), galaxy.getAdjacencyList());
                    if (bfPathFinder.getLength() != Double.POSITIVE_INFINITY)
                    {
                        log.append("Kürzester Pfad zwischen " + startEntry.getText() + " und " + arrivalEntry.getText() + ":" + newline + newline);
                        Stack<Integer> shortestPathStack = bfPathFinder.getShortestPath();
                        while (!shortestPathStack.empty()) {
                            log.append(galaxy.getNodeLabels()[shortestPathStack.pop()] + newline);
                        }
                        log.append(newline + "Die gesamte Entfernung ist " + bfPathFinder.getLength() + newline);
                        log.setCaretPosition(log.getDocument().getLength());
                    } else
                    {
                        log.append("Kein Pfad zwischen " + startEntry.getText() + " und " + arrivalEntry.getText() + "." + newline);
                    }

                    // The Dijkstra Algorithm is selected
                } else {
                    log.append("Berechnung des kürzesten Wegs mit dem Dijkstra-Algorithmus." + newline);
                    log.append("Datei " + filePath + newline);
                    Dijkstra dijkstraPathFinder = new Dijkstra(galaxy.getStartingNode(), galaxy.getArrivalNode(), galaxy.getAdjacencyList());
                    if (dijkstraPathFinder.getLength() != Double.POSITIVE_INFINITY)
                    {
                        log.append("Kürzester Pfad zwischen " + startEntry.getText() + " und " + arrivalEntry.getText() + ":" + newline + newline);
                        Stack<Integer> shortestPathStack = dijkstraPathFinder.getShortestPath();
                        while (!shortestPathStack.empty()) {
                            log.append(galaxy.getNodeLabels()[shortestPathStack.pop()] + newline);
                        }
                        log.append(newline + "Die gesamte Entfernung ist " + dijkstraPathFinder.getLength() + newline);
                        log.setCaretPosition(log.getDocument().getLength());
                    } else
                    {
                        log.append("Kein Pfad zwischen " + startEntry.getText() + " und " + arrivalEntry.getText() + "." + newline);
                    }
                }
            } catch (IOException anException) {
                anException.printStackTrace();
                log.append("Fehler beim Öffnen der Datei." + newline);
            } catch (ParseException anException) {
                anException.printStackTrace();
                log.append("Fehler beim Parsen der Datei." + newline);
            } catch (IllegalArgumentException anException)
            {
                anException.printStackTrace();
                log.append("Eingabefehler." + newline);
                log.append(anException.getMessage());
            }
    }

    /**
     * Factory class. It returns a PathFinder object that solves the problem and can be subsequently queried for results.
     * @param algorithm the chosen algorithm
     * @param start the starting node
     * @param arrival the arrival node
     * @param dEdges adjacency list representation of the graph
     * @return the PathFinder object
     */
    private PathFinder CreatePathFinder(String algorithm, int start, int arrival, Vector<Vector<DEdge>> dEdges)
    {
        if (algorithm.equals("Dijkstra")) // Good choice ;)
        {
            return new Dijkstra(start, arrival, dEdges);
        } else {
            return new BellmanFord(start, arrival, dEdges);
        }
    }

    /** Returns an ImageIcon, or null if the path was invalid.
     * @param path file path
     * @return the imageIcon
     */
    private static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = GalaxyPathFinder.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Create the GUI and show it.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("GalaxyPathFinder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        GalaxyPathFinder gpf = new GalaxyPathFinder();
        frame.add(gpf);

        //Display the window.
        frame.pack();

        // Set some proprieties
        frame.setSize(600, 600);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    /**
     * Starts the application GalaxyPathFinder
     * @param args not used
     */
    public static void main(String[] args) {
        // Creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                try {
                    // Set System Look and Feel
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
                catch (Exception e) {
                    // No action, use default
                }
                createAndShowGUI();
            }
        });
    }
}
