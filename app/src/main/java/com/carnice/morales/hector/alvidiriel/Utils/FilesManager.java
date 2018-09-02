package com.carnice.morales.hector.alvidiriel.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.carnice.morales.hector.alvidiriel.MainActivity;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

public class FilesManager {

    private Context context;
    private Intent intent;
    private static char cmts = '"';

    public FilesManager(Context context, Intent intent){
        this.context = context;
        this.intent = intent;
    }

    public void saveDataBaseContent() throws IOException {
        DBManager dbManager = new DBManager(context);
        FileWriter fileWriter = new FileWriter(
                                new FileOutputStream(context
                                        .getContentResolver()
                                        .openFileDescriptor(intent.getData(), "w")
                                        .getFileDescriptor())
                                        .getFD());

        String[] columns = dbManager.getMainColumns();
        ArrayList<ContentValues> tuples = dbManager.getAllTuples(null, null, null, null, null, 0);
        for(ContentValues values: tuples)
            fileWriter.write("INSERT OR IGNORE INTO ".concat(dbManager.getMainTableName())
                    .concat(" VALUES (" + cmts).concat(values.getAsString(columns[0]))
                    .concat(cmts + "," + cmts).concat(values.getAsString(columns[1]))
                    .concat(cmts + "," + cmts).concat(values.getAsString(columns[2]))
                    .concat(cmts + "," + cmts).concat(values.getAsString(columns[3]))
                    .concat(cmts + "," + values.getAsInteger(columns[4])).concat(");\n"));

        fileWriter.close();
    }

    public void getDataFileContent() throws IOException {
        DBManager dbManager = new DBManager(context);
        BufferedReader buffer = new BufferedReader(
                                new FileReader(
                                new FileInputStream(context
                                        .getContentResolver()
                                        .openFileDescriptor(intent.getData(), "r")
                                        .getFileDescriptor())
                                        .getFD()));

        for(String line; (line = buffer.readLine()) != null; dbManager.executeStatement(line))
            while(line.charAt(line.length()-1) != ';')
                line = "\n".concat(line.concat(buffer.readLine()));

        buffer.close();
    }

    private String getFromOldFromat(String line){
        DBManager dbManager = new DBManager(context);
        StringTokenizer tokenizer = new StringTokenizer(line, ""+cmts);
        String statement = "INSERT OR IGNORE INTO ".concat(dbManager.getMainTableName()).concat(" VALUES (" + cmts + "NULL" + cmts);
        HashSet<Integer> numeros = new HashSet<>();
        numeros.add(1); numeros.add(3); numeros.add(9); numeros.add(11);
        for(int i = 0; tokenizer.hasMoreTokens(); i++) {
            String current = tokenizer.nextToken();
            if (numeros.contains(i)) {
                statement = statement.concat(i == 11? "," : ","+cmts);
                statement = statement.concat(current.contains("@")? "" : current.contains("1")? "2" : current.contains("2")? "1" : current);
                statement = statement.concat(i == 11? "" : ""+cmts);
            }
        }

        Log.i("OLDER STATEMENT", line);
        Log.i("CURRENT STATEMENT", statement);
        return statement.concat(");");
    }

}
