package jujava.mediturnos.logica;

public class Helper {
    public static boolean validarTextoVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }


    public static boolean validarDNI(String dniStr) {
        if (dniStr == null) {
            return false;
        }
        try {
            int dni = Integer.parseInt(dniStr.trim());
            return dni > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

