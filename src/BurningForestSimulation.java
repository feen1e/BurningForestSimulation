import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class BurningForestSimulation {
    public static final String COLOR_RESET = "\u001B[0m";
    public static final String COLOR_GREEN = "\u001B[32m";
    public static final String COLOR_RED = "\u001B[31m";
    private final String[][] map;
    private final int size;
    private final double forestation;
    private boolean printMap = true;

    public BurningForestSimulation(int size, double forestation) {
        this.size = size;
        this.forestation = forestation;
        this.map = new String[size][size];
    }

    public void mapInitialization() {
        Random random = new Random();
        for (int x = 0; x < this.size; x++) {
            for (int y = 0; y < this.size; y++) {
                if (random.nextDouble() <= this.forestation) this.map[x][y] = COLOR_GREEN + "T" + COLOR_RESET;
                else this.map[x][y] = "X";
            }
        }
    }

    public void fireInitialization() {
        for (int y = 0; y < size; y++) {
            if (map[0][y].contains("T")) map[0][y] = COLOR_RED + "B" + COLOR_RESET;
        }
    }

    public void makeSimulation() {
        this.mapInitialization();
        waitAndPrint(3);
        this.fireInitialization();
        waitAndPrint(3);

        int nothingToBurn = 0;
        int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}};
        do {
            waitAndPrint(0.5);
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
                                this.map[newX][newY] = COLOR_RED + "B" + COLOR_RESET;
                                nothingToBurn = 0;
                            }
                        }
                    }
                }
            }

        } while (nothingToBurn < this.size * this.size);

        int[] trees = getTreeRatio();
        System.out.printf("""


                All trees: %d.
                Surviving trees: %d.
                burnt trees: %d.
                Percent of trees burnt: %.2f%%""", trees[0], trees[1], trees[2], (double) trees[2] / trees[0] * 100);
    }

    public void printMap(String[][] map) {
        for (int x = 0; x < this.size; x++) {
            for (int y = 0; y < this.size; y++) {
                System.out.print(" " + map[x][y] + " ");
            }
            System.out.println();
        }
    }

    public void waitAndPrint(double seconds) {
        if (printMap) {
            try {
                this.printMap(map);
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

    private static void simulationResults() {
        try {
            FileWriter writer = new FileWriter("results.txt");
            writer.write("Forestation Value; Iteration; All Trees; Alive Trees; Burnt Trees; % of Burnt;\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error while opening the file: " + e.getMessage());
        }
        try (FileWriter writer = new FileWriter("results.txt", true)) {
            for (int forestationValue = 0; forestationValue <= 20; forestationValue++) {
                for (int k = 0; k < 10; k++) {
                    BurningForestSimulation simulation = new BurningForestSimulation(100, Math.round((double) forestationValue / 20 / 0.05) * 0.05);
                    simulation.printMap = false;
                    simulation.makeSimulation();
                    writer.write(Math.round((double) forestationValue / 20 / 0.05) * 0.05 + ";" + (k + 1) + ";");
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
        simulationResults();
        System.out.println("\n-----------------------------------------------------------------------------------\n");
        BurningForestSimulation burningForestSimulation = new BurningForestSimulation(20, 0.32);
        burningForestSimulation.makeSimulation();
    }
}
