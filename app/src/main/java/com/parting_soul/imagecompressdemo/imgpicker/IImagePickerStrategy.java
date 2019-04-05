package com.parting_soul.imagecompressdemo.imgpicker;

/**
 * 图片获取策略
 *
 * @author parting_soul
 * @date 2018/11/21
 */
public interface IImagePickerStrategy {

    /**
     * 拍照
     *
     * @param callback
     */
    void takePhotos(PicturePickDialog.OnGetPictureCallback callback);

    /**
     * 获取图片
     *
     * @param callback
     */
    void obtainPictures(PicturePickDialog.OnGetPictureCallback callback);
}
