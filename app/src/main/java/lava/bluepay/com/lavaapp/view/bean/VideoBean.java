package lava.bluepay.com.lavaapp.view.bean;

/**
 * Created by bluepay on 2017/10/17.
 */

public class VideoBean {
    private String videoTitle;
    private String videoUrl;

    public VideoBean(String videoTitle, String videoUrl) {
        this.videoTitle = videoTitle;
        this.videoUrl = videoUrl;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
