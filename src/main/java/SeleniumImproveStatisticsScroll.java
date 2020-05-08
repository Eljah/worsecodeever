import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SeleniumImproveStatisticsScroll {
    public static void main(String[] args) throws IOException {
        ProfilesIni profile = new ProfilesIni();
        FirefoxProfile myprofile = profile.getProfile("default");
        FirefoxOptions options = new FirefoxOptions();
        options.setProfile(myprofile);
        //options.setHeadless(true);
        WebDriver driver = null;
        driver = new FirefoxDriver(options);

        while (true) {
            driver.manage().window().maximize();
            driver.get("https://zen.yandex.ru/id/5e7a1dbc0aeed842018ab3f4");

            //publications-groups-view__focus-status publications-groups-view__focus-status_inactive
            WebDriverWait wait0 = new WebDriverWait(driver, 10000);
            wait0.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='card-image-view__clickable']"))
            );
            int articlesCount=driver.findElements(By.xpath("//a[@class='card-image-view__clickable']")).size();

            String originalHandle = driver.getWindowHandle();

            String selectLinkOpeninNewTab = Keys.chord(Keys.CONTROL,Keys.RETURN);
            driver.findElement(By.xpath("(//a[@class='card-image-view__clickable'])["+ ThreadLocalRandom.current().nextInt(0, articlesCount)+"]")).sendKeys(selectLinkOpeninNewTab);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(String handle : driver.getWindowHandles()) {
                if (!handle.equals(originalHandle)) {
                    driver.switchTo().window(handle);
                }
            };

            System.out.println("Start for scrolling");
            for (int i=0; i<20; i++) {
                System.out.println("Scrolling "+i);
                JavascriptExecutor js = ((JavascriptExecutor) driver);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                js.executeScript("window.scrollBy(0, document.body.scrollHeight / 20)");
            }

            for(String handle : driver.getWindowHandles()) {
                if (!handle.equals(originalHandle)) {
                    driver.switchTo().window(handle);
                    driver.close();
                }
            }

            driver.switchTo().window(originalHandle);
        }
    }
}
