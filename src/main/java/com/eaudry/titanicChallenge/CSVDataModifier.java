package com.eaudry.titanicChallenge;

import com.eaudry.Main;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by eaudr on 13/03/2018.
 */
public class CSVDataModifier {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CSVDataModifier.class);
    private static int inputDataValidated = 0;
    private static int outputDataValidated = 0;

    public static void main(String[] args)throws IOException{

        //Setup file reader and file writer
        CSVReader reader = new CSVReader(new FileReader("src/main/resources/titanic_data/train_raw.csv"));
        CSVWriter writer = new CSVWriter(new FileWriter("src/main/resources/titanic_data/train_new.csv"));
        String [] nextLine;
        int rowNumber =0;

        //Skip first line (columns titles)
        reader.readNext();

        //As long as there is a next row in the file
        while ((nextLine = reader.readNext()) != null) {
            rowNumber++;
            //Get that line in a list
            List<String> rowAsList = new ArrayList<String>(Arrays.asList(nextLine));
            List<String> transformedRowAsList;

            try{
                //Check the format of each cell of the input
                boolean inputDataIsCorrect  = checkInputDataIsCorrect(rowAsList, rowNumber);
                if (inputDataIsCorrect){
                    transformedRowAsList = transformCSVData(rowAsList);
                    //Check the format of each cell transformed cell
                    boolean outPutDataIsCorrect = checkOutputDataIsCorrect(transformedRowAsList, rowNumber);
                    //If everything is right add it to the train data set
                    if (outPutDataIsCorrect){
                            addDataToTrainingFile(transformedRowAsList, writer);
                    }
                }
            }
            catch (Exception e){
                e.getStackTrace();
            }
        }// end of file reader
        log.info("Correct input data : " + inputDataValidated+"/"+rowNumber);
        log.info("Correct input data : " + outputDataValidated+"/"+inputDataValidated);
        writer.close();
    }// end of main

    private static boolean checkInputDataIsCorrect(List<String> rowAsList, int rowNumber){
        boolean inputDataIsCorrect = true;
        //Check if total variable count is correct
        if (rowAsList.size() != 12){
            inputDataIsCorrect = false;
            log.info("Line " + rowNumber + " : input data rejected, variable count != 12 : " + rowAsList.size());
        }

        //If it is correct you are sure can get() the 12 variables and check them
        else{
            String passengerId = rowAsList.get(0);
            String survived = rowAsList.get(1);
            String passengerClass = rowAsList.get(2);
            String name = rowAsList.get(3);
            String sex = rowAsList.get(4);
            String age = rowAsList.get(5);
            String sibSp = rowAsList.get(6);
            String parch = rowAsList.get(7);
            String ticketId = rowAsList.get(8);
            String fare = rowAsList.get(9);
            String cabin = rowAsList.get(10);
            String embarkedPort = rowAsList.get(11);

            //Check if "passengerId" variable matches an int
            if (!passengerId.matches("\\d+")){
                inputDataIsCorrect = false;
                log.info("Line " + rowNumber + " : input data rejected, passengerId is not an int : " + passengerId);
            }
            //Check if "survived" variable is 0 or 1
            else if (!survived.matches("[01]")){
                inputDataIsCorrect = false;
                log.info("Line " + rowNumber + " : input data rejected, survived is not 0 or 1 : " + survived);
            }
            //Check if "passenger class" variable is 1,2 or 3
            else if (!passengerClass.matches("[123]")){
                inputDataIsCorrect = false;
                log.info("Line " + rowNumber + " : input data rejected, passenger class is not 1,2 or 3 : " + passengerClass);
            }
            //Check if "name" variable is present
            else if (name.equals("")){
                inputDataIsCorrect = false;
                log.info("Line " + rowNumber + " : input data rejected, passenger name missing : " + name);
            }
            //Check if "sex" variable is male or female
            else if (!sex.equals("male") && !sex.equals("female")){
                inputDataIsCorrect = false;
                log.info("Line " + rowNumber + " : input data rejected, sex variable not recognized : " + sex);
            }
            //Check if "age" variable is either a positive number (xx.xx) or missing (data unknown)
            else if (!age.matches("([0-9]+)*\\.*[0-9]+") && !age.equals("")){
                inputDataIsCorrect = false;
                log.info("Line " + rowNumber + " : input data rejected, age variable not recognized : " + age);
            }
            //Check if "sibSp" variable is positive integer
            else if (!sibSp.matches("[0-9]+")){
                inputDataIsCorrect = false;
                log.info("Line " + rowNumber + " : input data rejected, sibSp is not a positive integer : " + sibSp);
            }
            //Check if "parch" variable is positive integer
            else if (!parch.matches("[0-9]+")){
                inputDataIsCorrect = false;
                log.info("Line " + rowNumber + " : input data rejected, parch is not a positive integer : " + parch);
            }
            //Check if "ticketId" variable is an optional prefix followed by a optional space and a number
            else if (!ticketId.matches(".*\\s*[0-9]+")){
                inputDataIsCorrect = false;
                log.info("Line " + rowNumber + " : input data rejected, tickedId not recognized : " + ticketId);
            }
            //Check if "fare" variable is either a positive number (xx.xx) or missing (data unknown)
            else if (!fare.matches("([0-9]+)*\\.*[0-9]+") && !fare.equals("")){
                inputDataIsCorrect = false;
                log.info("Line " + rowNumber + " : input data rejected, fare variable not recognized : " + fare);
            }
            //Check if "cabin" variable either starts with a letter or is missing (data unknown)
            else if (!cabin.matches("[A-Z].*") && !cabin.equals("")){
                inputDataIsCorrect = false;
                log.info("Line " + rowNumber + " : input data rejected, cabin does not start with a letter : " + cabin);
            }
            //Check if "embarkedPort" is C,S or Q
            else if (!embarkedPort.matches("[CSQ]") ){
                inputDataIsCorrect = false;
                log.info("Line " + rowNumber + " : input data rejected, embarked port not recognized : " + embarkedPort);
            }
        }
        if (inputDataIsCorrect) {inputDataValidated++;}
        return inputDataIsCorrect;
    }



    private static boolean checkOutputDataIsCorrect(List<String> transformedRowAsList, int rowNumber){
        boolean outputDataIsCorrect = true;


        return outputDataIsCorrect;
    }



    private static void addDataToTrainingFile(List<String> transformedRowAsList, CSVWriter writer){
        //Transform the list into an array to be accepted as parameter in the writer
        String transformedLineAsArray[] = new String[transformedRowAsList.size()];
        for(int i = 0; i < transformedRowAsList.size(); i++)
        {
            transformedLineAsArray[i] = transformedRowAsList.get(i);
        }

        //Write the transformed data line in the training data file
        writer.writeNext(transformedLineAsArray);

        //Flush the writer
        try{
            writer.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static List<String> transformCSVData(List<String> inputRowAsList) throws Exception{
        List<String> outputRowAsList = new ArrayList<String>();

        /** COLUMN 1 **/
        /** Add survived or not as new first column **/
        outputRowAsList.add(inputRowAsList.get(1));

        /** COLUMN 2 **/
        /** Passenger class: 1 = 1st, 2 = 2nd, 3 = 3rd **/
        outputRowAsList.add(inputRowAsList.get(2));

        /** COLUMN 3 **/
        /** Sex: male, female **/
        if (inputRowAsList.get(4).equals("female")){
            outputRowAsList.add("1");
        }
        else outputRowAsList.add("2");

        /** COLUMN 4 **/
        /** Age (in years) not always present, fractionnal if less than 1, xx.5 if estimated **/
        String age = inputRowAsList.get(5);
        if (age.equals("")){
            outputRowAsList.add("-99");
        }
        else outputRowAsList.add(age);

        /** COLUMN 5 **/
        /** SibSp (# of siblings / spouses aboard the Titanic) **/
        outputRowAsList.add(inputRowAsList.get(6));

        /** COLUMN 6 **/
        /** Parch (# of parents / children aboard the Titanic) **/
        outputRowAsList.add(inputRowAsList.get(7));

        /** COLUMN 7 and 8**/
        /** Ticket id: optional prefix + number, split and output as 2 columns **/
        //Split by space
        String[] tickedIdSplited = inputRowAsList.get(8).split("\\s+");
        //If ticket doesn't have a prefix
        if (tickedIdSplited.length == 1){
            //Put 0 in the prefix column
            outputRowAsList.add("0");
            //Put ticker number in the next column
            outputRowAsList.add(tickedIdSplited[0]);
        }
        //If ticket has a prefix
        else {
            //Absolute value of the hashcode of the prefix (or first part of the prefix if multiple spaces) in the column
            outputRowAsList.add(""+Math.abs(tickedIdSplited[0].hashCode()));
            //Put ticker number (always last part of the split) in the next column
            outputRowAsList.add(tickedIdSplited[tickedIdSplited.length-1]);
        }


        /** COLUMN 9 **/
        /** Fare (ticket price) **/
        outputRowAsList.add(inputRowAsList.get(9));

        /** COLUMN 10 **/
        /** Cabin (cabin id): save first letter (= deck) higher deck higher chance of survival, 8 if the info is missing **/
        String deckId = inputRowAsList.get(10);
        if (deckId.equals("")){
            outputRowAsList.add("8");
        }
        else if (deckId.substring(0, 1).equals("A")){
            outputRowAsList.add("1");
        }
        else if (deckId.substring(0, 1).equals("B")){
            outputRowAsList.add("2");
        }
        else if (deckId.substring(0, 1).equals("C")){
            outputRowAsList.add("3");
        }
        else if (deckId.substring(0, 1).equals("D")){
            outputRowAsList.add("4");
        }
        else if (deckId.substring(0, 1).equals("E")){
            outputRowAsList.add("5");
        }
        else if (deckId.substring(0, 1).equals("F")){
            outputRowAsList.add("6");
        }
        else if (deckId.substring(0, 1).equals("G")){
            outputRowAsList.add("7");
        }
        else throw new Exception("cabin_Id not recognized : "+deckId);



        /** COLUMN 11 **/
        /** Embarked (port of embarcation): 1 = Cherbourg, 2 = Queenstown, 3 = Southampton **/
        String embarkedPort = inputRowAsList.get(11);
        if (embarkedPort.equals("C")){
            outputRowAsList.add("1");
        }
        else if (embarkedPort.equals("Q")){
            outputRowAsList.add("2");
        }
        else if (embarkedPort.equals("S")){
            outputRowAsList.add("3");
        }
        else outputRowAsList.add("0");

        /** Return the new row **/
        return outputRowAsList;
    }

}
