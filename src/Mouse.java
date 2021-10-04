import javax.swing.*;
import java.awt.event.*;

public class Mouse implements MouseListener, MouseMotionListener, MouseWheelListener {

    // UTILITIES
    public static boolean mousePressed = false;
    public static boolean mouseQueued = false;
    public static int trueX, trueY;
    public static int boxX, boxY;
    public static String button;
    static boolean mouseHeld = false;
    static int direction = 0;
    static boolean scrolledInLastFrame = false;
    static double zoomSpeed = 0.1;


    // GENERAL
    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        mouseQueued = true;
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


    // DRAG
    @Override
    public void mouseDragged(MouseEvent e) {
        trueX = e.getX();
        trueY = e.getY();
        boxX = (trueX - Game.border[3]) / Game.boxSize;
        boxY = (trueY - Game.border[0]) / Game.boxSize;
    }

    @Override
    public void mouseMoved(MouseEvent e) {}


    // WHEEL
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        direction = e.getWheelRotation();
        scrolledInLastFrame = true;
    }


    // INTERPRETATION
    public static void interpret() {
        if (mousePressed || mouseQueued) {
            mouseHeld = true;

            if (Game.currentGen == Game.generations.size()) {
                if (Game.paused) Game.renderWhilePaused = true;

                if (boxX > -1 && boxX < Game.cols && boxY > -1 && boxY < Game.rows) {
                    Game.oldGrid[boxY + 1][boxX + 1] = (button.equals("left")) ? 255 : 0;
                    Game.grid[boxY + 1][boxX + 1] = (button.equals("left")) ? 255 : 0;
                }
            }
            mouseQueued = false;
        } else {
            mouseHeld = false;
        }

        if (scrolledInLastFrame) {
            if ((int) (Game.boxSize * (Game.zoom - (zoomSpeed * direction))) >= 1) {
                Game.zoom -= zoomSpeed * direction;
            }
            scrolledInLastFrame = false;
        }
    }
}
