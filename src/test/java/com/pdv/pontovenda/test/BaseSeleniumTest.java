package com.pdv.pontovenda.test;

import com.pdv.pontovenda.repository.ProdutoRepository;
import com.pdv.pontovenda.repository.UsuarioRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Classe base para testes Selenium.
 * Configura o WebDriver com Chrome headless, inicializa o servidor Spring Boot
 * em porta aleatoria e fornece acesso a URL base e repositorios para setup de dados.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseSeleniumTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected UsuarioRepository usuarioRepository;

    @Autowired
    protected ProdutoRepository produtoRepository;

    protected WebDriver driver;

    @BeforeAll
    static void configurarDriverGlobal() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void inicializarDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);

        // Limpa o banco antes de cada teste
        produtoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @AfterEach
    void fecharDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    /** Retorna a URL base do servidor de testes. */
    protected String baseUrl() {
        return "http://localhost:" + port;
    }

    /** Navega para uma rota relativa a URL base. */
    protected void navegarPara(String rota) {
        driver.get(baseUrl() + rota);
    }
}
