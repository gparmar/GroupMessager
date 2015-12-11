package in.tranquilsoft.groupmessager.task;

import android.content.Context;

import java.util.List;

import in.tranquilsoft.groupmessager.consumer.MultiResultSqliteConsumer;
import in.tranquilsoft.groupmessager.model.impl.GroupContactJunction;

/**
 * Created by gurdevp on 05/12/15.
 */
public class QueryForGrpContactJunctionsByGrpIdSqliteTask extends QueryForAllIdSqliteTask<GroupContactJunction> {
    private long groupId;

    public QueryForGrpContactJunctionsByGrpIdSqliteTask(Context context, MultiResultSqliteConsumer sqliteConsumer,
                                                        GroupContactJunction entity, int requestType, long grpId) {
        super(context, sqliteConsumer, entity, requestType);
        this.groupId = grpId;
    }

    @Override
    protected List<GroupContactJunction> doInBackground(Void... params) {
        return entity.getByContactGroup(context, groupId);
    }

    @Override
    protected void onPostExecute(List<GroupContactJunction> entities) {
        sqliteConsumer.performActionOnMultiRowQueryResult(requestType, entities);
    }

}
