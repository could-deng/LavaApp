package lava.bluepay.com.lavaapp.model.db;

/**
 * Created by bluepay on 2017/11/8.
 */

public class phoneNumRecord {
    public String telnum;
    public String imsi;
    public String imei;
    public String extendmsg;
    public String oper;
    public String uploadFlag;

    @Override
    public String toString() {
        return "telnum:"+telnum+",imsi:"+imsi+",imei"+imei+",extendmsg:"+extendmsg+",oper:"+oper+",uploadFloag:"+uploadFlag;
    }
}
