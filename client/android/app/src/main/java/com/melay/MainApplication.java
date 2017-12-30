package com.melay;

import android.app.Application;

import com.facebook.react.ReactApplication;
import com.react.SmsPackage;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.reactnativenavigation.NavigationApplication;

import java.util.Arrays;
import java.util.List;

public class MainApplication extends NavigationApplication  {

  @Override

  public boolean isDebug() {

      return BuildConfig.DEBUG;

  }
  //@Nullable
  @Override
  public List<ReactPackage> createAdditionalReactPackages() {

      return null;

  }
  @Override
  public String getJSMainModuleName() {
    return "index";
  }
}


