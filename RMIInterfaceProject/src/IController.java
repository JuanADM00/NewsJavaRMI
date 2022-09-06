import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IController extends Remote{
    public int login(String username, String password) throws RemoteException;
    public List getNewsList() throws RemoteException;//Toda la lista de noticias en la base de datos
    public List getNewsList(String value, int option) throws RemoteException;//Toda la lista de noticias cuyo campo evaluado coincida o contenga el valor buscado.
    public boolean createNews(String username, String headline, String content) throws RemoteException;
    public boolean modifyNews(String uniqueName, String headline, String content) throws RemoteException;
    public boolean deleteNews(String uniqueName) throws RemoteException;
}
