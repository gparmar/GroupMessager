package in.tranquilsoft.groupmessager.model.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import in.tranquilsoft.groupmessager.model.AbstractGroupContactJunction;
import in.tranquilsoft.groupmessager.model.MySqlLiteHelper;

public class GroupContactJunction extends AbstractGroupContactJunction {
    static String TAG = "GroupContactJunction";

    public List<GroupContactJunction> getByContactGroup(Context context, long contactGroupId) {
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, getColumns(),
                ContactGroupId_FIELD + "=?", new String[]{String.valueOf(contactGroupId)}, null, null, null);

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

    public void deleteByContactGroupIdContactId(Context context, long contactGrpId, long contactId) {
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME + " where " + ContactGroupId_FIELD + "=? and "
                + ContactId_FIELD + "=?", new Object[]{contactGrpId, contactId});
    }
}