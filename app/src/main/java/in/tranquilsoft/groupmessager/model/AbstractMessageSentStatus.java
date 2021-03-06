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

import in.tranquilsoft.groupmessager.model.impl.MessageSentStatus;
import in.tranquilsoft.groupmessager.util.Constants;

public class AbstractMessageSentStatus extends DefaultEntity<Long> implements Parcelable {
    public static final String TAG = "MessageSentStatus";
    public static final String TABLE_NAME = "MessageSentStatus";
    public static final String ID_FIELD = "id";
    public static final String HistoryId_FIELD = "history_id";
    public static final String ContactId_FIELD = "phone";
    public static final String SentStatus_FIELD = "sent_status";
    public static final String SentAt_FIELD = "sent_at";
    public static final String DeliveryStatus_FIELD = "delivery_status";
    public static final String DeliveredAt_FIELD = "delivered_at";
    public static final String ContactName_FIELD = "contact_name";

    public static final String[] OTHER_FIELDS = new String[]{HistoryId_FIELD,
            ContactId_FIELD, SentStatus_FIELD, SentAt_FIELD, DeliveryStatus_FIELD,
            DeliveredAt_FIELD, ContactName_FIELD};
    public static final String TABLE_CREATE_SQL = "create table " + TABLE_NAME +
            "(id integer primary key autoincrement,history_id integer,phone text," +
            "sent_status integer,sent_at integer,delivery_status integer," +
            "delivered_at integer, contact_name text)";

    private long id;
    private long historyId;
    private String phone;
    private int sentStatus;
    private long sentAt=-1l;
    private int deliveryStatus;
    private long deliveredAt=-1l;
    private String contactName;

    public AbstractMessageSentStatus() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(long historyId) {
        this.historyId = historyId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getSentStatus() {
        return sentStatus;
    }

    public void setSentStatus(int sentStatus) {
        this.sentStatus = sentStatus;
    }

    public long getSentAt() {
        return sentAt;
    }

    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }

    public int getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(int deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public long getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(long deliveredAt) {
        this.deliveredAt = deliveredAt;
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

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @Override
    public long create(Context context) {
        ContentValues cv = new ContentValues();
        cv.put(HistoryId_FIELD, getHistoryId());
        cv.put(ContactId_FIELD, getPhone());
        cv.put(SentStatus_FIELD, getSentStatus());
        cv.put(SentAt_FIELD, getSentAt());
        cv.put(DeliveryStatus_FIELD, getDeliveryStatus());
        cv.put(DeliveredAt_FIELD, getDeliveredAt());
        cv.put(ContactName_FIELD, getContactName());
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getWritableDatabase();
        long result = db.insert(TABLE_NAME, null, cv);
        db.close();
        return result;
    }

    @Override
    public void update(Context context) {
        Log.i(TAG, "Updating " + this);
        ContentValues cv = new ContentValues();
        cv.put(HistoryId_FIELD, getHistoryId());
        cv.put(ContactId_FIELD, getPhone());
        cv.put(SentStatus_FIELD, getSentStatus());
        cv.put(SentAt_FIELD, getSentAt());
        cv.put(DeliveryStatus_FIELD, getDeliveryStatus());
        cv.put(DeliveredAt_FIELD, getDeliveredAt());
        cv.put(ContactName_FIELD, getContactName());
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
    public MessageSentStatus getById(Context context, Long id) {
        Log.i(TAG, "Doing query by id:" + id);
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, getColumns(),
                ID_FIELD + "= ?", new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Log.d(TAG, "Cursor was not null and move to first");
            MessageSentStatus messageSentStatus = new MessageSentStatus();
            messageSentStatus.setId(cursor.getLong(0));
            messageSentStatus.setHistoryId(cursor.getLong(1));
            messageSentStatus.setPhone(cursor.getString(2));
            messageSentStatus.setSentStatus(cursor.getInt(3));
            messageSentStatus.setSentAt(cursor.getLong(4));
            messageSentStatus.setDeliveryStatus(cursor.getInt(5));
            messageSentStatus.setDeliveredAt(cursor.getLong(6));
            messageSentStatus.setContactName(cursor.getString(7));
            return messageSentStatus;
        }
        return null;
    }

    @Override
    public List<MessageSentStatus> getAll(Context context) {
        SQLiteDatabase db = MySqlLiteHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, getColumns(),
                null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            List<MessageSentStatus> result = new ArrayList<>();
            do {
                MessageSentStatus messageSentStatus = new MessageSentStatus();
                messageSentStatus.setId(cursor.getLong(0));
                messageSentStatus.setHistoryId(cursor.getLong(1));
                messageSentStatus.setPhone(cursor.getString(2));
                messageSentStatus.setSentStatus(cursor.getInt(3));
                messageSentStatus.setSentAt(cursor.getLong(4));
                messageSentStatus.setDeliveryStatus(cursor.getInt(5));
                messageSentStatus.setDeliveredAt(cursor.getLong(6));
                messageSentStatus.setContactName(cursor.getString(7));
                result.add(messageSentStatus);
            } while (cursor.moveToNext());
            return result;
        }
        return null;
    }

    @Override
    public String getIdField() {
        return ID_FIELD;
    }

    @Override
    public String[] getOtherFields() {
        return OTHER_FIELDS;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected AbstractMessageSentStatus(Parcel in) {
        id = in.readLong();
        historyId = in.readLong();
        phone = in.readString();
        sentStatus = in.readInt();
        sentAt = in.readLong();
        deliveryStatus = in.readInt();
        deliveredAt = in.readLong();
        contactName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeLong(historyId);
        out.writeString(phone);
        out.writeInt(sentStatus);
        out.writeLong(sentAt);
        out.writeInt(deliveryStatus);
        out.writeLong(deliveredAt);
        out.writeString(contactName);
    }

    public static final Parcelable.Creator<AbstractMessageSentStatus> CREATOR
            = new Parcelable.Creator<AbstractMessageSentStatus>() {
        public AbstractMessageSentStatus createFromParcel(Parcel in) {
            return new AbstractMessageSentStatus(in);
        }

        public AbstractMessageSentStatus[] newArray(int size) {
            return new AbstractMessageSentStatus[size];
        }
    };

    public String toString() {
        return "MessageSentStatus:[" + "id=" + id + ", " + "historyId=" + historyId + ", " +
                "phone=" + phone + ", " + "sentStatus=" + sentStatus + ", " + "sentAt=" +
                sentAt + ", " + "deliveryStatus=" + deliveryStatus + ", " + "deliveredAt="
                + deliveredAt +", "+"contactName="+contactName+ "]";
    }
}
