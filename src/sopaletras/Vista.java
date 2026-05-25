package sopaletras;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * VISTA - Interfaz gráfica con Swing.
 * Incluye selección letra a letra, botón solución y botón buscar palabra.
 */
public class Vista extends JFrame {

    private JTextField campoPalabra;
    private JButton btnAñadir;
    private JButton btnEliminar;
    private JButton btnConsultar;
    private JButton btnGenerar;
    private JButton btnLimpiar;
    private JButton btnSolucion;    //Muestra todas las palabras en verde
    private JButton btnBuscar;      //Busca y marca una palabra concreta
    private JList<String> listaPalabras;
    private DefaultListModel<String> modeloLista;
    private JPanel panelSopa;
    private JLabel[][] etiquetas;

    private static final int FILAS    = 15;
    private static final int COLUMNAS = 20;

    //Letras seleccionadas actualmente por el usuario
    private List<JLabel> seleccionadas = new ArrayList<>();
    private String palabraSeleccionada = "";

    //Lista de palabras de la BD (para comprobar aciertos y mostrar solución)
    private List<String> palabrasDB = new ArrayList<>();

    //Matriz actual de la sopa (para buscar palabras)
    private String[][] matrizActual;

    public Vista() {
        setTitle("Sopa de Letras");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(crearPanelIzquierdo(), BorderLayout.WEST);
        add(crearPanelDerecho(),   BorderLayout.CENTER);

        //Bloquea pegar texto en el campo para evitar caracteres inválidos
        campoPalabra.setTransferHandler(null);

        setVisible(true);
    }

    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(15, 15, 15, 10));
        panel.setPreferredSize(new Dimension(200, 0));

        JLabel lblPalabra = new JLabel("Palabra:");
        lblPalabra.setAlignmentX(Component.LEFT_ALIGNMENT);

        campoPalabra = new JTextField();
        campoPalabra.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        campoPalabra.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAñadir   = new JButton("Añadir");
        btnEliminar = new JButton("Eliminar");
        panelBotones.add(btnAñadir);
        panelBotones.add(btnEliminar);

        btnConsultar = new JButton("Consultar Palabras");
        btnConsultar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnConsultar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        //Botón para limpiar la selección actual
        btnLimpiar = new JButton("Limpiar Selección");
        btnLimpiar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLimpiar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        btnLimpiar.addActionListener(e -> limpiarSeleccion());

        //Botón para buscar una palabra concreta en la sopa
        btnBuscar = new JButton("Buscar Palabra");
        btnBuscar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBuscar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        //Botón para mostrar la solución completa
        btnSolucion = new JButton("Ver Solución");
        btnSolucion.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSolucion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        modeloLista   = new DefaultListModel<>();
        listaPalabras = new JList<>(modeloLista);
        JScrollPane scrollLista = new JScrollPane(listaPalabras);
        scrollLista.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollLista.setPreferredSize(new Dimension(180, 200));

        panel.add(lblPalabra);
        panel.add(Box.createVerticalStrut(5));
        panel.add(campoPalabra);
        panel.add(Box.createVerticalStrut(10));
        panel.add(panelBotones);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnConsultar);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btnLimpiar);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btnBuscar);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btnSolucion);
        panel.add(Box.createVerticalStrut(10));
        panel.add(scrollLista);

        return panel;
    }

    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(15, 5, 15, 15));

        btnGenerar = new JButton("Generar Sopa de Letras");
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBtn.add(btnGenerar);
        panel.add(panelBtn, BorderLayout.NORTH);

        panelSopa = new JPanel(new GridLayout(FILAS, COLUMNAS, 2, 2));
        etiquetas = new JLabel[FILAS][COLUMNAS];

        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                JLabel lbl = new JLabel("·", SwingConstants.CENTER);
                lbl.setFont(new Font("Monospaced", Font.BOLD, 14));
                lbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                lbl.setOpaque(true); //Necesario para que el color de fondo funcione

                //Guardamos fila y columna en el nombre para identificar la celda
                lbl.setName(f + "," + c);

                //Al clicar una letra se añade a la selección
                lbl.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        seleccionarLetra(lbl);
                    }
                });

                etiquetas[f][c] = lbl;
                panelSopa.add(lbl);
            }
        }

        JScrollPane scrollSopa = new JScrollPane(panelSopa);
        panel.add(scrollSopa, BorderLayout.CENTER);

        return panel;
    }

    //Lógica de selección letra a letra
    //Si la secuencia forma una palabra de la BD se marca en verde
    private void seleccionarLetra(JLabel lbl) {
        //Si la letra ya está en verde (encontrada) no hacemos nada
        if (lbl.getBackground() == Color.GREEN) return;

        //Marcamos en azul y añadimos a la selección
        lbl.setBackground(Color.CYAN);
        seleccionadas.add(lbl);
        palabraSeleccionada += lbl.getText();

        //Comprobamos si la selección forma una palabra de la BD
        for (String palabra : palabrasDB) {
            if (palabraSeleccionada.equalsIgnoreCase(palabra)) {
                //Palabra encontrada, la marcamos en verde
                for (JLabel l : seleccionadas) {
                    l.setBackground(Color.GREEN);
                }
                seleccionadas.clear();
                palabraSeleccionada = "";
                return;
            }
        }
    }

    //Limpia la selección actual (letras en azul vuelven a su color original)
    private void limpiarSeleccion() {
        for (JLabel l : seleccionadas) {
            l.setBackground(null);
        }
        seleccionadas.clear();
        palabraSeleccionada = "";
    }

    //Busca una palabra en la matriz y la marca en naranja
    public void buscarYMarcarPalabra(String palabra) {
        if (matrizActual == null) return;
        String[] word = palabra.toUpperCase().split("");
        int L = word.length;
        boolean encontrada = false;

        //Recorremos toda la matriz buscando la primera letra
        for (int f = 0; f < FILAS && !encontrada; f++) {
            for (int c = 0; c < COLUMNAS && !encontrada; c++) {
                if (!matrizActual[f][c].equals(word[0])) continue;

                //Probamos las 8 direcciones desde esta celda
                int[][] dirs = {{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{-1,0},{-1,1}};
                for (int[] dir : dirs) {
                    List<int[]> celdas = new ArrayList<>();
                    boolean ok = true;
                    for (int t = 0; t < L; t++) {
                        int nf = f + dir[0] * t;
                        int nc = c + dir[1] * t;
                        if (nf < 0 || nf >= FILAS || nc < 0 || nc >= COLUMNAS) { ok = false; break; }
                        if (!matrizActual[nf][nc].equals(word[t])) { ok = false; break; }
                        celdas.add(new int[]{nf, nc});
                    }
                    if (ok) {
                        //Marcamos las celdas en naranja
                        for (int[] celda : celdas) {
                            etiquetas[celda[0]][celda[1]].setBackground(Color.ORANGE);
                        }
                        encontrada = true;
                        break;
                    }
                }
            }
        }
        if (!encontrada) {
            mostrarMensaje("La palabra '" + palabra + "' no se encontró en la sopa.");
        }
    }

    //Muestra la solución completa marcando todas las palabras en verde
    public void mostrarSolucion() {
        for (String palabra : palabrasDB) {
            buscarYMarcarPalabra(palabra);
            //Marcamos en verde en vez de naranja para la solución completa
        }
        //Recorremos y cambiamos naranja por verde
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                if (etiquetas[f][c].getBackground() == Color.ORANGE) {
                    etiquetas[f][c].setBackground(Color.GREEN);
                }
            }
        }
    }

    //Actualiza las palabras de la BD para poder comprobar aciertos
    public void setPalabrasDB(List<String> palabras) {
        this.palabrasDB = palabras;
    }

    //Guarda la matriz actual para poder buscar palabras en ella
    public void setMatrizActual(String[][] matriz) {
        this.matrizActual = matriz;
    }

    public void mostrarSopa(String[][] matriz) {
        this.matrizActual = matriz; //Guardamos la matriz para búsquedas
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                etiquetas[f][c].setText(matriz[f][c]);
                etiquetas[f][c].setBackground(null); //Resetea colores al generar nueva sopa
                etiquetas[f][c].setForeground(Color.BLACK);
            }
        }
    }

    public void mostrarPalabras(List<String> palabras) {
        modeloLista.clear();
        for (String p : palabras) {
            modeloLista.addElement(p);
        }
    }

    public String getPalabra() {
        return campoPalabra.getText().trim().toUpperCase();
    }

    public String getPalabraSeleccionada() {
        return listaPalabras.getSelectedValue();
    }

    public void limpiarCampo() {
        campoPalabra.setText("");
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    public void addListenerAñadir(ActionListener al)    { btnAñadir.addActionListener(al); }
    public void addListenerEliminar(ActionListener al)  { btnEliminar.addActionListener(al); }
    public void addListenerConsultar(ActionListener al) { btnConsultar.addActionListener(al); }
    public void addListenerGenerar(ActionListener al)   { btnGenerar.addActionListener(al); }
    public void addListenerBuscar(ActionListener al)    { btnBuscar.addActionListener(al); }
    public void addListenerSolucion(ActionListener al)  { btnSolucion.addActionListener(al); }
}
