package pt.iul.pcd.message;

public class FilePartTable {

	FilePart[] table;
	int filePartsReceived;

	public FilePartTable(int size) {
		table = new FilePart[size];
		filePartsReceived = 0;
	}

	public synchronized void put(FilePart filePart, int index) {
		table[index] = filePart;
		filePartsReceived++;
		notifyAll();
	}

	public synchronized FilePart[] get() {
		while (filePartsReceived != table.length) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return table;
	}

}
