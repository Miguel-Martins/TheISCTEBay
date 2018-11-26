package pt.iul.pcd.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import pt.iul.pcd.directory.Directory;
import pt.iul.pcd.gui.GUI;
import pt.iul.pcd.message.FileDetails;
import pt.iul.pcd.message.FileResponse;

public class Client {

	// Atributos relativos aos detalhes de execução
	private String directoryAddress;
	private int directoryPort;
	private int clientPort;
	private String fileFolder;
	// Atributos relativos à conexão e comunicação com o diretório
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	// Atributos relativos ao servidor e comunicação com outros utilizadores
	private ServerSocket serverSocket;
	private ObjectOutputStream outToClient;
	private ObjectInputStream inFromClient;
	// Atributo relativo ao interface gráfico do utilizador
	private GUI gui;
	// Ficheiros
	File[] files;

	public Client(String[] args) {
		try {
			loadGUI();
			loadFields(args);
			initializeConnection();
			logIn();
			initializeServer();
//			communicateWithDirectory();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Inicializar o interface gráfico do utilizador
	private void loadGUI() {
		gui = new GUI(this);
	}

	// Detalhes de execução
	private void loadFields(String[] args) {
		if (args != null) {
			directoryAddress = args[0];
			directoryPort = Integer.parseInt(args[1]);
			clientPort = Integer.parseInt(args[2]);
			fileFolder = args[3];
			files = new File(fileFolder).listFiles();
		}
	}

	// Ligação ao diretório através da socket e criação dos canais de comunicação
	private void initializeConnection() throws IOException {
		socket = new Socket(directoryAddress, directoryPort);
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

	}

	// Enviar mensagem ao diretorio e inscrever o utilizador
	private void logIn() throws UnknownHostException {
		out.println("INSC " + InetAddress.getByName(null).toString().split("/")[1] + " " + clientPort);
	}

	private void initializeServer() {
		try {
			serverSocket = new ServerSocket(clientPort);
			while (true) {
				Socket incomingConnection = serverSocket.accept();
				System.out.println("Nova conexao");
				new ClientConnectionThread(incomingConnection, this).start();

			}
		} catch (IOException e) {
			System.out.println("Cliente saiu da conexão P2P");
		}
	}

	// Procurar no diretório por ficheiro com a palavra chave dada
	public void searchKeyword(String keyword) {
		try {
			List<String[]> userInfo = getUserInfo();
			if (!userInfo.isEmpty()) {
				for (String[] info : userInfo) {
					System.out.println("Percorrer os userPorts e criar InquiryThreads");
					Socket socket = new Socket(info[0], Integer.parseInt(info[1]));
					new InquiryThread(socket, keyword, this).start();
				}
			} else {
				System.out.println("Client - Lista Vazia");
				gui.getDefaultListModel().clear();
			}
		} catch (IOException e) {
			System.out.println("INQUIRY THREAD EXCEPÇÃO");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private List<String[]> getUserInfo() throws IOException, InterruptedException {
		List<String[]> userInfo = new ArrayList<String[]>();
		out.println(Directory.CONSULT);
		while (true) {
			String message = in.readLine();
			if (!message.equals(Directory.END_CONSULT)) {
				String userAddress = message.split(" ")[1];
				String userPort = message.split(" ")[2];
				if (Integer.parseInt(userPort) != clientPort) {
					String[] info = { userAddress, userPort };
					userInfo.add(info);

				}
			} else
				break;
		}
		return userInfo;
	}

	public void updateFileList(FileResponse fileResponse) {
		System.out.println("FileList up to date.");
		gui.updateList(fileResponse);
	}

	public FileResponse searchForFile(String fileName) {
		FileResponse fileResponse = new FileResponse();
		for (int i = 0; i != files.length; i++) {
			if (files[i].getName().contains(fileName)) {
				fileResponse.addFileDetails(new FileDetails(files[i].getName(), files[i].length()));
			}

		}
		return fileResponse;

	}

}
