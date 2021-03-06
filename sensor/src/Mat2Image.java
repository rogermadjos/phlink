import java.awt.image.BufferedImage;
import java.util.logging.*;
import org.opencv.core.Core;
import org.opencv.core.Mat;

public class Mat2Image {
    Mat mat = new Mat();
    BufferedImage img;
    byte[] dat;
    
    private final Logger logger = Logger.getLogger(Mat2Image.class.getName());
    
    public Mat2Image() {
    }
    public Mat2Image(Mat mat) {
        getSpace(mat);
    }
    public void getSpace(Mat mat) {
    	Logger logger = Logger.getLogger(Mat2Image.class.getName());
        this.mat = mat;
        int w = mat.cols(), h = mat.rows();
        if (dat == null || dat.length != w * h * 3)
            dat = new byte[w * h * 3];
        if (img == null || img.getWidth() != w || img.getHeight() != h
            || img.getType() != BufferedImage.TYPE_3BYTE_BGR)
                img = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        }
        BufferedImage getImage(Mat mat) {
            getSpace(mat);
            mat.get(0, 0, dat);
            img.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), dat);
 
        for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				int org = img.getRGB(x, y);
				int transformed = (((0x0000FF) & org) << 16)
						| ((0x00FF00) & org) | (((0xFF0000) & org) >> 16);
				img.setRGB(x, y, transformed);
			}
		}
        return img;
    }
    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
}