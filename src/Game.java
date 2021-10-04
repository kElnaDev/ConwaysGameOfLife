import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Game extends JPanel implements Runnable {
    // Core
    static int fps;
    static boolean running;
    static boolean paused = true;
    static boolean calcCurrentFrame = false; // used for temporarily un-pausing the game
    static double amountOfTicks = 60.0;
    static boolean renderWhilePaused = false;
    static boolean firstFrame = true;
        // to account for mouse input on first frame causing desync due to concurrent threads.
        /*
            Mouse input forces the app to draw the current grid. The issue comes when mouse input is interpreted while
            setup has not yet finished creating the grid. This is only a problem on the first frame.
        */

    // Sizing
    public static int rows;
    public static int cols;
    public static int boxSize = 10;
    public static int width;
    public static int height;
    public static int maxBoardWidth;
    public static int maxBoardHeight;
    public static int trueBoxSize = boxSize;
    public static int[] border = {50, 10, 10, 10};
        // follows CSS border (top, right, bottom, left) because I'm used to it, I guess
    public static boolean resized = false;
    public static int resizedWidth, resizedHeight;
    public static double zoom = 1, oldZoom = zoom;

    // Generations
    public static int[][] grid, oldGrid;
    public static ArrayList<int[][]> generations = new ArrayList<>();
    public static int currentGen = 0;

    // Randomness settings
    static double randThreshold = 0.5;

    // Fonts
    public static Font fontL, fontM, fontS; // Small

    public Game(int width, int height) {
        Game.width = width;
        Game.height = height;
        Game.maxBoardWidth = Game.width - Game.border[1] - Game.border[3];
        Game.maxBoardHeight = Game.height - Game.border[0] - Game.border[2];

        Dimension size = new Dimension(width, height);
        this.setMinimumSize(size);
        this.setPreferredSize(size);
        this.setMaximumSize(size);
        this.setFocusable(true);
        requestFocus();


        // Load Font
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("fonts/NotoSans.ttf")));
        } catch (IOException |FontFormatException e) { e.printStackTrace(); }

        fontL = new Font("Noto Sans Display", Font.BOLD, 30); // Large
        fontM = new Font("Noto Sans Display", Font.BOLD, 23); // Medium
        fontS = new Font("Noto Sans Display", Font.BOLD, 15); // Small
    }


    @Override
    public void run() {
        /* SETUP */
        Utilities.setup();

        /* LOOP */
        long lastTime = System.nanoTime();
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        int fpsUpdateCounter = 0; // counts how many times the fps updates
        while (running) {
            double ns = 1000000000 / amountOfTicks;
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
//                tick();
                repaint();
                frames++;
                delta--;
            }

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("[" + fpsUpdateCounter + "] FPS: " + frames);
                fps = frames;
                frames = 0;
                fpsUpdateCounter++;
            }
        }

        CGoL.stop();
    }

    @Override
    public void paint(Graphics g) {
        // zoom & resize handling - LEAVE AT TOP AS IT CHANGES WIDTH & HEIGHT
        if (zoom != oldZoom) {
            System.out.println("t");
            Utilities.handleResize();
            oldZoom = zoom;
        }

        if (resized) {
            Utilities.handleResize(resizedWidth, resizedHeight);
            resized = false;
        }


        // Background
        Graphics2D graphicsObj = (Graphics2D) g;
        graphicsObj.setPaint(Utilities.getColour("evening"));
        graphicsObj.fillRect(0, 0, width, height);


        // User input
        Key.interpret();
        Mouse.interpret();


        // Sync errors
        if (firstFrame) {
            firstFrame = false;
            if (Mouse.mouseHeld || renderWhilePaused)
                System.out.println("First frame discrepancy handled.");
            return;
        }


        // Update & draw grid
        if (!paused && !Mouse.mouseHeld) calcCurrentFrame = true;

        trueBoxSize = (int) (boxSize * zoom);
        if (calcCurrentFrame) generations.add(new int[rows + 2][cols + 2]);
        for (int row = 0; row < rows + 2; row++) {
            for (int col = 0; col < cols + 2; col++) {
                if (row == 0 || col == 0 || row == rows + 1 || col == cols + 1) {
                    if (calcCurrentFrame) {
                        generations.get(currentGen)[row][col] = 0;
                    }
                    continue;
                }

                if (calcCurrentFrame) {
                    int aliveNeighbours = Utilities.getNumOfAliveNeighbours(row, col);
                    if (aliveNeighbours == 3) {
                        grid[row][col] = 255;
                    } else if (grid[row][col] < 255 && grid[row][col] != 0) {
                        grid[row][col] -= (grid[row][col] - 2 > 0) ? 2 : grid[row][col];
                    } else if ((aliveNeighbours < 2 || aliveNeighbours > 3) && grid[row][col] == 255) {
                        grid[row][col] = 150;
                    }

                    generations.get(currentGen)[row][col] = grid[row][col];

                    if (generations.get(currentGen)[row][col] > 0)
                        graphicsObj.setPaint(Utilities.getColour("white", generations.get(currentGen)[row][col]));
                    else graphicsObj.setPaint(Utilities.getColour("midnight"));
                } else if (Mouse.mouseHeld || renderWhilePaused) {
                    if (grid[row][col] == 255) graphicsObj.setPaint(Utilities.getColour("white", grid[row][col]));
                    else graphicsObj.setPaint(Utilities.getColour("white", grid[row][col] + 10));
                } else {
                    try {
                        if (generations.get(currentGen - 1)[row][col] == 255)
                            graphicsObj.setPaint(Utilities.getColour("white", generations.get(currentGen - 1)[row][col]));
                        else
                            graphicsObj.setPaint(Utilities.getColour("white", generations.get(currentGen - 1)[row][col] + 10));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        graphicsObj.setPaint(Utilities.getColour("white", 10));
                    }
                }

                graphicsObj.fillRect(
                        (int) ((col - 1) * boxSize * zoom) + border[3],
                        (int) ((row - 1) * boxSize * zoom) + border[0],
                        trueBoxSize, trueBoxSize
                );
            }
        }


        // Update old grid
        if (calcCurrentFrame) {
            for (int row = 1; row <= rows; row++) {
                if (cols >= 0) System.arraycopy(grid[row], 1, oldGrid[row], 1, cols);
            }
            currentGen++;
        }


        // Header text
        graphicsObj.setPaint(Color.WHITE);
        graphicsObj.setFont(fontL);
        graphicsObj.drawString("Generation: " + currentGen, border[3], 35);

        graphicsObj.setFont(fontM);
        graphicsObj.drawString("FPS: " + fps + "  TPS: " + (int) amountOfTicks, width - border[1] - 170, 33);

        graphicsObj.drawString("Box Size: " + boxSize, width/2 - 60, 33);

        if (paused) {
            // size info
            int padding = 5;
            int margin = 9;
            int textWidth = 50;
            int textHeight = 11;
            int x = width - border[1] - textWidth - margin + 1;
            int y = border[0] + margin;

            // background
            graphicsObj.setPaint(Utilities.getColour("midnight"));
            graphicsObj.fillRoundRect(
                    x - padding, y - padding, textWidth + padding*2, textHeight + padding*2,
                    10, 10
            );

            // text
            graphicsObj.setPaint(Color.WHITE);
            graphicsObj.setFont(fontS);
            graphicsObj.drawString("Paused", x, y + textHeight);
        }


        calcCurrentFrame = false;
    }
}
