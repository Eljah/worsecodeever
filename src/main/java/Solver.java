import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.concurrency.AffinityManager;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.linalg.dataset.MultiDataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.transform.ImageTransform;
import org.nd4j.linalg.indexing.NDArrayIndex;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javax.imageio.ImageIO.read;

public class Solver extends NativeImageLoader implements Serializable {
    static ComputationGraph model = null;

    List<String> labelList =
            Arrays.asList(
                    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
            );

    public Solver() {
        InputStream is = null;
        try {
            is = new FileInputStream(new File("out/model18-64_2.zip"));
            model = ModelSerializer.restoreComputationGraph(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(new Solver().solve(ImageIO.read(new File("D:\\captchas\\test\\860615.jpg"))));
    }

    public String solve(BufferedImage img) {

        INDArray[] feature = null;
        int width = img.getWidth();
        int height = img.getHeight();
        WritableRaster raster = img.getRaster();
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int[] p = new int[4];
                raster.getPixel(w, h, p);
                p[0] = (int) (0.3 * p[0]);
                p[1] = (int) (0.59 * p[1]);
                p[2] = (int) (0.11 * p[2]);
                int y = p[0] + p[1] + p[2];
                raster.setSample(w, h, 0, y);
                //if (y < 230) feature[0].putScalar(new int[]{1, h, w}, 1.0);
            }
        }
        feature = new INDArray[]{
                Nd4j.zeros(1, 60, 200)
        };

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int[] p = new int[4];
                raster.getPixel(w, h, p);
                p[0] = (int) (0.3 * p[0]);
                p[1] = (int) (0.59 * p[1]);
                p[2] = (int) (0.11 * p[2]);
                int y = p[0] + p[1] + p[2];
//                    raster.setSample(w, h, 0, y);
                //System.out.println(Arrays.toString(feature[0].shape()));
                if (y < 230) feature[0].putScalar(new int[]{1, h, w}, 1.0);
                //if (y < 230) feature[0].putScalar(new int[]{1, h, k}, 1.0); //todo why it is possibke to put [1,60,200] there if features array is [1,60,1]???
            }
        }

        INDArray[] output= model.output(feature);

        for (int j = 0; j< 6; j++) {
            output[j] = output[j].get(NDArrayIndex.point(0),NDArrayIndex.all(),NDArrayIndex.interval(30,30,181)).transpose();
        }

        String peLabel = "";
        INDArray preOutput = null;
        for (int digit = 0; digit < 6; digit++) {
            preOutput = output[digit].getRow(digit, true);;
            String pre = labelList.get(Nd4j.argMax(preOutput, 1).getInt(0));
            peLabel += pre;
        }
        return peLabel;
    }
}
