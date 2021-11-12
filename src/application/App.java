/**
 * App será la clase que contenga todo el código relacionado con el conexionado
 * del cliente con el servidor.
 */
package application;

import exceptions.ConnectException;
import exceptions.SignInException;
import exceptions.UpdateException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import logic.HiloCierre;
import logic.HiloEntrada;

/**
 * La clase App será la encargada de gestionar el número de usuarios que pueden
 * conectarse
 *
 * @author Jonathan Camacho y Alejandro Gomez
 */
public class App {

    private final static Logger LOGGER = Logger.getLogger(App.class.getName());
    private final static ResourceBundle configFile = ResourceBundle.getBundle("archives.config");
    private static int port;
    private static int max_connections;

    private static int contador = 0;

    /**
     * Método que se encargará de gestionar el acceso de todos aquellos clientes
     * que se conecten al servidor mediante sockets e hilos.
     *
     * @param args
     * @throws ConnectException
     * @throws SignInException
     * @throws UpdateException
     */
    public static void main(String[] args) throws ConnectException, SignInException, UpdateException {
        port = Integer.valueOf(configFile.getString("port"));
        max_connections = Integer.valueOf(configFile.getString("maxServerConnections"));
        boolean max;
        ServerSocket servidor = null;

        try {
            max=false;
            servidor = new ServerSocket(port);
            LOGGER.info("Lado servidor iniciado");

            Socket clienteSocket = null;
            HiloCierre hiloCierre = new HiloCierre();
            hiloCierre.start();
            while (true) {
                clienteSocket = servidor.accept();
                LOGGER.info("Conexion con cliente iniciada");
                contador++;
                LOGGER.info("Comprobacion de si se ha llegado al limite de conexiones simultaneas");
                if (contador <= max_connections) {
                    max = false;
                    LOGGER.info("Creacion de hilo para el control de la peticion del cliente");
                    

                } else {
                    max=true;
                    LOGGER.warning("El servidor esta lleno");
                   

                }
                HiloEntrada hilo = new HiloEntrada(clienteSocket,max);
                    hilo.start();

            }
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Método el cual permite al server controlar el número de hilos en uso.
     *
     */
    public static void desconexion() {
        contador--;
    }

}
