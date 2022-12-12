package tests;

import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;

public class iOS_Appium_Test {
    boolean STAGING = true;
    IOSDriver driver;

    @BeforeMethod
    public void setUp(Method method) throws Exception {
        System.out.println("Saucelabs Appium test - setup method start");
        String methodName = method.getName(); //added
        System.out.println("setting up "+methodName);

        String username;
        String accessKey;
        String sauceUrl;
        if (STAGING) {
            username = System.getenv("SAUCE_STAGING_USERNAME");
            accessKey = System.getenv("SAUCE_STAGING_ACCESS_KEY");
            sauceUrl = "@ondemand.staging.saucelabs.net:443";
        } else {
            username = System.getenv("SAUCE_USERNAME");
            accessKey = System.getenv("SAUCE_ACCESS_KEY");
            sauceUrl = "@ondemand.us-west-1.saucelabs.com:443";
        }
        String SAUCE_REMOTE_URL = "https://"+username+":"+accessKey+sauceUrl+"/wd/hub";
        URL url = new URL(SAUCE_REMOTE_URL);

        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("platformName", "iOS");
        caps.setCapability("appium:deviceName", "iPhone 12 Pro Max");
        caps.setCapability("appium:automationName", "XCUITest");
        caps.setCapability("setupDeviceLock", true);
        MutableCapabilities sauceOptions = new MutableCapabilities();
        sauceOptions.setCapability("username", username); //Method added as parameter
        sauceOptions.setCapability("accessKey", accessKey); //Method added as parameter
        caps.setCapability("sauce:options", sauceOptions);

        driver = new IOSDriver(url, caps);
    }

    @Test
    public void loginTestValidProblem() {
        System.out.println("Sauce - Start loginTestValidProblem test");
        driver.activateApp("com.apple.Preferences");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        HashMap<String, String> scrollObject = new HashMap<String, String>();
        scrollObject.put("direction", "up");
        scrollObject.put("velocity", "1000");
        js.executeScript("mobile: swipe", scrollObject);
        HashMap<String, String> tapObject = new HashMap<String, String>();
        tapObject.put("x", "100");
        tapObject.put("y", "480");
        js.executeScript("mobile: tap", tapObject);
        HashMap<String, String> smallscroll = new HashMap<String, String>();
        smallscroll.put("direction", "up");
        smallscroll.put("velocity", "300");
        js.executeScript("mobile: swipe", scrollObject);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
