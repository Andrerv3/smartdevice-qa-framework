package com.smartdevice.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * CartPage — Page Object para la página del carrito de SauceDemo.
 * URL: https://www.saucedemo.com/cart.html
 */
public class CartPage extends BasePage {

    @FindBy(className = "cart_item")
    private List<WebElement> cartItems;

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    @FindBy(id = "continue-shopping")
    private WebElement continueShoppingButton;

    @FindBy(className = "title")
    private WebElement pageTitle;

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        waitForUrlContains("cart");
        return isElementVisible(
                org.openqa.selenium.By.className("cart_list")
        );
    }

    public int getCartItemCount() {
        return cartItems.size();
    }

    public String getPageTitle() {
        return getText(pageTitle);
    }

    public CartPage clickCheckout() {
        click(checkoutButton);
        return this;
    }

    public InventoryPage continueShopping() {
        click(continueShoppingButton);
        return new InventoryPage(driver);
    }
}