package in.tranquilsoft.groupmessager.consumer;

/**
 * Created by gurdevp on 05/12/15.
 */
public interface InsertDBConsumer extends DefaultConsumer {
    void performOnInsertResult(int requestType, Long generatedId, int result, String error);
}
