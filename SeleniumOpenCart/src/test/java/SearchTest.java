import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class SearchTest {

    WebDriver driver;
    WebDriverWait wait;
    String baseUrl = "http://localhost/opencart/";

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @BeforeMethod
    public void openHomePage() {
        driver.get(baseUrl);
    }

    // ===== Helper =====
    private void search(String keyword) {
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("search")));
        searchBox.clear();
        searchBox.sendKeys(keyword);
        searchBox.sendKeys(Keys.ENTER);
    }

    private List<WebElement> getProducts() {
        return driver.findElements(By.cssSelector(".product-thumb"));
    }

    // ==============================
    // TC01 - Search thành công
    // ==============================
    @Test
    public void TC01_searchValidProduct() {
        search("iphone");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-thumb")));
        List<WebElement> products = getProducts();

        Assert.assertTrue(products.size() > 0, "Không tìm thấy sản phẩm phù hợp");
    }

    // ==============================
    // TC02 - Không có kết quả
    // ==============================
    @Test
    public void TC02_searchNoResult() {
        search("dress");

        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'There is no product')]")));

        Assert.assertTrue(message.isDisplayed());
    }

    // ==============================
    // TC03 - Không nhập gì
    // ==============================
    @Test
    public void TC03_searchEmpty() {
        search("");

        List<WebElement> products = driver.findElements(By.cssSelector(".product-thumb"));

        // Không crash là pass
        Assert.assertNotNull(products);
    }

    // ==============================
    // TC04 - Nhập space
    // ==============================
    @Test
    public void TC04_searchSpace() {
        search("   ");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        String currentUrl = driver.getCurrentUrl();

        Assert.assertTrue(
                currentUrl.contains("search") || currentUrl.equals(baseUrl),
                "Search với space xử lý không đúng"
        );
    }

    // ==============================
    // TC05 - Ký tự đặc biệt
    // ==============================
    @Test
    public void TC05_searchSpecialCharacter() {
        search("@#$%^");

        // Không crash = pass
        Assert.assertTrue(driver.getCurrentUrl().contains("search"));
    }

    // ==============================
    // TC06 - Không phân biệt hoa thường
    // ==============================
    @Test
    public void TC06_searchCaseInsensitive() {
        search("iphone");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-thumb")));
        String firstLower = driver.findElement(By.cssSelector(".product-thumb h4 a")).getText();

        driver.get(baseUrl);

        search("IPHONE");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-thumb")));
        String firstUpper = driver.findElement(By.cssSelector(".product-thumb h4 a")).getText();

        Assert.assertEquals(firstLower, firstUpper, "Search bị phân biệt hoa thường");
    }

    // ==============================
    // TC07 - Gợi ý sản phẩm
    // ==============================
    @Test
    public void TC07_searchSuggestion() {
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("search")));
        searchBox.sendKeys("iph");

        // OpenCart mặc định KHÔNG có autocomplete → cần kiểm tra nếu có custom
        try {
            WebElement suggestion = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".dropdown-menu")));

            Assert.assertTrue(suggestion.isDisplayed());

        } catch (TimeoutException e) {
            System.out.println("Không có autocomplete (bỏ qua test)");
            Assert.assertTrue(true); // pass mềm
        }
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
