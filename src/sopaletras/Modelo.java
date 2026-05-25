package sopaletras;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MODELO - Se encarga de toda la comunicación con la base de datos MariaDB.
 * Contiene los métodos para añadir, eliminar y consultar palabras.
 */
public class Modelo {

    //Datos de conexión a MariaDB
    private static final String URL      = "jdbc:mariadb://localhost:3306/sopaletras";
    private static final String USUARIO  = "root";
    private static final String PASSWORD = "";  

    private Connection conexion;

    //Constructor que abre la conexión al arrancar el Modelo
    public Modelo() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("Conexión a MariaDB establecida correctamente.");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: No se encontró el driver de MariaDB. ¿Añadiste el .jar?");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("ERROR: No se pudo conectar a la BD.");
            e.printStackTrace();
        }
    }

    //Añade una palabra a la tabla 'palabras'
    public boolean añadirPalabra(String palabra) {
        String sql = "INSERT INTO palabras (palabra) VALUES (?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, palabra.toUpperCase());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("ERROR al añadir palabra: " + e.getMessage());
            return false;
        }
    }

    //Elimina una palabra de la tabla por su texto exacto
    public boolean eliminarPalabra(String palabra) {
        String sql = "DELETE FROM palabras WHERE palabra = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, palabra.toUpperCase());
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("ERROR al eliminar palabra: " + e.getMessage());
            return false;
        }
    }

    //Devuelve una lista con TODAS las palabras almacenadas en la BD
    public List<String> obtenerPalabras() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT palabra FROM palabras";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(rs.getString("palabra"));
            }
        } catch (SQLException e) {
            System.err.println("ERROR al consultar palabras: " + e.getMessage());
        }
        return lista;
    }

    //Cierra la conexión con la BD
    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
