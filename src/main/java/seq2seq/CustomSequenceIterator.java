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

import org.apache.commons.lang.ArrayUtils;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.MultiDataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by susaneraly on 3/27/16.
 * This is class to generate a multidataset from the AdditionRNN problem
 * Features of the multidataset
 *      - encoder input, eg. "12+13" and
 *      - decoder input, eg. "Go25 " for training and "Go   " for test
 * Labels of the multidataset
 *      - decoder output, "25 End"
 * These strings are encoded as one hot vector sequences.
 *
 * Sequences generated during test are never before seen by the net
 * The random number generator seed is used for repeatability so that each reset of the iterator gives the same data in the same order.
 */
public class CustomSequenceIterator implements MultiDataSetIterator {

    private Random randnumG;
    private final int seed;
    private final int batchSize;
    private final int totalBatches;

    private static final int numDigits = AdditionRNN.NUM_DIGITS;
    private static final int SEQ_VECTOR_DIM = AdditionRNN.FEATURE_VEC_SIZE;
    private static final Map<String, Integer> oneHotMap = new HashMap<>();
    private static final String[] oneHotOrder = new String[SEQ_VECTOR_DIM];

    private Set<String> seenSequences = new HashSet<>();
    private boolean toTestSet = false;
    private int currentBatch = 0;
    private List<String> names = null;
    private ListIterator<String> namesIterator = null;
    private int largerstWordSize = 0;

    CustomSequenceIterator(int seed, int batchSize, int totalBatches) {



        this.names = new ArrayList<>();
        try (FileReader f = new FileReader("name.csv")) {
            StringBuffer sb = new StringBuffer();
            while (f.ready()) {
                char c = (char) f.read();
                if (c == '\n') {
                    names.add(sb.toString());
                    sb = new StringBuffer();
                } else {
                    sb.append(c);
                }
            }
            if (sb.length() > 0) {
                names.add(sb.toString().trim());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String name: names) {
            String[] split = name.split(",");
            if (split[1].length() > largerstWordSize) largerstWordSize = split[1].length();
            if (split[2].length() > largerstWordSize) largerstWordSize = split[2].length();
        }

        System.out.println("Largest word size: "+largerstWordSize);

        this.seed = seed;
        this.randnumG = new Random(seed);

        this.batchSize = batchSize;
        this.totalBatches = totalBatches;

        oneHotEncoding();
    }

    MultiDataSet generateTest(int testSize) {
        toTestSet = true;
        MultiDataSet testData = next(testSize);
        reset();
        return testData;
    }

    @Override
    public MultiDataSet next(int sampleSize) {

        Collections.shuffle(names);
        namesIterator = names.listIterator();

        INDArray encoderSeq, decoderSeq, outputSeq;
        int currentCount = 0;
        int num1, num2;
        List<INDArray> encoderSeqList = new ArrayList<>();
        List<INDArray> decoderSeqList = new ArrayList<>();
        List<INDArray> outputSeqList = new ArrayList<>();
        while (currentCount < sampleSize && namesIterator.hasNext()) {

            String newLine = namesIterator.next();
            //System.out.println(newLine);

            String rus=newLine.split(",")[2];
            String tat=newLine.split(",")[1];
            while (rus.length()<largerstWordSize) {
                rus = rus + " ";
            };
            while (tat.length()<largerstWordSize) {
                tat = tat + " ";
            }
            //rus = rus + " ";

            String[] encoderInput = rus.split("");//prepToString(num1, num2);
            encoderSeqList.add(mapToOneHot(encoderInput));

            String[] decoderInput = (String[]) ArrayUtils.addAll(new String[]{"Go"},tat.split(""));//prepToString(num1 + num2, true);
            if (toTestSet) {
                //wipe out everything after "go"; not necessary since we do not use these at test time but here for clarity
                int i = 1;
                while (i < decoderInput.length) {
                    decoderInput[i] = " ";
                    i++;
                }
            }
            decoderSeqList.add(mapToOneHot(decoderInput));

            String[] decoderOutput = (String[]) ArrayUtils.addAll(tat.split(""),new String[]{"End"});//prepToString(num1 + num2, false);
            outputSeqList.add(mapToOneHot(decoderOutput));
            currentCount++;
        }

        encoderSeq = Nd4j.vstack(encoderSeqList);
        decoderSeq = Nd4j.vstack(decoderSeqList);
        outputSeq = Nd4j.vstack(outputSeqList);

        INDArray[] inputs = new INDArray[]{encoderSeq, decoderSeq};
        INDArray[] inputMasks = new INDArray[]{Nd4j.ones(sampleSize, numDigits * 2 + 1), Nd4j.ones(sampleSize, numDigits + 1 + 1)};
        INDArray[] labels = new INDArray[]{outputSeq};
        INDArray[] labelMasks = new INDArray[]{Nd4j.ones(sampleSize, numDigits + 1 + 1)};
        currentBatch++;
        //return new org.nd4j.linalg.dataset.MultiDataSet(inputs, labels, inputMasks, labelMasks);
        return new org.nd4j.linalg.dataset.MultiDataSet(inputs, labels);
    }

    @Override
    public void reset() {
        currentBatch = 0;
        toTestSet = false;
        seenSequences = new HashSet<>();
        randnumG = new Random(seed);
    }

    @Override
    public boolean resetSupported() {
        return true;
    }

    @Override
    public boolean asyncSupported() {
        return false;
    }

    @Override
    public boolean hasNext() {
        return currentBatch < totalBatches;
    }

    @Override
    public MultiDataSet next() {
        return next(batchSize);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public MultiDataSetPreProcessor getPreProcessor() {
        return null;
    }

    /*
        Helper method for encoder input
        Given two numbers, num1 and num, returns a string array which represents the input to the encoder RNN
        Note that the string is padded to the correct length and reversed
        Eg. num1 = 7, num 2 = 13 will return {"3","1","+","7"," "}
     */
    private String[] prepToString(int num1, int num2) {

        String[] encoded = new String[numDigits * 2 + 1];
        StringBuilder num1S = new StringBuilder(String.valueOf(num1));
        StringBuilder num2S = new StringBuilder(String.valueOf(num2));
        //padding
        while (num1S.length() < numDigits) {
            num1S.insert(0, " ");
        }
        while (num2S.length() < numDigits) {
            num2S.insert(0, " ");
        }

        String sumString = num1S + "+" + num2S;

        for (int i = 0; i < encoded.length; i++) {
            encoded[(encoded.length - 1) - i] = Character.toString(sumString.charAt(i));
        }

        return encoded;

    }

    /*
        Helper method for decoder input when goFirst
                      for decoder output when !goFirst
        Given a number, return a string array which represents the decoder input (or output) given goFirst (or !goFirst)

        eg. For numDigits = 2 and sum = 31
                if goFirst will return  {"go","3","1", " "}
                if !goFirst will return {"3","1"," ","eos"}

     */
    private String[] prepToString(int sum, boolean goFirst) {
        int start, end;
        String[] decoded = new String[numDigits + 1 + 1];
        if (goFirst) {
            decoded[0] = "Go";
            start = 1;
            end = decoded.length - 1;
        } else {
            start = 0;
            end = decoded.length - 2;
            decoded[decoded.length - 1] = "End";
        }

        String sumString = String.valueOf(sum);
        int maxIndex = start;
        //add in digits
        for (int i = 0; i < sumString.length(); i++) {
            decoded[start + i] = Character.toString(sumString.charAt(i));
            maxIndex ++;
        }

        //needed padding
        while (maxIndex <= end) {
            decoded[maxIndex] = " ";
            maxIndex++;
        }
        return decoded;

    }

    /*
        Takes in an array of strings and return a one hot encoded array of size 1 x 14 x timesteps
        Each element in the array indicates a time step
        Length of one hot vector = 14
     */
    private static INDArray mapToOneHot(String[] toEncode) {

        INDArray ret = Nd4j.zeros(1, SEQ_VECTOR_DIM, toEncode.length);
        for (int i = 0; i <
                toEncode.length;
             i++) {
            //System.out.println(Arrays.toString(toEncode)+": "+toEncode[i]);
            ret.putScalar(0,
                    oneHotMap.get(
                            toEncode[i]
                    ),
                    i,
                    1);
        }

        return ret;
    }

    static String mapToString(INDArray encodeSeq, INDArray decodeSeq) {
        StringBuilder ret = new StringBuilder();
        String [] encodeSeqS = oneHotDecode(encodeSeq);
        String [] decodeSeqS = oneHotDecode(decodeSeq);
        for (int i=0; i<encodeSeqS.length;i++) {
            ret.append("\t").append(encodeSeqS[i]).append(" +  ").append(decodeSeqS[i]).append("\n");
        }
        return ret.toString();
    }

    /*
        Helper method that takes in a one hot encoded INDArray and returns an interpreted array of strings
        toInterpret size batchSize x one_hot_vector_size(14) x time_steps
     */
    static String[] oneHotDecode(INDArray toInterpret) {

        String[] decodedString = new String[(int)toInterpret.size(0)];
        INDArray oneHotIndices = Nd4j.argMax(toInterpret, 1); //drops a dimension, so now a two dim array of shape batchSize x time_steps
        for (int i = 0; i < oneHotIndices.size(0); i++) {
            int[] currentSlice = oneHotIndices.slice(i).dup().data().asInt(); //each slice is a batch
            decodedString[i] = mapFromOneHot(currentSlice);
        }
        return decodedString;
    }

    private static String mapFromOneHot(int[] toMap) {
        StringBuilder ret = new StringBuilder();
        for (int value : toMap) {
            ret.append(oneHotOrder[value]);
        }
        //encoder sequence, needs to be reversed
        if (toMap.length > numDigits + 1 + 1) {
            return new StringBuilder(ret.toString()).reverse().toString();
        }
        return ret.toString();
    }

    /*
    One hot encoding map
    */
    private static void oneHotEncoding() {
        String alphabet = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяәүөҗһң";
        for (int i = 0; i < 39; i++) {
            oneHotOrder[i] = alphabet.substring(i,i+1);
            oneHotMap.put(alphabet.substring(i,i+1), i);
        }
        oneHotOrder[39] = " ";
        oneHotMap.put(" ", 39);

        oneHotOrder[40] = "+";
        oneHotMap.put("+", 40);

        oneHotOrder[41] = "Go";
        oneHotMap.put("Go", 41);

        oneHotOrder[42] = "End";
        oneHotMap.put("End", 42);

    }

    public void setPreProcessor(MultiDataSetPreProcessor preProcessor) {
    }
}
