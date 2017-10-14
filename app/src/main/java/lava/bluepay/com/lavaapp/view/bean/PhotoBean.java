package lava.bluepay.com.lavaapp.view.bean;

/**
 * 图片bean类
 */

public class PhotoBean {
    private String pictureTitle;
    private String pictureImg;


    public PhotoBean(String pictureImg) {
        this.pictureImg = pictureImg;
    }

    public PhotoBean(String pictureTitle, String pictureImg) {
        this.pictureTitle = pictureTitle;
        this.pictureImg = pictureImg;
    }

    public String getPictureImg() {
        return pictureImg;
    }

    public void setPictureImg(String pictureImg) {
        this.pictureImg = pictureImg;
    }

    public String getPictureTitle() {
        return pictureTitle;
    }

    public void setPictureTitle(String pictureTitle) {
        this.pictureTitle = pictureTitle;
    }
}
