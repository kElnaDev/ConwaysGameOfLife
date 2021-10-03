import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseListener, MouseMotionListener {

    public static boolean mousePressed = false;
    public static int trueX, trueY;
    public static int boxX, boxY;
    public static String button;
    static boolean mouseHeld = false;

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        mousePressed = true;
        trueX = e.getX();
        trueY = e.getY();
        boxX = (trueX - Game.border[3]) / Game.boxSize;
        boxY = (trueY - Game.border[0]) / Game.boxSize;

        if (SwingUtilities.isLeftMouseButton(e)) button = "left";
        else if (SwingUtilities.isMiddleMouseButton(e)) button = "middle";
        else if (SwingUtilities.isRightMouseButton(e)) button = "right";
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mousePressed = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        trueX = e.getX();
        trueY = e.getY();
        boxX = (trueX - Game.border[3]) / Game.boxSize;
        boxY = (trueY - Game.border[0]) / Game.boxSize;
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

    public static void interpret() {
        if (Mouse.mousePressed) {
            mouseHeld = true;

            if (Game.currentGen == Game.generations.size()) {
                if (Game.paused) Game.renderWhilePaused = true;

                if (Mouse.boxX > -1 && Mouse.boxX < Game.cols && Mouse.boxY > -1 && Mouse.boxY < Game.rows) {
                    Game.oldGrid[Mouse.boxY + 1][Mouse.boxX + 1] = (Mouse.button.equals("left"));
                    Game.grid[Mouse.boxY + 1][Mouse.boxX + 1] = (Mouse.button.equals("left"));
                }
            }
        } else {
            mouseHeld = false;
        }
    }
}
