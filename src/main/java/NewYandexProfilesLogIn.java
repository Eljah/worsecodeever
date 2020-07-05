import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewYandexProfilesLogIn {
    public static void main(String[] args) {
        int end = 100;
        int start = 60;
        for (int i = start; i <= end; i++) {
            System.out.println("Processing for i=" + i);
            ProfilesIni profile = new ProfilesIni();
            FirefoxProfile myprofile = profile.getProfile("rnn" + i);
            FirefoxOptions options = new FirefoxOptions();
            options.setProfile(myprofile);
            options.setHeadless(true);
            WebDriver driver = null;
            try {
                driver = new FirefoxDriver(options);
                driver.get("https://passport.yandex.ru/auth");
                WebDriverWait wait23 = new WebDriverWait(driver, 5);
                wait23.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@class, 'AuthAccountListItem-displayName')]")));

                driver.findElement(By.xpath("//*[contains(@class, 'AuthAccountListItem-displayName')]")).click();
                driver.findElement(By.xpath("//input[contains(@name, 'login')]")).sendKeys("waliev.ilyas." + i);
                driver.findElement(By.xpath("//button[contains(@type, 'submit')]")).click();
                driver.findElement(By.xpath("//input[contains(@name, 'passwd')]")).sendKeys("Tatarstan1920");
                driver.findElement(By.xpath("//button[contains(@type, 'submit')]")).click();

                Thread.sleep(1000);


            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error for i=" + i);
                i--;

            } finally {
                driver.quit();
            }
            //
        }
    }
}
