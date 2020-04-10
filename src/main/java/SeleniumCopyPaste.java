import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
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
import java.security.Key;
import java.sql.DriverManager;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SeleniumCopyPaste {
    public static void main(String[] args) throws IOException {
        ProfilesIni profile = new ProfilesIni();
        FirefoxProfile myprofile = profile.getProfile("zen");
        FirefoxOptions options = new FirefoxOptions();
        options.setProfile(myprofile);

        FirefoxProfile myprofileD = profile.getProfile("default");
        FirefoxOptions optionsD = new FirefoxOptions();
        optionsD.setProfile(myprofileD);

        WebDriver driver = null;
        WebDriver driver2 = null;
        WebDriver driver3 = null;

        driver3 = new FirefoxDriver(optionsD);
        driver2 = new FirefoxDriver(options);
        driver = new FirefoxDriver(options);
        while (true) {
            try {
                Set<String> fount = new LinkedHashSet<>();
                Set<String> processed = new LinkedHashSet<>();
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
                        fount.add(sb.toString().trim());
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
                        processed.add(sb.toString().trim());
                    }
                }
                String foundURL = null;
                for (String founts : fount) {
                    //System.out.println("Fount before: " + founts + ", " + founts.length());
                }
                ;
                for (String processeds : processed) {
                    // System.out.println("Processed: " + processeds + ", " + processeds.length());
                }
                ;
                internal:
                for (String founts : fount) {
                    boolean flag = true;
                    for (String processeds : processed) {
//                        if (founts.contains("https://zen.yandex.ru/media/id/592d9ca9d7d0a6f37915034d/3-shaga-k-pravilnomu-karkasniku-za-nedorogo-silami-brigady-v-2020-godu-5e86b98996544a69b3c54c2e")) {
//                            //System.out.println("Fount in fount");
//                        };
//                        if (processeds.contains("https://zen.yandex.ru/media/id/592d9ca9d7d0a6f37915034d/3-shaga-k-pravilnomu-karkasniku-za-nedorogo-silami-brigady-v-2020-godu-5e86b98996544a69b3c54c2e")) {
//                            //System.out.println("Fount in processeds");
//                        }
                        if (founts.trim().equals(processeds.trim())) {
                            //System.out.println("Flag set to false!");
                            //System.out.println("Fount "+founts);
                            //System.out.println("Processed "+processeds);
                            flag = false;
                        }
                    }
                    ;
                    if (flag) {
                        foundURL = founts;
                        break internal;
                    }
                }
                ;
                System.out.println("Finally selected: " + foundURL);
                if (foundURL.equals(null)) {
                    break;
                }

                try {
                    driver.manage().window().maximize();
                    driver.get("https://zen.yandex.ru/profile/editor/id/5e7a1dbc0aeed842018ab3f4");
                    Thread.sleep(10000);
                }
                catch (org.openqa.selenium.WebDriverException e) {
                    Thread.sleep(1000000);
                    continue;
//                    driver.quit();
//                    driver = new FirefoxDriver(options);
//                    driver.manage().window().maximize();
//                    driver.get("https://zen.yandex.ru/profile/editor/id/5e7a1dbc0aeed842018ab3f4");
                }
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

                driver2.manage().window().maximize();
                driver2.get("https://translate.google.ru/#view=home&op=translate&sl=ru&tl=tt");

                driver3.manage().window().maximize();
                driver3.get(foundURL);

                //suggested-publications-content-card__title-container
                //links
                //((JavascriptExecutor) driver3)
                //        .executeScript("window.scrollTo(0, document.body.scrollHeight)");
                // socials__feedback
                //driver3.manage().timeouts().implicitlyWait(150, TimeUnit.SECONDS);

                //sticky-container
                if (!driver.findElements(By.className("sticky-container")).isEmpty()) {
                    WebElement ele = driver3.findElement(By.className("socials__feedback"));
                    ((JavascriptExecutor) driver3).executeScript("arguments[0].scrollIntoView();", ele);
                    try {
                        WebDriverWait wait22 = new WebDriverWait(driver3, 150);
                        wait22.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='suggested-publications-layout article-interest-suggested-publications']"))
                        );
                    } catch (org.openqa.selenium.TimeoutException t) {
                        System.out.println("Missing suggested part");
//            List<WebElement> links = driver3.findElements(By.xpath("//div[@class='suggested-publications-cards-container']/*/div"))
                    }
                    ;
                }
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
                        //System.out.println("Throwing out: " + url);
                    }
                }
                for (String founts : fount) {
                    //System.out.println("Fount after: " + founts);
                }
                ;
                BufferedWriter writer = new BufferedWriter(new FileWriter("fount", false));
                for (String fountS2 : fount) {
                    writer.write(fountS2 + "\n");
                }
                writer.close();

                WebDriverWait wait = new WebDriverWait(driver2, 100);

                //article__title
                try {
                    String title = driver3.findElement(By.className("article__title")).getText();
                    driver2.findElement(By.id("source")).sendKeys(title);
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='tlid-results-container results-container']"))
                    );
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (NoSuchElementException ex1) {
                    System.err.println(ex1.toString());
                    FileWriter fw = new FileWriter("processed", true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(foundURL + "\n");
                    bw.close();
                }

                //source
                //Actions action2 = new Actions(driver2);
                //action2.keyDown(Keys.CONTROL).sendKeys("v").keyUp(Keys.CONTROL).perform();
                //jfk-button-img

//        driver2.findElement(By.className("jfk-button-img")).click();
                //DraftEditor-editorContainer

                //ReactModal__Overlay ReactModal__Overlay--after-open help-popup__overlay
                if (!driver.findElements(By.xpath("//div[@class='close-cross close-cross_black close-cross_size_s help-popup__close-cross']")).isEmpty()) {
                    //close-cross close-cross_black close-cross_size_s help-popup__close-cross
                    driver.findElement(By.xpath("//div[@class='close-cross close-cross_black close-cross_size_s help-popup__close-cross']")).click();
                }

                //ui-lib-popup-element__close
                if (!driver.findElements(By.xpath("//div[@class='ui-lib-popup-element__close']")).isEmpty()) {
                    //close-cross close-cross_black close-cross_size_s help-popup__close-cross
                    driver.findElement(By.xpath("//div[@class='ui-lib-popup-element__close']")).click();
                }

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
                        String titleTrans2 = "";
                        try {
                            WebDriverWait wait3 = new WebDriverWait(driver2, 100);
                            wait3.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='tlid-results-container results-container']"))
                            );
                            titleTrans2 = driver2.findElement(By.className("tlid-results-container")).getText();
                            System.out.println(titleTrans2);
                        }
                        catch (org.openqa.selenium.TimeoutException e)
                        {
                            System.out.println("Copy from translate problems");
                        }

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
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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

                // the element containing the text
                //driver.findElement(By.xpath("//*[contains(text(), '"+foundURL+"')]")).click();
// assuming driver is a well behaving WebDriver
//                Actions actions = new Actions(driver);
//// and some variation of this:
////                actions.moveToElement(element, 10, 5)
////                        .clickAndHold()
////                        .moveByOffset(30, 0)
////                        .release()
////                        .perform();
//                int count = 3;
//
//                while(count>0){
//                    actions.click(element).perform();
//                    count -= 1;
//                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Actions action = new Actions(driver);
                action.keyDown(Keys.SHIFT).sendKeys(Keys.HOME).keyUp(Keys.SHIFT).perform();
                //WebDriverWait wait4 = new WebDriverWait(driver2, 150);
                //wait4.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='editor-toolbar__link-tools']")));
                try {
                    driver.findElement(By.xpath("//div[@class='editor-toolbar__link-tools']")).click();
                    //ui-lib-input__control
                    //WebDriverWait wait5 = new WebDriverWait(driver2, 150);
                    //wait5.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@class='ui-lib-input__control']']"))
                    //);
                    driver.switchTo().activeElement().sendKeys(foundURL + Keys.ENTER);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (NoSuchElementException ex1) {
                    System.err.println(ex1.toString());
                    FileWriter fw = new FileWriter("processed", true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(foundURL + "\n");
                    bw.close();
                }
                //Опубликовать
                WebDriverWait wait2 = new WebDriverWait(driver, 100);
                wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class='ui-lib-button _size_s _view-type_blue _is-transition-enabled _width-type_regular editor-header__edit-btn']/span[text() = 'Опубликовать']"))
                );
                WebElement submit = driver.findElement(By.xpath("//button[@class='ui-lib-button _size_s _view-type_blue _is-transition-enabled _width-type_regular editor-header__edit-btn']/span[text() = 'Опубликовать']"));
                submit.click();
//                JavascriptExecutor ex=(JavascriptExecutor)driver;
//                ex.executeScript("arguments[0].click()", submit);
//
//                System.out.println("Published clicked");
//                try {
//                    Thread.sleep(1500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                JavascriptExecutor ex1=(JavascriptExecutor)driver;
//                ex1.executeScript("arguments[0].click()", submit);
//
//                System.out.println("Publishing...");
//                WebDriverWait wait3 = new WebDriverWait(driver, 100);
//                wait3.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@class='ui-lib-tag-input__input _is-empty']"))
//                );
//
//                driver.findElement(By.xpath("//input[@class='ui-lib-tag-input__input _is-empty']")).sendKeys("казань"+Keys.ENTER+"ислам"+Keys.ENTER+"православие"+Keys.ENTER+"русские"+Keys.ENTER+"русский язык"+Keys.ENTER+"россия"+Keys.ENTER+"ссср"+Keys.ENTER+"спорт"+Keys.ENTER+"мода и красота"+Keys.ENTER+"история россии"+Keys.ENTER);
//                //ui-lib-tag-input__input _is-empty
//
//                try {
//                    Thread.sleep(1500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                //Настройки
//                driver.findElement(By.xpath("//div[text() = 'Настройки']")).click();
//
//                try {
//                    Thread.sleep(1500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                driver.findElement(By.xpath("//label/span[text() = 'Отключить комментарии']/..//input[@type='checkbox']")).click();
//
//                WebDriverWait wait34 = new WebDriverWait(driver, 100);
//                wait34.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class='ui-lib-button _size_l _view-type_yellow _is-transition-enabled _width-type_regular publication-settings-actions__action']/span[text() = 'Опубликовать']"))
//                );
//                try {
//                    Thread.sleep(1500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                //Закрыть
//                driver.findElement(By.xpath("//button/span[text() = 'Закрыть']")).click();
//
//                try {
//                    Thread.sleep(1500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                FileWriter fw = new FileWriter("processed", true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(foundURL + "\n");
                bw.close();

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

            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println(ex.toString());
            } finally {

                //driver.quit();
                //driver2.quit();
                //driver3.quit();
            }
        }
    }

}
