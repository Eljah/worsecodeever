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
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;

class Main15LSTM {

    private static final Logger logger = LoggerFactory.getLogger(Main15LSTM.class);

    private static long seed = 123;
    private static int epochs = 3000; //50
    // 10 gave  up 100% coincide and  55% general coincide
    // 40 gave  up 100% coincide and  68% general coincide - without suppressing gradiens
    private static int batchSize = 200;
    private static String rootPath = System.getProperty("user.dir");

    private static String modelDirPath = rootPath + File.separatorChar + "out";
    private static String modelPath = modelDirPath + File.separatorChar + "model15";

    private static int lstmLayerSize = 200; //20
    int miniBatchSize = 320;
    private static int tbpttLength = 30;

    public static void main(String[] args) throws Exception {

        long startTime = System.currentTimeMillis();
        logger.info("start up time: " + startTime);

        File modelDir = new File(modelDirPath);

        // create dir
        boolean hasDir = modelDir.exists() || modelDir.mkdirs();
        logger.info(modelPath);

        // create model
        ComputationGraph model = createModel();
        // monitor the model score
        //UIServer uiServer = UIServer.getInstance();
        //StatsStorage statsStorage = new InMemoryStatsStorage();
        //uiServer.attach(statsStorage);


//        DataSetIterator trainMulIterator = new CaptchaSetIterator2(batchSize, "train");
//        DataSetIterator testMulIterator = new CaptchaSetIterator2(batchSize, "test");
//        DataSetIterator validateMulIterator = new CaptchaSetIterator2(batchSize, "validate");
        // construct the iterator
//        MultiDataSetIterator trainMulIterator = new CaptchaSetIterator3(batchSize, "train");
//        MultiDataSetIterator testMulIterator = new CaptchaSetIterator3(batchSize, "test");
//        MultiDataSetIterator validateMulIterator = new CaptchaSetIterator3(batchSize, "validate");

        //MultiDataSetIterator trainMulIterator = new CaptchaSetIterator4(batchSize, "outdebug");
        MultiDataSetIterator trainMulIterator = new CaptchaSetIterator7(batchSize, "ownfont\\out");
        MultiDataSetIterator testMulIterator = new CaptchaSetIterator7(1, "ownfont\\test");
        MultiDataSetIterator testMulIterator2 = new CaptchaSetIterator7(1, "ownfont\\test");
        //MultiDataSetIterator testMulIterator = new CaptchaSetIterator5(1, "outtest");
        MultiDataSetIterator validateMulIterator = new CaptchaSetIterator7(batchSize, "out");
//        // fit
        for (int i = 0; i < epochs; i++) {
            System.out.println("Epoch=====================" + i);
            model.fit(trainMulIterator);
            trainMulIterator.reset(); //todo NOTHEWORTHy!!!
            ModelSerializer.writeModel(model, modelPath+"-"+i+".zip", true);
            modelPredict(model, testMulIterator);
        }
        ModelSerializer.writeModel(model, modelPath+"-final.zip", true);
        long endTime = System.currentTimeMillis();
        System.out.println("=============run time=====================" + (endTime - startTime));

        System.out.println("=====eval model=====test same data==================");
        modelPredict(model, testMulIterator);

        System.out.println("=====eval model=====test true==================");
        modelPredict(model, testMulIterator2);

        //System.out.println("=====eval model=====validate==================");
        //modelPredict(model, validateMulIterator);
    }

    public static ComputationGraph createModel() {

        UIServer uiServer = UIServer.getInstance();
        //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
        StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later
        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
        uiServer.attach(statsStorage);

        ComputationGraphConfiguration config =
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
                        .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                        .graphBuilder()
                        .addInputs("trainFeatures")
                        .setInputTypes(InputType.recurrent(1))
                        .setOutputs("out1", "out2", "out3", "out4", "out5", "out6")
                        .addLayer(
                                "lstmPre",
                                new LSTM.Builder().nIn(60).nOut(lstmLayerSize)
                                        .activation(Activation.TANH).build(),
                                "trainFeatures")
                        .addLayer(
                                "lstm",
                                new LSTM.Builder().nIn(lstmLayerSize).nOut(lstmLayerSize)
                                        .activation(Activation.TANH).build(),
                                "lstmPre")

                        .addLayer(
                                "out1",
                                new RnnOutputLayer.Builder(LossFunctions.LossFunction.XENT)
                                        .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                                        .gradientNormalizationThreshold(10)
                                        .activation(Activation.SIGMOID).nIn(lstmLayerSize).nOut(10).build(),
                                "lstm")
                        .addLayer(
                                "out2",
                                new RnnOutputLayer.Builder(LossFunctions.LossFunction.XENT)
                                        .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                                        .gradientNormalizationThreshold(10)
                                        .activation(Activation.SIGMOID).nIn(lstmLayerSize).nOut(10).build(),
                                "lstm")
                        .addLayer(
                                "out3",
                                new RnnOutputLayer.Builder(LossFunctions.LossFunction.XENT)
                                        .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                                        .gradientNormalizationThreshold(10)
                                        .activation(Activation.SIGMOID).nIn(lstmLayerSize).nOut(10).build(),
                                "lstm")
                        .addLayer(
                                "out4",
                                new RnnOutputLayer.Builder(LossFunctions.LossFunction.XENT)
                                        .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                                        .gradientNormalizationThreshold(10)
                                        .activation(Activation.SIGMOID).nIn(lstmLayerSize).nOut(10).build(),
                                "lstm")
                        .addLayer(
                                "out5",
                                new RnnOutputLayer.Builder(LossFunctions.LossFunction.XENT)
                                        .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                                        .gradientNormalizationThreshold(10)
                                        .activation(Activation.SIGMOID).nIn(lstmLayerSize).nOut(10).build(),
                                "lstm")
                        .addLayer(
                                "out6",
                                new RnnOutputLayer.Builder(LossFunctions.LossFunction.XENT)
                                        .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                                        .gradientNormalizationThreshold(10)
                                        .activation(Activation.SIGMOID).nIn(lstmLayerSize).nOut(10).build(),
                                "lstm")
                        //.pretrain(false)
                        //.backprop(true)
                        //.backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength(tbpttLength).tBPTTBackwardLength(tbpttLength)
                        //.backpropType(BackpropType.Standard)
                        .build();
                        //.graphBuilder()
                        //.setOutputs("out1", "out2", "out3", "out4", "out5", "out6")
//                        .list()
//                        .layer(new LSTM.Builder().nIn(60).nOut(lstmLayerSize)
//                                .activation(Activation.TANH).build())
//                        .layer(new LSTM.Builder().nIn(lstmLayerSize).nOut(lstmLayerSize)
//                                .activation(Activation.TANH).build())
//                        .layer(new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT).activation(Activation.SOFTMAX)        //MCXENT + softmax for classification
//                                //.layer(new RnnOutputLayer.Builder(LossFunctions.LossFunction.SQUARED_LOSS).activation(Activation.RELU)        //MCXENT + softmax for classification
//                                .nIn(lstmLayerSize).nOut(10).build())
//                        //.nIn(lstmLayerSize).nOut(1).build())
//                        .build();

        // Construct and initialize model
        //ComputationGraph model = new MultiLayerConfiguration(config);
        ComputationGraph model = new ComputationGraph(config);

        //model.setListeners(new ScoreIterationListener(36), new StatsListener(statsStorage));
        model.init();
        model.setListeners(new ScoreIterationListener(1));
        model.setListeners(new StatsListener(statsStorage));

        return model;
    }

    public static void modelPredict(ComputationGraph model, MultiDataSetIterator iterator) {
        int sumCount = 0;
        int correctCount = 0;

        List<String> labelList =
                Arrays.asList(
                        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
//                , "A", "B", "C", "D", "E", "F", "G",
//            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
//            "Y", "Z"
                );

        int generalCoincide = 0;
        int generalSamples = 0;
        while (iterator.hasNext()) {
            MultiDataSet mds = iterator.next();
            INDArray[] output = model.output(mds.getFeatures());
            INDArray[] labels = mds.getLabels();
            //System.out.println(Arrays.toString(output[0].shape()));
            for (int j = 0; j< 6; j++) {
                //System.out.println("originalout:\n"+output[j]);
                //output[j] = output[j].get(NDArrayIndex.point(0),NDArrayIndex.all(),NDArrayIndex.interval(199,200)).transpose();
                output[j] = output[j].get(NDArrayIndex.point(0),NDArrayIndex.all(),NDArrayIndex.interval(30,30,181)).transpose();
                //System.out.println(Arrays.toString(output[j].shape()));
                //System.out.println("out\n"+output[j]);
            }
            System.out.println(Arrays.toString(labels[0].shape()));
            for (int j = 0; j< 6; j++) {
                //System.out.println("labelBefore\n"+labels[j]);
                //labels[j] = labels[j].get(NDArrayIndex.point(0),NDArrayIndex.all(),NDArrayIndex.interval(199,200)).transpose();
                labels[j] = labels[j].get(NDArrayIndex.point(0),NDArrayIndex.all(),NDArrayIndex.interval(30,30,181)).transpose();
                //System.out.println(Arrays.toString(labels[j].shape()));
                //System.out.println("label\n"+labels[j]);
            }
            int dataNum = batchSize > output[0].rows() ? output[0].rows() : batchSize;
            int coincideIndex = 0;
            //for (int dataIndex = 0; dataIndex < dataNum; dataIndex=dataIndex+30) {
                String reLabel = "";
                String peLabel = "";
                INDArray preOutput = null;
                INDArray realLabel = null;
                for (int digit = 0; digit < 6; digit++) {
                    //preOutput = output[digit].getRow(dataIndex);
                    //preOutput = output[digit].getRow(dataIndex, true);
                    preOutput = output[digit].getRow(digit, true);
                    //System.out.println("Pre-out\n"+preOutput);
                    String pre = labelList.get(Nd4j.argMax(preOutput, 1).getInt(0));
                    //String pre = labelList.get(Nd4j.argMax(preOutput, dataNum).getInt(0));
                    peLabel += pre;

                    //realLabel = labels[digit].getRow(dataIndex);
                    //System.out.println("Real-label full\n"+labels[digit]);
                    //realLabel = labels[digit].getRow(dataIndex, true);
                    realLabel = labels[digit].getRow(digit, true);
                    //System.out.println("Real-label\n"+realLabel);
                    String lab = labelList.get(Nd4j.argMax(realLabel, 1).getInt(0));
                    reLabel += lab;

                    if (pre.equals(lab)) {
                        coincideIndex++;
                        generalCoincide++;
                    }
                    generalSamples++;
                }
                if (peLabel.equals(reLabel)) {
                    correctCount++;
                }
                sumCount++;
                logger.info(
                        "real image {}  prediction {} status {} coincide {}%", reLabel, peLabel, peLabel.equals(reLabel), (int)(coincideIndex*100/6));
            //}
        }
        iterator.reset();
        System.out.println(
                "validate result : sum count =" + sumCount + " correct count=" + correctCount + "general coincide "+ (int)(generalCoincide*100/generalSamples)+"%");
    }
}
