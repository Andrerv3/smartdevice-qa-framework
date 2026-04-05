package com.smartdevice.pages;

import com.smartdevice.config.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * BasePage — clase madre de todos los Page Objects.
 *
 * Centraliza la lógica común de interacción con el browser:
 * waits, clicks, escritura de texto, etc.
 *
 * ¿Por qué centralizar los waits aquí?
 * Selenium es asíncrono — la página puede no haber cargado
 * cuando intentamos interactuar con un elemento.
 * Sin waits correctos los tests son inestables ("flaky").
 *
 * En entrevistas: "Implementé una BasePage con WebDriverWait
 * explícito para evitar tests flaky causados por timing issues."
 */
public abstract class BasePage {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final ConfigManager config;

    /**
     * Constructor — todas las páginas reciben el driver.
     * PageFactory.initElements inicializa los @FindBy de las subclases.
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.config = ConfigManager.getInstance();

        int explicitWait = config.getInt("ui.explicit.wait");
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));

        // Inicializa todos los @FindBy de la subclase automáticamente
        PageFactory.initElements(driver, this);
    }

    // ─────────────────────────────────────────────────
    // MÉTODOS DE INTERACCIÓN — usan wait explícito
    // ─────────────────────────────────────────────────

    /**
     * Espera a que el elemento sea clickable y hace click.
     * "Clickable" = visible + habilitado (no disabled).
     */
    protected void click(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
        log.debug("Click en elemento: {}", element);
    }

    /**
     * Espera a que el elemento sea visible, lo limpia y escribe texto.
     */
    protected void type(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
        log.debug("Texto escrito en elemento: '{}'", text);
    }

    /**
     * Espera a que el elemento sea visible y devuelve su texto.
     */
    protected String getText(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
        return element.getText().trim();
    }

    /**
     * Verifica si un elemento está visible en la página.
     * No lanza excepción si no existe — devuelve false.
     */
    protected boolean isElementVisible(By locator) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Espera a que la URL contenga el fragmento dado.
     * Útil para verificar navegación entre páginas.
     */
    protected void waitForUrlContains(String urlFragment) {
        wait.until(ExpectedConditions.urlContains(urlFragment));
    }

    /**
     * Scroll hasta un elemento usando JavaScript.
     * Útil cuando el elemento está fuera del viewport.
     */
    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Devuelve el título de la página actual.
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Devuelve la URL actual.
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}