package com.parting_soul.imagecompressdemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parting_soul.imagecompress.bean.Photo;
import com.parting_soul.imagecompress.core.CompositeCompress;
import com.parting_soul.imagecompress.core.CompressConfig;
import com.parting_soul.imagecompress.core.IImageCompress;
import com.parting_soul.imagecompress.core.ImageCompressManager;
import com.parting_soul.imagecompress.utils.Constants;
import com.parting_soul.imagecompress.utils.ImageProcessManager;
import com.parting_soul.imagecompressdemo.imgpicker.PicturePickDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Photo> mImgLists;
    private CompositeCompress mCompositeCompress = new CompositeCompress();
    private ImageProcessManager mImageProcessManager;
    private List<String> mCacheFileLists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x12);
        mImgLists = new ArrayList<>();
        mImageProcessManager = new ImageProcessManager(this);
        mCacheFileLists = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        switch (v.getId()) {
            case R.id.bt_compress:
                compressByQuality();
                break;
            case R.id.bt_compress_result:
                ShowLargeImageActivity.start(this, mImgLists);
                break;
            case R.id.bt_compress_frame_quality:
                compressFrame();
                break;
            case R.id.bt_luban_compress:
                compressByLuban();
                break;
            case R.id.bt_compress_network_img:
                compressNetworkImage();
                break;
            case R.id.bt_compress_bitmap:
                compressBitmap();
                break;
            default:
                break;
        }
    }

    private void compressBitmap() {
        PicturePickDialog dialog = new PicturePickDialog(this);
        dialog.setOnGetPictureCallback(new PicturePickDialog.OnGetPictureCallback() {
            @Override
            public void onResult(final String fileName) {
                Bitmap bitmap = BitmapFactory.decodeFile(fileName);
                mImageProcessManager.getImageFilePath(bitmap, new ImageProcessManager.OnGetImageFileCallback() {
                    @Override
                    public void onSuccess(String filePath) {
                        LogUtils.d("" + filePath);
                        showToast("图片转换完成");
                        mCacheFileLists.add(fileName);
                    }

                    @Override
                    public void onError() {
                        LogUtils.e("");
                    }
                }, true);
            }
        });
        dialog.show();
    }

    private void compressNetworkImage() {
        File outFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "info.jpg");
        mImageProcessManager.getImageFilePath("http://pic.ecook.cn/web/6669742.jpg!m720", outFile, new ImageProcessManager.OnGetImageFileCallback() {
            @Override
            public void onSuccess(String filePath) {
                LogUtils.d("" + filePath);
                showToast("图片下载完成");
                mCacheFileLists.add(filePath);
            }

            @Override
            public void onError() {
                LogUtils.e("");
            }
        });
    }

    private void compressByLuban() {
        final List<Photo> lists = getPhotoLists();

        List<String> paths = new ArrayList<>();
        for (Photo p : lists) {
            paths.add(p.getSourcePath());
        }

        mImgLists.clear();
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.show();
        final long c = System.currentTimeMillis();
        Luban.with(this)
                .load(paths)
                .setTargetDir(Constants.CACHE_PATH_COMPRESS_IMG)
                .setCompressListener(new OnCompressListener() {
                    int count = 0;

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File file) {
                        count++;

                        Photo p = new Photo(file.getAbsolutePath());
                        p.setCompressPath(file.getAbsolutePath());
                        mImgLists.add(p);

                        if (count == lists.size()) {
                            showToast("压缩成功");
                            Log.e("Luban", "压缩时间 " + (System.currentTimeMillis() - c) / 1000 + "s");
                            dialog.dismiss();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        count++;
                        showToast("压缩失败 ");
                        dialog.dismiss();
                    }
                }).launch();
    }

    private void compressFrame() {
        List<Photo> lists = getPhotoLists();
        final ProgressDialog dialog = new ProgressDialog(this);
        ImageCompressManager.builder(lists, new CompressConfig.Builder(this).create(),
                new IImageCompress.OnCompressResultCallback() {

                    @Override
                    public void onStart(IImageCompress compress) {
                        mCompositeCompress.add(compress);
                        dialog.show();
                    }

                    @Override
                    public void onCompressSuccess(List<Photo> lists) {
                        showToast("压缩成功");
                        mImgLists.clear();
                        mImgLists.addAll(lists);
                        dialog.dismiss();
                    }

                    @Override
                    public void onCompressFailed(List<Photo> lists, String error) {
                        showToast("压缩失败 " + error);
                        dialog.dismiss();
                    }
                }).compress();
    }

    private List<Photo> getPhotoLists() {
        List<Photo> lists = new ArrayList<>();
        String cameraDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/camera";
        File root = new File(cameraDir);
        if (!root.exists() || !root.isDirectory() || root.listFiles() == null) {
            return lists;
        }

        Photo p = null;
        for (File f : root.listFiles()) {
            if (!f.isDirectory() && (f.getName().endsWith(".jpg")
                    || f.getName().endsWith(".png"))) {
                p = new Photo(f.getAbsolutePath());
                lists.add(p);
            }
        }

        LogUtils.d("" + lists.size());

//        if (lists.size() > 8) {
//            lists = lists.subList(0, 8);
//        }

        return lists;
    }

    private void compressByQuality() {
        PicturePickDialog dialog = new PicturePickDialog(this);
        dialog.setOnGetPictureCallback(new PicturePickDialog.OnGetPictureCallback() {
            @Override
            public void onResult(String fileName) {
                exifInterfaceMsg(fileName);
            }
        });
        dialog.show();
    }


    /**
     * 获取图片在JPEG头部存储的照片信息
     *
     * @param fileName
     */
    private void exifInterfaceMsg(String fileName) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String orientation = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
        String dateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
        String make = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
        String model = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
        String flash = exifInterface.getAttribute(ExifInterface.TAG_FLASH);
        String imageLength = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
        String imageWidth = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
        String latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String latitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        String longitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
        String exposureTime = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
        String aperture = exifInterface.getAttribute(ExifInterface.TAG_APERTURE);
        String isoSpeedRatings = exifInterface.getAttribute(ExifInterface.TAG_ISO);
        String dateTimeDigitized = exifInterface.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED);
        String subSecTime = exifInterface.getAttribute(ExifInterface.TAG_SUBSEC_TIME);
        String subSecTimeOrig = exifInterface.getAttribute(ExifInterface.TAG_SUBSEC_TIME_ORIG);
        String subSecTimeDig = exifInterface.getAttribute(ExifInterface.TAG_SUBSEC_TIME_DIG);
        String altitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_ALTITUDE);
        String altitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF);
        String gpsTimeStamp = exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
        String gpsDateStamp = exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
        String whiteBalance = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
        String focalLength = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
        String processingMethod = exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);

        Log.e("TAG", "## orientation=" + orientation);
        Log.e("TAG", "## dateTime=" + dateTime);
        Log.e("TAG", "## make=" + make);
        Log.e("TAG", "## model=" + model);
        Log.e("TAG", "## flash=" + flash);
        Log.e("TAG", "## imageLength=" + imageLength);
        Log.e("TAG", "## imageWidth=" + imageWidth);
        Log.e("TAG", "## latitude=" + latitude);
        Log.e("TAG", "## longitude=" + longitude);
        Log.e("TAG", "## latitudeRef=" + latitudeRef);
        Log.e("TAG", "## longitudeRef=" + longitudeRef);
        Log.e("TAG", "## exposureTime=" + exposureTime);
        Log.e("TAG", "## aperture=" + aperture);
        Log.e("TAG", "## isoSpeedRatings=" + isoSpeedRatings);
        Log.e("TAG", "## dateTimeDigitized=" + dateTimeDigitized);
        Log.e("TAG", "## subSecTime=" + subSecTime);
        Log.e("TAG", "## subSecTimeOrig=" + subSecTimeOrig);
        Log.e("TAG", "## subSecTimeDig=" + subSecTimeDig);
        Log.e("TAG", "## altitude=" + altitude);
        Log.e("TAG", "## altitudeRef=" + altitudeRef);
        Log.e("TAG", "## gpsTimeStamp=" + gpsTimeStamp);
        Log.e("TAG", "## gpsDateStamp=" + gpsDateStamp);
        Log.e("TAG", "## whiteBalance=" + whiteBalance);
        Log.e("TAG", "## focalLength=" + focalLength);
        Log.e("TAG", "## processingMethod=" + processingMethod);
    }


//    /**
//     * 获取所有质量的图片
//     *
//     * @param filePath
//     * @return
//     */
//    private ArrayList<PicInfoBean> getAllQualityImageList(String filePath) {
//        ArrayList<PicInfoBean> lists = new ArrayList<>();
//        int quality = 100;
//        PicInfoBean infoBean = null;
//        while (quality > 0) {
//            String path = CompressUtils.(filePath, Bitmap.CompressFormat.JPEG, quality);
//            infoBean = new PicInfoBean(path, Formatter.formatFileSize(App.getContext(),
//                    new File(path).length()), quality);
//            infoBean.setBitmapSize(Formatter.formatFileSize(App.getContext(), BitmapFactory.decodeFile(path).getByteCount()));
//            quality -= 5;
//            lists.add(infoBean);
//        }
//        return lists;
//    }

    private void showToast(String msg) {
        Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageProcessManager.onDestroy();
        mCompositeCompress.destroy();
        clearFileCache();
    }

    private void clearFileCache() {
        for (String path : mCacheFileLists) {
            File file = new File(path);
            if (file.exists()) {
                LogUtils.d("" + file.delete());
            }
        }
    }

}
