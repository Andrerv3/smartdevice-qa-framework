package com.smartdevice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigManager — gestor centralizado de configuración.
 *
 * Patrón usado: SINGLETON
 * ¿Por qué Singleton? Porque solo necesitamos UNA instancia
 * que lea el archivo de propiedades UNA sola vez.
 * Si cada test creara su propia instancia, leeríamos el
 * archivo del disco cientos de veces innecesariamente.
 *
 * En entrevistas: "Usé Singleton para ConfigManager porque
 * garantiza una única lectura del archivo de configuración
 * y acceso global sin estado compartido mutable."
 */
public class ConfigManager {

    // Logger: para registrar eventos importantes
    // Usamos SLF4J que es el estándar de la industria
    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);

    // La instancia única de esta clase
    // "volatile" garantiza visibilidad entre hilos (thread-safety)
    // Importante cuando TestNG ejecuta tests en paralelo
    private static volatile ConfigManager instance;

    // Objeto Properties: es básicamente un Map<String, String>
    // que sabe leer archivos .properties
    private final Properties properties;

    // Nombre del archivo de configuración
    private static final String CONFIG_FILE = "config.properties";

    /**
     * Constructor PRIVADO — nadie puede hacer "new ConfigManager()"
     * desde fuera. Esto es lo que hace al patrón Singleton.
     */
    private ConfigManager() {
        properties = new Properties();
        loadProperties();
    }

    /**
     * Método estático para obtener la instancia única.
     *
     * Usamos "Double-Checked Locking" para thread-safety:
     * - Primera comprobación: evita sincronización innecesaria
     *   cuando la instancia ya existe (caso más común)
     * - synchronized: garantiza que solo UN hilo crea la instancia
     * - Segunda comprobación: por si dos hilos pasaron el primer if
     *   al mismo tiempo antes de que existiera la instancia
     */
    public static ConfigManager getInstance() {
        if (instance == null) {                    // Primera comprobación (sin lock)
            synchronized (ConfigManager.class) {   // Solo un hilo entra aquí a la vez
                if (instance == null) {             // Segunda comprobación (con lock)
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    /**
     * Carga el archivo config.properties desde el classpath.
     *
     * "Classpath" = las carpetas que Java conoce para buscar archivos.
     * Maven añade src/test/resources al classpath automáticamente,
     * por eso podemos buscar "config.properties" directamente
     * sin especificar la ruta completa.
     */
    private void loadProperties() {
        // getResourceAsStream busca el archivo en el classpath
        try (InputStream inputStream =
                     getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {

            if (inputStream == null) {
                throw new RuntimeException(
                        "No se encontró el archivo: " + CONFIG_FILE +
                                ". Asegúrate de que existe en src/test/resources/"
                );
            }

            properties.load(inputStream);
            log.info("Configuración cargada correctamente desde: {}", CONFIG_FILE);

        } catch (IOException e) {
            // Convertimos la excepción checked en unchecked
            // para no obligar a todos los métodos a declarar "throws IOException"
            throw new RuntimeException("Error al cargar configuración: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene una propiedad por su clave.
     *
     * Prioridad (de mayor a menor):
     * 1. Variables de entorno del sistema (para CI/CD)
     * 2. Propiedades del sistema pasadas con -D (ej: mvn test -Dui.browser=firefox)
     * 3. Archivo config.properties
     *
     * Esto nos permite sobreescribir valores en GitHub Actions
     * sin modificar el archivo de configuración.
     */
    public String get(String key) {
        // 1. Busca primero en variables de entorno del sistema operativo
        String envValue = System.getenv(key.toUpperCase().replace(".", "_"));
        if (envValue != null && !envValue.isEmpty()) {
            log.debug("Propiedad '{}' obtenida de variable de entorno", key);
            return envValue;
        }

        // 2. Busca en propiedades del sistema (pasadas con -Dkey=value)
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isEmpty()) {
            log.debug("Propiedad '{}' obtenida de system property", key);
            return systemValue;
        }

        // 3. Busca en el archivo config.properties
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException(
                    "Propiedad no encontrada: '" + key + "'. " +
                            "Verifica que existe en config.properties"
            );
        }

        return value.trim();
    }

    /**
     * Versión con valor por defecto — no lanza excepción si no existe.
     * Útil para propiedades opcionales.
     */
    public String get(String key, String defaultValue) {
        try {
            return get(key);
        } catch (RuntimeException e) {
            log.debug("Propiedad '{}' no encontrada, usando valor por defecto: '{}'",
                    key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Métodos de conveniencia para tipos específicos.
     * Evitan hacer Integer.parseInt() en cada test.
     */
    public int getInt(String key) {
        try {
            return Integer.parseInt(get(key));
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                    "La propiedad '" + key + "' no es un número entero válido"
            );
        }
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public long getLong(String key) {
        try {
            return Long.parseLong(get(key));
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                    "La propiedad '" + key + "' no es un número long válido"
            );
        }
    }
}