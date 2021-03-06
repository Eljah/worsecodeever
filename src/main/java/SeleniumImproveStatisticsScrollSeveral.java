import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class SeleniumImproveStatisticsScrollSeveral {
    public static void main(String[] args) throws IOException {
        ProfilesIni profile = new ProfilesIni();
        //FirefoxProfile myprofile = profile.getProfile("default");
        FirefoxProfile myprofile = profile.getProfile("rnn"+ThreadLocalRandom.current().nextInt(5, 40));
        //FirefoxProfile myprofile = profile.getProfile("margarita");
        FirefoxOptions options = new FirefoxOptions();
        options.setProfile(myprofile);
        options.setHeadless(true);
        WebDriver driver = null;
        driver = new FirefoxDriver(options);

        driver.manage().window().maximize();
        driver.get("https://zen.yandex.ru/id/5e7a1dbc0aeed842018ab3f4");

        int count = 100;

        while (true) {
            try {

                count--;
                if (count == 0) {
                    count = 100;
                    ProfilesIni profile2 = new ProfilesIni();
                    //FirefoxProfile myprofile = profile.getProfile("default");
                    FirefoxProfile myprofile2 = profile2.getProfile("rnn"+ThreadLocalRandom.current().nextInt(1, 99 + 1));
                    //FirefoxProfile myprofile = profile.getProfile("margarita");
                    FirefoxOptions options2 = new FirefoxOptions();
                    options2.setProfile(myprofile2);
                    options.setHeadless(true);
                    driver.quit();
                    driver = null;
                    driver = new FirefoxDriver(options2);

                    driver.manage().window().maximize();

                    driver.get("https://zen.yandex.ru/id/5e7a1dbc0aeed842018ab3f4");
                }

                //publications-groups-view__focus-status publications-groups-view__focus-status_inactive
                WebDriverWait wait0 = new WebDriverWait(driver, 10000);
                wait0.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='card-image-view__clickable']"))
                );
                int articlesCount = driver.findElements(By.xpath("//a[@class='card-image-view__clickable']")).size();

                String originalHandle = driver.getWindowHandle();

                String selectLinkOpeninNewTab = Keys.chord(Keys.CONTROL, Keys.RETURN);
                driver.findElement(By.xpath("(//a[@class='card-image-view__clickable'])[" + ThreadLocalRandom.current().nextInt(0, articlesCount) + "]")).sendKeys(selectLinkOpeninNewTab);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (String handle : driver.getWindowHandles()) {
                    if (!handle.equals(originalHandle)) {
                        driver.switchTo().window(handle);
                    }
                }
                ;

                System.out.println("Start for scrolling");
                for (int i = 0; i < 21; i++) {
                    System.out.println("Scrolling " + i);
                    JavascriptExecutor js = ((JavascriptExecutor) driver);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    js.executeScript("window.scrollBy(0, document.body.scrollHeight / 20+"+ ThreadLocalRandom.current().nextInt(-20, 20)+")");
                }

                for (String handle : driver.getWindowHandles()) {
                    if (!handle.equals(originalHandle)) {
                        driver.switchTo().window(handle);
                        driver.close();
                    }
                }

                driver.switchTo().window(originalHandle);

                JavascriptExecutor js = ((JavascriptExecutor) driver);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                js.executeScript("window.scrollBy(0, document.body.scrollHeight)");
            }
            catch (Exception e) {
                try {
                    //driver.close();
                    driver.quit();
                }
                catch (Exception ex) {ex.printStackTrace();}
                e.printStackTrace();
                driver = new FirefoxDriver(options);
                driver.manage().window().maximize();
                driver.get("https://zen.yandex.ru/id/5e7a1dbc0aeed842018ab3f4");
            }
        }
    }
}
