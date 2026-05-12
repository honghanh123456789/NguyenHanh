import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class BuyProductTest {

    WebDriver driver;
    WebDriverWait wait;

    String baseUrl = "http://localhost/opencart/";

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get(baseUrl);
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    // SAFE CLICK
    public void safeClick(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});", element);
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }


    // TC01:

    @Test
    public void TC01_Login_Checkout_Success() {

        //  1. LOGIN
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(By.linkText("My Account"))));
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Login"))));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-email")))
                .sendKeys("nguyenhanh@gmail.com");
        driver.findElement(By.id("input-password")).sendKeys("020103");

        driver.findElement(By.xpath("//form[contains(@action,'account/login')]//button[@type='submit']")).click();

        // ===== VERIFY LOGIN SUCCESS (đang ở My Account) =====
        wait.until(ExpectedConditions.urlContains("route=account/account"));

        //  2. CLICK Phones & PDAs
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("Phones & PDAs")
        )));

        //  ĐỢI PRODUCT LIST LOAD
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".product-thumb")
        ));

        //  SCROLL xuống để chắc chắn thấy product
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,400)");

        //  CLICK iPhone
        WebElement iphone = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@class='product-thumb']//a[contains(text(),'iPhone')]")
        ));

        safeClick(iphone);

        //  4. ADD TO CART
        WebElement addToCart = wait.until(ExpectedConditions.elementToBeClickable(By.id("button-cart")));
        safeClick(addToCart);

        // Verify add success
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".alert-success")
        ));

        //  5. CLICK GIỎ HÀNG
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@title='Shopping Cart']")
        )));

        wait.until(ExpectedConditions.urlContains("route=checkout/cart"));

        //  6. CLICK CHECKOUT
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("Checkout")
        )));

        //  CHỌN ADDRESS CHẮC CHẮN
        WebElement addressElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.name("address_id")
        ));

        // Đợi option load xong
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector("select[name='address_id'] option"), 1
        ));

        // Select
        Select addressDropdown = new Select(addressElement);

        // Chọn option KHÔNG phải "Please Select"
        for (WebElement option : addressDropdown.getOptions()) {
            if (!option.getText().contains("Please Select")) {
                addressDropdown.selectByVisibleText(option.getText());
                break;
            }
        }

        //  VERIFY đã chọn thành công
        wait.until(driver -> !addressDropdown.getFirstSelectedOption()
                .getText().contains("Please Select"));


        //  CLICK CHOOSE SHIPPING
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[contains(text(),'Choose')])[1]")
        )));

        //  CHỌN SHIPPING METHOD
        WebElement shippingOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[name='shipping_method']")
        ));
        safeClick(shippingOption);

        //  CLICK CONTINUE
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.id("button-shipping-method")
        )));

        //  CLICK CHOOSE PAYMENT
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[contains(text(),'Choose')])[2]")
        )));

        //  CHỌN PAYMENT METHOD
        WebElement paymentOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[name='payment_method']")
        ));
        safeClick(paymentOption);

        //  CLICK CONTINUE
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.id("button-payment-method")
        )));

        //   CONFIRM ORDER
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Confirm')]")
        )));

        //  VERIFY SUCCESS
        WebElement success = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(text(),'Your order has been placed')]")
        ));

        Assert.assertTrue(success.isDisplayed());
    }


    // TC02: FAIL - EMPTY FIELD

    @Test
    public void TC02_Checkout_Fail_Empty() {

        //  1. LOGIN
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(By.linkText("My Account"))));
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Login"))));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-email")))
                .sendKeys("nguyenhanh@gmail.com");
        driver.findElement(By.id("input-password")).sendKeys("020103");

        driver.findElement(By.xpath("//form[contains(@action,'account/login')]//button[@type='submit']")).click();

        //  VERIFY LOGIN SUCCESS (đang ở My Account)
        wait.until(ExpectedConditions.urlContains("route=account/account"));

        //  2. CLICK Phones & PDAs
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("Phones & PDAs")
        )));

        //  ĐỢI PRODUCT LIST LOAD
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".product-thumb")
        ));

        //  SCROLL xuống để chắc chắn thấy product
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,400)");

        //  CLICK iPhone
        WebElement iphone = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@class='product-thumb']//a[contains(text(),'iPhone')]")
        ));

        safeClick(iphone);

        //  ADD TO CART
        WebElement addToCart = wait.until(ExpectedConditions.elementToBeClickable(By.id("button-cart")));
        safeClick(addToCart);

        // Verify add success
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".alert-success")
        ));

        //  CLICK GIỎ HÀNG
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@title='Shopping Cart']")
        )));

        wait.until(ExpectedConditions.urlContains("route=checkout/cart"));

        //  CLICK CHECKOUT
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("Checkout")
        )));

        //  CLICK NEW ADDRESS
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//label[contains(.,'I want to use a new address')]")
        )));

        //  ĐỢI FORM LOAD
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.name("firstname")
        ));

        //  SCROLL xuống nút Continue
        WebElement continueBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(text(),'Continue')]")
        ));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", continueBtn
        );

        //  CLICK CONTINUE (KHÔNG NHẬP GÌ)
        safeClick(continueBtn);

    }


    // TC03: GUEST SUCCESS

    @Test
    public void TC03_Guest_Checkout_Success() {

        //  CLICK Phones & PDAs
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("Phones & PDAs")
        )));

        //  ĐỢI PRODUCT LIST LOAD
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".product-thumb")
        ));

        //  SCROLL xuống
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,400)");

        // ===== CLICK iPhone =====
        WebElement iphone = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@class='product-thumb']//a[contains(text(),'iPhone')]")
        ));

        safeClick(iphone);

        // ===== ADD TO CART =====
        WebElement addToCart = wait.until(ExpectedConditions.elementToBeClickable(By.id("button-cart")));
        safeClick(addToCart);

        // Verify add success
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".alert-success")
        ));

        // =====  CLICK GIỎ HÀNG =====
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@title='Shopping Cart']")
        )));

        wait.until(ExpectedConditions.urlContains("route=checkout/cart"));

        // ===== CLICK CHECKOUT =====
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("Checkout")
        )));

        // ===== CHỌN GUEST CHECKOUT =====
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//label[contains(text(),'Guest Checkout')]")
        )));


        // ===== WAIT FORM LOAD =====
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.name("firstname")
        ));
        // ===== FILL FORM =====
        driver.findElement(By.name("firstname")).sendKeys("hanh");
        driver.findElement(By.name("lastname")).sendKeys("nguyen");
        driver.findElement(By.name("email")).sendKeys("test@gmail.com");

        // ===== SCROLL xuống  =====
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,400)");

        // ===== WAIT FORM LOAD =====
        WebElement address = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.name("shipping_address_1")
        ));

        driver.findElement(By.name("shipping_address_1")).sendKeys("Hanoi");
        driver.findElement(By.name("shipping_city")).sendKeys("Hanoi");
        driver.findElement(By.name("shipping_postcode")).sendKeys("100abcd");

        // ===== CHỌN COUNTRY =====
        Select country = new Select(wait.until(ExpectedConditions.elementToBeClickable(
                By.name("shipping_country_id")
        )));
        country.selectByVisibleText("Viet Nam");

        // ===== ĐỢI REGION LOAD THEO COUNTRY =====
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector("select[name='shipping_zone_id'] option"), 1
        ));

        // ===== CHỌN REGION: HA NOI =====
        Select zone = new Select(wait.until(ExpectedConditions.elementToBeClickable(
                By.name("shipping_zone_id")
        )));
        zone.selectByVisibleText("Ha Noi");

        // ===== 8. CONTINUE ADDRESS =====
        WebElement continueBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(text(),'Continue')]")
        ));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", continueBtn
        );
        safeClick(continueBtn);

        // SCROLL lên trên
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,0)");

        // ===== 9. SHIPPING =====
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[contains(text(),'Choose')])[1]")
        )));

        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[name='shipping_method']")
        )));

        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.id("button-shipping-method")
        )));

        // ===== 10. PAYMENT =====
        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[contains(text(),'Choose')])[2]")
        )));

        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[name='payment_method']")
        )));

        safeClick(wait.until(ExpectedConditions.elementToBeClickable(
                By.id("button-payment-method")
        )));

        // ===== SCROLL xuống  =====
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,400)");

        // ===== 12. CONFIRM =====
        WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Confirm')]")
        ));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", confirmBtn
        );
        wait.until(ExpectedConditions.elementToBeClickable(confirmBtn));

        safeClick(confirmBtn);

        // ===== 13. VERIFY SUCCESS =====
        WebElement success = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(text(),'Your order has been placed')]")
        ));

        Assert.assertTrue(success.isDisplayed());
    }
}