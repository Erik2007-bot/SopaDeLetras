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
        //Valida la entrada y guarda la palabra en BD si es correcta
        vista.addListenerAñadir(e -> {
            String palabra = vista.getPalabra();

            //Validación 1: campo vacío
            if (palabra.isEmpty()) {
                vista.mostrarMensaje("Escribe una palabra antes de añadir.");
                return;
            }
            //Validación 2: solo letras, sin números ni caracteres especiales
            if (!palabra.matches("[A-ZÁÉÍÓÚÜÑ]+")) {
                vista.mostrarMensaje("Solo se permiten letras, sin caracteres especiales.");
                vista.limpiarCampo();
                return;
            }
            //Validación 3: palabra repetida
            if (modelo.existePalabra(palabra)) {
                vista.mostrarMensaje("La palabra '" + palabra + "' ya existe en la BD.");
                vista.limpiarCampo();
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

        //BOTÓN BUSCAR PALABRA
        //Busca la palabra seleccionada en la lista y la marca en naranja
        vista.addListenerBuscar(e -> {
            String seleccionada = vista.getPalabraSeleccionada();
            if (seleccionada == null) {
                vista.mostrarMensaje("Selecciona una palabra de la lista para buscarla.");
                return;
            }
            vista.buscarYMarcarPalabra(seleccionada);
        });

        //BOTÓN VER SOLUCIÓN
        //Marca todas las palabras de la sopa en verde automáticamente
        vista.addListenerSolucion(e -> {
            if (modelo.obtenerPalabras().isEmpty()) {
                vista.mostrarMensaje("Genera una sopa primero.");
                return;
            }
            vista.mostrarSolucion();
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
