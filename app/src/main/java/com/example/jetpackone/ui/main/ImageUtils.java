package com.example.jetpackone.ui.main;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.example.jitpacklibrary.LogUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

public class ImageUtils {
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        // 得到画布
        Canvas canvas = new Canvas(output);
        // 将画布的四角圆化
        final int color = Color.RED;
        final Paint paint = new Paint();
        // 得到与图像相同大小的区域 由构造的四个值决定区域的位置以及大小
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        // 值越大角度越明显
        final float roundPx = 50;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // drawRoundRect的第2,3个参数一样则画的是正圆的一角，如果数值不同则是椭圆的一角
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    @SuppressWarnings("deprecation")
    public static Bitmap createFramedPhoto(int x, int y, Bitmap image, float outerRadiusRat) {
        // 根据源文件新建一个darwable对象
        Drawable imageDrawable = new BitmapDrawable(image);

        // 新建一个新的输出图片
        Bitmap output = Bitmap.createBitmap(x, y, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        // 新建一个矩形
        RectF outerRect = new RectF(0, 0, x, y);

        // 产生一个红色的圆角矩形
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        canvas.drawRoundRect(outerRect, outerRadiusRat, outerRadiusRat, paint);

        // 将源图片绘制到这个圆角矩形上
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        imageDrawable.setBounds(0, 0, x, y);
        canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
        imageDrawable.draw(canvas);
        canvas.restore();

        return output;
    }

    /**
     * 生成圆角图片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap GetRoundedCornerBitmap(Bitmap bitmap) {
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
            final float roundPx = 30;
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

            final Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }

    public static void openLocalImage(Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, 1);
    }

    public static void toGallery(Context context) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        ((Activity) context).startActivityForResult(intent, 11);
    }

    /**
     * 以流形式获取本地图片
     *
     * @param context
     * @param uri
     * @return
     */
    public static Bitmap decodeBitmapByUri(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        Bitmap bitmap = null;
        try {
            InputStream is = contentResolver.openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static void cropImage(Activity activity, Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是裁剪框宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪后生成图片的宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);

        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, 2);
    }

    /**
     * 压缩图片质量
     *
     * @param path
     * @param options
     * @return
     */
    public static String setMinSize(Bitmap bitmap, int options, String path) {
        File f = new File(path);
        FileOutputStream fOut = null;
        try {
            f.createNewFile();
            fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.getStackTrace();
        }

        return path;
    }

    /*
     * 保存照片
     */
    public static void saveImg(Bitmap bitmap, String path) {
        if (bitmap != null) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(path);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmap.recycle();
            bitmap = null;

        }
    }

    /**
     * 按正方形裁切图片
     */

    public static Bitmap ImageCrop(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int wh = w > h ? h : w;// 裁切后所取的正方形区域边长
        int retX = w > h ? (w - h) / 2 : 0;// 基于原图，取正方形左上角x坐标
        int retY = w > h ? 0 : (h - w) / 2;
        // 下面这句是关键
        return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);

    }


    /**
     * 按压缩大小文件中读取图片
     *
     * @param filePath
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeBitmapFromFile(String filePath, int reqWidth, int reqHeight) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false; // 设置了此属性一定要记得将值设置为false
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }


    /**
     * Drawable-->Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }



    /**
     * 换算压缩图片的压缩比
     *
     * @param size
     * @return
     */
    public static int picSize(int size) {
        int options = 85;
        switch (size) {
            case 0:
                options = 85;
                break;
            case 1:
                options = 80;
                break;
            case 2:
                options = 75;
                break;
            case 3:
                options = 70;
                break;
            case 4:
                options = 65;
                break;
            case 5:
                options = 60;
                break;
            case 7:
                options = 55;
                break;
            case 8:
                options = 50;
                break;
            case 9:
                options = 45;
                break;
            case 10:
                options = 40;
                break;
            default:
                options = 35;
                break;
        }
        return options;

    }

    /**
     * 获取图片大小 解码后的图片
     *
     * @param bitmap
     * @return b
     */
    public static int getBitmapSize(Bitmap bitmap) {
        // API 19
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }
        // API 12
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight(); // earlier version
    }

    /**
     * 得到 图片旋转 的角度
     *
     * @param filepath
     * @return
     */
    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return degree;
    }

    /**
     * 按比例压缩 压缩成小图片
     */
    public static void extractThumbnail(String pcurl, String filePath, int width, int height) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, width, width);
        options.inJustDecodeBounds = false; // 设置了此属性一定要记得将值设置为false
        Bitmap thumbnail = BitmapFactory.decodeFile(filePath, options);
        int angle = getExifOrientation(filePath);
        if (angle != 0) {
            // 如果照片出现了 旋转 那么 就更改旋转度数
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            thumbnail = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);
        }
        saveBitmap(pcurl, thumbnail);

        if (thumbnail != null) {
            thumbnail.recycle();
            thumbnail = null;
        }

    }

    /**
     * 获取本地视频的第一帧
     *
     * @param filePath
     * @return
     */
    public static Bitmap getLocalVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        //MediaMetadataRetriever 是android中定义好的一个类，提供了统一
        //的接口，用于从输入的媒体文件中取得帧和元数据；
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据文件路径获取缩略图
            retriever.setDataSource(filePath);
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error error) {
            error.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }

    public static void saveBitmap(String filePath, Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    //AES加密使用的秘钥，注意的是秘钥的长度必须是16位 方案2
    private static final String AES_KEY = "MyDifficultPassw";



    //图片解密加载
    public static Bitmap getBitmapBytesDecode(String filePath){
        Bitmap bitmap = null;

        //方案2
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        CipherInputStream cis = null;
        try {
            FileInputStream fis = null;
            fis = new FileInputStream(filePath);
            SecretKeySpec sks = new SecretKeySpec(AES_KEY.getBytes(),
                    "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sks);
            //CipherInputStream 为加密输入流
            cis = new CipherInputStream(fis, cipher);
            int b;
            byte[] d = new byte[1024];
            while ((b = cis.read(d)) != -1) {
                out.write(d, 0, b);
            }
            //获取字节流显示图片
            byte[] bytes= out.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(cis != null){
                    cis.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                if(out != null){
                    out.close();
                }
            }catch (Exception e){

            }
        }
        return bitmap;
    }

    //图片解密加载
    public static byte[] getBytesBytesDecode(String filePath){
       // LogUtil.d("filePath:"+filePath);
        byte[] bytes = null;
        if(TextUtils.isEmpty(filePath)){
            return bytes;
        }
        ByteArrayOutputStream out = null;
        CipherInputStream cis = null;
        //方案2
        try {
            FileInputStream fis = null;
            fis = new FileInputStream(filePath);
            out = new ByteArrayOutputStream(1024);
            SecretKeySpec sks = new SecretKeySpec(AES_KEY.getBytes(),
                    "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sks);
            //CipherInputStream 为加密输入流
            cis = new CipherInputStream(fis, cipher);
            int b;
            byte[] d = new byte[1024];
            while ((b = cis.read(d)) != -1) {
                out.write(d, 0, b);
            }
            //获取字节流显示图片
            bytes = out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(cis != null){
                    cis.close();
                }
            }catch (Exception e){

            }
            try {
                if(out != null){
                    out.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return bytes;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    public static Bitmap cutSmallFaceImg(Bitmap largeBitmap, Rect rect) {
        if (largeBitmap == null)
            return null;
        return Bitmap.createBitmap(largeBitmap, rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
    }

    public static File saveBitmap(Context context, Bitmap bitmap){
        if (bitmap == null){
            return null;
        }
        File file = new File(getExternalStoragePath(context, "temp")+ System.currentTimeMillis()+"image.jpg");
        LogUtil.e("图片保存路径=="+file.getAbsolutePath());
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    public static String getExternalStoragePath(Context context, String addfilePath) {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(File.separator);
        String ROOT_DIR = "Android/data/" + context.getPackageName();
        sb.append(ROOT_DIR);
        sb.append(File.separator);
        sb.append(addfilePath);
        sb.append(File.separator);
        String ret = sb.toString();
        File file = new File(ret);
        if (!file.exists()) {
            file.mkdirs();
        }
        return ret;
    }



    public static byte[] getBitmapBytes(Bitmap bitmap){
        byte [] ret = null;
        if (bitmap == null){
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        ret =  baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }


}
