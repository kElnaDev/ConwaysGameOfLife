import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Key implements KeyListener {

    // Settings
    static int delayedAutoShift = 30; // no of frames since first press before considering holding
    static int shiftSpeed = 2; // if holding, how fast to move

    // Utilities
    static KeyEvent keyEvent;
    static KeyEvent key;
    static KeyEvent lastKey;
    static int framesSinceFirstKeyPress = 0;
    static boolean holdingKey = false;
    static boolean interpretKey = true;
    static boolean keyPressed = false;
    static boolean keyQueued = false;

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        keyEvent = e;
        keyQueued = true;
        System.out.println("Key pressed: \"" + keyEvent.getKeyChar() + "\" (" + keyEvent.getKeyCode() + ")");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyEvent = null;
    }

    public static void interpret() {
        lastKey = key;
        key = keyEvent;

        if (key == null) {
            keyPressed = false;
            framesSinceFirstKeyPress = 0;
            holdingKey = false;
            interpretKey = false;
        } else {
            if (lastKey == null || key.getKeyCode() != lastKey.getKeyCode()) {
                framesSinceFirstKeyPress = 0;
                interpretKey = true;
                holdingKey = false;
            } else {
                framesSinceFirstKeyPress++;
                if (framesSinceFirstKeyPress >= delayedAutoShift) holdingKey = true;

                if (holdingKey) interpretKey = (framesSinceFirstKeyPress % shiftSpeed == 0);
                else interpretKey = false;
            }

            if (interpretKey) {
                switch (key.getKeyCode()) {
                    // RAND
                    case KeyEvent.VK_R -> {
                        Utilities.randBoard();
                        if (Game.paused) Game.renderWhilePaused = true;
                    }

                    // CLEAR
                    case KeyEvent.VK_C -> {
                        if (!holdingKey) {
                            Utilities.clearBoard();
                            if (Game.paused) Game.renderWhilePaused = true;
                        }
                    }

                    // PAUSE
                    case KeyEvent.VK_P -> {
                        if (!holdingKey) {
                            if (Game.currentGen == Game.generations.size()) Game.paused = !Game.paused;
                            Game.renderWhilePaused = false;
                        }
                    }

                    // RAND THRESHOLD
                    case KeyEvent.VK_1 -> Game.randThreshold = 0.3;
                    case KeyEvent.VK_2 -> Game.randThreshold = 0.5;
                    case KeyEvent.VK_3 -> Game.randThreshold = 0.8;

                    // GENERATIONS
                    case KeyEvent.VK_LEFT -> {
                        if (Game.currentGen - 1 > 0) {
                            Game.paused = true;
                            Game.currentGen--;
                        }
                    }
                    case KeyEvent.VK_RIGHT -> {
                        if (Game.currentGen + 1 < Game.generations.size()) {
                            Game.paused = true;
                            Game.currentGen++;
                        } else if (Game.currentGen + 1 == Game.generations.size()) {
                            Game.calcCurrentFrame = true;
                        }
                    }
                    case KeyEvent.VK_UP -> {
                        if (!holdingKey) {
                            Game.paused = true;
                            Game.currentGen = 1;
                        }
                    }
                    case KeyEvent.VK_DOWN -> {
                        if (!holdingKey) {
                            Game.paused = false;
                            Game.currentGen = Game.generations.size();
                        }
                    }

                    // GAME SPEED
                    case KeyEvent.VK_MINUS, KeyEvent.VK_UNDERSCORE -> {
                        if (Game.amountOfTicks - 5 >= 5) Game.amountOfTicks -= 5;
                        System.out.println("Game now running at " + Game.amountOfTicks + " ticks per second");
                    }
                    case KeyEvent.VK_EQUALS, KeyEvent.VK_PLUS -> {
                        if (Game.amountOfTicks + 5 <= 60) Game.amountOfTicks += 5;
                        System.out.println("Game now running at " + Game.amountOfTicks + " ticks per second");
                    }

                    // BOX SIZE
                    case KeyEvent.VK_OPEN_BRACKET -> {
                        if (Game.boxSize - 1 >= 1) {
                            Game.boxSize--;
                            Utilities.handleResize();
                        }
                    }
                    case KeyEvent.VK_CLOSE_BRACKET -> {
                        if (Game.boxSize + 1 <= 20) {
                            Game.boxSize++;
                            Utilities.handleResize();
                        }
                    }
                }
            }
            keyPressed = true;
        }
    }
}
