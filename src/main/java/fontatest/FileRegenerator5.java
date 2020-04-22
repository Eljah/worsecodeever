package fontatest;

import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FileRegenerator5 {

    private static Iterator<File> fileIterator;
    private static File fullDirSource = null;
    private static File fullDirOut = null;
    private static int width = 200;
    private static int height = 60;
    private static int channels = 3;


    public static void main(String[] args) {

        Font font = new Font("Arial", Font.PLAIN, 48);

        height = 60;
        width = 200;
        channels = 3;
        try {
            fullDirSource =
                    //new File("src/main/resources");
                    new File("D:\\captchas\\ownfont2");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        fullDirOut = new File(fullDirSource, "out");
        fullDirSource = new File(fullDirSource, "train");
        //load();
        //while (fileIterator.hasNext()) {
        for (int j = 0; j < 10; j++) {

            System.out.println(j);
            for (int i = j; i < j + 100000; i++) {
                String nameNumber = String.format("%06d", i);
                String originalName = nameNumber;
                nameNumber = addChar(nameNumber, ' ', new Random().nextInt(6));
                nameNumber = addChar(nameNumber, '\u200B', 1 + new Random().nextInt(5));

                BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = img.createGraphics();

                g2d.setFont(font); //todo remove previous unsused stuff

                FontMetrics fm = g2d.getFontMetrics();
                g2d.setPaint(Color.WHITE);
                g2d.fillRect(0, 0, width, height);
                g2d.setColor(Color.BLACK);

                //AffineTransform affineTransform = new AffineTransform();
                ///affineTransform.setToTranslation();

                int total = 0;

                //System.out.println(nameNumber);
                for (char k : nameNumber.toCharArray()) {
                    int width = g2d.getFontMetrics().stringWidth(k + "");
                    AffineTransform orig = g2d.getTransform();
                    g2d.rotate(Math.PI / 180 * new Random().nextInt(15), total, fm.getAscent());
                    //g2d.drawString(k+"", total, fm.getAscent());

                    AffineTransform transform = g2d.getTransform();
                    transform.translate(total, fm.getAscent());
                    g2d.transform(transform);
                    g2d.setColor(Color.black);
                    FontRenderContext frc = g2d.getFontRenderContext();
                    int fontstyle = 35 + (int) (20 * Math.random());
                    //int fontstyle=15+k;
                    //System.out.println(fontstyle);
                    TextLayout tl = new TextLayout(k + "", g2d.getFont().deriveFont((float) fontstyle), frc);
                    Shape shape = tl.getOutline(null);
                    g2d.setStroke(new BasicStroke(2f));
                    g2d.draw(shape);
                    g2d.setColor(Color.white);
                    //g2d.fill(shape);

                    g2d.setTransform(orig);
                    if (k == '\u200B') total = total - 10;
                    total = total + width;
                }
                //g2d.drawString(nameNumber, 0, fm.getAscent());
                //g2d.drawString("a");
                g2d.dispose();
                //int[] pixel = new int[width * height];
                //PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixel, 0, width);

                for (int k = 1; k < width; k++) {
                    for (int l = 0; l < height; l++) {
                        int lnew = l + (int) (4 * Math.sin(k / 3));
                        if (lnew < height && lnew > 0) img.setRGB(k - 1, lnew, img.getRGB(k, l));
                    }
                }

                Graphics2D g2d2 = img.createGraphics();

                CubicCurve2D shape = new CubicCurve2D.Float();
                g2d2.setColor(Color.black);
                g2d2.setStroke(new BasicStroke(2f));
                shape.setCurve(0f, 20f + 20 * Math.random(), 100f + 20 * Math.random(), 0f, 100f + 20 * Math.random(), 60F, 200f, 20f + 20 * Math.random());
                g2d2.draw(shape);

                Font font2 = new Font("Arial", Font.PLAIN, 10);
                g2d2.setFont(font2);
                FontMetrics fm2 = g2d2.getFontMetrics();
                g2d2.setColor(Color.BLACK);
                g2d2.drawString("хуйня", 170, fm2.getAscent());

                g2d2.dispose();
                try {
                    ImageIO.write(img, "jpg", new File(fullDirOut + "\\" + j + "\\" + originalName + ".jpg"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static String addChar(String str, char ch, int position) {
        return str.substring(0, position) + ch + str.substring(position);
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

    public static List<Point> getPoints(Shape shape) {
        List<Point> out = new ArrayList<Point>();
        PathIterator iterator = shape.getPathIterator(null);

        double[] coordinates = new double[6];
        double x = 0, y = 0;

        while (!iterator.isDone()) {

            double x1 = coordinates[0];
            double y1 = coordinates[1];

            double x2 = coordinates[2];
            double y2 = coordinates[3];

            double x3 = coordinates[4];
            double y3 = coordinates[5];

            switch (iterator.currentSegment(coordinates)) {
                case PathIterator.SEG_QUADTO:
                    x3 = x2;
                    y3 = y2;

                    x2 = x1 + 1 / 3f * (x2 - x1);
                    y2 = y1 + 1 / 3f * (y2 - y1);

                    x1 = x + 2 / 3f * (x1 - x);
                    y1 = y + 2 / 3f * (y1 - y);

                    out.add(new Point((int) x3, (int) y3));

                    x = x3;
                    y = y3;
                    break;

                case PathIterator.SEG_CUBICTO:
                    out.add(new Point((int) x3, (int) y3));
                    x = x3;
                    y = y3;
                    break;
                case PathIterator.SEG_LINETO:
                    out.add(new Point((int) x1, (int) y1));
                    x = x1;
                    y = y1;
                    break;
                case PathIterator.SEG_MOVETO:
                    out.add(new Point((int) x1, (int) y1));
                    x = x1;
                    y = y1;
                    break;
            }
            iterator.next();
        }

        return out;
    }
}
