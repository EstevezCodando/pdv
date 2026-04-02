package com.pdv.pontovenda.test;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PosDeploySeleniumTest")
class PosDeploySeleniumTest {

    private WebDriver driver;

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Deve validar a navegacao basica da aplicacao publicada")
    void deveValidarAplicacaoPublicada() {
        String baseUrl = System.getenv().getOrDefault("PDV_BASE_URL", "http://127.0.0.1:8080");

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.get(baseUrl);
        assertTrue(driver.getTitle().toLowerCase().contains("pdv") || driver.getPageSource().contains("Ponto de Venda"));

        driver.get(baseUrl + "/usuarios");
        assertTrue(driver.getPageSource().contains("Usuarios") || driver.getPageSource().contains("usu"));

        driver.get(baseUrl + "/produtos");
        assertTrue(driver.getPageSource().contains("Produtos") || driver.getPageSource().contains("prod"));
    }
}
