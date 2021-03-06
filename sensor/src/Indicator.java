import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

public class Indicator extends ImagePanel {
	public static final int IDLE = 0;
	public static final int Error = 1;
	public static final int ACTIVE = 2;
	private Dimension PANEL_SIZE = new Dimension(480, 480);
	private final Logger logger = Logger.getLogger(Indicator.class.getName());
	private BufferedImage buffer = new BufferedImage(PANEL_SIZE.width, PANEL_SIZE.height, BufferedImage.TYPE_INT_ARGB);
	private int state;
	
	public Indicator() {
		super();
		logger.setLevel(Level.INFO);
		setPreferredSize(PANEL_SIZE);
		setState(IDLE);
		draw();
	}
	
    public void draw(){
		Graphics g = buffer.getGraphics();
		g.setColor(new Color(255, 255, 255, 0));
		g.fillRect(0, 0, PANEL_SIZE.width, PANEL_SIZE.height);
		Color color;
		switch (state) {
		case IDLE:
			color = Color.ORANGE;
			break;
		case ERROR:
			color = Color.RED;
			break;
		case ACTIVE:
			color = Color.GREEN;
			break;
		default:
			color = Color.RED;
			break;
		}
        
        g.setColor(color);
        int radius = (int) (PANEL_SIZE.width * 0.35);
        g.fillOval(PANEL_SIZE.width / 2 - radius, PANEL_SIZE.height / 2 - radius,
        		radius * 2, radius * 2);
        
        updateImage(buffer);
    }
	
	private boolean canUpdateState = true;

	public synchronized void setState(int state) {
		final Indicator self = this;
		if(!canUpdateState) {
			return;
		}
		this.state = state;
		if(state == ACTIVE) {
			canUpdateState = false;
			draw();
			Timer timer = new Timer(2000, new ActionListener() {
				@Override
				public synchronized void actionPerformed(ActionEvent e) {
					self.state = IDLE;
					self.draw();
					canUpdateState = true;
				}
			});
			timer.setRepeats(false);
			timer.start();
		}
		if(state == ERROR) {
			canUpdateState = false;
			draw();
			Timer timer = new Timer(1000, new ActionListener() {
				@Override
				public synchronized void actionPerformed(ActionEvent e) {
					self.state = IDLE;
					self.draw();
					canUpdateState = true;
				}
			});
			timer.setRepeats(false);
			timer.start();
		}
	}
	
	public int getState() {
		return state;
	}
}
