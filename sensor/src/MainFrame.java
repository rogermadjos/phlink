import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import sun.net.www.protocol.gopher.GopherClient;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainFrame extends JFrame {
    private ImagePanel videoPane;
    public final int FRAME_WIDTH = 640;
    public final int FRAME_HEIGHT = 480;
    
    private final Logger logger = Logger.getLogger(MainFrame.class.getName());
    private QRDecoder qrDecoder;
    private Thread displayThread;
    private Thread decodeThread;
    private Indicator indicator;

    /**
    * Launch the application.
    */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
    * Create the frame.
    */
    public MainFrame() {
    	super();
    	logger.setLevel(Level.INFO);
    	setTitle("phlink");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				logger.log(Level.INFO, "started");
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				logger.log(Level.INFO, "close");
				((StoppableThread) displayThread).stopThread();
				((StoppableThread) decodeThread).stopThread();
				videoCap.release();
			}
		});
        
        videoPane = new ImagePanel();
        videoPane.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        
        indicator = new Indicator();
        
        GroupLayout layout = new GroupLayout(getContentPane());
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        	.addComponent(indicator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        	.addComponent(videoPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        	
        );
        
        layout.setVerticalGroup(layout.createSequentialGroup()
        	.addComponent(indicator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        	.addComponent(videoPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        );
        
        pack();
        setLocationRelativeTo(null);
        
        qrDecoder = new QRDecoder(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand() == "QR_CODE_ACTIVE") {
					logger.log(Level.INFO, "QR_CODE_ACTIVE");
					indicator.setState(Indicator.ACTIVE);
				}
			}
		});

        displayThread = new DisplayThread();
        decodeThread = new DecodeThread();
        displayThread.start();
        decodeThread.start();
    }

    VideoCap videoCap = new VideoCap();
    BufferedImage imageFrame = null;
    
    private BufferedImage getFrame() {
    	return videoCap.getOneFrame();
    }
    
    class StoppableThread extends Thread {
    	protected boolean active = true;
    	
        public void stopThread() {
        	active = false;
		}
    }

    class DisplayThread extends StoppableThread {
        @Override
        public void run() {
            while (active){
                BufferedImage image = getFrame();
                imageFrame = image;
                videoPane.updateImage(imageFrame);
                try { Thread.sleep(20);
                } catch (InterruptedException e) {    }
            }  
        } 
    }
    
    class DecodeThread extends StoppableThread {
        @Override
        public void run() {
            while (active){
            	if (imageFrame != null) {
                	synchronized (imageFrame) {
                		qrDecoder.decodeQRCode(imageFrame);
    				}
            	}
                try { Thread.sleep(100);
                } catch (InterruptedException e) {    }
            }  
        }
    }

}