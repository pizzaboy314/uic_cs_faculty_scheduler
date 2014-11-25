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
		Worker.init(); //TODO separate println in init from file loading
		
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
        		frame.repaint();
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
        
		final JComboBox rank1 = new JComboBox(Worker.coursesToArray());
		rank1.setSelectedIndex(0);
		Worker.setCurrRank1((String)rank1.getSelectedItem());
		rank1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String num = (String)rank1.getSelectedItem();
				Worker.setCurrRank1(num);
			}
		});
		
		final JComboBox rank2 = new JComboBox(Worker.coursesToArray());
		rank2.setSelectedIndex(0);
		Worker.setCurrRank2((String)rank2.getSelectedItem());
		rank2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String num = (String)rank2.getSelectedItem();
				Worker.setCurrRank2(num);
			}
		});
		
		final JComboBox rank3 = new JComboBox(Worker.coursesToArray());
		rank3.setSelectedIndex(0);
		Worker.setCurrRank3((String)rank3.getSelectedItem());
		rank3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String num = (String)rank3.getSelectedItem();
				Worker.setCurrRank3(num);
			}
		});
		
		final JComboBox instructors = new JComboBox(Worker.instructorsToArray());
		instructors.setSelectedIndex(0);
		String name = (String)instructors.getSelectedItem();
		int[] indexes = Worker.setCurrName(name);
		rank1.setSelectedIndex(indexes[0]);
		rank2.setSelectedIndex(indexes[1]);
		rank3.setSelectedIndex(indexes[2]);
		instructors.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String name = (String)instructors.getSelectedItem();
				int[] indexes = Worker.setCurrName(name);
				rank1.setSelectedIndex(indexes[0]);
				rank2.setSelectedIndex(indexes[1]);
				rank3.setSelectedIndex(indexes[2]);
			}
		});
		
		final JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Worker.saveInfo();
			}
		});
		
		dropdowns.add(new JLabel("Instructor"));
		dropdowns.add(instructors);
		dropdowns.add(new JLabel("1st Pick"));
		dropdowns.add(rank1);
		dropdowns.add(new JLabel("2nd Pick"));
		dropdowns.add(rank2);
		dropdowns.add(new JLabel("3rd Pick"));
		dropdowns.add(rank3);
		dropdowns.add(save);
		frame.repaint();
		frame.repaint();
		frame.repaint();

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