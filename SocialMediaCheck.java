package jolt;

import java.time.Duration;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class SocialMediaCheck {

    public static void main(String[] args) throws InterruptedException {

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.manage().window().maximize();
        driver.get("https://www.jolt.film");

        // ---------- Close cookie popup if present ----------
        try {
            WebElement closeButton = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.cssSelector(".onetrust-close-btn-handler.banner-close-button.ot-close-icon")));
            closeButton.click();
            System.out.println("✅ Cookie popup closed.");
        } catch (Exception e) {
            System.out.println("ℹ️ Cookie popup not present.");
        }
        Thread.sleep(1000);
        
        // ---------- Scroll to Social Site section ----------
        WebElement socialSection = driver.findElement(By.xpath("//div[@class='social_site']"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", socialSection);
        System.out.println("✅ Scrolled to Social Site section");
        Thread.sleep(1000);

        // Save parent window
        String parentWindow = driver.getWindowHandle();

        // ================= INSTAGRAM =================
        WebElement instagramIcon = driver.findElement(
                By.xpath("//a[@aria-label=\"Visit Jolt Film's Instagram page\"]"));
        instagramIcon.click();
        System.out.println("✅ Clicked Instagram icon");

        Thread.sleep(3000);
        closeChildTab(driver, parentWindow, "instagram");
        Thread.sleep(3000);
        
        // ================= FACEBOOK =================
        WebElement facebookIcon = driver.findElement(
                By.xpath("//a[@aria-label=\"Visit Jolt Film's Facebook page\"]"));
        facebookIcon.click();
        System.out.println("✅ Clicked Facebook icon");

        Thread.sleep(3000);
        closeChildTab(driver, parentWindow, "facebook");

        // ================= TIKTOK =================
        WebElement TiktokIcon = driver.findElement(
                By.xpath("//a[@aria-label=\"Visit Jolt Film's TikTok page\"]"));
        TiktokIcon.click();
        System.out.println("✅ Clicked Tiktok icon");

        Thread.sleep(3000);
        closeChildTab(driver, parentWindow, "tiktok");
        Thread.sleep(3000);

        // ================= TWITTER =================
        WebElement TwitterIcon = driver.findElement(
                By.xpath("//a[@aria-label=\"Visit Jolt Film's Twitter page\"]"));
        TwitterIcon.click();
        System.out.println("✅ Clicked Twitter icon");

        Thread.sleep(3000);
        closeChildTab(driver, parentWindow, "x");
        Thread.sleep(3000);
        
        // ================= YOUTUBE =================
        WebElement YoutubeIcon = driver.findElement(
                By.xpath("//a[@aria-label=\"Visit Jolt Film's YouTube page\"]"));
        YoutubeIcon.click();
        System.out.println("✅ Clicked Youtube icon");

        Thread.sleep(3000);
        closeChildTab(driver, parentWindow, "youtube");
        Thread.sleep(3000);
        
        
         driver.quit(); 
    }

    // ---------- Reusable method to close social media tabs ----------
    public static void closeChildTab(WebDriver driver, String parentWindow, String siteName) {

        Set<String> allWindows = driver.getWindowHandles();

        for (String window : allWindows) {
            if (!window.equals(parentWindow)) {
                driver.switchTo().window(window);

                if (driver.getTitle().toLowerCase().contains(siteName)) {
                    System.out.println("🔴 Closing " + siteName + " tab: " + driver.getTitle());
                    driver.close();
                    break;
                }
            }
        }

        // 🔑 Switch back to main window
        driver.switchTo().window(parentWindow);
        System.out.println("🔙 Switched back to jolt.film");
    }
}
