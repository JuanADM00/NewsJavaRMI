import java.io.Serializable;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
/*import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;
import com.mysql.cj.jdbc.MysqlDataSource;*/
public class ConexionDB implements IController, Serializable{
    protected Connection conn;
    protected Statement sentencia;
    protected ResultSet resultSet;
    //protected String driver, user, password, url;
    protected static final String driver = "com.mysql.cj.jdbc.Driver", user = "root", password = "Jpad18UPB*",
            url = "jdbc:mysql://localhost:3306/AMAYANEWS";
    protected List<News> newsList = null;
    //protected DataSource dataSource;

    /*private DataSource getMySQLDataSource() {
		Properties props = new Properties();
		FileInputStream fis = null;
		DataSource mysqlDS = null;
		try {
			fis = new FileInputStream("dbProperties.properties");
			props.load(fis);
			driver = props.getProperty("MYSQL_DB_DRIVER_CLASS");
            url = props.getProperty("MYSQL_DB_URL");
			user = props.getProperty("MYSQL_DB_USERNAME");
			password = props.getProperty("MYSQL_DB_PASSWORD");
            mysqlDS = new MysqlDataSource();
			mysqlDS.setURL(url);
			mysqlDS.setUser(user);
			mysqlDS.setPassword(password);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mysqlDS;
	}*/

    public ConexionDB() {
        conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
            if (conn != null) {
                System.out.println("CONEXION ESTABLECIDA");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ResultSet getResultSet() {
        return resultSet;
    }
    
    private void cerrarResult() {
        try {
            resultSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConexionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cerrarSentencia() {
        try {
            sentencia.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConexionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cerrarConexion() {
        try {
            if (resultSet != null) {
                cerrarResult();
            }
            if (sentencia != null) {
                cerrarSentencia();
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConexionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void ejecutarConsulta(String consulta) {
        try {
            sentencia = conn.createStatement();
            resultSet = sentencia.executeQuery(consulta);
        } catch (SQLException ex) {
            Logger.getLogger(ConexionDB.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private boolean existeValor(String valor, String columna, String tabla) {

        boolean existe = false;
        Statement sentenciaAux;

        try {
            sentenciaAux = conn.createStatement();
            ResultSet aux = sentenciaAux.executeQuery(
                    "SELECT COUNT(*) FROM " + tabla + " WHERE UPPER(" + columna + ") ='" + valor.toUpperCase() + "'");
            aux.next();
            if (aux.getInt(1) >= 1) {
                existe = true;
            }
            aux.close();
            sentenciaAux.close();

        } catch (SQLException ex) {
            Logger.getLogger(ConexionDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return existe;
    }

    private int getID(String username) {
        int id = -1;
        String query = "SELECT ID FROM USERS WHERE USERNAME ='" + username + "'";
        try {
            ejecutarConsulta(query);
            if (resultSet.next()) {
                ResultSet rs = getResultSet();
                id = rs.getInt("ID");
            }
            return id;
        } catch (Exception e) {
            return id;
        }
    }

    @Override
    public int login(String username, String password) {
        String query = "SELECT ID, USERNAME, TYPE FROM USERS WHERE USERNAME ='" + username + "' AND PASSWORD ='"
                + password + "';";
        int status = -1;
        try {
            ejecutarConsulta(query);
            if (resultSet.next()) {
                ResultSet rs = getResultSet();
                if (rs.getString("TYPE").equals("AD")) {
                    status = 0;
                } else {
                    status = 1;
                }
            }
        } catch (Exception e) {
            return status;
        }
        return status;
    }

    @Override
    public boolean createNews(String username, String headline, String content) throws RemoteException {
        String uniqueName = randomUniqueName();
        int uid = getID(username);
        boolean succesful = false;
        if (!existeValor(uniqueName, "UNIQUENAME", "NEWS") && uid > 0) {
            String commandAction = "INSERT INTO NEWS (UNIQUENAME, HEADLINE, CONTENT, AUTHOR) VALUES (?, ?, ?, ?)";
            try {
                PreparedStatement ps = conn.prepareStatement(commandAction);
                ps.setString(1, uniqueName);
                ps.setString(2, headline);
                ps.setString(3, content);
                ps.setInt(4, uid);
                int inserteddRows = ps.executeUpdate();
                if (inserteddRows == 1) {
                    succesful = true;
                }
                ps.close();
            } catch (Exception e) {
                return succesful;
            }
        }
        return succesful;
    }

    @Override
    public boolean deleteNews(String uniqueName) throws RemoteException {
        boolean succesful = false;
        if (!existeValor(uniqueName, "UNIQUENAME", "NEWS")) {
            String commandAction = "DELETE FROM NEWS WHERE UNIQUENAME = ?";
            try {
                PreparedStatement ps = conn.prepareStatement(commandAction);
                ps.setString(1, uniqueName);
                int deletedRows = ps.executeUpdate();
                if (deletedRows == 1) {
                    succesful = true;
                }
                ps.close();
            } catch (Exception e) {
                return succesful;
            }
        }
        return succesful;
    }

    @Override
    public List<News> getNewsList() throws RemoteException {
        if (newsList.equals(null)) {
            newsList = new ArrayList<News>();
        } else {
            newsList.clear();
        }
        String query = "SELECT NEWS.UNIQUENAME AS CODIGO_NOTICIA, NEWS.HEADLINE AS TITULAR, USERS.USERNAME AS AUTOR, NEWS.CONTENT AS CONTENIDO, NEWS.CREATIONDATE AS FECHA_DE_CREACION, NEWS.LASTMODIFICATIONDATE AS ULTIMA_MODIFICACION FROM NEWS, USERS WHERE NEWS.AUTHOR = USERS.ID ORDER BY NEWS.UNIQUENAME;";
        try {
            ejecutarConsulta(query);
            while (resultSet.next()) {
                ResultSet rs = getResultSet();
                News nueva = new News();
                nueva.setUniqueName(rs.getString("CODIGO_NOTICIA"));
                nueva.setHeadline(rs.getString("TITULAR"));
                nueva.setAuthor(rs.getString("AUTOR"));
                nueva.setContent(rs.getString("CONTENIDO"));
                nueva.setCreationDate(rs.getTimestamp("FECHA_DE_CREACION"));
                nueva.setLastModificationDate(rs.getTimestamp("ULTIMA_MODIFICACION"));
                newsList.add(nueva);
            }
        } catch (Exception e) {
            return null;
        }
        return newsList;
    }

    @Override
    public List<News> getNewsList(String value, int option) throws RemoteException {
        newsList = getNewsList();
        if (!newsList.equals(null)) {
            List<News> copy = new ArrayList<News>();
            switch (option) {
                case 0:// Código único
                    for (News news : newsList) {
                        if (news.getUniqueName().toUpperCase().equals(value.toUpperCase())
                                || news.getUniqueName().toUpperCase().contains(value.toUpperCase())) {
                            copy.add(news);
                        }
                    }
                    return copy;
                case 1:// Titular
                    for (News news : newsList) {
                        if (news.getHeadline().toUpperCase().equals(value.toUpperCase())
                                || news.getHeadline().toUpperCase().contains(value.toUpperCase())) {
                            copy.add(news);
                        }
                    }
                    return copy;
                case 2:// Contenido
                    for (News news : newsList) {
                        if (news.getContent().toUpperCase().equals(value.toUpperCase())
                                || news.getContent().toUpperCase().contains(value.toUpperCase())) {
                            copy.add(news);
                        }
                    }
                    return copy;
                case 3:// Autor
                    for (News news : newsList) {
                        if (news.getAuthor().toUpperCase().equals(value.toUpperCase())) {
                            copy.add(news);
                        }
                    }
                    return copy;
                case 4:// Fecha de creación (superiores)
                    for (News news : newsList) {
                        if (news.getCreationDate().compareTo(Timestamp.valueOf(value)) >= 0) {
                            copy.add(news);
                        }
                    }
                    return copy;
                case 5:// Última actualización (superiores)
                    for (News news : newsList) {
                        if (news.getLastModificationDate().compareTo(Timestamp.valueOf(value)) >= 0) {
                            copy.add(news);
                        }
                    }
                    return copy;
                default:
                    return null;
            }
        }

        return null;
    }

    @Override
    public boolean modifyNews(String uniqueName, String headline, String content) throws RemoteException {
        boolean succesful = false;
        if (!existeValor(uniqueName, "UNIQUENAME", "NEWS")) {
            String commandAction = "UPDATE NEWS SET HEADLINE = ?, CONTENT = ? WHERE UNIQUENAME = '" + uniqueName + "'";
            try {
                PreparedStatement ps = conn.prepareStatement(commandAction);
                ps.setString(1, headline);
                ps.setString(2, content);
                ps.setString(3, uniqueName);
                int updatedRows = ps.executeUpdate();
                if (updatedRows == 1) {
                    succesful = true;
                }
                ps.close();
            } catch (Exception e) {
                return succesful;
            }
        }
        return succesful;
    }

    private String randomUniqueName() {
        byte[] array = new byte[7]; // length is bounded by 7
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));

        return generatedString;
    }

}
