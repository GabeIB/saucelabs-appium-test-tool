/*
Must have the following environment variables
SAUCE_USERNAME
SAUCE_ACCESS_KEY
SAUCE_STAGING_USERNAME
SAUCE_STAGING_ACCESS_KEY
*/
package tests;

import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.URL;

public class Android_SauceLabs_Appium_Test {
    // options
    private static final boolean STAGING = false;
    private static final boolean EMULATE = true;
    private static final String ANDROID_VERSION = "10";

    private static final String APP_NAME = "Android.SauceLabs.Mobile.Sample.app.2.7.1.apk";
    private static final String APP_ID = ""; //if ID is not empty it overrides name
    private static final String APP_ACTIVITY = "com.swaglabsmobileapp.MainActivity";
    // end options

    private AndroidDriver driver;

    String usernameID = "test-Username";
    String passwordID = "test-Password";
    String submitButtonID = "test-LOGIN";
    By ProductTitle = By.xpath("//android.widget.TextView[@text='PRODUCTS']");

    @BeforeMethod
    public void setUp(Method method) throws Exception {
        System.out.println("Saucelabs Android Appium test - setup method start");
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

        String appUrl;
        if (!APP_ID.isEmpty()){
            appUrl = "storage:"+APP_ID;
        } else {
            appUrl = "storage:filename="+APP_NAME;
        }

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("appActivity", APP_ACTIVITY);
        caps.setCapability("platformName", "Android");
        caps.setCapability("app", appUrl);
        caps.setCapability("platformVersion", ANDROID_VERSION);
        MutableCapabilities sauceOptions = new MutableCapabilities();
        sauceOptions.setCapability("name", methodName); //Method added as parameter

        if (EMULATE) {
            caps.setCapability("automationName", "UiAutomator2");
            caps.setCapability("deviceName", "Android GoogleAPI Emulator"); //This will change to type of device e.g. Pixel 4 on Saucelabs
        } else {
            sauceOptions.setCapability("username", username); //Method added as parameter
            sauceOptions.setCapability("accessKey", accessKey); //Method added as parameter
        }
        caps.setCapability("sauce:options", sauceOptions);

        driver = new AndroidDriver(url, caps);
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        System.out.println("Saucelabs Android EMU test - after hook");
        try{
            if (driver != null) {
                ((JavascriptExecutor) driver).executeScript("sauce:job-result=" + (result.isSuccess() ? "passed" : "failed"));
            }
        }
        finally {
            System.out.println("release driver");
            driver.quit();
        }
    }

    @Test
    public void loginToSwagLabsTestValid() {
        System.out.println("Sauce - Start loginToSwagLabsTestValid test");

        login("standard_user", "secret_sauce");

        // Verification
        Assert.assertTrue(isOnProductsPage());
    }

    @Test
    public void loginTestValidProblem() {
        System.out.println("Sauce - Start loginTestValidProblem test");

        login("problem_user", "secret_sauce");

        // Verification - we on Product page
        Assert.assertTrue(isOnProductsPage());
    }

    public void login(String user, String pass){

        WebDriverWait wait = new WebDriverWait(driver, 5);
        final WebElement usernameEdit = wait.until(ExpectedConditions.visibilityOfElementLocated(new MobileBy.ByAccessibilityId(usernameID)));

        usernameEdit.click();
        usernameEdit.sendKeys(user);

        WebElement passwordEdit = driver.findElementByAccessibilityId(passwordID);
        passwordEdit.click();
        passwordEdit.sendKeys(pass);

        WebElement submitButton = driver.findElementByAccessibilityId(submitButtonID);
        submitButton.click();
    }

    public boolean isOnProductsPage() {
        //Create an instance of a Appium explicit wait so that we can dynamically wait for an element
        WebDriverWait wait = new WebDriverWait(driver, 5);

        //wait for the product field to be visible and store that element into a variable
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(ProductTitle));
        } catch (TimeoutException e){
            System.out.println("*** Timed out waiting for product page to load.");
            return false;
        }
        return true;
    }
}
