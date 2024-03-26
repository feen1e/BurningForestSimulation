import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class BurningForestSimulation {
    private static final String COLOR_RESET = "\u001B[0m";
    private static final String COLOR_GREEN = "\u001B[32m";
    private static final String COLOR_RED = "\u001B[31m";
    private String[][] map;
    private int size;
    private double forestation;
    private boolean printMap;
    public JFrame frame;
    public JTextArea text;
    public JTextArea results;
    public JTextPane forest;
    public JTextField textFieldSize = new JTextField();
    public JTextField textFieldForestation = new JTextField();
    public JPanel inputs = new JPanel();

    public BurningForestSimulation(int size, double forestation, boolean showMap) {
        this.size = size;
        this.forestation = forestation;
        this.map = new String[size][size];
        JButton button = new JButton("Run");
        if (showMap) {
            frame = new JFrame();
            text = new JTextArea("Please provide values to start the simulation.");
            text.setEditable(false);
            text.setMargin(new Insets(10, 10, 10, 10));
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            inputs.setLayout(new GridLayout(2, 3));
            inputs.add(new JLabel("   Size of the forest (int):"));
            inputs.add(new JLabel("   Forestation value (double 0.0 - 1.0):"));
            inputs.add(new JLabel(""));
            inputs.add(textFieldSize);
            inputs.add(textFieldForestation);
            panel.add(text, BorderLayout.NORTH);
            panel.add(inputs, BorderLayout.CENTER);
            results = new JTextArea("Results here");
            results.setEditable(false);
            results.setMargin(new Insets(10, 10, 10, 10));
            results.setText("""
                All trees: ???
                Surviving trees: ???
                Burnt trees: ???
                Percentage of trees burnt: ???""");
            frame.setLayout(new BorderLayout());
            frame.add(panel, BorderLayout.NORTH);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setTitle("Burning Forest Simulation");
            frame.pack();
            frame.setVisible(true);
            frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            frame.add(results, BorderLayout.SOUTH);
            inputs.add(button);
            forest = new JTextPane();
            forest.setEditable(false);
            forest.setFont(new Font("Courier New", Font.BOLD, 20));
            forest.setContentType("text/html");
            forest.setMargin(new Insets(10, 10, 10, 10));
            JScrollPane scrollPane = new JScrollPane(forest);
            frame.add(scrollPane, BorderLayout.CENTER);
        }
        this.printMap = showMap;
        button.addActionListener(e -> {
            int inputSize = Integer.parseInt(textFieldSize.getText());
            double inputForestation = Double.parseDouble(textFieldForestation.getText());
            if (inputSize <= 0 || inputForestation < 0.0 || inputForestation > 1.0) {
                text.setText("Illegal values. Please provide correct values to start the simulation.");
            } else {
                this.size = inputSize;
                this.forestation = inputForestation;
                this.map = new String[this.size][this.size];
                //System.out.println(this.size + "     " + this.forestation);
                this.makeSimulation();
            }
        });
    }

    public void mapInitialization() {
        Random random = new Random();
        for (int x = 0; x < this.size; x++) {
            for (int y = 0; y < this.size; y++) {
                if (random.nextDouble() <= this.forestation) this.map[x][y] = "T";
                else this.map[x][y] = "X";
            }
        }
    }

    public void fireInitialization() {
        for (int y = 0; y < size; y++) {
            if (map[0][y].contains("T")) map[0][y] = "B";
        }
    }

    public void makeSimulation() {
        if (printMap) {
            new Thread(() -> {
                text.setText("Running your simulation...");
                this.mapInitialization();
                waitAndPrint(3);
                this.fireInitialization();
                waitAndPrint(3);

                int nothingToBurn = 0;
                int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}};
                do {
                    waitAndPrint(0.5);
                    nothingToBurn = updateBurnProgress(nothingToBurn, directions);

                } while (nothingToBurn < this.size * this.size);
                int[] trees = getTreeRatio();

                SwingUtilities.invokeLater(() -> {
                    printMap(this.map);
                    results.setText(null);
                    results.append("All trees: " + trees[0]);
                    results.append("\nSurviving trees: " + trees[1]);
                    results.append("\nBurnt trees: " + trees[2]);
                    results.append("\nPercentage of trees burnt: " + String.format("%.3f", (double) trees[2] / trees[0] * 100) + "%");
                    text.setText("Simulation complete.");
                    System.out.printf("""
                        
                        -----------------------------------------------------------------------------------
                        All trees: %d.
                        Surviving trees: %d.
                        Burnt trees: %d.
                        Percentage of trees burnt: %.2f%%.
                        -----------------------------------------------------------------------------------
                        
                        """, trees[0], trees[1], trees[2], (double) trees[2] / trees[0] * 100);
                });
            }).start();
        } else {
            this.mapInitialization();
            this.fireInitialization();
            int nothingToBurn = 0;
            int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}};
            do {
                nothingToBurn = updateBurnProgress(nothingToBurn, directions);
            } while (nothingToBurn < this.size * this.size);
        }
    }

    private int updateBurnProgress(int nothingToBurn, int[][] directions) {
        String[][] copyOfMap = new String[this.map.length][];
        for (int i = 0; i < this.map.length; i++) {
            copyOfMap[i] = Arrays.copyOf(this.map[i], this.map[i].length);
        }
        for (int x = 0; x < this.size; x++) {
            for (int y = 0; y < this.size; y++) {
                nothingToBurn += 1;
                if (copyOfMap[x][y].contains("B")) {
                    for (int[] direction : directions) {
                        int newX = x + direction[0];
                        int newY = y + direction[1];
                        if (newX >= 0 && newX < this.size && newY >= 0 && newY < this.size && copyOfMap[newX][newY].contains("T")) {
                            this.map[newX][newY] = "B";
                            nothingToBurn = 0;
                        }
                    }
                }
            }
        }
        return nothingToBurn;
    }

    public void printMap(String[][] map) {
        StringBuilder html = new StringBuilder("<html color=\"black\"><font size=\"5\"><tt><b>");
        for (int x = 0; x < this.size; x++) {
            for (int y = 0; y < this.size; y++) {
                if (map[x][y].contains("T")) {
                    html.append("<span color=\"green\">&nbsp;T&nbsp;</span>");
                    System.out.print(" " + COLOR_GREEN + map[x][y] + COLOR_RESET + " ");
                } else if (map[x][y].contains("B")) {
                    html.append("<span color=\"red\">&nbsp;B&nbsp;</span>");
                    System.out.print(" " + COLOR_RED + map[x][y] + COLOR_RESET + " ");
                } else {
                    html.append("&nbsp;X&nbsp;");
                    System.out.print(" " + map[x][y] + " ");
                }
            }
            html.append("<br>");
            System.out.println();
        }
        html.append("</b></tt></font></html>");
        forest.setText(String.valueOf(html));
    }

    public void waitAndPrint(double seconds) {
        if (this.printMap) {
            try {
                this.printMap(this.map);
                System.out.println("\n-----------------------------------------------------------------------------------\n");
                Thread.sleep((long) (seconds * 1000));
            } catch (InterruptedException e) {
                System.out.println("\n\n\n\n\n");
            }
        }
    }

    public int[] getTreeRatio() {
        int[] trees = {0, 0, 0};
        // [All, Alive, Burnt]
        for (int x = 0; x < this.size; x++) {
            for (int y = 0; y < this.size; y++) {
                if (this.map[x][y].contains("B")) trees[2] += 1;
                else if (this.map[x][y].contains("T")) trees[1] += 1;
            }

        }
        trees[0] = trees[1] + trees[2];
        return trees;
    }

    public static void simulationResults() {
        try {
            FileWriter writer = new FileWriter("results.txt");
            writer.write("Forestation Value; Iteration; All Trees; Alive Trees; Burnt Trees; % of Burnt;\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error while opening the file: " + e.getMessage());
        }
        try (FileWriter writer = new FileWriter("results.txt", true)) {
            for (int forestationValue = 0; forestationValue <= 20; forestationValue++) {
                double forestation = Math.round((double) forestationValue / 20.0 / 0.05) * 0.05;
                for (int k = 0; k < 10; k++) {
                    BurningForestSimulation simulation = new BurningForestSimulation(100, forestation, false);
                    simulation.printMap = false;
                    simulation.makeSimulation();
                    writer.write(forestation + ";" + (k + 1) + ";");
                    int[] trees = simulation.getTreeRatio();
                    writer.write(trees[0] + ";" + trees[1] + ";" + trees[2] + ";" + (double) trees[2] / trees[0] * 100 + ";\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error while writing to file: " + e.getMessage());
        }
        System.out.println("\nSimulation complete. Results are stored in results.txt");
    }

    public static void main(String[] args) {
        System.out.println("Autor: Dominik Kaczmarek 281007");
        simulationResults();
        System.out.println("\n-----------------------------------------------------------------------------------\n");
        BurningForestSimulation burningForestSimulation = new  BurningForestSimulation(0, 0, true);
    }
}
