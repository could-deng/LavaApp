package lava.bluepay.com.lavaapp;

import android.os.Environment;

import java.io.File;

/**
 * Created by bluepay on 2017/10/17.
 */

public class Config {

    public static final String API_HOST = "192.168.0.0.1";
    public static final String API_PORT = ":8180";


    public static final String PRODUCT_DIC = "LavaApp";
    public static final String PATH_APP_STORRAGE = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + File.separator+PRODUCT_DIC+File.separator;
    public static final String PHOTO_PATH = PATH_APP_STORRAGE + File.separator + "Pic"+ File.separator;

}
