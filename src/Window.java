import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Window extends JFrame {
    public Window(String name, Game game) {
        this.setTitle(name);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                Game.running = false;
            }
        });

        this.add(game);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
