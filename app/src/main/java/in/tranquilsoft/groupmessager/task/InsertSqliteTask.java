package in.tranquilsoft.groupmessager.task;

import android.content.Context;
import android.os.AsyncTask;

import in.tranquilsoft.groupmessager.consumer.InsertDBConsumer;
import in.tranquilsoft.groupmessager.model.DefaultEntity;

/**
 * Created by gurdevp on 05/12/15.
 */
public class InsertSqliteTask extends AsyncTask<Void, Void, Long> {
    Context context;
    DefaultEntity entity;
    InsertDBConsumer sqliteConsumer;
    int requestType;
    String error;

    public InsertSqliteTask(Context context, InsertDBConsumer sqliteConsumer,
                            DefaultEntity entity,
                            int requestType) {
        this.entity = entity;
        this.context = context;
        this.sqliteConsumer = sqliteConsumer;
        this.requestType = requestType;
    }

    @Override
    protected Long doInBackground(Void... params) {
        long result = -1;
        try {
            result = entity.create(context);
        } catch (Exception e) {
            error = e.getMessage();
        }

        return result;
    }

    @Override
    protected void onPostExecute(Long generatedId) {
        sqliteConsumer.performOnInsertResult(requestType, generatedId,
                generatedId==-1?InsertDBConsumer.FAILED:InsertDBConsumer.SUCCESS,error);
    }
}
