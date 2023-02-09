import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import java.util.Set;

public class test_task {
    public static void main(String[] args) {
        Script();
    }

    public static void Script() {
        // Set the driver               [for win: System.setProperty("webdriver.chrome.driver","C:/Users/USER/Documents/soft/chromedriver.exe");]
        // Set the driver               [for lnx: System.setProperty("webdriver.chrome.driver","/home/ruine/soft/drivers_4_selen/chromedriver_linux64/chromedriver");]
        System.setProperty("webdriver.chrome.driver","C:/Users/USER/Documents/soft/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        WebElement elementToGo, awaited;
        // Open the website
        driver.manage().window().maximize();
        driver.get("https://ya.ru");
        // Look for market.yandex via cssSelector (since it is a static component)
        driver.findElement(By.cssSelector("body > main > div.services-pinned.services-pinned_more_yes.i-bem.services-pinned_js_inited > div > a")).click();
        // Open market.yandex via xpath (since it is a dynamic component)
        awaited = (new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='popup2__content-wrapper']//a[contains(@href,'market.yandex.ru')]"))));
        awaited.click();
        // Switch the tab
        switchTabs(driver, true);
        // Wait thirty seconds to load elements of the page (or to enter the captcha), then open the catalogue
        awaited = (new WebDriverWait(driver, Duration.ofSeconds(30)).until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@id='catalogPopupButton']"))));
        awaited.click();
        // In a catalogue, hover the electronics element and go to smartphones side-element...
        try {
            awaited = (new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("#catalogPopup > div > div > div > div > div > div > div:nth-child(1) > div > ul > li:nth-child(4) > a > span"))));
            Actions actions = new Actions(driver);
            actions.moveToElement(awaited).perform();
            driver.findElement(By.xpath("//a[text()='Смартфоны']")).click();
            // ...then select all filters...
            driver.findElement(By.xpath("//a[@data-auto='allFiltersButton']")).click();
        } catch (NoSuchElementException exc) {
            // If the hovering is failed, go to electronics...
            awaited = (new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("#catalogPopup > div > div > div > div > div > div > div:nth-child(1) > div > ul > li:nth-child(4) > a > span"))));
            awaited.click();
            // ...then select the smartphone menu...
            awaited = (new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@href,'smartfony')]"))));
            awaited.click();
            // ...then select smartphones...
            awaited = (new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@href,'/catalog--smartfony')]"))));
            awaited.click();
            // ...then select all filters...
            driver.findElement(By.xpath("//a[@data-auto='allFiltersButton']")).click();
        }
        // ...then set the maximum price of 20000...
        elementToGo = driver.findElement(By.xpath("//input[@data-auto='range-filter-input-max']"));
        elementToGo.click();
        elementToGo.sendKeys("20000");
        // ...then set the minimum diagonal of 3; due to the non-clickable nature of the element, JSexecutor is called
        elementToGo = driver.findElement(By.xpath("//h4[contains(text(),'Диагональ экрана (точно),')]"));
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("arguments[0].click();", elementToGo);
        driver.findElement(By.xpath("(//input[@data-auto='range-filter-input-min'])[2]")).sendKeys("3");
        // setting the checkboxes of smartphones' producers
        List<WebElement> checkboxes = driver.findElements(By.xpath("//div[@data-filter-id='7893318']//div[@class='_1WMsA _1VtMI _176_6']"));
        int i = 1;
        for (WebElement chkBox : checkboxes) {
            chkBox.click();
            i++;
            if (i==6) break;
        }
        // clicking the show results button
        driver.findElement(By.xpath("//div[@class='_1Y664 _3ECo0']//a[contains(text(),'Показать')]")).click();
        // load the page until full in order to select all elements
        pageFullLoad(executor);
        // setting the titles of products into the list
        List<WebElement> elements = driver.findElements(By.xpath("//div[@data-test-id='virtuoso-item-list']//div[@data-index]//a[@title]"));
        if (elements.isEmpty()) {
            // in case the titles are not appeared on the page, the titles of images are taken
            if (!(driver.findElements(By.xpath("//div[@data-index='0']//div[text()='Популярные предложения']")).isEmpty()))
                // in case the 'popular suggestions' panel appeared on the page, elements are counted starting with the first index
                elements = driver.findElements(By.xpath("//div[@data-test-id='virtuoso-item-list']//div[@data-index>0]//img[@title]"));
            else elements = driver.findElements(By.xpath("//div[@data-test-id='virtuoso-item-list']//div[@data-index]//img[@title]"));
        }
        String firstElement;
        // storing the first, most popular element on the page
        firstElement = (elements.get(0).getAttribute("title"));
        // show how many elements are shown on the page
        if (elements.size() == 10) System.out.println("На странице 10 элементов."); else System.out.println("На странице не 10 элементов, a " + elements.size());
        // change the sorting; selecting with CSS, since the element is static
        driver.findElement(By.cssSelector("#serpTop > div > div > div:nth-child(1) > div > div > noindex > div > button._23p69._3D8AA.cia-cs")).click();
        // locating the stored element, scrolling down the page till the element is found
        try {
            pageFullLoad(executor);
            driver.findElement(By.xpath("//img[@title='" + firstElement + "']")).click();
            // Switch the tab
            switchTabs(driver,false);
            // locating and printing the rating
            String numericRating = (driver.findElement(By.xpath("//span[@data-auto='rating-badge-value']")).getAttribute("textContent"));
            System.out.println("Оценка товара '" + firstElement + "' равна: " + numericRating);
            // close the browser
            driver.quit();
        } catch (NoSuchElementException | StaleElementReferenceException exc) {
            System.out.println("Самого популярного товара по выбранным фильтрам (" + firstElement + ") нет среди " + elements.size() + " самых дешёвых.");
        }
    }

    public static void switchTabs(WebDriver driver, boolean toClose) {
        String window1 = driver.getWindowHandle();
        Set<String> currentWindows = driver.getWindowHandles();
        String window2 = null;
        for (String window : currentWindows) {
            if (!window.equals(window1)) {
                window2 = window;
                break;
            }
        }
        if (toClose) driver.close();
        driver.switchTo().window(window2);
    }

    public static void pageFullLoad(JavascriptExecutor executor) {
        try {
            long lastHeight = (long) (executor).executeScript("return document.body.scrollHeight");
            while (true) {
                (executor).executeScript("window.scrollTo(0, document.body.scrollHeight);");
                Thread.sleep(1000);
                long newHeight = (long) (executor).executeScript("return document.body.scrollHeight");
                if (newHeight == lastHeight) {
                    break;
                }
                lastHeight = newHeight;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
