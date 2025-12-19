package jolt;

import java.time.Duration;
import java.util.*;
import java.util.regex.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

public class SignUp {

    public static void main(String[] args) throws InterruptedException {

        Scanner sc = new Scanner(System.in);

        System.out.println("Please Enter Email Address : ");
        String un = sc.next();

        // ✅ Password validation with re-entry loop
        String pw;
        while (true) {
            System.out.println("Please Enter Password : ");
            pw = sc.next();

            List<String> errors = validatePassword(pw);

            if (errors.isEmpty()) {
                System.out.println("✅ Password accepted.");
                break;
            } else {
                System.out.println("❌ Invalid Password. Please fix the following:");
                for (String err : errors) {
                    System.out.println("- " + err);
                }
                System.out.println();
            }
        }
        // First name
        System.out.println("Please Enter First name : ");
        String fn = sc.next();

        // Last name
        System.out.println("Do you want to enter Last Name? (yes/no): ");
        String choice = sc.next().trim().toLowerCase();

        String ln = "";
        if (choice.equals("yes") || choice.equals("y")) {
            System.out.println("Please Enter Last name : ");
            ln = sc.next();
        }

        //Country
        
        System.out.println("Do you want to select Country ? (yes/no): ");
        String choice2 = sc.next().trim().toLowerCase();

        String country = "";
        if (choice2.equals("yes") || choice2.equals("y")) {
            System.out.println("Enter Country Name : ");
            country = sc.next();
        }
        
        System.out.println("Do you want to enter Phone number? (yes/no): ");
        String phone1 = sc.next().trim().toLowerCase();

        String phone = "";
        if (phone1.equals("yes") || phone1.equals("y")) {
            System.out.println("Enter Phone Number : ");
            phone = sc.next();     
        }

        WebDriver driver = new ChromeDriver();
        driver.get("https://www.jolt.film/");
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

        Thread.sleep(2000);
        driver.findElement(By.xpath("//button[@aria-label='Sign Up']")).click();
        Thread.sleep(2000);
        driver.findElement(By.cssSelector("button._button-signup-id")).click();
        Thread.sleep(2000);

        // ---- Check email error message ----
        try {
            WebElement emailerrorMsg = driver.findElement(By.xpath("//div[@id='error-cs-email-required']"));
            System.out.println("⚠️ Email Validation Message: " + emailerrorMsg.getText());
        } catch (TimeoutException e) {}

        Thread.sleep(1000);

        // Check invalid email
        driver.findElement(By.id("email")).sendKeys("qa+123@g");
        Thread.sleep(2000);

        try {
            WebElement invalidemail = driver.findElement(By.xpath("//div[@id='error-cs-email-invalid']"));
            System.out.println("⚠️ Invalid Email Validation Message: " + invalidemail.getText());
        } catch (TimeoutException e) {}

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement emailField = wait.until(ExpectedConditions.elementToBeClickable(By.id("email")));
        emailField.clear();
        emailField.sendKeys(un);

        Thread.sleep(2000);
        driver.findElement(By.cssSelector("button._button-signup-id")).click();

        // Validate email using if-else
        List<WebElement> emailErrors = driver.findElements(By.xpath("//div[@id='error-cs-email-invalid']"));
        if (!emailErrors.isEmpty()) {
            String validationMsg = emailErrors.get(0).getText();
            System.out.println("⚠️ Validation Message: " + validationMsg);
        }

        Thread.sleep(3000);

        // Continue without passkey popup
        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            WebElement continueWithoutPasskey = wait1.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("button[value='abort-passkey-enrollment']")));
            continueWithoutPasskey.click();
        } catch (Exception e) {}

        driver.findElement(By.name("password")).sendKeys(pw);
        Thread.sleep(500);
        driver.findElement(By.name("action")).click();
        Thread.sleep(1000);

        // User already exists check
        try {
            WebElement userExistMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//p[@class='c3fb3f447 c5e961090']")));
            String msg = userExistMsg.getText().trim();

            if (!msg.isEmpty()) {
                System.out.println("⚠️ User Already Exists Message: " + msg);
                driver.quit();
                return;
            }
        } catch (TimeoutException e) {}

        Thread.sleep(1000);
        driver.findElement(By.xpath("//span[@class='af-button-text']")).click();
        Thread.sleep(1000);

        // First name validation
        try {
            WebElement firstnameError = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//p[@id='af_uid_404']")));
            System.out.println("⚠️ First name Validation Message: " + firstnameError.getText());
        } catch (TimeoutException e) {}

        // Checkbox validation
        try {
            WebElement checkboxError = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//p[@id='af_uid_412']")));
            System.out.println("⚠️ Checkbox Validation Message: " + checkboxError.getText());
        } catch (TimeoutException e) {}

        Thread.sleep(1000);

        try {
            WebElement boxError = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//p[@id='af_uid_402']")));
            System.out.println("⚠️ Box Error Message: " + boxError.getText());
        } catch (TimeoutException e) {}

        Thread.sleep(2000);

        WebElement firstNameField = wait.until(ExpectedConditions.elementToBeClickable(By.name("first_name")));
        firstNameField.click();
        firstNameField.sendKeys(fn);

        Thread.sleep(1000);

        if (!ln.isEmpty()) {
            WebElement lastNameField = wait.until(ExpectedConditions.elementToBeClickable(By.name("last_name")));
            lastNameField.click();
            lastNameField.sendKeys(ln);
            Thread.sleep(1000);
        }

        // COUNTRY DROPDOWN -----

        WebElement cellPhoneField = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@title='India (भारत) (+91)']")));
        cellPhoneField.click();
        Thread.sleep(1000);

        WebElement countryDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@placeholder='Type to find your country…']")));
        countryDropdown.click();
        Thread.sleep(800);

        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Type to find your country…']")));
        searchBox.clear();
        searchBox.sendKeys(country);

        Thread.sleep(1000);

      
        // Case-insensitive exact-start match (avoids British Indian Ocean Territory)
        String countryLower = country.toLowerCase().trim();

        List<WebElement> countries = driver.findElements(
            By.xpath("//div[contains(@class,'af-telField-country')]//span[contains(@class,'af-telFieldOptionListCountry-name')]")
        );

        WebElement correctCountry = null;

        for (WebElement c : countries) {
            String text = c.getText().toLowerCase().trim();

            // Must start with the country name typed by user
            if (text.startsWith(countryLower)) {
                correctCountry = c;
                break;
            }
        }

        if (correctCountry == null) {
            System.out.println("❌ No matching country found!");
        } else {
            // scroll into view
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", correctCountry);
            Thread.sleep(300);

            // Click parent <div> (clickable element)
            WebElement clickableDiv = correctCountry.findElement(By.xpath("./ancestor::div[contains(@class,'af-telField-country')]"));
            clickableDiv.click();

            // show which country is clicked
            System.out.println("✅ Country Selected: " + correctCountry.getText());
        }

        
        Thread.sleep(1000);

        // Enter phone number in browser
        if (!phone.isEmpty()) {
            WebElement phoneField = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@id='af_uid_407']")));
            phoneField.clear();
            phoneField.sendKeys(phone);
         
        }

        
        
        // Checkbox validation
        try {
            WebElement phonenoError = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//p[@id='af_uid_408']")));
            System.out.println("⚠️ Phone No Validation Message: " + phonenoError.getText());
        } catch (TimeoutException e) {}

        Thread.sleep(1000);

        // Checkbox
        WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("af_uid_411")));
        checkbox.click();
        Thread.sleep(1000);

        driver.findElement(By.xpath("//button//span[text()='Continue']")).click();

        //
        List<WebElement> boxErrorList = driver.findElements(By.xpath("//p[@id='af_uid_402']"));

        if (!boxErrorList.isEmpty()) {
            System.out.println("⚠️ Box Error Message: " + boxErrorList.get(0).getText());
        }


        Thread.sleep(2000);
  

        Thread.sleep(10000);
        driver.quit();
        System.out.println("✅ Signup Successfully ! ");
    }

    // -----------------------------
    // PASSWORD VALIDATION METHOD
    // -----------------------------
    private static List<String> validatePassword(String password) {

        List<String> errors = new ArrayList<>();

        if (password.length() < 10) {
            errors.add("Password must be at least 10 characters long.");
        }

        boolean hasLower = Pattern.compile("[a-z]").matcher(password).find();
        boolean hasUpper = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find();

        int count = 0;
        if (hasLower) count++;
        if (hasUpper) count++;
        if (hasDigit) count++;
        if (hasSpecial) count++;

        if (count < 3) {
            errors.add("Password must contain at least 3 of: lowercase, uppercase, numbers, special characters.");
        }

        // Check for 3 identical consecutive characters
        if (Pattern.compile("(.)\\1\\1").matcher(password).find()) {
            errors.add("Password must not contain more than 2 identical consecutive characters.");
        }

        return errors;
    }
}
