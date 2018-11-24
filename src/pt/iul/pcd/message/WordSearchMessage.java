package pt.iul.pcd.message;

import java.io.Serializable;

public class WordSearchMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String keyword;
	
	public WordSearchMessage(String keyword)
	{
		this.keyword = keyword;
	}

	public String getKeyword() {
		return keyword;
	}
	
	
}
