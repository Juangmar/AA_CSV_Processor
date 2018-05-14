package view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileChooser extends JFrame {

	/**
	 * Default serial version used to suppress warning
	 */
	private static final long serialVersionUID = 7558101181907117923L;
	
	private JFileChooser fc;
	private JPanel body;
	private HashMap<Integer, File> files;
	private HashMap<Integer, JLabel> filenames;
	private HashMap<Integer, JButton> fileselect;
	private JButton addField, about, go, exit;
	private int nextID;
	
	public FileChooser() {
		super();
		loadWindow();
		loadContent();
	}
	
	private void loadWindow() {
		setTitle("CSV standardizer");
		//setSize(500,200);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		/*
		 * As a design choice, the window will be displayed in the middle of the screen.
		 * To do so:
		 */
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); //We get the screen size
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2); //The window is set to be in half the screen size (the middle) 
		
	}
	
	private void loadContent() {
		//File Chooser initializer 
		fc = new JFileChooser(); 
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Data files (.csv)", "csv", "csv"); //Only .csv files will be allowed
		fc.setFileFilter(filter); //The .cvs filter is set to the File Chooser
		
		
		files = new HashMap<Integer, File>(); //File list initializer
		filenames = new HashMap<Integer, JLabel>(); //Label list initializer
		fileselect = new HashMap<Integer, JButton>();
		
		addField = new JButton("Add more files...");
		addField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addFileField();
			}
		});
		
		about = new JButton("About");
		about.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showAbout(getTitle());
			}
		});
		
		nextID = 0;
		
		go = new JButton("Process"); //Process the files
		go.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		exit = new JButton("Exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		//Panel initializer. The value will be replaced when fileField is added though addFileField
		body = new JPanel(new GridLayout(3,3));
		add(body);
		
		addFileField();
	}
	

	private void addFileField() {
		remove(body);
		
		final int currentID = nextID;
		
		File e = null;
		files.put(currentID, e);
		
		JLabel lab = new JLabel("(File not selected yet)");
		filenames.put(currentID, lab);
	
		JButton sel = new JButton("Explore...");
		sel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//In response to a button click:
				int returnVal = fc.showOpenDialog(body);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					files.put(currentID, fc.getSelectedFile());
					filenames.get(currentID).setText(fc.getSelectedFile().getName());
		        } else {
		           System.out.println("Open command cancelled by user.");
		        }
			}
		});
		fileselect.put(currentID, sel);
		
		nextID++;
		
		body = new JPanel(new GridLayout( (fileselect.size()+2) , 3) );
		
		for(int i = 0; i < fileselect.size(); i++) {
			JLabel current = new JLabel("File " + i + ":");
			body.add(current);
			body.add(filenames.get(i));
			body.add(fileselect.get(i));
		}
		body.add(addField);
		body.add(new JLabel());
		body.add(about);
		body.add(go);
		body.add(new JLabel());
		body.add(exit);
		

		setSize(500,((fileselect.size()+2)*36));
		add(body);
	}
	
	private void showAbout(String title) {
		JFrame about = new JFrame(title);
		about.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel panel = new JPanel();
		JTextPane area = new JTextPane();
		
		area.setEditable(false);
		
		final String newline = System.lineSeparator();
		
		String content = "CSV Standarizer" + 
					newline 
					+ newline 
					+ "This is the documentation about the program."
					+ newline
					+ newline
					+ "This program processes multiple csv files with a specific format and generates a single file with a stardard format and"
					+ " only the relevant attributes with utf-8 encoding."
					+ newline
					+ "The raw data must have the following format:"
					+ newline
					+ newline
					+ "first line of header"
					+ newline
					+ "COMMENT_ID,AUTHOR,DATE,CONTENT,CLASS"
					+ newline
					+ newline
					+ "The program will generate a csv with the attributes:"
					+ newline
					+ "AUTHOR,CONTENT,CLASS";
		area.setText(content);
		JScrollPane pane = new JScrollPane(area);
		about.add(pane);
		
		about.setSize(320,350);
		about.setResizable(false);
		about.setVisible(true);
	}

}
