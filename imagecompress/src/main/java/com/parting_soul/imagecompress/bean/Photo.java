package com.parting_soul.imagecompress.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @author parting_soul
 * @date 2019/4/4
 */
public class Photo implements Serializable, Parcelable {
    private String sourcePath;
    private String compressPath;
    private boolean isCompress;

    public Photo(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    protected Photo(Parcel in) {
        sourcePath = in.readString();
        compressPath = in.readString();
        isCompress = in.readByte() != 0;
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getCompressPath() {
        return compressPath;
    }

    public void setCompressPath(String compressPath) {
        this.compressPath = compressPath;
    }

    public boolean isCompress() {
        return isCompress;
    }

    public void setCompress(boolean compress) {
        isCompress = compress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sourcePath);
        dest.writeString(compressPath);
        dest.writeByte((byte) (isCompress ? 1 : 0));
    }

    @Override
    public String toString() {
        return "Photo{" +
                "sourcePath='" + sourcePath + '\'' +
                ", compressPath='" + compressPath + '\'' +
                ", isCompress=" + isCompress +
                '}';
    }
}
