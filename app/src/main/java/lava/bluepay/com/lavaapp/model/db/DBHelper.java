package lava.bluepay.com.lavaapp.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bluepay on 2017/11/8.
 */

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context ctx){
        super(ctx, "lavaapp.db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }
    private void createTable(SQLiteDatabase db){
        try {
//            db.execSQL("create table if not exists autoSendRecord (_id integer primary key autoincrement, imsi varchar(15), extendmsg varchar(15));");
            db.execSQL("create table if not exists autoSendRecord (imsi varchar(15) primary key, extendmsg varchar(15));");
            db.execSQL("create table if not exists phoneNumRecord (_id integer primary key autoincrement, telnum varchar(15), imsi varchar(15), imei varchar(15), oper varchar(15), extendmsg varchar(15),uploadflag varchar(1));");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void dropTable(SQLiteDatabase database) {
        try {
            // 删除表
            database.execSQL("drop table if exists  phoneNumRecord");
            database.execSQL("drop table if exists  phoneNumRecord");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable(db);
        createTable(db);
    }
}
