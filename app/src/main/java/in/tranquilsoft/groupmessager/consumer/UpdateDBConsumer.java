package in.tranquilsoft.groupmessager.consumer;

/**
 * Created by gurdevp on 05/12/15.
 */
public interface UpdateDBConsumer extends DefaultConsumer {
    void performOnUpdateResult(int requestType, int result, String error);
}
