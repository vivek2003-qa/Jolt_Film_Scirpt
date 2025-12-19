package jolt;

import java.time.Duration;
import java.util.List;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CartItemRemove {

    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);

        // ----------- Sign In First -----------
        System.out.print("Please Enter Email Address: ");
        String un = sc.next();
        System.out.print("Please Enter Password: ");
        String pw = sc.next();
        sc.nextLine(); // consume newline
        System.out.print("Enter the movie name: ");
        String movieName = sc.nextLine();
        

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
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
                        //Thread.sleep(400);
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
       

        // Click on Rent Now Button
        try {
            WebElement rentNowButton = driver.findElement(By.xpath(
                    "//div[contains(@class,'watch_films_btns')]//button[@aria-label='Rent Now']"));
            rentNowButton.click();
            System.out.println("✅ 'Rent Now' button clicked.");
        } catch (Exception e) {
        }

        Thread.sleep(1000);

        // Locate amount
        WebElement amountElement = driver.findElement(By.cssSelector("span.d_amount"));
        String amountText = amountElement.getText().replace("$", "").trim();
        double currentAmount = Double.parseDouble(amountText);

        System.out.println("Current Film Amount: $" + currentAmount);

        // Locate plus & minus buttons
        List<WebElement> buttons = driver.findElements(By.cssSelector("button.plus_minus"));
        WebElement minusButton = buttons.get(0);
        WebElement plusButton = buttons.get(1);

        // -------- Minimize browser --------
        driver.manage().window().minimize();

        // Ask user input
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter amount you want to pay: ");
        double userAmount = scanner.nextDouble();

        // -------- Maximize browser --------
        driver.manage().window().maximize();

     // ================= MIN PRICE CHECK ONLY WHEN REQUIRED =================
        double dynamicMinPrice = currentAmount;

        if (userAmount < currentAmount) {

            // Move down only until userAmount OR minimum reached
            while (isMinusClickable(minusButton)) {

                String currentText = driver.findElement(By.cssSelector("span.d_amount"))
                        .getText().replace("$", "").trim();
                double liveAmount = Double.parseDouble(currentText);

                if (liveAmount <= userAmount) {
                    break; // STOP at required amount
                }

                minusButton.click();
                sleep(300);

                dynamicMinPrice = Double.parseDouble(
                        driver.findElement(By.cssSelector("span.d_amount"))
                                .getText().replace("$", "").trim());
            }

            System.out.println("Minimum Price is: $" + dynamicMinPrice);

            if (userAmount < dynamicMinPrice) {
                System.out.println("⚠️ You Cannot go below Minimum Price: $" + dynamicMinPrice);
                driver.quit();
                return;
            }
        }

     // 🔥 UPDATE currentAmount after min-price logic
        currentAmount = Double.parseDouble(
                driver.findElement(By.cssSelector("span.d_amount"))
                        .getText().replace("$", "").trim());


        double difference = userAmount - currentAmount;

        // Increase amount
        if (difference > 0) {
            int clicks = (int) difference;
            System.out.println("Clicking PLUS " + clicks + " times");

            for (int i = 0; i < clicks; i++) {
                plusButton.click();
                sleep(300);
            }
        }

        // Decrease amount
        if (difference < 0) {
            int clicks = (int) Math.abs(difference);
            System.out.println("Clicking MINUS " + clicks + " times");

            for (int i = 0; i < clicks; i++) {
                if (!isMinusClickable(minusButton)) {
                    System.out.println("❌ Minus button disabled.");
                    break;
                }
                minusButton.click();
                sleep(300);
            }
        }

        // Final amount verification
        String finalAmountText = driver.findElement(By.cssSelector("span.d_amount"))
                .getText().replace("$", "");
        System.out.println("Final Selected Amount: $" + finalAmountText);

       

     // -------- ADD TO CART --------
     try {
         WebElement addToCartButton = wait.until(
             ExpectedConditions.elementToBeClickable(
                 By.xpath("//button[normalize-space()='ADD TO CART']")
             )
         );
         addToCartButton.click();
         System.out.println("✅ 'Add to Cart' button clicked.");
     } catch (Exception e) {
         System.out.println("❌ Add to Cart failed: " + e.getMessage());
     }
     Thread.sleep(1000);

     // -------- CLOSE POPUP (2nd CROSS) --------
     try {
         WebElement crossButton = wait.until(
             ExpectedConditions.elementToBeClickable(
                 By.xpath("//body/div[contains(@data-overlay-container,'true')]/div[contains(@class,'rightsidebar opne_right')]/div[contains(@class,'')]/div[1]")
             )
         );
         crossButton.click();
         System.out.println("✅ 'Cross' button clicked.");
     } catch (Exception e) {
         System.out.println("❌ Cross button failed: " + e.getMessage());
     }
     Thread.sleep(1000);
     
     // -------- OPEN CART --------
     try {
         WebElement cartButton = wait.until(
             ExpectedConditions.elementToBeClickable(
                 By.xpath("//div[contains(@class,'relative inline-flex shrink-0')]//*[name()='svg']")
             )
         );
         cartButton.click();
         System.out.println("✅ 'Cart' button clicked.");
     } catch (Exception e) {
         System.out.println("❌ Cart button failed: " + e.getMessage());
     }
     Thread.sleep(1000);
     

     while (true) {

    	    List<WebElement> cartItems = driver.findElements(
    	            By.xpath("//div[contains(@class,'cart_details')]"));

    	    if (cartItems.isEmpty()) {
    	        System.out.println("🛒 Cart is empty. All items deleted.");
    	        break;
    	    }

    	    WebElement item = cartItems.get(0);

    	    String filmName = item.findElement(
    	            By.xpath(".//h4")).getText();

    	    String price = item.findElement(
    	            By.xpath(".//div[@class='amount']")).getText();

    	    WebElement deleteButton = item.findElement(
    	            By.xpath(".//img[@alt='item delete icon']"));

    	    deleteButton.click();

    	    System.out.println("❌ Deleted Film: " + filmName + " | Price: " + price);

    	    wait.until(ExpectedConditions.stalenessOf(item)); // 🔥 KEY FIX
    	}

     
        scanner.close();
        driver.quit();
    }
    

    // ---------- HELPER METHODS ----------
    private static boolean isMinusClickable(WebElement minusButton) {
        return minusButton.isEnabled()
                && !minusButton.getAttribute("class").contains("disabled")
                && minusButton.getAttribute("aria-disabled") == null;
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}
