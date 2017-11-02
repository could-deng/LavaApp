package lava.bluepay.com.lavaapp;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        String res= "";
        try {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = simpleDateFormat.parse("03:10:00");
            long ts = date.getTime();
            res = String.valueOf(ts);
            System.out.print(res);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}