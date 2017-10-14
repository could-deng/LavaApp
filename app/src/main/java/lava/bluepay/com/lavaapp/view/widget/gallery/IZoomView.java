package lava.bluepay.com.lavaapp.view.widget.gallery;

public interface IZoomView {
    void reset();
    boolean isZoomToOriginalSize();
    void setSize(int width, int height);
    void setMargin(int marginLeft, int marginTop);
}
