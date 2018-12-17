package pt.iul.pcd.client;

import pt.iul.pcd.message.FilePart;
import pt.iul.pcd.message.FilePartTable;

public class WriterThread extends Thread {

	private FilePartTable filePartTable;
	private Client client;
	private String fileName;

	public WriterThread(FilePartTable filePartTable, Client client, String fileName) {
		this.filePartTable = filePartTable;
		this.client = client;
		this.fileName = fileName;

	}

	@Override
	public void run() {
		FilePart[] filePartsToWrite = filePartTable.get();
		client.writeFile(filePartsToWrite, totalBytes(filePartsToWrite), fileName);
	}

	private int totalBytes(FilePart[] fileParts) {
		int total = 0;
		for (FilePart fp : fileParts)
			total += fp.getBytes().length;
		return total;
	}

}
