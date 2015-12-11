package in.tranquilsoft.groupmessager.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by gurdevp on 05/12/15.
 */
public abstract class DefaultEntity {
    public abstract void createTable(SQLiteDatabase db);

    public abstract void dropTable(SQLiteDatabase db);

    public abstract void updateTable(SQLiteDatabase db, int oldVersion, int newVersion);

    public abstract long create(Context context);

    public abstract void update(Context context);

    public abstract void delete(Context context);

    public abstract <T> T getById(Context context, long id);

    public abstract <T> List<T> getAll(Context context);

    public abstract String getIdField();

    public abstract String[] getOtherFields();

    public String[] getColumns() {
        String[] columns = new String[getOtherFields().length + 1];
        columns[0] = getIdField();
        for (int i = 0; i < getOtherFields().length; i++) {
            columns[i + 1] = getOtherFields()[i];
        }
        return columns;
    }
}
