import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class ViewOrderTest {

    WebDriver driver;
    WebDriverWait wait;
    Actions actions;

    String baseUrl = "http://localhost/opencart/";

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        actions = new Actions(driver);
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) driver.quit();
    }

    // ===== LOGIN =====
    private void login(String email, String password) {
        driver.get(baseUrl + "index.php?route=account/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-email"))).sendKeys(email);
        driver.findElement(By.id("input-password")).sendKeys(password);
        // Click Login
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//form[contains(@action,'login')]//button[@type='submit']")
        )).click();

        // Verify login thành công
        wait.until(ExpectedConditions.urlContains("route=account/account"));
    }

    // ===== CLICK MY ACCOUNT → ORDER HISTORY  =====
    private void OrderHistory() {

        // ===== CLICK MY ACCOUNT  =====
        wait.until(ExpectedConditions.urlContains("account/account"));

        // click Order History trong content page
        WebElement orderHistory = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@id='content']//a[contains(@href,'route=account/order')]")
        ));

        orderHistory.click();

        wait.until(ExpectedConditions.urlContains("account/order"));
    }

    // ==============================
    // TC01 – Có đơn hàng
    // ==============================
    @Test
    public void TC01_viewOrderHistory_withOrders() {

        login("nguyenhanh@gmail.com", "020103");

        OrderHistory();

        List<WebElement> orders = driver.findElements(
                By.cssSelector(".table-responsive tbody tr")
        );
        Assert.assertTrue(orders.size() > 0, "Không có đơn hàng");
    }

    // ==============================
    // TC02 – Không có đơn hàng
    // ==============================
    @Test
    public void TC02_viewOrderHistory_noOrders() {

        login("hanh@gmail.com", "123456@");

        OrderHistory();

        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'You have not made any previous orders')]")
        ));

        Assert.assertTrue(message.isDisplayed(), "Không hiển thị thông báo no orders");
    }

    // ==============================
    // TC03 – Xem chi tiết đơn hàng
    // ==============================
    @Test
    public void TC03_viewOrderDetail() {

        login("nguyenhanh@gmail.com", "020103");

        OrderHistory();

        List<WebElement> orders = driver.findElements(
                By.cssSelector(".table-responsive tbody tr")
        );
        Assert.assertTrue(orders.size() > 0, "Không có đơn hàng");

        // click icon con mắt (View)
        WebElement viewBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//i[contains(@class,'fa-eye')]/parent::a")
        ));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", viewBtn);

        //  CHECK TRANG DETAIL
        WebElement orderDetailTable = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".table.table-bordered")
        ));
        // SCROLL XUỐNG CUỐI
        // ===== SCROLL xuống  =====
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,700)");

        Assert.assertTrue(orderDetailTable.isDisplayed(), "Không hiển thị chi tiết đơn hàng");
    }
}