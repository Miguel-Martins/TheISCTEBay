package pt.iul.pcd.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;

import pt.iul.pcd.message.Block;
import pt.iul.pcd.message.FileBlockRequestMessage;
import pt.iul.pcd.message.FilePart;
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
				if (message instanceof WordSearchMessage) {
					dealWithSearchMessage((WordSearchMessage) message);
					interrupt();
				} else if (message instanceof FileBlockRequestMessage) {
					dealWithDownloadMessage((FileBlockRequestMessage) message);
				}
			}

		}
	}

	private void dealWithDownloadMessage(FileBlockRequestMessage message) {
		try {
			client.getThreadPool().submit(new Runnable() {

				@Override
				public void run() {
					try {
						Block block = message.getBlock();
						byte[] fileContents = Files.readAllBytes(client.getFile(block.getFileName()).toPath());
						byte[] filesToSend = new byte[block.getLength()];
						for (int i = 0; i != block.getLength(); i++) {
							filesToSend[i] = fileContents[block.getOffSet() + i];
						}
						FilePart filePart = new FilePart(filesToSend);
						outToClient.writeObject(filePart);
						outToClient.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void dealWithSearchMessage(WordSearchMessage message) throws IOException {
		FileResponse answer = client.searchForFile(message.getKeyword());
		outToClient.writeObject(answer);
		outToClient.flush();
		incomingConnection.close();
	}
}
