package paztechnologies.com.meribus.DatabaseHelper;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Admin on 6/26/2017.
 */

public class SeatDatabase extends SQLiteOpenHelper {

    private static String DB_NAME = "Restaurant.sqlite";
    private final Context myContext;
    private SQLiteDatabase mDataBase;
    private String DB_PATH;


    public static SeatDatabase newInstance(Context context) {
        return new SeatDatabase(context);

    }


    public SeatDatabase(Context context) {
        super(context,DB_NAME,null,1);
        DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        myContext = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void openDataBase() throws SQLException {

        // Open the database
        String myPath = DB_PATH + DB_NAME;
        mDataBase = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);

    }


    public void createdatabase() throws IOException{
        boolean dbExist=checkDataBase();
        if(dbExist){

        }else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            }catch (IOException e){
                throw  new Error("ERROR COPYING DATABASE");
            }
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if(!db.isReadOnly()){
            db.execSQL("PRAGMA foreign_keys=ON;");
            db.execSQL("PRAGMA case_sensitive_like=ON");
        }
    }

    @Override
    public synchronized void close() {
        // TODO Auto-generated method stub
        if (mDataBase != null)
            mDataBase.close();

        super.close();
    }
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {

            // database does't exist yet.

        }

        if (checkDB != null) {

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException{
        InputStream myinput=myContext.getAssets().open(DB_NAME);
        String outfilename= DB_PATH +DB_NAME;
        OutputStream myoutput=new FileOutputStream(outfilename);
        if(myinput!=null&& myoutput!=null){
            byte[] buffer=new byte[1024];
            int length;
            while ((length=myinput.read(buffer))>0){
                myoutput.write(buffer,0,length);
            }

        }else {
            Log.d("error", "Not Found");
        }
        myoutput.flush();
        myoutput.close();
        myinput.close();
    }
}
