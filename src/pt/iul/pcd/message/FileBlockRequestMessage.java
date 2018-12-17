package pt.iul.pcd.message;

import java.io.Serializable;

public class FileBlockRequestMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Block block; 
	
	public FileBlockRequestMessage(Block block) {
		this.block = block;
	}

	public Block getBlock()
	{
		return block;
	}
	
	@Override
	public String toString() {
		
		return this.getClass().getSimpleName().toLowerCase();
	}
}
