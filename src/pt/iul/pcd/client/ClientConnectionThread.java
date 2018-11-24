package pt.iul.pcd.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import pt.iul.pcd.message.FileBlockRequestMessage;
import pt.iul.pcd.message.FileResponse;
import pt.iul.pcd.message.WordSearchMessage;

public class ClientConnectionThread extends Thread {

	private Socket incomingConnection;
	private ObjectOutputStream outToClient;
	private ObjectInputStream inFromClient;
	private Client client;

	public ClientConnectionThread(Socket incomingConnection, Client client) {
		this.incomingConnection = incomingConnection;
		this.client = client;
	}

	@Override
	public void run() {
		try {
			initializeConnection();
			startServing();
		} catch (IOException e) {
			System.out.println("Cliente saiu da ClientConnectionThread");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void initializeConnection() throws IOException {
		outToClient = new ObjectOutputStream(incomingConnection.getOutputStream());
		inFromClient = new ObjectInputStream(incomingConnection.getInputStream());
	}

	private void startServing() throws ClassNotFoundException, IOException {
		while (true) {
			Object message = inFromClient.readObject();
			if (message != null) {
				System.out.println(message.toString());
				if (message instanceof WordSearchMessage) {
					dealWithSearchMessage((WordSearchMessage) message);
					interrupt();
				} else if (message instanceof FileBlockRequestMessage) {

				}
			}

		}
	}
	
	private void dealWithSearchMessage(WordSearchMessage message) throws IOException {
		FileResponse answer = client.searchForFile(message.getKeyword());
		outToClient.writeObject(answer);
		outToClient.flush();
	}
}
