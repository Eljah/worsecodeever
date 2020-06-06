package seq2seq;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.nativeblas.Nd4jCpu;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CLITester {
    public static void main(String[] args) {
        System.out.println("----- ExampleMain started -------");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line = null;
            InputStream is = new FileInputStream(new File("out/_names_2_-6.zip"));
            ComputationGraph model = ModelSerializer.restoreComputationGraph(is);
            Seq2SeqPredicter predictor = new Seq2SeqPredicter(model);
            CustomSequenceIterator2.oneHotEncoding();
            while (true) {
                if ((line = reader.readLine()) != null) {
                    System.out.println("echo>> " + line);
                    while (line.length()<14) {
                        line = line + " ";
                    }
                    MultiDataSet toPredictor = predictor.encode(line.replace("ль","љ").replace("ый","ї").replace("дж","џ").replace("қ","къ").replace("ғ","гъ"));
                    INDArray output = predictor.output(toPredictor);
                    String[] encodeSeqS = CustomSequenceIterator2.oneHotDecode(output);
                    List list = Arrays.asList(encodeSeqS);
                    System.out.println((new StringBuilder(String.join("", list)).reverse().toString()).replace("љ","ль").replace("ї","ый").replace("џ","дж").replace("қ","къ").replace("ғ","гъ"));
                } else {
                    //input finishes
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
