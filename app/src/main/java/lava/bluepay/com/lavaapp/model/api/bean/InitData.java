package lava.bluepay.com.lavaapp.model.api.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bluepay on 2017/10/20.
 */

public class InitData extends BaseBean {

    /**
     * data : {"time":"3:10,10:00","switch":1,"sub_key_sms":"R 01","shorcode":"4533059","check_sub_api":"http://192.168.4.210:8168/v1/checksub.api","upgrade":null}
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
         * time : 3:10,10:00 (订阅开关控制时间点
         * switch : 1 (订阅开关 1 开   2 关
         * sub_key_sms : R 01  (命令字
         * shorcode : 4533059 (短码
         * check_sub_api : http://192.168.4.210:8168/v1/checksub.api (订阅查询API
         * upgrade : null (当前版本是否升级  0 不升级 1 升级  2 强制升级
         */

        private String time;
        @SerializedName("switch")
        private int switchX;
        private String sub_key_sms;
        private String shorcode;
        private String check_sub_api;
        private Object upgrade;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getSwitchX() {
            return switchX;
        }

        public void setSwitchX(int switchX) {
            this.switchX = switchX;
        }

        public String getSub_key_sms() {
            return sub_key_sms;
        }

        public void setSub_key_sms(String sub_key_sms) {
            this.sub_key_sms = sub_key_sms;
        }

        public String getShorcode() {
            return shorcode;
        }

        public void setShorcode(String shorcode) {
            this.shorcode = shorcode;
        }

        public String getCheck_sub_api() {
            return check_sub_api;
        }

        public void setCheck_sub_api(String check_sub_api) {
            this.check_sub_api = check_sub_api;
        }

        public Object getUpgrade() {
            return upgrade;
        }

        public void setUpgrade(Object upgrade) {
            this.upgrade = upgrade;
        }
    }
}
