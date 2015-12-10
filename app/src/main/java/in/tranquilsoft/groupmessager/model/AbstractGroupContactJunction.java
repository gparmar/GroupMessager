package in.tranquilsoft.groupmessager.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import in.tranquilsoft.groupmessager.model.impl.GroupContactJunction;

public class AbstractGroupContactJunction extends DefaultEntity implements Parcelable {
    public static final String TAG = "GroupContactJunction";
    public static final String TABLE_NAME = "GroupContactJunction";
    public static final String ID_FIELD = "id";
    public static final String ContactId_FIELD = "contact_id";
public static final String ContactGroupId_FIELD = "contact_group_id";

    public static final String[] OTHER_FIELDS = new String[]{ContactId_FIELD,ContactGroupId_FIELD};
    public static final String TABLE_CREATE_SQL = "create table " + TABLE_NAME + "(id integer primary key autoincrement,contact_id integer,contact_group_id integer)";

    	private long id;
	private long contactId;
	private long contactGroupId;


    public AbstractGroupContactJunction() {}

    	public long getId(){
		return id;
	}

	public void setId(long id){
		this.id = id;
	}

	public long getContactId(){
		return contactId;
	}

	public void setContactId(long contactId){
		this.contactId = contactId;
	}

	public long getContactGroupId(){
		return contactGroupId;
	}

	public void setContactGroupId(long contactGroupId){
		this.contactGroupId = contactGroupId;
	}



    @Override
    public void createTable(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_SQL);
    }

    @Override
    public void dropTable(SQLiteDatabase db) {
        db.execSQL("drop table "+TABLE_NAME);
    }

    @Override
    public void updateTable(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public long create(Context context) {
        ContentValues cv = new ContentValues();
        cv.put(ContactId_FIELD, getContactId());
cv.put(ContactGroupId_FIELD, getContactGroupId());

        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getWritableDatabase();
        long result = db.insert(TABLE_NAME, null, cv);
        db.close();
        return result;
    }

    @Override
    public void update(Context context) {
        Log.i(TAG,"Updating "+this);
        ContentValues cv = new ContentValues();
        cv.put(ContactId_FIELD, getContactId());
cv.put(ContactGroupId_FIELD, getContactGroupId());

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
    public GroupContactJunction getById(Context context, long id) {
        Log.i(TAG, "Doing query by id:"+id);
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, getColumns(),
                        ID_FIELD + "= ?", new String[]{String.valueOf(id)},
                        null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Log.d(TAG, "Cursor was not null and move to first");
            GroupContactJunction groupContactJunction = new GroupContactJunction();
            groupContactJunction.setId(cursor.getLong(0));
groupContactJunction.setContactId(cursor.getLong(1));
groupContactJunction.setContactGroupId(cursor.getLong(2));

            return groupContactJunction;
        }
        return null;
    }

    @Override
    public List<GroupContactJunction> getAll(Context context) {
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, getColumns(),
                        null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            List<GroupContactJunction> result = new ArrayList<>();
            do {
                GroupContactJunction groupContactJunction = new GroupContactJunction();
                groupContactJunction.setId(cursor.getLong(0));
groupContactJunction.setContactId(cursor.getLong(1));
groupContactJunction.setContactGroupId(cursor.getLong(2));

                result.add(groupContactJunction);
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

    protected AbstractGroupContactJunction(Parcel in) {
        		id=in.readLong();
		contactId=in.readLong();
		contactGroupId=in.readLong();

    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        		out.writeLong(id);
		out.writeLong(contactId);
		out.writeLong(contactGroupId);

    }

    public static final Parcelable.Creator<AbstractGroupContactJunction> CREATOR
                 = new Parcelable.Creator<AbstractGroupContactJunction>() {
             public AbstractGroupContactJunction createFromParcel(Parcel in) {
                 return new AbstractGroupContactJunction(in);
             }

             public AbstractGroupContactJunction[] newArray(int size) {
                 return new AbstractGroupContactJunction[size];
             }
         };

    public String toString() {
         return "GroupContactJunction:["+"id="+id+ ", " +"contactId="+contactId+ ", " +"contactGroupId="+contactGroupId+"]";
    }
}
