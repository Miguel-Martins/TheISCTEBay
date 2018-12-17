package pt.iul.pcd.message;

import java.io.Serializable;

public class Block implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fileName;
	private int offSet;
	private int length;
	private int index;

	public Block(String fileName, int offSet, int length, int index) {
		this.fileName = fileName;
		this.offSet = offSet;
		this.length = length;
		this.index = index;
	}

	public String getFileName() {
		return fileName;
	}

	public int getOffSet() {
		return offSet;
	}

	public int getLength() {
		return length;
	}

	public int getIndex() {
		return index;
	}
}
