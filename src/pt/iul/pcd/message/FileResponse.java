package pt.iul.pcd.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Conjunto de File Details
	private List<FileDetails> list;
	
	public FileResponse()
	{
		list = new ArrayList<FileDetails>();
	}
	
	public synchronized void addFileDetails(FileDetails fileDetails)
	{
		list.add(fileDetails);
	}
	
	public List<FileDetails> getList()
	{
		return list;
	}
	
}
