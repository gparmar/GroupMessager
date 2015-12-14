package in.tranquilsoft.groupmessager.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import in.tranquilsoft.groupmessager.consumer.SingleResultSqliteConsumer;
import in.tranquilsoft.groupmessager.model.DefaultEntity;

/**
 * Created by gurdevp on 05/12/15.
 */
public class QueryByIdSqliteTask<ID> extends AsyncTask<Void, Void, DefaultEntity> {
    String TAG = "QueryByIdSqliteTask";
    Context context;
    DefaultEntity<ID> entity;
    ID id;
    SingleResultSqliteConsumer sqliteConsumer;
    int requestType;

    public QueryByIdSqliteTask(Context context, SingleResultSqliteConsumer sqliteConsumer,
                               DefaultEntity entity, ID id,
                               int requestType) {
        this.entity = entity;
        this.id = id;
        this.context = context;
        this.sqliteConsumer = sqliteConsumer;
        this.requestType = requestType;
    }


    @Override
    protected DefaultEntity doInBackground(Void... params) {
        Log.i(TAG, "Doing query by id:" + id);
        return entity.getById(context, id);
    }

    @Override
    protected void onPostExecute(DefaultEntity entity) {
        Log.i(TAG, "Got result:" + entity);
        sqliteConsumer.performActionOnQueryResult(requestType, entity);
    }
}
