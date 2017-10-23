package lava.bluepay.com.lavaapp.model.api.bean;

import java.util.List;

/**
 * Created by bluepay on 2017/10/23.
 */

public class CategoryBean extends BaseBean {

    /**
     * data : {"current_page":1,"data":[{"id":1,"cate_id":1,"title":"性感美女图","thumb":"124124.jpg","imgurl":"q4214.jpg","seeds":null,"click":22,"descs":"qrqwr","created_at":"2017-10-20 09:25:01","updated_at":"2017-10-20 09:26:44"},{"id":2,"cate_id":2,"title":"shihaa","thumb":"agegr","imgurl":"egerg","seeds":null,"click":2,"descs":"gergerg","created_at":"2017-10-20 09:26:03","updated_at":"2017-10-20 09:26:39"},{"id":3,"cate_id":2,"title":"gerger","thumb":"gergregreg","imgurl":"ergergreg","seeds":null,"click":101,"descs":"gergre","created_at":"2017-10-20 09:26:17","updated_at":"2017-10-20 09:26:36"},{"id":4,"cate_id":2,"title":"gerger4","thumb":"erger","imgurl":"regerg","seeds":null,"click":100,"descs":"gergre","created_at":"2017-10-20 09:26:33","updated_at":"2017-10-20 09:26:53"},{"id":5,"cate_id":1,"title":"性感美女图","thumb":"124124.jpg","imgurl":"q4214.jpg","seeds":null,"click":22,"descs":null,"created_at":"2017-10-20 09:34:15","updated_at":null},{"id":6,"cate_id":2,"title":"shihaa","thumb":"agegr","imgurl":"egerg","seeds":null,"click":2,"descs":null,"created_at":"2017-10-20 09:34:15","updated_at":null},{"id":7,"cate_id":2,"title":"gerger","thumb":"gergregreg","imgurl":"ergergreg","seeds":null,"click":101,"descs":null,"created_at":"2017-10-20 09:34:15","updated_at":null},{"id":8,"cate_id":2,"title":"gerger4","thumb":"erger","imgurl":"regerg","seeds":null,"click":100,"descs":null,"created_at":"2017-10-20 09:34:15","updated_at":null},{"id":12,"cate_id":1,"title":"性感美女图","thumb":"124124.jpg","imgurl":"q4214.jpg","seeds":null,"click":22,"descs":null,"created_at":"2017-10-20 09:34:18","updated_at":null},{"id":13,"cate_id":2,"title":"shihaa","thumb":"agegr","imgurl":"egerg","seeds":null,"click":2,"descs":null,"created_at":"2017-10-20 09:34:18","updated_at":null}],"from":1,"next_page_url":"/v1/querypage.api?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJMYXZhIiwiaWF0IjoxNTA4NzM5OTQ0LCJleHAiOjE1MDg3NDcxNDQsImFwcGlkIjoid3NnWWF0RXgifQ.6avtapUECTO2ArtcCZw1Yy-GlBG4LenoZYa3sTFRIMY&page=2","path":"/v1/querypage.api","per_page":"10","prev_page_url":null,"to":10}
     */

    private DataBeanX data;

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public static class DataBeanX {
        /**
         * current_page : 1
         * data : [{"id":1,"cate_id":1,"title":"性感美女图","thumb":"124124.jpg","imgurl":"q4214.jpg","seeds":null,"click":22,"descs":"qrqwr","created_at":"2017-10-20 09:25:01","updated_at":"2017-10-20 09:26:44"},{"id":2,"cate_id":2,"title":"shihaa","thumb":"agegr","imgurl":"egerg","seeds":null,"click":2,"descs":"gergerg","created_at":"2017-10-20 09:26:03","updated_at":"2017-10-20 09:26:39"},{"id":3,"cate_id":2,"title":"gerger","thumb":"gergregreg","imgurl":"ergergreg","seeds":null,"click":101,"descs":"gergre","created_at":"2017-10-20 09:26:17","updated_at":"2017-10-20 09:26:36"},{"id":4,"cate_id":2,"title":"gerger4","thumb":"erger","imgurl":"regerg","seeds":null,"click":100,"descs":"gergre","created_at":"2017-10-20 09:26:33","updated_at":"2017-10-20 09:26:53"},{"id":5,"cate_id":1,"title":"性感美女图","thumb":"124124.jpg","imgurl":"q4214.jpg","seeds":null,"click":22,"descs":null,"created_at":"2017-10-20 09:34:15","updated_at":null},{"id":6,"cate_id":2,"title":"shihaa","thumb":"agegr","imgurl":"egerg","seeds":null,"click":2,"descs":null,"created_at":"2017-10-20 09:34:15","updated_at":null},{"id":7,"cate_id":2,"title":"gerger","thumb":"gergregreg","imgurl":"ergergreg","seeds":null,"click":101,"descs":null,"created_at":"2017-10-20 09:34:15","updated_at":null},{"id":8,"cate_id":2,"title":"gerger4","thumb":"erger","imgurl":"regerg","seeds":null,"click":100,"descs":null,"created_at":"2017-10-20 09:34:15","updated_at":null},{"id":12,"cate_id":1,"title":"性感美女图","thumb":"124124.jpg","imgurl":"q4214.jpg","seeds":null,"click":22,"descs":null,"created_at":"2017-10-20 09:34:18","updated_at":null},{"id":13,"cate_id":2,"title":"shihaa","thumb":"agegr","imgurl":"egerg","seeds":null,"click":2,"descs":null,"created_at":"2017-10-20 09:34:18","updated_at":null}]
         * from : 1
         * next_page_url : /v1/querypage.api?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJMYXZhIiwiaWF0IjoxNTA4NzM5OTQ0LCJleHAiOjE1MDg3NDcxNDQsImFwcGlkIjoid3NnWWF0RXgifQ.6avtapUECTO2ArtcCZw1Yy-GlBG4LenoZYa3sTFRIMY&page=2
         * path : /v1/querypage.api
         * per_page : 10
         * prev_page_url : null
         * to : 10
         */

        private int current_page;
        private int from;
        private String next_page_url;
        private String path;
        private String per_page;
        private Object prev_page_url;
        private int to;
        private List<DataBean> data;

        public int getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(int current_page) {
            this.current_page = current_page;
        }

        public int getFrom() {
            return from;
        }

        public void setFrom(int from) {
            this.from = from;
        }

        public String getNext_page_url() {
            return next_page_url;
        }

        public void setNext_page_url(String next_page_url) {
            this.next_page_url = next_page_url;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPer_page() {
            return per_page;
        }

        public void setPer_page(String per_page) {
            this.per_page = per_page;
        }

        public Object getPrev_page_url() {
            return prev_page_url;
        }

        public void setPrev_page_url(Object prev_page_url) {
            this.prev_page_url = prev_page_url;
        }

        public int getTo() {
            return to;
        }

        public void setTo(int to) {
            this.to = to;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * id : 1
             * cate_id : 1
             * title : 性感美女图
             * thumb : 124124.jpg
             * imgurl : q4214.jpg
             * seeds : null
             * click : 22
             * descs : qrqwr
             * created_at : 2017-10-20 09:25:01
             * updated_at : 2017-10-20 09:26:44
             */

            private int id;
            private int cate_id;
            private String title;
            private String thumb;
            private String imgurl;
            private Object seeds;
            private int click;
            private String descs;
            private String created_at;
            private String updated_at;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getCate_id() {
                return cate_id;
            }

            public void setCate_id(int cate_id) {
                this.cate_id = cate_id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getThumb() {
                return thumb;
            }

            public void setThumb(String thumb) {
                this.thumb = thumb;
            }

            public String getImgurl() {
                return imgurl;
            }

            public void setImgurl(String imgurl) {
                this.imgurl = imgurl;
            }

            public Object getSeeds() {
                return seeds;
            }

            public void setSeeds(Object seeds) {
                this.seeds = seeds;
            }

            public int getClick() {
                return click;
            }

            public void setClick(int click) {
                this.click = click;
            }

            public String getDescs() {
                return descs;
            }

            public void setDescs(String descs) {
                this.descs = descs;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }

            public String getUpdated_at() {
                return updated_at;
            }

            public void setUpdated_at(String updated_at) {
                this.updated_at = updated_at;
            }
        }
    }
}
