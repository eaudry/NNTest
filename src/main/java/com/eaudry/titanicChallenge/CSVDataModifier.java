package com.eaudry.titanicChallenge;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by eaudr on 13/03/2018.
 */
public class CSVDataModifier {

    public static void main(String[] args)throws IOException{

        CSVReader reader = new CSVReader(new FileReader("src/main/resources/titanic_data/train_raw.csv"));
        CSVWriter writer = new CSVWriter(new FileWriter("src/main/resources/titanic_data/train_new.csv"));
        String [] nextLine;

        //Skip first line (columns titles)
        reader.readNext();

        //As long as there is a next line in the file
        while ((nextLine = reader.readNext()) != null) {
            //Get that line in a list
            List<String> lineAsList = new ArrayList<String>(Arrays.asList(nextLine));
            List<String> transformedLineAsList = new ArrayList<String>(Arrays.asList(nextLine));

            //Transform each data line
            for(int i = 0; i < lineAsList.size(); i++)
            {
                transformedLineAsList = transformCSVData(lineAsList);
            }

            //Transform the list into an array to be accepted as parameter in the writer
            String transformedLineAsArray[] = new String[transformedLineAsList.size()];
            for(int i = 0; i < transformedLineAsList.size(); i++)
            {
                transformedLineAsArray[i] = transformedLineAsList.get(i);
            }

            //Write the transformed line in the new file
            writer.writeNext(transformedLineAsArray);
        }
        writer.close();
    }




    private static List<String> transformCSVData(List<String> inputRowAsList) {
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

        /** Return the new row **/
        return outputRowAsList;
    }

}
