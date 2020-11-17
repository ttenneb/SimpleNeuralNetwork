import org.ejml.simple.SimpleMatrix;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.Random;

public class SimpleNeuralNetwork {
    int inputSize;
    int hiddenSize;
    int ouputSize;
    int numHidden;
    static int dead = 0;

    double learningRate = .0002;

    SimpleMatrix[] weights;
    SimpleMatrix[] bias;
    public SimpleNeuralNetwork(int inputSize, int numOfHiddenLayers, int HiddenLayerSize, int outputSize){
        this.inputSize=inputSize;
        this.hiddenSize=HiddenLayerSize;
        this.ouputSize=outputSize;
        this.numHidden = numOfHiddenLayers;

        Random rand = new Random();

        this.weights = new SimpleMatrix[numOfHiddenLayers+1];
        this.bias = new SimpleMatrix[numOfHiddenLayers+1];

        weights[0] = SimpleMatrix.random_DDRM(hiddenSize, this.inputSize, -1, 1,rand);
        //System.out.println(weights[0].toString());

        if (weights.length > 2)
            for(int i = 1; i < weights.length-1; i++){

                weights[i] = SimpleMatrix.random_DDRM(hiddenSize, hiddenSize, -1, 1,rand);
                //System.out.println(weights[i].toString());
            }

        weights[weights.length-1]=SimpleMatrix.random_DDRM(ouputSize, hiddenSize, -1, 1,rand);
        //System.out.println(weights[weights.length-1].toString());

        for(int i = 0; i < bias.length-1; i++){
            bias[i] = new SimpleMatrix(hiddenSize, 1);
            bias[i].fill(0);
        }
        bias[numHidden] = new SimpleMatrix(outputSize, 1);
        bias[numHidden].fill(0);
    }
    public Output feedforward(SimpleMatrix input) {
        SimpleMatrix[] hidden = new SimpleMatrix[numHidden];
        SimpleMatrix output = new SimpleMatrix(ouputSize, 1);
        hidden[0]=RELU(weights[0].mult(input).plus(bias[0]));
        for( int i = 1; i < hidden.length; i++){
            hidden[i] = RELU(weights[i].mult(hidden[i-1]).plus(bias[i]));
        }
        output=softmax(weights[numHidden].mult(hidden[hidden.length-1]).plus(bias[numHidden]));
        String[] key = {"0", "1", "2", "3", "4","5","6","7","8","9"};
        if(containsNAN(output)){
            System.out.println("GUh");
        }
        return new Output(output, key, hidden);
    }
    public void train(SimpleMatrix input, SimpleMatrix target){
        Output Output = feedforward(input);
        SimpleMatrix error[] = new SimpleMatrix[numHidden+1];
        SimpleMatrix gradient[] = new SimpleMatrix[numHidden+1];
        SimpleMatrix deltaM[] = new SimpleMatrix[numHidden+1];

        //calculate errors
        //error[0] = Output.output.elementLog().elementMult(target).scale(-1);
        error[0] = target.minus(Output.output);

        for(int i = 1; i < error.length; i++){
            error[i]=weights[weights.length-i].transpose().mult(error[i-1]);
        }
        //for(SimpleMatrix m : error){
          ///  m.print();
        //}
        //calculate gradients
        gradient[0]=error[0].elementMult(diRELU(Output.output)).scale(learningRate);

        deltaM[0] = gradient[0].mult(Output.hidden[numHidden-1].transpose());
        for(int i = 1; i < gradient.length-1; i++){
            gradient[i]=error[i].elementMult(diRELU(Output.hidden[numHidden-i-1])).scale(learningRate);
            deltaM[i]=gradient[i].mult(Output.hidden[numHidden-i-1].transpose());
        }
        gradient[numHidden]=error[numHidden].elementMult(diRELU(Output.hidden[0])).scale(learningRate);
        deltaM[numHidden]=gradient[numHidden].mult(input.transpose());

        for(int i = 0; i < gradient.length; i++){
            bias[i]=bias[i].plus(gradient[numHidden-i]);
            weights[i]=weights[i].plus(deltaM[numHidden-i]);
        }

    }
    private static SimpleMatrix sigmoid(SimpleMatrix A){
        SimpleMatrix B = new SimpleMatrix(A.numRows(),A.numCols());

        B.fill(Math.exp(1));
        B = B.elementPower(A.scale(-1));
        B=B.plus(1);

        SimpleMatrix C=new SimpleMatrix(A.numRows(),A.numCols());

        C.fill(1);
        B=C.elementDiv(B);
        return B;

    }
    private static SimpleMatrix disigmoid(SimpleMatrix A){
        SimpleMatrix B = A.scale(-1).plus(1);
        //A.print();
        //B.print();
        return A.elementMult(B);
    }
    private static SimpleMatrix RELU(SimpleMatrix A){
        SimpleMatrix B = new SimpleMatrix(A.numRows(), 1);
        for(int i = 0 ; i < A.numRows(); i++){
            double x = A.get(i,0);
            if(x <= 0 ){
                x=0;
            }
            B.set(i,0,x);
        }
        return B;
    }
    private static SimpleMatrix diRELU(SimpleMatrix A){
        SimpleMatrix B = new SimpleMatrix(A.numRows(), 1);
        for(int i = 0 ; i < A.numRows(); i++){
            double x = A.get(i,0);
            if(x <= 0 ){
                x=0;
            }else{
                x=1;
            }
            B.set(i,0,x);
        }
        return B;
    }
    private static SimpleMatrix LRELU(SimpleMatrix A){
        SimpleMatrix B = new SimpleMatrix(A.numRows(), 1);
        for(int i = 0 ; i < A.numRows(); i++){
            double x = A.get(i,0);
            if(x <= 0 ){
                x=x*.1;
            }
            B.set(i,0,x);
        }
        return B;
    }
    private static SimpleMatrix diLRELU(SimpleMatrix A){
        SimpleMatrix B = new SimpleMatrix(A.numRows(), 1);
        for(int i = 0 ; i < A.numRows(); i++){
            double x = A.get(i,0);
            if(x <= 0 ){
                x=.1;
            }else{
                x=1;
            }
            B.set(i,0,x);
        }
        return B;
    }
    private static SimpleMatrix softmax(SimpleMatrix A){
        double total = 0;
        SimpleMatrix output = new SimpleMatrix(A.numRows(), 1);
        double max = 0;
        for(int i = 0; i < A.numRows(); i++){
            if(A.get(i,0) > max)
                max = A.get(i,0);
        }
        A=A.minus(max);
        for(int i = 0; i < A.numRows(); i++){
            total += Math.exp(A.get(i,0));
        }
        total = Math.max(total, Math.pow(10,-9));
        for(int i = 0; i < A.numRows(); i++){
            output.set(i, 0, Math.exp(A.get(i,0))   /total);
        }
        return output;
    }
    private static SimpleMatrix dsoftmax(SimpleMatrix A, SimpleMatrix T){
        SimpleMatrix output = new SimpleMatrix(A.numRows(), 1);
        output=T.minus(A);
        return output;
    }
    private static boolean containsNAN(SimpleMatrix A){
        for(int i = 0; i < A.numRows(); i++){
            if(Double.isNaN(A.get(i,0)))
                return true;
        }
        return false;
    }

}
