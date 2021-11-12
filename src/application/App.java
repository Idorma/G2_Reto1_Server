/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import classes.User;
import exceptions.ConnectException;
import exceptions.SignInException;
import exceptions.UpdateException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import logic.DaoFactory;
import logic.DaoImplementation;
import logic.HiloEntrada;

/**
 *
 * @author 2dam
 */
public class App {

    /**
     * @param args the command line arguments
     */
    private final static int port = 5001;
    
    public static void main(String[] args) throws ConnectException, SignInException, UpdateException {
        // TODO code application logic here
        
     /*   DaoFactory facto = new DaoFactory();
        User user = new User();
        user.setUsername("alain");
        user.setPassword("abcd*1234");
        
            facto.getDao().signIn(user);
        
        */

        ServerSocket servidor = null;
        
        try {
            servidor = new ServerSocket(port);
            System.out.println("Servidor iniciado");
            
            Socket clienteSocket = null;
            
            while(true){
                clienteSocket=servidor.accept();
                System.out.println("Cliente conectado");
                
                
                
                HiloEntrada hilo = new HiloEntrada(clienteSocket);
                hilo.start();
            }
            
            
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
