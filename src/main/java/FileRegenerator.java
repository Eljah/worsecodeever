import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FileRegenerator {

    private static Iterator<File> fileIterator;
    private static File fullDirSource = null;
    private static File fullDirOut = null;
    private static int width = 200;
    private static int height = 60;
    private static int channels = 3;


    public static void main(String[] args) {
        height = 60;
        width = 200;
        channels = 3;
        try {
            fullDirSource =
                    //new File("src/main/resources");
                    new File("D:\\captchas");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        fullDirOut =  new File(fullDirSource, "out");
        fullDirSource = new File(fullDirSource, "train");
        load();
        while (fileIterator.hasNext()) {
            File image=fileIterator.next();
            String nameNumber=image.getName().substring(0, image.getName().lastIndexOf('.'));

            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = img.createGraphics();
            Font font = new Font("Arial", Font.PLAIN, 48);
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.setPaint(Color.WHITE);
            g2d.fillRect(0,0, width, height);
            g2d.setColor(Color.BLACK);
            g2d.drawString(nameNumber, 0, fm.getAscent());
            g2d.dispose();
            try {
                ImageIO.write(img, "jpg", new File(fullDirOut+"\\"+nameNumber+".jpg"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    protected static void load() {
        try {
            List<File> dataFiles = (List<File>) FileUtils.listFiles(fullDirSource, new String[]{"jpg"}, true);
            //Collections.shuffle(dataFiles);
            fileIterator = dataFiles.iterator();
            //numExample = dataFiles.size();
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }
}
