package in.tranquilsoft.groupmessager.task;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import in.tranquilsoft.groupmessager.consumer.MultiResultSqliteConsumer;
import in.tranquilsoft.groupmessager.model.impl.GroupContactJunction;

/**
 * Created by gurdevp on 05/12/15.
 */
public class QueryForGrpContactJunctionsByGrpIdSqliteTask extends AsyncTask<Void, Void, List<String>> {
    private long groupId;
    private GroupContactJunction entity;
    Context context;
    MultiResultSqliteConsumer sqliteConsumer;
    int requestType;
    public QueryForGrpContactJunctionsByGrpIdSqliteTask(Context context, MultiResultSqliteConsumer sqliteConsumer,
                                                        GroupContactJunction entity, int requestType, long grpId) {
        this.context = context;
        this.groupId = grpId;
        this.sqliteConsumer = sqliteConsumer;
        this.entity = entity;
        this.requestType = requestType;

    }

    @Override
    protected List<String> doInBackground(Void... params) {
        return entity.getByContactGroup(context, groupId);
    }

    @Override
    protected void onPostExecute(List<String> entities) {
        if (sqliteConsumer != null) {
            sqliteConsumer.performActionOnMultiRowQueryResult(requestType, entities);
        }
    }

}
