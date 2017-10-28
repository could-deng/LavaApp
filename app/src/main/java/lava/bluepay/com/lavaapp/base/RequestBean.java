package lava.bluepay.com.lavaapp.base;

/**
 * 请求对象
 */

public class RequestBean {
    private int requestType = -1;
    private String url ;

    public RequestBean(int requestType, String url) {
        this.requestType = requestType;
        this.url = url;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
