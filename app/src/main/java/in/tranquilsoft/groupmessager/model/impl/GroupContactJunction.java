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

    public List<String> getByContactGroup(Context context, long contactGroupId) {
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, getColumns(),
                ContactGroupId_FIELD + "=?", new String[]{String.valueOf(contactGroupId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            List<String> result = new ArrayList<>();
            do {
                result.add(cursor.getString(1));

                //result.add(groupContactJunction);
            } while (cursor.moveToNext());
            return result;
        }
        return null;
    }

    public int getSelectedContactCount(Context context, long contactGroupId) {
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.rawQuery("select count(*) count from " + TABLE_NAME + " where "
                + ContactGroupId_FIELD + "=?"
                , new String[]{contactGroupId + ""});

        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            try {
                return cursor.getInt(cursor.getColumnIndex("count"));
            }
            catch (Exception e){}
        }
        return 0;
    }

    public void deleteByContactGroupIdAndPhone(Context context, long contactGrpId, String phone) {
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME + " where " + ContactGroupId_FIELD + "=? and "
                + ContactId_FIELD + "=?", new Object[]{contactGrpId, phone});
    }
}