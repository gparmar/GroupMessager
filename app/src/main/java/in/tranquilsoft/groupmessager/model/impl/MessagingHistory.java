package in.tranquilsoft.groupmessager.model.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import in.tranquilsoft.groupmessager.model.AbstractMessagingHistory;
import in.tranquilsoft.groupmessager.model.MySqlLiteHelper;

public class MessagingHistory extends AbstractMessagingHistory {
    static String TAG = "MessagingHistory";

    ContactGroup group;

    public ContactGroup getGroup() {
        return group;
    }

    public void setGroup(ContactGroup group) {
        this.group = group;
    }

    public List<MessagingHistory> getAll(Context context) {
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, getColumns(),
                null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            List<MessagingHistory> result = new ArrayList<>();
            do {
                MessagingHistory messagingHistory = new MessagingHistory();
                messagingHistory.setId(cursor.getLong(0));
                messagingHistory.setGroupId(cursor.getLong(1));
                messagingHistory.setSentTime(cursor.getLong(2));
                messagingHistory.setSmsMessage(cursor.getString(3));

                messagingHistory.setGroup(new ContactGroup().getById(context, messagingHistory.getGroupId()));
                result.add(messagingHistory);
            } while (cursor.moveToNext());
            return result;
        }
        return null;
    }
}