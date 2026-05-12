import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class ViewUserAccountTest {

    WebDriver driver;
    WebDriverWait wait;

    String baseUrl = "http://localhost/opencart/";
    String email = "nguyenhanh@gmail.com";
    String password = "020103";

    String expectedFirstName = "hanh";
    String expectedLastName = "Nguyen";

    @BeforeClass
    public void setup() {

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    public void testViewAccountInfo() {

 
        driver.get(baseUrl + "index.php?route=account/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-email"))).sendKeys(email);

        driver.findElement(By.id("input-password")).sendKeys(password);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//form[contains(@action,'login')]//button[@type='submit']")
        )).click();

        wait.until(ExpectedConditions.urlContains("route=account/account"));

        wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("Edit your account information")
        )).click();


        wait.until(ExpectedConditions.urlContains("route=account/edit"));


        String actualFirstName = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("input-firstname"))).getAttribute("value");

        String actualLastName = driver.findElement(By.id("input-lastname")).getAttribute("value");

        String actualEmail = driver.findElement(By.id("input-email")).getAttribute("value");


        Assert.assertEquals(actualFirstName, expectedFirstName);
        Assert.assertEquals(actualLastName, expectedLastName);
        Assert.assertEquals(actualEmail, email);

        System.out.println("PASS - View Account Info");
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
