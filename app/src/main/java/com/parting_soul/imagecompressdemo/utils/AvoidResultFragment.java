package com.parting_soul.imagecompressdemo.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;

import java.util.Map;

/**
 * @author parting_soul
 * @date 2018/9/21
 */
public class AvoidResultFragment extends Fragment {
    private Map<Integer, AvoidResultManager.OnResultCallback> mCallbackMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mCallbackMap = new ArrayMap<>();
    }

    public void startForResult(Intent intent, int requestCode, AvoidResultManager.OnResultCallback callback) {
        mCallbackMap.put(requestCode, callback);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        AvoidResultManager.OnResultCallback callback = mCallbackMap.get(requestCode);
        if (callback != null) {
            callback.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
