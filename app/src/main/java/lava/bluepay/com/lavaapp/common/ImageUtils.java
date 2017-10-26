package lava.bluepay.com.lavaapp.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.facebook.common.internal.ByteStreams.copy;

/**
 * Created by bluepay on 2017/10/16.
 */

public class ImageUtils {

    public static Bitmap productBitmap(String urlPath){
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        Uri uri = Uri.parse(urlPath);
        bitmap = BitmapFactory.decodeFile(uri.toString(),options);
        return bitmap;
    }

    /**
     * 得到本地或者网络上的bitmap url - 网络或者本地图片的绝对路径,比如:
     *
     * A.网络路径: url="http://blog.foreverlove.us/girl2.png" ;
     *
     * B.本地路径:url="file://mnt/sdcard/photo/image.png";
     *
     * C.支持的图片格式 ,png, jpg,bmp,gif等等
     *
     * @param url
     * @return
     */
    public static Bitmap GetLocalOrNetBitmap(String url)
    {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try
        {
            in = new BufferedInputStream(new URL(url).openStream(), 2*1024);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 2*1024);
            copy(in, out);
            in.close();
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            return bitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存bitmap至本地文件
     * @param bm
     * @param localPath
     */
    public static void saveBitmap2File(Bitmap bm,String localPath){
        if(bm == null || TextUtils.isEmpty(localPath)){
            return;
        }
        try {
            File file = new File(localPath);
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG,100,fos);//不管原文件的文件后缀，一律使用jpg格式.
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //region=====================自定义模糊化处理=========================

    /**
     * bitmap 模糊化处理
     */
    @SuppressLint("NewApi")
    public static Bitmap newBlur(Bitmap bkg, SimpleDraweeView view) {
        float scaleFactor = 20;//图片缩放比例；
        int radius = 15;//模糊程度
        Bitmap bitmap = null;
        try {
//                bkg = zoomBitmap(bkg);//方法比例:屏幕／实际
            if (bkg == null) return null;
            Bitmap overlay = Bitmap.createBitmap(                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
                    (int) (bkg.getWidth() / scaleFactor),
                    (int) (bkg.getHeight() / scaleFactor),
                    Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(overlay);
            canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
            canvas.scale(1 / scaleFactor, 1 / scaleFactor);
            Paint paint = new Paint();
            paint.setFlags(Paint.FILTER_BITMAP_FLAG);
            canvas.drawBitmap(bkg, 0, 0, paint);
            bitmap = ImageUtils.doBlur(overlay, radius, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
//
//    /**
//     * bitmap 模糊化处理
//     */
//    @SuppressLint("NewApi")
//    public static Bitmap newBlurToViewSize(Bitmap bkg, SimpleDraweeView view) {
//        float scaleFactor = 20;//图片缩放比例；
//        int radius = 15;//模糊程度
//        Bitmap bitmap = null;
//        try {
////                bkg = zoomBitmap(bkg);//方法比例:屏幕／实际
//            if (bkg == null) return null;
//            Bitmap overlay = Bitmap.createBitmap(
//                    (int) (view.getWidth() / scaleFactor),
//                    (int) (view.getHeight() / scaleFactor),
//                    Bitmap.Config.RGB_565);
//            Canvas canvas = new Canvas(overlay);
//            canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
//            canvas.scale(1 / scaleFactor, 1 / scaleFactor);
//            Paint paint = new Paint();
//            paint.setFlags(Paint.FILTER_BITMAP_FLAG);
//            canvas.drawBitmap(bkg, 0, 0, paint);
//            bitmap = ImageUtils.doBlur(overlay, radius, true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return bitmap;
//    }


    public static Bitmap doBlur(Bitmap sentBitmap, int radius,
                                boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    //endregion=====================自定义模糊化处理=========================



    //region=====================sdk17以上的RenderScript模糊化处理=========================



    /**
     * 图片缩放比例
     */
    private static final float BITMAP_SCALE = 0.4f;
    /**
     * 最大模糊度(在0.0到25.0之间)
     */
    private static final float BLUR_RADIUS = 1f;//15

    /**
     * 模糊图片的具体方法(转换为与原图图片相同大小的Bitmap)
     *
     * @param context 上下文对象
     * @param image   需要模糊的图片
     * @return 模糊处理后的图片
     */
    public static Bitmap blur(Context context, Bitmap image) {
        Bitmap outputBitmap;
        // 计算图片缩小后的长宽
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                int width = Math.round(image.getWidth() * BITMAP_SCALE);
                int height = Math.round(image.getHeight() * BITMAP_SCALE);

                // 将缩小后的图片做为预渲染的图片。
                Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
                // 创建一张渲染后的输出图片。
                outputBitmap = Bitmap.createBitmap(inputBitmap);

                ScriptIntrinsicBlur blurScript = getBlurTool(context);
                // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间。
                // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去。
                Allocation tmpIn = Allocation.createFromBitmap(getRenderScript(context), inputBitmap);
                Allocation tmpOut = Allocation.createFromBitmap(getRenderScript(context), outputBitmap);

                // 设置渲染的模糊程度, 25f是最大模糊度
                blurScript.setRadius(BLUR_RADIUS);
                // 设置blurScript对象的输入内存
                blurScript.setInput(tmpIn);
                // 将输出数据保存到输出内存中
                blurScript.forEach(tmpOut);
                // 将数据填充到Allocation中
                tmpOut.copyTo(outputBitmap);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return outputBitmap;
    }



    /**
     * 模糊图片的具体方法（转化为指定大小的Bitmap）
     *
     * @param context 上下文对象
     * @param image   需要模糊的图片
     * @return 模糊处理后的图片
     */
    public static Bitmap blur(Context context, Bitmap image,int showWidth,int showHeight) {
        Bitmap outputBitmap;
        // 计算图片缩小后的长宽
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                int width = Math.round(showWidth * BITMAP_SCALE);
                int height = Math.round(showHeight * BITMAP_SCALE);

                // 将缩小后的图片做为预渲染的图片。
                Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
                // 创建一张渲染后的输出图片。
                outputBitmap = Bitmap.createBitmap(inputBitmap);

                ScriptIntrinsicBlur blurScript = getBlurTool(context);
                // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间。
                // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去。
                Allocation tmpIn = Allocation.createFromBitmap(getRenderScript(context), inputBitmap);
                Allocation tmpOut = Allocation.createFromBitmap(getRenderScript(context), outputBitmap);

                // 设置渲染的模糊程度, 25f是最大模糊度
                blurScript.setRadius(BLUR_RADIUS);
                // 设置blurScript对象的输入内存
                blurScript.setInput(tmpIn);
                // 将输出数据保存到输出内存中
                blurScript.forEach(tmpOut);
                // 将数据填充到Allocation中
                tmpOut.copyTo(outputBitmap);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return outputBitmap;
    }



    private static ScriptIntrinsicBlur blurScript;
    private static RenderScript rs;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static RenderScript getRenderScript(Context context) {
        if (rs == null) {
            // 创建RenderScript内核对象
            rs = RenderScript.create(context);
        }
        return rs;
    }

    private static ScriptIntrinsicBlur getBlurTool(Context context) {
        if (blurScript == null) {
            // 创建一个模糊效果的RenderScript的工具对象
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                blurScript = ScriptIntrinsicBlur.create(getRenderScript(context), Element.U8_4(getRenderScript(context)));
            }
        }
        return blurScript;
    }


    //endregion=====================sdk17以上的RenderScript模糊化处理=========================
}
