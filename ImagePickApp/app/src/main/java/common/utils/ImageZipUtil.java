package common.utils;

import android.graphics.BitmapFactory;

/**
 * Created by hch on 2017/9/27.
 */

public class ImageZipUtil {
    private int maxSize;//图片压缩的最大大小单位为kb
    private String url;//图片的url

    public ImageZipUtil() {
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void zipImage(String srcImagePath, float width, float height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcImagePath, options);
        //根据原始图片的宽高比和期望的输出图片的宽高比计算最终输出的图片的宽和高
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        float maxWidth = width;//期望输出的图片宽度
        float maxHeight = height;//期望输出的图片高度
        float srcRatio = srcWidth / srcHeight;
        float outRatio = maxWidth / maxHeight;
        float actualOutWidth = srcWidth;//最终输出的图片宽度
        float actualOutHeight = srcHeight;//最终输出的图片高度

        if (srcWidth > maxWidth || srcHeight > maxHeight)

        {
            if (srcRatio < outRatio) {
                actualOutHeight = maxHeight;
                actualOutWidth = actualOutHeight * srcRatio;
            } else if (srcRatio > outRatio) {
                actualOutWidth = maxWidth;
                actualOutHeight = actualOutWidth / srcRatio;
            } else {
                actualOutWidth = maxWidth;
                actualOutHeight = maxHeight;
            }
        }
//计算sampleSize
//        options.inSampleSize = computSampleSize(options, actualOutWidth, actualOutHeight);
    }


}
