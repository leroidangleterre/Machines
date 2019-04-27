import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardListener implements KeyListener {

    private GraphicPanel panel;

    public KeyboardListener(GraphicPanel p) {
        super();
        this.panel = p;
        System.out.println("New KeyboardListener");
    }

    public void keyPressed(KeyEvent e) {
        System.out.println("KeyPressed: '" + e.getKeyChar() + "'");
        switch (e.getKeyChar()) {
            case '0':
                panel.resetView();
                break;
            case '4':
                panel.swipe(-1, 0);
                break;
            case '6':
                panel.swipe(+1, 0);
                break;
            case '8':
                panel.swipe(0, +1);
                break;
            case '2':
                panel.swipe(0, -1);
                break;
            case '+':
                panel.zoomIn();
                break;
            case '-':
                panel.zoomOut();
                break;
            case ' ':
                panel.evolve(0.1);
                break;
            case 'g':
                panel.toggleGravity();
                break;
            case 'p':
                panel.togglePlayPause();
                break;
            case 'b':
                panel.blockSpeeds();
                break;
            case 'm':
                panel.mutate();
                break;
            case '*':
                panel.increaseSpringSize(1.1);
                break;
            case '/':
                panel.increaseSpringSize(1 / 1.1);
                break;
            default:
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        // System.out.println("KeyReleased: ");
    }

    public void keyTyped(KeyEvent e) {
        // System.out.println("KeyTyped: ");
    }

}
