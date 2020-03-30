import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeleniumCopyPaste {
    public static void main(String[] args) throws IOException {
        ProfilesIni profile = new ProfilesIni();
        FirefoxProfile myprofile = profile.getProfile("zen");
        FirefoxOptions options = new FirefoxOptions();
        options.setProfile(myprofile);
        WebDriver driver = null;
        WebDriver driver2 = null;
        WebDriver driver3 = null;
        while (true) {
            try {
                Set<String> fount = new HashSet<String>();
                Set<String> processed = new HashSet<String>();
                try (FileReader f = new FileReader("fount")) {
                    StringBuffer sb = new StringBuffer();
                    while (f.ready()) {
                        char c = (char) f.read();
                        if (c == '\n') {
                            fount.add(sb.toString());
                            sb = new StringBuffer();
                        } else {
                            sb.append(c);
                        }
                    }
                    if (sb.length() > 0) {
                        fount.add(sb.toString());
                    }
                }
                try (FileReader f = new FileReader("processed")) {
                    StringBuffer sb = new StringBuffer();
                    while (f.ready()) {
                        char c = (char) f.read();
                        if (c == '\n') {
                            processed.add(sb.toString());
                            sb = new StringBuffer();
                        } else {
                            sb.append(c);
                        }
                    }
                    if (sb.length() > 0) {
                        processed.add(sb.toString());
                    }
                }
                String foundURL = null;
                for (String founts : fount) {
                    System.out.println("Fount before: " + founts + ", " + founts.length());
                }
                ;
                for (String processeds : processed) {
                    System.out.println("Processed: " + processeds + ", " + processeds.length());
                }
                ;
                for (String founts : fount) {
                    boolean flag = true;
                    for (String processeds : processed) {
                        if (founts.equals(processeds)) {
                            flag = false;
                        }
                    }
                    ;
                    if (flag) {
                        foundURL = founts;
                        break;
                    }
                }
                ;
                System.out.println("Finally selected: " + foundURL);
                if (foundURL.equals(null)) {
                    break;
                }
                driver = new FirefoxDriver(options);
                driver.manage().window().maximize();
                driver.get("https://zen.yandex.ru/profile/editor/id/5e7a1dbc0aeed842018ab3f4");

                //new-publication-dropdown__add-button
                driver.findElement(By.className("new-publication-dropdown__add-button")).click();
                //new-publication-dropdown__button-text
                driver.findElement(By.className("new-publication-dropdown__button-text")).click();

                //Open a new tab using Ctrl + t
                //driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"t");
//        driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"t");
//        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
//        driver.switchTo().window(tabs.get(0));
////Switch between tabs using Ctrl + \t
                //driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"\t");

                driver2 = new FirefoxDriver(options);
                driver2.manage().window().maximize();
                driver2.get("https://translate.google.ru/#view=home&op=translate&sl=ru&tl=tt");

                driver3 = new FirefoxDriver(options);
                driver3.manage().window().maximize();
                driver3.get(foundURL);

                //suggested-publications-content-card__title-container
                //links
                //((JavascriptExecutor) driver3)
                //        .executeScript("window.scrollTo(0, document.body.scrollHeight)");
                // socials__feedback
                WebElement ele = driver3.findElement(By.className("socials__feedback"));
                ((JavascriptExecutor) driver3).executeScript("arguments[0].scrollIntoView();", ele);
                try {
                    WebDriverWait wait22 = new WebDriverWait(driver3, 30);
                    wait22.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='suggested-publications-layout article-interest-suggested-publications']"))
                    );
                }
                catch (org.openqa.selenium.TimeoutException t) {
                    System.out.println("Missing suggested part");
//            List<WebElement> links = driver3.findElements(By.xpath("//div[@class='suggested-publications-cards-container']/*/div"))
                };
//            for (WebElement link : links) {
//                String url = link.findElement(By.tagName("a")).getAttribute("href");
//                url = url.substring(0, url.lastIndexOf("?"));
//                fount.add(url);
//                System.out.println("New fount url same publisher: " + url);
//            }

                List<WebElement> links2 = driver3.findElements(By.xpath("//div[@class='suggested-publications-cards-container']/div/div/a[1]"));
                for (WebElement link : links2) {
                    String url = link.getAttribute("href");
                    url = url.substring(0, url.lastIndexOf("?"));
                    if (url.contains("zen.yandex.ru/media/")) {
                        fount.add(url);
                        System.out.println("New fount url2 interesting: " + url);
                    } else {
                        System.out.println("Throwing out: " + url);
                    }
                }
                for (String founts : fount) {
                    System.out.println("Fount after: " + founts);
                }
                ;
                BufferedWriter writer = new BufferedWriter(new FileWriter("fount", false));
                for (String fountS2 : fount) {
                    writer.write(fountS2 + "\n");
                }
                writer.close();

                //article__title
                String title = driver3.findElement(By.className("article__title")).getText();
                //source
                driver2.findElement(By.id("source")).sendKeys(title);
                //Actions action2 = new Actions(driver2);
                //action2.keyDown(Keys.CONTROL).sendKeys("v").keyUp(Keys.CONTROL).perform();
                //jfk-button-img
                WebDriverWait wait = new WebDriverWait(driver2, 100);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='tlid-results-container results-container']"))
                );
//        driver2.findElement(By.className("jfk-button-img")).click();
                //DraftEditor-editorContainer
                String titleTrans = driver2.findElement(By.className("tlid-results-container")).getText();
                System.out.println(titleTrans);
                driver.findElement(By.className("DraftEditor-editorContainer")).click();
                driver.switchTo().activeElement().sendKeys(titleTrans);
//        Actions action = new Actions(driver);
//        action.keyDown(Keys.CONTROL).sendKeys("v").keyUp(Keys.CONTROL).perform();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //текст
                driver.findElement(By.xpath("//div[@class='zen-editor-block zen-editor-block-paragraph']")).click();
                //article-render
                List<WebElement> paragraphs = driver3.findElements(By.xpath("//div[@class='article-render']/*"));
                for (WebElement a : paragraphs) {
                    if (a.getTagName().equals("p") & a.getText().trim().length() > 0) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Translating: " + a.getText());
                        driver2.findElement(By.id("source")).clear();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        driver2.findElement(By.id("source")).sendKeys(a.getText());
                        WebDriverWait wait3 = new WebDriverWait(driver2, 100);
                        wait3.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='tlid-results-container results-container']"))
                        );
                        String titleTrans2 = driver2.findElement(By.className("tlid-results-container")).getText();
                        System.out.println(titleTrans2);
                        driver.switchTo().activeElement().sendKeys(titleTrans2);
                        driver.switchTo().activeElement().sendKeys(Keys.ENTER);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (a.getTagName().equals("figure")) {
                        WebElement image = a.findElement(By.tagName("img"));
                        String imageUrl = image.getAttribute("src");
                        System.out.println(imageUrl);
                        //image.click();
                        //image.sendKeys(Keys.CONTROL+ "c");
//                WebDriver driver4 = new FirefoxDriver(options);
//                driver4.get(imageUrl);
//                driver4.findElement(By.tagName("img")).sendKeys(Keys.CONTROL+ "c");

                        ImageSelection imgSel = null;
                        try {
                            imgSel = new ImageSelection(ImageIO.read(new URL(imageUrl)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);

                        Actions action2 = new Actions(driver);
                        action2.keyDown(Keys.SHIFT).sendKeys(Keys.INSERT).keyUp(Keys.SHIFT).perform();
                        driver.switchTo().activeElement().sendKeys(Keys.ENTER);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                driver.switchTo().activeElement().sendKeys(Keys.ENTER);
                driver.switchTo().activeElement().sendKeys(Keys.ENTER);
                driver.switchTo().activeElement().sendKeys(foundURL);
                FileWriter fw = new FileWriter("processed", true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(foundURL + "\n");
                bw.close();

                //Опубликовать
                WebDriverWait wait2 = new WebDriverWait(driver, 100);
                wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button/span[text() = 'Опубликовать']"))
                );
                //

//        String newWindow = driver.getWindowHandle();
//
//        driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL+"t");
//        driver.get("Second URL");
//
//
////Perform whatever actions you want done on the secondary tab, let’s pretend we’re logging in somewhere
//
//        driver.findElement(By.id("ID")).sendKeys("username");
//        driver.findElement(By.id("ID")).sendKeys("password");
//        driver.findElement(By.id("submit")).click();
//
//        //driver.switchTo().window(mainWindow);
//
////Perform whatever actions you want done on the main tab, we’ll click a button
//        driver.findElement(By.id("button")).click();
//
//// Close current tab
//        //driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + 'w');

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            catch (Exception ex) {
                System.err.println(ex.toString());
            }
            finally {


                driver.close();
                driver2.close();
                driver3.close();
            }
        }
    }

}
