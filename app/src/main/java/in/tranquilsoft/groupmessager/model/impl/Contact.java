package in.tranquilsoft.groupmessager.model.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.tranquilsoft.groupmessager.model.AbstractContact;
import in.tranquilsoft.groupmessager.model.MySqlLiteHelper;

public class Contact extends AbstractContact {
    static String TAG = "Contact";
    boolean dirty = false;
    boolean newContact = false;
    boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isNewContact() {
        return newContact;
    }

    public void setNewContact(boolean newContact) {
        this.newContact = newContact;
    }

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
            contact.setPhone(cursor.getString(0));
            contact.setName(cursor.getString(1));
            contact.setGroupId(cursor.getLong(2));

            //Log.e(TAG, "Returning "+contact);
            return contact;
        }
        //Log.e(TAG, "Returning null");
        return null;
    }

    public List<Contact> getByContactGrpId(Context context, Long contactGroupId) {
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.rawQuery("select c.* from " + Contact.TABLE_NAME + " c, " + GroupContactJunction.TABLE_NAME +
                " gcj where c." + Contact.ID_FIELD + "=gcj." + GroupContactJunction.ContactId_FIELD + " and "
                + " gcj." + GroupContactJunction.ContactGroupId_FIELD + "=?", new String[]{contactGroupId.toString()});

        if (cursor != null && cursor.moveToFirst()) {
            List<Contact> result = new ArrayList<>();
            do {
                Contact contact = new Contact();
                int idIdx = cursor.getColumnIndex(Contact.ID_FIELD);
                String phone= cursor.getString(idIdx);
                contact.setPhone(phone);

                int nameIdx = cursor.getColumnIndex(Contact.Name_FIELD);
                contact.setName(cursor.getString(nameIdx));
//                int phoneIdx = cursor.getColumnIndex(Contact.Phone_FIELD);
//                contact.setPhone(cursor.getString(phoneIdx));
                int grpIdIdx = cursor.getColumnIndex(Contact.GroupId_FIELD);
                contact.setGroupId(cursor.getLong(grpIdIdx));

                result.add(contact);
            } while (cursor.moveToNext());
            return result;
        }
        return null;
    }

    public List<Contact> getByContactByStartingPhrase(Context context, String phrase) {
        if (phrase == null) {
            return null;
        }
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, getColumns(),
                Name_FIELD+" like '"+phrase+"%'", null, null, null, Contact.Name_FIELD + " asc");

        if (cursor != null && cursor.moveToFirst()) {
            List<Contact> result = new ArrayList<>();
            do {
                Contact contact = new Contact();
                contact.setPhone(cursor.getString(0));
                contact.setName(cursor.getString(1));
                contact.setGroupId(cursor.getLong(2));

                result.add(contact);
            } while (cursor.moveToNext());
            return result;
        }
        return null;
    }


    public void deleteAll(Context context) {
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }
}