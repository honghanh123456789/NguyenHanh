import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class ViewProductsTest {

    WebDriver driver;
    WebDriverWait wait;

    String baseUrl = "http://localhost/opencart/";

    @BeforeClass
    public void setup() {

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // =========================
    // Hàm scroll + click
    // =========================
    public void safeClick(WebElement element) {
        try {
            // Scroll tới element
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);

            // Đợi clickable
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();

        } catch (Exception e) {
            // Fallback JS click nếu bị lỗi
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }


    // TC 1: Chưa login

    @Test(priority = 1)
    public void viewProductDetailWithoutLogin() {

        driver.get(baseUrl);

        WebElement product = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("(//div[@class='product-thumb']//h4/a)[1]")));

        String nameHome = product.getText();

        // SCROLL + CLICK
        safeClick(product);

        WebElement nameDetail = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//h1")));

        Assert.assertEquals(nameDetail.getText(), nameHome);

        // Kiểm tra giá
        WebElement price = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//ul[@class='list-unstyled']//h2")));
        Assert.assertTrue(price.isDisplayed());

        // Add to Cart
        WebElement addToCart = driver.findElement(By.id("button-cart"));
        Assert.assertTrue(addToCart.isDisplayed());


        // SCROLL xuống phần mô tả

        WebElement descriptionTab = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//a[contains(@href,'#tab-description')]")));

        safeClick(descriptionTab);


        // Đợi nội dung mô tả

        WebElement descriptionContent = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.id("tab-description")));

        // Scroll xuống nội dung
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                descriptionContent
        );

        // Verify có mô tả
        Assert.assertTrue(descriptionContent.getText().length() > 0);

        System.out.println("Description: " + descriptionContent.getText());

    }

    // =========================
    // 🔹 TC 2: Đã login
    // =========================
    @Test(priority = 2)
    public void viewProductDetailWithLogin() {

        driver.get(baseUrl);

        //  Login

        WebElement myAccount = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//span[text()='My Account']")));
        safeClick(myAccount);

        WebElement loginBtn = wait.until(ExpectedConditions
                .elementToBeClickable(By.linkText("Login")));
        safeClick(loginBtn);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-email")))
                .sendKeys("nguyenhanh@gmail.com");

        driver.findElement(By.id("input-password"))
                .sendKeys("020103");
        // Click login
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//form[contains(@action,'login')]//button[@type='submit']")
        )).click();


        // 4: VỀ HOME

        try {
            // Click icon Home trong breadcrumb
            WebElement homeBtn = wait.until(ExpectedConditions
                    .elementToBeClickable(By.xpath("//ul[@class='breadcrumb']//a")));
            safeClick(homeBtn);

        } catch (Exception e) {
            // fallback nếu click fail
            driver.get(baseUrl + "index.php?route=common/home&language=en-gb");
        }


        // 5: WAIT ĐÚNG HOME
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("route=common/home"),
                ExpectedConditions.urlContains("opencart/")
        ));

        // Đợi search box (xác nhận home load xong)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("search")));


        // 6: SCROLL XUỐNG
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,700)");


        // 7: CHỌN SẢN PHẨM

        WebElement product = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("(//h4/a)[1]")));

        String nameHome = product.getText();

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                product
        );

        safeClick(product);


        // 8: VERIFY DETAIL
        WebElement nameDetail = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//h1")));

        Assert.assertEquals(nameDetail.getText(), nameHome);

        // giá
        Assert.assertTrue(wait.until(ExpectedConditions
                        .visibilityOfElementLocated(By.xpath("//ul[@class='list-unstyled']//h2")))
                .isDisplayed());

        // add to cart
        Assert.assertTrue(wait.until(ExpectedConditions
                        .visibilityOfElementLocated(By.id("button-cart")))
                .isDisplayed());


        // SCROLL xuống phần mô tả
        WebElement descriptionTab = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//a[contains(@href,'#tab-description')]")));

        safeClick(descriptionTab);


        // Đợi nội dung mô tả
        WebElement descriptionContent = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.id("tab-description")));

        // Scroll xuống nội dung
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                descriptionContent
        );

        // Verify có mô tả
        Assert.assertTrue(descriptionContent.getText().length() > 0);

        System.out.println("Description: " + descriptionContent.getText());

    }
    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}