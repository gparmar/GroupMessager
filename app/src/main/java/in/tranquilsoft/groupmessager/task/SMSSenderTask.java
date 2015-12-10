package in.tranquilsoft.groupmessager.task;

import android.os.AsyncTask;

import java.util.List;

import in.tranquilsoft.groupmessager.model.impl.Contact;

/**
 * Created by gurdevp on 10/12/15.
 */
public class SMSSenderTask extends AsyncTask<Void, Void, Void> {
    long contactGrpId;
    String sms;
    public SMSSenderTask(long contactGrpId, String sms) {
        this.contactGrpId = contactGrpId;
        this.sms = sms;
    }

    @Override
    protected Void doInBackground(Void... params) {
        List<Contact> contacts =
        return null;
    }
}
