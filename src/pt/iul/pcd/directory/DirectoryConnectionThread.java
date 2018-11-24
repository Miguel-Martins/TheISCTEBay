package pt.iul.pcd.directory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import pt.iul.pcd.user.User;

public class DirectoryConnectionThread extends Thread {

	private Socket socket;
	private List<User> users;
	private User user;
	// Canais de entrada e saida de informação
	private BufferedReader in;
	private PrintWriter out;
	
	public DirectoryConnectionThread(Socket socket, List<User> users) {
		super();
		this.socket = socket;
		this.users = users;
	}
	
	@Override
	public void run() {
		try {
			initializeConnection();
			startServing();
		} catch (IOException e) {
			//Ligação caiu, tratar deste caso!
			removeUser();
			System.out.println("Cliente saiu");
//			e.printStackTrace();
		}
	}

	private void initializeConnection() throws IOException {
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
	}
	
	private void startServing() throws IOException
	{
		while(true)
		{
			String message = in.readLine();
			if(message != null)
				parseMessage(message);
		}
	}

	private void parseMessage(String message) {
		String command = message.split(" ")[0];
		switch(command)
		{
		case Directory.SIGN_UP:
			signUser(message);
			break;
		case Directory.CONSULT:
			consultUsers();
			break;
		}
	}

	private void signUser(String message) {
		System.out.println(message);
		String[] info = message.split(" ");
		user = new User(info[1], Integer.parseInt(info[2]));
		synchronized (users) {
			users.add(user);
		}
	}
	
	private void consultUsers()
	{
		synchronized (users) {
			for(User user: users)
			{
				out.println("CLT " + user.getUserAddress() + " " + user.getUserPort());
			}
			out.println("END");			
		}
	}
	
	private synchronized void removeUser() {
		users.remove(user);
		
	}

}
