package in.tranquilsoft.groupmessager.consumer;

import java.util.List;

import in.tranquilsoft.groupmessager.model.DefaultEntity;

/**
 * Created by gurdevp on 05/12/15.
 */
public interface MultiResultSqliteConsumer<T> {
    void performActionOnMultiRowQueryResult(int requestType, List<T> entities);
}
