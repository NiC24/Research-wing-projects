import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.fazecast.jSerialComm.SerialPort;

/**
 * Project Name - GUI for Fault Detection Created with Eclipse IDE Author -
 * Nihal Chirayath
 **/
public class Main {

	static SerialPort chosenPort;
	static int x = 0;

	public static void main(String[] args) {

		JFrame window = new JFrame();
		window.setTitle("Machine Fault Detector");
		window.setSize(1300, 700);
		window.setLayout(new BorderLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// DD box , connect button and their placement at the top of the window
		JComboBox<String> portList = new JComboBox<String>();
		JButton connectButton = new JButton("Connect");
		JPanel topPanel = new JPanel();
		topPanel.add(portList);
		topPanel.add(connectButton);
		window.add(topPanel, BorderLayout.NORTH);

		// Printing Current Values
		JLabel soundl = new JLabel(
				"<html><span style='font-size:15px'>" + "Noice from the machine:" + "</span></html>");
		JLabel soundo = new JLabel(
				"<html><span style='font-size:15px'>" + " &lt;Not Connected&gt;  " + "</span></html>");
		JPanel SidePanel = new JPanel();
		SidePanel.add(soundl);
		SidePanel.add(soundo);

		window.add(SidePanel, BorderLayout.PAGE_END);

		// DD box items
		SerialPort[] portNames = SerialPort.getCommPorts();
		for (int i = 0; i < portNames.length; i++)
			portList.addItem(portNames[i].getSystemPortName());

		// Creating graph
		XYSeries series = new XYSeries("Noise");
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		JFreeChart chart = ChartFactory.createXYLineChart("Noise Chart", "Time (seconds)", "Decibels", dataset);
		window.add(new ChartPanel(chart), BorderLayout.CENTER);

		// Configuring the connect button and thread to listen for data
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (connectButton.getText().equals("Connect")) {
					// attempt to connect to the serial port
					chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
					chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
					if (chosenPort.openPort()) {
						connectButton.setText("Disconnect");
						portList.setEnabled(false);
					}

					// Creating a thread that listens for incoming text and
					// populates the graph
					Thread thread = new Thread() {
						@Override
						public void run() {
							Scanner scanner = new Scanner(chosenPort.getInputStream());
							scanner.nextFloat();
							scanner.nextLine();
							// intializing counters to show the error message
							int countp = 0;
							while (scanner.hasNextLine()) {
								try {
									String line = scanner.nextLine();
									float number = Float.parseFloat(line);
									soundo.setText(
											"<html><span style='font-size:15px'>" + line + " PPM " + "</span></html>");
									series.add(x++, number);
									// Checking for abnormal conditions
									// PPM
									if (number > 80) {
										if (countp > 5) {
											Toolkit.getDefaultToolkit().beep();
											JOptionPane.showMessageDialog(window,
													"Please Check the Machine as it is emmitting unusually loud sounds ",
													"Machine fault may occur", JOptionPane.WARNING_MESSAGE);
											countp = 0;
										} else {
											Toolkit.getDefaultToolkit().beep();
											JOptionPane.showMessageDialog(window,
													"Noise from machine, unsually Loud !! ", "Unusually Loud",
													JOptionPane.WARNING_MESSAGE);
											countp++;
										}
									}

									window.repaint();
								} catch (Exception e) {
								}
							}
							scanner.close();
						}
					};
					thread.start();
				} else {
					// Disconnecting from the serial port
					chosenPort.closePort();
					portList.setEnabled(true);
					connectButton.setText("Connect");
					series.clear();
					x = 0;
				}
			}
		});

		window.setVisible(true);
	}

}