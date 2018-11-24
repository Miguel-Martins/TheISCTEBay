package pt.iul.pcd.message;

import java.io.Serializable;

public class FileDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fileName;
	private long fileSize;
	
	public FileDetails(String fileName, long fileSize) {
		this.fileName = fileName;
		this.fileSize = fileSize;
	}

	public String getFileName() {
		return fileName;
	}

	public long getFileSize() {
		return fileSize;
	}
	
	@Override
	public String toString() {
		return fileName + " " + fileSize;
	}
	
	
}
