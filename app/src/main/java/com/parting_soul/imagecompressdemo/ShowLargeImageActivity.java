package com.parting_soul.imagecompressdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parting_soul.imagecompress.bean.Photo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author parting_soul
 * @date 2019/4/3
 */
public class ShowLargeImageActivity extends AppCompatActivity {
    public static final String EXTRA_IMG_PATH = "extra_img_path";
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_show_large);
        mViewPager = findViewById(R.id.mViewPager);

        List<Photo> lists = getIntent().getParcelableArrayListExtra(EXTRA_IMG_PATH);
        initViewPager(lists);
    }

    private void initViewPager(List<Photo> lists) {
        mViewPager.setAdapter(new ImageAdapter(this, lists));
    }

    public static void start(Context context, ArrayList<Photo> infoBeans) {
        Intent intent = new Intent(context, ShowLargeImageActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_IMG_PATH, infoBeans);
        context.startActivity(intent);
    }

    static class ImageAdapter extends PagerAdapter {
        private List<Photo> mLists;
        private Context context;

        public ImageAdapter(Context context, List<Photo> list) {
            this.mLists = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return mLists.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View root = LayoutInflater.from(context).inflate(R.layout.adapter_large_img, null);
            ImageView iv = root.findViewById(R.id.iv_img);
            TextView tvInfo = root.findViewById(R.id.tv_img_info);

            Photo bean = mLists.get(position);
            iv.setImageBitmap(BitmapFactory.decodeFile(bean.getCompressPath()));
            tvInfo.setText(
                    String.format(" source size = %s  compress size = %s", getFileSize(bean.getSourcePath()), getFileSize(bean.getCompressPath())));

            container.addView(root);
            return root;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

    }

    private static String getFileSize(String path) {
        return Formatter.formatFileSize(App.getContext(), new File(path).length());
    }

}
