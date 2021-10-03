import java.util.Arrays;

public class Utilities {
    public static void setup() {
        Game.rows = (Game.height - Game.border[0] - Game.border[2]) / Game.boxSize;
        Game.cols = (Game.width - Game.border[1] - Game.border[3]) / Game.boxSize;

        Game.grid = new boolean[Game.rows + 2][Game.cols + 2];
        Game.oldGrid = new boolean[Game.rows + 2][Game.cols + 2];

        clearBoard();
        randBoard();

        Game.paused = false;
    }

    public static void handleResize(Game game) {
        Game.width = game.getWidth();
        Game.height = game.getHeight();
        Game.rows = (Game.height - Game.border[0] - Game.border[2]) / Game.boxSize;
        Game.cols = (Game.width - Game.border[1] - Game.border [3]) / Game.boxSize;

        Game.grid = Arrays.copyOf(Game.grid, Game.rows + 2);
        Game.oldGrid = Arrays.copyOf(Game.oldGrid, Game.rows + 2);
        for (int row = 0; row < Game.rows + 2; row++) {
            if (Game.grid[row] == null) {
                Game.grid[row] = new boolean[Game.cols + 2];
                Game.oldGrid[row] = new boolean[Game.cols + 2];
            } else {
                Game.grid[row] = Arrays.copyOf(Game.grid[row], Game.cols + 2);
                Game.oldGrid[row] = Arrays.copyOf(Game.oldGrid[row], Game.cols + 2);
            }
        }
    }


    public static void clearBoard() {
        for (int row = 0; row < Game.rows + 2; row++) {
            for (int col = 0; col < Game.cols + 2; col++) {
                Game.grid[row][col] = false;
                Game.oldGrid[row][col] = false;
            }
        }
    }


    public static void randBoard() {
        for (int row = 1; row <= Game.rows; row++) {
            for (int col = 1; col <= Game.cols; col++) {
                if (Math.random() >= Game.randThreshold) {
                    Game.grid[row][col] = true;
                    Game.oldGrid[row][col] = true;
                }
            }
        }
    }


    public static int getNumOfAliveNeighbours(int row, int col) {
        int aliveNeighbours = 0;

        if (Game.oldGrid[row - 1][col - 1]) aliveNeighbours++; // Top left
        if (Game.oldGrid[row - 1][col]) aliveNeighbours++; // Top middle
        if (Game.oldGrid[row - 1][col + 1]) aliveNeighbours++; // Top right
        if (Game.oldGrid[row][col - 1]) aliveNeighbours++; // Left
        if (Game.oldGrid[row][col + 1]) aliveNeighbours++; // Right
        if (Game.oldGrid[row + 1][col - 1]) aliveNeighbours++; // Bottom left
        if (Game.oldGrid[row + 1][col]) aliveNeighbours++; // Bottom middle
        if (Game.oldGrid[row + 1][col + 1]) aliveNeighbours++; // Bottom right

        return aliveNeighbours;
    }
}
