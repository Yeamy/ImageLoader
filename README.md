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
-->  null, begin load task  
  
2. check disk cache  
-->  not null, return and get from http  
-->  null, get from http  
  
3. get from http  
-->  has data, save to disk  
-->  has modified, update disk cache
