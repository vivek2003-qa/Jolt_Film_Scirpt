package jolt;

import java.time.Duration;
import java.util.List;
import java.util.Scanner;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

public class UpdateProfile {
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);

        // ----------- Sign In First -----------
        System.out.print("Please Enter Email Address: ");
        String un = sc.next();
        System.out.print("Please Enter Password: ");
        String pw = sc.next();
        sc.nextLine(); // consume newline

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
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

        Thread.sleep(500);
        
        // ----------- Minimize the browser -----------
        ((JavascriptExecutor) driver).executeScript("window.blur();");
        driver.manage().window().minimize();
        System.out.println("🪟 Browser minimized.");

        // ----------- Take user input for profile update -----------
        System.out.print("\nEnter First Name (required): ");
        String firstName = sc.nextLine();

        String lastName = "";
     

        // ✅ Ask repeatedly until valid yes/no for Last Name
        String editLast;
        while (true) {
            System.out.print("Do you want to edit Last Name? (yes/no): ");
            editLast = sc.nextLine().trim().toLowerCase();
            if (editLast.equals("yes") || editLast.equals("no")) break;
            System.out.println("⚠️ Invalid input. Please type 'yes' or 'no'.");
        }

        // ✅ Handle based on choice
        if (editLast.equals("yes")) {
            System.out.print("Enter Last Name: ");
            lastName = sc.nextLine().trim();
            // After entering last name, proceed to country
            
        } else {
            // If user chose not to edit last name, ask country directly
      
        }

        // Country Selection
        System.out.print("Enter Country Name: ");
        String countryInput = sc.nextLine().trim().toLowerCase();
        
        // ✅ Ask for Cell phone number (no restriction)
        System.out.print("Enter Cell Phone Number: ");
        String cellNumber = sc.nextLine().trim();

        // ----------- Restore (maximize) browser -----------
        driver.manage().window().maximize();
        Thread.sleep(1000);

        // ----------- Open profile and update fields -----------
        try {
            WebElement profileBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='toggler']")));
            profileBtn.click();
            System.out.println("✅ Profile button clicked.");
        } catch (Exception ex) {
            System.out.println("⚠️ Profile button not found: " + ex.getMessage());
        }

        Thread.sleep(1000);
        driver.findElement(By.xpath("//button[normalize-space()='My Profile']")).click();
        Thread.sleep(1000);

        
     // ----------- Update First Name -----------
        try {
            WebElement firstNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='6']")));
            firstNameInput.click();
            Thread.sleep(300);

            // Clear existing text completely
            firstNameInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            firstNameInput.sendKeys(Keys.DELETE);
            Thread.sleep(300);
            firstNameInput.clear();
            Thread.sleep(300);

            // Type slowly (character by character) to ensure React detects typing
            for (char c : firstName.toCharArray()) {
                firstNameInput.sendKeys(String.valueOf(c));
                Thread.sleep(100);
            }

            // Trigger change event (simulate leaving the field)
            firstNameInput.sendKeys(Keys.TAB);
            Thread.sleep(500);

            System.out.println("✅ First name updated successfully and confirmed.");
        } catch (Exception e) {
            System.out.println("❌ Error while updating first name: " + e.getMessage());
        }


        // Update last name if provided
        if (!lastName.isEmpty()) {
            WebElement lastNameField = driver.findElement(By.xpath("//input[@id='12']"));
            lastNameField.click();
            lastNameField.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            Thread.sleep(300);
            lastNameField.sendKeys(Keys.DELETE);
            Thread.sleep(300);
            lastNameField.sendKeys(lastName);
            System.out.println("✅ Last name updated successfully.");
        }

        // ----------- Country Selection -----------
        WebElement flagBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[contains(@class,'selected-flag') and @role='button']")));
        flagBtn.click();

        List<WebElement> countries = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
            By.xpath("//li[contains(@class,'country')]//span[contains(@class,'country-name')]")));

        for (WebElement country : countries) {
            if (country.getText().trim().equalsIgnoreCase(countryInput)) {
                country.click();
                break;
            }
        }
        Thread.sleep(1000);
        
        // ----------- Update Cell Phone Number -----------
        try {
            WebElement phoneField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[@placeholder='Cell Phone Number (Optional)']")
                )
            );

            phoneField.click();
            Thread.sleep(300);

            // Get existing value
            String existingValue = phoneField.getAttribute("value");
            System.out.println("Existing phone value: " + existingValue);

            // If value contains space (country code + number)
            if (existingValue != null && existingValue.contains(" ")) {
                String countryCode = existingValue.substring(0, existingValue.indexOf(" ") + 1);
                
                // Clear full field
                phoneField.sendKeys(Keys.chord(Keys.CONTROL, "a"));
                phoneField.sendKeys(Keys.DELETE);

                // Re-enter only country code
                phoneField.sendKeys(countryCode);
            } else {
                // If no value or no space, clear everything
                phoneField.sendKeys(Keys.chord(Keys.CONTROL, "a"));
                phoneField.sendKeys(Keys.DELETE);
            }

            // Enter new number from console
            phoneField.sendKeys(cellNumber);

            System.out.println("✅ Cell phone number updated successfully.");

        } catch (Exception e) {
            System.out.println("❌ Unable to update cell phone number: " + e.getMessage());
        }

        //click on update profile button
        Thread.sleep(1000);
        driver.findElement(By.xpath("//button[@aria-label='update Profile']")).click();
        
        // ---- Check box validation message ----
        try {
            WebDriverWait validation = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement ErrorValidation = validation.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[text()='Please check the SMS terms']")));
            System.out.println("✅ Check box Validation Message: " + ErrorValidation.getText());
        } catch (TimeoutException e) {
           
        }
        
        Thread.sleep(2000);
        
        // ----------- Remove last 5 digits from phone number -----------
        try {
            WebElement phoneField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[@placeholder='Cell Phone Number (Optional)']")
                )
            );

            phoneField.click();
            Thread.sleep(300);

            // Remove last 5 digits using BACK_SPACE
            for (int i = 0; i < 5; i++) {
                phoneField.sendKeys(Keys.BACK_SPACE);
            }

            System.out.println("✅ Last 5 digits removed successfully.");

        } catch (Exception e) {
            System.out.println("❌ Unable to remove last 5 digits: " + e.getMessage());
        }

        //Click on terms and conditions checkbox
        Thread.sleep(1000);
        driver.findElement(By.xpath("//input[@id='311']")).click();
        
        //click on update profile button
        Thread.sleep(1000);
        driver.findElement(By.xpath("//button[@aria-label='update Profile']")).click();
       
        // ---- Invalid phone number popup error ----
        try {
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement popupError = longWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'Please enter a valid phone number')]")
            ));
            System.out.println("✅ Popup Message: " + popupError.getText());
        } catch (TimeoutException e) {
            System.out.println("⚠️ No invalid phone number popup shown.");
        }
    	Thread.sleep(1000);
        
    	// ----------- Update Cell Phone Number -----------
        try {
            WebElement phoneField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[@placeholder='Cell Phone Number (Optional)']")
                )
            );

            phoneField.click();
            Thread.sleep(300);

            // Get existing value
            String existingValue = phoneField.getAttribute("value");
            System.out.println("Existing phone value: " + existingValue);

            // If value contains space (country code + number)
            if (existingValue != null && existingValue.contains(" ")) {
                String countryCode = existingValue.substring(0, existingValue.indexOf(" ") + 1);
                
                // Clear full field
                phoneField.sendKeys(Keys.chord(Keys.CONTROL, "a"));
                phoneField.sendKeys(Keys.DELETE);

                // Re-enter only country code
                phoneField.sendKeys(countryCode);
            } else {
                // If no value or no space, clear everything
                phoneField.sendKeys(Keys.chord(Keys.CONTROL, "a"));
                phoneField.sendKeys(Keys.DELETE);
            }

            // Enter new number from console
            phoneField.sendKeys(cellNumber);

            System.out.println("✅ Cell phone number updated successfully.");

        } catch (Exception e) {
            System.out.println("❌ Unable to update cell phone number: " + e.getMessage());
        }

        Thread.sleep(1000);

        //Click on terms and conditions checkbox
        Thread.sleep(1000);
        driver.findElement(By.xpath("//input[@id='311']")).click();
        
        //click on update profile button
        driver.findElement(By.xpath("//button[@aria-label='update Profile']")).click();
        Thread.sleep(1000);						
        
        // ---- Success popup ----
        try {
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement successPopup = longWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'User has been updated successfully')]")
            ));
            System.out.println("✅ Success Popup: " + successPopup.getText());
        } catch (TimeoutException e) {
            System.out.println("❌ No success popup appeared.");
        }
        
        Thread.sleep(1000);
        driver.quit(); 
    }
 
}
