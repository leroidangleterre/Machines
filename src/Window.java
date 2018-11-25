
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import javax.swing.JFrame;
import javax.swing.JButton;

public class Window extends JFrame{

	private GraphicPanel panel;
	private Toolbar toolbar;
	private int xMouse, yMouse;
	private boolean leftClickActive, centerClickActive, rightClickActive;

	private KeyboardListener keyboardListener;
	
	public Window(){
		super();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setSize(new Dimension(1900, 1000));

		this.addMouseListener(new MyMouseListener());
		this.addMouseMotionListener(new MyMouseMotionListener());
		this.addMouseWheelListener(new MyMouseWheelListener());

		this.xMouse = 0;
		this.yMouse = 0;
		this.leftClickActive = false;
		this.centerClickActive = false;
		this.rightClickActive = false;

		this.setLayout(new BorderLayout());
	}

	public Window(GraphicPanel pan){
		this();
		this.setPanel(pan);
		this.keyboardListener = new KeyboardListener(this.panel);
		this.addKeyListener(this.keyboardListener);
		this.panel.addKeyListener(this.keyboardListener);
		this.toolbar.setKeyListener(this.keyboardListener);
	}


	public void setPanel(GraphicPanel pan){
		this.panel = pan;
		this.getContentPane().add(this.panel, BorderLayout.CENTER);
		this.toolbar = new Toolbar(this.panel);
		this.getContentPane().add(this.toolbar, BorderLayout.SOUTH);
		
		this.panel.addMouseListener(new MyMouseListener());
		this.panel.addMouseMotionListener(new MyMouseMotionListener());
		this.panel.addMouseWheelListener(new MyMouseWheelListener());
	}

	/* Change the zoom factor and keep unchanged only the point hovered by the mouse. */
	public void zoomOnMouse(double fact){
		this.panel.zoomOnMouse(fact, xMouse, yMouse);
	}


	private class MyMouseMotionListener implements MouseMotionListener{

		public MyMouseMotionListener(){
			super();
		}

		public void mouseMoved(MouseEvent e){
			xMouse = e.getX();
			yMouse = e.getY();
		}
		public void mouseDragged(MouseEvent e){

			// TODO:  the left and right clicks may have other properties.
			if(centerClickActive || rightClickActive){
				/* The Y coordinate axis is oriented downwards in the window,
				   upwards in the GraphicPanel. */
				panel.translate(e.getX() - xMouse,
						-(e.getY() - yMouse));
			}
			xMouse = e.getX();
			yMouse = e.getY();
		}
	}


	private class MyMouseWheelListener implements MouseWheelListener{

		public MyMouseWheelListener(){
			super();
		}

		public void mouseWheelMoved(MouseWheelEvent e){
			// System.out.print("wheel turn ");
			if(e.getWheelRotation() > 0){
				// System.out.println("down");
				zoomOnMouse(1/1.1);
			}
			else{
				// System.out.println("up");
				zoomOnMouse(1.1);
			}
		}
	}

	private class MyMouseListener implements MouseListener{

		public MyMouseListener(){
			super();
		}

		public void mouseClicked(MouseEvent e){
		}
		public void mouseEntered(MouseEvent e){
			// System.out.println("Mouse entered");
		}
		public void mouseExited(MouseEvent e){
			// System.out.println("Mouse exited");
		}
		public void mousePressed(MouseEvent e){
			if(e.getButton() == MouseEvent.BUTTON1){
				leftClickActive = true;
			}
			if(e.getButton() == MouseEvent.BUTTON2){
				centerClickActive = true;
			}
			if(e.getButton() == MouseEvent.BUTTON3){
				rightClickActive = true;
			}
		}
		public void mouseReleased(MouseEvent e){
			if(e.getButton() == MouseEvent.BUTTON1){
				leftClickActive = false;
			}
			if(e.getButton() == MouseEvent.BUTTON2){
				centerClickActive = false;
			}
			if(e.getButton() == MouseEvent.BUTTON3){
				rightClickActive = false;
			}
		}



	}


}
