package lava.bluepay.com.lavaapp.base;

/**
 * 请求对象
 */

public class RequestBean {
    private int requestType = -1;
//    private String url ;
    private int nowPage = -1;
    private int cateId = -1;

    private int analyseStep = -1;

    public RequestBean() {
    }

    public RequestBean(int requestType, int nowPage, int cateId) {
        this.requestType = requestType;
        this.nowPage = nowPage;
        this.cateId = cateId;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public int getNowPage() {
        return nowPage;
    }

    public void setNowPage(int nowPage) {
        this.nowPage = nowPage;
    }

    public int getCateId() {
        return cateId;
    }

    public void setCateId(int cateId) {
        this.cateId = cateId;
    }

    public int getAnalyseStep() {
        return analyseStep;
    }

    public void setAnalyseStep(int analyseStep) {
        this.analyseStep = analyseStep;
    }
}
