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

        //As long as there is a next line in the file
        while ((nextLine = reader.readNext()) != null) {
            //Get that line in a list
            List<String> lineAsList = new ArrayList<String>(Arrays.asList(nextLine));

            //Transform each data cell
            for(int i = 0; i < lineAsList.size(); i++)
            {
                String transformedData = transformCSVData(lineAsList.get(i), i);
                lineAsList.set(i, transformedData);
            }

            //Transform the list into an array to be accepted as parameter in the writer
            String lineAsArray[] = new String[lineAsList.size()];
            for(int i = 0; i < lineAsList.size(); i++)
            {
                lineAsArray[i] = lineAsList.get(i);
            }

            //Write the transformed line in the new file
            writer.writeNext(lineAsArray);
        }
    }



    private static String transformCSVData(String inputString, int i) {
        String outputString;
        switch (i) {
            //PassengerId
            case 0:  outputString = inputString;
                break;
            //Survived (0 = No, 1 = Yes)
            //A voir si il ne faut pas le mettre en position 1 de la liste pour éviter les pb sur le test data après)
            case 1:  outputString = inputString;
                break;
            //Pclass (Ticket class: 1 = 1st, 2 = 2nd, 3 = 3rd)
            case 2:  outputString = inputString;
                break;
            //Name
            case 3:  outputString = inputString;
                break;
            //Sex (male, female)
            case 4:  outputString = inputString;
                break;
            //Age (age in years) not always present
            //fractionnal if less than 1, xx.5 if estimated
            case 5:  outputString = inputString;
                break;
            //SibSp (# of siblings / spouses aboard the Titanic)
            case 6:  outputString = inputString;
                break;
            //Parch (# of parents / children aboard the Titanic)
            case 7:  outputString = inputString;
                break;
            //Ticket (number or complex string, do research on that)
            case 8:  outputString = inputString;
                break;
            //Fare (ticket price)
            case 9: outputString = inputString;
                break;
            //Cabin (cabin number)
            case 10: outputString = inputString;
                break;
            //Embarked (port of embarcation)
            //C = Cherbourg, Q = Queenstown, S = Southampton
            case 11: outputString = inputString;
                break;
            default: outputString = inputString;
                break;
        }
        return outputString;
    }

}
