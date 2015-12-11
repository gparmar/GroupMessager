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

import in.tranquilsoft.groupmessager.model.impl.Contact;

public class AbstractContact extends DefaultEntity implements Parcelable {
    public static final String TAG = "Contact";
    public static final String TABLE_NAME = "Contact";
    public static final String ID_FIELD = "id";
    public static final String Name_FIELD = "name";
    public static final String Phone_FIELD = "phone";
    public static final String GroupId_FIELD = "group_id";

    public static final String[] OTHER_FIELDS = new String[]{Name_FIELD, Phone_FIELD, GroupId_FIELD};
    public static final String TABLE_CREATE_SQL = "create table " + TABLE_NAME + "(id integer primary key autoincrement,name text,phone text,group_id integer)";

    private long id;
    private String name;
    private String phone;
    private long groupId;


    public AbstractContact() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
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
        cv.put(Name_FIELD, getName());
        cv.put(Phone_FIELD, getPhone());
        cv.put(GroupId_FIELD, getGroupId());

        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getWritableDatabase();
        long result = db.insert(TABLE_NAME, null, cv);
        db.close();
        return result;
    }

    @Override
    public void update(Context context) {
        Log.i(TAG, "Updating " + this);
        ContentValues cv = new ContentValues();
        cv.put(Name_FIELD, getName());
        cv.put(Phone_FIELD, getPhone());
        cv.put(GroupId_FIELD, getGroupId());

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
    public Contact getById(Context context, long id) {
        Log.i(TAG, "Doing query by id:" + id);
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, getColumns(),
                ID_FIELD + "= ?", new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Log.d(TAG, "Cursor was not null and move to first");
            Contact contact = new Contact();
            contact.setId(cursor.getLong(0));
            contact.setName(cursor.getString(1));
            contact.setPhone(cursor.getString(2));
            contact.setGroupId(cursor.getLong(3));

            return contact;
        }
        return null;
    }

    @Override
    public List<Contact> getAll(Context context) {
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, getColumns(),
                null, null, null, null, Contact.Name_FIELD + " asc");
        if (cursor != null && cursor.moveToFirst()) {
            List<Contact> result = new ArrayList<>();
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getLong(0));
                contact.setName(cursor.getString(1));
                contact.setPhone(cursor.getString(2));
                contact.setGroupId(cursor.getLong(3));

                result.add(contact);
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

    protected AbstractContact(Parcel in) {
        id = in.readLong();
        name = in.readString();
        phone = in.readString();
        groupId = in.readLong();

    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(name);
        out.writeString(phone);
        out.writeLong(groupId);

    }

    public static final Parcelable.Creator<AbstractContact> CREATOR
            = new Parcelable.Creator<AbstractContact>() {
        public AbstractContact createFromParcel(Parcel in) {
            return new AbstractContact(in);
        }

        public AbstractContact[] newArray(int size) {
            return new AbstractContact[size];
        }
    };

    public String toString() {
        return "Contact:[" + "id=" + id + ", " + "name=" + name + ", " + "phone=" + phone + ", " + "groupId=" + groupId + "]";
    }
}
