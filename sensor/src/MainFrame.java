import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import fare.FareMatrixModel;

public class MainFrame extends JFrame {
    private ImagePanel videoPane;
    public final int FRAME_WIDTH = 640;
    public final int FRAME_HEIGHT = 480;
    
    private final Logger logger = Logger.getLogger(MainFrame.class.getName());
    private QRDecoder qrDecoder;
    private Thread displayThread;
    private Thread decodeThread;
    private Indicator indicator;
    private JComboBox<String> locationSelector;
    private JComboBox<String> operationSelector;
    private FareMatrixModel fares;

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
        try {
			fares = new FareMatrixModel();
		} catch (ClassNotFoundException | SQLException e1) {
			e1.printStackTrace();
		}
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
        
        List<String> locations = null;
		try {
			locations = fares.getAllLocations();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
        locationSelector = new JComboBox<String>();
        Iterator<String> iter = locations.iterator();
        while(iter.hasNext()) {
        	locationSelector.addItem(iter.next());
        }
        
        operationSelector = new JComboBox<>(new String[] { "EMBARK", "DISEMBARK" });
        
        GroupLayout layout = new GroupLayout(getContentPane());
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        	.addComponent(indicator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        	.addComponent(videoPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        	.addGroup(layout.createSequentialGroup()
        		.addComponent(operationSelector)	
        		.addComponent(locationSelector)	
        	)
        );
        
        layout.setVerticalGroup(layout.createSequentialGroup()
        	.addComponent(indicator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        	.addComponent(videoPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        	.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    			.addComponent(operationSelector)
    			.addComponent(locationSelector)
        	)
        	
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