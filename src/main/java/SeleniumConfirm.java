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

public class SeleniumConfirm {
    public static void main(String[] args) throws IOException, InterruptedException {
        ProfilesIni profile = new ProfilesIni();
        FirefoxProfile myprofile = profile.getProfile("zen");
        FirefoxOptions options = new FirefoxOptions();
        options.setProfile(myprofile);
        options.setHeadless(true);
        WebDriver driver = null;
        driver = new FirefoxDriver(options);

        while (true) {
            driver.manage().window().maximize();
            driver.get("https://zen.yandex.ru/profile/editor/id/5e7a1dbc0aeed842018ab3f4");

            //publications-groups-view__focus-status publications-groups-view__focus-status_inactive
            WebDriverWait wait0 = new WebDriverWait(driver, 100);
            wait0.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='publications-groups-view__focus-status publications-groups-view__focus-status_inactive']"))
            );
            driver.findElement(By.xpath("//div[@class='publications-groups-view__focus-status publications-groups-view__focus-status_inactive']")).click();

            WebDriverWait wait1 = new WebDriverWait(driver, 100);
            wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'publication-card-item')][3]"))
            );

            if (driver.findElements(By.xpath("//div[contains(@class, 'publication-card-item')][3]//div[@class='card-cover-publication__background']")).size() > 0 && driver.findElements(By.xpath("//div[contains(@class, 'publication-card-item')][3]//div[@class='card-cover-publication__content']")).size()>0) {
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

                //publication-card-item publication-card-item_type_image publication-card-item_draft publication-card-item_content_article
                driver.findElement(By.xpath("//div[contains(@class, 'publication-card-item')][3]")).click();

                //Опубликовать
                try {
                    WebDriverWait wait2 = new WebDriverWait(driver, 20);
                    wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class='ui-lib-button _size_s _view-type_blue _is-transition-enabled _width-type_regular editor-header__edit-btn']/span[text() = 'Опубликовать']"))
                    );
                }
                catch (org.openqa.selenium.NoSuchElementException e) {
                    System.out.println("No element to post");
                    Thread.sleep(200000);
                    continue;
                }
                catch (org.openqa.selenium.TimeoutException e) {
                    System.out.println("No element to post");
                    Thread.sleep(200000);
                    continue;
                }

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
                    WebDriverWait wait3 = new WebDriverWait(driver, 10);
                    wait3.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@class='ui-lib-tag-input__input _is-empty']"))
                    );
                }
                catch (org.openqa.selenium.TimeoutException e) {
                    System.out.println("Timing out");
                    e.printStackTrace();
                    continue;
                }
                catch (org.openqa.selenium.NoSuchElementException e) {
                    System.out.println("No element");
                    e.printStackTrace();
                    continue;
                }

                driver.findElement(By.xpath("//input[@class='ui-lib-tag-input__input _is-empty']")).sendKeys("казань" + Keys.ENTER + "ислам" + Keys.ENTER + "православие" + Keys.ENTER + "русские" + Keys.ENTER + "русский язык" + Keys.ENTER + "россия" + Keys.ENTER + "ссср" + Keys.ENTER + "спорт" + Keys.ENTER + "мода и красота" + Keys.ENTER + "история россии" + Keys.ENTER);
                //ui-lib-tag-input__input _is-empty

//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

                //Настройки
                driver.findElement(By.xpath("//div[text() = 'Настройки']")).click();

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
                    WebDriverWait wait2345 = new WebDriverWait(driver, 100);
                    wait2345.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//img[@class='captcha__image']")));
                } catch (org.openqa.selenium.TimeoutException t) {
                    System.out.println("Missing captcha part");
//            List<WebElement> links = driver3.findElements(By.xpath("//div[@class='suggested-publications-cards-container']/*/div"))
                }


                if (!driver.findElements(By.xpath("//img[@class='captcha__image']")).isEmpty()) {
                    System.out.println("Capthcha handling");
                    //close-cross close-cross_black close-cross_size_s help-popup__close-cross
                    WebElement captcha = driver.findElement(By.xpath("//img[@class='captcha__image']"));
                    String imageUrl = captcha.getAttribute("src");
                    System.out.println(imageUrl);
                    BufferedImage img = ImageIO.read(new URL(imageUrl));
                    WebDriver part = driver;
                    final List<String> keyPressed = new ArrayList<>();
                    JFrame frame = new JFrame("FrameDemo");
                    ImageIcon icon = new ImageIcon(img);
                    JLabel label = new JLabel(icon);
                    JTextField textField = new JTextField(6);
                    Font font1 = new Font("SansSerif", Font.BOLD, 20);
                    textField.setFont(font1);
                    textField.setHorizontalAlignment(JTextField.CENTER);
                    textField.requestFocus();
                    textField.addKeyListener(
                            new KeyListener() {

                                public void keyTyped(KeyEvent e) {
                                }

                                @Override
                                public void keyPressed(KeyEvent e) {
                                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                        synchronized (keyPressed) {
                                            String captchaS = textField.getText();
                                            System.out.println("Logging capthca: " + captchaS);
                                            //Введите символы с картинки
                                            part.findElement(By.xpath("//input[@placeholder='Введите символы с картинки']")).sendKeys(captchaS);
                                            //ui-lib-button _size_l _view-type_blue _is-transition-enabled _width-type_regular
                                            part.findElement(By.xpath("//button[@class='ui-lib-button _size_l _view-type_blue _is-transition-enabled _width-type_regular']/span[text() = 'Опубликовать']")).click();
                                            System.out.println("Submitting");

                                            File outputfile = new File("D:\\captchas\\" + captchaS + ".jpg");
                                            try {
                                                ImageIO.write(img, "jpg", outputfile);
                                            } catch (IOException ex) {
                                                ex.printStackTrace();
                                            }
                                            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                                            //part.quit();
                                            keyPressed.add(captchaS);
                                            keyPressed.notify();
                                        }
                                    }
                                }

                                @Override
                                public void keyReleased(KeyEvent e) {

                                }
                            }
                    );
                    //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setLayout(new FlowLayout());
                    JPanel pane = new JPanel();
                    frame.add(pane);
                    pane.add(label, BorderLayout.CENTER);
                    pane.add(textField, BorderLayout.CENTER);
                    frame.pack();
                    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                    frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
                    frame.setVisible(true);
                    synchronized (keyPressed) {
                        //keyPressed.wait();
                        while (keyPressed.isEmpty())
                            keyPressed.wait();
                    }
                    ;
                    //driver.quit();
                } else {
                    //driver.quit();
                }
            }
        }
    }
}
