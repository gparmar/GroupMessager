package in.tranquilsoft.groupmessager.consumer;

import in.tranquilsoft.groupmessager.model.DefaultEntity;

/**
 * Created by gurdevp on 05/12/15.
 */
public interface SingleResultSqliteConsumer {
    void performActionOnQueryResult(int requestType, DefaultEntity entity);
}
