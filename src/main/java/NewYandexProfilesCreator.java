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
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewYandexProfilesCreator {
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
                driver.get("https://passport.yandex.ru/registration/mail");
                driver.findElement(By.xpath("//input[contains(@name, 'firstname')]")).sendKeys("Ильяс");
                driver.findElement(By.xpath("//input[contains(@name, 'lastname')]")).sendKeys("Валиев");
                driver.findElement(By.xpath("//input[contains(@name, 'login')]")).sendKeys("waliev.ilyas." + i);
                driver.findElement(By.xpath("//input[contains(@id, 'password')]")).sendKeys("Tatarstan1920");
                driver.findElement(By.xpath("//input[contains(@id, 'password_confirm')]")).sendKeys("Tatarstan1920");
                driver.findElement(By.xpath("//div[contains(@class, 'link_has-no-phone')]/span")).click();
                WebDriverWait wait234 = new WebDriverWait(driver, 10);
                wait234.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[contains(@id, 'hint_answer')]")));
                driver.findElement(By.xpath("//input[contains(@id, 'hint_answer')]")).sendKeys("Дядя Юра музыкант");
                //@id='captcha'
                //@class='captcha__image'

                WebDriverWait wait2345 = new WebDriverWait(driver, 20);
                wait2345.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//img[@class='captcha__image']")));

                String value = null;
                BufferedImage img2 = null;

                System.out.println("Captcha handling for i=" + i);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                WebElement captcha = driver.findElement(By.xpath("//img[@class='captcha__image']"));
                String imageUrl = captcha.getAttribute("src");
                System.out.println(imageUrl);
                try {
                    img2 = ImageIO.read(new URL(imageUrl));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File outputfile = new File("temp.jpg");
                try {
                    ImageIO.write(img2, "jpg", outputfile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                List<AnnotateImageRequest> requests = new ArrayList<>();

                ByteString imgBytes = null;
                try {
                    imgBytes = ByteString.readFrom(new FileInputStream(outputfile));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Image img = Image.newBuilder().setContent(imgBytes).build();
                Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
                AnnotateImageRequest request =
                        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
                requests.add(request);

                try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
                    BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
                    List<AnnotateImageResponse> responses = response.getResponsesList();

                    for (AnnotateImageResponse res : responses) {
                        if (res.hasError()) {
                            System.out.printf("Error: %s\n", res.getError().getMessage());
                            return;
                        }

                        // For full list of available annotations, see http://g.co/cloud/vision/docs
                        for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                            System.out.printf("Text: %s\n", annotation.getDescription());
                            //System.out.printf("Position : %s\n", annotation.getBoundingPoly());

                        }
                        value = res.getTextAnnotationsList().get(0).getDescription();
                        value = value.replace('\n',' ');
                        System.out.println(value);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                driver.findElement(By.xpath("//input[contains(@id, 'captcha')]")).sendKeys(value);
                driver.findElement(By.xpath("//button[contains(@type, 'submit')]")).click();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                driver.get("https://passport.yandex.ru/auth");
                WebDriverWait wait23 = new WebDriverWait(driver, 5);
                wait23.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@class, 'AuthAccountListItem-displayName')]")));

                driver.findElement(By.xpath("//*[contains(@class, 'AuthAccountListItem-displayName')]")).click();
//            driver.findElement(By.xpath("//input[contains(@name, 'login')]")).sendKeys("waliev.ilyas." + i);
//            driver.findElement(By.xpath("//button[contains(@type, 'submit')]")).click();
//            driver.findElement(By.xpath("//input[contains(@name, 'passwd')]")).sendKeys("Tatarstan1920");
//            driver.findElement(By.xpath("//button[contains(@type, 'submit')]")).click();
//            driver.findElement(By.xpath("//div[@data-t='phone_skip']/button")).click();

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
