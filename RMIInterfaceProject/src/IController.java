import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IController<T> extends Remote{
    public int login(String username, String password) throws RemoteException;
    public List<T> getNewsList() throws RemoteException;//Toda la lista de noticias en la base de datos
    public List<T> getNewsList(String author) throws RemoteException; //Toda la lista de noticias hechas por un usuario
    public List<T> getNewsList(String value, int option) throws RemoteException;//Toda la lista de noticias cuyo campo evaluado coincida o contenga el valor buscado.
    public boolean createNews(int uid, String headline, String content) throws RemoteException;
    public boolean modifyNews(String uniqueName, String headline, String content) throws RemoteException;
    public boolean deleteNews(String uniqueName) throws RemoteException;
}
