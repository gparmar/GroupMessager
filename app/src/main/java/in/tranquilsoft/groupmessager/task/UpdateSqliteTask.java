package in.tranquilsoft.groupmessager.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import in.tranquilsoft.groupmessager.consumer.InsertDBConsumer;
import in.tranquilsoft.groupmessager.consumer.UpdateDBConsumer;
import in.tranquilsoft.groupmessager.model.DefaultEntity;

/**
 * Created by gurdevp on 05/12/15.
 */
public class UpdateSqliteTask extends AsyncTask<Void, Void, Void> {
    String TAG = "UpdateSqliteTask";
    Context context;
    DefaultEntity entity;
    UpdateDBConsumer sqliteConsumer;
    int requestType;
    String error;

    public UpdateSqliteTask(Context context, UpdateDBConsumer sqliteConsumer,
                            DefaultEntity entity,
                            int requestType) {
        this.entity = entity;
        this.context = context;
        this.sqliteConsumer = sqliteConsumer;
        this.requestType = requestType;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            entity.update(context);
        } catch (Exception e) {
            error = e.getMessage();
            Log.e(TAG, "", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.e(TAG, "error:" + error);
        if (sqliteConsumer != null) {
            sqliteConsumer.performOnUpdateResult(requestType,
                    error != null ? InsertDBConsumer.FAILED : InsertDBConsumer.SUCCESS, error);
        }
    }
}
