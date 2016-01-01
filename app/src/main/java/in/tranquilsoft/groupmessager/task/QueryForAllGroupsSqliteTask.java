package in.tranquilsoft.groupmessager.task;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import in.tranquilsoft.groupmessager.consumer.MultiResultSqliteConsumer;
import in.tranquilsoft.groupmessager.model.DefaultEntity;
import in.tranquilsoft.groupmessager.model.impl.ContactGroup;
import in.tranquilsoft.groupmessager.model.impl.GroupContactJunction;

/**
 * Created by gurdevp on 05/12/15.
 */
public class QueryForAllGroupsSqliteTask extends AsyncTask<Void, Void, List<ContactGroup>> {
    protected Context context;
    protected ContactGroup entity;
    protected MultiResultSqliteConsumer sqliteConsumer;
    protected int requestType;

    public QueryForAllGroupsSqliteTask(Context context, MultiResultSqliteConsumer sqliteConsumer,
                                       ContactGroup entity, int requestType) {
        this.entity = entity;
        this.context = context;
        this.sqliteConsumer = sqliteConsumer;
        this.requestType = requestType;
    }

    @Override
    protected List<ContactGroup> doInBackground(Void... params) {
        List<ContactGroup> result = ((ContactGroup) entity).getAll(context);
        GroupContactJunction gcj = new GroupContactJunction();
        for (ContactGroup cg: result) {
            cg.setContactsCount(gcj.getSelectedContactCount(context, cg.getGroupId()));
        }
        return result;
    }

    @Override
    protected void onPostExecute(List<ContactGroup> entities) {
        if (sqliteConsumer != null) {
            sqliteConsumer.performActionOnMultiRowQueryResult(requestType, entities);
        }
    }

}
