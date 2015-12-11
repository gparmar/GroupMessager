package in.tranquilsoft.groupmessager.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.tranquilsoft.groupmessager.model.impl.MessagingHistory;

public class AbstractMessagingHistory extends DefaultEntity implements Parcelable {
    public static final String TAG = "MessagingHistory";
    public static final String TABLE_NAME = "MessagingHistory";
    public static final String ID_FIELD = "id";
    public static final String GroupD_FIELD = "group_d";
    public static final String SentTime_FIELD = "sent_time";

    public static final String[] OTHER_FIELDS = new String[]{GroupD_FIELD, SentTime_FIELD};
    public static final String TABLE_CREATE_SQL = "create table " + TABLE_NAME + "(id integer primary key autoincrement,group_d integer,sent_time integer)";

    private long id;
    private long groupD;
    private Date sentTime;


    public AbstractMessagingHistory() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGroupD() {
        return groupD;
    }

    public void setGroupD(long groupD) {
        this.groupD = groupD;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }


    @Override
    public void createTable(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_SQL);
    }

    @Override
    public void dropTable(SQLiteDatabase db) {
        db.execSQL("drop table " + TABLE_NAME);
    }

    @Override
    public void updateTable(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public long create(Context context) {
        ContentValues cv = new ContentValues();
        cv.put(GroupD_FIELD, getGroupD());
        cv.put(SentTime_FIELD, getSentTime());

        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getWritableDatabase();
        long result = db.insert(TABLE_NAME, null, cv);
        db.close();
        return result;
    }

    @Override
    public void update(Context context) {
        Log.i(TAG, "Updating " + this);
        ContentValues cv = new ContentValues();
        cv.put(GroupD_FIELD, getGroupD());
        cv.put(SentTime_FIELD, getSentTime());

        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getWritableDatabase();
        db.update(TABLE_NAME, cv, ID_FIELD + "= ?", new String[]{String.valueOf(getId())});
        db.close();

    }

    @Override
    public void delete(Context context) {
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getWritableDatabase();
        db.delete(TABLE_NAME, ID_FIELD + "= ?", new String[]{String.valueOf(getId())});
        db.close();
    }

    @Override
    public MessagingHistory getById(Context context, long id) {
        Log.i(TAG, "Doing query by id:" + id);
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, getColumns(),
                ID_FIELD + "= ?", new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Log.d(TAG, "Cursor was not null and move to first");
            MessagingHistory messagingHistory = new MessagingHistory();
            messagingHistory.setId(cursor.getLong(0));
            messagingHistory.setGroupD(cursor.getLong(1));
            messagingHistory.setSentTime(cursor.getLong(2));

            return messagingHistory;
        }
        return null;
    }

    @Override
    public List<MessagingHistory> getAll(Context context) {
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, getColumns(),
                null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            List<MessagingHistory> result = new ArrayList<>();
            do {
                MessagingHistory messagingHistory = new MessagingHistory();
                messagingHistory.setId(cursor.getLong(0));
                messagingHistory.setGroupD(cursor.getLong(1));
                messagingHistory.setSentTime(cursor.getLong(2));

                result.add(messagingHistory);
            } while (cursor.moveToNext());
            return result;
        }
        return null;
    }

    @Override
    public String getIdField() {
        return "id";
    }

    @Override
    public String[] getOtherFields() {
        return OTHER_FIELDS;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected AbstractMessagingHistory(Parcel in) {
        id = in.readLong();
        groupD = in.readLong();

    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeLong(groupD);

    }

    public static final Parcelable.Creator<AbstractMessagingHistory> CREATOR
            = new Parcelable.Creator<AbstractMessagingHistory>() {
        public AbstractMessagingHistory createFromParcel(Parcel in) {
            return new AbstractMessagingHistory(in);
        }

        public AbstractMessagingHistory[] newArray(int size) {
            return new AbstractMessagingHistory[size];
        }
    };

    public String toString() {
        return "MessagingHistory:[" + "id=" + id + ", " + "groupD=" + groupD + ", " + "sentTime=" + sentTime + "]";
    }
}
