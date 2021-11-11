/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import application.App;
import classes.MessageType;
import classes.User;
import classes.UserInfo;
import exceptions.ConnectException;
import exceptions.ServerFullException;
import exceptions.SignInException;
import exceptions.SignUpException;
import exceptions.UpdateException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static logic.DaoFactory.getDao;

/**
 * Hilo del lado servidor para el control y ejecucion de peticiones del cliente.
 *
 * @author Jonathan Camacho, Alejandro Gomez y Alain Cosgaya
 */
public class HiloEntrada extends Thread {
    
    private static final Logger LOGGER = Logger.getLogger(HiloEntrada.class.getName());
    Socket so;
    boolean max;

    /**
     * Constructor del hilo.
     *
     * @param so
     */
    public HiloEntrada(Socket so, boolean max) {
        this.so = so;
        this.max = max;
        
    }

    /**
     * Ejecucion del proceso del hilo. Se hara una lectura de la peticion del
     * cliente, y segun el mensaje recibido se ejecutara el metodo
     * correspondiente a dicho mensaje. Despues de la ejecucion de esto, se
     * devolvera al lado cliente la variable UserInfo con el resultado de la
     * peticion.
     */
    @Override
    public void run() {
        UserInfo userResponse = new UserInfo();
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        
        try {
            UserInfo userInfo;
            User user = null;
            LOGGER.info("Prepara la lectura y escritura de objetos con el lado cliente");
            in = new ObjectInputStream(so.getInputStream());//recibir mensajes
            out = new ObjectOutputStream(so.getOutputStream());
            LOGGER.info("Comprobacion del hilo de si el servidor esta lleno");
            if (!max) {
                LOGGER.info("Recibe el objeto enviado desde el lado cliente");
                userInfo = (UserInfo) in.readObject();
                
                LOGGER.info("Comprueba la peticion hecha por el cliente");
                if (userInfo.getMessage() == MessageType.SIGNIN_REQUEST) {
                    LOGGER.info("Peticion de inicio de sesion");
                    user = getDao().signIn(userInfo.getUser());
                    LOGGER.info("Peticion completada exitosamente");
                    userResponse.setMessage(MessageType.SIGNIN_OK);
                    
                }
                if (userInfo.getMessage() == MessageType.SIGNUP_REQUEST) {
                    LOGGER.info("Peticion de registro");
                    user = getDao().signUp(userInfo.getUser());
                    LOGGER.info("Peticion completada exitosamente");
                    userResponse.setMessage(MessageType.SIGNUP_OK);
                    
                }
                userResponse.setUser(user);
            } else {
                
                throw new ServerFullException("El servidor esta lleno");
            }
            
        } catch (IOException ex) {
            Logger.getLogger(HiloEntrada.class.getName()).log(Level.SEVERE, null, ex);
        }  finally {
            App.desconexion();
            try {
                out.close();
                in.close();
                so.close();
            } catch (IOException ex) {
                Logger.getLogger(HiloEntrada.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.interrupt();
        }
        
    }
}
