# ImageLoader
A Image Loader for Android

## sample
  
sample: http://www.appchina.com/app/com.yeamy.dnf.monsterscard

## How to use

### Initialize
    ImageLoader loader = ImageLoader.getInstance();  
    loader.init(context);

### Load image
    loader.get("http://~~~.jpeg", imageView);

### Cancel
    loader.remove(imageView);

## How it loading image
  
1. check memory cache  
-->  not null, return  
-->  null, begin load task below  
  
2. check disk cache  
-->  not null, return and check from http  
-->  null, get from http  
  
3. get from http  
-->  has data(http 200), save to disk and return  
-->  has modified, update disk cache and return  
-->  no change(http 304), do nothing
