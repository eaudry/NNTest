package com.eaudry.titanicChallenge;

import com.eaudry.Main;
import com.eaudry.PlotUtil;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.standalone.ClassPathResource;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by eaudr on 13/03/2018.
 */
public class TitanicChallenge {

    public static void main(String[] args) throws Exception {

        final Logger log = LoggerFactory.getLogger(Main.class);

        int seed = 123;
        double learningRate = 0.01;
        int batchSize = 1;
        int nEpochs = 5;

        int numInputs = 8;
        int numOutputs = 2;
        int numHiddenNodes = 6;

        final String filenameTrain  = new ClassPathResource("/titanic_data/train_new_2.csv").getFile().getPath();
        final String filenameTest  = new ClassPathResource("/titanic_data/test_new_2.csv").getFile().getPath();

        //final String filenameTrain  = new ClassPathResource("/classification/linear_data_train.csv").getFile().getPath();
        //final String filenameTest  = new ClassPathResource("/classification/linear_data_eval.csv").getFile().getPath();

        //Load the training data:
        RecordReader rrTrainingData = new CSVRecordReader();
//        rr.initialize(new FileSplit(new File("src/main/resources/classification/linear_data_train.csv")));
        rrTrainingData.initialize(new FileSplit(new File(filenameTrain)));
        DataSetIterator trainIter = new RecordReaderDataSetIterator(rrTrainingData,batchSize,0,2);

        //NormalizerMinMaxScaler preProcessor = new NormalizerMinMaxScaler();
        //preProcessor.fit(trainIter);

        //Load the test/evaluation data:
        RecordReader rrTestData = new CSVRecordReader();
        rrTestData.initialize(new FileSplit(new File(filenameTest)));
        DataSetIterator testIter = new RecordReaderDataSetIterator(rrTestData,batchSize,0,2);

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(1)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                //.learningRate(learningRate)
                .updater(new Nesterovs(0.1, 0.9))
                //.updater(Updater.NESTEROVS) //pour setup les valeurs par défaut (0.1 LR et 0.9 MOM)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.SOFTMAX).weightInit(WeightInit.XAVIER)
                        .nIn(numHiddenNodes).nOut(numOutputs).build())
                .pretrain(false).backprop(true).build();


        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(10));  //Print score every 10 parameter updates

        /** To visualize network training: http://localhost:9000/train **/
        /** To use port xxxx, pass the following to the JVM on launch: -Dorg.deeplearning4j.ui.port=xxxx**/
        //Initialize the user interface backend
        UIServer uiServer = UIServer.getInstance();

        //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
        StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later

        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
        uiServer.attach(statsStorage);

        //Then add the StatsListener to collect this information from the network, as it trains
        model.setListeners(new StatsListener(statsStorage));
        /** **/


        log.info("Train model....");
        for ( int n = 0; n < nEpochs; n++) {
            log.info("Epoch: " + n);
            model.fit( trainIter );
        }

        int k = 0;
        log.info("Evaluate model....");
        Evaluation eval = new Evaluation(numOutputs);
        while(testIter.hasNext()){
            k++;
            log.info("evaluating row : "+k);
            DataSet t = testIter.next();
            INDArray features = t.getFeatureMatrix();
            INDArray lables = t.getLabels();
            INDArray predicted = model.output(features,false);

            eval.eval(lables, predicted);

        }

        //Print the evaluation statistics
        System.out.println(eval.stats());


        //------------------------------------------------------------------------------------
        //Training is complete. Code that follows is for plotting the data & predictions only

        //Plot the data:
        double xMin = 0;
        double xMax = 1.0;
        double yMin = -0.2;
        double yMax = 0.8;

        //Let's evaluate the predictions at every point in the x/y input space
        int nPointsPerAxis = 50;
        double[][] evalPoints = new double[nPointsPerAxis*nPointsPerAxis][2];
        int count = 0;
        for( int i=0; i<nPointsPerAxis; i++ ){
            for( int j=0; j<nPointsPerAxis; j++ ){
                double x = i * (xMax-xMin)/(nPointsPerAxis-1) + xMin;
                double y = j * (yMax-yMin)/(nPointsPerAxis-1) + yMin;

                evalPoints[count][0] = x;
                evalPoints[count][1] = y;

                count++;
            }
        }

        INDArray allXYPoints = Nd4j.create(evalPoints);
        INDArray predictionsAtXYPoints = model.output(allXYPoints);

        //Get all of the training data in a single array, and plot it:
        rrTrainingData.initialize(new FileSplit(new ClassPathResource("/classification/linear_data_train.csv").getFile()));
        rrTrainingData.reset();
        int nTrainPoints = 200;
        trainIter = new RecordReaderDataSetIterator(rrTrainingData,nTrainPoints,0,2);
        DataSet ds = trainIter.next();
        PlotUtil.plotTrainingData(ds.getFeatures(), ds.getLabels(), allXYPoints, predictionsAtXYPoints, nPointsPerAxis);

        //Get test data, run the test data through the network to generate predictions, and plot those predictions:
        rrTestData.initialize(new FileSplit(new ClassPathResource("/classification/linear_data_eval.csv").getFile()));
        rrTestData.reset();
        int nTestPoints = 50;
        testIter = new RecordReaderDataSetIterator(rrTestData,nTestPoints,0,2);
        ds = testIter.next();
        INDArray testPredicted = model.output(ds.getFeatures());
        PlotUtil.plotTestData(ds.getFeatures(), ds.getLabels(), testPredicted, allXYPoints, predictionsAtXYPoints, nPointsPerAxis);

        log.info("****************Example finished********************");


        //Save the model
        //Where to save the network. Note: the file is in .zip format - can be opened externally
        File locationToSave = new File("MyNetwork.zip");
        //Updater: i.e., the state for Momentum, RMSProp, Adagrad etc. Save this if you want to train your network more in the future
        boolean saveUpdater = true;
        ModelSerializer.writeModel(model, locationToSave, saveUpdater);

        log.info("****************Model Save********************");

        //Load the model
        MultiLayerNetwork modelRestored = ModelSerializer.restoreMultiLayerNetwork(locationToSave);

        log.info("****************Model loaded********************");

        log.info("Saved and loaded parameters are equal:      " + model.params().equals(modelRestored.params()));
        log.info("Saved and loaded configurations are equal:  " + model.getLayerWiseConfigurations().equals(modelRestored.getLayerWiseConfigurations()));


    }
}
