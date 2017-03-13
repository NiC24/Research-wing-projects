import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

import sun.audio.*;
import java.io.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


import com.fazecast.jSerialComm.SerialPort;

/**
 * Project Name - GUI for Intruder Alert
 * Created with Eclipse IDE  
 * Author - Nihal Chirayath 
**/ 

public class Main {
	static SerialPort chosenPort;
	static int x = 0;
	static boolean in0 = false ;
	
	static void playSound(String soundFile) {
	    File f = new File(soundFile);
	    try{
	    
	    Clip clip = AudioSystem.getClip();
	    clip.open(AudioSystem.getAudioInputStream(f));
	    clip.start();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    
	}
	
	public static void main(String[] args){
		//Window
				JFrame window = new JFrame();
				window.setTitle("Intruder alert System");
				window.setSize(400, 200);
				window.setLayout(new BorderLayout());
				window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				
		// DD box , connect button and their placement  at the top of the window
				JComboBox<String> portList = new JComboBox<String>();
				JButton connectButton = new JButton("Connect");
				JPanel topPanel = new JPanel();
				topPanel.add(portList);
				topPanel.add(connectButton);
				window.add(topPanel, BorderLayout.NORTH);
				JPanel midPanel = new JPanel();
				JPanel endPanel = new JPanel();
				JLabel label = new JLabel("No Intruder Detected");
				JLabel label1 = new JLabel("Not Connected");
				JButton reset = new JButton("Reset");
				midPanel.add(label);
				midPanel.add(label1);
				endPanel.add(reset);
				window.add(midPanel,BorderLayout.CENTER);
				window.add(endPanel,BorderLayout.AFTER_LAST_LINE);
			//  DD box items
		 		SerialPort[] portNames = SerialPort.getCommPorts();
				for(int i = 0; i < portNames.length; i++)
					portList.addItem(portNames[i].getSystemPortName());
				
				
				
				// Configuring the connect button and  thread to listen for data
				connectButton.addActionListener(new ActionListener(){
					@Override public void actionPerformed(ActionEvent arg0) {
						if(connectButton.getText().equals("Connect")) {
							// attempt to connect to the serial port
							chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
							chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
							if(chosenPort.openPort()) {
								connectButton.setText("Disconnect");
								portList.setEnabled(false);
							}
							
							// Creating a  thread that listens for incoming text and populates the graph
							Thread thread = new Thread(){
								@Override public void run() {
									Scanner scanner = new Scanner(chosenPort.getInputStream());
									//Scrapping the 1st set of values
									String line = scanner.nextLine();
									String detected = "Motion detected!";
									
									while(scanner.hasNextLine()) {
										try {
											line = scanner.nextLine();
											label1.setText("<html><span style='font-size:20px'>"+line+"</span></html>");
									if(line.equalsIgnoreCase(detected))
									{
										 in0 = true ;
										}
									if ( in0 ){
												label.setText("<html><span style='font-size:20px'>INTRUDER DETECTED!!!!</span></html>");
												
												playSound("1.wav");
									}
									reset.addActionListener(new ActionListener() {
										
										@Override
										public void actionPerformed(ActionEvent e) {
											// TODO Auto-generated method stub
											in0 = false ;
											label.setText("Alarm Reseted");
										}
									});
											window.repaint();
										} catch(Exception e) {}
									}
									scanner.close();
								}
							};
							thread.start();
						} else {
							//Disconnecting  from the serial port
							chosenPort.closePort();
							portList.setEnabled(true);
							connectButton.setText("Connect");
						}
					}
				});
				
				window.setVisible(true);
			}

		}
