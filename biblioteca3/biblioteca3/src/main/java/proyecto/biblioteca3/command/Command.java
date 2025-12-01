package proyecto.biblioteca3.command;

/**
 * Interfaz Command que define el método ejecutar para los comandos (Patrón
 * Command).
 * Cada implementación realiza una acción concreta (agregar, prestar, devolver,
 * registrar).
 */
public interface Command<T> {
    /**
     * Ejecuta la acción del comando.
     * 
     * @return El resultado de la ejecución
     */
    T ejecutar();
}
