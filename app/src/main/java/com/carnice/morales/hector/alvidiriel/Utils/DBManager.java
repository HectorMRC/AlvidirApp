package com.carnice.morales.hector.alvidiriel.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

public class DBManager extends DBCoordinator{

    //CONSTRUCTORA:
    public DBManager(Context context){
        super(context);
    }

    //FUNCIONS PRIVADES:
    /*pre: cert*/
    /*post: s'ha actualitzat la data i hora de la taula Sync.*/
    private void updateSyncDatetime(){
        SQLiteDatabase db_syncWriter = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MainDBContract.FeedEntry.COLUMN_LAST_SYNC, Calendar.getInstance().getTime().toString());

        try{
            db_syncWriter.update(MainDBContract.FeedEntry.SYNC_TABLE_NAME, values, MainDBContract.FeedEntry.COLUMN_LAST_SYNC + " IS NOT NULL;", null);
        } catch (SQLException e){
            //TODO: toast notificant error
        }

        db_syncWriter.close();
    }

    /*pre: cert*/
    /*post: s'ha retornat un ContentValues ple amb els elements entrats.*/
    private ContentValues mainContentValues(String type, String word, String tran, String info, int skin){
        ContentValues values = new ContentValues();
        values.put(MainDBContract.FeedEntry.COLUMN_TYPE, type);
        values.put(MainDBContract.FeedEntry.COLUMN_WORD, word);
        values.put(MainDBContract.FeedEntry.COLUMN_TRAN, tran);
        values.put(MainDBContract.FeedEntry.COLUMN_INFO, info);
        values.put(MainDBContract.FeedEntry.COLUMN_SKIN, skin);
        return values;
    }

    /*pre: la definició composta per word i tran existeix a la base de dades.*/
    /*post: els atributs de totes les referencia a qualsevol d'ambdós mots s'ha actualitzat.*/
    private void linkUpdate(String oldWord, String oldTran, String newWord, String newTran){
        String[] columns = getMainColumns();
        String where = columns[1].concat(" = ? AND ").concat(columns[2]).concat(" = ? ");
        String[] args = new String[]{newWord, newTran};

        ContentValues root = getAllTuples(where, args, null, null, null, 0).get(0);
        ArrayList<ContentValues> refers = getAllReferences(oldWord, oldTran);
        for(ContentValues values: refers)
            updateItem(values.getAsString(columns[1]),
                       values.getAsString(columns[2]),
                       root.getAsString(columns[0]),
                       values.getAsInteger(columns[4]) == 1? values.getAsString(columns[1]) : newWord,
                       values.getAsInteger(columns[4]) == 2? values.getAsString(columns[2]) : newTran,
                       values.getAsInteger(columns[4]) == 1? newWord : newTran,
                       values.getAsInteger(columns[4]));
    }

    //FUNCIONS D'US:
    /*pre: cert*/
    /*post: ha retornat @app.db.sync_datetime == @drive.db.sync_datetime.*/
    public boolean checkIfSync() {
        return true;
    }

    /*pre: els elements word i tran no son null*/
    /*post: s'ha retornat true si, i  només si, l'insert a la bd de l'element amb els
    *       atributs declarats s'ha realitzat sense incidents.*/
    public boolean insertItem(String type, String word, String tran, String info, int skin){
        SQLiteDatabase db_mainWriter = getWritableDatabase();
        ContentValues values = mainContentValues(type, word, tran, info, skin);

        try {
            db_mainWriter.insertOrThrow(MainDBContract.FeedEntry.MAIN_TABLE_NAME, null, values);
            updateSyncDatetime();
        } catch (SQLException e){
            Log.i("INSERT SQLException", e.toString());
            return false;
        }

        db_mainWriter.close();
        return true;
    }

    /*pre: els elements word i tran no son null*/
    /*post: s'ha retornat true si, i només si, la supresió a la bd de l'element amb
    *       c.p.(word, tran) s'ha realitzat sense incidents.*/
    public boolean deleteItem(String word, String tran){
        SQLiteDatabase db_mainWriter = getWritableDatabase();

        try {
            db_mainWriter.delete(MainDBContract.FeedEntry.MAIN_TABLE_NAME,
                      MainDBContract.FeedEntry.COLUMN_WORD + " LIKE ? AND " + MainDBContract.FeedEntry.COLUMN_TRAN + " LIKE ? ",
                                 new String[]{word, tran});
            updateSyncDatetime();

        } catch (SQLException e){
            //TODO: toast notificant error
            Log.i("DELETE SQLException", e.toString());
            return false;
        }

        db_mainWriter.close();
        return true;
    }

    /*pre: els elements word i tran no son null*/
    /*post: s'ha retornat true si, i només si, l'actualització a la bd de l'element amb
    *       c.p.(word, tran) s'ha realitzat sense incidents .*/
    public boolean updateItem(String word, String tran, String type, String newWord, String newTran, String info, int skin){
        SQLiteDatabase db_mainWriter = getWritableDatabase();
        ContentValues values = mainContentValues(type, newWord, newTran, info, skin);

        try {
            db_mainWriter.update(MainDBContract.FeedEntry.MAIN_TABLE_NAME,
                                 values,
                      MainDBContract.FeedEntry.COLUMN_WORD + " LIKE ? AND " + MainDBContract.FeedEntry.COLUMN_TRAN + " LIKE ? ",
                                 new String[]{word, tran});
            updateSyncDatetime();

        } catch (SQLException e){
            //TODO: toast notificant error
            Log.i("UPDATE SQLException", e.toString());
            return false;
        }

        db_mainWriter.close();
        linkUpdate(word, tran, newWord, newTran);
        return true;
    }

    /*pre: statement és una sentecia en SQL*/
    /*post: s'ha retornat true si, i només si, la sentencia statement s'ha pogut executar
    *       sense incidents; altrament el resultat és false.*/
    public boolean executeStatement(String statement){
        SQLiteDatabase db_mainWriter = getWritableDatabase();
        try {
            db_mainWriter.execSQL(statement);
            updateSyncDatetime();

        }catch (SQLException e){
            //TODO: toast notificant error
            Log.i("QUERY SQLException", e.toString());
            return false;
        }

        return true;
    }

    /*pre: cert*/
    /*post: s'ha eliminat tot el contingut de la base de dades.*/
    public boolean clearContent(){
        SQLiteDatabase db_mainWriter = getWritableDatabase();
        try {
            db_mainWriter.execSQL("DELETE FROM " + MainDBContract.FeedEntry.MAIN_TABLE_NAME);
            updateSyncDatetime();

        } catch (SQLException e){
            //TODO: toast notificant error
            Log.i("CLEAR SQLException", e.toString());
            return false;
        }

        db_mainWriter.close();
        return true;
    }

    /*pre: els elements word i tran no son null*/
    /*post: s'ha retornat true si i només si existeix a la bd alguna tupla amb c.p.(word, tran)*/
    public boolean checkIfExists(String word, String tran){
        SQLiteDatabase db_reader = getReadableDatabase();
        Cursor cursor = db_reader.query(
                MainDBContract.FeedEntry.MAIN_TABLE_NAME,
                new String[]{"*"},
                MainDBContract.FeedEntry.COLUMN_WORD + " = ? AND " + MainDBContract.FeedEntry.COLUMN_TRAN + " = ?",
                new String[] {word, tran},
                null,
                null,
                null);

        boolean count = cursor.getCount() > 0;
        db_reader.close();
        cursor.close();

        return count;
    }

    /*pre: l'atribut statement no es buit*/
    /*post: s'ha retornat un array amb totes les tuples que satisfan statement.*/
    public ArrayList<ContentValues> getAllTuples(String selection, String[] args, String groupBy, String having, String sortBy, int color){
        SQLiteDatabase db_reader = getReadableDatabase();
        Cursor cursor = db_reader.query(MainDBContract.FeedEntry.MAIN_TABLE_NAME,
                new String[]{"*"}, selection, args, groupBy, having, sortBy);

        ArrayList<ContentValues> tuples = new ArrayList<>();
        while (cursor.moveToNext()){
            ContentValues contentValues = mainContentValues(cursor.getString(0),
                                                            cursor.getString(1),
                                                            cursor.getString(2),
                                                            cursor.getString(3),
                                                            cursor.getInt(4));
            contentValues.put("Color", color);
            tuples.add(contentValues);
        }

        db_reader.close();
        cursor.close();
        return tuples;
    }

    /*pre: la definició composta per word i tran existeix a la base de dades.*/
    /*post: s'han retornat totes les referencies a la definició {word, tran}*/
    public ArrayList<ContentValues> getAllReferences(String word, String tran){
        String[] columns = getMainColumns();
        String where = "( ".concat(columns[4])
                           .concat(" = 1 AND ")
                           .concat(columns[2]).concat(" = ? AND ")
                           .concat(columns[3]).concat(" = ? ) OR ( ")
                           .concat(columns[4])
                           .concat(" = 2 AND ")
                           .concat(columns[1]).concat(" = ? AND ")
                           .concat(columns[3]).concat(" = ? ) ");

        String[] args = new String[]{tran, word, word, tran};
        return getAllTuples(where, args, null, null, null, 0);
    }

    /*pre: la definició composta per word i tran existeix a la base de dades.*/
    /*post: s'ha retornat la tupla sencera, amb tots els seus components.*/
    public ContentValues getTuple(String word, String tran){
        String[] columns = getMainColumns();
        String where = columns[1].concat(" = ? AND ").concat(columns[2]).concat(" = ? ");
        String[] args = new String[]{word, tran};

        return getAllTuples(where, args, null, null, null, 0).get(0);
    }

    /*pre: la definició composta per word i tran existeix a la base de dades.*/
    /*post: s'ha retornat la tupla pare, és a dir, aquella en que Skin és 0.*/
    public ContentValues getRoot(String word, String tran){
        String[] columns = getMainColumns();
        String where = columns[1].concat(" = ? AND ").concat(columns[2]).concat(" = ? ");
        String[] args = new String[]{word, tran};

        ContentValues tupla = getAllTuples(where, args, null, null, null, 0).get(0);
        return tupla.getAsInteger(columns[4]) == 0? tupla :
               getRoot(tupla.getAsString(columns[tupla.getAsInteger(columns[4]) == 1? 3 : 1]),
                       tupla.getAsString(columns[tupla.getAsInteger(columns[4]) == 2? 3 : 2]));
    }

    /*pre: cert*/
    /*post: s'ha retornat un string amb la data i hora de l'última actualització*/
    public String getLastSync(){
        SQLiteDatabase db_reader = getReadableDatabase();
        Cursor cursor = db_reader.query(
                MainDBContract.FeedEntry.SYNC_TABLE_NAME,
                new String[]{"*"},
                null,
                null,
                null,
                null,
                null);

        cursor.moveToNext();
        String datetime = cursor.getString(1);

        db_reader.close();
        cursor.close();
        return datetime;
    }

    /*pre: cert*/
    /*post: s'ha retornat el nom de la taula principal.*/
    public String getMainTableName(){
        return MainDBContract.FeedEntry.MAIN_TABLE_NAME;
    }

    /*pre: cert*/
    /*post: s'ha retornat un vector d'strings amb les claus primaries de la taula main*/
    public String[] getKey(){
        return new String[]{MainDBContract.FeedEntry.COLUMN_WORD, MainDBContract.FeedEntry.COLUMN_TRAN, "Color"};
    }

    /*pre: cert*/
    /*post: s'ha retornat un array d'strings amb tots els noms de les columnes de la taula main*/
    public String[] getMainColumns(){
        return new String[]{MainDBContract.FeedEntry.COLUMN_TYPE,
                            MainDBContract.FeedEntry.COLUMN_WORD,
                            MainDBContract.FeedEntry.COLUMN_TRAN,
                            MainDBContract.FeedEntry.COLUMN_INFO,
                            MainDBContract.FeedEntry.COLUMN_SKIN};
    }

    /*pre: cert*/
    /*post: s'ha retornat el nom de la única columna de la taula sync*/
    public String getSyncColumns(){
        return MainDBContract.FeedEntry.COLUMN_LAST_SYNC;
    }

}
