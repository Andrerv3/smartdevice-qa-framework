package com.smartdevice.base;


import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import com.smartdevice.config.ConfigManager;
import com.smartdevice.utils.DriverFactory;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public class BaseTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final ConfigManager config = ConfigManager.getInstance();

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        log.info("═══════════════════════════════════════════");
        log.info("  SmartDevice QA Framework — Iniciando    ");
        log.info("  Entorno: {}", config.get("environment"));
        log.info("═══════════════════════════════════════════");
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        log.info("═══════════════════════════════════════════");
        log.info("  SmartDevice QA Framework — Finalizado   ");
        log.info("═══════════════════════════════════════════");
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(java.lang.reflect.Method method) {
        log.info("─── Iniciando test: {} ───", method.getName());

        // Añade información del test al reporte de Allure
        Allure.description("Test ejecutado en entorno: " + config.get("environment"));
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            log.error("Test FALLIDO: {}", result.getName());
            log.error("Causa: {}", result.getThrowable().getMessage());

            if (DriverFactory.isDriverActive()) {
                // El screenshot se adjunta automáticamente al reporte
                takeScreenshot(result.getName());

                // Adjunta también la URL donde falló
                Allure.addAttachment("URL al fallar",
                        "text/plain",
                        DriverFactory.getDriver().getCurrentUrl());
            }

            // Adjunta el mensaje de error al reporte
            Allure.addAttachment("Error",
                    "text/plain",
                    result.getThrowable().getMessage());

        } else if (result.getStatus() == ITestResult.SUCCESS) {
            log.info("Test EXITOSO: {}", result.getName());
        } else if (result.getStatus() == ITestResult.SKIP) {
            log.warn("Test SALTADO: {}", result.getName());
        }

        DriverFactory.quitDriver();
    }

    protected WebDriver getDriver() {
        return DriverFactory.getDriver();
    }

    @Attachment(value = "Screenshot", type = "image/png")
    protected byte[] takeScreenshot(String testName) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver instanceof TakesScreenshot screenshotDriver) {
                return screenshotDriver.getScreenshotAs(OutputType.BYTES);
            }
        } catch (Exception e) {
            log.warn("No se pudo capturar screenshot: {}", e.getMessage());
        }
        return new byte[0];
    }

    private void takeScreenshotOnFailure() {
        if (config.getBoolean("screenshot.on.failure")) {
            takeScreenshot("failure");
        }
    }
}