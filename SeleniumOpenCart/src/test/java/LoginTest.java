
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class LoginTest {

    WebDriver driver;
    WebDriverWait wait;

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

    // Mở trang login
    public void openLoginPage() {
        driver.get("http://localhost/opencart/index.php?route=account/login");
    }

    // Hàm login dùng chung
    public void login(String email, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-email"))).clear();
        driver.findElement(By.id("input-email")).sendKeys(email);

        driver.findElement(By.id("input-password")).clear();
        driver.findElement(By.id("input-password")).sendKeys(password);

        driver.findElement(By.xpath("//form[contains(@action,'account/login')]//button[@type='submit']")).click();
    }

    // Hàm check lỗi
    public void verifyLoginFail() {
        wait.until(ExpectedConditions.urlContains("account/login"));
        Assert.assertTrue(driver.getCurrentUrl().contains("account/login"));
    }


    // TC01 – LOGIN THÀNH CÔNG

    @Test
    public void TC01_loginSuccess() {
        openLoginPage();
        login("hanhnguyenone2004@gmail.com", "123456");

        WebElement logout = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//aside//a[contains(@href,'logout')]")
                )
        );

        Assert.assertTrue(logout.isDisplayed());
    }


    // TC02 – SAI PASSWORD

    @Test
    public void TC02_saiPassword() {
        openLoginPage();
        login("hanhnguyenone2004@gmail.com", "456123");
        verifyLoginFail();
    }


    // TC03 – EMAIL KHÔNG TỒN TẠI

    @Test
    public void TC03_emailKhongTonTai() {
        openLoginPage();
        login("abcxyz@gmail.com", "123456");
        verifyLoginFail();
    }


    // TC04 – EMAIL THIẾU MIỀN .COM

    @Test
    public void TC04_emailThieuCom() {
        openLoginPage();
        login("abc@gmail", "123456");
        verifyLoginFail();
    }


    // TC05 – EMAIL THIẾU @

    @Test
    public void TC05_emailThieu() {
        openLoginPage();
        login("abcgmail.com", "123456");
        verifyLoginFail();
    }


    // TC06 – EMAIL RỖNG

    @Test
    public void TC06_rongEmail() {
        openLoginPage();
        login("", "123456");
        verifyLoginFail();
    }


    // TC07 – PASSWORD RỖNG

    @Test
    public void TC07_rongPassword() {
        openLoginPage();
        login("hanhnguyenone2004@gmail.com", "");
        verifyLoginFail();
    }


    // TC08 – NHẬP EMAIL KHÔNG CÓ THÔNG TIN TRƯỚC @

    @Test
    public void TC08_thieuThongTin() {
        openLoginPage();
        login("@gmail.com", "123");
        verifyLoginFail();
    }


    //  TC09 – EMAIL THIEU GMAIL

    @Test
    public void TC09_thieuGmail() {
        openLoginPage();
        login("abc@.com", "123");
        verifyLoginFail();
    }


    // TC10 – EMAIL CÓ KHOẢNG TRẮNG

    @Test
    public void TC10_emailCoKhoangTrang() {
        openLoginPage();
        login("hanhnguye none2004@gmail.com  ", "123456");
        verifyLoginFail();
    }
}

