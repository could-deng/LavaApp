package lava.bluepay.com.lavaapp.model.api.bean;

/**
 * Created by bluepay on 2017/11/7.
 */

public class PhoneNumBean {

    /**
     * ua : Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/604.3.5 (KHTML, like Gecko) Version/11.0.1 Safari/604.3.5
     * msisdn : null
     * oper : wifi
     */

    private String ua;
    private String msisdn;
    private String oper;

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }
}
