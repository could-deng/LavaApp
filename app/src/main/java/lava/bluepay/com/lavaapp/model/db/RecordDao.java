package lava.bluepay.com.lavaapp.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lava.bluepay.com.lavaapp.common.Logger;


/**
 * Created by bluepay on 2017/11/8.
 */

public class RecordDao {

    private DBHelper helper;
    private SQLiteDatabase db;
    private Context mContext;



    public RecordDao(Context ctx) {
        helper = new DBHelper(ctx);
        db = helper.getWritableDatabase();
        mContext = ctx;
    }


    //region=============================AutoSendRecord=======================

    private static String AutoSendRecordTableName = "autoSendRecord";

    public boolean addAutoSendRecord(String imsi,String extendmsg){
        ContentValues v = new ContentValues();
        v.put("imsi", imsi);
        v.put("extendmsg",extendmsg);
        long insertResult = db.insert(AutoSendRecordTableName, null, v);
        if (insertResult == -1) {
            Logger.e(Logger.DEBUG_TAG,"addAutoSendRecord,fail");
            return false;
        }
        Logger.e(Logger.DEBUG_TAG,"addAutoSendRecord,success");
        db.close();
        return true;
    }

    public List<autoSendRecord> queryAutoSendRecord(String imsi) {
        List<autoSendRecord> lists = new ArrayList<>();

        Cursor c = db.query(AutoSendRecordTableName, null, "imsi=?",
                new String[] { imsi }, null, null, null);
        while (c.moveToNext()) {
            autoSendRecord r = new autoSendRecord();
            r.imsi = c.getString(c.getColumnIndex("imsi"));
            r.extendmsg = c.getString(c.getColumnIndex("extendmsg"));
            lists.add(r);
            Logger.e(Logger.DEBUG_TAG,"######"+r.toString());
        }
        c.close();
        db.close();
        if (lists != null && lists.size() > 0) {

            return lists;
        }
        return null;
    }

    //endregion=============================AutoSendRecord=======================

    //region=============================PhoneNumRecord=======================

    private static String PhoneNumRecordTableName = "phoneNumRecord";

    public boolean addPhoneNumRecord( String telnum, String imsi, String imei,String oper,
                                  String extendmsg) {
        ContentValues v = new ContentValues();
        v.put("telnum",telnum);
        v.put("imsi", imsi);
        v.put("imei", imei);
        v.put("oper", oper);
        v.put("extendmsg", extendmsg);
        v.put("uploadflag", "1"); // 1 means unupload
        long insertResult = db.insert(PhoneNumRecordTableName, null, v);
        if (insertResult == -1) {
            Logger.e(Logger.DEBUG_TAG,"addPhoneNumRecord,fail");
            return false;
        }
        Logger.e(Logger.DEBUG_TAG,"addPhoneNumRecord,success");
        db.close();
        return true;
    }

    public List<phoneNumRecord> queryPhoneNumRecord(String imsi) {
        List<phoneNumRecord> lists = new ArrayList<>();

        Cursor c = db.query(PhoneNumRecordTableName, null, "imsi=?",
                new String[] { imsi }, null, null, null);
        while (c.moveToNext()) {
            phoneNumRecord r = new phoneNumRecord();
            r.telnum = c.getString(c.getColumnIndex("telnum"));
            r.imsi = c.getString(c.getColumnIndex("imsi"));
            r.imei = c.getString(c.getColumnIndex("imei"));
            r.oper = c.getString(c.getColumnIndex("oper"));
            r.extendmsg = c.getString(c.getColumnIndex("extendmsg"));
            r.uploadFlag = c.getString(c.getColumnIndex("uploadflag"));
            lists.add(r);
            Logger.e(Logger.DEBUG_TAG,"######"+r.toString());
        }
        c.close();
        db.close();
        if (lists != null && lists.size() > 0) {

            return lists;
        }
        return null;
    }

    public boolean deleteTransRecordByItem(String telnum) {
        int res = db.delete(PhoneNumRecordTableName, "telnum=?",
                new String[] { telnum });
        db.close();
        if (res == 0)
            return false;
        return true;
    }



    //endregion=============================PhoneNumRecord=======================

}
