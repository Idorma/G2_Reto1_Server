package logic;

import classes.Signable;
import exceptions.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import classes.UserInfo;
import classes.User;
import java.sql.Connection;

/**
 * La implementacion del DAO con la cual se ejecutan las acciones solicitadas
 * por el usuario
 *
 * @author Alain Cosgaya y Alejandro Gomez
 */
public class DaoImplementation implements Signable{
    
    private Connection con = null;
    private PreparedStatement stmt = null;
    private final String signInUser = "CALL `login_validator`(?,?)";
    private final String signUpUser = "CALL `register_validator`(?,?,?,?)";

    /**
     * Metodo que comprueba a traves de un procedimiento almacenado si los
     * campos introducidos por el usuario son correctos. En el caso de que sean
     * correctos, a traves de otro procedimiento almacenado se actualizara la
     * tabla del registro de inicios de sesion, controlando el limite de
     * registros guardados.
     *
     * @param message Los datos introducidos por el usuario por validar.
     *
     * @return Objeto tipo UserInfo con los datos del usuario y un mensaje de
     * tipo enum con lo ocurrido a lo largo de la ejecucion del metodo.
     *
     * @throws ConnectException
     *
     * @throws SignInException
     *
     * @throws UpdateException
     */
    @Override
    public User signIn(User message) throws ConnectException, SignInException, UpdateException {
        User user;
        ResultSet rs = null;
        //try {
            con = Pool.getInstance().getConnection();
        /*} catch (SQLException e) {
            throw new ConnectException("Error al intentar conectarse al servidor, intentelo mas tarde");
        }*/
        try {

            stmt = con.prepareStatement(signInUser);
            stmt.setString(1, message.getUsername());
            stmt.setString(2, message.getPassword());
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getLong("users.id"));
                user.setUsername(rs.getString("users.login"));
                user.setEmail(rs.getString("users.email"));
                user.setFullName(rs.getString("users.fullName"));
                user.setPassword(rs.getString("users.password"));
                
            } else {
                throw new SignInException("Los parametros introducidos no corresponden a ningún cliente");
            }
        } catch (SQLException e) {
            throw new UpdateException("Error al intentar verificar los parametros en la base de datos");

        }
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new ConnectException("Error al intentar cerrar la conexion al servidor, intentelo mas tarde");
            }
            

        }
        return user;
    }

    /**
     * Metodo que registra al usuario en la base de datos en el caso de que los
     * valores de login y email no esten ya en uso. En el caso de que no esten
     * en uso, se registrara el usuario en la base de datos, otro procedimiento
     * almacenado se actualizara la tabla del registro de inicios de sesion,
     * controlando el limite de registros guardados.
     *
     * @param message Los datos introducidos por el usuario para su validacion y
     * registro.
     *
     * @return Objeto tipo UserInfo con los datos del usuario y un mensaje de
     * tipo enum con lo ocurrido a lo largo de la ejecucion del metodo.
     * 
     * @throws ConnectException
     * 
     * @throws SignUpException
     *
     * @throws UpdateException
     */
    @Override
    public User signUp(User message) throws ConnectException, SignUpException, UpdateException {
        boolean existe;
        ResultSet rs = null;
       /* try {
            
        } catch (SQLException e) {
            throw new ConnectException("Error al intentar conectarse al servidor, intentelo mas tarde");
        }*/
       con = Pool.getInstance().getConnection();
        try {

            stmt = con.prepareStatement(signUpUser);
            stmt.setString(1, message.getUsername());
            stmt.setString(2, message.getEmail());
            stmt.setString(3, message.getFullName());
            stmt.setString(4, message.getPassword());
            rs = stmt.executeQuery();
            existe = rs.getBoolean("existe");
            if (existe) {
                throw new SignUpException("El nombre de usuario o email corresponden a otro cliente");
              
            }

        } catch (SQLException e) {
            throw new UpdateException("Error al intentar registrar la conexión en la base de datos");
        }
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new ConnectException("Error al intentar cerrar la conexion al servidor, intentelo mas tarde");
            }
        }
        return message;

    }

}