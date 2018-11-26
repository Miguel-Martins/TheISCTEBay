package pt.iul.pcd.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import pt.iul.pcd.message.FileResponse;
import pt.iul.pcd.message.WordSearchMessage;

public class InquiryThread extends Thread {

	private Socket socket;
	private ObjectOutputStream outToClient;
    private ObjectInputStream inFromClient;
    // Palavra a procurar
    private String keyword;
    // Cliente
    private Client client;
	
	public InquiryThread(Socket socket, String keyword, Client client)
	{
		try {
			this.socket = socket;
			outToClient = new ObjectOutputStream(socket.getOutputStream());
			inFromClient = new ObjectInputStream(socket.getInputStream());
			this.keyword = keyword;
			this.client = client;
		} catch (IOException e) {
			System.out.println("Excepção apanhada nos ObjectStreams da InquiryThread");
		}
	}

	@Override
	public void run() {
		try {
			searchForKeyword();
			dealWithInput();
		} catch (IOException e) {
			System.out.println("Cliente saiu da conexão P2P");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void searchForKeyword() throws IOException {
		outToClient.writeObject(new WordSearchMessage(keyword));
		outToClient.flush();
	}
	
	private void dealWithInput() throws ClassNotFoundException, IOException
	{	
		FileResponse fileDetails = (FileResponse) inFromClient.readObject();
		System.out.println("RECEBI O FILERESPONSE");
		client.updateFileList(fileDetails);
	}
	
}
