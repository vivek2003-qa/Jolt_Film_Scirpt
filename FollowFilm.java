package jolt;

import java.time.Duration;
import java.util.Scanner;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

public class FollowFilm {
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);

        // ----------- User Input -----------
        System.out.print("Please Enter Email Address: ");
        String un = sc.next();
        System.out.print("Please Enter Password: ");
        String pw = sc.next();
        sc.nextLine(); // consume newline
        System.out.print("Enter the movie name: ");
        String movieName = sc.nextLine();

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        driver.manage().window().maximize();
        driver.get("https://www.jolt.film");

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

        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Enter email and click Continue
            WebElement usernameField = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
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
            WebElement passwordField = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
            passwordField.sendKeys(pw);            

            WebElement loginPasswordBtn = driver.findElement(By.cssSelector("button._button-login-password"));
            loginPasswordBtn.click();
            System.out.println("✅ Clicked on Continue after Password.");
            
            // ✅ Wait briefly for possible validation messages
            Thread.sleep(1000);

            // ---- Check: User does not exist ----
            try {
                WebElement userNotExistMsg = wait1.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//p[@class='cc6c34cee c0dad7210']")));
                System.out.println("⚠️ Validation Message: " + userNotExistMsg.getText());
                driver.quit();
                sc.close();
                return;
            } catch (TimeoutException e) {
                
            }

            // ---- Check: Password error ----
            try {
                WebElement passwordError = wait1.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//span[@id='error-element-password']")));
                System.out.println("⚠️ Validation Message: " + passwordError.getText());
                driver.quit();
                sc.close();
                return;
            } catch (TimeoutException e) {
               
            }
            
            // "continue without passkey"
            try {
            	WebElement continueWithoutPasskey = wait1.until(
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

        // --- Movie Selection (fixed) ---
        try {
            String movieLower = movieName.toLowerCase();
            String xpathLiteral;
            if (!movieLower.contains("'")) {
                xpathLiteral = "'" + movieLower + "'";
            } else if (!movieLower.contains("\"")) {
                xpathLiteral = "\"" + movieLower + "\"";
            } else {
                String[] parts = movieLower.split("'");
                StringBuilder sb = new StringBuilder("concat(");
                for (int i = 0; i < parts.length; i++) {
                    if (i > 0) sb.append(", \"'\", ");
                    sb.append("'").append(parts[i]).append("'");
                }
                sb.append(")");
                xpathLiteral = sb.toString();
            }

            String movieXpath =
            	    "//div[contains(@class,'movie_card')]//*[self::img or self::h3 or self::p or self::span]" +
            	    "[contains(translate(@alt, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), " + xpathLiteral + ")" +
            	    " or contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), " + xpathLiteral + ")]";


            java.util.List<WebElement> matches = driver.findElements(By.xpath(movieXpath));

            if (matches.isEmpty()) {
                System.out.println("❌ Movie not found: " + movieName);
            } else {
                WebElement target = matches.get(0);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", target);
                Thread.sleep(800);

                try {
                    target.click();
                    System.out.println("✅ Clicked on movie : " + movieName);
                    
                } catch (Exception e1) {
                    try {
                        WebElement ancestor = target.findElement(By.xpath("./ancestor::a[1]"));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", ancestor);
                        Thread.sleep(400);
                        ancestor.click();
                        System.out.println("✅ Clicked ancestor link for movie: " + movieName);
                    } catch (Exception e2) {
                        ((JavascriptExecutor) driver).executeScript(
                            "var evt = new MouseEvent('click', {bubbles:true, cancelable:true, view:window}); arguments[0].dispatchEvent(evt);",
                            target
                        );
                        System.out.println("✅ Clicked movie via JS event dispatch (fallback).");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Movie selection error: " + e.getMessage());
        }
        Thread.sleep(1000);

     // ---- Follow button state check ----
        try {
            // Check if follow button (not selected) is present
            java.util.List<WebElement> alreadyFollowed = driver.findElements(
                By.xpath("//button[@class='d_flex items-center gap-[1rem] max-ml:hidden follow_btn']//div[@class='cursor_pointer btn_icon unfollow_icon']")
            );

            if (!alreadyFollowed.isEmpty()) {
                System.out.println("🎬 Film already followed.");
            } else {
                // If not followed, click the follow button
                WebElement followBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@class='d_flex items-center gap-[1rem] max-ml:hidden follow_btn unfllow !cursor-pointer']"))
                );

                followBtn.click();
                System.out.println("✅ Clicked Follow button.");
                Thread.sleep(2000);

                // ---- confirmation popup check ----
                try {
                    WebElement popupMsg = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(text(), 'You’re now following this film.')]")));
                    System.out.println("✅ Popup Message: " + popupMsg.getText());
                } catch (TimeoutException e) {
                   
                }

                
            }

        } catch (Exception e) {
            System.out.println("❌ Error checking follow state: " + e.getMessage());
        }

        
        driver.quit();
        sc.close();
    }
}
