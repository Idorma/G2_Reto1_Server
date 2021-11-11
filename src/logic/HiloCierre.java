/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import exceptions.ConnectException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hilo para el cerrado del servidor mediante consola. Se prepara un scanner
 * para la lectura, y en el caso de que introduzca el texto correspondiente, se
 * cerraran las conexiones del Pool y se cerrara el programa.
 *
 * @author Jonathan Camacho, Alejandro Gomez y Alain Cosgaya
 */
public class HiloCierre extends Thread {

    private static final Logger LOGGER = Logger.getLogger(HiloEntrada.class.getName());

    @Override
    public void run() {
        LOGGER.info("Se crea scanner para la lectura del texto");
        Scanner sc = new Scanner(System.in);
        System.out.println("Desea cerrar el servidor? (SI/NO)");
        while (true) {
            String close = sc.next();
            LOGGER.info("Comprobacion del texto introducido");
            if (close.equalsIgnoreCase("SI")) {
                LOGGER.info("Cierre de conexiones del pool");
                try {
                    Pool.getInstance().closeConnection();
                } catch (ConnectException ex) {
                    Logger.getLogger(HiloCierre.class.getName()).log(Level.SEVERE, null, ex);
                }
                LOGGER.info("Cierre del programa");
                System.exit(0);
            }

        }
    }
}
