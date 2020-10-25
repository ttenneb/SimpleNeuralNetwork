
import org.ejml.simple.SimpleMatrix;

import java.awt.event.KeyEvent;
import java.io.*;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Manager extends Game {
    static Engine engine;
    static SimpleMatrix[] trainingData;
    static SimpleMatrix[] targetData;
    static SimpleMatrix[] testingData;
    static SimpleMatrix[] testingTargets;
    static String[] testingAnswers;

    static SimpleMatrix drawnInput = new SimpleMatrix(784,1);
    static Output drawnOutput;

    static int currentTest= 0;
    static boolean pressed = false;
    static boolean[] results;
    static Output output;
    static SimpleNeuralNetwork nn = new SimpleNeuralNetwork(784, 2, 64, 10);
    static int count = 0;
    public static void main(String args[]) throws IOException {
        Random random = new Random();
        //XOR
        /*training[0] = new SimpleMatrix(2, 1);
        training[0].set(0,0,0);
        training[0].set(1,0,0);
        training[1] = new SimpleMatrix(2, 1);
        training[1].set(0,0,1);
        training[1].set(1,0,0);
        training[2] = new SimpleMatrix(2, 1);
        training[2].set(0,0,0);
        training[2].set(1,0,1);
        training[3] = new SimpleMatrix(2, 1);
        training[3].set(0,0,1);
        training[3].set(1,0,1);

        SimpleMatrix[] target = new SimpleMatrix[4];
        target[0] = new SimpleMatrix(1,1);
        target[0].set(0,0,0);
        target[1] = new SimpleMatrix(1,1);
        target[1].set(0,0,1);
        target[2] = new SimpleMatrix(1,1);
        target[2].set(0,0,1);
        target[3] = new SimpleMatrix(1,1);
        target[3].set(0,0,0);
       Random random = new Random();
        for(int i = 0; i < 500000; i++) {
            int index= random.nextInt(4);
            nn.train(training[index], target[index]);
        }
        for(int i = 0; i < training.length; i++) {
            Output A = nn.feedforward(training[i]);
            A.output.print();
        }*/

        //Cross Classification
        /*int tSize= 500;
        trainingData = new SimpleMatrix[tSize];
        targetData=new SimpleMatrix[tSize];
        testingData= new SimpleMatrix[tSize];
        testingTargets = new SimpleMatrix[tSize];
        for(int i = 0 ; i < trainingData.length; i++){
            trainingData[i]= new SimpleMatrix(3,1);
            targetData[i] = new SimpleMatrix(2,1);
            testingData[i]= new SimpleMatrix(3,1);
            testingTargets[i] = new SimpleMatrix(2,1);

            targetData[i].fill(0);
            testingTargets[i].fill(0);

            //create training data
            int a =random.nextInt(320)-160;
            int b = random.nextInt(240)-120;
            trainingData[i].set(0,0, a);
            trainingData[i].set(1,0, b);
            trainingData[i].set(2,0,a*b);
            //create trainging targets
            if(a>0){
                if(b<0)
                    targetData[i].set(0,0,1); //quad 1
                else
                    targetData[i].set(1,0,1); // quad 4
            }else{
                if(b<0)
                    targetData[i].set(1,0,1); // quad 2
                else
                    targetData[i].set(0,0,1); // quad 3
            }

            //create testing data
            a =random.nextInt(320)-160;
            b = random.nextInt(240)-120;
            testingData[i].set(0,0, a);
            testingData[i].set(1,0, b);
            testingData[i].set(2,0,a*b);

            //create testing targets
            if(a>0){
                if(b<0)
                    testingTargets[i].set(0,0,1); //quad 1
                else
                    testingTargets[i].set(1,0,1); // quad 4
            }else{
                if(b<0)
                    testingTargets[i].set(1,0,1); // quad 2
                else
                    testingTargets[i].set(0,0,1); // quad 3
            }

        }

        //Training
        for(int i = 0; i < 20000; i++){
            int index = random.nextInt(tSize);
            nn.train(trainingData[index], targetData[index]);
        }
        SimpleMatrix a = new SimpleMatrix(2,1);
        a.set(0,0,100);
        a.set(1,0,50);

        results = new boolean[tSize];
        //collect results on trained data
        for(int i = 0 ; i < results.length; i++){
            Output A = nn.feedforward(testingData[i]);
            if(testingTargets[i].get(0,0) == 1 && A.output.get(0,0) > A.output.get(1,0)){
                results[i] = true;
            }
            else if(testingTargets[i].get(1,0) == 1 && A.output.get(0,0) < A.output.get(1,0)){
                results[i] = true;
            }else
            results[i] = false;
        }*/

        //MINST
        trainingData = new SimpleMatrix[60000];
        targetData = new SimpleMatrix[60000];
        testingData = new SimpleMatrix[10000];
        testingTargets = new SimpleMatrix[10000];
        testingAnswers = new String[10000];

        InputStream A = Manager.class.getResourceAsStream("/mnist_train.csv");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(A))) {
            String line;
            int index = 0;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                targetData[index] = new SimpleMatrix(10, 1);
                targetData[index].fill(0);
                targetData[index].set(Integer.parseInt(values[0]), 0, 1);

                trainingData[index] = new SimpleMatrix(784, 1);
                for(int i = 1; i < values.length; i++){
                  trainingData[index].set(i-1,0,(Double.parseDouble(values[i])/127.5)-1);
                }
                index++;
            }
        }

        A = Manager.class.getResourceAsStream("/mnist_test.csv");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(A))) {
            String line;
            int index = 0;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                testingTargets[index] = new SimpleMatrix(10, 1);
                testingTargets[index].fill(0);
                testingTargets[index].set(Integer.parseInt(values[0]), 0, 1);
                testingAnswers[index] = values[0];

                testingData[index] = new SimpleMatrix(784, 1);
                for(int i = 1; i < values.length; i++){
                  testingData[index].set(i-1,0,(Double.parseDouble(values[i])/127.5)-1);
                }
                index++;
            }

        }
        int trainLength = 5000000;
        for(int i = 0; i < trainLength; i++){
            int index = random.nextInt(60000);
            nn.train(trainingData[index], targetData[index]);
            System.out.println(100*((double)i/(double)trainLength) + "%");
        }
        int totalCorrect=0;
        for(int i = 0 ; i < testingData.length; i++){
            Output y = nn.feedforward(testingData[i]);
            if(y.prediction.equals(testingAnswers[i]))
                totalCorrect++;
        }



        System.out.println("Correct: " + totalCorrect + " / Percentage: " + 100*(double)totalCorrect/10000+"%");
        System.out.println(SimpleNeuralNetwork.dead);

        drawnInput.fill(0);

        engine = new Engine(new Manager());
        engine.start();
    }

    @Override
    public void update(Input i) {
        if(i.isKey(KeyEvent.VK_SPACE)){
            if(!pressed) {
                currentTest++;
                output = nn.feedforward(testingData[currentTest]);
                if (output.prediction.equals(testingAnswers[currentTest]))
                    count++;
               // output.output.print();
                pressed = true;
            }
        }else{
            pressed=false;
        }
        int size = 2;
        if(i.isMB(1 )&& !i.isKey(KeyEvent.VK_E)){
            if(i.getMouseX()>150 && i.getMouseX() < 179 && i.getMouseY()>150 && i.getMouseY() < 179){
                int index = (i.getMouseY() - 150) * 28 + i.getMouseX() - 150;
                if(index > 783){
                    index = 783;
                }
                for(int j = 0; j < size; j++) {
                    drawnInput.set(index, 0, .9);
                    if(index + j < 784)
                    drawnInput.set(index+j, 0, 250);
                    if(index-j >= 0)
                    drawnInput.set(index-j, 0, 250);
                    if(index + j*28 < 784)
                    drawnInput.set(index +j*28, 0, 250);
                    if(index-j*28 >= 0)
                    drawnInput.set(index -j*28, 0, 250);
                }
            }
        }
        if(i.isMB(1) && i.isKey(KeyEvent.VK_E)){
            if(i.getMouseX()>150 && i.getMouseX() < 179 && i.getMouseY()>150 && i.getMouseY() < 179){
                int index = (i.getMouseY() - 150) * 28 + i.getMouseX() - 150;
                if(index > 783){
                    index = 783;
                }
                for(int j = 0; j < size; j++) {
                    drawnInput.set(index, 0, 0);
                    if(index + j < 784)
                    drawnInput.set(index+j, 0, 0);
                    if(index-j >= 0)
                    drawnInput.set(index-j, 0, 0);
                    if(index + j*28 < 784)
                    drawnInput.set(index+j*28, 0, 0);
                    if(index-j*28 >= 0)
                    drawnInput.set(index-j*28, 0, 0);
                }
            }
        }
        if(i.isKey(KeyEvent.VK_C)){
            drawnInput.fill(0);
        }
        drawnOutput = nn.feedforward(drawnInput);
    }

    @Override
    public void renderer(Renderer r) {
       //Cross display
     /* int count = 0;
        for(SimpleMatrix t : testingData){
            r.drawLine(0, 120, 320, 120, 0xff00ff);
            r.drawLine(160, 0, 160, 240, 0xff00ff);


            double x = t.get(0,0);
            double y = t.get(1,0);

            if(testingTargets[count].get(0,0) ==1 && results[count])
                r.drawImage((int)x-2+160, (int)y-2+120, new Image(5,5,0xff0000));
            if(testingTargets[count].get(1,0) ==1 && results[count])
                r.drawImage((int)x-2+160, (int)y-2+120, new Image(5,5,0x0000ff));

            r.drawImage((int)x+160, (int)y+120, new Image(3,3,0x00ff00));

            count++;
       // }*/
     //MNIST
        r.clear();
        r.drawLine(150, 150, 179, 150, 0xff0000);
        r.drawLine(150, 150, 150, 179, 0xff0000);
        r.drawLine(179, 150,179, 179,  0xff0000);
        r.drawLine(150, 179,179, 179,  0xff0000);
        r.drawMatrix(151,151, drawnInput);
        r.drawMatrix(50,50, testingData[currentTest]);
        if(output !=null) {
            r.drawText(output.prediction, 100, 100, 0xff0000);
            r.drawText(testingAnswers[currentTest], 110, 100, 0x00ff00);
            r.drawText(Integer.toString(count) + "/" + Integer.toString(currentTest), 200, 100, 0x0000ff);
        }
        if(drawnOutput != null){
            r.drawText(drawnOutput.prediction, 185,185, 0xff0000);
        }
    }

}
