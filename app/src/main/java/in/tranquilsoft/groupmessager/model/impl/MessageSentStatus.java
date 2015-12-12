package in.tranquilsoft.groupmessager.model.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.tranquilsoft.groupmessager.model.AbstractMessageSentStatus;
import in.tranquilsoft.groupmessager.model.MySqlLiteHelper;
import in.tranquilsoft.groupmessager.util.Constants;

public class MessageSentStatus extends AbstractMessageSentStatus {
    static String TAG = "MessageSentStatus";
    Contact contact;

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public MessageSentStatus getByHistoryIdContactId(Context context, long historyId,
                                                     long contactId) {
        Log.i(TAG, "Doing query by historyId:" + historyId+
        ", contactId:"+contactId);
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, getColumns(),
                HistoryId_FIELD + "= ? and "+ContactId_FIELD+"=?", new String[]{String.valueOf(historyId)
                ,String.valueOf(contactId)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Log.d(TAG, "Cursor was not null and move to first");
            MessageSentStatus messageSentStatus = new MessageSentStatus();
            messageSentStatus.setId(cursor.getLong(0));
            messageSentStatus.setHistoryId(cursor.getLong(1));
            messageSentStatus.setContactId(cursor.getLong(2));
            messageSentStatus.setSentStatus(cursor.getInt(3));
            messageSentStatus.setSentAt(cursor.getLong(4));
            messageSentStatus.setDeliveryStatus(cursor.getInt(5));
            messageSentStatus.setDeliveredAt(cursor.getLong(6));



            return messageSentStatus;
        }
        return null;
    }

    public List<MessageSentStatus> getHistoryId(Context context, long historyId) {
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, getColumns(),
                HistoryId_FIELD+"=?", new String[]{String.valueOf(historyId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            List<MessageSentStatus> result = new ArrayList<>();
            do {
                MessageSentStatus messageSentStatus = new MessageSentStatus();
                messageSentStatus.setId(cursor.getLong(0));
                messageSentStatus.setHistoryId(cursor.getLong(1));
                messageSentStatus.setContactId(cursor.getLong(2));
                messageSentStatus.setSentStatus(cursor.getInt(3));
                messageSentStatus.setSentAt(cursor.getLong(4));
                messageSentStatus.setDeliveryStatus(cursor.getInt(5));
                messageSentStatus.setDeliveredAt(cursor.getLong(6));
                messageSentStatus.setContact(new Contact().getById(context, messageSentStatus.getContactId()));
                result.add(messageSentStatus);
            } while (cursor.moveToNext());
            return result;
        }
        return null;
    }
}