import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class CGoL {
    static Thread thread;
    static Window window;
    static Game game;

    public static void main(String[] args) {
        game = new Game(900, 900);
        window = new Window("Conway's Game of Life", game);

        // Game Input Listeners
        Key keyListener = new Key();
        Mouse mouseListener = new Mouse();
        game.addKeyListener(keyListener);
        game.addMouseListener(mouseListener);
        game.addMouseMotionListener(mouseListener);
        game.addMouseWheelListener(mouseListener);

        // Handle window close
        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Game.resizedWidth = game.getWidth();
                Game.resizedHeight = game.getHeight();
                Game.resized = true;
            }
        });

        // Game Thread
        Game.running = true;
        thread = new Thread(game);
        thread.start();
    }

    public static void stop() {
        window.dispose();
        thread.interrupt();
        System.exit(0);
    }
}