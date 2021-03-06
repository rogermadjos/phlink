import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

public class QRDecoder {
	private final Logger logger = Logger.getLogger(QRDecoder.class.getName());
	private ActionListener listener;
	private String data = "";
	private boolean canDecode = true;
	
	public QRDecoder(ActionListener listener) {
		logger.setLevel(Level.INFO);
		this.listener = listener;
	}
	
	private synchronized void debounce() {
		canDecode = false;
		Timer timer = new Timer(3000, new ActionListener() {
			@Override
			public synchronized void actionPerformed(ActionEvent e) {
				canDecode = true;
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
	
	public String getData() {
		return data;
	}
	
    public void decodeQRCode(BufferedImage image) {
    	if(!canDecode) {
    		return;
    	}
    	BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
    	QRCodeReader reader = new QRCodeReader();
    	Result result;
    	try {
			result = reader.decode(binaryBitmap);
			String newData = result.getText();
			data = newData;
			debounce();
			listener.actionPerformed(new ActionEvent(this, 0, "QR_CODE_ACTIVE"));
    		logger.log(Level.FINE, "new: " + newData + ", old: " + data);
		} catch (ReaderException e) {
			if( e instanceof ChecksumException ) {
				listener.actionPerformed(new ActionEvent(this, 0, "QR_CODE_AWARE"));
			}
			if( e instanceof NotFoundException ) {
				listener.actionPerformed(new ActionEvent(this, 0, "QR_CODE_IDLE"));
			}
			logger.log(Level.FINEST, "No QR code", e);
		}
    }
}
