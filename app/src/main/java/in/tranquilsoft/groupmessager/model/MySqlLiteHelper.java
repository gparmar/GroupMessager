package in.tranquilsoft.groupmessager.model;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import in.tranquilsoft.groupmessager.model.impl.Contact;
import in.tranquilsoft.groupmessager.model.impl.ContactGroup;
import in.tranquilsoft.groupmessager.model.impl.GroupContactJunction;
import in.tranquilsoft.groupmessager.model.impl.MessageSentStatus;
import in.tranquilsoft.groupmessager.model.impl.MessagingHistory;

/**
 * Created by gurdevp on 05/12/15.
 */
public class MySqlLiteHelper extends SQLiteOpenHelper {
    private static MySqlLiteHelper mySqlLiteHelper;
    public static final String DATABASE_NAME = "GroupMessager";
    public static final int DATABASE_VERSION = 6;
    private Context context;

    private MySqlLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        new ContactGroup().createTable(db);
        new Contact().createTable(db);
        new GroupContactJunction().createTable(db);
        new MessagingHistory().createTable(db);
        new MessageSentStatus().createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion==5 && newVersion==6) {
            db.execSQL("alter table "+MessagingHistory.TABLE_NAME+" drop column group_d");
            db.execSQL("alter table "+MessagingHistory.TABLE_NAME+" add column group_id integer");
        } else {
            try {
                new ContactGroup().dropTable(db);
                new Contact().dropTable(db);
                new GroupContactJunction().dropTable(db);
                new MessagingHistory().dropTable(db);
                new MessageSentStatus().dropTable(db);
            } catch (Exception e) {
            }
            new ContactGroup().createTable(db);
            new Contact().createTable(db);
            new GroupContactJunction().createTable(db);
            new MessagingHistory().createTable(db);
            new MessageSentStatus().createTable(db);
        }
//        new ContactGroup().updateTable(db, oldVersion, newVersion);
//        new AbstractContact().updateTable(db, oldVersion, newVersion);
    }

    public static MySqlLiteHelper getInstance(Context context) {
        if (mySqlLiteHelper == null) {
            mySqlLiteHelper = new MySqlLiteHelper(context.getApplicationContext());
        }
        return mySqlLiteHelper;
    }

    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }


    }

}
