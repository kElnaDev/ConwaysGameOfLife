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
    static int rows, cols, boxSize = 10;
    static int width, height;
    static int[] border = {50, 10, 10, 10};
        // follows CSS border (top, right, bottom, left) because I'm used to it, I guess
    static boolean resized = false;

    // Generations
    static boolean[][] grid, oldGrid;
    static ArrayList<boolean[][]> generations = new ArrayList<>();
    static int currentGen = 0;

    // Randomness settings
    static double randThreshold = 0.5;

    // Colours
    public static Color midnight = new Color(19, 19, 19),
            evening = new Color(31, 31, 31),
            dark = new Color(45, 45, 45);

    // Fonts
    public static Font fontL, fontM, fontS; // Small

    public Game(int width, int height) {
        Game.width = width;
        Game.height = height;

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

        if (resized) {
            Utilities.handleResize();
            resized = false;
        }

        Graphics2D graphicsObj = (Graphics2D) g;
        graphicsObj.setPaint(evening);
        graphicsObj.fillRect(0, 0, width, height);

        Key.interpret();
        Mouse.interpret();

        if (firstFrame) {
            firstFrame = false;
            if (Mouse.mouseHeld || renderWhilePaused)
                System.out.println("First frame discrepancy handled.");
            return;
        }

        // Update grid
        if ((!paused && !Mouse.mouseHeld) || calcCurrentFrame) generations.add(new boolean[rows + 2][cols + 2]);
        for (int row = 0; row < rows + 2; row++) {
            for (int col = 0; col < cols + 2; col++) {
                if (row == 0 || col == 0 || row == rows + 1 || col == cols + 1) {
                    if ((!paused && !Mouse.mouseHeld) || calcCurrentFrame) {
                        generations.get(currentGen)[row][col] = false;
                    }
                    continue;
                }

                if ((!paused && !Mouse.mouseHeld) || calcCurrentFrame) {
                    int aliveNeighbours = Utilities.getNumOfAliveNeighbours(row, col);
                    if (aliveNeighbours < 2 || aliveNeighbours > 3) {
                        grid[row][col] = false;
                    } else if (aliveNeighbours == 3) {
                        grid[row][col] = true;
                    }

                    generations.get(currentGen)[row][col] = grid[row][col];

                    if (generations.get(currentGen)[row][col]) graphicsObj.setPaint(Color.WHITE);
                    else graphicsObj.setPaint(midnight);
                } else if (Mouse.mouseHeld || renderWhilePaused) {
                    if (grid[row][col]) graphicsObj.setPaint(Color.WHITE);
                    else graphicsObj.setPaint(dark);
                } else {
                    if (generations.get(currentGen - 1)[row][col]) graphicsObj.setPaint(Color.WHITE);
                    else graphicsObj.setPaint(dark);
                }

                graphicsObj.fillRect((col - 1) * boxSize + border[3], (row - 1) * boxSize + border[0], boxSize, boxSize);
            }
        }


        // Update old grid
        if ((!paused && !Mouse.mouseHeld) || calcCurrentFrame) {
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
            graphicsObj.setPaint(midnight);
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
