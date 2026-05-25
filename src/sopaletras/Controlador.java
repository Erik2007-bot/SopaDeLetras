package sopaletras;

import java.util.List;

/**
 * CONTROLADOR - Contiene toda la lógica para generar la sopa de letras.
 * Recibe las palabras del Modelo y devuelve la matriz a la Vista.
 */
public class Controlador {

    private static final int FILAS    = 15;
    private static final int COLUMNAS = 20;

    private String[][] M;
    private int[][]    H;

    public Controlador() {
        M = new String[FILAS][COLUMNAS];
        H = new int[FILAS][COLUMNAS];
    }

    // Genera la sopa con las palabras que vienen de la BD
    public String[][] generarSopa(List<String> palabras) {
        M = new String[FILAS][COLUMNAS];
        H = new int[FILAS][COLUMNAS];
        inicializaMatrices();

        for (String palabra : palabras) {
            String[] word = palabra.toUpperCase().split("");
            boolean colocado = false;
            int intentos = 0;
            while (!colocado && intentos < 100) {
                int orientacion = azar(8);
                switch (orientacion) {
                    case 0: colocado = colocarPalabra0(word); break;
                    case 1: colocado = colocarPalabra1(word); break;
                    case 2: colocado = colocarPalabra2(word); break;
                    case 3: colocado = colocarPalabra3(word); break;
                    case 4: colocado = colocarPalabra4(word); break;
                    case 5: colocado = colocarPalabra5(word); break;
                    case 6: colocado = colocarPalabra6(word); break;
                    case 7: colocado = colocarPalabra7(word); break;
                }
                intentos++;
            }
        }
        return M;
    }

    public String[][] getMatriz() { return M; }

    private int azar(int limite) {
        return (int) Math.floor(Math.random() * limite);
    }

    private void inicializaMatrices() {
        String cadena = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ";
        String[] letra = cadena.split("");
        for (int f = 0; f < M.length; f++) {
            for (int c = 0; c < M[0].length; c++) {
                M[f][c] = letra[azar(letra.length)];
                H[f][c] = 0;
            }
        }
    }

    private boolean colocarPalabra0(String[] word) {
        int f = azar(M.length);
        int L = word.length;
        if (M[0].length - L + 1 <= 0) return false;
        int c = azar(M[0].length - L + 1);
        boolean permitido = true;
        for (int t = 0; t < L; t++) {
            if (H[f][c + t] == 1 && !M[f][c + t].equals(word[t])) permitido = false;
        }
        if (permitido) {
            for (int t = 0; t < L; t++) { M[f][c + t] = word[t]; H[f][c + t] = 1; }
        }
        return permitido;
    }

    private boolean colocarPalabra2(String[] word) {
        int L = word.length;
        if (M.length - L + 1 <= 0) return false;
        int f = azar(M.length - L + 1);
        int c = azar(M[0].length);
        boolean permitido = true;
        for (int t = 0; t < L; t++) {
            if (H[f + t][c] == 1 && !M[f + t][c].equals(word[t])) permitido = false;
        }
        if (permitido) {
            for (int t = 0; t < L; t++) { M[f + t][c] = word[t]; H[f + t][c] = 1; }
        }
        return permitido;
    }

    private boolean colocarPalabra4(String[] word) {
        int f = azar(M.length);
        int L = word.length;
        if (M[0].length - L + 1 <= 0) return false;
        int c = azar(M[0].length - L + 1);
        boolean permitido = true;
        for (int t = 0; t < L; t++) {
            if (H[f][c + t] == 1 && !M[f][c + t].equals(word[L - t - 1])) permitido = false;
        }
        if (permitido) {
            for (int t = 0; t < L; t++) { M[f][c + t] = word[L - t - 1]; H[f][c + t] = 1; }
        }
        return permitido;
    }

    private boolean colocarPalabra6(String[] word) {
        int L = word.length;
        if (M.length - L + 1 <= 0) return false;
        int f = azar(M.length - L + 1);
        int c = azar(M[0].length);
        boolean permitido = true;
        for (int t = 0; t < L; t++) {
            if (H[f + t][c] == 1 && !M[f + t][c].equals(word[L - t - 1])) permitido = false;
        }
        if (permitido) {
            for (int t = 0; t < L; t++) { M[f + t][c] = word[L - t - 1]; H[f + t][c] = 1; }
        }
        return permitido;
    }

    private boolean colocarPalabra1(String[] word) {
        int L = word.length;
        int f = azar(M.length);
        int c = azar(M[0].length);
        boolean permitido = true;
        for (int t = 0; t < L; t++) {
            if ((f + t >= M.length) || (c + t >= M[0].length)) permitido = false;
            else if (H[f + t][c + t] == 1 && !M[f + t][c + t].equals(word[t])) permitido = false;
        }
        if (permitido) {
            for (int t = 0; t < L; t++) { M[f + t][c + t] = word[t]; H[f + t][c + t] = 1; }
        }
        return permitido;
    }

    private boolean colocarPalabra5(String[] word) {
        int L = word.length;
        int f = azar(M.length);
        int c = azar(M[0].length);
        boolean permitido = true;
        for (int t = 0; t < L; t++) {
            if ((f + t >= M.length) || (c + t >= M[0].length)) permitido = false;
            else if (H[f + t][c + t] == 1 && !M[f + t][c + t].equals(word[L - t - 1])) permitido = false;
        }
        if (permitido) {
            for (int t = 0; t < L; t++) { M[f + t][c + t] = word[L - t - 1]; H[f + t][c + t] = 1; }
        }
        return permitido;
    }

    private boolean colocarPalabra3(String[] word) {
        int L = word.length;
        int f = azar(M.length);
        int c = azar(M[0].length);
        boolean permitido = true;
        for (int t = 0; t < L; t++) {
            if ((f + t >= M.length) || (c - t < 0)) permitido = false;
            else if (H[f + t][c - t] == 1 && !M[f + t][c - t].equals(word[t])) permitido = false;
        }
        if (permitido) {
            for (int t = 0; t < L; t++) { M[f + t][c - t] = word[t]; H[f + t][c - t] = 1; }
        }
        return permitido;
    }

    private boolean colocarPalabra7(String[] word) {
        int L = word.length;
        int f = azar(M.length);
        int c = azar(M[0].length);
        boolean permitido = true;
        for (int t = 0; t < L; t++) {
            if ((f + t >= M.length) || (c - t < 0)) permitido = false;
            else if (H[f + t][c - t] == 1 && !M[f + t][c - t].equals(word[L - t - 1])) permitido = false;
        }
        if (permitido) {
            for (int t = 0; t < L; t++) { M[f + t][c - t] = word[L - t - 1]; H[f + t][c - t] = 1; }
        }
        return permitido;
    }
}
