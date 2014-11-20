// Taken from: http://www.comweb.nl/java/Console/Console.html
// Adapted for use with this application by Bryan Spahr

package classes;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class GUIapp extends WindowAdapter implements WindowListener, Runnable {
	private JFrame frame;
	private JTextArea textArea;
	private Thread reader;
	private Thread reader2;
	private boolean quit;

	private final PipedInputStream pin = new PipedInputStream();
	private final PipedInputStream pin2 = new PipedInputStream();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GUIapp() {
		// create all components and add them
		frame = new JFrame("UIC CS Faculty Scheduler");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension((int) (screenSize.width / 2), (int) (screenSize.height / 2));
		int x = (int) (frameSize.width / 2);
		int y = (int) (frameSize.height / 2);
		frame.setBounds(x, y, frameSize.width, frameSize.height);

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
        
        JPanel dropdowns = new JPanel();
        dropdowns.setLayout(new FlowLayout());
        
        clearConsole.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		textArea.setText("");
        	}
        });
        reloadInstructors.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		Worker.updateInstructors();
        		frame.repaint();
        	}
        });
        reloadCourses.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		Worker.updateCourses();
        		frame.repaint();
        	}
        });

		frame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
		frame.getContentPane().add(dropdowns, BorderLayout.NORTH);
		frame.getContentPane().add(controls, BorderLayout.SOUTH);
		frame.setVisible(true);
		frame.addWindowListener(this);

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
		
		Worker.init();
		
		String[] arr = Worker.instructorsToArray();
		JComboBox instructor = new JComboBox(arr);
		instructor.setSelectedIndex(0);
		dropdowns.add(instructor);
		frame.repaint();

	}

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

	public synchronized void windowClosing(WindowEvent evt) {
		frame.setVisible(false); // default behaviour of JFrame
		frame.dispose();
	}

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