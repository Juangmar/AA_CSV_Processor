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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import business.Processor;

/**
 * View for the app. It manages the file chooser and executes the processor.
 * @author Juan Gomez-Martinho
 *
 */
/**
 * @author juan
 *
 */
public class FileChooser extends JFrame {

	/**
	 * Default serial version used to suppress warning
	 */
	private static final long serialVersionUID = 7558101181907117923L;
	
	private JFileChooser fc; // Only one file chooser, so the last directory is saved between file selctions.
	private JPanel body; //One unique body for the view.
	
	// Index --> File, Name, Button
	private HashMap<Integer, File> files; //HashMap that stores the pair Index-File
	private HashMap<Integer, JLabel> filenames; //HashMap to store the pair Index-File Name.
	private HashMap<Integer, JButton> fileselect; //HashMap to store the pair Index-Button to re-select the file.
	
	private JButton addField, about, go, exit; // Single buttons to control the program
	private int nextID; // Variable to store the next index to use with the HashMaps
	
	/**
	 *  Default and only constructor. No arguments needed.
	 */
	public FileChooser() {
		super(); // Call the super constructor from JFrame, from which it extends.
		loadWindow(); // Load the main window (JFrame attributes)
		loadContent(); // Initializes it's own attributes
	}
	
	private void loadWindow() {
		setTitle("CSV standardizer"); // Window title
		//setSize(500,200); //Sets the window size
		setDefaultCloseOperation(DISPOSE_ON_CLOSE); //When 
		
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
		fileselect = new HashMap<Integer, JButton>(); //Button list initializer
		
		addField = new JButton("Add more files..."); //The button to add files have a self-explanatory text
		addField.addActionListener(new ActionListener() { // Add an action to the button when pressed
			@Override
			public void actionPerformed(ActionEvent e) {
				addFileField(); //When pressed, ad a field to the current view
			}
		});
		
		about = new JButton("About"); //The button to see the program instructions has a self-explanatory text 
		about.addActionListener(new ActionListener() { //Add an action to the button when pressed

			@Override
			public void actionPerformed(ActionEvent e) {
				showAbout(getTitle()); //When pressed, show the "about" window
			}
		});
		
		nextID = 0; //The id count starts on 0
		
		go = new JButton("Process"); //The button to Process the files has a self-explanatory text
		go.addActionListener(new ActionListener() { //Add an action to the button when pressed
			@Override
			public void actionPerformed(ActionEvent e) {
				execute(); //When pressed, call to the method that starts the processing
			}
		});
		
		exit = new JButton("Exit"); //The button to exit the program has a self-explanatory text
		exit.addActionListener(new ActionListener() { //Ad an action to the button when pressed
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0); //Exit the current program.
			}
		});
		
		//Panel initializer. The value will be replaced when fileField is added though addFileField
		body = new JPanel(new GridLayout(3,3));
		add(body); //Finally, add the JPanel to the current JFrame
		
		addFileField(); //ad the file fields to the window
	}
	

	/**
	 *  Method to add a new field to choose a new File.
	 */
	private void addFileField() {
		remove(body); //To repaint the view, remove the current one.
		
		final int currentID = nextID; //The current ID to be worked with is the current value of nextId
		
		File e = null; //Create a new file object, null at the beginning 
		files.put(currentID, e); //Put the file in the HashMap with the saved id.
		
		JLabel lab = new JLabel("(File not selected yet)"); //The label to show the file name. By default, it has none.
		filenames.put(currentID, lab); //Put the label in the HashMap with the saved id.
	
		JButton sel = new JButton("Explore..."); //A new button to select the file
		sel.addActionListener(new ActionListener() { //Execute when the button is pressed.
			@Override
			public void actionPerformed(ActionEvent e) {
				//In response to a button click:
				int returnVal = fc.showOpenDialog(body); //Open the file chooser, and store the result
				if (returnVal == JFileChooser.APPROVE_OPTION) { // If the return value from the chooser is correct
					files.put(currentID, fc.getSelectedFile()); //Put the result file into the HashMap with the unique id.
					filenames.get(currentID).setText(fc.getSelectedFile().getName()); //Set the label text to show the file name.
		        } else { //File not chosen
		           //Do nothing
		        }
			}
		});
		fileselect.put(currentID, sel); //Put the created button into the HashMap with it's id.
		
		nextID++; //Having the components added, increase the next id.
		
		body = new JPanel(new GridLayout( (fileselect.size()+2) , 3) ); //The new panel has a row for each file field plus 2 (add file, go+exit)
		
		for(int i = 0; i < fileselect.size(); i++) { //Create the row: Label + File Name + Button
			JLabel current = new JLabel("File " + i + ":");
			body.add(current);
			body.add(filenames.get(i));
			body.add(fileselect.get(i));
		}
		body.add(addField); //Add the button to expand the fields
		body.add(new JLabel()); //Add a blank space
		body.add(about); //Add the about button
		body.add(go); //Add the execute button
		body.add(new JLabel()); //Add a blank space
		body.add(exit); //Add the exit button
		

		setSize(500,((fileselect.size()+2)*36)); //Set the new dimensions depending on the amount of current fields
		add(body); //finally, add the body
	}
	
	/**
	 * Method to show the about window
	 * @param title String containing the window title.
	 */
	private void showAbout(String title) {
		JFrame about = new JFrame(title); //New window / frame
		about.setDefaultCloseOperation(DISPOSE_ON_CLOSE); //When the window is closed, dispose that window only.
		
		JTextPane area = new JTextPane(); //Text pane to contain the body
		area.setEditable(false); //The text will not be editable
		
		final String newline = System.lineSeparator(); //Get the system line separator
		//The text containing the about description:
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
		area.setText(content); //Set the text into the Text Area
		JScrollPane pane = new JScrollPane(area); //Create a scrollable pane containing the Text Area
		about.add(pane); //Add the the pane into the window
		
		about.setSize(320,350); //Set default size
		about.setResizable(false); //Not resizable
		about.setVisible(true); //Set visible
	}
	
	/**
	 * Method that executes the file's processing 
	 */
	private void execute() {
		if(!files.isEmpty()) { //Not execute if there's no files to process
			Processor controller = new Processor(); //Create the Application Controller
			try {
				File file = controller.compute(files); // try to compute the files
				if(file!=null) JOptionPane.showMessageDialog(this, "File saved:\n" + file.getAbsolutePath()); //If the return isn't null, show the path of the result
			} catch (Exception e) {
				//Show error.
				e.printStackTrace();
			}
		} else {
			//Show warning
		}
	}

}
