import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class CGoL {
    static Thread thread;
    static Window window;

    public static void main(String[] args) {
        Game game = new Game(900, 900);
        window = new Window("Conway's Game of Life", game);

        Key keyListener = new Key();
        Mouse mouseListener = new Mouse();
        game.addKeyListener(keyListener);
        game.addMouseListener(mouseListener);
        game.addMouseMotionListener(mouseListener);
        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Game.resized = true;
            }
        });

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