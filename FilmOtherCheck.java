package jolt;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

 class FilmOtherCheck {
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
        
            
        // ------------  PRE DESCRIPTION SECTION CODE  ------------------

        try {
            List<WebElement> preDescSection = driver.findElements(
                    By.xpath("//div[contains(@class,'film_long_desc') and contains(@class,'film_pre_description')]")
            );

            if (preDescSection.size() > 0 && preDescSection.get(0).isDisplayed()) {
                String preDescText = preDescSection.get(0).getText().trim();

                if (!preDescText.isEmpty()) {
                    System.out.println("Pre Description:");
                    System.out.println(preDescText);
                } else {
                    System.out.println("Pre Description section is empty");
                }
            } else {
                System.out.println("Pre Description section not available");
            }

        } catch (Exception e) {
            System.out.println("Pre Description section not available");
        }

        // ------------ DESCRIPTION SECTION CODE  ------------------

        try {
            List<WebElement> DescSection = driver.findElements(
                    By.id("filmDescription")
            );

            if (DescSection.size() > 0 && DescSection.get(0).isDisplayed()) {
                String DescText = DescSection.get(0).getText().trim();

                if (!DescText.isEmpty()) {
                    System.out.println("Description:");
                    System.out.println(DescText);
                } else {
                    System.out.println("Description section is empty");
                }
            } else {
                System.out.println("Description section not available");
            }

        } catch (Exception e) {
            System.out.println("Description section not available");
        }

        
     // ------------  IN THE NEWS SECTION CODE  ------------------

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Actions actions = new Actions(driver);

        // Scroll to IN THE NEWS section
        boolean isNewsSectionAvailable = true;

        try {
            WebElement newsSection = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[contains(@class,'upcoming_event') and contains(@class,'in_new_sec')]")
            ));

            js.executeScript("arguments[0].scrollIntoView(true);", newsSection);
            Thread.sleep(1500);

            js.executeScript("window.scrollBy(0, -200);"); // adjust for header
            Thread.sleep(800);

            System.out.println("✅ Scrolled to IN THE NEWS section.");
        } catch (Exception e) {
            isNewsSectionAvailable = false;
            System.out.println("❌ IN THE NEWS section not available.");
        }

        // Proceed ONLY if section exists
        if (isNewsSectionAvailable) {

            // XPaths
            By rightArrowNewsXpath = By.xpath(
                    "(//div[contains(@class,'upcoming_event in_new_sec')]//div[contains(@class,'slick-next')])[1]"
            );

            By cardTitleXpath = By.xpath(
                    "//div[contains(@class,'related_news')]/following-sibling::div[contains(@class,'event_detail')]//h4"
            );
            By dateXpath = By.xpath(
                    "//div[contains(@class,'related_news')]/following-sibling::div[contains(@class,'event_detail')]/p"
            );
            By sourceXpath = By.xpath(
                    "//div[contains(@class,'related_news')]/following-sibling::div[contains(@class,'event_detail')]//span[contains(@class,'article_content')]"
            );

            // Data holders
            List<String> allNewsTitles = new ArrayList<>();
            List<String> allDatesText  = new ArrayList<>();
            List<String> allSourceText = new ArrayList<>();
            List<String> seenNewsCards = new ArrayList<>();

            int previousCountNews = 0;

            System.out.println("⏳ Collecting news cards...");

            // ---------- CLICK RIGHT ARROW UNTIL NO NEW CARDS ----------
            while (true) {

                List<WebElement> titles  = driver.findElements(cardTitleXpath);
                List<WebElement> dates   = driver.findElements(dateXpath);
                List<WebElement> sources = driver.findElements(sourceXpath);

                for (int i = 0; i < Math.min(titles.size(),
                        Math.min(dates.size(), sources.size())); i++) {

                    String title  = titles.get(i).getText().trim();
                    String date   = dates.get(i).getText().trim();
                    String source = sources.get(i).getText().trim();

                    if (title.isEmpty())
                        continue;

                    String uniqueKey = title + " | " + date + " | " + source;

                    if (!seenNewsCards.contains(uniqueKey)) {
                        seenNewsCards.add(uniqueKey);
                        allNewsTitles.add(title);
                        allDatesText.add(date);
                        allSourceText.add(source);
                    }
                }

                if (allNewsTitles.size() == previousCountNews) {
                    break;
                }
                previousCountNews = allNewsTitles.size();

                try {
                    WebElement rightArrow = wait.until(
                            ExpectedConditions.elementToBeClickable(rightArrowNewsXpath));

                    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", rightArrow);
                    Thread.sleep(400);

                    try {
                        rightArrow.click();
                    } catch (Exception e) {
                        try {
                            actions.moveToElement(rightArrow).click().perform();
                        } catch (Exception ex) {
                            js.executeScript("arguments[0].click();", rightArrow);
                        }
                    }

                    Thread.sleep(900);

                } catch (Exception e) {
                    break;
                }
            }

            // ---------- PRINT NEWS IN TABLE FORMAT ----------
            System.out.println("\n==================== IN THE NEWS ====================");

            // Table Header
            System.out.printf("%-5s | %-150s | %-20s | %-30s%n",
                    "No", "NEWS TITLE", "DATE", "SOURCE");
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            // Table Rows
            for (int i = 0; i < allNewsTitles.size(); i++) {

                System.out.printf("%-5d | %-150s | %-20s | %-30s%n",
                        (i + 1),
                        allNewsTitles.get(i),
                        allDatesText.get(i),
                        allSourceText.get(i));
            }

            System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("✅ Total News Cards Collected: " + allNewsTitles.size());

        }



     // ------------  Cast & Crew SECTION CODE  ------------------

     // Scroll to CAST & CREW section
     boolean isCastSectionAvailable = true;

     try {
         WebElement castSection = wait.until(ExpectedConditions.presenceOfElementLocated(
                 By.xpath("//div[@class='cast_header']//h4[contains(@class,'film_header_title')]")
         ));

         js.executeScript("arguments[0].scrollIntoView(true);", castSection);
         Thread.sleep(1500);

         js.executeScript("window.scrollBy(0, -200);"); // adjust for header
         Thread.sleep(800);

         System.out.println("✅ Scrolled to CAST & CREW section.");
     } catch (Exception e) {
         isCastSectionAvailable = false;
         System.out.println("❌ CAST & CREW section not available.");
     }

     // Proceed ONLY if section exists
     if (isCastSectionAvailable) {

         // XPaths
         By rightArrowCastXpath = By.xpath(
                 "(//div[contains(@class,'cast_sec')]//div[contains(@class,'slick-next')])[1]"
         );

         By castTitleXpath = By.xpath("(//div[contains(@class,'crew_card')]//h5)");
         By castXpath      = By.xpath("(//div[contains(@class,'crew_card')]//span)");

         // Data holders
         List<String> allCastTitles = new ArrayList<>();
         List<String> allSpanText   = new ArrayList<>();
         List<String> seenCastPairs = new ArrayList<>();

         int previousCountCast = 0;

         System.out.println("⏳ Collecting Cast Details...");

         // ---------- CLICK RIGHT ARROW UNTIL NO NEW CASTS ----------
         while (true) {

             List<WebElement> castTitles = driver.findElements(castTitleXpath);
             List<WebElement> castSpans  = driver.findElements(castXpath);

             for (int i = 0; i < Math.min(castTitles.size(), castSpans.size()); i++) {

                 String title = castTitles.get(i).getText().trim();
                 String span  = castSpans.get(i).getText().trim();

                 if (title.isEmpty() || span.isEmpty())
                     continue;

                 String uniqueKey = title + " | " + span;

                 if (!seenCastPairs.contains(uniqueKey)) {
                     seenCastPairs.add(uniqueKey);
                     allCastTitles.add(title);
                     allSpanText.add(span);
                 }
             }

             if (allCastTitles.size() == previousCountCast) {
                 break;
             }
             previousCountCast = allCastTitles.size();

             try {
                 WebElement rightArrowCast = wait.until(
                         ExpectedConditions.elementToBeClickable(rightArrowCastXpath));

                 js.executeScript("arguments[0].scrollIntoView({block: 'center'});", rightArrowCast);
                 Thread.sleep(400);

                 try {
                     rightArrowCast.click();
                 } catch (Exception e) {
                     try {
                         actions.moveToElement(rightArrowCast).click().perform();
                     } catch (Exception ex) {
                         js.executeScript("arguments[0].click();", rightArrowCast);
                     }
                 }

                 Thread.sleep(900);

             } catch (Exception e) {
                 break;
             }
         }

      // ---------- PRINT CAST IN TABLE FORMAT ----------
         System.out.println("\n================ CAST & CREW =================");

         // Table Header
         System.out.printf("%-5s | %-40s | %-40s%n",
                 "No", "Cast Name", "Cast Roles");
         System.out.println("---------------------------------------------------------------------------------------------------");

         // Table Rows
         for (int i = 0; i < allCastTitles.size(); i++) {
             System.out.printf("%-5d | %-40s | %-40s%n",
                     (i + 1),
                     allCastTitles.get(i),
                     allSpanText.get(i));
         }

         System.out.println("---------------------------------------------------------------------------------------------------");
         System.out.println("✅ Total Cast & Crew Collected: " + allCastTitles.size());
     }


  // ------------  VIDEO SECTION CODE  ------------------

  // Scroll to VIDEO section
  boolean isVideoSectionAvailable = true;

  try {
      WebElement videoSection = wait.until(ExpectedConditions.presenceOfElementLocated(
              By.xpath("//div[@class='upcoming_event discover_film_sec']//h2[contains(@class,'film_header_title')]")
      ));

      js.executeScript("arguments[0].scrollIntoView(true);", videoSection);
      Thread.sleep(1500);

      js.executeScript("window.scrollBy(0, -200);"); // adjust for header
      Thread.sleep(800);

      System.out.println("✅ Scrolled to VIDEO section.");
  } catch (Exception e) {
      isVideoSectionAvailable = false;
      System.out.println("❌ VIDEO section not available.");
  }

  // Proceed ONLY if section exists
  if (isVideoSectionAvailable) {

      // XPaths
      By rightArrowVideoXpath = By.xpath(
              "(//div[contains(@class,'upcoming_event video_Sec')]//div[contains(@class,'slick-next')])[1]"
      );

      By videoTitleXpath = By.xpath(
              "(//div[contains(@class,'date_detail EventVedioDate')]//span)"
      );

      // Data holders
      List<String> allVideoTitles = new ArrayList<>();
      List<String> seenVideoTitles = new ArrayList<>();

      int previousCountVideo = 0;

      System.out.println("⏳ Collecting Video Titles...");

      // ---------- CLICK RIGHT ARROW UNTIL NO NEW VIDEOS ----------
      while (true) {

          List<WebElement> videoTitles = driver.findElements(videoTitleXpath);

          for (WebElement video : videoTitles) {

              String title = video.getText().trim();

              if (title.isEmpty())
                  continue;

              if (!seenVideoTitles.contains(title)) {
                  seenVideoTitles.add(title);
                  allVideoTitles.add(title);
              }
          }

          if (allVideoTitles.size() == previousCountVideo) {
              break;
          }
          previousCountVideo = allVideoTitles.size();

          try {
              WebElement rightArrowVideo = wait.until(
                      ExpectedConditions.elementToBeClickable(rightArrowVideoXpath));

              js.executeScript("arguments[0].scrollIntoView({block: 'center'});", rightArrowVideo);
              Thread.sleep(400);

              try {
                  rightArrowVideo.click();
              } catch (Exception e) {
                  try {
                      actions.moveToElement(rightArrowVideo).click().perform();
                  } catch (Exception ex) {
                      js.executeScript("arguments[0].click();", rightArrowVideo);
                  }
              }

              Thread.sleep(900);

          } catch (Exception e) {
              break;
          }
      }

      // ---------- PRINT VIDEO TITLES ----------
      System.out.println("------ VIDEO TITLES ------");

      for (int i = 0; i < allVideoTitles.size(); i++) {
          System.out.println((i + 1) + ". " + allVideoTitles.get(i));
      }

      System.out.println("✅ Total Videos Collected: " + allVideoTitles.size());
  }


  // ------------  PHOTOS SECTION CODE  ------------------

  // Scroll to PHOTOS section
  boolean isPhotoSectionAvailable = true;

  try {
      WebElement photoSection = wait.until(ExpectedConditions.presenceOfElementLocated(
              By.xpath("//div[@class='upcoming_event photos_sec']//h4[contains(@class,'film_header_title')]")
      ));

      js.executeScript("arguments[0].scrollIntoView(true);", photoSection);
      Thread.sleep(1500);

      js.executeScript("window.scrollBy(0, -200);"); // adjust for header
      Thread.sleep(800);

      System.out.println("✅ Scrolled to PHOTOS section.");
  } catch (Exception e) {
      isPhotoSectionAvailable = false;
      System.out.println("❌ PHOTOS section not available.");
  }

  // Proceed ONLY if section exists
  if (isPhotoSectionAvailable) {

      // XPaths
      By rightArrowPhotoXpath = By.xpath(
              "(//div[contains(@class,'upcoming_event photos_sec')]//div[contains(@class,'slick-next')])[1]"
      );

      By photoImgXpath = By.xpath(
              "//div[contains(@class,'upcoming_event photos_sec')]//div[contains(@class,'event_card')]//img[@alt]"
      );

      // Data holders
      List<String> allPhotoAlts = new ArrayList<>();
      List<String> seenPhotoAlts = new ArrayList<>();

      int previousCountPhotos = 0;

      System.out.println("⏳ Collecting Photos...");

      // ---------- CLICK RIGHT ARROW UNTIL NO NEW PHOTOS ----------
      while (true) {

          List<WebElement> photoImgs = driver.findElements(photoImgXpath);

          for (WebElement img : photoImgs) {

              String altText = img.getAttribute("alt").trim();

              if (altText.isEmpty())
                  continue;

              String src = img.getAttribute("src");

              if (src == null || src.trim().isEmpty())
                  continue;

              if (!seenPhotoAlts.contains(src)) {
                  seenPhotoAlts.add(src);
                  allPhotoAlts.add(src);
              }

          }

          if (allPhotoAlts.size() == previousCountPhotos) {
              break;
          }
          previousCountPhotos = allPhotoAlts.size();

          try {
              WebElement rightArrowPhoto = wait.until(
                      ExpectedConditions.elementToBeClickable(rightArrowPhotoXpath));

              js.executeScript("arguments[0].scrollIntoView({block: 'center'});", rightArrowPhoto);
              Thread.sleep(400);

              try {
                  rightArrowPhoto.click();
              } catch (Exception e) {
                  try {
                      actions.moveToElement(rightArrowPhoto).click().perform();
                  } catch (Exception ex) {
                      js.executeScript("arguments[0].click();", rightArrowPhoto);
                  }
              }

              Thread.sleep(900);

          } catch (Exception e) {
              break;
          }
      }

      // ---------- PRINT PHOTO COUNT ----------
      System.out.println("------ PHOTOS ------");
      System.out.println("✅ Total Photos Collected: " + allPhotoAlts.size());
  }


     
//------------  DISCOVER MORE FILMS SECTION CODE  ------------------

//Scroll to DISCOVER MORE FILMS section
boolean isDiscoverSectionAvailable = true;

try {
   WebElement discoverSection = wait.until(ExpectedConditions.presenceOfElementLocated(
           By.xpath("//div[contains(@class,'upcoming_event discover_film_sec')]")
   ));

   js.executeScript("arguments[0].scrollIntoView(true);", discoverSection);
   Thread.sleep(1500);

   js.executeScript("window.scrollBy(0, -200);"); // adjust for header
   Thread.sleep(800);

   System.out.println("✅ Scrolled to DISCOVER MORE FILMS section.");
} catch (Exception e) {
   isDiscoverSectionAvailable = false;
   System.out.println("❌ DISCOVER MORE FILMS section not available.");
}

//Proceed ONLY if section exists
if (isDiscoverSectionAvailable) {

   // XPaths
   By rightArrowDiscoverXpath = By.xpath(
           "(//div[contains(@class,'upcoming_event discover_film_sec')]//div[contains(@class,'slick-next')])[1]"
   );

   By discoverFilmImgXpath = By.xpath(
           "//div[contains(@class,'upcoming_event discover_film_sec')]//div[contains(@class,'event_card')]//img[@alt]"
   );

   // Data holders
   List<String> allFilmNames = new ArrayList<>();
   List<String> seenFilmNames = new ArrayList<>();

   int previousCountFilms = 0;

   System.out.println("⏳ Collecting Discover More Films titles...");

   // ---------- CLICK RIGHT ARROW UNTIL NO NEW FILMS ----------
   while (true) {

       List<WebElement> filmImgs = driver.findElements(discoverFilmImgXpath);

       for (WebElement img : filmImgs) {

           String filmName = img.getAttribute("alt").trim();

           if (filmName.isEmpty())
               continue;

           if (!seenFilmNames.contains(filmName)) {
               seenFilmNames.add(filmName);
               allFilmNames.add(filmName);
           }
       }

       if (allFilmNames.size() == previousCountFilms) {
           break;
       }
       previousCountFilms = allFilmNames.size();

       try {
           WebElement rightArrowDiscover = wait.until(
                   ExpectedConditions.elementToBeClickable(rightArrowDiscoverXpath));

           js.executeScript("arguments[0].scrollIntoView({block: 'center'});", rightArrowDiscover);
           Thread.sleep(400);

           try {
               rightArrowDiscover.click();
           } catch (Exception e) {
               try {
                   actions.moveToElement(rightArrowDiscover).click().perform();
               } catch (Exception ex) {
                   js.executeScript("arguments[0].click();", rightArrowDiscover);
               }
           }

           Thread.sleep(900);

       } catch (Exception e) {
           break;
       }
   }

   // ---------- PRINT FILM NAMES ----------
   System.out.println("------ DISCOVER MORE FILMS ------");

   for (int i = 0; i < allFilmNames.size(); i++) {
       System.out.println((i + 1) + ". " + allFilmNames.get(i));
   }

   System.out.println("✅ Total Films Collected: " + allFilmNames.size());
}


     
    }
    }
 