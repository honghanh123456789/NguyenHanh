import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class ShoppingCartManagementTest {

    WebDriver driver;
    WebDriverWait wait;

    String baseUrl = "http://localhost/opencart/";

    @BeforeClass
    public void setup() {

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
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


    // ADD PRODUCT
    public void addProductByName( String productName) {

        driver.get(baseUrl);

        // Scroll xuống
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,700)");

        WebElement product = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//a[text()='" + productName + "']")));

        safeClick(product);

        // WAIT TRANG PRODUCT LOAD
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("button-cart")));

        WebElement addToCart = wait.until(ExpectedConditions
                .elementToBeClickable(By.id("button-cart")));
        // click add to cart
        safeClick(addToCart);

        // Wait thông báo success
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".alert-success")));
    }

    // OPEN CART
    public void openCart() {

        WebElement cartBtn = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//a[@title='Shopping Cart']")));

        safeClick(cartBtn);

        // Đợi cart page load
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("route=checkout/cart"),
                ExpectedConditions.visibilityOfElementLocated(By.tagName("body"))
        ));
    }


    // REMOVE ALL
    public void removeAllProducts() {

        while (true) {
            try {
                WebElement removeBtn = wait.until(ExpectedConditions
                        .elementToBeClickable(By.xpath("//a[contains(@href,'cart.remove')]")));

                safeClick(removeBtn);

                // đợi reload
                wait.until(ExpectedConditions.stalenessOf(removeBtn));

            } catch (Exception e) {
                break;
            }
        }
    }


    // TC1: Cart rỗng
    @Test(priority = 1)
    public void TC01_viewEmptyCart() {

        driver.get(baseUrl);
        openCart();

        WebElement emptyMsg = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//p[contains(text(),'Your shopping cart is empty')]")));

        Assert.assertTrue(emptyMsg.isDisplayed());

    }


    //  TC2: Add to cart
    @Test(priority = 2)
    public void TC02_addProductToCart() {

        addProductByName("MacBook");

        WebElement success = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector(".alert-success")));

        Assert.assertTrue(success.isDisplayed());

    }


    //  TC3: View cart có sản phẩm
    @Test(priority = 3)
    public void TC03_viewCartWithProduct() {

        //addProductByName("MacBook");
        openCart();

        // Đợi row sản phẩm xuất hiện
        WebElement productRow = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//div[@id='shopping-cart']//table//tbody//tr")));

        Assert.assertTrue(productRow.isDisplayed());

    }


    //  TC4: Remove 1 product
    @Test(priority = 4)
    public void TC04_removeOneProduct() {

        //addProductByName("MacBook");
        openCart();

        WebElement removeBtn = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//a[contains(@href,'cart.remove')]")));

        safeClick(removeBtn);

        wait.until(ExpectedConditions.stalenessOf(removeBtn));

        WebElement emptyMsg = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//p[contains(text(),'Your shopping cart is empty')]")));

        Assert.assertTrue(emptyMsg.isDisplayed());

    }

    // TC5: Remove all products
    @Test(priority = 5)
    public void TC5_removeAllProductsTest() {

        //  ADD 2 SẢN PHẨM
        addProductByName("MacBook");
        addProductByName("iPhone");

        // MỞ CART
        openCart();

        //  VERIFY CÓ 2 SẢN PHẨM
        int rows = wait.until(ExpectedConditions
                        .presenceOfAllElementsLocatedBy(By.xpath("//div[@id='shopping-cart']//table//tbody//tr"))).size();

        Assert.assertEquals(rows, 2);

        // XÓA TẤT CẢ
        removeAllProducts();

        // VERIFY CART RỖNG
        WebElement emptyMsg = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//p[contains(text(),'Your shopping cart is empty')]")));

        Assert.assertTrue(emptyMsg.isDisplayed());

    }

    // TC6: Add product với số lượng âm (EXPECTED FAIL)
    @Test(priority = 6)
    public void TC06_addProductWithNegativeQuantity() {

        // 1. TRUY CẬP TRANG CHỦ
        driver.get(baseUrl);

        // Scroll xuống để thấy sản phẩm
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,700)");

        // 2. CLICK SẢN PHẨM
        WebElement product = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//a[text()='MacBook']")));

        safeClick(product);

        // 3. ĐỢI TRANG PRODUCT LOAD
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("button-cart")));

        // 4. NHẬP SỐ LƯỢNG ÂM
        WebElement qtyInput = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.id("input-quantity")));

        qtyInput.clear();
        qtyInput.sendKeys("-1");

        // 5. CLICK ADD TO CART
        WebElement addToCart = wait.until(ExpectedConditions
                .elementToBeClickable(By.id("button-cart")));
        safeClick(addToCart);

        // MỞ CART ĐỂ CHECK
        openCart();

        // CỐ TÌNH EXPECT có sản phẩm → nhưng thực tế KHÔNG có → FAIL
        WebElement productRow = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//div[@id='shopping-cart']//table//tbody//tr")));

        Assert.assertTrue(productRow.isDisplayed(),
                "Expected product in cart but it was not added");
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}


