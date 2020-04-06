/*
 * Main.java
 *
 * Copyright (c) 2018 Yen-Chin, Lee <coldnew.tw@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.indexaccum.IAMax;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;

class Main4LSTM {

    private static final Logger logger = LoggerFactory.getLogger(Main4LSTM.class);

    private static long seed = 123;
    private static int epochs = 10000; //50
    private static int batchSize = 1;
    private static String rootPath = System.getProperty("user.dir");

    private static String modelDirPath = rootPath + File.separatorChar + "out";
    private static String modelPath = modelDirPath + File.separatorChar + "model4.zip";

    private static int lstmLayerSize = 1000;
    private static int tbpttLength = 500;

    public static void main(String[] args) throws Exception {

        long startTime = System.currentTimeMillis();
        logger.info("start up time: " + startTime);

        File modelDir = new File(modelDirPath);

        // create dir
        boolean hasDir = modelDir.exists() || modelDir.mkdirs();
        logger.info(modelPath);

        // create model
        MultiLayerNetwork model = createModel();
        // monitor the model score
        //UIServer uiServer = UIServer.getInstance();
        //StatsStorage statsStorage = new InMemoryStatsStorage();
        //uiServer.attach(statsStorage);


        DataSetIterator trainMulIterator = new CaptchaSetIterator2(batchSize, "train");
        DataSetIterator testMulIterator = new CaptchaSetIterator2(batchSize, "test");
        DataSetIterator validateMulIterator = new CaptchaSetIterator2(batchSize, "validate");
        // construct the iterator
//        DataSetIterator trainMulIterator = new CaptchaSetIterator2(batchSize, "out");
//        DataSetIterator testMulIterator = new CaptchaSetIterator2(batchSize, "out");
//        DataSetIterator validateMulIterator = new CaptchaSetIterator2(batchSize, "out");
//        // fit
        for (int i = 0; i < epochs; i++) {
            System.out.println("Epoch=====================" + i);
            model.fit(trainMulIterator);
        }
        ModelSerializer.writeModel(model, modelPath, true);
        long endTime = System.currentTimeMillis();
        System.out.println("=============run time=====================" + (endTime - startTime));

        System.out.println("=====eval model=====test==================");
        modelPredict(model, testMulIterator);

        System.out.println("=====eval model=====validate==================");
        modelPredict(model, validateMulIterator);
    }

    public static MultiLayerNetwork createModel() {

        UIServer uiServer = UIServer.getInstance();
        //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
        StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later
        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
        uiServer.attach(statsStorage);

        MultiLayerConfiguration config =
                new NeuralNetConfiguration.Builder()
//                        .seed(seed)
//                        .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
//                        .l2(1e-3)
//                        .updater(new Adam(1e-3))
//                        .weightInit(WeightInit.XAVIER_UNIFORM)
//                        .graphBuilder()
//                        .addInputs("trainFeatures")
//                        //.setInputTypes(InputType.convolutional(60, 200, 1))
//                        //.setOutputs("out1", "out2", "out3", "out4", "out5", "out6")
//                        .layer("lstm1",new LSTM.Builder().nIn(60).nOut(lstmLayerSize).activation(Activation.TANH).build(),
//                                "trainFeatures")
//                        .layer("lstm2", new LSTM.Builder().nIn(lstmLayerSize).nOut(lstmLayerSize).activation(Activation.TANH).build(), "lstm1")
//                        .layer("rnn", new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT).activation(Activation.SOFTMAX)        //MCXENT + softmax for classification
//                                .nIn(lstmLayerSize).nOut(60).build(), "lstm2")
//                        .backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength(tbpttLength).tBPTTBackwardLength(tbpttLength)
//                        .build();
                        .seed(12345)
                        .l2(0.0001)
                        .weightInit(WeightInit.XAVIER)
                        //.updater(new Adam(0.05))
                        .updater(new Adam(0.005))
                        .list()
                        .layer(new LSTM.Builder().nIn(60).nOut(lstmLayerSize)
                                .activation(Activation.TANH).build())
                        .layer(new LSTM.Builder().nIn(lstmLayerSize).nOut(lstmLayerSize)
                                .activation(Activation.TANH).build())
                        .layer(new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT).activation(Activation.SOFTMAX)        //MCXENT + softmax for classification
                                //.layer(new RnnOutputLayer.Builder(LossFunctions.LossFunction.SQUARED_LOSS).activation(Activation.RELU)        //MCXENT + softmax for classification
                                .nIn(lstmLayerSize).nOut(10).build())
                        //.nIn(lstmLayerSize).nOut(1).build())
                        .backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength(tbpttLength).tBPTTBackwardLength(tbpttLength)
                        .build();

        // Construct and initialize model
        //ComputationGraph model = new MultiLayerConfiguration(config);
        MultiLayerNetwork model = new MultiLayerNetwork(config);

        //model.setListeners(new ScoreIterationListener(36), new StatsListener(statsStorage));
        model.init();
        model.setListeners(new ScoreIterationListener(1));
        model.setListeners(new StatsListener(statsStorage));

        return model;
    }

    public static void modelPredict(MultiLayerNetwork model, DataSetIterator iterator) {
        int sumCount = 0;
        int correctCount = 0;

        List<String> labelList =
                Arrays.asList(
                        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
//                , "A", "B", "C", "D", "E", "F", "G",
//            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
//            "Y", "Z"
                );

        while (iterator.hasNext()) {
            DataSet mds = iterator.next();
            model.rnnClearPreviousState();
            INDArray output = model.rnnTimeStep(mds.getFeatures());
            //model.output(mds.getFeatures());
            //System.out.println(Arrays.toString(output.shape()));
            INDArray labels = mds.getLabels();
            //System.out.println(Arrays.toString(labels.shape()));
            //int dataNum = batchSize > output.rows() ? output.rows() : batchSize;
            //for (int dataIndex = 0; dataIndex < dataNum; dataIndex++) {

            System.out.println("FEATURES");
            for (int j = 0; j < 60; j++) {
                for (int i = 0; i < 200; i++)
                    System.out.print(mds.getFeatures().getScalar(new int[]{1, j, i}).getInt() > 0 ? "1" : " ");
                System.out.println();
            }

            System.out.println("RECOGNIZED");

            System.out.println(Arrays.toString(output.shape()));
            for (int j = 0; j < 10; j++) {
                for (int i = 194; i < 200; i++)
                    System.out.print(output.getScalar(new int[]{1, j, i}).getDouble() + "\t");
                System.out.println();
            }

            System.out.println("DATASET LABEL");

            for (int j = 0; j < 10; j++) {
                for (int i = 0; i < 6; i++)
                    System.out.print(labels.getScalar(new int[]{1, j, i}).getDouble() + "\t");
                System.out.println();
            }

            String reLabel = "";
            String preLabel = "";

            //INDArray preOutput = null;
            INDArray realLabel = null; //todo when here makes crash

            System.out.println(Arrays.toString(output.shape()));
            System.out.println(Arrays.toString(labels.shape()));
            //System.out.println(Arrays.toString(labels.get(NDArrayIndex.point(1), NDArrayIndex.all()).shape()));

            for (int digit = 0; digit < 6; digit++) {

                System.out.println(Arrays.toString(output.get(NDArrayIndex.point(0),NDArrayIndex.all(), NDArrayIndex.interval(digit,digit+1)).shape()));
                System.out.println(output.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.interval(digit,digit+1)));

                int idx = Nd4j.getExecutioner().execAndReturn(new IAMax(output.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.interval(digit,digit+1)))).getFinalResult().intValue();
                System.out.println(idx);
                preLabel += labelList.get(idx);

                //System.out.println(Arrays.toString(labels.get(NDArrayIndex.point(0),NDArrayIndex.all(), NDArrayIndex.interval(digit-194,digit-194+1)).shape()));
                //realLabel = labels.get(NDArrayIndex.point(0),NDArrayIndex.all(), NDArrayIndex.interval(digit-194,digit-194+1));
                int idx2 = Nd4j.getExecutioner().execAndReturn(new IAMax(labels.get(NDArrayIndex.point(0),NDArrayIndex.all(), NDArrayIndex.interval(digit,digit+1)))).getFinalResult().intValue();
                //System.out.println(idx2);
                //realLabel = labels.get(NDArrayIndex.point(1), NDArrayIndex.all());
                reLabel += labelList.get(idx2);
                //}
            }

            System.out.println("Pre: " + preLabel);
            System.out.println("Re:  " + reLabel);
            if (preLabel.equals(reLabel)) {
                correctCount++;
                // System.out.println(correctCount);
            }
            sumCount++;
            // System.out.println(sumCount);
//            logger.info(
//                    "real image {}  prediction {} status {}", reLabel, peLabel, peLabel.equals(reLabel));
        }
        iterator.reset();
        System.out.println(
                "validate result : sum count =" + sumCount + " correct count=" + correctCount);
    }
}
