package com.smartdevice.ui;

import com.smartdevice.base.BaseTest;
import com.smartdevice.pages.InventoryPage;
import com.smartdevice.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * LoginTest — tests de UI para la funcionalidad de login de SauceDemo.
 *
 * Cada test sigue el patrón AAA:
 * - Arrange: preparar datos y estado inicial
 * - Act:     ejecutar la acción que se testea
 * - Assert:  verificar el resultado esperado
 */
@Epic("UI Testing")
@Feature("Login — SauceDemo")
public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    /**
     * @BeforeMethod: antes de CADA test creamos una nueva
     * instancia de LoginPage y navegamos a la página.
     * Así cada test empieza desde el mismo estado inicial.
     */
    @BeforeMethod(alwaysRun = true)
    public void setupPage() {
        loginPage = new LoginPage(getDriver());
        loginPage.navigate();
    }

    // ═══════════════════════════════════════════
    // TEST 1: Login exitoso con credenciales válidas
    // ═══════════════════════════════════════════

    @Test(groups = {"ui", "smoke"})
    @Story("Login exitoso")
    @Description("Verifica que un usuario válido puede hacer login y accede al inventario")
    @Severity(SeverityLevel.CRITICAL)
    public void login_withValidCredentials_shouldNavigateToInventory() {
        // Arrange
        String username = config.get("ui.username");
        String password = config.get("ui.password");

        // Act
        InventoryPage inventoryPage = loginPage.loginWith(username, password);

        // Assert
        assertTrue(inventoryPage.isLoaded(),
                "La página de inventario debería estar visible tras el login");
        assertEquals(inventoryPage.getPageTitle(), "Products",
                "El título de la página debería ser 'Products'");

        log.info("Login exitoso verificado — usuario: {}", username);
    }

    // ═══════════════════════════════════════════
    // TEST 2: Login fallido con password incorrecta
    // ═══════════════════════════════════════════

    @Test(groups = {"ui", "smoke"})
    @Story("Login fallido — credenciales inválidas")
    @Description("Verifica que credenciales incorrectas muestran mensaje de error")
    @Severity(SeverityLevel.CRITICAL)
    public void login_withInvalidPassword_shouldShowError() {
        // Arrange
        String username = config.get("ui.username");
        String wrongPassword = "wrong_password_123";

        // Act
        loginPage.enterUsername(username)
                .enterPassword(wrongPassword)
                .clickLogin();

        // Assert — verificamos que seguimos en login y hay error
        assertTrue(loginPage.isErrorDisplayed(),
                "Debería mostrarse un mensaje de error");
        assertTrue(loginPage.getErrorMessage().contains("Username and password do not match"),
                "El mensaje de error debería indicar credenciales incorrectas");

        log.info("Mensaje de error verificado correctamente");
    }

    // ═══════════════════════════════════════════
    // TEST 3: Login con campos vacíos
    // ═══════════════════════════════════════════

    @Test(groups = {"ui"})
    @Story("Login fallido — campos vacíos")
    @Description("Verifica que intentar login sin credenciales muestra error de validación")
    @Severity(SeverityLevel.NORMAL)
    public void login_withEmptyFields_shouldShowValidationError() {
        // Act — click en login sin rellenar nada
        loginPage.clickLogin();

        // Assert
        assertTrue(loginPage.isErrorDisplayed(),
                "Debería mostrarse un error de validación");
        assertTrue(loginPage.getErrorMessage().contains("Username is required"),
                "El error debería indicar que el username es requerido");
    }

    // ═══════════════════════════════════════════
    // TEST 4: Login con usuario bloqueado
    // ═══════════════════════════════════════════

    @Test(groups = {"ui"})
    @Story("Login fallido — usuario bloqueado")
    @Description("Verifica que un usuario bloqueado no puede acceder al sistema")
    @Severity(SeverityLevel.NORMAL)
    public void login_withLockedUser_shouldShowLockedError() {
        // SauceDemo tiene un usuario específico para testear este caso
        loginPage.enterUsername("locked_out_user")
                .enterPassword(config.get("ui.password"))
                .clickLogin();

        assertTrue(loginPage.isErrorDisplayed(),
                "Debería mostrarse error para usuario bloqueado");
        assertTrue(loginPage.getErrorMessage().contains("Sorry, this user has been locked out"),
                "El error debería indicar que el usuario está bloqueado");
    }
}