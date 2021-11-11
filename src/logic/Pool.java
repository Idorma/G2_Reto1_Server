package logic;

import exceptions.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta clase será la encargada de gestionar las conexiones con la base de
 * datos.
 *
 * @author Jonathan Camacho y Alejandro Gomez
 */
public class Pool {

    private static final Logger LOGGER = Logger.getLogger(Pool.class.getName());
    /**
     * Creamos un Stack, para poder almacenar las conexiones y así poder
     * controlarlas.
     */
    private Stack<Connection> poolStack;

    private final ResourceBundle configFile;
    private final String driverBD;
    private final String urlBD;
    private final String userBD;
    private final String contraBD;

    /**
     * Utilizaremos el parametro pool para poder instanciar la clase cuando
     * vayamos a conectarnos a la base de datos.
     */
    private static Pool pool;

    /**
     * Metodo con el que haremos la conexion con la base de datos. Además,
     * añadiremos al Stack la conexión
     *
     * @throws ConnectException
     */
    private Pool() throws ConnectException {
        LOGGER.info("Se obtienen los datos necesarios para abrir la conexion con la BD");
        poolStack = new Stack<>();
        this.configFile = ResourceBundle.getBundle("archives.config");
        this.driverBD = configFile.getString("driver");
        this.urlBD = configFile.getString("con");
        this.userBD = configFile.getString("DBUSER");
        this.contraBD = configFile.getString("DBPASS");
    }


    public static synchronized Pool getInstance() throws ConnectException {
        LOGGER.info("Se instancia la clase Pool");
        if (pool == null) {
            pool = new Pool();
            return pool;
        } else {
            return pool;
        }
    }


    public Connection getConnection() throws ConnectException {
        Connection connection = null;
        if (!poolStack.isEmpty()) {
            LOGGER.info("Se obtiene conexion del pool de conexiones");
            connection = poolStack.pop();
        }else {
            try {
                LOGGER.info("Se crea una nueva conexion en el pool de conexiones");
                connection = (Connection) DriverManager.getConnection(urlBD, userBD, contraBD);
            } catch (SQLException ex) {
                Logger.getLogger(Pool.class.getName()).log(Level.SEVERE, null, ex);
                throw new ConnectException("Error al intentar abrir la BD");
            }
        }
        return connection;
    }


    public void freeConnection(Connection connection) throws ConnectException {
        try {
            if (connection != null && !connection.isClosed()) {
                LOGGER.info("Se retorna conexion al pool de conexiones.");
                poolStack.push(connection);
            } else {
                throw new ConnectException("Se intento liberar una conexion nula fuera del pool.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Pool.class.getName()).log(Level.SEVERE, null, ex);
            throw new ConnectException("Se intento liberar una conexion cerrada fuera del pool.");
        }
    }

    

    public void closeConnection() {
        LOGGER.info("Cerrando pool de conexiones. Todas las conexiones existentes con cerradas.");
        while (!poolStack.isEmpty()) {
            try {
                poolStack.pop().close();
            } catch (SQLException ex) {
                Logger.getLogger(Pool.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
