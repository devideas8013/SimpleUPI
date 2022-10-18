# SimpleUPI
> Step 1. Add the JitPack repository to your build file

```gradle
allprojects {
   repositories {
	...
	maven { url 'https://jitpack.io' }
   }
}
```

> Step 2. Add the dependency
```gradle
dependencies {
    implementation 'com.github.devideas8013:SimpleUPI:Tag'
}
```

# Demo APK:
### [Visit official website to download](https://devsuggest.com/)

# Code Example
> To get API Key [click here](https://devsuggest.com/)
 
```java
SimpleUPI simpleUPI = new SimpleUPI();
simpleUPI.init(MainActivity.this, "123");

pay_now_btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        simpleUPI.pay(MainActivity.this, "1.00", "");
     }
 });
```


# Suggestion
If you have any suggestions or issue then please contact me at [this](https://devsuggest.com/)
