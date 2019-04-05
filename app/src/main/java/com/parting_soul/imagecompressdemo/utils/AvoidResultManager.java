package com.parting_soul.imagecompressdemo.utils;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * startForResult 管理类
 *
 * @author parting_soul
 * @date 2018/9/21
 */
public class AvoidResultManager {
    private static final String TAG = "cn.ecook.util.AvoidResultFragment";
    private AvoidResultFragment mResultFragment;

    public AvoidResultManager(FragmentActivity activity) {
        mResultFragment = getAvoidResultFragment(activity);
    }

    public AvoidResultManager(Fragment fragment) {
        mResultFragment = getAvoidResultFragment(fragment);
    }

    /**
     * 获取对应的Fragment
     *
     * @param activity
     * @return
     */
    private AvoidResultFragment getAvoidResultFragment(FragmentActivity activity) {
        AvoidResultFragment fragment = findFragmentByTag(activity);
        if (fragment == null) {
            fragment = new AvoidResultFragment();
            FragmentManager manager = activity.getSupportFragmentManager();
            manager.beginTransaction()
                    .add(fragment, TAG)
                    .commitNow();
        }
        return fragment;
    }

    private AvoidResultFragment findFragmentByTag(FragmentActivity activity) {
        return (AvoidResultFragment) activity.getSupportFragmentManager().findFragmentByTag(TAG);
    }


    /**
     * 获取对应的Fragment
     *
     * @param fragmentParent
     * @return
     */
    private AvoidResultFragment getAvoidResultFragment(Fragment fragmentParent) {
        AvoidResultFragment fragment = findFragmentByTag(fragmentParent);
        if (fragment == null) {
            fragment = new AvoidResultFragment();
            FragmentManager manager = fragmentParent.getChildFragmentManager();
            manager.beginTransaction()
                    .add(fragment, TAG)
                    .commitNow();
        }
        return fragment;
    }

    private AvoidResultFragment findFragmentByTag(Fragment fragment) {
        return (AvoidResultFragment) fragment.getChildFragmentManager().findFragmentByTag(TAG);
    }

    /**
     * 待返回结果启动Activity
     *
     * @param intent
     * @param requestCode
     * @param callback
     */
    public void startForResult(Intent intent, int requestCode, AvoidResultManager.
            OnResultCallback callback) {
        mResultFragment.startForResult(intent, requestCode, callback);
    }

    /**
     * 待返回结果启动Activity
     *
     * @param clazz       待启动Activity的Class
     * @param requestCode
     * @param callback
     */
    public void startForResult(Class<?> clazz, int requestCode, AvoidResultManager.
            OnResultCallback callback) {
        Intent intent = new Intent(mResultFragment.getActivity(), clazz);
        mResultFragment.startForResult(intent, requestCode, callback);
    }


    public interface OnResultCallback {

        /**
         * 回调返回的数据
         *
         * @param requestCode
         * @param resultCode
         * @param data
         */
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}
