import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SeleniumConfirmRNN {
    public static void main(String[] args) throws IOException {
        ProfilesIni profile = new ProfilesIni();
        FirefoxProfile myprofile = profile.getProfile("zen");
        FirefoxOptions options = new FirefoxOptions();
        options.setProfile(myprofile);
        //options.setHeadless(true);
        WebDriver driver = null;
        driver = new FirefoxDriver(options);
        Solver solver = new Solver();

        while (true) {
            try {
                driver.manage().window().maximize();
                driver.get("https://zen.yandex.ru/profile/editor/id/5e7a1dbc0aeed842018ab3f4");

                //publications-groups-view__focus-status publications-groups-view__focus-status_inactive
                WebDriverWait wait0 = new WebDriverWait(driver, 100);
                wait0.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='Черновики']"))
                );
                driver.findElement(By.xpath("//label[text()='Черновики']")).click();

                try {
                    WebDriverWait wait1 = new WebDriverWait(driver, 100);
                    wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'publication-card-item')][3]"))
                    );
                } catch (org.openqa.selenium.TimeoutException e) {
                    System.out.println("No 3rd card in panel");
                    Thread.sleep(200);
                    continue;
                }

                if (driver.findElements(By.xpath("//div[contains(@class, 'publication-card-item')][3]//div[@class='card-cover-publication__background']")).size() > 0 && driver.findElements(By.xpath("//div[contains(@class, 'publication-card-item')][3]//div[@class='card-cover-publication__content']")).size() > 0) {
                    //ui-lib-button _size_m _view-type_transparent _is-transition-enabled _width-type_regular zen-publishers-more-button zen-publishers-more-button_white card-cover-publication__dots-button

                    //card-cover-publication__content
                    System.out.println("Cleaning up imageless: " + driver.
                            findElement(By.xpath("//div[contains(@class, 'publication-card-item')][3]//div[@class='card-cover-publication__content']")).getText());

                    driver.findElement(By.xpath("//div[contains(@class, 'publication-card-item')][3]")).
                            findElement(By.xpath("//div[@class='card-cover-publication__dots-container']/button")).click();
                    driver.findElement(By.xpath("//div[contains(@class, 'publication-card-item')][3]")).
                            findElement(By.xpath("//button/span/span[text()='Удалить']")).click();
                    WebDriverWait wait12 = new WebDriverWait(driver, 100);
                    wait12.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class='control button2 button2_view_classic button2_size_m button2_theme_zen-clear-black']/span[text()='Да']"))
                    );
                    //control button2 button2_view_classic button2_size_m button2_theme_zen-clear-black
                    driver.findElement(By.xpath("//button[@class='control button2 button2_view_classic button2_size_m button2_theme_zen-clear-black' and contains(.//span, 'Да')]")).click();
                    System.out.println("Deleting imageless article");

                } else {
                    //card-cover-publication__background

                    if (driver.findElement(By.xpath("//div[contains(@class, 'publication-card-item')][3]//div[@class='card-cover-publication__content']")).getText().trim().equals(""))
                    {
                        System.out.println("Cleaning up duplicate: " + driver.
                                findElement(By.xpath("//div[contains(@class, 'publication-card-item')][3]//div[@class='card-cover-publication__content']")).getText());
                        driver.findElement(By.xpath("//div[contains(@class, 'publication-card-item')][3]//div[@class='card-cover-publication__dots-container']/button")).click();
                        driver.findElement(By.xpath("//button/span/span[text()='Удалить']")).click();
                        WebDriverWait wait12 = new WebDriverWait(driver, 100);
                        wait12.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class='ui-lib-button _size_l _view-type_red _is-transition-enabled _width-type_regular desktop-popup__button' and contains(.//span, 'Удалить')]"))
                        );
                        //control button2 button2_view_classic button2_size_m button2_theme_zen-clear-black
                        driver.findElement(By.xpath("//button[@class='ui-lib-button _size_l _view-type_red _is-transition-enabled _width-type_regular desktop-popup__button' and contains(.//span, 'Удалить')]")).click();
                        System.out.println("Deleting duplicatearticle");
                        continue;
                    }

                    //publication-card-item publication-card-item_type_image publication-card-item_draft publication-card-item_content_article
                    driver.findElement(By.xpath("//div[contains(@class, 'publication-card-item')][3]")).click();

                    //Опубликовать
                    try {
                        WebDriverWait wait2 = new WebDriverWait(driver, 20);
                        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class='ui-lib-button _size_s _view-type_blue _is-transition-enabled _width-type_regular editor-header__edit-btn']/span[text() = 'Опубликовать']"))
                        );
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        System.out.println("No element to post");
                        Thread.sleep(200);
                        continue;
                    } catch (org.openqa.selenium.TimeoutException e) {
                        System.out.println("No element to post");
                        Thread.sleep(200);
                        continue;
                    }

                    //ReactModal__Overlay ReactModal__Overlay--after-open help-popup__overlay
                    if (!driver.findElements(By.xpath("//div[@class='close-cross close-cross_black close-cross_size_s help-popup__close-cross']")).isEmpty()) {
                        //close-cross close-cross_black close-cross_size_s help-popup__close-cross
                        driver.findElement(By.xpath("//div[@class='close-cross close-cross_black close-cross_size_s help-popup__close-cross']")).click();
                    }

                    List<WebElement> checking = driver.findElements(By.xpath("//div[@class='ui-lib-popup-element__close']"));
                    //ui-lib-popup-element__close
                    if (!checking.isEmpty()) {
                        //close-cross close-cross_black close-cross_size_s help-popup__close-cross
                        checking.get(0).click();
                    }

                    WebElement submit = driver.findElement(By.xpath("//button[@class='ui-lib-button _size_s _view-type_blue _is-transition-enabled _width-type_regular editor-header__edit-btn']/span[text() = 'Опубликовать']"));
                    submit.click();

                    WebDriverWait wait34 = new WebDriverWait(driver, 20);
                    wait34.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class='ui-lib-button _size_l _view-type_yellow _is-transition-enabled _width-type_regular publication-settings-actions__action']/span[text() = 'Опубликовать']"))
                    );
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

                    try {
                        WebDriverWait wait3 = new WebDriverWait(driver, 20);
                        wait3.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@class='ui-lib-tag-input__input _is-empty']"))
                        );
                        driver.findElement(By.xpath("//input[@class='ui-lib-tag-input__input _is-empty']")).sendKeys("казань" + Keys.ENTER + "ислам" + Keys.ENTER + "православие" + Keys.ENTER + "спорт" + Keys.ENTER + "мода и красота" + Keys.ENTER);
                    } catch (org.openqa.selenium.TimeoutException e) {
                        System.out.println("Timing out");
                        e.printStackTrace();
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        System.out.println("No element");
                        e.printStackTrace();
                    }


                    //ui-lib-tag-input__input _is-empty

//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

                    //Настройки
                    driver.findElement(By.xpath("//*[text() = 'Настройки']")).click();

//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

                    driver.findElement(By.xpath("//label/span[text() = 'Отключить комментарии']/..//input[@type='checkbox']")).click();

                    WebDriverWait wait234 = new WebDriverWait(driver, 100);
                    wait234.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class='ui-lib-button _size_l _view-type_yellow _is-transition-enabled _width-type_regular publication-settings-actions__action']/span[text() = 'Опубликовать']"))
                    );
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

                    driver.findElement(By.xpath("//button[@class='ui-lib-button _size_l _view-type_yellow _is-transition-enabled _width-type_regular publication-settings-actions__action']/span[text() = 'Опубликовать']")).click();

                    System.out.println("Submitting");

                    try {
                        WebDriverWait wait2345 = new WebDriverWait(driver, 10);
                        wait2345.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//img[@class='captcha__image']")));
                    } catch (org.openqa.selenium.TimeoutException t) {
                        System.out.println("Missing captcha part");
//            List<WebElement> links = driver3.findElements(By.xpath("//div[@class='suggested-publications-cards-container']/*/div"))
                    }

                    String value = null;
                    BufferedImage img = null;
                    WebElement buttonSubmit=driver.findElement(By.xpath("//button[@class='ui-lib-button _size_l _view-type_blue _is-transition-enabled _width-type_regular']/span[text() = 'Опубликовать']"));

                    while (!driver.findElements(By.xpath("//img[@class='captcha__image']")).isEmpty()) {
                        System.out.println("Captcha handling");
                        WebElement captcha = driver.findElement(By.xpath("//img[@class='captcha__image']"));
                        String imageUrl = captcha.getAttribute("src");
                        System.out.println(imageUrl);
                        img = ImageIO.read(new URL(imageUrl));
                        File outputfile = new File("D:\\captchas\\rnn\\temp.jpg");
                        try {
                            ImageIO.write(img, "jpg", outputfile);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        img = ImageIO.read(new File("D:\\captchas\\rnn\\temp.jpg"));
                        value = solver.solve(img);
                        System.out.println("Resolved: " + value);
                        //close-cross close-cross_black close-cross_size_s help-popup__close-cross

                        //Введите символы с картинки
                        driver.findElement(By.xpath("//input[@placeholder='Введите символы с картинки']")).sendKeys(value);
                        //ui-lib-button _size_l _view-type_blue _is-transition-enabled _width-type_regular
                        buttonSubmit.click();
                        System.out.println("Submitting");

                        try {
                            WebDriverWait wait2345 = new WebDriverWait(driver, 10);
                            wait2345.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='ui-lib-input _size_m _is-error captcha__input-block']")));
                            System.out.println("Wrong captcha inserted");
                        } catch (org.openqa.selenium.TimeoutException t) {
                            System.out.println("No error for captcha detected");
//            List<WebElement> links = driver3.findElements(By.xpath("//div[@class='suggested-publications-cards-container']/*/div"))
                        }
                    }

                    if (img != null) {
                        File outputfile = new File("D:\\captchas\\rnn\\" + value + ".jpg");
                        try {
                            ImageIO.write(img, "jpg", outputfile);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    ;
                    //driver.quit();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                driver.quit();
                driver = new FirefoxDriver(options);
            }
            catch (Exception e) {
                e.printStackTrace();
                driver.quit();
                driver = new FirefoxDriver(options);
            }
        }
    }
}
