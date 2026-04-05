package com.smartdevice.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Post — modelo de datos para la entidad Post de JSONPlaceholder.
 *
 * La API devuelve JSON así:
 * {
 *   "userId": 1,
 *   "id": 1,
 *   "title": "sunt aut facere...",
 *   "body": "quia et suscipit..."
 * }
 *
 * Esta clase es el "molde" al que Jackson convierte ese JSON.
 *
 * @JsonIgnoreProperties(ignoreUnknown = true):
 * Si la API añade campos nuevos que no están en esta clase,
 * Jackson los ignora en vez de lanzar una excepción.
 * Esto hace el código robusto ante cambios en la API.
 *
 * En entrevistas: "Uso POJOs con Jackson para deserializar
 * las respuestas JSON de la API a objetos Java tipados,
 * lo que permite hacer assertions más expresivos y seguros."
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {

    // @JsonProperty mapea el campo JSON al atributo Java
    // En este caso los nombres coinciden, pero es buena
    // práctica ser explícito — documenta la estructura
    @JsonProperty("userId")
    private int userId;

    @JsonProperty("id")
    private int id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("body")
    private String body;

    // Constructor vacío — necesario para que Jackson
    // pueda crear instancias de esta clase
    public Post() {}

    // Constructor completo — útil para crear Posts en tests
    public Post(int userId, String title, String body) {
        this.userId = userId;
        this.title = title;
        this.body = body;
    }

    // ─── Getters y Setters ───────────────────────────
    // Sin Lombok los escribimos manualmente.
    // En entrevistas esto demuestra que entiendes
    // encapsulación real, no solo anotaciones mágicas.

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    // toString útil para logs y debugging
    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                '}';
    }
}