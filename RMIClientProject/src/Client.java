import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 1802;

    public static void main(String[] args) throws RemoteException, NotBoundException {
        try {
            String path = "rmi://" + IP + ":" + Integer.toString(PORT) + "/service";
            IController interfaz = (IController) Naming.lookup(path); // Buscar en el registro...
            Scanner sc = new Scanner(System.in);
            int election = -1, status = -1;
            String menuCU = "------------------\n1) Mis noticias\n2) Buscar noticias\n3) Nueva noticia\n4) Modificar noticia\n5) Borrar noticia\n0) Salir";
            String menuAD = "------------------\n1) Buscar noticias\n2) Modificar noticia\n3) Borrar noticia\n0) Salir";
            String username = "", password = "", code = "", headline = "", content = "";
            List myList = new ArrayList<>();
            boolean control = true, loged = false;
            while (control == true) {
                System.out.println("Nombre de usuario:");
                username = sc.nextLine();
                System.out.println("Contraseña:");
                password = sc.nextLine();
                status = interfaz.login(username, password);
                if (status > -1) {
                    control = false;
                }
            }
            loged = true;
            while (loged) {
                control = true;
                while (control == true) {
                    if (status == 0) {
                        System.out.println(menuAD);
                    } else {
                        System.out.println(menuCU);
                    }
                    try {
                        election = Integer.valueOf(sc.nextLine());
                        if (election > -1 && election < 6) {
                            control = false;
                        } else {
                            System.out.println("Opción inexistente. Intenta de nuevo");
                        }
                    } catch (Exception e) {
                        System.out.println("Formato inválido. Intenta de nuevo.");
                    }
                }
                switch (election) {
                    case 1:
                        if (status == 0) {// Opción ADMIN (buscar noticias)
                            myList = interfaz.getNewsList();
                            System.out.println(myList.toString());
                        } else {// Opción COMMON USER (mis noticias)
                            myList = interfaz.getNewsList(username, 3);
                            System.out.println(myList.toString());
                        }
                        break;
                    case 2:
                        if (status == 0) {// Opción ADMIN (modificar noticia)
                            System.out.println("Ingrese código de la noticia:");
                            code = sc.nextLine();
                            myList = interfaz.getNewsList(code, 0);
                            if (myList.equals(null)) {
                                System.out.println("Fallo en la consulta.");
                            } else {
                                if (myList.isEmpty()) {
                                    System.out.println("Código inexistente.");
                                } else {
                                    System.out.println(myList.toString());
                                    System.out.println("Nuevo titular:");
                                    headline = sc.nextLine();
                                    System.out.println("Nuevo contenido:");
                                    content = sc.nextLine();
                                    boolean succesful = interfaz.modifyNews(code, headline, content);
                                    if (succesful) {
                                        System.out.println("Actualización exitosa.");
                                    } else {
                                        System.out.println("Fallo en la actualización.");
                                    }
                                }
                            }

                        } else {// Opción COMMON USER (buscar noticias)
                            myList = interfaz.getNewsList();
                            System.out.println(myList.toString());
                        }
                        break;
                    case 3:
                        if (status == 0) {// Opción ADMIN (borrar)
                            System.out.println("Ingrese código de la noticia:");
                            code = sc.nextLine();
                            myList = interfaz.getNewsList(code, 0);
                            if (myList.equals(null)) {
                                System.out.println("Fallo en la consulta.");
                            } else {
                                if (myList.isEmpty()) {
                                    System.out.println("Código inexistente.");
                                } else {
                                    boolean succesful = interfaz.deleteNews(code);
                                    if (succesful) {
                                        System.out.println("Eliminación exitosa.");
                                    } else {
                                        System.out.println("Fallo en la eliminación.");
                                    }
                                }
                            }
                        } else {// Opción COMMON USER (nueva noticia)
                            System.out.println("Titular:");
                            headline = sc.nextLine();
                            System.out.println("Contenido:");
                            content = sc.nextLine();
                            boolean succesful = interfaz.createNews(username, headline, content);
                            if (succesful) {
                                System.out.println("Actualización exitosa.");
                            } else {
                                System.out.println("Fallo en la actualización.");
                            }
                        }
                        break;
                    case 4:
                        if (status == 0) {// Opción ADMIN (N/A)
                            System.out.println("Opción inexistente. Intenta de nuevo");
                        } else {// Opción COMMON USER (modificar noticia)
                            System.out.println("Ingrese código de la noticia:");
                            code = sc.nextLine();
                            myList = interfaz.getNewsList(code, 0);
                            if (myList.equals(null)) {
                                System.out.println("Fallo en la consulta.");
                            } else {
                                if (myList.isEmpty()) {
                                    System.out.println("Código inexistente.");
                                } else if (!myList.toString().contains(username)) {
                                    System.out.println("Sólo permiso de lectura sobre esta noticia.");
                                } else {
                                    System.out.println(myList.toString());
                                    System.out.println("Nuevo titular:");
                                    headline = sc.nextLine();
                                    System.out.println("Nuevo contenido:");
                                    content = sc.nextLine();
                                    boolean succesful = interfaz.modifyNews(code, headline, content);
                                    if (succesful) {
                                        System.out.println("Actualización exitosa.");
                                    } else {
                                        System.out.println("Fallo en la actualización.");
                                    }
                                }
                            }
                        }
                        break;
                    case 5:
                        if (status == 0) {// Opción ADMIN (N/A)
                            System.out.println("Opción inexistente. Intenta de nuevo");
                        } else {// Opción COMMON USER (borrar noticia)
                            System.out.println("Ingrese código de la noticia:");
                            code = sc.nextLine();
                            myList = interfaz.getNewsList(code, 0);
                            if (myList.equals(null)) {
                                System.out.println("Fallo en la consulta.");
                            } else {
                                if (myList.isEmpty()) {
                                    System.out.println("Código inexistente.");
                                } else if (!myList.toString().contains(username)) {
                                    System.out.println("Sólo permiso de lectura sobre esta noticia.");
                                } else {
                                    boolean succesful = interfaz.deleteNews(code);
                                    if (succesful) {
                                        System.out.println("Eliminación exitosa.");
                                    } else {
                                        System.out.println("Fallo en la eliminación.");
                                    }
                                }
                            }
                        }
                        break;
                    case 0: // (salir)
                        sc.close();
                        loged = false;
                        break;

                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
