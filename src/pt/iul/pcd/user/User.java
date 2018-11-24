package pt.iul.pcd.user;

public class User {

	private String userAddress;
	private int userPort;
	
	public User(String userAddress, int userPort) {
		this.userAddress = userAddress;
		this.userPort = userPort;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public int getUserPort() {
		return userPort;
	}
	
	
	
}
