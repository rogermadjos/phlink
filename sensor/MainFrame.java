import java.awt.EventQueue;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {
    private JPanel contentPane;
    public final int FRAME_WIDTH = 800;
    public final int FRAME_HEIGHT = 600;

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        setResizable(false);
        contentPane.setLayout(null);
        setLocationRelativeTo(null);

        new MainThread().start();
    }

    // VideoCap videoCap = new VideoCap();

    // public void paint(Graphics g){
    //     g = contentPane.getGraphics();
    //     g.drawImage(videoCap.getOneFrame(), 0, 0, this);
    // }

    class MainThread extends Thread {
        @Override
        public void run() {
            for (;;){
                repaint();
                try { Thread.sleep(30);
                } catch (InterruptedException e) {    }
            }  
        } 
    }
}