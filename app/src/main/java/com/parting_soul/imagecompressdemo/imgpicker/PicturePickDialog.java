package com.parting_soul.imagecompressdemo.imgpicker;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.parting_soul.imagecompressdemo.BaseDialog;
import com.parting_soul.imagecompressdemo.R;


/**
 * 图片选择对话框
 *
 * @author parting_soul
 * @date 2018/9/26
 */
public class PicturePickDialog extends BaseDialog {
    private int dp20;
    private OnGetPictureCallback mCallback;
    private IImagePickerStrategy mImagePickerStrategy;

    public PicturePickDialog(@NonNull FragmentActivity activity) {
        super(activity);
        mImagePickerStrategy = new SystemNativeImagePickerStrategy(activity);
        dp20 = (int) (activity.getResources().getDisplayMetrics().density * 20);
        setView(R.layout.dialog_picture_pick)
                .gravity(Gravity.BOTTOM)
                .width(mDisplayTool.getwScreen() - 2 * dp20)
                .height(ViewGroup.LayoutParams.WRAP_CONTENT)
                .anim(R.style.bottomDialogAnim_style);
    }

    @Override
    protected void initView() {
        //取消
        mContentView.findViewById(R.id.tv_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });

        //照相机
        mContentView.findViewById(R.id.tv_camera)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        mImagePickerStrategy.takePhotos(mCallback);
                    }
                });

        //相册
        mContentView.findViewById(R.id.tv_album)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        //选择图片
                        mImagePickerStrategy.obtainPictures(mCallback);
                    }
                });

    }

    /**
     * 图片获取成功回调
     */
    public interface OnGetPictureCallback {
        void onResult(String fileName);
    }

    public void setOnGetPictureCallback(OnGetPictureCallback callback) {
        this.mCallback = callback;
    }

    /**
     * 获取照片的策略
     *
     * @param strategy
     */
    public void setImagePickerStrategy(IImagePickerStrategy strategy) {
        this.mImagePickerStrategy = strategy;
    }

}
