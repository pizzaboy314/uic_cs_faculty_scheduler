// Taken from: http://www.comweb.nl/java/Console/Console.html
// Adapted for use with this application by Bryan Spahr

package classes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Contains all GUI elements and calls the functions in the Worker class as
 * needed.
 * 
 * Taken from: http://www.comweb.nl/java/Console/Console.html and adapted for
 * use with this application by Bryan Spahr.
 * 
 * @author bryan
 *
 */
public class GUIapp extends WindowAdapter implements WindowListener, Runnable {
	/**
	 * Popup frame that can be opened to edit faculty info.
	 */
	private JFrame editorFrame;
	/**
	 * Main GUI window with buttons.
	 */
	private JFrame mainFrame;
	/**
	 * Recipient of sysout print statements in the editor window.
	 */
	private JTextArea textArea;
	/**
	 * For capturing the text stream from sysout.
	 */
	private Thread reader;
	/**
	 * For capturing the text stream from sysout.
	 */
	private Thread reader2;
	/**
	 * Used for properly closing threads when the application is terminated. I
	 * think.
	 */
	private boolean quit;

	/**
	 * Display window for showing calculated results.
	 */
	private JFrame resultFrame;
	/**
	 * For displaying the results text to the user.
	 */
	private JTextArea resultText;
	/**
	 * For storing the result string as a field in this class. In case it's
	 * needed later.
	 */
	private String resultString;
	/**
	 * File pointer for saving the results to a plaintext file.
	 */
	private File resultFile;
	/**
	 * Save dialog for saving the results to a plaintext file.
	 */
	private JFileChooser fc;

	/**
	 * For piping sysout text to the editor window.
	 */
	private final PipedInputStream pin = new PipedInputStream();
	/**
	 * For piping sysout text to the editor window.
	 */
	private final PipedInputStream pin2 = new PipedInputStream();

	/**
	 * Creates a new instance of GUIapp (duh), also calls the Worker class's
	 * init method and all GUI methods and init functions in this class.
	 */
	public GUIapp() {
		boolean showEditor = !Worker.init();
		textStreamInit();
		mainWindow();
		editorWindow(showEditor);
		resultWindow();
	}

	/**
	 * Creates and shows the main GUI window, including all Swing elements.
	 * Closing this windows is the only way to exit the application.
	 */
	public synchronized void mainWindow() {
		mainFrame = new JFrame("UIC CS Faculty Scheduler");

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension((int) (screenSize.width / 2), (int) (screenSize.height / 2));
		int x = (int) (frameSize.width - 150);
		int y = (int) (frameSize.height - 75);
		mainFrame.setBounds(x, y, 300, 75);

		JButton loadEditor = new JButton("Faculty Editor");
		loadEditor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editorFrame.setVisible(true);
				editorFrame.repaint();
			}
		});
		JButton loadResults = new JButton("Generate Instructors");
		loadResults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resultString = Worker.chooseInstructors(Worker.courses);
				resultText.setText(resultString);
				resultFrame.setVisible(true);
			}
		});

		JPanel controls = new JPanel();
		controls.setLayout(new FlowLayout());
		controls.add(loadEditor);
		controls.add(loadResults);

		mainFrame.add(controls, BorderLayout.SOUTH);
		mainFrame.addWindowListener(this);
		mainFrame.setVisible(true);

	}

	/**
	 * Creates the window for results but doesn't show it.
	 */
	public synchronized void resultWindow() {
		resultFrame = new JFrame("Results");
		File resultPath = new File(System.getProperty("user.dir"));
		fc = new JFileChooser(resultPath);

		FileFilter filter = new FileNameExtensionFilter("Text file (*.txt)", "txt");
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension((int) (screenSize.width / 2), (int) (screenSize.height / 2));
		int x = (int) (frameSize.width / 2);
		int y = (int) (frameSize.height / 2);
		resultFrame.setBounds(x, y, 400, frameSize.height);

		resultText = new JTextArea();
		resultText.setText("");
		resultText.setEditable(false);

		JButton saveFile = new JButton("Save Results");
		saveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showSaveDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					String filepath = f.getAbsolutePath();
					String filename = f.getName();

					if (!filename.contains(".txt")) {
						resultFile = new File(filepath + ".txt");
					} else {
						resultFile = f;
					}

					try {
						Files.write(Paths.get(resultFile.getAbsolutePath()), resultString.getBytes());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		JPanel controls = new JPanel();
		controls.setLayout(new FlowLayout());
		controls.add(saveFile);

		resultFrame.getContentPane().add(new JScrollPane(resultText), BorderLayout.CENTER);
		resultFrame.getContentPane().add(controls, BorderLayout.SOUTH);
	}

	/**
	 * Creates the editor window with all swing elements.
	 * 
	 * @param show
	 *            Indicates whether or not the editor window should be shown at
	 *            the start.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized void editorWindow(boolean show) {
		// create all components and add them
		editorFrame = new JFrame("Faculty Editor");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension((int) (screenSize.width / 2), (int) (screenSize.height / 2));
		int x = (int) (frameSize.width - 320);
		int y = (int) (frameSize.height - 200);
		editorFrame.setBounds(x, y, 640, 300);

		textArea = new JTextArea();
		textArea.setEditable(false);
		
		JButton clearConsole = new JButton("clear console");
		JButton reloadInstructors = new JButton("Reload Instructors");
		JButton reloadCourses = new JButton("Reload Courses");
		
		JPanel controls = new JPanel();
        controls.setLayout(new FlowLayout());
        controls.add(clearConsole);
        controls.add(reloadInstructors);
        controls.add(reloadCourses);
        
        JPanel dropdowns1 = new JPanel();
        dropdowns1.setLayout(new FlowLayout());
        JPanel dropdowns2 = new JPanel();
        dropdowns2.setLayout(new FlowLayout());
        JPanel dropdowns = new JPanel();
        dropdowns.setLayout(new BorderLayout());
//        dropdowns.setBounds(0, 0, frameSize.width, 100);
        
        clearConsole.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		textArea.setText("");
        		editorFrame.repaint();
        	}
        });
        reloadInstructors.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		Worker.updateInstructors();
        		editorFrame.repaint();
        	}
        });
        reloadCourses.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		Worker.updateCourses();
        		editorFrame.repaint();
        	}
        });
        
		final JComboBox course1 = new JComboBox(Worker.coursesToArray());
		course1.setSelectedIndex(0);
		Worker.setCurrCourse1((String)course1.getSelectedItem());
		course1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String num = (String)course1.getSelectedItem();
				Worker.setCurrCourse1(num);
			}
		});
		
		final JComboBox course2 = new JComboBox(Worker.coursesToArray());
		course2.setSelectedIndex(0);
		Worker.setCurrCourse2((String)course2.getSelectedItem());
		course2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String num = (String)course2.getSelectedItem();
				Worker.setCurrCourse2(num);
			}
		});
		
		final JComboBox course3 = new JComboBox(Worker.coursesToArray());
		course3.setSelectedIndex(0);
		Worker.setCurrCourse3((String)course3.getSelectedItem());
		course3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String num = (String)course3.getSelectedItem();
				Worker.setCurrCourse3(num);
			}
		});
		
		final JComboBox course4 = new JComboBox(Worker.coursesToArray());
		course4.setSelectedIndex(0);
		Worker.setCurrCourse4((String)course4.getSelectedItem());
		course4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String num = (String)course4.getSelectedItem();
				Worker.setCurrCourse4(num);
			}
		});
		
		final JComboBox course5 = new JComboBox(Worker.coursesToArray());
		course5.setSelectedIndex(0);
		Worker.setCurrCourse5((String)course5.getSelectedItem());
		course5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String num = (String)course5.getSelectedItem();
				Worker.setCurrCourse5(num);
			}
		});
		
		final JComboBox course6 = new JComboBox(Worker.coursesToArray());
		course6.setSelectedIndex(0);
		Worker.setCurrCourse6((String)course6.getSelectedItem());
		course6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String num = (String)course6.getSelectedItem();
				Worker.setCurrCourse6(num);
			}
		});
		
		final JComboBox course7 = new JComboBox(Worker.coursesToArray());
		course7.setSelectedIndex(0);
		Worker.setCurrCourse7((String)course7.getSelectedItem());
		course7.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String num = (String)course7.getSelectedItem();
				Worker.setCurrCourse7(num);
			}
		});
		
		final JComboBox course8 = new JComboBox(Worker.coursesToArray());
		course8.setSelectedIndex(0);
		Worker.setCurrCourse8((String)course8.getSelectedItem());
		course8.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String num = (String)course8.getSelectedItem();
				Worker.setCurrCourse8(num);
			}
		});
		
		final JComboBox instructors = new JComboBox(Worker.instructorsToArray());
		instructors.setSelectedIndex(0);
		String name = (String)instructors.getSelectedItem();
		int[] indexes = Worker.setCurrName(name);
		course1.setSelectedIndex(indexes[0]);
		course2.setSelectedIndex(indexes[1]);
		course3.setSelectedIndex(indexes[2]);
		course4.setSelectedIndex(indexes[3]);
		course5.setSelectedIndex(indexes[4]);
		course6.setSelectedIndex(indexes[5]);
		course7.setSelectedIndex(indexes[6]);
		course8.setSelectedIndex(indexes[7]);
		instructors.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String name = (String)instructors.getSelectedItem();
				int[] indexes = Worker.setCurrName(name);
				course1.setSelectedIndex(indexes[0]);
				course2.setSelectedIndex(indexes[1]);
				course3.setSelectedIndex(indexes[2]);
				course4.setSelectedIndex(indexes[3]);
				course5.setSelectedIndex(indexes[4]);
				course6.setSelectedIndex(indexes[5]);
				course7.setSelectedIndex(indexes[6]);
				course8.setSelectedIndex(indexes[7]);
			}
		});
		
		final JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Worker.saveInfo();
			}
		});
		
		dropdowns1.add(new JLabel("Instructor"));
		dropdowns1.add(instructors);
		dropdowns1.add(new JLabel("Class 1"));
		dropdowns1.add(course1);
		dropdowns1.add(new JLabel("Class 2"));
		dropdowns1.add(course2);
		dropdowns1.add(new JLabel("Class 3"));
		dropdowns1.add(course3);
		dropdowns1.add(save);
		
		dropdowns2.add(new JLabel("Class 4"));
		dropdowns2.add(course4);
		dropdowns2.add(new JLabel("Class 5"));
		dropdowns2.add(course5);
		dropdowns2.add(new JLabel("Class 6"));
		dropdowns2.add(course6);
		dropdowns2.add(new JLabel("Class 7"));
		dropdowns2.add(course7);
		dropdowns2.add(new JLabel("Class 8"));
		dropdowns2.add(course8);
		
		dropdowns.add(dropdowns1, BorderLayout.NORTH);
		dropdowns.add(dropdowns2, BorderLayout.SOUTH);
		
		
		editorFrame.repaint();
		editorFrame.repaint();
		editorFrame.repaint();

		editorFrame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
		editorFrame.getContentPane().add(dropdowns, BorderLayout.NORTH);
		editorFrame.getContentPane().add(controls, BorderLayout.SOUTH);
		editorFrame.setVisible(show);
		Worker.initPrint();

	}

	/**
	 * Taken from the example code referenced above. Evidently it sets up the
	 * text piping from sysout to the text area in the editor window.
	 */
	public synchronized void textStreamInit() {
		try {
			PipedOutputStream pout = new PipedOutputStream(this.pin);
			System.setOut(new PrintStream(pout, true));
		} catch (java.io.IOException io) {
			textArea.append("Couldn't redirect STDOUT to this console\n"
					+ io.getMessage());
		} catch (SecurityException se) {
			textArea.append("Couldn't redirect STDOUT to this console\n"
					+ se.getMessage());
		}

		try {
			PipedOutputStream pout2 = new PipedOutputStream(this.pin2);
			System.setErr(new PrintStream(pout2, true));
		} catch (java.io.IOException io) {
			textArea.append("Couldn't redirect STDERR to this console\n"
					+ io.getMessage());
		} catch (SecurityException se) {
			textArea.append("Couldn't redirect STDERR to this console\n"
					+ se.getMessage());
		}
		

		quit = false; // signals the Threads that they should exit

		// Starting two seperate threads to read from the PipedInputStreams
		//
		reader = new Thread(this);
		reader.setDaemon(true);
		reader.start();
		//
		reader2 = new Thread(this);
		reader2.setDaemon(true);
		reader2.start();
	}

	/**
	 * Standard JSwing event method. When the GUI window is closed, all threads
	 * are closed safely. Only the main window uses this method, so that only
	 * closing the main window closes the application.
	 */
	public synchronized void windowClosed(WindowEvent evt) {
		quit = true;
		this.notifyAll(); // stop all threads
		try {
			reader.join(1000);
			pin.close();
		} catch (Exception e) {
		}
		try {
			reader2.join(1000);
			pin2.close();
		} catch (Exception e) {
		}
		System.exit(0);
	}

	/**
	 * No idea what this does. Google it.
	 */
	public synchronized void windowClosing(WindowEvent evt) {
		mainFrame.setVisible(false); // default behavior of JFrame
		mainFrame.dispose();
	}

	/**
	 * Standard threaded GUI method. Here it's used to manage the sysout text
	 * threads.
	 */
	public synchronized void run() {
		try {
			while (Thread.currentThread() == reader) {
				try {
					this.wait(100);
				} catch (InterruptedException ie) {
				}
				if (pin.available() != 0) {
					String input = this.readLine(pin);
					textArea.append(input);
				}
				if (quit)
					return;
			}

			while (Thread.currentThread() == reader2) {
				try {
					this.wait(100);
				} catch (InterruptedException ie) {
				}
				if (pin2.available() != 0) {
					String input = this.readLine(pin2);
					textArea.append(input);
				}
				if (quit)
					return;
			}
		} catch (Exception e) {
			textArea.append("\nConsole reports an Internal error.");
			textArea.append("The error is: " + e);
		}

	}

	/**
	 * Threading manager for sysout piping from aforementioned example code. No,
	 * I don't know what it does.
	 * 
	 * @param in
	 *            A PipedInputStream, duh
	 * @return some sort of string obviously
	 * @throws IOException
	 *             if there's an IOException, why do I have to describe this
	 */
	public synchronized String readLine(PipedInputStream in) throws IOException {
		String input = "";
		do {
			int available = in.available();
			if (available == 0)
				break;
			byte b[] = new byte[available];
			in.read(b);
			input = input + new String(b, 0, b.length);
		} while (!input.endsWith("\n") && !input.endsWith("\r\n") && !quit);
		return input;
	}

}