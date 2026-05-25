package sopaletras;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * VISTA - Interfaz gráfica con Swing.
 * Incluye selección letra a letra para resolver la sopa.
 */
public class Vista extends JFrame {

    private JTextField campoPalabra;
    private JButton btnAñadir;
    private JButton btnEliminar;
    private JButton btnConsultar;
    private JButton btnGenerar;
    private JButton btnLimpiar;             
    private JList<String> listaPalabras;
    private DefaultListModel<String> modeloLista;
    private JPanel panelSopa;
    private JLabel[][] etiquetas;

    private static final int FILAS    = 15;
    private static final int COLUMNAS = 20;

    //Letras seleccionadas actualmente por el usuario
    private List<JLabel> seleccionadas = new ArrayList<>();
    private String palabraSeleccionada = "";

    //Lista de palabras de la BD (para comprobar si acertó)
    private List<String> palabrasDB = new ArrayList<>();

    public Vista() {
        setTitle("Sopa de Letras");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(crearPanelIzquierdo(), BorderLayout.WEST);
        add(crearPanelDerecho(),   BorderLayout.CENTER);

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

        modeloLista   = new DefaultListModel<>();
        listaPalabras = new JList<>(modeloLista);
        JScrollPane scrollLista = new JScrollPane(listaPalabras);
        scrollLista.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollLista.setPreferredSize(new Dimension(180, 250));

        panel.add(lblPalabra);
        panel.add(Box.createVerticalStrut(5));
        panel.add(campoPalabra);
        panel.add(Box.createVerticalStrut(10));
        panel.add(panelBotones);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnConsultar);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btnLimpiar);
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
                lbl.setOpaque(true); // Necesario para que el color de fondo funcione

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
    private void seleccionarLetra(JLabel lbl) {
        if (lbl.getBackground() == Color.GREEN) return;

        lbl.setBackground(Color.CYAN);
        seleccionadas.add(lbl);
        palabraSeleccionada += lbl.getText();

        for (String palabra : palabrasDB) {
            if (palabraSeleccionada.equalsIgnoreCase(palabra)) {
                for (JLabel l : seleccionadas) {
                    l.setBackground(Color.GREEN);
                }
                seleccionadas.clear();
                palabraSeleccionada = "";
                return;
            }
        }
    }

    //Limpia la selección actual (letras en azul vuelven a blanco)
    private void limpiarSeleccion() {
        for (JLabel l : seleccionadas) {
            l.setBackground(null);
        }
        seleccionadas.clear();
        palabraSeleccionada = "";
    }

    //Métodos públicos usados desde Main
    //Actualiza las palabras de la BD para poder comprobar aciertos
    public void setPalabrasDB(List<String> palabras) {
        this.palabrasDB = palabras;
    }

    public void mostrarSopa(String[][] matriz) {
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                etiquetas[f][c].setText(matriz[f][c]);
                etiquetas[f][c].setBackground(null); // Resetea colores al generar nueva sopa
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
}
