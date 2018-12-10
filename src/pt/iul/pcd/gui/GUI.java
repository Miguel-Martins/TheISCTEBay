package pt.iul.pcd.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import pt.iul.pcd.client.Client;
import pt.iul.pcd.message.FileDetails;
import pt.iul.pcd.message.FileResponse;

public class GUI {
	// Dimensões da frame
	private static final int width = 300;
	private static final int height = 200;
	private JFrame frame;
	// Componentes
	private JTextField textInsert;
	private JButton search;
	private JButton download;
	private JProgressBar progressBar;
	// Lista
	private JList<FileDetails> resultList;
	DefaultListModel<FileDetails> userModelList;
	// Cliente
	Client client;

	public GUI(Client client) {
		frame = new JFrame("TheISCTEBay");
		this.client = client;
		initializeFrame();
		loadFields();
		addContentToFrame();
		frame.pack();
		frame.setVisible(true);
	}

	private void initializeFrame() {
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
	}

	private void loadFields() {
		textInsert = new JTextField();
		textInsert.setPreferredSize(new Dimension(100, 20));

		search = new JButton("Procurar");
		search.setPreferredSize(new Dimension(90, 20));
		loadSearchButtonAction();

		download = new JButton("Descarregar");
		download.setPreferredSize(new Dimension(90, 20));
		download.setFont(download.getFont().deriveFont(22.0f));
		loadDownloadButtonAction();

		userModelList = new DefaultListModel<FileDetails>();
		resultList = new JList<FileDetails>(userModelList);
		resultList.setPreferredSize(new Dimension(200, 200));
		resultList.setFont(resultList.getFont().deriveFont(22.0f));

		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(50, 50));

	}

	private void addContentToFrame() {
		loadTopPanel();
		loadBottomPanel();
	}

	private void loadTopPanel() {
		JPanel topPanel = new JPanel();

		JLabel textToSearch = new JLabel("Texto a procurar: ");
		textToSearch.setPreferredSize(new Dimension(105, 20));

		topPanel.add(textToSearch);
		topPanel.add(textInsert);
		topPanel.add(search);
		frame.add(topPanel, BorderLayout.PAGE_START);
	}

	private void loadBottomPanel() {
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(1, 2));

		JPanel bottomLeftPanel = new JPanel();
		JPanel bottomRightPanel = new JPanel();
		bottomRightPanel.setLayout(new GridLayout(2, 1));

		bottomLeftPanel.add(resultList);

		bottomRightPanel.add(download);
		bottomRightPanel.add(progressBar);

		bottomPanel.add(bottomLeftPanel);
		bottomPanel.add(bottomRightPanel);

		frame.add(bottomPanel, BorderLayout.PAGE_END);
	}

	private void loadSearchButtonAction() {
		search.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				userModelList.clear();
				client.searchKeyword(textInsert.getText());
				updateList(client.getPeerList());
			}
		});
	}

	public void updateList(FileResponse fileResponse) {
		synchronized (userModelList) {
			for (FileDetails fd : fileResponse.getList())
				userModelList.addElement(fd);
			resultList.setModel(userModelList);
		}

	}

}