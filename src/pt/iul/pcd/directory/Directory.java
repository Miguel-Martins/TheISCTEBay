package pt.iul.pcd.directory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import pt.iul.pcd.user.User;

public class Directory {
	
	// Constantes dos comandos
	public static final String SIGN_UP = "INSC";
	public static final String CONSULT = "CLT";
	public static final String END_CONSULT = "END";
	// Atributo relativo aos detalhes de execução
	private int serverPort;
	// Atributos relativos ao servidor
	private ServerSocket serverSocket;
	private Socket socket;
	// Atributo relativo à estrutura de dados para guardar os utilizadores
	private List<User> users = new ArrayList<User>();
	
	public Directory(String args[])
	{
		if(args != null)
			serverPort = Integer.parseInt(args[0]);
		initializeServer();
	}

	// Inicializa o servidor e estabelece as conexões com utilizadores
	private void initializeServer() {
		try {
			serverSocket = new ServerSocket(serverPort);
			while(true)
			{
				socket = serverSocket.accept();
				new DirectoryConnectionThread(socket, users).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
	