/* *****************************************************************************
 * Copyright (c) 2015-2019 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ******************************************************************************/

package seq2seq;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.graph.rnn.DuplicateToTimeSeriesVertex;
import org.deeplearning4j.nn.conf.graph.rnn.LastTimeStepVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.conf.layers.recurrent.Bidirectional;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;


/**
 * Created by susaneraly on 3/27/16.
 */
public class BidirectionalLSTM {

    /*
        This example is modeled off the sequence to sequence RNNs described in http://arxiv.org/abs/1410.4615
        Specifically, a sequence to sequence NN is build for the addition operation. Addition is viewed as a translation task.
        For eg. "12+23 " = " 35" with "12+23 " as the input sequence to be translated to the output sequence " 35"
        For a general idea of seq2seq models refer to the image on Pg. 3 in the paper https://arxiv.org/pdf/1406.1078v3

        This example is build using a computation graph with RNN layers.
        Refer here for more details on computation graphs in dl4j
            https://deeplearning4j.org/compgraph
        And here for RNNs
            https://deeplearning4j.org/usingrnns

        There are two RNN layers to this computation graph. The inputs to them are as follows,
        During training:
            - encoder RNN layer:
                   Takes in the addition input string, eg. '12+13'
            - decoder RNN layer:
                   Takes in an input that combines the following two elements
                      1. The output of the very last time step of the encoder
                      2. The shifted 'correct' output of the addition (by appending with a "Go"), 'Go25 '

            which is then trained to fit to the output of the decoder RNN layer, eg '25 '

        During test the inputs are as follows:
            - encoder RNN layer:
                    Takes in the addition input string '12+13'
            - decoder RNN layer:
                   For a time step t takes in an input that combines the following two elements
                      1. The output of the very last time step of the encoder
                      2. The output of the decoder at time step, t-1; For t = 0 input to the decoder is merely "go"

        One hot vectors are used for encoding/decoding (length of one hot vector is 14 for 10 digits and "+"," ",beginning of string and end of string
        10 epochs give ~85% accuracy for 2 digits
        20 epochs give >95% accuracy for 2 digits

        To try out addition for numbers with different number of digits simply change "NUM_DIGITS"
     */

    static final int NUM_DIGITS = 2;
    //Random number generator seed, for reproducability
    public static final int seed = 1234;

    //Tweak these to tune the dataset size = batchSize * totalBatches
    public static int batchSize = 700;
    public static int nEpochs = 3000;

    //Tweak the number of hidden nodes
    private static final int numHiddenNodes = 512;//512;//1024;

    //This is the size of the one hot vector
    static final int FEATURE_VEC_SIZE = 48;

    public static void main(String[] args) throws IOException {

        //DataType is set to double for higher precision
        Nd4j.setDefaultDataTypes(DataType.DOUBLE, DataType.DOUBLE);

        //This is a custom iterator that returns MultiDataSets on each call of next - More details in comments in the class
        int totalBatches = 500;
        CustomSequenceIterator2 iterator = new CustomSequenceIterator2(seed, batchSize, totalBatches);

        ComputationGraphConfiguration configuration = new NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(0.25))
                .seed(seed)
                .graphBuilder()
                .addInputs("additionIn")
                .setInputTypes(InputType.recurrent(FEATURE_VEC_SIZE))
                .addLayer("bdlstm1", new Bidirectional(new LSTM.Builder().nIn(FEATURE_VEC_SIZE).nOut(numHiddenNodes).activation(Activation.TANH).build()), "additionIn")
                .addLayer("bdlstm2", new Bidirectional(new LSTM.Builder().nIn(numHiddenNodes*2).nOut(numHiddenNodes).activation(Activation.TANH).build()), "bdlstm1")
                .addVertex("merge", new MergeVertex(), "bdlstm2", "additionIn")
                .addLayer("output", new RnnOutputLayer.Builder().nIn(numHiddenNodes*2+FEATURE_VEC_SIZE).nOut(FEATURE_VEC_SIZE).activation(Activation.SOFTMAX).lossFunction(LossFunctions.LossFunction.MCXENT).build(), "merge")
                .setOutputs("output")
                .build();

        ComputationGraph net = new ComputationGraph(configuration);

        UIServer uiServer = UIServer.getInstance();
        //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
        StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later
        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
        uiServer.attach(statsStorage);

        net.init();
        net.setListeners(new ScoreIterationListener(1));
        net.setListeners(new StatsListener(statsStorage));

        String rootPath = System.getProperty("user.dir");
        String modelDirPath = rootPath + File.separatorChar + "out";
        String modelPath = modelDirPath + File.separatorChar + "_names";


        //Train model:
        int iEpoch = 0;
        int testSize = 100;
        Seq2SeqPredicter predictor = new Seq2SeqPredicter(net);
        while (iEpoch < nEpochs) {
            net.fit(iterator);
            System.out.printf("* = * = * = * = * = * = * = * = * = ** EPOCH %d ** = * = * = * = * = * = * = * = * = * = * = * = * = * =\n", iEpoch);
            MultiDataSet testData = iterator.generateTest(testSize);
            INDArray predictions = predictor.output(testData);
            encode_decode_eval(predictions, testData.getFeatures()[0], testData.getLabels()[0]);
            /*
            (Comment/Uncomment) the following block of code to (see/or not see) how the output of the decoder is fed back into the input during test time
            */
            System.out.println("Printing stepping through the decoder for a minibatch of size three:");
            testData = iterator.generateTest(3);
            predictor.output(testData, false);
            System.out.println("\n* = * = * = * = * = * = * = * = * = ** EPOCH " + iEpoch + " COMPLETE ** = * = * = * = * = * = * = * = * = * = * = * = * = * =");
            iEpoch++;

            //if (iEpoch % 10 == 0) {
                ModelSerializer.writeModel(net, modelPath + "_2-" + iEpoch + ".zip", true);
            //}
        }

    }

    private static void encode_decode_eval(INDArray predictions, INDArray questions, INDArray answers) {

        int nTests = (int) predictions.size(0);
        int wrong = 0;
        int correct = 0;
        String[] questionS = CustomSequenceIterator2.oneHotDecode(questions);
        String[] answersS = CustomSequenceIterator2.oneHotDecode(answers);
        String[] predictionS = CustomSequenceIterator2.oneHotDecode(predictions);
        for (int iTest = 0; iTest < nTests; iTest++) {
            if (!answersS[iTest].equals(predictionS[iTest])) {
                System.out.println(questionS[iTest] + " gives " + predictionS[iTest] + " != " + answersS[iTest]);
                wrong++;
            } else {
                System.out.println(questionS[iTest] + " gives " + predictionS[iTest] + " == " + answersS[iTest]);
                correct++;
            }
        }
        double randomAcc = Math.pow(10, -1 * (NUM_DIGITS + 1)) * 100;
        System.out.println("WRONG: " + wrong);
        System.out.println("CORRECT: " + correct);
        System.out.println("Note randomly guessing digits in succession gives lower than a accuracy of:" + randomAcc + "%");
        System.out.println("The digits along with the spaces have to be predicted\n");
    }

}

