package fontatest;

import fontastic.FContour;
import fontastic.FGlyph;
import fontastic.FPoint;
import fontastic.Fontastic;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FileRegenerator2 {

    private static Iterator<File> fileIterator;
    private static File fullDirSource = null;
    private static File fullDirOut = null;
    private static int width = 200;
    private static int height = 60;
    private static int channels = 3;


    public static void main(String[] args) {

        Font font = new Font("Arial", Font.PLAIN, 48);
        String str = "";
        File physicalFont = null;
        File physicalFontLocalCopy = new File("captchafont3.ttf");
        try {
            //Font font = new Font("Arial Black", Font.PLAIN, 20);
            Method method = font.getClass().getDeclaredMethod("getFont2D");
            method.setAccessible(true);
            str = method.invoke(font).toString();
            if (str.contains(" fileName=")) {
                str = str.substring(str.indexOf(" fileName=")+10).replace("\r\n", "\n")+"\n";
                str = str.substring(0, str.indexOf("\n"));
                physicalFont = new File(str);
            }
            if (physicalFont==null || !physicalFont.isFile()) {
                System.out.println("not found");
            } else {
                System.out.println("font file: "+ physicalFont.getAbsolutePath());
            }

            Path copied = physicalFontLocalCopy.toPath();
            Path originalPath = physicalFont.toPath();
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        Fontastic f = null;
        try {
            f = new Fontastic("ExampleFont", physicalFontLocalCopy);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //FGlyph nospace=f.getGlyph('\u200B');
        FGlyph nospace = new FGlyph('\u200B');
        //FGlyph ch0 = new FGlyph('0');
        //nospace.setAdvanceWidth(-20);
        //List<FContour> spaceCounturs = f.
        //        getGlyph('a').
        //        getContours();
        //System.out.println(spaceCounturs.size());
        //f.setAuthor("Nobody");
        //f.ge
        //System.out.println(f.getGlyph('0').g);
        //System.out.println(f.getGlyph('1').getAdvanceWidth());

        FontRenderContext context = new FontRenderContext(null, false, false);

        GeneralPath shape = new GeneralPath();
        TextLayout layout = new TextLayout("0", font, context);

        Shape outline = layout.getOutline(null);
        shape.append(outline, true);

        //f.addGlyph('0');

        FPoint[] fpoints=new FPoint[getPoints(shape).size()];

        for (int i=0; i<fpoints.length; i++) {
            fpoints[i] = new FPoint(getPoints(shape).get(i).x*20,getPoints(shape).get(i).y*20+20);
        }
        FContour fContour = new FContour(fpoints);
        f.addGlyph('0',fContour);

//        f.addGlyph('1');
//        f.addGlyph('2');
//        f.addGlyph('3');
//        f.addGlyph('4');
//        f.addGlyph('5');
//        f.addGlyph('6');
//        f.addGlyph('7');
//        f.addGlyph('8');
//        f.addGlyph('9');
        f.addGlyph('\u200B');
        f.addGlyph('h');// Assign contour to character A
        //f.getGlyph('\u200B').setContour(0, spaceCounturs.get(0));
        f.getGlyph('\u200B').setAdvanceWidth(-20);
        f.getGlyph('h').setAdvanceWidth(-20);
        f.buildFont();

        Font drawFont = null;
        try {
//            GraphicsEnvironment ge =
//                    GraphicsEnvironment.getLocalGraphicsEnvironment();
//            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, physicalFontLocalCopy));
            drawFont = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(physicalFontLocalCopy));
            //drawFont = ge.getAllFonts()[0];
            System.out.println("Font size: "+drawFont.getSize());
            System.out.println("Font family: "+drawFont.getFamily());
            System.out.println("Font style: "+drawFont.getStyle());
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;

        height = 60;
        width = 200;
        channels = 3;
        try {
            fullDirSource =
                    //new File("src/main/resources");
                    new File("D:\\captchas\\ownfont");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        fullDirOut =  new File(fullDirSource, "out");
        fullDirSource = new File(fullDirSource, "train");
        //load();
        //while (fileIterator.hasNext()) {
        for (int i=0; i<1000; i++)
        {
            String nameNumber= String.format("%06d", i);
            String originalName=nameNumber;
            nameNumber=addChar(nameNumber,' ',new Random().nextInt(6));
            nameNumber=addChar(nameNumber,'\u200B',new Random().nextInt(6));

            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = img.createGraphics();

            //
            g2d.setFont(drawFont);
            Font newFont = g2d.getFont().deriveFont(48f);
            g2d.setFont(newFont);
            //
            g2d.setFont(font); //todo remove previous unsused stuff

            System.out.println("Font size: "+g2d.getFont().getSize());
            System.out.println("Font family: "+g2d.getFont().getFamily());
            System.out.println("Font style: "+g2d.getFont().getStyle());

            FontMetrics fm = g2d.getFontMetrics();
            g2d.setPaint(Color.WHITE);
            g2d.fillRect(0,0, width, height);
            g2d.setColor(Color.BLACK);

            //AffineTransform affineTransform = new AffineTransform();
            ///affineTransform.setToTranslation();

            int width = g2d.getFontMetrics().stringWidth(nameNumber);
            System.out.println(width);

            g2d.drawString(nameNumber, 0, fm.getAscent());
            //g2d.drawString("a");
            g2d.dispose();
            try {
                ImageIO.write(img, "jpg", new File(fullDirOut+"\\"+originalName+".jpg"));
            } catch (IOException ex) {
                ex.printStackTrace();
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

                    out.add(new Point((int)x3, (int)y3));

                    x = x3;
                    y = y3;
                    break;

                case PathIterator.SEG_CUBICTO:
                    out.add(new Point((int)x3, (int)y3));
                    x = x3;
                    y = y3;
                    break;
                case PathIterator.SEG_LINETO:
                    out.add(new Point((int)x1, (int)y1));
                    x = x1;
                    y = y1;
                    break;
                case PathIterator.SEG_MOVETO:
                    out.add(new Point((int)x1, (int)y1));
                    x = x1;
                    y = y1;
                    break;
            }
            iterator.next();
        }

        return out;
    }
}
