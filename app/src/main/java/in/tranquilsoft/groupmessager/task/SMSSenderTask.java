package in.tranquilsoft.groupmessager.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import in.tranquilsoft.groupmessager.MainActivity;
import in.tranquilsoft.groupmessager.model.impl.Contact;
import in.tranquilsoft.groupmessager.model.impl.MessageSentStatus;
import in.tranquilsoft.groupmessager.model.impl.MessagingHistory;
import in.tranquilsoft.groupmessager.util.Constants;

/**
 * Created by gurdevp on 10/12/15.
 */
public class SMSSenderTask extends AsyncTask<Void, Integer, Void> {


    public static final int SMS_SENT_REQUEST = 1;
    public static final int SMS_DELIVERY_REQUEST = 2;
    String TAG = "SMSSenderTask";
    long contactGrpId;
    String sms;
    Context context;
    ProgressDialog progress;




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

        Intent sentIntent = new Intent(MainActivity.SENT_ACTION);
        sentIntent.putExtra(MainActivity.HISTORY_ID, historyId);
        sentIntent.putExtra(MainActivity.CONTACT_GROUP_ID, contactGrpId);
//        sentIntent.putExtra(MainActivity.CONTACT_NAME, contact.getName());
//        sentIntent.putExtra(MainActivity.CONTACT_PHONE_NUMBER, contact.getPhone());
        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(context, SMS_SENT_REQUEST,
                sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent deliveryIntent = new Intent(MainActivity.DELIVERED_ACTION);
        deliveryIntent.putExtra(MainActivity.HISTORY_ID, historyId);
        deliveryIntent.putExtra(MainActivity.CONTACT_GROUP_ID, contactGrpId);
        //deliveryIntent.putExtra(CONTACT_ID, contact.getId());
//        deliveryIntent.putExtra(MainActivity.CONTACT_PHONE_NUMBER, contact.getPhone());
        PendingIntent deliveryPendingIntent = PendingIntent.getBroadcast(context, SMS_SENT_REQUEST,
                deliveryIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        for (Contact contact : contacts) {
            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(contact.getPhone(), null, sms, sentPendingIntent, deliveryPendingIntent);
            publishProgress(count++, size);

            MessageSentStatus mss=new MessageSentStatus();
            mss.setPhone(contact.getPhone());
            mss.setSentAt(System.currentTimeMillis());
            mss.setHistoryId(historyId);
            mss.setSentStatus(Constants.FALSE);
            mss.setContactName(contact.getName());
            mss.create(context);
        }

        return null;
    }


//    private void MultipleSMS(String phoneNumber, String message) {
//        String SENT = "SMS_SENT";
//        String DELIVERED = "SMS_DELIVERED";
//
//        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(
//                SENT), 0);
//
//        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
//                new Intent(DELIVERED), 0);
//
//        // ---when the SMS has been sent---
//        context.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
//                        ContentValues values = new ContentValues();
//                        for (int i = 0; i < MobNumber.size() - 1; i++) {
//                            values.put("address", MobNumber.get(i).toString());
//                            // txtPhoneNo.getText().toString());
//                            values.put("body", MessageText.getText().toString());
//                        }
//                        context.getContentResolver().insert(
//                                Uri.parse("content://sms/sent"), values);
//                        Toast.makeText(context, "SMS sent",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                        Toast.makeText(context, "Generic failure",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//                        Toast.makeText(context, "No service",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                        Toast.makeText(context, "Null PDU",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//                        Toast.makeText(context, "Radio off",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(SENT));
//
//        // ---when the SMS has been delivered---
//        context.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(context, "SMS delivered",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        Toast.makeText(context, "SMS not delivered",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(DELIVERED));
//
//        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
//    }

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
