import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Utilities {
    public static void setup() {
        Game.rows = (Game.height - Game.border[0] - Game.border[2]) / Game.boxSize;
        Game.cols = (Game.width - Game.border[1] - Game.border[3]) / Game.boxSize;

        Game.grid = new int[Game.rows + 2][Game.cols + 2];
        Game.oldGrid = new int[Game.rows + 2][Game.cols + 2];

        clearBoard();
        randBoard();

        Game.paused = false;
    }

    public static Color getColour(String name) {
        return getColour(name, 255);
    }

    public static Color getColour(String name, int opacity) {
        switch (name) {
            case "midnight" -> {
                return new Color(19, 19, 19, opacity);
            }
            case "evening" -> {
                return new Color(31, 31, 31, opacity);
            }
            case "dark" -> {
                return new Color(45, 45, 45, opacity);
            }
            case "white" -> {
                return new Color(255, 255, 255, opacity);
            }
            default -> {
                return new Color(0, 0, 0, 255);
            }
        }
    }

    public static void handleResize() {
        handleResize(Game.width, Game.height);
    }

    public static void handleResize(int width, int height) {
        Game.width = width;
        Game.height = height;
        Game.maxBoardWidth = Game.width - Game.border[1] - Game.border[3];
        Game.maxBoardHeight = Game.height - Game.border[0] - Game.border[2];
        Game.rows = Game.maxBoardHeight / Game.trueBoxSize;
        Game.cols = Game.maxBoardWidth / Game.trueBoxSize;

        if (Game.rows > Game.grid.length) {
            Game.grid = Arrays.copyOf(Game.grid, Game.rows + 2);
            Game.oldGrid = Arrays.copyOf(Game.oldGrid, Game.rows + 2);
            for (int row = 0; row < Game.rows + 2; row++) {
                if (Game.grid[row] == null) {
                    Game.grid[row] = new int[Game.cols + 2];
                    Game.oldGrid[row] = new int[Game.cols + 2];
                }
                if (Game.cols > Game.grid[row].length) {
                    Game.grid[row] = Arrays.copyOf(Game.grid[row], Game.cols + 2);
                    Game.oldGrid[row] = Arrays.copyOf(Game.oldGrid[row], Game.cols + 2);
                }
            }
        }
    }

    public static void clearBoard() {
        for (int row = 0; row < Game.rows + 2; row++) {
            for (int col = 0; col < Game.cols + 2; col++) {
                Game.grid[row][col] = 0;
                Game.oldGrid[row][col] = 0;
            }
        }
        Game.currentGen = 0;
        Game.generations = new ArrayList<>();
    }


    public static void randBoard() {
        for (int row = 1; row <= Game.rows; row++) {
            for (int col = 1; col <= Game.cols; col++) {
                if (Math.random() >= Game.randThreshold) {
                    Game.grid[row][col] = 255;
                    Game.oldGrid[row][col] = 255;
                }
            }
        }
    }


    public static int getNumOfAliveNeighbours(int row, int col) {
        int aliveNeighbours = 0;

        if (Game.oldGrid[row - 1][col - 1] == 255) aliveNeighbours++; // Top left
        if (Game.oldGrid[row - 1][col] == 255) aliveNeighbours++; // Top middle
        if (Game.oldGrid[row - 1][col + 1] == 255) aliveNeighbours++; // Top right
        if (Game.oldGrid[row][col - 1] == 255) aliveNeighbours++; // Left
        if (Game.oldGrid[row][col + 1] == 255) aliveNeighbours++; // Right
        if (Game.oldGrid[row + 1][col - 1] == 255) aliveNeighbours++; // Bottom left
        if (Game.oldGrid[row + 1][col] == 255) aliveNeighbours++; // Bottom middle
        if (Game.oldGrid[row + 1][col + 1] == 255) aliveNeighbours++; // Bottom right

        return aliveNeighbours;
    }
}
