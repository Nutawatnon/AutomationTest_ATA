package org.example.demo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.junit.Test;


import static org.junit.jupiter.api.Assertions.*;

import static com.codeborne.selenide.Condition.attribute;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.text.DecimalFormat;
import java.util.List;
//import org.testng.annotations.Test;

public class MainPageTest {


    WebDriver driver;
    @BeforeTest
    public void setup() {

        System.setProperty("webdriver.chrome.driver", "resources/chromedriver");
        driver = new ChromeDriver();
    }

    public void Login_func(){
        driver = new ChromeDriver();
        driver.get("https://www.saucedemo.com/");
        WebElement username = driver.findElement(By.id("user-name"));
        WebElement password = driver.findElement(By.id("password"));
        username.sendKeys("standard_user");
        password.sendKeys("secret_sauce");
        WebElement LoginButton = driver.findElement(By.id("login-button"));
        LoginButton.click();
    }

    @Test
    public void Test1() {
        //validate login successfully by checking if inventory is displayed.
        Login_func();
        WebElement Inventory = driver.findElement(By.id("inventory_container"));
        assertTrue(Inventory.isDisplayed());

    }

    @Test
    public void Test2() {
        //Test sorting price from high to low and validate numbers.
        Login_func();

        WebElement Select = driver.findElement(By.className("product_sort_container"));
        Select.click();
        WebElement option = driver.findElement(By.xpath("//*[@id=\"header_container\"]/div[2]/div/span/select/option[4]"));
        option.click();
        //
        WebElement inventoryContainer = driver.findElement(By.id("inventory_container"));
        List<WebElement> inventoryItems = inventoryContainer.findElements(By.className("inventory_item"));
        Double previousPrice = null;
        for (WebElement item : inventoryItems) {
            WebElement price = item.findElement(By.className("inventory_item_price"));
            String priceText = price.getText().replace("$", "");
            Double currentPrice = Double.parseDouble(priceText);

            if (previousPrice != null) {
                assertTrue(currentPrice <= previousPrice, "Not list high to low");
            }
            previousPrice = currentPrice;
        }
    }

    @Test
    public void Test3() {
        //add 2 products to the cart and proceed to complete check out items.
        Login_func();
        WebElement addBackPack = driver.findElement(By.id("add-to-cart-sauce-labs-backpack"));
        addBackPack.click();
        WebElement addBikeLight = driver.findElement(By.id("add-to-cart-sauce-labs-bike-light"));
        addBikeLight.click();
        WebElement cart = driver.findElement(By.className("shopping_cart_link"));
        cart.click();
        WebElement checkOut = driver.findElement(By.id("checkout"));
        checkOut.click();
        WebElement firstName = driver.findElement(By.id("first-name"));
        WebElement lastName = driver.findElement(By.id("last-name"));
        WebElement postal = driver.findElement(By.id("postal-code"));
        firstName.sendKeys("Test_Name");
        lastName.sendKeys("Test_LastName");
        postal.sendKeys("1234");
        WebElement continueButton = driver.findElement(By.id("continue"));
        continueButton.click();
        WebElement finish = driver.findElement(By.id("finish"));
        finish.click();
        WebElement complete = driver.findElement(By.className("complete-header"));
        assertEquals("Thank you for your order!", complete.getText());
    }

    @Test
    public void Test4() {
        //check log check out summary page and check total price both before tax and after tax.
        Login_func();
        WebElement addBackPack = driver.findElement(By.id("add-to-cart-sauce-labs-backpack"));
        addBackPack.click();
        WebElement addBikeLight = driver.findElement(By.id("add-to-cart-sauce-labs-bike-light"));
        addBikeLight.click();
        WebElement cart = driver.findElement(By.className("shopping_cart_link"));
        cart.click();
        WebElement checkOut = driver.findElement(By.id("checkout"));
        checkOut.click();
        WebElement firstName = driver.findElement(By.id("first-name"));
        WebElement lastName = driver.findElement(By.id("last-name"));
        WebElement postal = driver.findElement(By.id("postal-code"));
        firstName.sendKeys("Test_Name");
        lastName.sendKeys("Test_LastName");
        postal.sendKeys("1234");
        WebElement continueButton = driver.findElement(By.id("continue"));
        continueButton.click();

        // Find the cart list container
        WebElement cartList = driver.findElement(By.className("cart_list"));

        // Find all cart items
        List<WebElement> cartItems = cartList.findElements(By.className("cart_item"));

        // Initialize total price variable
        double totalPrice = 0.0;

        // Iterate through each cart item to calculate total price
        for (WebElement item : cartItems) {
            // Find the price element for each item
            WebElement priceElement = item.findElement(By.className("inventory_item_price"));
            String priceText = priceElement.getText().replace("$", "");
            double itemPrice = Double.parseDouble(priceText);

            // Add item price to total price
            totalPrice += itemPrice;
        }
        WebElement itemTotal = driver.findElement(By.className("summary_subtotal_label"));
        String Total_before_tax = itemTotal.getText().replace("Item total: $", "");
        double priceTotal = Double.parseDouble(Total_before_tax);
        WebElement afterTaxPrice = driver.findElement(By.className("summary_total_label"));
        String Total_after_tax = afterTaxPrice.getText().replace("Total: $", "");
        double total = Double.parseDouble(Total_after_tax);
        assertEquals(totalPrice, priceTotal );
        double priceWithTax = totalPrice+(totalPrice*0.08);
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedString = df.format(priceWithTax);
        double expectedPrice = Double.parseDouble(formattedString);
        assertEquals(expectedPrice,total);
    }

    @Test
    public void Test5() {
        //validate that Locked_out_user can not login to the system.
        driver = new ChromeDriver();
        driver.get("https://www.saucedemo.com/");
        WebElement username = driver.findElement(By.id("user-name"));
        WebElement password = driver.findElement(By.id("password"));
        username.sendKeys("locked_out_user");
        password.sendKeys("secret_sauce");
        WebElement LoginButton = driver.findElement(By.id("login-button"));
        LoginButton.click();
        WebElement errorMsg = driver.findElement(By.className("error-message-container"));
        assertEquals("Epic sadface: Sorry, this user has been locked out.",errorMsg.getText());
    }

    @AfterTest
    public void teardown() {
            driver.quit();

    }
}