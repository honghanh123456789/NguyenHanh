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


    public void safeClick(WebElement element) {
        try {
            
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);

            
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();

        } catch (Exception e) {

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


        safeClick(product);

        WebElement nameDetail = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//h1")));

        Assert.assertEquals(nameDetail.getText(), nameHome);


        WebElement price = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//ul[@class='list-unstyled']//h2")));
        Assert.assertTrue(price.isDisplayed());


        WebElement addToCart = driver.findElement(By.id("button-cart"));
        Assert.assertTrue(addToCart.isDisplayed());




        WebElement descriptionTab = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//a[contains(@href,'#tab-description')]")));

        safeClick(descriptionTab);


        WebElement descriptionContent = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.id("tab-description")));


        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                descriptionContent
        );


        Assert.assertTrue(descriptionContent.getText().length() > 0);

        System.out.println("Description: " + descriptionContent.getText());

    }

    
    //  TC 2: Đã login

    @Test(priority = 2)
    public void viewProductDetailWithLogin() {

        driver.get(baseUrl);


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

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//form[contains(@action,'login')]//button[@type='submit']")
        )).click();


        try {

            WebElement homeBtn = wait.until(ExpectedConditions
                    .elementToBeClickable(By.xpath("//ul[@class='breadcrumb']//a")));
            safeClick(homeBtn);

        } catch (Exception e) {

            driver.get(baseUrl + "index.php?route=common/home&language=en-gb");
        }


        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("route=common/home"),
                ExpectedConditions.urlContains("opencart/")
        ));


        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("search")));


        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,700)");



        WebElement product = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("(//h4/a)[1]")));

        String nameHome = product.getText();

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                product
        );

        safeClick(product);


        WebElement nameDetail = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//h1")));

        Assert.assertEquals(nameDetail.getText(), nameHome);


        Assert.assertTrue(wait.until(ExpectedConditions
                        .visibilityOfElementLocated(By.xpath("//ul[@class='list-unstyled']//h2")))
                .isDisplayed());


        Assert.assertTrue(wait.until(ExpectedConditions
                        .visibilityOfElementLocated(By.id("button-cart")))
                .isDisplayed());


        WebElement descriptionTab = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//a[contains(@href,'#tab-description')]")));

        safeClick(descriptionTab);


        WebElement descriptionContent = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.id("tab-description")));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                descriptionContent
        );

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
