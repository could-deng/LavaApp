package lava.bluepay.com.lavaapp.model.api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import lava.bluepay.com.lavaapp.common.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bluepay on 2017/10/19.
 */

public class OKHttpUtil {

    private static OKHttpUtil instance;
    private OkHttpClient mOkHttpClient;
    public OKHttpUtil() {
    }
    public static OKHttpUtil getInstance(){
        if(instance == null){
            instance = new OKHttpUtil();
        }
        return instance;
    }
    public OkHttpClient getClient(){
        if(mOkHttpClient == null){
            mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(90, TimeUnit.SECONDS)
                    .writeTimeout(90,TimeUnit.SECONDS)
                    .readTimeout(90,TimeUnit.SECONDS)
                    .build();
        }
        return mOkHttpClient;
    }

    /**
     * 阻塞方式
     * @param request
     * @return
     */
    public Response execute(Request request){
        if(request == null){
            return null;
        }
        Response response = null;
        try {
            response = getClient().newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.e(Logger.DEBUG_TAG,"execute error:"+e.getMessage());
        }
        return response;
    }

}
