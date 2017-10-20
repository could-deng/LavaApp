package lava.bluepay.com.lavaapp.model.api.bean;

/**
 * Created by bluepay on 2017/10/20.
 */

public class BaseBean {

    /**
     * code : 200
     * message : 200 OK
     * data : {"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJMYXZhIiwiaWF0IjoxNTA4NDAyMjQ0LCJleHAiOjE1MDg0MDk0NDQsImFwcGlkIjoid3NnWWF0RXgifQ.xmKV0ADn7t1K6dXBBma01r77wUNm_mI4bzUgchUdBZg","expired":1508409444}
     */

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
