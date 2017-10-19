package lava.bluepay.com.lavaapp.model.process;

import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.model.api.ApiUtils;
import lava.bluepay.com.lavaapp.model.api.OKHttpUtil;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bluepay on 2017/10/19.
 */

public class BaseProcessor {
    private static BaseProcessor instance;

    public BaseProcessor() {
    }

    public static BaseProcessor getInstance(){
        if(instance == null){
            instance = new BaseProcessor();
        }
        return instance;
    }




    public String getDataFromApi(String url){
        Request request = new Request.Builder().url(url).build();
        String ret;
        if(ApiUtils.isNetWorkAvailable()){
            try {
                Response response = OKHttpUtil.getInstance().execute(request);
                if(response !=null){
                    if(response.isSuccessful()){
                        ret = response.body().toString();
                    }else{
                        ret = getHttpExceptionString(response.code());
                    }
                }else{
                    ret = getHttpExceptionString(ApiUtils.HTTP_REQUEST_EXCEPTION);
                }
                return ret;
            }catch (Exception e){
                ret = getHttpExceptionString(ApiUtils.HTTP_REQUEST_EXCEPTION);
                Logger.e(Logger.DEBUG_TAG,"BaseProcessor,getDataFromApi(),execption");
            }
        }else{
            ret = getHttpExceptionString(ApiUtils.HTTP_REQUEST_INPUT_INVALID);
            Logger.e(Logger.DEBUG_TAG,"BaseProcessor,netWork not available");
        }
        return ret;
    }
    private String getHttpExceptionString(int httpCode) {
        return String.format("{ 'code':%s }", httpCode);
    }













}
