package com.parting_soul.imagecompressdemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author parting_soul
 * @date 2019/4/3
 */
public class PicInfoBean implements Parcelable {
    private String path;
    private String imgSize;
    private int quality;
    private String bitmapSize;

    public PicInfoBean(String path, String imgSize, int quality) {
        this.path = path;
        this.imgSize = imgSize;
        this.quality = quality;
    }


    protected PicInfoBean(Parcel in) {
        path = in.readString();
        imgSize = in.readString();
        quality = in.readInt();
        bitmapSize = in.readString();
    }

    public static final Creator<PicInfoBean> CREATOR = new Creator<PicInfoBean>() {
        @Override
        public PicInfoBean createFromParcel(Parcel in) {
            return new PicInfoBean(in);
        }

        @Override
        public PicInfoBean[] newArray(int size) {
            return new PicInfoBean[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImgSize() {
        return imgSize;
    }

    public void setImgSize(String imgSize) {
        this.imgSize = imgSize;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getBitmapSize() {
        return bitmapSize;
    }

    public void setBitmapSize(String bitmapSize) {
        this.bitmapSize = bitmapSize;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(imgSize);
        dest.writeInt(quality);
        dest.writeString(bitmapSize);
    }
}
