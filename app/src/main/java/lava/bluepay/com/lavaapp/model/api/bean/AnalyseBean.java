package lava.bluepay.com.lavaapp.model.api.bean;

/**
 * Created by bluepay on 2017/11/10.
 */

public class AnalyseBean {
    private String msisdn;
    private String imei;
    private String telnum;
    private int step;

    public AnalyseBean(String msisdn, String imei, String telnum, int step) {
        this.msisdn = msisdn;
        this.imei = imei;
        this.telnum = telnum;
        this.step = step;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getTelnum() {
        return telnum;
    }

    public void setTelnum(String telnum) {
        this.telnum = telnum;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    @Override
    public String toString() {

        return "msisdn="+msisdn+",imei="+imei+",telnum"+telnum+",step"+step;
    }
}
