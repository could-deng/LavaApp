package lava.bluepay.com.lavaapp.common.pay;

/**
 * 异步任务的回调
 */

public interface IExecutorCallback {
    void onExecuted(int result,String msg);
}
