package jolt;

import java.time.Duration;
import java.util.ArrayList;
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

public class EventPlay {

    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);

        // ----------- Sign In First -----------
        System.out.print("Please Enter Email Address: ");
        String un = sc.next();
        System.out.print("Please Enter Password: ");
        String pw = sc.next();
        sc.nextLine();
        System.out.print("Enter the movie name: ");
        String movieName = sc.nextLine();

        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();
        driver.get("https://jolt.film/");

        // Close cookie popup if present
        try {
            WebElement closeButton = driver.findElement(
                    By.cssSelector(".onetrust-close-btn-handler.banner-close-button.ot-close-icon"));
            closeButton.click();
            Thread.sleep(1000);
        } catch (Exception e) {}

        // Click Login button
        try {
            WebElement loginBtn = driver.findElement(By.cssSelector("button[aria-label='Log IN']"));
            loginBtn.click();
        } catch (Exception e) {}

        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Enter email
            WebElement usernameField = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
            usernameField.sendKeys(un);

            WebElement continueBtn = driver.findElement(By.cssSelector("button._button-login-id"));
            continueBtn.click();

            // Email validation
            List<WebElement> emailErrors = driver.findElements(By.xpath("//div[@id='error-cs-email-invalid']"));
            if (!emailErrors.isEmpty()) {
                System.out.println("⚠️ Validation: " + emailErrors.get(0).getText());
                driver.quit();
                return;
            }

            // Password page
            WebElement passwordField = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
            passwordField.sendKeys(pw);

            WebElement loginPasswordBtn = driver.findElement(By.cssSelector("button._button-login-password"));
            loginPasswordBtn.click();
            Thread.sleep(1000);

            // User not exist
            try {
                WebElement userNotExistMsg = wait1.until(
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@class='cc6c34cee c0dad7210']")));
                System.out.println("⚠️ " + userNotExistMsg.getText());
                driver.quit();
                return;
            } catch (TimeoutException e) {}

            // Password error
            try {
                WebElement passwordError = wait1.until(
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@id='error-element-password']")));
                System.out.println("⚠️ " + passwordError.getText());
                driver.quit();
                return;
            } catch (TimeoutException e) {}

            // Continue without passkey
            try {
                WebElement continueWithoutPasskey = wait1.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector("button[value='abort-passkey-enrollment']")));
                continueWithoutPasskey.click();
                Thread.sleep(700);
            } catch (Exception e) {}

            System.out.println("Login Successful!");

        } catch (Exception e) {
            System.out.println("⚠️ Login error: " + e.getMessage());
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


            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement targetMovie = w.until(ExpectedConditions.presenceOfElementLocated(By.xpath(movieXpath)));

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", targetMovie);
            Thread.sleep(800);

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", targetMovie);

            System.out.println("🎬 Movie clicked: " + movieName);

        } catch (Exception e) {
            System.out.println("❌ Movie not found or click failed: " + movieName);
            e.printStackTrace();
        }

        Thread.sleep(2000);

        List<WebElement> initialEvents = driver.findElements(By.cssSelector("h4.cursor-pointer.title_event"));

        if (initialEvents.isEmpty()) {
            System.out.println("❌ No event available for this movie.");
            driver.quit();
            return;
        }

        JavascriptExecutor js = (JavascriptExecutor) driver;

        String EventSection = "//button[@aria-label='See all of your event registrations in My Orders.']";

        try {
            WebElement element = wait1.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath(EventSection)));

            wait1.until(ExpectedConditions.visibilityOf(element));

            js.executeScript("arguments[0].scrollIntoView(true);", element);
            Thread.sleep(1500);

            js.executeScript("window.scrollBy(0, -200);");
            Thread.sleep(1000);

            System.out.println("✅ Successfully scrolled to the Event section");

        } catch (Exception e) {}

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor jsEvt = (JavascriptExecutor) driver;

        List<String> allEventNames = new ArrayList<>();

        List<WebElement> events1 = driver.findElements(By.cssSelector("h4.cursor-pointer.title_event"));
        for (WebElement ev : events1) {
            String n = ev.getText().trim();
            if (!n.isEmpty() && !allEventNames.contains(n)) {
                allEventNames.add(n);
            }
        }

        try {
            By rightarrowXpath = By.xpath(
                    "//div[@class='inner_container']//div[contains(@class,'upcoming_event_cards')]//div//div[@class='slick-arrow slick-next']//*[name()='svg']");

            try {
                for (int i = 0; i < 2; i++) {
                    WebElement rightArrow = wait.until(
                            ExpectedConditions.elementToBeClickable(rightarrowXpath)
                    );
                    rightArrow.click();
                    Thread.sleep(500);
                }
            } catch (Exception e) {}
        } catch (Exception e1) {}

        List<WebElement> events2 = driver.findElements(By.cssSelector("h4.cursor-pointer.title_event"));
        for (WebElement ev : events2) {
            String n = ev.getText().trim();
            if (!n.isEmpty() && !allEventNames.contains(n)) {
                allEventNames.add(n);
            }
        }

        List<WebElement> events3 = driver.findElements(By.cssSelector("h4.cursor-pointer.title_event"));
        for (WebElement ev : events3) {
            String n = ev.getText().trim();
            if (!n.isEmpty() && !allEventNames.contains(n)) {
                allEventNames.add(n);
            }
        }

        driver.manage().window().minimize();

        System.out.println("------ AVAILABLE EVENTS (ALL) ------");
        for (int i = 0; i < allEventNames.size(); i++) {
            System.out.println((i + 1) + ". " + allEventNames.get(i));
        }

        int choice = 0;
        while (true) {
            System.out.print("Select event from given above: ");
            choice = sc.nextInt();

            if (choice >= 1 && choice <= 5) break;

            System.out.println("⚠️ Please select from given list");
        }

        driver.manage().window().maximize();
        Thread.sleep(1000);

        List<WebElement> finalEvents = driver.findElements(By.cssSelector("h4.cursor-pointer.title_event"));
        WebElement selectedEvent = finalEvents.get(choice - 1);

        By leftarrowXpath = By.xpath("//div[@class='slick-arrow slick-prev']//*[name()='svg']");

        if (choice != 4 && choice != 5) {
            try {
                for (int i = 0; i < 2; i++) {
                    WebElement leftArrow = wait.until(
                            ExpectedConditions.elementToBeClickable(leftarrowXpath)
                    );
                    leftArrow.click();
                    Thread.sleep(500);
                }
            } catch (Exception e) {}
        }

        jsEvt.executeScript("arguments[0].click();", selectedEvent);

        //--------------------------------------------------------------------
        // --------------------- FIXED BUTTON LOGIC ---------------------------
        WebDriverWait waitBtn = new WebDriverWait(driver, Duration.ofSeconds(12));
        JavascriptExecutor jsx = (JavascriptExecutor) driver;

        WebElement btn = null;
        String clickedButton = "";

        try {
            btn = waitBtn.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@aria-label='Watch Event Replay']")));
            clickedButton = "Watch Replay";
            Thread.sleep(500);
            driver.findElement(By.xpath("//button[@aria-label='Play video']")).click();
            Thread.sleep(2500);
        } catch (Exception e) {}

        if (btn == null) {
            try {
                btn = waitBtn.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[@aria-label='RSVP']")));
                clickedButton = "RSVP";
            } catch (Exception e) {}
        }

        if (btn == null) {
            try {
                btn = waitBtn.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[@aria-label='Join Now']")));
                clickedButton = "Join Now";
            } catch (Exception e) {}
        }

        if (btn != null) {
            jsx.executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
            Thread.sleep(400);
            jsx.executeScript("arguments[0].click();", btn);
            System.out.println("✅ Clicked: " + clickedButton);
        } else {
            System.out.println("❌ No button found.");
        }

        // --------------------------------------------------------------------
        // ⭐⭐⭐ FIX: Close modal after Watch Replay so Add To Cart works ⭐⭐⭐
        Thread.sleep(2000);
        try {
            WebDriverWait waitModal = new WebDriverWait(driver, Duration.ofSeconds(8));

            WebElement modalClose = waitModal.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[aria-label='Close modal']")));

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", modalClose);
            Thread.sleep(1500);
            System.out.println("✔ Modal closed, Add to Cart now clickable");

        } catch (Exception ex) {
            // Modal not present → continue
        }
        // --------------------------------------------------------------------

        Thread.sleep(1000);

        // Add to Cart
        try {
            WebElement AddtoCart = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@aria-label='ADD TO CART']")));
            AddtoCart.click();
        } catch (Exception e) {
            System.out.println("❌ Add to Cart not clickable");
        }

        // Checkout
        try {
            WebElement Checkout = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@aria-label='CHECKOUT']")));
            Checkout.click();
        } catch (Exception e) {}

        // Apply Promo Code
        try {
            WebElement promoInput = wait1.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='600']")));
            promoInput.sendKeys("ATEST100");
            WebElement applyBtn = driver.findElement(By.xpath("//button[@aria-label='apply']"));
            applyBtn.click();
        } catch (TimeoutException e) {}

        List<WebElement> promoMsg = driver.findElements(By.xpath("//div[contains(text(),'Promo code applied successfully!')]"));

        if (!promoMsg.isEmpty()) {
            System.out.println("Promocode Message: " + promoMsg.get(0).getText());
        }

        List<WebElement> promoExpiredMsg = driver.findElements(
                By.xpath("//div[contains(text(),'Promo code is expired.')]")
        );

        if (!promoExpiredMsg.isEmpty()) {
            System.out.println("Promocode Message: " + promoExpiredMsg.get(0).getText());
        }

        List<WebElement> promoNotValidMsg = driver.findElements(
                By.xpath("//div[contains(text(),'Promo code is not valid or expired.')]"));

        if (!promoNotValidMsg.isEmpty()) {
            System.out.println("Promocode Message: " + promoNotValidMsg.get(0).getText());
        }

        driver.quit();
    }
}
