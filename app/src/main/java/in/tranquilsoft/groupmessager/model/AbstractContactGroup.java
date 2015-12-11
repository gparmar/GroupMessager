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

import in.tranquilsoft.groupmessager.model.impl.ContactGroup;

public class AbstractContactGroup extends DefaultEntity implements Parcelable {
    public static final String TAG = "ContactGroup";
    public static final String TABLE_NAME = "ContactGroup";
    public static final String ID_FIELD = "group_id";
    public static final String GroupName_FIELD = "group_name";

    public static final String[] OTHER_FIELDS = new String[]{GroupName_FIELD};
    public static final String TABLE_CREATE_SQL = "create table " + TABLE_NAME + "(group_id integer primary key autoincrement,group_name text)";

    private long groupId;
    private String groupName;


    public AbstractContactGroup() {
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
        cv.put(GroupName_FIELD, getGroupName());

        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getWritableDatabase();
        long result = db.insert(TABLE_NAME, null, cv);
        db.close();
        return result;
    }

    @Override
    public void update(Context context) {
        Log.i(TAG, "Updating " + this);
        ContentValues cv = new ContentValues();
        cv.put(GroupName_FIELD, getGroupName());

        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getWritableDatabase();
        db.update(TABLE_NAME, cv, ID_FIELD + "= ?", new String[]{String.valueOf(getGroupId())});
        db.close();

    }

    @Override
    public void delete(Context context) {
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getWritableDatabase();
        db.delete(TABLE_NAME, ID_FIELD + "= ?", new String[]{String.valueOf(getGroupId())});
        db.close();
    }

    @Override
    public ContactGroup getById(Context context, long id) {
        Log.i(TAG, "Doing query by id:" + id);
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, getColumns(),
                ID_FIELD + "= ?", new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Log.d(TAG, "Cursor was not null and move to first");
            ContactGroup contactGroup = new ContactGroup();
            contactGroup.setGroupId(cursor.getLong(0));
            contactGroup.setGroupName(cursor.getString(1));

            return contactGroup;
        }
        return null;
    }

    @Override
    public List<ContactGroup> getAll(Context context) {
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, getColumns(),
                null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            List<ContactGroup> result = new ArrayList<>();
            do {
                ContactGroup contactGroup = new ContactGroup();
                contactGroup.setGroupId(cursor.getLong(0));
                contactGroup.setGroupName(cursor.getString(1));

                result.add(contactGroup);
            } while (cursor.moveToNext());
            return result;
        }
        return null;
    }

    @Override
    public String getIdField() {
        return "group_id";
    }

    @Override
    public String[] getOtherFields() {
        return OTHER_FIELDS;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected AbstractContactGroup(Parcel in) {
        groupId = in.readLong();
        groupName = in.readString();

    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(groupId);
        out.writeString(groupName);

    }

    public static final Parcelable.Creator<AbstractContactGroup> CREATOR
            = new Parcelable.Creator<AbstractContactGroup>() {
        public AbstractContactGroup createFromParcel(Parcel in) {
            return new AbstractContactGroup(in);
        }

        public AbstractContactGroup[] newArray(int size) {
            return new AbstractContactGroup[size];
        }
    };

    public String toString() {
        return "ContactGroup:[" + "groupId=" + groupId + ", " + "groupName=" + groupName + "]";
    }
}
