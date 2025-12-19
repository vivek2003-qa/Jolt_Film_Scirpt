package jolt;

import java.time.Duration;
import java.util.List;
import java.util.Scanner;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

public class MoviePurchasePlay {
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
        System.out.print("Enter the promo code: ");
        String promoCode = sc.nextLine();

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        JavascriptExecutor js = (JavascriptExecutor) driver;

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
       
      
        // --- check not available in your region using XPath + JS Alert ---
        try {
            // First check via XPath
            List<WebElement> regionPopup = driver.findElements(
                By.xpath("//div[contains(text(),'This film is not available in your region.')]")
            );

            if (!regionPopup.isEmpty()) {
                // If popup found → trigger JavaScript alert
                js.executeScript("alert('REGION_BLOCKED');");
            }

            // Now wait max 2 sec for JS alert
            WebDriverWait alertWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            alertWait.until(ExpectedConditions.alertIsPresent());

            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();

            if (alertText.equals("REGION_BLOCKED")) {
                System.out.println("✅ Region Popup Detected via Alert!");
            } else {
                System.out.println("⚠️ Unexpected alert text: " + alertText);
            }

            alert.accept(); // close alert

        } catch (TimeoutException e) {
            System.out.println("⚠️ Region restriction popup NOT detected (no alert).");
        } catch (Exception e) {
            System.out.println("❌ Error detecting region popup through alert: " + e.getMessage());
        }

        // --- Rent Now ---
        clickIfAvailable(wait1, js, By.cssSelector("div[class='watch_films_btns d_flex align_items_center max-ml:mt-[2rem] max-ml:hidden'] button[aria-label='Rent Now']"), "'Rent Now'");

        // --- Start Watching ---
        boolean startClicked = tryClick(wait1, js, By.xpath("//div[contains(@class,'watch_films_btns d_flex align_items_center max-ml:mt-[2rem] max-ml:hidden')]//button[contains(@aria-label,'START WATCHING')][normalize-space()='START WATCHING']"), "'Start Watching'");
        startClicked = tryClick(wait1, js, By.cssSelector("button[aria-label='START WATCHING'][type='button']"), "'Start Watching of Popup'");

        if (startClicked) {
            Thread.sleep(2000);

            //Click on Pause button  
            driver.findElement(By.xpath("//button[@aria-label='Pause']")).click();
            System.out.println("✅ Clicked on Pause button");
            
            // Click on Pause button again
           
            driver.findElement(By.xpath("//button[@aria-label='Pause']")).click();
            
            //Click on Picture in Picture button 
          
            driver.findElement(By.xpath("//button[@aria-label='Picture-in-Picture']")).click();
            System.out.println("✅ Clicked on Picture in Picture button");
            Thread.sleep(1500);
            
            //Click on Picture in Picture button  Again
           
            driver.findElement(By.xpath("//button[@aria-label='Picture-in-Picture']")).click();
         
            //Click on mute button 
           
            driver.findElement(By.xpath("//button[@aria-label='Mute']")).click();
            System.out.println("✅ Clicked on mute button");
            Thread.sleep(500);
            
            // Unmute 
            driver.findElement(By.xpath("//button[@aria-label='Mute']")).click();
            
            WebDriverWait wait11 = new WebDriverWait(driver, Duration.ofSeconds(10));
         // 1️⃣ SAFE SUBTITLE BUTTON CLICK
            WebElement subtitleBtn = null;

            try {
                subtitleBtn = wait11.until(
                        ExpectedConditions.elementToBeClickable(
                                By.xpath("//div[@id='subtitle-toggle-dropdown']")
                        )
                );
                subtitleBtn.click();
                System.out.println("✅ Clicked on Subtitle button");
                Thread.sleep(500);
            } catch (Exception e) {
                System.out.println("⚠️ Subtitle not found, skipping subtitle selection...");
            }

            // 2️⃣ SAFE SUBTITLE OPTION SELECT
            if (subtitleBtn != null) {
                try {
                    WebElement englishSubtitle = wait.until(
                            ExpectedConditions.elementToBeClickable(
                                    By.xpath("//div[contains(@class,'subtitle_dropdown')]//div[@data-value='off']")
                            )
                    );
                    englishSubtitle.click();
                    System.out.println("✅ Off Subtitle selected");
                    Thread.sleep(500);
                    
                } catch (Exception e) {
                    System.out.println("⚠️ Off subtitle option not available, skipping...");
                    
                }
            }

            Thread.sleep(1000);
         // -------- Click Setting button --------
            try {
                WebElement settingBtn = wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.xpath("//button[@aria-label='Settings']")
                        )
                );
                settingBtn.click();
                System.out.println("✅ Clicked on Setting button ");
                Thread.sleep(500);
            } catch (Exception e) {
                System.out.println("⚠️ Setting button not found or not clickable. Skipping...");
            }

            // -------- Click Video Quality --------
            try {
                WebElement VideoQuality = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("//select[@class='bmpui-ui-selectbox bmpui-ui-videoqualityselectbox']")
                    )
                );
                VideoQuality.click();
                System.out.println("✅ Clicked on Video Quality in Setting");

                Thread.sleep(1000);
                VideoQuality.click();
            } catch (Exception e) {
                System.out.println("⚠️ Video Quality option not found. Skipping...");
            }

            // -------- Select HD 720p --------
            try {
                WebElement videoqualclick = wait.until(
                        ExpectedConditions.elementToBeClickable(
                            By.xpath("//option[normalize-space(text())='HD 720p (4Mbit)']")
                        )
                );
                videoqualclick.click();
                System.out.println("✅ Clicked on HD video Quality");
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println("⚠️ HD 720p option not available. Skipping...");
            }

            // -------- Select Speed 2x --------
            try {
                WebElement speed = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("//option[normalize-space(text())='2x']")
                    )
                );
                speed.click();
                System.out.println("✅ Clicked on speed in Setting");
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println("⚠️ Speed 2x option not available. Skipping...");
            }

            
            Thread.sleep(1000);
            
            driver.findElement(By.xpath("//div[contains(@class, 'cross-icon')]")).click();
            System.out.println("✅ Clicked on cross icon");
        }

        // --- Continue Watching ---
        if (!startClicked) {   // ✅ RUN ONLY IF 'Start Watching' WAS NOT CLICKED
            boolean continueClicked = tryClick(wait1, js, 
                By.xpath("//div[contains(@class,'watch_films_btns d_flex align_items_center max-ml:mt-[2rem] max-ml:hidden')]//button[contains(@aria-label,'Continue Watching')][normalize-space()='Continue Watching']"), 
                "'Continue Watching'");

            if (continueClicked) {
                Thread.sleep(2000);
                
                //Click on Pause button  
                driver.findElement(By.xpath("//button[@aria-label='Pause']")).click();
                System.out.println("✅ Clicked on Pause button");
                
                // Click on Pause button again
                
                driver.findElement(By.xpath("//button[@aria-label='Pause']")).click();
                
                //Click on Picture in Picture button 
                
                driver.findElement(By.xpath("//button[@aria-label='Picture-in-Picture']")).click();
                System.out.println("✅ Clicked on Picture in Picture button ");
                Thread.sleep(1500);
                
                //Click on Picture in Picture button  Again
                driver.findElement(By.xpath("//button[@aria-label='Picture-in-Picture']")).click();
             
                //Click on mute button 
                driver.findElement(By.xpath("//button[@aria-label='Mute']")).click();
                System.out.println("✅ Clicked on mute button");
                Thread.sleep(500);
                
                // Unmute 
                driver.findElement(By.xpath("//button[@aria-label='Mute']")).click();
                
                WebDriverWait wait11 = new WebDriverWait(driver, Duration.ofSeconds(10));
                Scanner sc1 = new Scanner(System.in);

             // 1️⃣ SAFE SUBTITLE BUTTON CLICK
                WebElement subtitleBtn = null;

                try {
                    subtitleBtn = wait11.until(
                            ExpectedConditions.elementToBeClickable(
                                    By.xpath("//div[@id='subtitle-toggle-dropdown']")
                            )
                    );
                    subtitleBtn.click();
                    System.out.println("✅ Clicked on Subtitle button");
                    Thread.sleep(500);
                } catch (Exception e) {
                    System.out.println("⚠️ Subtitle not found, skipping subtitle selection...");
                }

                // 2️⃣ SAFE SUBTITLE OPTION SELECT
                if (subtitleBtn != null) {
                    try {
                        WebElement englishSubtitle = wait.until(
                                ExpectedConditions.elementToBeClickable(
                                        By.xpath("//div[contains(@class,'subtitle_dropdown')]//div[@data-value='off']")
                                )
                        );
                        englishSubtitle.click();
                        System.out.println("✅ Off Subtitle selected");
                        Thread.sleep(500);
                        
                    } catch (Exception e) {
                        System.out.println("⚠️ Off subtitle option not available, skipping...");
                        
                    }
                }

                Thread.sleep(1000);
             // -------- Click Setting button --------
                try {
                    WebElement settingBtn = wait.until(
                            ExpectedConditions.elementToBeClickable(
                                    By.xpath("//button[@aria-label='Settings']")
                            )
                    );
                    settingBtn.click();
                    System.out.println("✅ Clicked on Setting button ");
                    Thread.sleep(500);
                } catch (Exception e) {
                    System.out.println("⚠️ Setting button not found or not clickable. Skipping...");
                }

                // -------- Click Video Quality --------
                try {
                    WebElement VideoQuality = wait.until(
                        ExpectedConditions.elementToBeClickable(
                            By.xpath("//select[@class='bmpui-ui-selectbox bmpui-ui-videoqualityselectbox']")
                        )
                    );
                    VideoQuality.click();
                    System.out.println("✅ Clicked on Video Quality in Setting");

                    Thread.sleep(1000);
                    VideoQuality.click();
                } catch (Exception e) {
                    System.out.println("⚠️ Video Quality option not found. Skipping...");
                }

                // -------- Select HD 720p --------
                try {
                    WebElement videoqualclick = wait.until(
                            ExpectedConditions.elementToBeClickable(
                                By.xpath("//option[normalize-space(text())='HD 720p (4Mbit)']")
                            )
                    );
                    videoqualclick.click();
                    System.out.println("✅ Clicked on HD video Quality");
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println("⚠️ HD 720p option not available. Skipping...");
                }

                // -------- Select Speed 2x --------
                try {
                    WebElement speed = wait.until(
                        ExpectedConditions.elementToBeClickable(
                            By.xpath("//option[normalize-space(text())='2x']")
                        )
                    );
                    speed.click();
                    System.out.println("✅ Clicked on speed in Setting");
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println("⚠️ Speed 2x option not available. Skipping...");
                }

                
                Thread.sleep(1000);
                driver.findElement(By.xpath("//div[contains(@class, 'cross-icon')]")).click();
                System.out.println("✅ Clicked on cross icon");
              
        // --- Watch Preview ---
        clickIfAvailable(wait11, js, By.cssSelector("button[class='btn position_relative sky_border_btn text_uppercase trailer_btn :!min-w-[19.2rem] !max-w-[19.2rem] !min-h-[4rem] !text-[1.6rem] max-ml:!max-w-full max-ml:!min-w-full !bg-[#12141466]']"), "'Watch Preview'");   
        Thread.sleep(5000);
        try {
            driver.findElement(By.xpath("//div[contains(@class, 'cross-icon')]")).click();
            System.out.println("✅ Clicked on cross icon");
            } catch (Exception e) {}
            }
        
        // ------------ HOURS LEFT TO WATCH CODE ------------------

        try {
            WebDriverWait wait12 = new WebDriverWait(driver, Duration.ofSeconds(8));

            WebElement hoursLeftText = wait12.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[@class='block max-ml:hidden']//h2[contains(@class,'aval_or_not')]")));

            String hoursLeft = hoursLeftText.getText().trim();

            System.out.println("Hours Left : " + hoursLeft);

        } catch (TimeoutException e) {
            System.out.println("Hours Left text not available");
        } catch (Exception e) {
           
        }
        

        // --- Add to Cart ---
        clickIfAvailable(wait1, js, By.xpath("//button[contains(text(),'ADD TO CART')]"), "'ADD TO CART'");

        // --- Checkout ---
        clickIfAvailable(wait1, js, By.cssSelector("button[aria-label='CHECKOUT']"), "'CHECKOUT'");
       
        
        // --- Apply Promo Code ---
        try {
            WebElement promoInput = wait1.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='600']")));
            promoInput.clear();
            Thread.sleep(500);
            promoInput.sendKeys(promoCode);
            WebElement applyBtn = driver.findElement(By.xpath("//button[@aria-label='apply']"));
            applyBtn.click();
            
            
        } catch (TimeoutException e) {
            
        }
        
        WebDriverWait wait11 = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            WebElement promoMsg = wait11.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(),'Promo code applied successfully!')]")
                )
            );
            System.out.println("Promocode Message: " + promoMsg.getText());
        } catch (TimeoutException e) {
            System.out.println("Promocode Message not displayed");
        }

            
        
        // ---- Check for promo code expired or not ----
        List<WebElement> promoExpiredMsg = driver.findElements(
            By.xpath("//div[contains(text(),'Promo code is expired.')]")
        );

        if (!promoExpiredMsg.isEmpty()) {
            System.out.println("Promocode Message: " + promoExpiredMsg.get(0).getText());
        } else {
           
        }

        
        // ---- Check for promo code not valid ----
        List<WebElement> promoNotValidMsg = driver.findElements(
            By.xpath("//div[contains(text(),'Promo code is not valid or expired.')]")
        );

        if (!promoNotValidMsg.isEmpty()) {
            System.out.println("Promocode Message: " + promoNotValidMsg.get(0).getText());
        } else {
           
        }
        
        // --- Purchase ---
        clickIfAvailable(wait1, js, By.xpath("//button[@aria-label='PURCHASE']"), "'PURCHASE'");
        
        // ---- Purchase success message ----
        try {
            WebElement purchaseMsg = wait1.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(text(),'Order placed successfully')]")));
            
            System.out.println("Purchase Message: " + purchaseMsg.getText());
        } catch (TimeoutException e) {
        }

        sc.close();
            }
            
        driver.quit();
    }

    // ----------- Helper Method to Click Buttons Safely -----------
    private static void clickIfAvailable(WebDriverWait wait, JavascriptExecutor js, By locator, String name) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            js.executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", element);
            Thread.sleep(500);

            try {
                element.click();
            } catch (Exception e1) {
                try {
                    new org.openqa.selenium.interactions.Actions((WebDriver) js)
                            .moveToElement(element)
                            .click()
                            .perform();
                } catch (Exception e2) {
                    js.executeScript(
                            "var evt = new MouseEvent('click', {bubbles:true, cancelable:true, view:window}); arguments[0].dispatchEvent(evt);",
                            element
                    );
                }
            }

            System.out.println("✅ " + name + " button clicked.");
            Thread.sleep(1000);

        } catch (TimeoutException e) {
            System.out.println("⚠️ " + name + " button not available.");
        } catch (Exception e) {
            System.out.println("❌ Failed to click " + name + " button: " + e.getMessage());
        }
    }

    // ----------- Helper Method to Try Clicking and Return Status -----------
    private static boolean tryClick(WebDriverWait wait, JavascriptExecutor js, By locator, String name) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            js.executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", element);
            Thread.sleep(500);
            element.click();
            System.out.println("✅ " + name + " button clicked.");
            return true;
        } catch (TimeoutException e) {
            System.out.println("⚠️ " + name + " button not available.");
            return false;
        } catch (Exception e) {
            System.out.println("❌ Failed to click " + name + " button: " + e.getMessage());
            return false;
        }
    }
}
