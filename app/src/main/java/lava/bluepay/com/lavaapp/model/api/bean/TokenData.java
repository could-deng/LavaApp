package lava.bluepay.com.lavaapp.model.api.bean;

/**
 * Created by bluepay on 2017/10/20.
 */

public class TokenData extends BaseBean {


    /**
     * data : {"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJMYXZhIiwiaWF0IjoxNTA4NDY1Mjg2LCJleHAiOjE1MDg0NzI0ODYsImFwcGlkIjoid3NnWWF0RXgifQ.xxN6yRYJsOmFHdhvEXXDRulADKly9JGjZ5wevOFIeig","expired":1508472486}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * token : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJMYXZhIiwiaWF0IjoxNTA4NDY1Mjg2LCJleHAiOjE1MDg0NzI0ODYsImFwcGlkIjoid3NnWWF0RXgifQ.xxN6yRYJsOmFHdhvEXXDRulADKly9JGjZ5wevOFIeig
         * expired : 1508472486
         */

        private String token;
        private String expired;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getExpired() {
            return expired;
        }

        public void setExpired(String expired) {
            this.expired = expired;
        }
    }
}
