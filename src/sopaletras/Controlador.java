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
    
    //Guardamos las posiciones de cada palabra para mostrar la solución
    private List<String> ultimasPalabras;

    public Controlador() {
        M = new String[FILAS][COLUMNAS];
        H = new int[FILAS][COLUMNAS];
    }

    //Genera la sopa con las palabras que vienen de la BD
    public String[][] generarSopa(List<String> palabras) {
        M = new String[FILAS][COLUMNAS];
        H = new int[FILAS][COLUMNAS];
        inicializaMatrices();
        ultimasPalabras = palabras;

        for (String palabra : palabras) {
            String[] word = palabra.toUpperCase().split("");

            //Si la palabra es más larga que la matriz no se puede colocar
            if (word.length > COLUMNAS && word.length > FILAS) {
                System.out.println("AVISO: La palabra '" + palabra + "' es demasiado larga.");
                continue;
            }

            boolean colocado = false;
            //Probamos todas las orientaciones posibles en orden aleatorio
            //Sin límite arbitrario: paramos cuando se coloca o cuando
            //se han probado todas las combinaciones posibles
            int[] orientaciones = {0, 1, 2, 3, 4, 5, 6, 7};
            mezclar(orientaciones);

            for (int o : orientaciones) {
                if (colocado) break;
                //Intentamos varias posiciones para cada orientación
                for (int intento = 0; intento < FILAS * COLUMNAS; intento++) {
                    switch (o) {
                        case 0: colocado = colocarPalabra0(word); break;
                        case 1: colocado = colocarPalabra1(word); break;
                        case 2: colocado = colocarPalabra2(word); break;
                        case 3: colocado = colocarPalabra3(word); break;
                        case 4: colocado = colocarPalabra4(word); break;
                        case 5: colocado = colocarPalabra5(word); break;
                        case 6: colocado = colocarPalabra6(word); break;
                        case 7: colocado = colocarPalabra7(word); break;
                    }
                    if (colocado) break;
                }
            }

            if (!colocado) {
                System.out.println("AVISO: No se pudo colocar: " + palabra);
            }
        }
        return M;
    }

    //Mezcla un array de orientaciones para probarlas en orden aleatorio
    private void mezclar(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = azar(i + 1);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    //Devuelve la última lista de palabras usada (para mostrar solución)
    public List<String> getUltimasPalabras() {
        return ultimasPalabras;
    }

    public String[][] getMatriz() { return M; }

    //Devuelve un número al azar entre 0 y limite-1
    private int azar(int limite) {
        return (int) Math.floor(Math.random() * limite);
    }

    //Rellena la matriz con letras al azar incluyendo la Ñ
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

    //Dirección 0: izquierda → derecha
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

    //Dirección 2: arriba → abajo
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

    //Dirección 4: derecha → izquierda
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

    //Dirección 6: abajo → arriba
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

    //Dirección 1: diagonal sur-este ↘
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

    //Dirección 5: diagonal sur-este invertida ↘ al revés
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

    //Dirección 3: diagonal sur-oeste ↙
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

    //Dirección 7: diagonal sur-oeste invertida ↙ al revés
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
