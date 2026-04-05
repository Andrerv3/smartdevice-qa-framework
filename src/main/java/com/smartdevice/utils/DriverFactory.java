package com.smartdevice.utils;

import com.smartdevice.config.ConfigManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * DriverFactory — gestiona la creación y ciclo de vida del WebDriver.
 *
 * Patrón usado: FACTORY + THREADLOCAL
 *
 * ¿Por qué ThreadLocal?
 * TestNG puede ejecutar tests en PARALELO (varios tests a la vez).
 * Si todos los tests comparten el mismo WebDriver, se pisarían unos a otros.
 * ThreadLocal garantiza que cada hilo (cada test en paralelo) tiene
 * SU PROPIO WebDriver independiente.
 *
 * En entrevistas: "Usé ThreadLocal para el WebDriver para soportar
 * ejecución paralela sin condiciones de carrera entre tests."
 */
public class DriverFactory {

    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);

    // ThreadLocal: cada hilo tiene su propia copia del WebDriver
    // InheritableThreadLocal permite que hilos hijos hereden el valor
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private static final ConfigManager config = ConfigManager.getInstance();

    // Constructor privado — esta clase solo tiene métodos estáticos
    // No tiene sentido instanciarla
    private DriverFactory() {}

    /**
     * Inicializa y devuelve el WebDriver según la configuración.
     * Si ya existe uno para este hilo, lo devuelve directamente.
     */
    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            driverThreadLocal.set(createDriver());
        }
        return driverThreadLocal.get();
    }

    /**
     * Crea un nuevo WebDriver según el browser configurado.
     * Aquí aplica el patrón Factory: el llamador no sabe
     * qué tipo concreto de driver se crea, solo lo usa.
     */
    private static WebDriver createDriver() {
        String browser = config.get("ui.browser").toLowerCase();
        boolean headless = config.getBoolean("ui.headless");

        log.info("Iniciando WebDriver — browser: {}, headless: {}", browser, headless);

        WebDriver driver = switch (browser) {
            case "chrome" -> createChromeDriver(headless);
            case "firefox" -> createFirefoxDriver(headless);
            default -> throw new RuntimeException(
                    "Browser no soportado: '" + browser + "'. " +
                            "Valores válidos: chrome, firefox"
            );
        };

        configureDriver(driver);
        log.info("WebDriver iniciado correctamente");
        return driver;
    }

    /**
     * Crea ChromeDriver con opciones profesionales.
     * Estas opciones evitan problemas comunes en CI/CD.
     */
    private static WebDriver createChromeDriver(boolean headless) {
        // WebDriverManager descarga y configura chromedriver automáticamente
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        if (headless) {
            // Modo sin ventana — necesario en servidores CI/CD
            // que no tienen pantalla (como GitHub Actions)
            options.addArguments("--headless=new");
        }

        // Opciones estándar para entornos CI/CD y estabilidad
        options.addArguments("--no-sandbox");          // Necesario en Linux CI
        options.addArguments("--disable-dev-shm-usage"); // Evita crasheos en Docker
        options.addArguments("--disable-gpu");          // Estabilidad en headless
        options.addArguments("--window-size=1920,1080"); // Tamaño estándar HD
        options.addArguments("--disable-extensions");   // Sin extensiones en tests
        options.addArguments("--disable-notifications"); // Sin popups de notificaciones

        return new ChromeDriver(options);
    }

    /**
     * Crea FirefoxDriver — útil para cross-browser testing.
     */
    private static WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions options = new FirefoxOptions();

        if (headless) {
            options.addArguments("--headless");
        }

        options.addArguments("--width=1920");
        options.addArguments("--height=1080");

        return new FirefoxDriver(options);
    }

    /**
     * Configura timeouts del driver.
     * Estos se aplican independientemente del browser elegido.
     */
    private static void configureDriver(WebDriver driver) {
        int implicitWait = config.getInt("ui.implicit.wait");
        int pageLoadTimeout = config.getInt("ui.page.load.timeout");

        // ImplicitWait: tiempo que Selenium espera antes de
        // lanzar NoSuchElementException si no encuentra un elemento
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(implicitWait));

        // PageLoadTimeout: tiempo máximo para que cargue una página
        driver.manage().timeouts()
                .pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));

        // Maximizar ventana para consistencia visual
        driver.manage().window().maximize();
    }

    /**
     * Indica si hay un WebDriver activo para el hilo actual.
     * Usado por BaseTest para saber si capturar screenshot.
     * No crea driver si no existe — solo consulta.
     */
    public static boolean isDriverActive() {
        return driverThreadLocal.get() != null;
    }

    /**
     * Cierra y elimina el WebDriver del hilo actual.
     * SIEMPRE debe llamarse al final de cada test (en @AfterMethod).
     * Si no se llama, el browser queda abierto y hay memory leaks.
     */
    public static void quitDriver() {
        // get() devuelve null si no hay driver para este hilo
        // NO llamamos a getDriver() porque eso crearía uno nuevo
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                log.info("Cerrando WebDriver");
                driver.quit();
            } catch (Exception e) {
                log.warn("Error al cerrar WebDriver: {}", e.getMessage());
            } finally {
                // Siempre eliminamos la referencia aunque falle el quit
                driverThreadLocal.remove();
            }
        }
        // Si driver == null, no hacemos nada — test de API, no hay browser
    }
}