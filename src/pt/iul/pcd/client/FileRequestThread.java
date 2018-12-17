package pt.iul.pcd.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import pt.iul.pcd.controlstructures.BlockingQueue;
import pt.iul.pcd.message.Block;
import pt.iul.pcd.message.FileBlockRequestMessage;
import pt.iul.pcd.message.FilePart;
import pt.iul.pcd.message.FilePartTable;
import pt.iul.pcd.message.FileResponse;
import pt.iul.pcd.user.User;

public class FileRequestThread extends Thread {

	private Client client;
	private User user;
	private BlockingQueue<Block> blockingQueue;
	private Socket socket;
	private ObjectOutputStream outToClient;
	private ObjectInputStream inFromClient;
	private Block block;
	private FilePartTable filePartTable;

	public FileRequestThread(Client client, User user, BlockingQueue<Block> blockingQueue,
			FilePartTable filePartTable) {
		this.client = client;
		this.user = user;
		this.blockingQueue = blockingQueue;
		this.filePartTable = filePartTable;
	}

	@Override
	public void run() {
		try {
			initializeFields();

			while (!blockingQueue.isEmpty()) {
				try {
					block = blockingQueue.poll();
					outToClient.writeObject(new FileBlockRequestMessage(block));
					dealWithInput();

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// Tratar da excepção para quando re
			e.printStackTrace();
		}
	}

	private void initializeFields() throws IOException {
		socket = new Socket(user.getUserAddress(), user.getUserPort());
		outToClient = new ObjectOutputStream(socket.getOutputStream());
		inFromClient = new ObjectInputStream(socket.getInputStream());
	}

	private void dealWithInput() throws ClassNotFoundException, IOException {
		FilePart filePart = (FilePart) inFromClient.readObject();
		filePartTable.put(filePart, block.getIndex());
	}

}
