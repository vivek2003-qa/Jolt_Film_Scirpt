package jolt;

import java.time.Duration;
import java.util.Scanner;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

public class Delete_UserAccount {
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);

        // ----------- Sign In First -----------
        System.out.print("Please Enter Email Address: ");
        String un = sc.next();
        System.out.print("Please Enter Password: ");
        String pw = sc.next();
        sc.nextLine(); // consume newline

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();
        driver.get("https://www.jolt.film");

        // Close cookies popup if found
        try {
            WebElement closeButton = driver.findElement(By.cssSelector(".onetrust-close-btn-handler.banner-close-button.ot-close-icon"));
            closeButton.click();
            System.out.println("✅ Cookie popup closed.");
        } catch (NoSuchElementException ignored) {
        }

        // Click Login button
        try {
            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[aria-label='Log IN']")));
            loginBtn.click();
        } catch (Exception e) {
            System.out.println("⚠️ Login button not found: " + e.getMessage());
        }

        // Enter username and password
        try {
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
            usernameField.sendKeys(un);
            driver.findElement(By.cssSelector("button._button-login-id")).click();

            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
            passwordField.sendKeys(pw);
            driver.findElement(By.cssSelector("button._button-login-password")).click();

            try {
                WebElement continueWithoutPasskey = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[value='abort-passkey-enrollment']")));
                continueWithoutPasskey.click();
            } catch (TimeoutException ignored) {
            }

            System.out.println("✅ Login successful.");
        } catch (Exception e) {
            System.out.println("❌ Error during login: " + e.getMessage());

            
        }
     // ✅ Added: Click on profile button by XPath
        try {
            WebElement profileBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='toggler']")));
            profileBtn.click();
            System.out.println("✅ Profile button clicked.");
        } catch (Exception ex) {
            System.out.println("⚠️ Profile button not found: " + ex.getMessage());
        }
    
        //My Profile button click
        Thread.sleep(1000);
        driver.findElement(By.xpath("//button[normalize-space()='My Profile']")).click();
        
        
        //Click on FAQ hyperlink
        Thread.sleep(1000);
        driver.findElement(By.xpath("//a[contains(@class,'barlow_regular text-[1.6rem] leading-normal text-[#aaa] max-ml:text-[1.4rem] relative faq_link')]")).click();
        
        //click on delete account hyperlink
        Thread.sleep(1000);
        driver.findElement(By.xpath("//a[normalize-space()='click here to delete your account']")).click();
        
        // Wait for the dropdown to be clickable
        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Click on the dropdown
        WebElement dropdown = wait1.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'react-select__control')]")
        ));
        dropdown.click();
        System.out.println("✅ Dropdown clicked.");

        // Wait for visual confirmation (dropdown visible)
        Thread.sleep(1500); // 1.5 seconds pause so dropdown opens fully

        // Wait and select the specific option
        WebElement option = wait1.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'react-select__option') and text()='I finished watching the film(s) I signed up for']")
        ));

       

        // Pause a bit before clicking (so you see it highlighted)
        Thread.sleep(1000);

        option.click();
        System.out.println("✅ Option selected: I finished watching the film(s) I signed up for");

        // Short delay after selection (for visual confirmation)
        Thread.sleep(1000);

        //agree Checkbox
        Thread.sleep(1000);
        driver.findElement(By.xpath("//input[@name='conformation']")).click();
        
        //click on Delete button
        Thread.sleep(1000);
        driver.findElement(By.xpath("//div[@class='form_group w_100 del_acc__btn mt-[1.5rem]']")).click();
        
        //click on logout button
        Thread.sleep(1000);
        driver.findElement(By.xpath("//button[@aria-label='LOG OUT']")).click();
        
        System.out.println("✅ Delete Account Successfully!!");
        
        Thread.sleep(2500);
        driver.quit();
    }
    
}

