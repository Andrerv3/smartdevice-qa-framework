package com.smartdevice.api;

import io.qameta.allure.Step;
import com.smartdevice.base.BaseTest;
import com.smartdevice.models.Post;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * PostsApiTest — tests para el endpoint /posts de JSONPlaceholder.
 *
 * Anotaciones de Allure para el reporte:
 * @Epic    → módulo grande (ej: "API Testing")
 * @Feature → funcionalidad concreta (ej: "Posts Endpoint")
 * @Story   → caso de uso específico
 *
 * Estas anotaciones organizan los resultados en el reporte
 * de Allure en una jerarquía visual clara.
 */
@Epic("API Testing")
@Feature("Posts Endpoint — JSONPlaceholder")
public class PostsApiTest extends BaseTest {

    // URL base de la API, leída desde config.properties
    private String baseUrl;

    /**
     * @BeforeClass: se ejecuta UNA VEZ antes de todos los
     * tests de ESTA clase (no de todas las clases).
     *
     * Configuramos RestAssured aquí para no repetirlo
     * en cada test.
     */

    @BeforeClass(alwaysRun = true)
    @Step("Configurar cliente REST Assured")
    public void setupApi() {
        baseUrl = config.get("api.base.url");

        RestAssured.baseURI = baseUrl;
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        log.info("API Tests configurados — baseURI: {}", baseUrl);
    }
    // ═══════════════════════════════════════════════════
    // TEST 1: GET /posts — obtener todos los posts
    // ═══════════════════════════════════════════════════

    @Test(groups = {"api", "smoke"})
    @Story("Obtener listado de posts")
    @Description("Verifica que GET /posts devuelve 200 y una lista no vacía de 100 posts")
    @Severity(SeverityLevel.CRITICAL)
    public void getAllPosts_shouldReturn200AndNonEmptyList() {
        log.info("Ejecutando: getAllPosts_shouldReturn200AndNonEmptyList");

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/posts")
                .then()
                // Verificamos el status code
                .statusCode(200)
                // Verificamos que Content-Type es JSON
                .contentType(ContentType.JSON)
                // Verificamos que la lista no está vacía
                .body("$", not(empty()))
                // JSONPlaceholder siempre devuelve exactamente 100 posts
                .body("size()", equalTo(100));

        log.info("Test getAllPosts PASSED");
    }

    // ═══════════════════════════════════════════════════
    // TEST 2: GET /posts/{id} — obtener un post concreto
    // ═══════════════════════════════════════════════════

    @Test(groups = {"api", "smoke"})
    @Story("Obtener post por ID")
    @Description("Verifica que GET /posts/1 devuelve el post correcto con todos sus campos")
    @Severity(SeverityLevel.CRITICAL)
    public void getPostById_shouldReturnCorrectPost() {
        log.info("Ejecutando: getPostById_shouldReturnCorrectPost");

        int postId = 1;

        // Opción A: assertions inline con Hamcrest
        // Más conciso, ideal para verificaciones simples
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", postId)
                .when()
                .get("/posts/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(postId))
                .body("userId", equalTo(1))
                .body("title", notNullValue())
                .body("body", notNullValue());

        log.info("Test getPostById PASSED");
    }

    // ═══════════════════════════════════════════════════
    // TEST 3: GET /posts/{id} — deserializar a objeto Java
    // ═══════════════════════════════════════════════════

    @Test(groups = {"api"})
    @Story("Deserializar respuesta a objeto Java")
    @Description("Verifica que la respuesta JSON se deserializa correctamente al modelo Post")
    @Severity(SeverityLevel.NORMAL)
    public void getPostById_shouldDeserializeToPostObject() {
        log.info("Ejecutando: getPostById_shouldDeserializeToPostObject");

        // Opción B: deserializar a objeto Java
        // Más expresivo, permite lógica de assertions más compleja
        Post post = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/posts/1")
                .then()
                .statusCode(200)
                // .as() convierte el JSON a un objeto Post usando Jackson
                .extract()
                .as(Post.class);

        // Assertions con TestNG — más legibles que Hamcrest para objetos
        assertNotNull(post, "El post no debería ser null");
        assertEquals(post.getId(), 1, "El ID del post debería ser 1");
        assertEquals(post.getUserId(), 1, "El userId debería ser 1");
        assertNotNull(post.getTitle(), "El título no debería ser null");
        assertTrue(post.getTitle().length() > 0, "El título no debería estar vacío");

        log.info("Post deserializado correctamente: {}", post);
    }

    // ═══════════════════════════════════════════════════
    // TEST 4: POST /posts — crear un nuevo post
    // ═══════════════════════════════════════════════════

    @Test(groups = {"api"})
    @Story("Crear nuevo post")
    @Description("Verifica que POST /posts crea un recurso y devuelve 201 con el objeto creado")
    @Severity(SeverityLevel.CRITICAL)
    public void createPost_shouldReturn201AndCreatedPost() {
        log.info("Ejecutando: createPost_shouldReturn201AndCreatedPost");

        // Creamos el objeto que vamos a enviar
        Post newPost = new Post(1, "Mi título de prueba", "Contenido del post de prueba");

        Post createdPost = given()
                .contentType(ContentType.JSON)
                // .body() serializa el objeto Java a JSON automáticamente
                .body(newPost)
                .when()
                .post("/posts")
                .then()
                // 201 Created — código correcto para creación de recursos
                .statusCode(201)
                .body("title", equalTo(newPost.getTitle()))
                .body("body", equalTo(newPost.getBody()))
                .body("userId", equalTo(newPost.getUserId()))
                // JSONPlaceholder asigna id=101 al nuevo recurso
                .body("id", greaterThan(0))
                .extract()
                .as(Post.class);

        assertNotNull(createdPost.getId(), "El post creado debería tener un ID asignado");
        log.info("Post creado correctamente con ID: {}", createdPost.getId());
    }

    // ═══════════════════════════════════════════════════
    // TEST 5: GET /posts/{id} con ID inválido — caso negativo
    // ═══════════════════════════════════════════════════

    @Test(groups = {"api"})
    @Story("Manejo de errores — recurso no encontrado")
    @Description("Verifica que GET /posts/9999 devuelve 404 para un ID inexistente")
    @Severity(SeverityLevel.NORMAL)
    public void getPostById_withInvalidId_shouldReturn404() {
        log.info("Ejecutando: getPostById_withInvalidId_shouldReturn404");

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/posts/9999")
                .then()
                .statusCode(404);

        log.info("Test caso negativo 404 PASSED");
    }

    // ═══════════════════════════════════════════════════
    // TEST 6: DELETE /posts/{id}
    // ═══════════════════════════════════════════════════

    @Test(groups = {"api"})
    @Story("Eliminar post")
    @Description("Verifica que DELETE /posts/1 devuelve 200")
    @Severity(SeverityLevel.NORMAL)
    public void deletePost_shouldReturn200() {
        log.info("Ejecutando: deletePost_shouldReturn200");

        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/posts/1")
                .then()
                .statusCode(200);

        log.info("Test deletePost PASSED");
    }
}