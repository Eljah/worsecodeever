package fontatest;

import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileRegeneratorAvatar {

    private static Iterator<File> fileIterator;
    private static File fullDirSource = null;
    private static File fullDirOut = null;
    private static int width = 400;
    private static int height = 400;
    private static int channels = 3;


    public static void main(String[] args) {
        try {
            fullDirOut =
                    //new File("src/main/resources");
                    new File("D:\\avatars");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<String> bigrams = new ArrayList<>();
        BufferedImage image = null;

        for (char alphabet = 'a'; alphabet <= 'z'; alphabet++) {
            for (char alphabet2 = 'a'; alphabet2 <= 'z'; alphabet2++) {
                bigrams.add((alphabet + "" + alphabet2 + "").toUpperCase());
            }
        }

        for (String bigram : bigrams) {
            try {
                image = ImageIO.read(new File("generic.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Graphics2D g2d = image.createGraphics();
//            Font font = new Font("Arial", Font.PLAIN, 48);
//            g2d.setFont(font);
            //FontMetrics fm = g2d.getFontMetrics();
            //g2d.setPaint(Color.WHITE);
            //g2d.fillRect(0,0, width, height);
            //g2d.setColor(Color.BLACK);
            //g2d.drawString(bigram, 0, fm.getAscent());
            //g2d.dispose();
             for (int i = 160; i<200; i++) {
                 for (int j = -3; j<4; j++) {
                     for (int k = -3; k<4; k++) {
                         new File(fullDirOut + "\\" + j + "\\" + k + "\\" + i).mkdirs();
                         Font font = new Font("Arial", Font.PLAIN, i);
                         g2d.setFont(font);
                         g2d.setPaint(Color.WHITE);

                         TextLayout textLayout = new TextLayout(bigram, g2d.getFont(),
                                 g2d.getFontRenderContext());
                         double textHeight = textLayout.getBounds().getHeight();
                         double textWidth = textLayout.getBounds().getWidth();

                         g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                 RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

// Draw the text in the center of the image
                         g2d.drawString(bigram, width / 2 - (int) textWidth / 2 + j,
                                 height / 2 + (int) textHeight / 2 + k);

                         try {
                             ImageIO.write(image, "png", new File(fullDirOut + "\\" + j + "\\" + k + "\\" + i + "\\" + bigram + ".png"));
                         } catch (IOException ex) {
                             ex.printStackTrace();
                         }
                     }
                 }
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
