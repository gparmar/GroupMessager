package in.tranquilsoft.groupmessager.task;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import in.tranquilsoft.groupmessager.consumer.MultiResultSqliteConsumer;
import in.tranquilsoft.groupmessager.model.DefaultEntity;

/**
 * Created by gurdevp on 05/12/15.
 */
public class QueryForAllIdSqliteTask<T> extends AsyncTask<Void, Void, List<T>> {
    protected Context context;
    protected T entity;
    protected MultiResultSqliteConsumer sqliteConsumer;
    protected int requestType;

    public QueryForAllIdSqliteTask(Context context, MultiResultSqliteConsumer sqliteConsumer,
                                   T entity, int requestType) {
        this.entity = entity;
        this.context = context;
        this.sqliteConsumer = sqliteConsumer;
        this.requestType = requestType;
    }

    @Override
    protected List<T> doInBackground(Void... params) {
        return ((DefaultEntity)entity).getAll(context);
    }

    @Override
    protected void onPostExecute(List<T> entities) {
        sqliteConsumer.performActionOnMultiRowQueryResult(requestType, entities);
    }

}
