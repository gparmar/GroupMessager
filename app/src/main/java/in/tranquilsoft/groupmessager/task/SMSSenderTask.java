package in.tranquilsoft.groupmessager.task;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.List;

import in.tranquilsoft.groupmessager.model.impl.Contact;
import in.tranquilsoft.groupmessager.model.impl.MessageSentStatus;
import in.tranquilsoft.groupmessager.model.impl.MessagingHistory;
import in.tranquilsoft.groupmessager.util.Constants;

/**
 * Created by gurdevp on 10/12/15.
 */
public class SMSSenderTask extends AsyncTask<Void, Integer, Void> {
    public static final String CONTACT_PHONE_NUMBER = "CONTACT_PHONE_NUMBER";
    public static final String CONTACT_NAME = "CONTACT_NAME";
    public static final String PHONE = "PHONE";
    public static final String CONTACT_GROUP_ID = "CONTACT_GROUP_ID";
    public static final String HISTORY_ID = "HISTORY_ID";

    public static final int SMS_SENT_REQUEST = 1;
    public static final int SMS_DELIVERY_REQUEST = 2;
    String TAG = "SMSSenderTask";
    long contactGrpId;
    String sms;
    Context context;
    ProgressDialog progress;

    public static class MySentBroadcastReceiver extends BroadcastReceiver {
        public MySentBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String phone = intent.getStringExtra(CONTACT_PHONE_NUMBER);
            long contactGrpId = intent.getLongExtra(CONTACT_GROUP_ID, -1);
            long historyId = intent.getLongExtra(HISTORY_ID, -1);
            String contactName = intent.getStringExtra(CONTACT_NAME);

            MessageSentStatus mss = new MessageSentStatus().getByHistoryIdAndPhone(context, historyId, phone);
            if (mss == null) {
                mss=new MessageSentStatus();
                mss.setPhone(phone);
                mss.setSentAt(System.currentTimeMillis());
                mss.setHistoryId(historyId);
                mss.setSentStatus(Constants.TRUE);
                mss.setContactName(contactName);
                mss.create(context);
            } else {
                mss.setSentAt(System.currentTimeMillis());
                mss.setSentStatus(Constants.TRUE);
                mss.update(context);
            }

            Log.e("MySentBroadcastReceiver", "Sent message for contact grp id:" + contactGrpId +
                    ", phone:" + phone);
        }
    }

    public static class MyDeliveryBroadcastReceiver extends BroadcastReceiver {
        public MyDeliveryBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String phone = intent.getStringExtra(CONTACT_PHONE_NUMBER);
            long contactGrpId = intent.getLongExtra(CONTACT_GROUP_ID, -1);
            long historyId = intent.getLongExtra(HISTORY_ID, -1);
            String contactName = intent.getStringExtra(CONTACT_NAME);

            MessageSentStatus mss = new MessageSentStatus().getByHistoryIdAndPhone(context, historyId, phone);
            if (mss == null) {
                mss=new MessageSentStatus();
                mss.setPhone(phone);
                mss.setDeliveredAt(System.currentTimeMillis());
                mss.setHistoryId(historyId);
                mss.setDeliveredAt(Constants.TRUE);
                mss.setContactName(contactName);
                mss.create(context);
            } else {
                mss.setDeliveredAt(System.currentTimeMillis());
                mss.setDeliveryStatus(Constants.TRUE);
                mss.update(context);
            }
            Log.e("MyDlvryBrcastReceiver", "Delivered message for contact grp id:" + contactGrpId +
                    ", phone:" + phone);
        }
    }


    public SMSSenderTask(long contactGrpId, String sms, Context context) {
        this.contactGrpId = contactGrpId;
        this.sms = sms;
        this.context = context;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = new ProgressDialog(context);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        progress.setMessage("Sending SMSes to the contacts in this group. Please wait...");
        progress.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        List<Contact> contacts = new Contact().getByContactGrpId(context, contactGrpId);
        Log.e(TAG, "contacts:" + contacts);
        int count = 0;
        int size = contacts.size();
        MessagingHistory history = new MessagingHistory();
        history.setGroupId(contactGrpId);
        history.setSentTime(System.currentTimeMillis());
        history.setSmsMessage(sms);
        long historyId = history.create(context);
        for (Contact contact : contacts) {
            SmsManager smsManager = SmsManager.getDefault();
            Intent sentIntent = new Intent(context, MySentBroadcastReceiver.class);
            sentIntent.putExtra(HISTORY_ID, historyId);
            sentIntent.putExtra(CONTACT_GROUP_ID, contactGrpId);
            sentIntent.putExtra(CONTACT_NAME, contact.getName());
            sentIntent.putExtra(CONTACT_PHONE_NUMBER, contact.getPhone());
            PendingIntent sentPendingIntent = PendingIntent.getBroadcast(context, SMS_SENT_REQUEST,
                    sentIntent, PendingIntent.FLAG_ONE_SHOT);
            Intent deliveryIntent = new Intent(context, MyDeliveryBroadcastReceiver.class);
            deliveryIntent.putExtra(HISTORY_ID, historyId);
            deliveryIntent.putExtra(CONTACT_GROUP_ID, contactGrpId);
            //deliveryIntent.putExtra(CONTACT_ID, contact.getId());
            deliveryIntent.putExtra(CONTACT_PHONE_NUMBER, contact.getPhone());
            PendingIntent deliveryPendingIntent = PendingIntent.getBroadcast(context, SMS_SENT_REQUEST,
                    deliveryIntent, PendingIntent.FLAG_ONE_SHOT);
            smsManager.sendTextMessage(contact.getPhone(), null, sms, sentPendingIntent, deliveryPendingIntent);
            publishProgress(count++, size);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progress.dismiss();
        new AlertDialog.Builder(context).setMessage("SMS has been sent to all contacts.")
                .setPositiveButton("OK", null).show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progress.setProgress(values[0]);
        progress.setMax(values[1]);
    }
}
