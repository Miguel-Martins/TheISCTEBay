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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.iul.pcd.controlstructures.BlockingQueue;
import pt.iul.pcd.controlstructures.ThreadPool;
import pt.iul.pcd.directory.Directory;
import pt.iul.pcd.gui.GUI;
import pt.iul.pcd.message.Block;
import pt.iul.pcd.message.FileDetails;
import pt.iul.pcd.message.FilePart;
import pt.iul.pcd.message.FilePartTable;
import pt.iul.pcd.message.FileResponse;
import pt.iul.pcd.user.User;

public class Client {

	// Constante relativa ao tamanho máximo de um bloco em bits
	private static final int MAX_BLOCK_LENGHT = 1024;
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
	private File[] files;
	// HashMap
	private Map<FileDetails, ArrayList<User>> peerList;
	// ThreadPool
	private static final int MAX_THREADS_WORKERS = 5;
	private ThreadPool threadPool;

	public Client(String[] args) {
		try {
			loadGUI();
			loadFields(args);
			initializeConnection();
			logIn();
			initializeServer();
			// communicateWithDirectory();
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
			threadPool = new ThreadPool(MAX_THREADS_WORKERS);
		}
	}

	// Ligação ao diretório através da socket e criação dos canais de
	// comunicação
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
			peerList = new HashMap<FileDetails, ArrayList<User>>();
			List<User> userInfo = getUserInfo();
			if (!userInfo.isEmpty()) {
				for (User info : userInfo) {
					System.out.println("Percorrer os userPorts e criar InquiryThreads");
					Socket socket = new Socket(info.getUserAddress(), info.getUserPort());
					InquiryThread iq = new InquiryThread(socket, keyword, this, info);
					iq.start();
					iq.join();
					// perguntar o join
				}
			} else {
				System.out.println("Client - Lista Vazia");
			}
		} catch (IOException e) {
			System.out.println("INQUIRY THREAD EXCEPÇÃO");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private List<User> getUserInfo() throws IOException, InterruptedException {
		List<User> userInfo = new ArrayList<User>();
		out.println(Directory.CONSULT);
		while (true) {
			String message = in.readLine();
			if (!message.equals(Directory.END_CONSULT)) {
				String userAddress = message.split(" ")[1];
				String userPort = message.split(" ")[2];
				if (Integer.parseInt(userPort) != clientPort) {
					User user = new User(userAddress, Integer.parseInt(userPort));
					userInfo.add(user);

				}
			} else
				break;
		}
		return userInfo;
	}

	public void updateFileList(FileResponse fileResponse, User user) {
		addToPeerList(fileResponse, user);
	}

	private void addToPeerList(FileResponse fileResponse, User user) {
		for (FileDetails fd : fileResponse.getList()) {
			if (peerList.containsKey(fd))
				peerList.get(fd).add(user);
			else {
				List<User> list = new ArrayList<User>();
				list.add(user);
				peerList.put(fd, (ArrayList<User>) list);
			}
		}
	}

	public FileResponse getPeerList() {
		FileResponse fp = new FileResponse();
		for (FileDetails fd : peerList.keySet()) {
			fp.addFileDetails(fd);
		}
		return fp;
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

	private List<User> getUsersWithFileDetails(FileDetails fileDetails) {
		return peerList.get(fileDetails);
	}

	public ThreadPool getThreadPool() {
		return threadPool;
	}

	public File getFile(String fileName) {
		File file = null;
		for (int i = 0; i != files.length; i++) {
			if (files[i].getName().equals(fileName)) {
				file = files[i];
			}
		}
		return file;
	}

	private BlockingQueue<Block> getBlockQueue(FileDetails fileDetails, int numberOfBlocks)
			throws InterruptedException {
		BlockingQueue<Block> blocks = new BlockingQueue<Block>(numberOfBlocks);
		for (int i = 0; i != numberOfBlocks; i++) {
			String fileName = fileDetails.getFileName();
			int offSet = i * MAX_BLOCK_LENGHT;
			int lenght;
			if (i == numberOfBlocks - 1) {
				lenght = (int) (fileDetails.getFileSize() - (MAX_BLOCK_LENGHT * i));
			} else {
				lenght = MAX_BLOCK_LENGHT;
			}
			blocks.offer(new Block(fileName, offSet, lenght, i));
		}
		return blocks;
	}

	public void downloadKeyword(FileDetails fileDetails) {
		try {
			int numberOfBlocks = (int) (fileDetails.getFileSize() / MAX_BLOCK_LENGHT + 1);
			BlockingQueue<Block> blockingQueue = getBlockQueue(fileDetails, numberOfBlocks);
			FilePartTable filePartTable = new FilePartTable(numberOfBlocks);
			new WriterThread(filePartTable, this, fileDetails.getFileName()).start();
			for (User user : peerList.get(fileDetails)) {
				Thread t = new FileRequestThread(this, user, blockingQueue, filePartTable);
				t.start();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void writeFile(FilePart[] filePartsToWrite, int size, String fileName) {
		byte[] fileToWrite = new byte[size];
		for (int i = 0; i != filePartsToWrite.length; i++) {
			byte[] fp = filePartsToWrite[i].getBytes();
			for (int j = 0; j != filePartsToWrite[i].getBytes().length; j++) {
				fileToWrite[i * MAX_BLOCK_LENGHT + j] = fp[j];
			}
		}
		try {
			Files.write(Paths.get(fileFolder + "/" + fileName), fileToWrite);
			files = new File(fileFolder).listFiles();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Se o utilizador tentar ligar-se a um utilizador que já nao existe, tratar
	// desse caso. O utilizador podia ter estado online quando disse que tinha
	// o ficheiro procurado, mas entretanto desligou-se.
}
