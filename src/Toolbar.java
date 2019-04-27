import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

/* This class describes a component that regroups buttons.
 */
public class Toolbar extends JPanel {

    private JButton buttonStart;
    private JButton buttonStop;
    private JButton buttonRAZ;
    private JButton buttonEvolve;
    private JButton buttonSuperposition;
    private JButton buttonMutate;

    private GraphicPanel panel;

    private KeyboardListener keyboardListener;

    public Toolbar(GraphicPanel pan) {

        this.panel = pan;

        this.buttonStart = new JButton("Play");
        this.buttonStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (buttonStart.getText().compareTo("Play") == 0) {
                    panel.play();
                    buttonStart.setText("Pause");
                } else {
                    panel.pause();
                    buttonStart.setText("Play");
                }
            }
        });
        this.add(this.buttonStart);

        this.buttonEvolve = new JButton("evolve");
        this.buttonEvolve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.evolve();
            }
        });
        this.add(this.buttonEvolve);

        this.buttonSuperposition = new JButton("Superposition");
        this.buttonSuperposition.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.switchSuperposition();
            }
        });
        this.add(this.buttonSuperposition);

        this.buttonMutate = new JButton("Mutate");
        this.buttonMutate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.mutate();
            }
        });
        this.add(this.buttonMutate);
    }

    public void setKeyListener(KeyboardListener k) {
        this.addKeyListener(k);
        this.buttonStart.addKeyListener(k);
        this.buttonEvolve.addKeyListener(k);
        this.buttonSuperposition.addKeyListener(k);
    }
}
