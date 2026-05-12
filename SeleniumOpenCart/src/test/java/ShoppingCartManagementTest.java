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



    public void safeClick(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});", element);
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }


    public void addProductByName( String productName) {

        driver.get(baseUrl);


        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,700)");

        WebElement product = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//a[text()='" + productName + "']")));

        safeClick(product);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("button-cart")));

        WebElement addToCart = wait.until(ExpectedConditions
                .elementToBeClickable(By.id("button-cart")));

        safeClick(addToCart);


        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".alert-success")));
    }


    public void openCart() {

        WebElement cartBtn = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//a[@title='Shopping Cart']")));

        safeClick(cartBtn);


        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("route=checkout/cart"),
                ExpectedConditions.visibilityOfElementLocated(By.tagName("body"))
        ));
    }



    public void removeAllProducts() {

        while (true) {
            try {
                WebElement removeBtn = wait.until(ExpectedConditions
                        .elementToBeClickable(By.xpath("//a[contains(@href,'cart.remove')]")));

                safeClick(removeBtn);

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

        openCart();

        WebElement productRow = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//div[@id='shopping-cart']//table//tbody//tr")));

        Assert.assertTrue(productRow.isDisplayed());

    }


    //  TC4: Remove 1 product
    @Test(priority = 4)
    public void TC04_removeOneProduct() {

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

        addProductByName("MacBook");
        addProductByName("iPhone");

        openCart();

        int rows = wait.until(ExpectedConditions
                        .presenceOfAllElementsLocatedBy(By.xpath("//div[@id='shopping-cart']//table//tbody//tr"))).size();

        Assert.assertEquals(rows, 2);

        removeAllProducts();

        WebElement emptyMsg = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//p[contains(text(),'Your shopping cart is empty')]")));

        Assert.assertTrue(emptyMsg.isDisplayed());

    }

    // TC6: Add product với số lượng âm 
    @Test(priority = 6)
    public void TC06_addProductWithNegativeQuantity() {

        driver.get(baseUrl);

        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,700)");

        WebElement product = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//a[text()='MacBook']")));

        safeClick(product);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("button-cart")));

        WebElement qtyInput = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.id("input-quantity")));

        qtyInput.clear();
        qtyInput.sendKeys("-1");

        WebElement addToCart = wait.until(ExpectedConditions
                .elementToBeClickable(By.id("button-cart")));
        safeClick(addToCart);

        openCart();

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


