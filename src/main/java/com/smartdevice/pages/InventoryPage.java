package com.smartdevice.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * InventoryPage — Page Object para la página de productos de SauceDemo.
 * URL: https://www.saucedemo.com/inventory.html
 */
public class InventoryPage extends BasePage {

    @FindBy(className = "inventory_list")
    private WebElement inventoryList;

    @FindBy(className = "inventory_item")
    private List<WebElement> inventoryItems;

    @FindBy(className = "shopping_cart_link")
    private WebElement cartIcon;

    @FindBy(className = "shopping_cart_badge")
    private WebElement cartBadge;

    @FindBy(css = "[data-test='product-sort-container']")
    private WebElement sortDropdown;

    // El primer botón "Add to cart" de la lista
    @FindBy(css = ".inventory_item:first-child .btn_inventory")
    private WebElement firstAddToCartButton;

    @FindBy(className = "title")
    private WebElement pageTitle;

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    // ─── Métodos de verificación ──────────────────────

    /**
     * Verifica que la página de inventario está cargada.
     * Comprueba URL y presencia de la lista de productos.
     */

    @Step("Verificar que la página de inventario está cargada")
    public boolean isLoaded() {
        waitForUrlContains("inventory");
        return isElementVisible(
                org.openqa.selenium.By.className("inventory_list")
        );
    }

    public int getProductCount() {
        return inventoryItems.size();
    }

    public String getPageTitle() {
        return getText(pageTitle);
    }

    // ─── Métodos de acción ────────────────────────────

    /**
     * Añade el primer producto del listado al carrito.
     */

    @Step("Añadir primer producto al carrito")
    public InventoryPage addFirstProductToCart() {
        click(firstAddToCartButton);
        log.info("Primer producto añadido al carrito");
        return this;
    }

    /**
     * Devuelve el número que aparece en el badge del carrito.
     * El badge solo aparece cuando hay productos en el carrito.
     */
    public int getCartItemCount() {
        try {
            return Integer.parseInt(getText(cartBadge));
        } catch (Exception e) {
            return 0;
        }
    }

    @Step("Ir al carrito")
    public CartPage goToCart() {
        click(cartIcon);
        return new CartPage(driver);
    }
}