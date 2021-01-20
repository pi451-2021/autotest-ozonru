package ru.ozon;

import io.qameta.allure.*;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OzonAutoTests {
    private Properties props;
    private ChromeDriver driver;
    private final int minPrice = 10000;
    private final int maxPrice = 11000;

    @Before
    public void Setup() {
        try {
            props = new Properties();
            driver = new ChromeDriver();
            props.load(new FileInputStream("config.ini"));
            System.setProperty(props.getProperty("webdriverName"),
                    props.getProperty("webdriverPath"));
        } catch (Exception ex) {
            fail("Error: " + ex.getMessage());
        }
    }

    @After
    public void Close() {
        props.clear();
        driver.quit();
    }

    @Test
    @Description("First test")
    public void Test1() {
        MainPage();
        Catalog();
        Appliances();
        KitchenAppliances();
        CoffeeMaker();
        PriceRange();
        SortAsc();
        AddToBasket();
        Basket();
    }

    @Test
    @Description("Second test")
    public void Test2() {
        MainPage();
        Catalog();
        Appliances();
        KitchenAppliances();
        CoffeeMaker();
        CarobCoffeeMaker();
        HeatedCups();
        PriceRange();
        SortAsc();
        AddToBasket();
        Basket();
    }

    @Test
    @Description("Third test")
    public void Test3() {
        MainPage();
        Catalog();
        Appliances();
        KitchenAppliances();
        CoffeeMaker();
        PriceRange();
        SortAsc();
        Favorites();
    }

    /* Открытие главной страницы */
    @Step("Go to the main page of Ozon.ru")
    private void MainPage() {
        try {
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            driver.get(props.getProperty("urlSite"));
            timeOut();
            driver.getKeyboard().pressKey(Keys.ESCAPE); // Закрытие модального окна
            timeOut();
            createScreenshot("OzonMain");
        }
        catch (Exception ex) {
            fail("Error: " + ex.getMessage());
        }
    }

    /* Открытие каталога */
    @Step("Go to Catalog.")
    private void Catalog() {
        try {
            clickXpath(props.getProperty("xpCatalog"));
            timeOut();
            createScreenshot("OzonCatalog");
        }
        catch (Exception ex) {
            fail("Error: " + ex.getMessage());
        }
    }

    /* Раздел Бытовая техника */
    @Step("Go to Appliances.")
    private void Appliances() {
        try {
            clickXpath(props.getProperty("xpAppliances"));
            timeOut();
            createScreenshot("OzonAppliances");
        } catch (Exception ex) {
            fail("Error: " + ex.getMessage());
        }
    }

    /* Раздел Техника для кухни */
    @Step("Go to Kitchen Appliances.")
    private void KitchenAppliances() {
        try {
            clickXpath(props.getProperty("xpKitchApl"));
            timeOut();
            createScreenshot("OzonKitchenAppliances");
        } catch (Exception ex) {
            fail("Error: " + ex.getMessage());
        }

    }

    /* Раздел Кофеварки и кофемашины */
    @Step("Go to Coffee Makers and coffee machines.")
    private void CoffeeMaker() {
        try {
            clickXpath(props.getProperty("xpCoffeeMaker"));
            timeOut();
            createScreenshot("OzonCoffeeMaker");
        } catch (Exception ex) {
            fail("Error: " + ex.getMessage());
        }
    }

    /* Выбор типа кофеварки - рожковая */
    @Step("Select carob coffee maker.")
    private void CarobCoffeeMaker() {
        try {
            clickXpath(props.getProperty("xpCarobCoffee"));
            timeOut();
            createScreenshot("CarobCoffeeMaker");
        } catch (Exception ex) {
            fail("Error: " + ex.getMessage());
        }
    }

    /* Выбор метода приготовления - подогрев чашек */
    @Step("Select heated cups.")
    private void HeatedCups() {
        try {
            clickXpath(props.getProperty("xpHeatedCups"));
            timeOut();
            driver.getKeyboard().pressKey(Keys.PAGE_UP);
            createScreenshot("HeatedCups");
        } catch (Exception ex) {
            fail("Error: " + ex.getMessage());
        }
    }

    /* Определение диапазона цен */
    @Step("Select Price Range from 10000 to 11000 rubles. " +
            "Check for the prices of goods are correct.")
    private void PriceRange() {
        try {
            String minStr = Integer.toString(minPrice);
            String maxStr = Integer.toString(maxPrice);
            doubleClickXpath(props.getProperty("xpRangeLeft"));
            driver.getKeyboard().sendKeys(minStr);
            driver.getKeyboard().pressKey(Keys.ENTER);
            timeOut();
            doubleClickXpath(props.getProperty("xpRangeRight"));
            driver.getKeyboard().sendKeys(maxStr);
            driver.getKeyboard().pressKey(Keys.ENTER);
            timeOut();
            createScreenshot("OzonPriceRange");
            timeOut();
            WebElement div = findElementByClassWait(20, "b7t2");
            List<WebElement> tegAs = div.findElements(By.tagName("a"));
            int max = 1;
            for (WebElement a : tegAs) {
                if (isNumeric(a.getText())) {
                    int tmp = Integer.parseInt(a.getText());
                    if (tmp > max) max = tmp;
                }
            }
            timeOut();
            this.checkEveryProductOnPage();
            for (int i = 2; i <= max; i++) {
                clickXpath(props.getProperty("xpInputPage"));
                driver.getKeyboard().sendKeys(Integer.toString(i));
                driver.getKeyboard().pressKey(Keys.ENTER);
                timeOut();
                this.checkEveryProductOnPage();
                if (i == max) {
                    div = findElementByClassWait(20, "b7t2");
                    tegAs = div.findElements(By.tagName("a"));
                    for (WebElement a : tegAs)
                        if (isNumeric(a.getText())) {
                            int tmp = Integer.parseInt(a.getText());
                            if (tmp > max) max = tmp;
                        }
                }
            }
            timeOut();
            if (max > 1) {
                driver.navigate().back();
                clickXpath(props.getProperty("xpInputPage"));
                driver.getKeyboard().sendKeys("1");
                driver.getKeyboard().pressKey(Keys.ENTER);
                timeOut();
            }
        } catch (Exception ex) {
            fail("Error: " + ex.getMessage());
        }

    }

    /* Сортировка цены по возрастанию */
    @Step("Select sort in ascending order of price.")
    private void SortAsc() {
        try {
            // Открытие выпадающего списка
            clickXpath(props.getProperty("xpDropDownList"));
            timeOut();
            driver.getKeyboard().sendKeys(Keys.DOWN, Keys.DOWN);
            timeOut();
            driver.getKeyboard().pressKey(Keys.ENTER);
            timeOut();
            createScreenshot("OzonSortAsc");
            timeOut();
        } catch (Exception ex) {
            fail("Error: " + ex.getMessage());
        }
    }

    /* Добавление в корзину */
    @Step("Add first good to the Basket.")
    private void AddToBasket() {
        try {
            clickXpath(props.getProperty("xpAddBasket"));
            timeOut();
            createScreenshot("OzonAddBasket");
        } catch (Exception ex) {
            fail("Error: " + ex.getMessage());
        }
    }

    /* Переход в корзину */
    @Step("Go to the Basket. Increase the amount of goods to 3. " +
            "Check that increase price is correct.")
    private void Basket() {
        try {
            clickXpath(props.getProperty("xpBasket"));
            timeOut();
            createScreenshot("OzonMyBasket");

            // Получение цены из корзины
            String price1 = findElementByXpathWait(20,
                    props.getProperty("xpPriceBasket")).getText();
            // Увеличиваем количество до 3-ех
            clickXpath(props.getProperty("xpDropDownCount"));
            driver.getKeyboard().sendKeys(Keys.DOWN, Keys.DOWN);
            driver.getKeyboard().sendKeys(Keys.ENTER);
            timeOut();
            createScreenshot("OzonFinalScreen");
            // Сравнение цен
            String price2 = findElementByXpathWait(20,
                    props.getProperty("xpSumBasket")).getText();
            // Выдать ошибку при несоответствии цены
            if (buildInt(price1) * 3 != buildInt(price2))
                fail("Кол-во товаров меньше 3-ех, либо цена некорректна.");
        } catch (Exception ex) {
            fail("Error: " + ex.getMessage());
        }
    }


    /* Добавление товара в избранное */
    @Step("Add good with a minimal price to favorites. Check that a " +
            "good was been added to favorites and price is correct.")
    private void Favorites() {
        try {
            timeOut();
            clickXpath(props.getProperty("xpAddFavorites"));
            timeOut();
            createScreenshot("AddToFavorites");
            timeOut();
            // Сохранение цены и имени товара
            int[] pricesBefore = getChosenProductPricesListOnPage();
            timeOut();
            String productName = findElementByXpathWait(20,
                    props.getProperty("xpFavoritesPrice")).getText();
            timeOut();
            // Переход в раздел избранное
            clickXpath(props.getProperty("xpFavorites"));
            timeOut();
            createScreenshot("Favorites");
            timeOut();
            int[] pricesAfter = getChosenProductPricesListOnPage();
            // Проверка наличия и цены товара в избранном
            String element = driver.findElementByLinkText(productName).getText();
            if (element.isEmpty()) {
                fail("Товар не добавился в избранное");
            }
            if (pricesAfter[0] != pricesBefore[0] ||
                    pricesAfter[1] != pricesBefore[1])
                fail("Цена изменилась");
        } catch (Exception ex) {
            fail("Error: " + ex.getMessage());
        }
    }

    /* Вспомогательные функции */
    private int getMinPrice(List<WebElement> prices)
    {
        List<String> pricesStr = new ArrayList<>();
        for (WebElement p : prices)
            pricesStr.add(p.getText());
        if (prices.size() > 1)
            return Math.min(buildInt(pricesStr.get(0)),
                    buildInt(pricesStr.get(1)));
        else return buildInt(pricesStr.get(0));
    }

    private void checkEveryProductOnPage() {
        StringBuilder sb = new StringBuilder();
        List<WebElement> priceElements =
                findElementsByClassWait(20, "b5v4");
        for (WebElement element : priceElements) {
            List<WebElement> prices = element.findElements(By.tagName("span"));
            int min = getMinPrice(prices);
            if (min > maxPrice || min < minPrice) {
                sb.append("\n");
                sb.append(min);
            }
        }
        if (sb.length() > 0)
            fail("Цены продуктов не соответствует условиям выборки\n"
                    + sb.toString());
    }

    private int[] getChosenProductPricesListOnPage() {
        WebElement priceElements = findElementByClassWait(30,"b5v4");
        //парсинг цены
        //в массив могут попасть 2 цены в итемах со скидкой (цена со скидкой и цена  до скидки)
        List<WebElement> pricesEl =  priceElements.findElements(By.tagName("span"));
        int[] res = new int[2];
        res[0] = buildInt(pricesEl.get(0).getText());
        if (pricesEl.size() > 1)
            res[1] = buildInt(pricesEl.get(1).getText());
        return res;
    }

    private void createScreenshot(String name)
    {
        Allure.addAttachment(name,
                new ByteArrayInputStream(((TakesScreenshot) driver)
                        .getScreenshotAs(OutputType.BYTES)));
    }

    private void clickXpath(String path)
    {
        (new WebDriverWait(driver, 20))
                .until(ExpectedConditions
                        .presenceOfElementLocated(By.xpath(path))).click();
    }
    private void doubleClickXpath(String path)
    {
        Actions actions = new Actions(driver);
        WebElement element =
                (new WebDriverWait(driver, 20))
                        .until(ExpectedConditions
                                .presenceOfElementLocated(By.xpath(path)));
        actions.doubleClick(element).perform();
    }

    private WebElement findElementByXpathWait(int timeout, String xpath)
    {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        return wait.until(ExpectedConditions
                .presenceOfElementLocated(By.xpath(xpath)));
    }

    private WebElement findElementByClassWait(int timeout, String name)
    {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        return wait.until(ExpectedConditions
                .presenceOfElementLocated(By.className(name)));
    }

    private List<WebElement> findElementsByClassWait(int timeout, String name)
    {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        return wait.until(ExpectedConditions
                .presenceOfAllElementsLocatedBy(By.className(name)));
    }

    private void timeOut()
    {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    private int buildInt(String str)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++)
            if (Character.isDigit(str.charAt(i)))
                sb.append(str.charAt(i));
        return Integer.parseInt(sb.toString());
    }
}
// *********************************************************************** //