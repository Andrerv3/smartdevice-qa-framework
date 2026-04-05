# SmartDevice QA Framework

![QA Pipeline](https://github.com/Andrerv3/smartdevice-qa-framework/actions/workflows/qa-pipeline.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Selenium](https://img.shields.io/badge/Selenium-4.27-green?logo=selenium)
![TestNG](https://img.shields.io/badge/TestNG-7.10-red)
![Maven](https://img.shields.io/badge/Maven-3.9-blue?logo=apachemaven)
![Allure](https://img.shields.io/badge/Allure-2.29-yellow)

Suite profesional de automatización de testing para aplicaciones web y APIs REST.
Diseñada con arquitectura escalable, patrones de diseño enterprise y pipeline CI/CD completo.

📊 **[Ver último reporte Allure](https://Andrerv3.github.io/smartdevice-qa-framework/)**

---

## Índice

- [Descripción](#descripción)
- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Requisitos previos](#requisitos-previos)
- [Instalación y ejecución](#instalación-y-ejecución)
- [Tests implementados](#tests-implementados)
- [CI/CD](#cicd)
- [Patrones de diseño](#patrones-de-diseño)

---

## Descripción

Framework de automatización de QA diseñado para validar la calidad de aplicaciones
web y servicios REST. Implementa las mejores prácticas del sector:

- **Tests de API REST** contra JSONPlaceholder con REST Assured
- **Tests de UI** contra SauceDemo con Selenium WebDriver
- **Reportes visuales** con Allure Reports publicados automáticamente
- **Pipeline CI/CD** con GitHub Actions en cada push
- **Arquitectura por capas** con Page Object Model y patrones enterprise

---

## Tecnologías

| Tecnología | Versión | Propósito |
|---|---|---|
| Java | 21 LTS | Lenguaje principal |
| Selenium WebDriver | 4.27 | Automatización de UI |
| REST Assured | 5.5 | Testing de APIs REST |
| TestNG | 7.10 | Framework de testing |
| Maven | 3.9 | Gestión de dependencias y build |
| Allure Reports | 2.29 | Reportes visuales profesionales |
| WebDriverManager | 5.9 | Gestión automática de drivers |
| GitHub Actions | — | CI/CD pipeline |

---

## Arquitectura
```
┌─────────────────────────────────────┐
│         CI/CD — GitHub Actions      │  ← Ejecución automática en cada push
├─────────────────────────────────────┤
│      Tests — TestNG + Allure        │  ← Orquestación y reportes
├──────────────┬──────────────────────┤
│ Page Objects │    API Clients       │  ← Abstracción de UI y API
├─────────────────────────────────────┤
│    Utilities — Driver, Waits, Logs  │  ← Servicios transversales
├─────────────────────────────────────┤
│    Config — Properties + Entornos   │  ← Configuración desacoplada
└─────────────────────────────────────┘
```

**Patrones implementados:**
- **Page Object Model** — abstracción de páginas UI
- **Singleton** — ConfigManager con thread-safety
- **Factory** — DriverFactory con ThreadLocal para paralelismo
- **Fluent Interface** — method chaining en Page Objects

---

## Estructura del proyecto
```
smartdevice-qa-framework/
├── .github/
│   └── workflows/
│       └── qa-pipeline.yml          # Pipeline CI/CD
├── src/
│   ├── main/java/com/smartdevice/
│   │   ├── config/
│   │   │   └── ConfigManager.java   # Singleton — gestión de configuración
│   │   ├── pages/
│   │   │   ├── BasePage.java        # Clase base con waits y utilidades
│   │   │   ├── LoginPage.java       # Page Object — login SauceDemo
│   │   │   ├── InventoryPage.java   # Page Object — listado de productos
│   │   │   └── CartPage.java        # Page Object — carrito de compras
│   │   ├── models/
│   │   │   └── Post.java            # POJO — modelo de datos API
│   │   └── utils/
│   │       └── DriverFactory.java   # Factory + ThreadLocal WebDriver
│   └── test/
│       ├── java/com/smartdevice/
│       │   ├── base/
│       │   │   └── BaseTest.java    # Setup/teardown + screenshots
│       │   ├── api/
│       │   │   └── PostsApiTest.java # 6 tests REST Assured
│       │   └── ui/
│       │       └── LoginTest.java   # 4 tests Selenium
│       └── resources/
│           ├── config.properties    # Configuración por entorno
│           ├── allure.properties    # Configuración Allure
│           └── testng.xml           # Suite y grupos de tests
└── pom.xml                          # Dependencias y plugins Maven
```

---

## Requisitos previos

- Java 21 LTS ([descargar](https://adoptium.net/))
- Maven 3.9+ ([descargar](https://maven.apache.org/download.cgi))
- Google Chrome (versión estable)
- Allure CLI — `brew install allure` (Mac) / [instrucciones](https://allurereport.org/docs/)

---

## Instalación y ejecución

### Clonar el repositorio
```bash
git clone https://github.com/Andrerv3/smartdevice-qa-framework.git
cd smartdevice-qa-framework
```

### Ejecutar toda la suite
```bash
mvn clean test
```

### Ejecutar solo tests de API
```bash
mvn clean test -Dgroups="api"
```

### Ejecutar solo tests de UI
```bash
mvn clean test -Dgroups="ui"
```

### Ejecutar solo smoke tests
```bash
mvn clean test -Dgroups="smoke"
```

### Ejecutar en modo headless (sin abrir browser)
```bash
mvn clean test -Dui.headless=true
```

### Ejecutar con Firefox
```bash
mvn clean test -Dui.browser=firefox
```

### Generar y ver reporte Allure
```bash
# Ver reporte en tiempo real (abre el browser automáticamente)
allure serve target/allure-results

# Generar reporte estático
allure generate target/allure-results --clean -o target/allure-report
```

---

## Tests implementados

### API Tests — JSONPlaceholder (`/posts`)

| Test | Método | Endpoint | Verificación |
|---|---|---|---|
| `getAllPosts_shouldReturn200AndNonEmptyList` | GET | `/posts` | 200 + lista de 100 elementos |
| `getPostById_shouldReturnCorrectPost` | GET | `/posts/1` | 200 + campos correctos |
| `getPostById_shouldDeserializeToPostObject` | GET | `/posts/1` | Deserialización a POJO |
| `createPost_shouldReturn201AndCreatedPost` | POST | `/posts` | 201 + objeto creado |
| `getPostById_withInvalidId_shouldReturn404` | GET | `/posts/9999` | 404 caso negativo |
| `deletePost_shouldReturn200` | DELETE | `/posts/1` | 200 eliminación |

### UI Tests — SauceDemo

| Test | Escenario | Resultado esperado |
|---|---|---|
| `login_withValidCredentials_shouldNavigateToInventory` | Login válido | Navega a inventario |
| `login_withInvalidPassword_shouldShowError` | Password incorrecta | Muestra error |
| `login_withEmptyFields_shouldShowValidationError` | Campos vacíos | Error de validación |
| `login_withLockedUser_shouldShowLockedError` | Usuario bloqueado | Error específico |

---

## CI/CD

El pipeline se ejecuta automáticamente en cada push a `main` o `develop`:
```
Push → GitHub Actions → Tests (headless) → Allure Report → GitHub Pages
```

**Pasos del pipeline:**
1. Checkout del código
2. Setup Java 21 con caché de dependencias Maven
3. Setup Google Chrome estable
4. Ejecución de la suite completa en modo headless
5. Upload de resultados Allure como artefacto
6. Generación y publicación del reporte en GitHub Pages

---

## Patrones de diseño

### Page Object Model
Cada página de la aplicación tiene su propia clase que encapsula los locators
y las interacciones. Los tests hablan en lenguaje de negocio, no en selectores CSS.

### Singleton — ConfigManager
Una única instancia lee el archivo de configuración. Soporta sobreescritura
mediante variables de entorno para integración con CI/CD.

### Factory + ThreadLocal — DriverFactory
Crea el WebDriver apropiado según configuración y lo asigna a un ThreadLocal,
garantizando aislamiento entre tests en ejecución paralela.

### Fluent Interface
Los Page Objects devuelven `this` o la página siguiente, permitiendo
encadenar acciones: `loginPage.enterUsername("user").enterPassword("pass").clickLogin()`.