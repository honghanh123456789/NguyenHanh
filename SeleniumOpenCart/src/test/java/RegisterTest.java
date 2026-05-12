
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class RegisterTest {

    WebDriver driver;
    WebDriverWait wait;


    // SETUP / TEARDOWN

    @BeforeMethod
    public void setup() {

        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void teardown() {
        driver.quit();
    }


    // HELPER FUNCTIONS

    public void openRegisterPage() {
        driver.get("http://localhost/opencart/index.php?route=account/register");
    }

    public void register(String firstName, String lastName, String email, String password, boolean agreePolicy) {

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-firstname"))).clear();
        driver.findElement(By.id("input-firstname")).sendKeys(firstName);

        driver.findElement(By.id("input-lastname")).clear();
        driver.findElement(By.id("input-lastname")).sendKeys(lastName);

        driver.findElement(By.id("input-email")).clear();
        driver.findElement(By.id("input-email")).sendKeys(email);

        driver.findElement(By.id("input-password")).clear();
        driver.findElement(By.id("input-password")).sendKeys(password);

        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Tìm checkbox trước
        WebElement privacy = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.name("agree"))
        );

        // Scroll xuống khu vực checkbox (đảm bảo luôn thấy phần dưới)
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", privacy);

        // Tick Privacy nếu cần
        if (agreePolicy) {
            wait.until(ExpectedConditions.elementToBeClickable(privacy));
            js.executeScript("arguments[0].click();", privacy);
        }

        // Tìm nút Continue
        WebElement btn = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("button.btn-primary"))
        );
        // Scroll tới button
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        js.executeScript("arguments[0].click();", btn);
    }

    // VERIFY

    public void verifySuccess() {
        wait.until(ExpectedConditions.urlContains("account/success"));
        Assert.assertTrue(driver.getCurrentUrl().contains("account/success"));
    }

    public void verifyFail() {

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("account/register"),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-danger"))
        ));

        boolean isStillRegisterPage = driver.getCurrentUrl().contains("account/register");

        String page = driver.getPageSource();

        boolean hasError =
                page.contains("Warning") ||
                        page.contains("must be") ||
                        page.contains("invalid");

        Assert.assertTrue(isStillRegisterPage || hasError, "Không thấy lỗi!");
    }

    // TEST CASES

    // TC01 – Đăng ký thành công
    @Test
    public void TC01_dangKyThanhCong() {
        openRegisterPage();

        String email = "hanhnguyen@gmail.com";

        register("Hanh", "Nguyen", email, "123456", true);

        verifySuccess();
    }

    // TC02 – Bỏ trống First Name
    @Test
    public void TC02_missingFirstName() {
        openRegisterPage();
        register("", "Nguyen", "test@gmail.com", "123456", true);
        verifyFail();
    }

    // TC03 - bỏ trống Last Name
    @Test
    public void TC03_missingLastName() {
        openRegisterPage();
        register("Hanh", "", "test@gmail.com", "123456", true);
        verifyFail();
    }

    // TC04 - bỏ trống email
    @Test
    public void TC04_missingEmail() {
        openRegisterPage();
        register("Hanh", "Nguyen", "", "123456", true);
        verifyFail();
    }

    //TC05 - bỏ trống password
    @Test
    public void TC05_missingPassword() {
        openRegisterPage();
        register("Hanh", "Nguyen", "test@gmail.com", "", true);
        verifyFail();
    }

    // TC06 – Email đã tồn tại
    @Test
    public void TC06_emailDaTonTai() {
        openRegisterPage();
        register("Hanh", "Nguyen", "hanhnguyenone2004@gmail.com", "123456", true);
        verifyFail();
    }

    // TC07 – Email thiếu @
    @Test
    public void TC07_emailThieua() {
        openRegisterPage();
        register("Hanh", "Nguyen", "abcgmail.com", "123456", true);
        verifyFail();
    }

    // TC08 – Email thiếu .com
    @Test
    public void TC08_emailThieuCom() {
        openRegisterPage();
        register("Hanh", "Nguyen", "abc@gmail", "123456", true);
        verifyFail();
    }

    // TC09 – Email thiếu thông tin trước @
    @Test
    public void TC09_emailThieuThongTin() {
        openRegisterPage();
        register("Hanh", "Nguyen", "@gmail.com", "123456", true);
        wait.until(ExpectedConditions.urlContains("account/register"));

        Assert.assertTrue(driver.getCurrentUrl().contains("account/register"));
    }

    // TC010 – Email có khoảng trắng
    @Test
    public void TC10_emailCoKhoangTrang() {
        openRegisterPage();
        register("Hanh", "Nguyen", "hanh nguyen@gmail.com", "123456", true);
        verifyFail();
    }

    // TC11 – Password < 6
    @Test
    public void TC11_passwordQuaNgan() {
        openRegisterPage();
        register("Hanh", "Nguyen", "test@gmail.com", "123", true);
        wait.until(ExpectedConditions.urlContains("account/register"));

        String page = driver.getPageSource();

        Assert.assertTrue(
                page.contains("Password") ||
                        page.contains("must be") ||
                        page.contains("Warning") ||
                        driver.getCurrentUrl().contains("account/register"),
                "Không có lỗi password!"
        );
    }

    // TC12 – Password > 40
    @Test
    public void TC12_passwordQuaDai() {
        openRegisterPage();
        register("Hanh", "Nguyen", "test@gmail.com",
                "12345678901234567890123456789012345678901234567890", true);
        verifyFail();
    }

    // TC13 - không tick Privacy
    @Test
    public void TC13_khongTickPrivacy() {
        openRegisterPage();

        register("Hanh", "Nguyen", "test" + System.currentTimeMillis() + "@gmail.com", "123456", false);
        verifyFail();
    }

}