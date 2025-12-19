package jolt;

import java.time.Duration;
import java.util.Scanner;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

public class Logout {
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please Enter Email Address : ");
        String un = sc.next();
        System.out.println("Please Enter Password : ");
        String pw = sc.next();

        WebDriver driver = new ChromeDriver();
        driver.get("https://www.jolt.film");
        driver.manage().window().maximize();
        Thread.sleep(1000);

        // Close cookie popup if present
        try {
            WebElement closeButton = driver.findElement(
                By.cssSelector(".onetrust-close-btn-handler.banner-close-button.ot-close-icon"));
            closeButton.click();
            System.out.println("✅ Cookie popup closed.");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("✅ Cookie popup not found or already closed.");
        }

        // Click Login button
        try {
            WebElement loginBtn = driver.findElement(By.cssSelector("button[aria-label='Log IN']"));
            loginBtn.click();
            System.out.println("✅ Log IN button Clicked.");
        } catch (Exception e) {
            System.out.println("Login button not found: " + e.getMessage());
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Enter email and click Continue
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
            usernameField.sendKeys(un);

            WebElement continueBtn = driver.findElement(By.cssSelector("button._button-login-id"));
            continueBtn.click();
            System.out.println("✅ Click on Continue after Email.");

            // ✅ Email validation using if-else instead of try-catch
            java.util.List<WebElement> emailErrors = driver.findElements(By.xpath("//div[@id='error-cs-email-invalid']"));
            if (!emailErrors.isEmpty()) {
                String validationMsg = emailErrors.get(0).getText();
                System.out.println("⚠️ Validation Message: " + validationMsg);
                driver.quit();
                sc.close();
                return;
            } 
            else {
            }

            // Continue to password only if no validation message shown
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
            passwordField.sendKeys(pw);            

            WebElement loginPasswordBtn = driver.findElement(By.cssSelector("button._button-login-password"));
            loginPasswordBtn.click();
            System.out.println("✅ Clicked on Continue after Password.");
            
            // ✅ Wait briefly for possible validation messages
            Thread.sleep(1000);

            // ---- Check: User does not exist ----
            try {
                WebElement userNotExistMsg = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//p[@class='cc6c34cee c0dad7210']")));
                System.out.println("⚠️ Validation Message: " + userNotExistMsg.getText());
                driver.quit();
                sc.close();
                return;
            } catch (TimeoutException e) {
                
            }

            // ---- Check: Password error ----
            try {
                WebElement passwordError = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//span[@id='error-element-password']")));
                System.out.println("⚠️ Validation Message: " + passwordError.getText());
                driver.quit();
                sc.close();
                return;
            } catch (TimeoutException e) {
               
            }
            
            // "continue without passkey"
            try {
            	WebElement continueWithoutPasskey = wait.until(
            		    ExpectedConditions.visibilityOfElementLocated(
            		        By.cssSelector("button[value='abort-passkey-enrollment']")));
            		continueWithoutPasskey.click();
            		System.out.println("✅ Clicked on Continue without passkey.");
            		Thread.sleep(500);
            } catch (Exception e) {
                
            }
            System.out.println("✅ Login Successful!");
        } catch (Exception e) {
            System.out.println("⚠️ Error during login: " + e.getMessage());
        }
        // ----------- Click on Profile  -----------
        try {
            WebElement profileBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='toggler']")));
            profileBtn.click();
            System.out.println("✅ Profile button clicked.");
        } catch (Exception e) {
         
        }
        Thread.sleep(1000);
        
        // ----------- Click on Logout in Profile  -----------
        try {
            WebElement Logout = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Log Out']")));
            Logout.click();
            System.out.println("✅ Logout in Profile clicked.");
        } catch (Exception e) {
         
        }
        Thread.sleep(1000);
        
        // ----------- Click on Logout Button  -----------
        try {
            WebElement LogoutBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@aria-label,'LOG OUT')]")));
            LogoutBtn.click();
            System.out.println("✅ Logout Button clicked.");
        } catch (Exception e) {
         
        }
        driver.quit();
        }
    
    }