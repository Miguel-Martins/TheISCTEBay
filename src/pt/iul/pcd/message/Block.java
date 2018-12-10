package pt.iul.pcd.message;

public class Block {

	private String fileName;
	private int	offSet;
	private int	length;
	
	public Block(String fileName, int offSet, int length) {
		this.fileName = fileName;
		this.offSet = offSet;
		this.length = length;
	}
	
	
}
