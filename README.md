#Android双缓存图片的一个工具类

##如何使用

1.直接应用util包

* DiskLruCache.java ,MemoryLruCache.java, DoubleCacheManager.java三个类（需要引入android.support.v4包）
* DiskLruCache.java 是开源的一个硬盘缓存类，很多知名的Android app有用到来作为硬盘缓存的方案。 
* MemoryLruCache是继承android.support.v4.util.LruCache类，用于内存缓存（需要引入android v4包）
* 自己封装的一个双缓存类。

2.用法

* 存储缓存图片：DoubleCacheManager.getInstance(Context context).putBitmap(String key,Bitmap bitmap)
* 获取缓存图片：DoubleCacheManager.getInstance(Context context).getBitmap(String key)

##双缓存原理

![](https://github.com/mayubao/AndroidDoubleCache/blob/master/imgs/Android_Image_Double_Cache.png)
