package lava.bluepay.com.lavaapp.common.pay;

import android.content.Context;

/**
 * Created by bluepay on 2017/10/26.
 */

public class SmsSendBean {
    private Context context;
    private String toNum;
    private String content;

    public SmsSendBean(Context context, String toNum, String content) {
        this.context = context;
        this.toNum = toNum;
        this.content = content;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getToNum() {
        return toNum;
    }

    public void setToNum(String toNum) {
        this.toNum = toNum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
