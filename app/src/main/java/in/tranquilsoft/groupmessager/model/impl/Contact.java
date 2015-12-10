package in.tranquilsoft.groupmessager.model.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import in.tranquilsoft.groupmessager.model.AbstractContact;
import in.tranquilsoft.groupmessager.model.MySqlLiteHelper;

public class Contact extends AbstractContact {
    static String TAG = "Contact";
    boolean dirty = false;

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public Contact getByPhone(Context context, String phone) {
        Log.i(TAG, "Doing query by phone:" + phone);
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, getColumns(),
                ID_FIELD + "= ?", new String[]{phone},
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

    public void deleteAll(Context context){
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getWritableDatabase();
        db.execSQL("delete from "+TABLE_NAME);
    }
}