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
To get API Key [click here](https://devsuggest.com/)
 
```java
public class MainActivity extends AppCompatActivity implements SimpleUPI.SimpleUPICallbacks {

 .....
    
  SimpleUPI simpleUPI = new SimpleUPI();
  simpleUPI.init(MainActivity.this, "API_KEY");

  pay_now_btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        simpleUPI.pay(MainActivity.this, "1.00", "");
       }
   });
   
  .....
  
}
``` 
# Callbacks
```java
@Override
public void onPaymentSuccess(String transaction_id, String transaction_status, String transaction_ref_no) {
}

@Override
public void onPaymentFailure(String error_message) {
}

@Override
 public void onUnknownError(String transaction_status) {
}

@Override
public void onPaymentAppNotFound(String error_message) {
}

@Override
public void onPaymentDismiss() {
}
```

# Suggestion
If you have any suggestions or issue then please contact me at [this](https://devsuggest.com/)
