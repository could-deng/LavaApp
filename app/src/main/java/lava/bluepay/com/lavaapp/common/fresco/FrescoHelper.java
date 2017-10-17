package lava.bluepay.com.lavaapp.common.fresco;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.view.adapter.RecyclerViewAdapter;

/**
 * Created by bluepay on 2017/10/17.
 */

public class FrescoHelper {

    /**
     * Fresco加载图片的同时保存图片到本地
     * @param contet
     * @param url
     * @param localPath
     */
    public static void saveImage2Local(Context contet, String url, final String localPath, final RecyclerViewAdapter.OnBitmapDownloadListener listener){
        if(TextUtils.isEmpty(url) || TextUtils.isEmpty(localPath)){
            return;
        }
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(url))
                .setProgressiveRenderingEnabled(true)
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> inDiskCacheSource = imagePipeline.fetchDecodedImage(imageRequest,contet);

        BaseBitmapDataSubscriber subscriber = new BaseBitmapDataSubscriber() {

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }
            @Override
            protected void onNewResultImpl(Bitmap bitmap) {
                saveBitmap2File(bitmap,localPath,listener);
                Logger.i(Logger.DEBUG_TAG,"FrescoHelper,subscriber,onNewResultImpl");
            }
        };

        inDiskCacheSource.subscribe(subscriber, CallerThreadExecutor.getInstance());
    }



    /**
     * 保存图片到本地
     * @param bitmap
     * @param localPath 带文件后缀的文件绝对路径
     */
    public static final void saveBitmap2File(Bitmap bitmap, String localPath, RecyclerViewAdapter.OnBitmapDownloadListener listener){
        if(bitmap == null){
            Logger.i(Logger.DEBUG_TAG,"saveBitmap2File,bitmap == null");
            return;
        }
        String fileExtension = lava.bluepay.com.lavaapp.common.FileUtils.getFileExtension(localPath);
        if(TextUtils.isEmpty(fileExtension)){
            Logger.i(Logger.DEBUG_TAG,"saveBitmap2File,fileExtension == null");
            return;
        }
        File file = new File(localPath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            Boolean result = false;
            assert bitmap !=null;
            if(fileExtension!=null && fileExtension.equals("png")){
                result = bitmap.compress(Bitmap.CompressFormat.PNG,100 ,fos);
            }else {
                result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
            listener.onDownloadFinish(result);

            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
