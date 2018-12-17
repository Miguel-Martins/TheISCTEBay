package pt.iul.pcd.message;

import java.io.Serializable;

public class FilePart implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private byte[] bytes;

	public FilePart(byte[] bytes) {
		this.bytes = bytes;
	}

	public byte[] getBytes() {
		return bytes;
	}

}
