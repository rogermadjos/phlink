import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

import backend.Backend;
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
    private JLabel labelOne;
    private JLabel labelTwo;
    private JLabel labelThree;
    private JLabel labelFour;
    private JLabel labelFive;
    private JLabel labelSix;
    private JLabel labelSeven;
    private JLabel labelEight;
    private JLabel labelNine;
    private JLabel labelTen;
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
        locationSelector.setFont(new Font("Arial", Font.BOLD, 26));
        //\u20B1
        labelOne = new JLabel("Balance: ");
        labelOne.setFont(new Font("Arial", Font.BOLD, 26));
        labelTwo = new JLabel();
        labelTwo.setFont(new Font("Arial", Font.BOLD, 26));
        labelTwo.setForeground(Color.blue);
        
        labelThree = new JLabel("phlinkID: ");
        labelThree.setFont(new Font("Arial", Font.BOLD, 26));
        labelFour = new JLabel();
        labelFour.setFont(new Font("Arial", Font.BOLD, 26));
        labelFour.setForeground(Color.blue);
        
        labelFive = new JLabel("Destination: ");
        labelFive.setFont(new Font("Arial", Font.BOLD, 26));
        labelSix = new JLabel();
        labelSix.setFont(new Font("Arial", Font.BOLD, 26));
        labelSix.setForeground(Color.blue);
        
        labelSeven = new JLabel("Fare: ");
        labelSeven.setFont(new Font("Arial", Font.BOLD, 26));
        labelEight = new JLabel();
        labelEight.setFont(new Font("Arial", Font.BOLD, 26));
        labelEight.setForeground(Color.blue);
        
        labelNine = new JLabel("Email: ");
        labelNine.setFont(new Font("Arial", Font.BOLD, 26));
        labelTen = new JLabel();
        labelTen.setFont(new Font("Arial", Font.BOLD, 26));
        labelTen.setForeground(Color.blue);
        
        GroupLayout layout = new GroupLayout(getContentPane());
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        	.addGroup(layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
        			.addComponent(labelOne)	
        			.addComponent(labelThree)
        			.addComponent(labelFive)
        			.addComponent(labelSeven)
        			.addComponent(labelNine)
        		)
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        			.addComponent(labelTwo)
        			.addComponent(labelFour)
        			.addComponent(labelSix)
        			.addComponent(labelEight)
        			.addComponent(labelTen)
        		)
        	)
        	.addComponent(indicator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        	.addComponent(videoPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
//        	.addGroup(layout.createSequentialGroup()
//        		.addComponent(locationSelector)	
//        	)
        );
        
        layout.setVerticalGroup(layout.createSequentialGroup()
        	.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        		.addComponent(labelOne)
    			.addComponent(labelTwo)
        	)
        	.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        		.addComponent(labelThree)
    			.addComponent(labelFour)
        	)
        	.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        		.addComponent(labelFive)
    			.addComponent(labelSix)
        	)
        	.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        		.addComponent(labelSeven)
    			.addComponent(labelEight)
        	)
        	.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        		.addComponent(labelNine)
    			.addComponent(labelTen)
        	)
        	.addComponent(indicator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        	.addComponent(videoPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
//        	.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
//    			.addComponent(locationSelector)
//        	)
        	
        );
        
        pack();
        setLocationRelativeTo(null);
        
    	Backend backend = new Backend();
    	try {
			backend.embark("sgFdFFgd", UUID.randomUUID().toString(), 2);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        qrDecoder = new QRDecoder(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand() == "QR_CODE_ACTIVE") {
					String data = qrDecoder.getData();
					logger.log(Level.INFO, "QR_CODE_ACTIVE: " + data);
					
					Scanner scan = new Scanner(data);
					scan.useDelimiter(":");
					String phlinkId = scan.next();
					String ticketId = scan.next();
					int fareId = Integer.parseInt(scan.next());
					
					String[] destinations = new String[] { "Opol", "El Salvador", "Alubijid", "Laguindingan" };
					int[] fares = new int[] { 15, 27, 42, 52 };
					System.out.println(phlinkId);
					System.out.println(ticketId);
					labelFour.setText(phlinkId);
					labelSix.setText(destinations[fareId - 1]);
					labelEight.setText("\u20B1" + fares[fareId - 1] + "");
					indicator.setState(Indicator.ACTIVE);
					Timer timer = new Timer(2000, new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							labelTwo.setText("");
							labelFour.setText("");
							labelSix.setText("");
							labelEight.setText("");
							labelTen.setText("");
						}
					});
					timer.setRepeats(false);
					timer.start();
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