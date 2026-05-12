import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class LogoutTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void setUp() {

        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get("http://localhost/opencart/");
    }

    @Test
    public void testLogout() {

        // ===== 1. Click My Account =====
        By myAccount = By.xpath("//span[contains(text(),'My Account')]");
        wait.until(ExpectedConditions.elementToBeClickable(myAccount)).click();

        // ===== 2. Click Login =====
        wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("Login")
        )).click();

        // ===== 3. Nhập Email =====
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("input-email")
        )).sendKeys("nguyenhanh@gmail.com");

        // ===== 4. Nhập Password =====
        driver.findElement(By.id("input-password"))
                .sendKeys("020103");

        // ===== 5. Click Login =====
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//form[contains(@action,'login')]//button[@type='submit']")
        )).click();

        // ===== WAIT KẾT QUẢ =====
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("account/account"),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-danger"))
        ));

        // ===== CHECK =====
        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL: " + currentUrl);

        if (currentUrl.contains("account/account")) {
            System.out.println("LOGIN SUCCESS");
        } else {
            String error = driver.findElement(By.cssSelector(".alert-danger")).getText();
            System.out.println("LOGIN FAIL: " + error);
            Assert.fail("Login thất bại");
        }
        // ===== 7. Click My Account (mở dropdown) =====
        wait.until(ExpectedConditions.elementToBeClickable(myAccount)).click();

        // ===== 8. Đợi Logout xuất hiện =====
        By logoutBtn = By.xpath("//a[contains(@href,'route=account/logout')]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(logoutBtn));

        // ===== 9. Click Logout =====
        wait.until(ExpectedConditions.elementToBeClickable(logoutBtn)).click();

        // ===== 10. Verify logout =====
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(text(),'Account Logout')]")
        ));

        Assert.assertTrue(
                driver.findElement(By.xpath("//h1[contains(text(),'Account Logout')]")).isDisplayed(),
                "Logout FAILED!"
        );
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}