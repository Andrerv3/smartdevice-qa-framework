package com.smartdevice.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * LoginPage — Page Object para la página de login de SauceDemo.
 *
 * URL: https://www.saucedemo.com
 *
 * @FindBy es la anotación de Selenium PageFactory.
 * En vez de escribir driver.findElement(By.id("user-name"))
 * en cada método, declaramos el elemento una vez aquí
 * y PageFactory lo inicializa automáticamente en el constructor
 * de BasePage.
 *
 * En entrevistas: "Uso @FindBy con PageFactory para declarar
 * los locators como atributos de clase, separando la definición
 * del elemento de su uso en los métodos de interacción."
 */
public class LoginPage extends BasePage {

    // ─── Locators declarados como atributos ───────────
    // Si el ID cambia, solo cambiamos aquí, no en los tests

    @FindBy(id = "user-name")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    // ─── Constructor ──────────────────────────────────

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // ─── Métodos de navegación ────────────────────────

    /**
     * Navega a la página de login.
     * Siempre llama a este método al inicio de un test de login.
     */

    @Step("Navegar a la página de login")
    public LoginPage navigate() {
        String url = config.get("ui.base.url");
        driver.get(url);
        log.info("Navegando a: {}", url);
        return this;
    }

    // ─── Métodos de acción ────────────────────────────

    /**
     * Escribe el username en el campo correspondiente.
     * Devuelve "this" para permitir method chaining:
     * loginPage.enterUsername("user").enterPassword("pass").clickLogin()
     */

    @Step("Introducir username: {username}")
    public LoginPage enterUsername(String username) {
        type(usernameField, username);
        return this;
    }

    @Step("Introducir password")
    public LoginPage enterPassword(String password) {
        type(passwordField, password);
        return this;
    }

    @Step("Hacer click en el botón de login")
    public InventoryPage clickLogin() {
        click(loginButton);
        log.info("Login ejecutado");
        return new InventoryPage(driver);
    }

    /**
     * Método de conveniencia que encapsula el flujo completo de login.
     * Los tests pueden usar este método cuando no necesitan
     * verificar pasos intermedios del login.
     */

    @Step("Login completo con usuario: {username}")
    public InventoryPage loginWith(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }

    // ─── Métodos de verificación ──────────────────────

    public boolean isErrorDisplayed() {
        return isElementVisible(
                org.openqa.selenium.By.cssSelector("[data-test='error']")
        );
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public boolean isLoaded() {
        return isElementVisible(
                org.openqa.selenium.By.id("login-button")
        );
    }
}