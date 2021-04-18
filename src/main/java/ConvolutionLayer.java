import org.ejml.simple.SimpleMatrix;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.ArrayList;
import java.util.Random;

public class ConvolutionLayer {
    SimpleMatrix[] input;
    SimpleMatrix[] output;
    SimpleMatrix[] pooledOutput;
    SimpleMatrix[][] filters;
    int filterSize = 3;
    int stride =1;
    int poolingSize=2;
    int poolStride=2;
    int poolOutputSize;
    int outputSize;
    double learningRate = .001;


    public ConvolutionLayer(int NumFilters, int inputSize, int inputDepth){
        filters = new SimpleMatrix[NumFilters][inputDepth];
        output = new SimpleMatrix[NumFilters*inputDepth];
        pooledOutput = new SimpleMatrix[NumFilters*inputDepth];

        //Hmmmm not sure abt this

        Random r = new Random();

        for (int i = 0; i < NumFilters; i++) {
            for (int j = 0; j < inputDepth; j++) {
                filters[i][j] = SimpleMatrix.random_DDRM(filterSize, filterSize, -1, 1, r);
            }
        }

        outputSize = (inputSize - filterSize /* TODO + padding*/)/stride + 1;
        poolOutputSize = (outputSize - poolingSize /* TODO + padding*/)/poolStride + 1;
    }
    //TODO TEST
    public SimpleMatrix feedforward(SimpleMatrix[] input){
        this.input=input;

        //System.out.println(poolOutputSize);
        //take convultions of filter and patches and fill the output tensors
        int counter = 0;
        for(int f = 0; f < filters.length; f++) {
            for (int k = 0; k < input.length; k++) {
                output[counter] = new SimpleMatrix(outputSize, outputSize);

                for (int i = 0; i < output[f].numRows(); i++) {
                    for (int j = 0; j < output[f].numRows(); j ++) {
                            SimpleMatrix ex = input[k].extractMatrix(i * stride, i * stride + filterSize, j * stride, j * stride + filterSize);
                            output[counter].set(i, j, convolution(ex, filters[f][k]));
                    }
                }
                counter++;
            }
        }
        //perfrom relu on outputs and pool them
        for (int i = 0; i < pooledOutput.length; i++) {
            pooledOutput[i]=maxPool(RELU(output[i]));
            //pooledOutput[i].print();
        }
        //ToDo flatten
        return flatten(pooledOutput);
    }
    //TODO FINISh
    public void Train(SimpleMatrix input, SimpleMatrix[] target){
        //convert the flatten gradients from the fully connected layers to 2d matrices
        SimpleMatrix gradients[] = unflatten(input);
        // pass the gradients through the pooling layers
        SimpleMatrix depooledGradients[] = new SimpleMatrix[gradients.length];
        for (int i = 0; i < gradients.length; i++) {
            depooledGradients[i] = deMaxPool(gradients[i], output[i]);
        }
        //pass the gradients through the RELU layer
        SimpleMatrix diReluGradients[] = new SimpleMatrix[gradients.length];
        for (int i = 0; i < gradients.length; i++) {
            diReluGradients[i]=diRELU(depooledGradients[i]);
        }

        //pass gradients through the convultional layer
        SimpleMatrix[][] dL_dF = new SimpleMatrix[filters.length][target.length];
        for (int i = 0; i < dL_dF.length; i++) {
            for (int d = 0; d < target.length; d++) {
                dL_dF[i][d] = new SimpleMatrix(filters[0][d].numRows(), filters[0][d].numCols());
                for (int j = 0; j < dL_dF[i][d].numRows(); j++) {
                    for (int k = 0; k < dL_dF[i][d].numCols(); k++) {
                        SimpleMatrix ex = target[d].extractMatrix(j * stride, j * stride + filterSize, k * stride, k * stride + filterSize);
                        dL_dF[i][d].set(j, k, convolution(ex, diReluGradients[i]));
                    }
                }
            }
            for (int j = 0; j < target.length; j++) {
                dL_dF[i][j].scale(learningRate);
                filters[i][j].minus(dL_dF[i][j]);
            }

        }

    }
    private static SimpleMatrix diRELU(SimpleMatrix A){
        SimpleMatrix B = new SimpleMatrix(A.numRows(), A.numCols());
        for(int i = 0 ; i < A.numRows(); i++){
            for (int j = 0; j < A.numCols(); j++) {
                double x = A.get(i,j);
                if(x <= 0 ){
                    x=0;
                }else{
                    x=1;
                }
                B.set(i,j,x);
            }
        }
        return B;
    }
    public SimpleMatrix deMaxPool(SimpleMatrix input1, SimpleMatrix input2){
        //input1 is the matrix of gradients that have been obtained by deflatting the graients from the fully connected layer
        //input2 is the original prepooled output of the cnn layer
        SimpleMatrix output = new SimpleMatrix(input2.numRows(), input2.numCols());
        output.fill(0);
        int x=0,y=0;
        for (int i = 0; i < input1.numRows(); i++) {
            for (int j = 0; j < input1.numCols(); j++) {
                //find the max location
                double  max = Double.MIN_VALUE;
                for (int k = i*poolStride; k < i*poolStride+poolingSize; k++) {
                    for (int l = j*poolStride; l < j*poolStride+poolingSize ; l++) {
                        //TODO this is selecting the first max it finds and passes the gradient there, MAKE SURE the MaxPool Function does the same!!!!
                        if(input2.get(k,l) > max){
                            max = input2.get(k,l);
                            x=k;
                            y=l;
                        }
                    }

                output.set(x,y,input1.get(i,j));

                }
            }
        }
        return output;
    }
    //TODO Test
    public SimpleMatrix[] unflatten(SimpleMatrix input){
        SimpleMatrix[] gradients = new SimpleMatrix[filters.length];
        //int gradientSize = output[0].numRows()*output[0].numCols();
        int counter = 0;
        for (int i = 0; i < gradients.length; i++) {
            gradients[i] = new SimpleMatrix(poolOutputSize,poolOutputSize);
            for (int j = 0; j < gradients[0].numRows(); j++) {
                for (int k = 0; k < gradients[0].numCols(); k++) {
                    gradients[i].set(j,k,input.get(counter,0));
                    counter++;
                }
            }
        }
        return gradients;
    }
    //TODO TEST
    private SimpleMatrix RELU(SimpleMatrix A) {
        SimpleMatrix output = new SimpleMatrix(A.numRows(), A.numCols());
        for (int i = 0; i < A.numRows(); i++) {
            for (int j = 0; j < A.numCols(); j++) {
                if(A.get(i,j) > 0){
                    output.set(i,j, A.get(i,j));
                }else{
                    output.set(i,j, 0);
                }
            }
        }
        return output;
    }
    //TODO TEST
    private double convolution(SimpleMatrix filter, SimpleMatrix input){
        double total = 0;
        for(int i = 0; i < filter.numRows(); i++) {
            for (int j = 0; j < filter.numRows(); j++) {
                total += filter.get(i,j)*input.get(i,j);
            }
        }
        return total;
    }
    //TODO TEST
    private SimpleMatrix maxPool(SimpleMatrix input){
        SimpleMatrix output = new SimpleMatrix(poolOutputSize, poolOutputSize);
        for (int i = 0; i < poolOutputSize; i++) {
            for (int j = 0; j < poolOutputSize; j++) {
                //TODO feel weird might be incorrect
                SimpleMatrix ex = input.extractMatrix(i*poolStride, i*poolStride + poolingSize, j*poolStride, j*poolStride + poolingSize);
                output.set(i,j, Max(ex));
            }
        }
        return output;
    }
    //TODO TEST
    private double Max(SimpleMatrix input){
        double max = Integer.MIN_VALUE;
        for (int i = 0; i < input.numRows(); i++) {
            for (int j = 0; j < input.numCols(); j++) {
                max = Double.max(max, input.get(i,j));
            }
        }
        return max;
    }
    //TODO TEST
    public SimpleMatrix flatten(SimpleMatrix input[]){
        int outputNum = output.length*poolOutputSize*poolOutputSize;
        SimpleMatrix flat = new SimpleMatrix(outputNum, 1);
        int counter = 0;
        for (SimpleMatrix output: input) {
            //go row by column or column by row TODO???
            for (int i = 0; i < output.numRows(); i++) {
                for (int j = 0; j < output.numCols(); j++) {
                    flat.set(counter, 0, output.get(i,j));
                    counter++;
                }
            }
        }
        return flat;
    }
    public int getOutputSize(){
        return poolOutputSize*poolOutputSize*filters.length;
    }
}
