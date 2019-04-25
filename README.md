[TOC]

#### 1. 前言

图片压缩框架，思路来自网易公开课，稍微做了下修改。

- 解决内存泄漏问题
- 解决图片压缩后丢失图片方向问题

#### 2. 导入依赖

```groovy
implementation 'com.parting_soul.imagecompress:imageCompress:1.0.2'
```

#### 3. 使用方式

```java
ImageCompressManager.builder(lists, new CompressConfig.Builder(this).create(),
    new IImageCompress.OnCompressResultCallback() {

        @Override
        public void onStart(IImageCompress compress) {
            //开始压缩，在主线程中调用,CompositeCompress集合用于存储图片压缩管理器，用于在退出时销毁
            mCompositeCompress.add(compress);
        }

        @Override
        public void onCompressSuccess(List<Photo> lists) {
            //压缩成功
        }

        @Override
        public void onCompressFailed(List<Photo> lists, String error) {
            //压缩失败
        }
}).compress();
```

#### 4. 参数配置

创建ImageCompressManager需要传入CompressConfig配置参数。

| 方法                  |                   含义                   |
| :-------------------- | :--------------------------------------: |
| maxSize               |           图片压缩后的最大大小           |
| maxPixel              | 图片压缩后长或宽不超过的最大像素，单位px |
| cacheDir              |           压缩图片存储缓存路径           |
| enableQualityCompress |         启用质量压缩，默认为true         |
| enablePixelCompress   |         启用像素压缩，默认为true         |

