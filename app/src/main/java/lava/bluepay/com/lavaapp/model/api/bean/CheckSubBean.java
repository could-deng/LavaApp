package lava.bluepay.com.lavaapp.model.api.bean;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;

import lava.bluepay.com.lavaapp.common.Utils;
import lava.bluepay.com.lavaapp.model.MemExchange;
import lava.bluepay.com.lavaapp.model.db.phoneNumRecord;

/**
 * Created by bluepay on 2017/10/20.
 */

public class CheckSubBean extends BaseBean {

    //- subscribe：是否能订阅 1 能  2不能
    //- status：A/T 在订购中 N/E 用户已退订  S/O 新用户 B/W 黑名单用户
    //- content ：内容说明
    //- msisdn：订阅手机号
    /**
     * data : {"subscribe":1,"status":"S/O","content":"1009 New User","msisdn":"13418638286"}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * subscribe : 1
         * status : S/O
         * content : 1009 New User
         * msisdn : 13418638286
         */

        private int subscribe;
        private String status;
        private String content;
        private String msisdn;

        public int getSubscribe() {
            return subscribe;
        }

        public void setSubscribe(int subscribe) {
            this.subscribe = subscribe;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getMsisdn() {
            return msisdn;
        }

        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }
    }

    /**
     * 根据服务器返回的是否订阅信息判断
     * @param data
     * @return
     */
    public static boolean ifHaveSubscribe(DataBean data){
        if(MemExchange.getInstance().getCanSee()){
            return true;
        }
        if(data !=null && !TextUtils.isEmpty(data.getStatus())){
            return (data.getStatus().equalsIgnoreCase("A/T"))?true:false;
        }
        if(data == null){
            return true;
        }
        return false;
    }

    /**
     * 根据已发送的imsi数据库表判断是否订阅
     * @param imsi
     * @return
     */
    public static boolean ifHaveSubscribe(String imsi){


        if(TextUtils.isEmpty(imsi) || MemExchange.getInstance().getCanSee()){
            return true;
        }
        if(MemExchange.getInstance().getCheckSubData()!=null){
            return ifHaveSubscribe(MemExchange.getInstance().getCheckSubData());
        }
        //
        return MemExchange.haveSendMsg;
    }
}
