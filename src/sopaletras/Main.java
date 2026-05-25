package sopaletras;

import java.util.List;

/**
 * MAIN - Punto de entrada de la aplicación.
 * Instancia las tres clases MVC y conecta los eventos de la Vista
 * con las operaciones del Modelo y el Controlador.
 */
public class Main {

    public static void main(String[] args) {

        //Instanciamos las tres clases del patrón MVC
        Modelo      modelo      = new Modelo();
        Controlador controlador = new Controlador();
        Vista       vista       = new Vista();

        //BOTÓN AÑADIR
        //Lee la palabra del campo, la guarda en BD y refresca la lista
        vista.addListenerAñadir(e -> {
            String palabra = vista.getPalabra();
            if (palabra.isEmpty()) {
                vista.mostrarMensaje("Escribe una palabra antes de añadir.");
                return;
            }
            boolean ok = modelo.añadirPalabra(palabra);
            if (ok) {
                vista.mostrarMensaje("Palabra '" + palabra + "' añadida correctamente.");
                vista.limpiarCampo();
                vista.mostrarPalabras(modelo.obtenerPalabras()); //Refresca la lista
            } else {
                vista.mostrarMensaje("Error al añadir la palabra.");
            }
        });

        //BOTÓN ELIMINAR
        //Lee la palabra seleccionada en la lista y la borra de la BD
        vista.addListenerEliminar(e -> {
            String seleccionada = vista.getPalabraSeleccionada();
            if (seleccionada == null) {
                vista.mostrarMensaje("Selecciona una palabra de la lista para eliminar.");
                return;
            }
            boolean ok = modelo.eliminarPalabra(seleccionada);
            if (ok) {
                vista.mostrarMensaje("Palabra '" + seleccionada + "' eliminada.");
                vista.mostrarPalabras(modelo.obtenerPalabras()); //Refresca la lista
            } else {
                vista.mostrarMensaje("Error al eliminar la palabra.");
            }
        });

        //BOTÓN CONSULTAR
        //Pide todas las palabras al Modelo y las muestra en la lista
        vista.addListenerConsultar(e -> {
            List<String> palabras = modelo.obtenerPalabras();
            vista.mostrarPalabras(palabras);
            if (palabras.isEmpty()) {
                vista.mostrarMensaje("No hay palabras en la base de datos.");
            }
        });

        //BOTÓN GENERAR SOPA
        //Modelo → palabras → Controlador → matriz → Vista
        //También pasa las palabras a la Vista para comprobar aciertos al jugar
        vista.addListenerGenerar(e -> {
            List<String> palabras = modelo.obtenerPalabras();
            if (palabras.isEmpty()) {
                vista.mostrarMensaje("Añade palabras a la BD antes de generar la sopa.");
                return;
            }
            String[][] matriz = controlador.generarSopa(palabras);
            vista.mostrarSopa(matriz);
            vista.setPalabrasDB(palabras); //Palabras para comprobar aciertos
        });

        //Cerramos la conexión a la BD al cerrar la ventana
        vista.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                modelo.cerrarConexion();
            }
        });
    }
}
