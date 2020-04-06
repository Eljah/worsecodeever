/*
 * CaptchaSetIterator.java
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

import org.datavec.image.transform.ImageTransform;

import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.MultiDataSet;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.MultiDataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;
import org.nd4j.linalg.indexing.NDArrayIndex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class CaptchaSetIterator2 implements DataSetIterator {
    private int batchSize = 0;
    private int batchNum = 0;
    private int numExample = 0;
    private CaptchaLoader load;
    private DataSetPreProcessor preProcessor;

    public CaptchaSetIterator2(int batchSize, String dataSetType) {
        this(batchSize, null, dataSetType);
    }

    public CaptchaSetIterator2(int batchSize, ImageTransform imageTransform, String dataSetType) {
        this.batchSize = batchSize;
        load = new CaptchaLoader(imageTransform, dataSetType);
        numExample = load.totalExamples();
        //System.out.println(batchSize + " batch size");
    }

    @Override
    public boolean hasNext() {
        if (batchNum < numExample-1) {
            System.out.println(batchNum+" batchNum");
            System.out.println(numExample+" numExamples");
            return true;
        } else {
            System.out.println(batchNum+" batchNum: false");
            System.out.println(numExample+" numExamples: false");
            return false;
        }
    }

    @Override
    public DataSet next() {
        return next(batchSize);
    }

    @Override
    public DataSet next(int i) {
        batchNum += i;
      //  System.out.println(i+" batch");
      //  System.out.println(batchNum+" batchNum");
        DataSet ds = load.nextDataSet(i);
        if (preProcessor != null) {
            preProcessor.preProcess(ds);
        }
        return ds;
    }

    @Override
    public int inputColumns() {
        return 60; //pixels in column
    }

    @Override
    public int totalOutcomes() {
        return 60; //possible digits//todo 10
    }

    @Override
    public boolean resetSupported() {
        return false;
    }

    @Override
    public boolean asyncSupported() {
        return true;
    }

    @Override
    public void reset() {
        batchNum = 0;
        load.reset();
    }

    @Override
    public int batch() {
        return batchNum;
    }

    @Override
    public void setPreProcessor(DataSetPreProcessor preProcessor) {
        this.preProcessor = preProcessor;
    }

    @Override
    public DataSetPreProcessor getPreProcessor() {
        return preProcessor;
    }

    @Override
    public List<String> getLabels() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forEachRemaining(Consumer<? super DataSet> action) {

    }

}
