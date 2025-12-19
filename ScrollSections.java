     package jolt;

import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

public class ScrollSections {
    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.jolt.film");
        driver.manage().window().maximize();
        Thread.sleep(1500);

        String[] sections = {"Who We Are", "Filmmakers", "Partners", "Support Our Films"};

        for (String section : sections) {
            WebElement sectionBtn = driver.findElement(By.xpath("//button[@title='" + section + "']"));
            sectionBtn.click();
            System.out.println("✅ Clicked on: " + section);
            Thread.sleep(1500);
            smoothScroll(driver);

            if (section.equals("Support Our Films")) {
                try {
                    WebDriverWait waitFaq = new WebDriverWait(driver, Duration.ofSeconds(10));
                    WebElement faqLink = waitFaq.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a[href='/faq']")));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", faqLink);
                    faqLink.click();
                    System.out.println("✅ Clicked on: FAQ");
                    Thread.sleep(1500);
                    smoothScroll(driver);

                    WebElement termsLink = driver.findElement(By.cssSelector("a[href='/terms']"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", termsLink);
                    termsLink.click();
                    System.out.println("✅ Clicked on: Terms & Conditions");
                    Thread.sleep(1500);
                    smoothScroll(driver);

                    WebElement privacyLink = driver.findElement(By.cssSelector("a[href='/privacy-policy']"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", privacyLink);
                    privacyLink.click();
                    System.out.println("✅ Clicked on: Privacy & Policy");
                    Thread.sleep(1500);
                    smoothScroll(driver);

                    WebElement sellLink = driver.findElement(By.cssSelector("a[href='/privacy-policy#doNotSell']"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", sellLink);
                    sellLink.click();
                    System.out.println("✅ Clicked on: Do not Sell or Share My Personal Information");
                    Thread.sleep(1500);
                    smoothScroll(driver);

                    WebElement watchLink = driver.findElement(By.cssSelector("a[href='/how-to-watch']"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", watchLink);
                    watchLink.click();
                    System.out.println("✅ Clicked on: How to Watch");
                    Thread.sleep(1500);
                    smoothScroll(driver);

                    driver.findElement(By.xpath("//button[normalize-space()='Cookies Settings']")).click();
                    Thread.sleep(1500);
                    driver.findElement(By.xpath("//button[@id='close-pc-btn-handler']")).click();
                    System.out.println("✅ Clicked on: Cookies Settings");

                    driver.findElement(By.xpath("//img[contains(@alt,'LOGO')]")).click();
                    Thread.sleep(1000);

                    driver.findElement(By.xpath("//button[@title='Films']")).click();
                    smoothScroll(driver);
                    System.out.println("✅ Clicked on: Films");

                } catch (Exception e) {
                    System.out.println("❌ FAQ or Terms button not found: " + e.getMessage());
                }
            }
        }
        
        // Close the browser after scrolling the Films section
        driver.quit();
        System.out.println("✅ Browser closed after Films section scroll.");
    }

    public static void smoothScroll(WebDriver driver) throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Long pageHeight = (Long) js.executeScript("return document.body.scrollHeight");
        for (int i = 0; i <= pageHeight; i += 50) {
            js.executeScript("window.scrollTo(0, " + i + ");");
            Thread.sleep(30);
        }
    }
}
