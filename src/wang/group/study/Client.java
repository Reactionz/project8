/**  Group Study:
  Things to do:
1: Keep the checked/uncked status after a new visitor is login and a visitir is logged
out.
2: Allow a visitor or client to swithch from current group io another group.
3. Allow visitor to create new group.
5. Convert it to a applet program.
 */
package wang.group.study;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.*;
import java.io.*;
import java.util.Random;
import java.net.*;
import java.util.*;



public class Client extends JFrame {

	static Random rnd = new Random();
	static JFrame clientWindow;
	static NameDialog dialog;
	static RepDialog repDialog;
	static int zipCode;
	static String name;
	String titleStr = "Online Group Study ";
	String hostAddress = "localhost";
//	String hostAddress = "odin.cs.csub.edu";
	// String hostAddress = "delphi.cs.csubak.edu";
	static int port = 9766;
	static int frame_height = 450;
	static int frame_width = 900;
	static int small_width = 280;
	int font_size = 12;
	int small_font_size = 10;
	JLabel lbNameMsg[] = { new JLabel("Name"), new JLabel("Messsage") };
	JTextField tfNameMsg[] = { new JTextField("Anthony"), new JTextField(20) };
	public JTextArea msgBoard = new JTextArea();
	JButton btnChangeGroup = new JButton("Change");
	JButton btnAddGroup = new JButton("Add");
	JButton btnStart = new JButton("Start");
	// JButton btnRemoveGroup = new JButton("Delete");
	// JButton btnSynchronize = new JButton("Sync");
	JPanel plBtnNorth = new JPanel(new BorderLayout());
	JPanel plBtnSouth = new JPanel( new BorderLayout() );
	JPanel plNameMsg[] = { new JPanel(), new JPanel() };
	JPanel plSouth = new JPanel();
	JPanel plVisitors = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JPanel plGroups = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JScrollPane sclEast = new JScrollPane();
	JScrollPane sclWest = new JScrollPane();
	JScrollPane sclGroups = new JScrollPane();
	TitledBorder plVisitorsTitledBorder = new TitledBorder("Members");
	JPanel plWest = new JPanel(new BorderLayout());
	TitledBorder plGroupsTitledBorder = new TitledBorder("Reps");

	Socket chatSocket = null;

	boolean privateMessage = false;
	String privateReceiverIDs;
	String privateReceiverNames;
	boolean justChecked = false;
	Message message = new Message();

	ObjectOutputStream out = null;
	ObjectInputStream in = null;
	VGroup vgroup = new VGroup();
	Group group; // shared by all clients in the same group.
	Visitor visitor = null;
	int groupID = 1;
	String groupName;
	String privateList = "";
	String visitorName;

	Container c = null;
	// static JApplet applet;
	CAgent cAgent = null;

	////// REPRESENTATIVE VARIABLES /////////
	Random rnd2 = new Random();
	Container c2 = null;
	CAgent cAgent2 = null;

	// New Representative Window that should be similar to the other client.
	
	// A different name and it will work on localhost in this case.
	// Change variable name for different hosts.
	String titleStr2 = "Online Support";
	String hostAddress2 = "localhost";
	// String hostAddress = "odin.cs.csub.edu";
	// Port I am working on - 9 - last 3 ID Numbers
	int port2 = 9766;
	// Initial Frame Height and Width, Not Going to force it to make you pick
	// a group maybe.
	int frameHeight2 = 450;
	int frameWidth2 = 900;
	int smallWidth2 = 280;
	int fontSize2 = 12;
	int smallFontSize2 = 10;

	// Labels for the Text Field Boxes
	JLabel lbNameMsg2[] = { new JLabel("Representative"), new JLabel("Message") };

	// The Text Fields to Input Messages and Send.
	JTextField tfNameMsg2[] = { new JTextField("Lawrence"), new JTextField(25) };

	// Message board that will show the entire chat.
	public JTextArea msgBoard2 = new JTextArea();

	// Able to change groups. Might use it to target clients to take out of groups
	// and stuff. *********
	JButton btnChangeGroup2 = new JButton("Change");
	JButton btnAddGroup2 = new JButton("Add");

	// Areas to hold certain fields
	// BtnNorth - Contains Group Change Buttons
	// NameMsg - Contains Panels to Hold Name and Message Text Field

	JPanel plBtnNorth2 = new JPanel(new BorderLayout());
	// JPanel plBtnSouth = new JPanel(new BorderLayout());
	// Panel for Text Fields
	JPanel plNameMsg2[] = { new JPanel(), new JPanel() };
	JPanel plSouth2 = new JPanel();
	// Panel for Visitors and Groups
	JPanel plVisitors2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JPanel plGroups2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	// Scrolling for if the panels get too filled up.
	JScrollPane scrollEast2 = new JScrollPane();
	JScrollPane scrollWest2 = new JScrollPane();
	JScrollPane scrollGroups2 = new JScrollPane();
	// Titles
	TitledBorder plVisitorsTitledBorder2 = new TitledBorder("Members");
	TitledBorder plGroupsTitledBorder2 = new TitledBorder("");
	// Panel for Visitors it seems like but im not sure either lol
	JPanel plWest2 = new JPanel(new BorderLayout());

	Socket chatSocket2 = null;

	// Starting Variables for Private Messaging
	boolean privateMessage2 = false;
	String privateReceiverIDs2; // Who is receiving this private message?
	String privateReceiverNames2; // What are their names?
	boolean justChecked2 = false;
	Message message2 = new Message();

	ObjectOutputStream out2 = null;
	ObjectInputStream in2 = null;
	VGroup vgroup2 = new VGroup();
	Group group2;
	Visitor visitor2 = null;
	// Going to start with picking a random number from 1 to 5;
	// Using Max and Min as Bounds.

	int groupID2 = rnd.nextInt(Names.repNames.length);

	String groupName2;
	String privateList2 = "";
	String visitorName2;

	public Client() {
		sclEast.getViewport().add(plVisitors);
		sclWest.getViewport().add(plWest);
		sclGroups.getViewport().add(plGroups);
		plWest.add(sclGroups, BorderLayout.CENTER);
		c = getContentPane();
		plVisitors.setBorder(plVisitorsTitledBorder);
		plVisitors.setToolTipText("Select Member(s) to send private Message");
		plVisitors.setPreferredSize(new Dimension(130, frame_height));
		plVisitors.setFont(new Font("verdana", Font.PLAIN, small_font_size));
		c.add(sclEast, BorderLayout.EAST);
		c.add(sclWest, BorderLayout.WEST);


		plGroups.setBorder(plGroupsTitledBorder);
		plWest.setToolTipText("Selct a Representative to continue");
		plWest.setPreferredSize(new Dimension(150, frame_height - 100));
		plWest.setFont(new Font("verdana", Font.PLAIN, small_font_size - 70));
		plGroups.setPreferredSize(new Dimension(140, frame_height - 100));
		plGroups.setFont(new Font("verdana", Font.PLAIN, small_font_size));
		// plGroups.setLayout( new BorderLayout() );
		// btnAddGroup.setEnabled(false);
		// btnChangeGroup.setEnabled(false);
		// btnRemoveGroup.setEnabled( false ); btnSynchronize.setEnabled( false );
		btnAddGroup.setToolTipText("Add a new Rep");
		// btnRemoveGroup.setToolTipText ("Remove a group");
		btnChangeGroup.setToolTipText("Change Rep for current client");
		// btnSynchronize.setToolTipText ("Synchronize groups' sizes");

		plBtnNorth.add(btnAddGroup, BorderLayout.WEST);
		plBtnNorth.add(btnChangeGroup, BorderLayout.CENTER);
		// plBtnSouth.add( btnAddGroup, BorderLayout.EAST );
		// plBtnSouth.add( btnRemoveGroup, BorderLayout.WEST );
		plWest.add(plBtnNorth, BorderLayout.NORTH);
		// plWest.add( plBtnSouth, BorderLayout.SOUTH );

		plSouth.setLayout(new BorderLayout());

		c.setBackground(Color.black);
		msgBoard.setBackground(Color.black);
		msgBoard.setForeground(Color.yellow);
		for (int i = 0; i < plNameMsg.length; i++) {
			plNameMsg[i].setLayout(new FlowLayout());
			lbNameMsg[i].setFont(new Font("verdana", Font.PLAIN, font_size));
			tfNameMsg[i].setFont(new Font("verdana", Font.PLAIN, font_size));
			plNameMsg[i].add(lbNameMsg[i]);
			plNameMsg[i].add(tfNameMsg[i]);
			tfNameMsg[i].setEnabled(false);
		}
		tfNameMsg[0].setColumns(8);
		tfNameMsg[0].setForeground(Color.blue);
		tfNameMsg[1].setColumns(20);
		tfNameMsg[1].setToolTipText("Enter message to send and press ENTER");
		tfNameMsg[1].setVisible(false);
		lbNameMsg[1].setVisible(false);
		plSouth.add(plNameMsg[0], BorderLayout.WEST);
		plSouth.add(plNameMsg[1], BorderLayout.CENTER);
		plSouth.add(btnStart, BorderLayout.EAST);		

		msgBoard.setFont(new Font("verdana", Font.PLAIN, font_size + 2));
		c.add(new JScrollPane(msgBoard), BorderLayout.CENTER);
		c.add(plSouth, BorderLayout.SOUTH);


		addListeners();

		setSize(small_width, frame_height);
		setTitle(titleStr);
		makeConnection();
		dialog = new NameDialog(this, true);
		dialog.setVisible(true);
		
		// tfNameMsg[0].setText(Names.visitors[rnd.nextInt(Names.visitors.length)]);
		tfNameMsg[0].setText(name);

		repDialog = new RepDialog(this);
		repDialog.setVisible(true);

	}

	public void stop() {
		try {
			// Make a LOGOUT message, and send out.
			message.set(Message.LOGOUT, null, null);
			Thread thr = new ClientSender(out, new Message(message));
			thr.start();
			try {
				thr.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			if (chatSocket != null)
				chatSocket.close();
			out = null;
			in = null;
			message = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(-1);
	}

	boolean isInt(String numStr) {
		if (numStr == null || numStr.trim().length() < 1)
			return false;
		for (int i = 0; i < numStr.length(); i++)
			if (numStr.charAt(i) < '0' || numStr.charAt(i) > '9')
				return false;
		return true;
	}

	void addListeners() {

		btnAddGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String gName = JOptionPane.showInputDialog(null, "Enter the  group name or cancel");
				gName = gName.replaceAll(";", "").replaceAll("_", "").replaceAll(" ", "");
				sendMessage(new Message(Message.ADDGROUP, gName, null));
			}
		});
		/*
		 * btnRemoveGroup.addActionListener( new ActionListener() { public void
		 * actionPerformed ( ActionEvent e ) { String gID =
		 * JOptionPane.showInputDialog(null, "Enter the  group number or cancel"); if (
		 * ! isInt( gID ) ) return; sendMessage( new Message( Message.REMOVEGROUP, gID,
		 * null) ); }});
		 * 
		 * btnSynchronize.addActionListener( new ActionListener() { public void
		 * actionPerformed ( ActionEvent e ) { sendMessage( new Message(
		 * Message.SYNCHRONIZE, null, null) ); }});
		 */
		btnChangeGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String intStr = JOptionPane.showInputDialog(null, "Enter an int or cancel");
				if (intStr == null)
					return;
				int toGroupID = Integer.parseInt(intStr);
				Group grp = vgroup.find(toGroupID);
				if (grp == null) {
					JOptionPane.showMessageDialog(null, "The group doesn't exist.");
					return;
				}
				groupID = toGroupID;
				sendMessage(new Message(Message.CHANGEGROUP, toGroupID + "", null));
			}
		});

		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				visitorName = new String(name);
				
				if (visitorName.equals("")) {
					JOptionPane.showMessageDialog(null, "Enter your name and press ENTER!");
					return;
				}
				lbNameMsg[1].setVisible(true);
				tfNameMsg[0].setEnabled(false);
				tfNameMsg[1].setVisible(true);
				tfNameMsg[1].setEnabled(true);
				clientWindow.setTitle(titleStr);
				clientWindow.setSize(frame_width, frame_height);

				sendMessage(new Message(Message.LOGIN, groupID + "", visitorName));
				btnStart.setVisible(false);
				return;
			}
		});

		// tfNameMsg[0].addActionListener(new ActionListener() {
		// 	@Override
		// 	public void actionPerformed(ActionEvent e) {
		// 		// visitorName = new String(tfNameMsg[0].getText()).trim();
		// 		// visitorName = new String(name);
				
		// 		// if (visitorName.equals("")) {
		// 		// 	JOptionPane.showMessageDialog(null, "Enter your name and press ENTER!");
		// 		// 	return;
		// 		// }

		// 		// tfNameMsg[0].setEnabled(false);
		// 		// tfNameMsg[1].setEnabled(true);
		// 		// clientWindow.setTitle(titleStr);
		// 		// clientWindow.setSize(frame_width, frame_height);
		// 		// sendMessage(new Message(Message.LOGIN, groupID + "", visitorName));
		// 		// return;
		// 	}
		// });

		tfNameMsg[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = tfNameMsg[1].getText().trim();
				if (msg.length() < 1)
					return;
				msg = visitorName + ": " + msg;
				if (privateMessage)
					sendMessage(new Message(Message.PRIVATE,
							msg + String.format("  \t <private to %s>", privateReceiverIDs), privateReceiverIDs));
				else
					sendMessage(new Message(Message.PUBLIC, msg, null));

				tfNameMsg[1].setText("");
			}
		}); // end of actionPerformed(), anonymos innter class and addListener().

		this.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}

			public void windowClosing(WindowEvent e) {
				stop();
				System.exit(0);
			}
		});
	}

	void makeConnection() {

		try {
			chatSocket = new Socket(hostAddress, port);
			chatSocket2 = new Socket(hostAddress2, port2);
			out = new ObjectOutputStream(chatSocket.getOutputStream());
			in = new ObjectInputStream(chatSocket.getInputStream());
			out2 = new ObjectOutputStream(chatSocket2.getOutputStream());
			in2 = new ObjectInputStream(chatSocket2.getInputStream());
			(cAgent = new CAgent()).start();
			// (cAgent2 = new CAgent()).start();

			// get exiting groups from server.
			message.set(Message.GETGROUPS, null, null);
			sendMessage(message);
			// message2.set(Message.GETGROUPS, null, null);
			// sendMessage(message2);
		} catch (IOException er) {
			System.out.printf("Error in connection to server!\n");
			er.printStackTrace();
			System.exit(-1);
		}
	}

	void sendMessage(Message message) {

		try {
			out.writeObject(message);
			// out2.writeObject(message2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		 * Thread thr = new ClientSender( out, message); thr.start() ; try { thr.join();
		 * } catch (InterruptedException f ) { }
		 */
	}

	// Implementation of Action Listener.
	static int k = 1;
	static String str = null;

	public void paint(Graphics g) {
		super.paint(g);
	}

	public static void main(String arg[]) {
		clientWindow = new Client();
		// clientWindow.setBackground(Color.black);
		// applet = new Client();
		// applet.init();
		clientWindow.setTitle("Select A Representative!");
		clientWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// clientWindow.getContentPane().add( applet );

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				clientWindow.setVisible(true);
			}
		});
	}

	class RepDialog extends JDialog {


		public RepDialog() {
			this(null);
		}

		public RepDialog(final JFrame parent) {
			super(parent);
			initializeGUI();
		}

		public void initializeGUI() {
			scrollEast2.getViewport().add(plVisitors2);
			scrollWest2.getViewport().add(plWest2);
			scrollGroups2.getViewport().add(plGroups2);

			plWest2.add(scrollGroups2, BorderLayout.CENTER);
			c2 = getContentPane();

			plVisitors2.setBorder(plVisitorsTitledBorder2);
			plVisitors2.setToolTipText("Blah blah blah");
			plVisitors2.setPreferredSize(new Dimension(130, frameHeight2));
			plVisitors2.setFont(new Font("Verdana", Font.PLAIN, smallFontSize2));

			c2.add(scrollEast2, BorderLayout.EAST);
			c2.add(scrollWest2, BorderLayout.WEST);

			plGroups2.setBorder(plGroupsTitledBorder2);
			plWest2.setToolTipText("");
			plWest2.setPreferredSize(new Dimension(150, frameHeight2 - 100));
			plWest2.setFont(new Font("Verdana", Font.PLAIN, smallFontSize2 - 70));

			plGroups2.setPreferredSize(new Dimension(150, frameHeight2 - 100));
			plGroups2.setFont(new Font("Verdana", Font.PLAIN, smallFontSize2));

			plGroups2.setLayout( new BorderLayout() );

			plWest2.add(plBtnNorth2, BorderLayout.NORTH);

			plSouth2.setLayout(new BorderLayout());

			c2.setBackground(Color.black);
			msgBoard2.setBackground(Color.white);
			msgBoard2.setForeground(Color.BLUE);

			for (int i = 0; i < plNameMsg2.length; i++) {
				plNameMsg2[i].setLayout(new FlowLayout());
				lbNameMsg2[i].setFont(new Font("Verdana", Font.PLAIN, fontSize2));
				tfNameMsg2[i].setFont(new Font("Verdana", Font.PLAIN, fontSize2));
				plNameMsg2[i].add(lbNameMsg2[i]);
				plNameMsg2[i].add(tfNameMsg2[i]);
				// tfNameMsg[i].setEnabled ( false);
			}

			tfNameMsg2[0].setColumns(8);
			tfNameMsg2[0].setForeground(Color.blue);
			tfNameMsg2[1].setColumns(20);
			tfNameMsg2[1].setToolTipText("Enter message to send and press ENTER");
			plSouth2.add(plNameMsg2[0], BorderLayout.WEST);
			plSouth2.add(plNameMsg2[1], BorderLayout.CENTER);

			msgBoard2.setFont(new Font("Verdana", Font.PLAIN, fontSize2 + 2));

			c2.add(new JScrollPane(msgBoard2), BorderLayout.CENTER);
			c2.add(plSouth2, BorderLayout.SOUTH);

			addListeners();

			setSize(smallWidth2, frameHeight2);
			setTitle(titleStr2);
			// pack();
			// makeConnection(); don't do this twice it seems like
			// but i think i could maybe create a second thread for that window.
			tfNameMsg2[0].setText(Names.repNames[rnd2.nextInt(Names.repNames.length)]);

		}

		void addListeners() {

			tfNameMsg2[0].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					visitorName2 = tfNameMsg2[0].getText().trim();
					if (visitorName2.equals("")) {
						JOptionPane.showMessageDialog(null, "Enter yur name and press ENTER!");
						return;
					}
					tfNameMsg2[0].setEnabled(false);
					tfNameMsg2[1].setEnabled(true);
					repDialog.setTitle(titleStr2);
					repDialog.setSize(frameWidth2, frameHeight2);
					sendMessage(new Message(Message.LOGIN, groupID2 + "", visitorName2));
					return;
				}
			});
	
			tfNameMsg2[1].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String msg2 = tfNameMsg2[1].getText().trim();
					if (msg2.length() < 1)
						return;
					msg2 = visitorName2 + ": " + msg2;
					if (privateMessage2)
						sendMessage(new Message(Message.PRIVATE,
								msg2 + String.format("  \t <private to %s>", privateReceiverIDs2), privateReceiverIDs2));
					else
						sendMessage(new Message(Message.PUBLIC, msg2, null));
	
					tfNameMsg2[1].setText("");
				}
			}); // end of actionPerformed(), anonymos innter class and addListener().
	
			this.addWindowListener(new WindowAdapter() {
				public void windowClosed(WindowEvent e) {
					System.exit(0);
				}
	
				public void windowClosing(WindowEvent e) {
					stop();
					System.exit(0);
				}
			});
		}
	}

	class NameDialog extends JDialog {
		// Labels for the Text Field
		JLabel lblZipCode = new JLabel("Zip Code");
		JLabel lblName = new JLabel("Name");
		
		// Text fields for the zip code and name.
		JTextField tfZipCode = new JTextField(20);
		JTextField tfName = new JTextField(20);

		// Button for start chat
		JButton btnStartChat = new JButton("Start Chat!");
		JButton btnCancel = new JButton("Cancel");

		JLabel lblStatus = new JLabel(" ");

		public NameDialog() {
			this(null, true);
		}

		public NameDialog(final JFrame parent, boolean modal) {
			// Set the Parent JFrame and set the modal dialog to true.
			super(parent, modal);
			tfZipCode.setText("93309");

			// Set up layouts for the 
			JPanel p3 = new JPanel(new GridLayout(2,1));
			p3.add(lblName);
			p3.add(lblZipCode);

			JPanel p4 = new JPanel(new GridLayout(2,1));
			p4.add(tfName);
			p4.add(tfZipCode);
			

			JPanel p1 = new JPanel();
			p1.add(p3);
			p1.add(p4);

			JPanel p2 = new JPanel();
			p2.add(btnStartChat);

			JPanel p5 = new JPanel(new BorderLayout());
			p5.add(p2, BorderLayout.CENTER);
			p5.add(lblStatus, BorderLayout.NORTH);
			lblStatus.setForeground(Color.RED);
			lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
			tfName.setText(Names.visitors[rnd.nextInt(Names.visitors.length)]);

			setLayout(new BorderLayout());
			add(p1, BorderLayout.CENTER);
			add(p5, BorderLayout.SOUTH);
			pack();
			setLocationRelativeTo(null);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});

			btnStartChat.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean check = true;
					try {
						Integer.parseInt(tfZipCode.getText());
					} catch (NumberFormatException nfe) {
						check = false;
					}
					if(tfName.getText().trim() != " " && isInt(tfZipCode.getText().trim())) {
						zipCode = Integer.parseInt(tfZipCode.getText());
						name = tfName.getText().trim();
						parent.setVisible(true);
						setVisible(false);
					} else {
						lblStatus.setText("Try again!");
					}
				}

			});

			btnCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
					parent.dispose();
					System.exit(0);
				}
			});
		
		}

		
	}

	class ClientSender extends Thread {
		ObjectOutputStream out; // OutputStreamWrite derived from Socket.
		Message message; // Message from a chat room client to other clients
		Message message2;
		ObjectOutputStream out2;

		public ClientSender(ObjectOutputStream w, Message s) {
			out = w;
			message = s;

			// out2 = w;
			// message2 = s;

		}

		public void run() {
			try {
				out.writeObject(message);
				// out2.writeObject(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	GroupButtonMap groupMap = new GroupButtonMap();
	VisitorButtonMap visitorMap = new VisitorButtonMap();

	// GroupButtonMap groupMap = new GroupButtonMap();
	// VisitorButtonMap visitorMap = new VisitorButtonMap();

	boolean loggedIn = false;
	boolean loggedIn2 = false;

	class CAgent extends Thread {
		// This seems to control threading of the groups and controlling adding and removing of groups.


		public CAgent() {
		}

		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton aButton = (AbstractButton) actionEvent.getSource();
				switch (aButton.getClass().getName()) {
					case "javax.swing.JRadioButton":
						group = groupMap.get((JRadioButton) aButton);
						groupID = group.ID;
						tfNameMsg[0].setEnabled(false);
						tfNameMsg[1].setEnabled(true);
						for (Component c : plGroups.getComponents())
							c.setEnabled(false);

						// clientWindow.setTitle("Enter your Name");
						groupName = aButton.getText();
						///////////

						group = groupMap.get((JRadioButton) aButton);
						groupID = group.ID;
						tfNameMsg2[0].setEnabled(false);
						tfNameMsg2[1].setEnabled(true);
						for (Component c2 : plGroups2.getComponents())
							c2.setEnabled(false);

						// clientWindow.setTitle("Enter your Name");
						groupName2 = aButton.getText();
						break;
					case "javax.swing.JCheckBox":
						visitorMap.setStatus((JCheckBox) aButton);
						plVisitorsTitledBorder.setTitle(String.format("Members ( %d )", visitorMap.selected));
						privateMessage = (visitorMap.selected > 0);
						plVisitors.revalidate();
						plVisitors.repaint();
						if (privateMessage) {
							privateReceiverIDs = group.visitorIDList(true);
							privateReceiverNames = group.visitorList(true);
						}

						visitorMap.setStatus((JCheckBox) aButton);
						plVisitorsTitledBorder2.setTitle(String.format("Members ( %d )", visitorMap.selected));
						privateMessage2 = (visitorMap.selected > 0);
						plVisitors2.revalidate();
						plVisitors2.repaint();
						if (privateMessage2) {
							privateReceiverIDs2 = group.visitorIDList(true);
							privateReceiverNames2 = group.visitorList(true);
						}
						break;
					default:
						break;
				}

			}
		};

		void addOneGroup(String newGroup_info) {
			Group grp = Group.toGroup(newGroup_info);
			vgroup.add(grp); // add to group vecor.
			JRadioButton button = new JRadioButton(newGroup_info, false); // read a radio button for the group
			button.setEnabled(!loggedIn);
			button.addActionListener(listener); // ad listener for the button.
			groupMap.add(button, grp); // add button-group pair to map.
			plGroups.add(button); // add button a button panel.
			plGroups.revalidate();
			plGroups.repaint(); // repaint button panel.
		}

		void addOneGroup2(String newGroup_info) {

			Group grp2 = Group.toGroup(newGroup_info);
			vgroup.add(grp2); // add to group vecor.
			JRadioButton button2 = new JRadioButton(newGroup_info, false); // read a radio button for the group
			button2.setEnabled(!loggedIn2);
			button2.addActionListener(listener); // ad listener for the button.
			groupMap.add(button2, grp2); // add button-group pair to map.
			plGroups2.add(button2); // add button a button panel.
			plGroups2.revalidate();
			plGroups2.repaint(); // repaint button panel.
		}

		void removeOneGroup(String gID) {
			Group grp = vgroup.find(Integer.parseInt(gID));
			JRadioButton button = groupMap.get(grp);
			vgroup.remove(grp); // add to group vecor.
			groupMap.remove(grp);
			plGroups.remove(button); // add button a button panel.
			plGroups.revalidate();
			plGroups.repaint(); // repaint button panel.
		}

		void removeOneGroup2(String gID) {
			Group grp = vgroup.find(Integer.parseInt(gID));
			JRadioButton button = groupMap.get(grp);
			vgroup.remove(grp); // add to group vecor.
			groupMap.remove(grp);
			plGroups2.remove(button); // add button a button panel.
			plGroups2.revalidate();
			plGroups2.repaint(); // repaint button panel.
		}

		void synchronizeGroups(String group_list) { // update groups, sizes with new counts.

			VGroup vgrp = VGroup.toVGroup(group_list);
			Set<Group> set = groupMap.groupMap.keySet();
			Group grpMap[] = set.toArray(new Group[0]); // .getGroupArray();
			Group grpSvr[] = vgrp.vgroup.toArray(new Group[0]);
			// System.out.printf("\nArray sizes Synchronize(): %d, %d\n", grpMap.length,
			// grpSvr.length);
			Arrays.sort(grpMap);
			Arrays.sort(grpSvr);
			int i = 0, j = 0;
			while (i < grpSvr.length && j < grpMap.length) {

				if (grpSvr[i].ID == grpMap[j].ID) {
					groupMap.get(grpMap[j]).setText(grpSvr[i].toString());
					i++;
					j++;
				} else if (grpSvr[i].ID > grpMap[j].ID) {
					removeOneGroup(grpMap[j].ID + "");
					j++;
				} else {
					addOneGroup(grpSvr[i].toString());
					i++;
				}
			}

			while (i < grpSvr.length) {
				addOneGroup(grpSvr[i].toString());
				i++;
			}

			while (j < grpMap.length) {
				removeOneGroup(grpMap[j].ID + "");
				j++;
			}
			repaintGroupPanel();
		}

		void synchronizeGroups2(String group_list) { // update groups, sizes with new counts.

			VGroup vgrp = VGroup.toVGroup(group_list);
			Set<Group> set = groupMap.groupMap.keySet();
			Group grpMap[] = set.toArray(new Group[0]); // .getGroupArray();
			Group grpSvr[] = vgrp.vgroup.toArray(new Group[0]);
			// System.out.printf("\nArray sizes Synchronize(): %d, %d\n", grpMap.length,
			// grpSvr.length);
			Arrays.sort(grpMap);
			Arrays.sort(grpSvr);
			int i = 0, j = 0;
			while (i < grpSvr.length && j < grpMap.length) {

				if (grpSvr[i].ID == grpMap[j].ID) {
					// groupMap.get(grpMap[j]).setText(grpSvr[i].toString());
					groupMap.get(grpMap[j]).setText(grpSvr[i].toString());

					i++;
					j++;
				} else if (grpSvr[i].ID > grpMap[j].ID) {
					// removeOneGroup(grpMap[j].ID + "");
					removeOneGroup2(grpMap[j].ID + "");
					j++;
				} else {
					// addOneGroup(grpSvr[i].toString());
					addOneGroup2(grpSvr[i].toString());
					i++;
				}
			}

			while (i < grpSvr.length) {
				// addOneGroup(grpSvr[i].toString());
				addOneGroup2(grpSvr[i].toString());

				i++;
			}

			while (j < grpMap.length) {
				// removeOneGroup(grpMap[j].ID + "");
				removeOneGroup2(grpMap[j].ID + "");

				j++;
			}
			// repaintGroupPanel();
			repaintGroupPanel2();
		}

		

		// If there are multiple groups, you would use this.
		void addAllGroups(String groupList) {
			// Visitor Group will add based on the string of how many groups are available.
			vgroup.add(groupList);
			// It will initialize an empty button and group.
			JRadioButton button;
			Group group;
			// Based on the size of the visitor Group created from the group list, add each group.
			for (int i = 0; i < vgroup.size(); i++) {
				// Get the group from the visitor Group
				group = vgroup.get(i);
				// Add the name of the group to the button and set it to false until it gets pressed.
				button = new JRadioButton(group.toString(), false);
				// Add the action listener from CAgent to the radio button.
				button.addActionListener(listener);
				// Add the button and group to the map.
				groupMap.add(button, group);
				// Then add the button.
				plGroups.add(button);
			}
			// Check the groups panel based on the group map (I think) and revalidate and repaint it.
			// plGroups.revalidate();
			// plGroups.repaint();
			repaintGroupPanel();
		}

		void addAllGroups2(String groupList) {
			// Visitor Group will add based on the string of how many groups are available.
			vgroup.add(groupList);
			// It will initialize an empty button and group.
			JRadioButton button;
			Group group;
			// Based on the size of the visitor Group created from the group list, add each group.
			for (int i = 0; i < vgroup2.size(); i++) {
				// Get the group from the visitor Group
				group = vgroup.get(i);
				// Add the name of the group to the button and set it to false until it gets pressed.
				button = new JRadioButton(group.toString(), false);
				// Add the action listener from CAgent to the radio button.
				button.addActionListener(listener);
				// Add the button and group to the map.
				groupMap.add(button, group);
				// Then add the button.
				plGroups2.add(button);
			}
			// Check the groups panel based on the group map (I think) and revalidate and repaint it.
			// plGroups.revalidate();
			// plGroups.repaint();
			repaintGroupPanel2();
		}

		void loginSetup(Visitor v) { // setup visitor name, group variables and window title.
			visitorName = v.toString();
			visitorName2 = v.toString();
			tfNameMsg[0].setText(visitorName);
			visitor = v;
			clientWindow.setTitle(String.format("%s - Representative [%s_%d] Visitor [%s]", titleStr, group.name, group.ID, v));
			// for (Component c : plGroups.getComponents())
			// 	c.setEnabled(false);
			btnChangeGroup.setEnabled(true);
			btnAddGroup.setEnabled(true);
			// btnRemoveGroup.setEnabled( true );
			// btnSynchronize.setEnabled( true );
			loggedIn = true;

			tfNameMsg2[0].setText(visitorName2);
			// visitor2 = v;
			// visitor = v;
			repDialog.setTitle(String.format("%s - Representative [%s_%d] Visitor [%s]", titleStr2, group2.name, group2.ID, v));
			btnChangeGroup2.setEnabled(true);
			btnAddGroup2.setEnabled(true);	
			loggedIn2 = true;

		}

		// void loginSetup2(Visitor v) {
		// 	visitorName2 = v.toString();
		// 	tfNameMsg2[0].setText(visitorName2);
		// 	// visitor2 = v;
		// 	visitor = v;
		// 	repDialog.setTitle(String.format("%s - Representative [%s_%d] Visitor [%s]", titleStr2, group2.name, group2.ID, v));
		// 	btnChangeGroup2.setEnabled(true);
		// 	btnAddGroup2.setEnabled(true);	
		// 	loggedIn2 = true;	
		// }

		void addAllVisitors(String allVisitors) {
			Group grp = new Group();
			grp.add(allVisitors);
			for (int i = 0; i < grp.size(); i++) {
				addOneVisitor(grp.get(i));
			}

		}

		void addAllVisitors2(String allVisitors) {
			Group grp = new Group();
			grp.add(allVisitors);
			for (int i = 0; i < grp.size(); i++) {
				addOneVisitor2(grp.get(i));
			}

		}

		void addOneVisitor(Visitor v) {
			// Takes in the visitor object and adds it to the group.
			group.add(v);
			// Creates a checkbox with the name of that Visitor.
			JCheckBox box = new JCheckBox(v.toString());
			// Add that checkbox and the visitor to the visitor map.
			visitorMap.add(box, v);
			// Add the actionListener from the CAgent Class.
			box.addActionListener(listener);
			// Add the box to the panel to show the new visitor that has been added as well.
			plVisitors.add(box);
		}

		void addOneVisitor2(Visitor v) {
			// Takes in the visitor object and adds it to the group.
			group.add(v);
			// Creates a checkbox with the name of that Visitor.
			JCheckBox box = new JCheckBox(v.toString());
			// Add that checkbox and the visitor to the visitor map.
			visitorMap.add(box,v);
			// Add the actionListener from the CAgent Class.
			box.addActionListener(listener);
			// Add the box to the panel to show the new visitor that has been added as well.
			plVisitors2.add(box);
		}
		
		// A Helper Function for the function above
		void addOneVisitor(String vName_vID) {
			// A different function where it takes in the string of the Visitor and adds it by finding the name and ID.
			Visitor v = Visitor.toVisitor(vName_vID);
			// Uses the function above to actually add the visitor.
			addOneVisitor(v);
		}

		void addOneVisitor2(String vName_vID) {
			// A different function where it takes in the string of the Visitor and adds it by finding the name and ID.
			Visitor v = Visitor.toVisitor(vName_vID);
			// Uses the function above to actually add the visitor.
			addOneVisitor2(v);
		}

		// Probably used in the remove group button.
		void removeAllVisitors() {
			group.clear();
			visitorMap.clear();
			plVisitors.removeAll();
			repaintVisitorPanel();
		}

		void removeAllVisitors2() {
			group2.clear();
			visitorMap.clear();
			plVisitors2.removeAll();
			repaintVisitorPanel2();
		}

		// Remove the visitor from group, visitor-buttom mal, and
		// remove the button from button map and panel.

		// Maybe we can use this function to add a moving function to another group which might be a representative.
		void removeOneVisitor(String vId_vName) {
			// Remove that Visitor
			Visitor v = Visitor.toVisitor(vId_vName);
			// If they don't exist or they don't have a group, return empty.
			if (v == null)
				return;
			if (group == null)
				return;
			// Within the group, find that ID where it assigns a new visitor.
			v = group.find(v.ID);
			// Remove that visitor from the panel of visitors.
			plVisitors.remove(visitorMap.get(v));
			// Remove them from the group they are in.
			group.remove(v);
			// Remove from the visitor map which I'm thinking is the button with their name.
			visitorMap.remove(v);
			// Change the name of the Border to the amount of people within the group.
			plVisitorsTitledBorder.setTitle(String.format("Members ( %d )", visitorMap.selected));
		}

		void removeOneVisitor2(String vId_vName) {
			// Remove that Visitor
			Visitor v = Visitor.toVisitor(vId_vName);
			// If they don't exist or they don't have a group, return empty.
			if (v == null)
				return;
			if (group2 == null)
				return;
			// Within the group, find that ID where it assigns a new visitor.
			v = group2.find(v.ID);
			// Remove that visitor from the panel of visitors.
			plVisitors2.remove(visitorMap.get(v));
			// Remove them from the group they are in.
			group2.remove(v);
			// Remove from the visitor map which I'm thinking is the button with their name.
			visitorMap.remove(v);
			// Change the name of the Border to the amount of people within the group.
			plVisitorsTitledBorder2.setTitle(String.format("Members ( %d )", visitorMap.selected));
		}

		void processGroupChanged(Message changeGroupMsg) { // group list is rececived.
			Visitor v = Visitor.toVisitor(changeGroupMsg.body);
			if (v.ID == visitor.ID) {
				privateMessage = false;
				removeOneVisitor(changeGroupMsg.body); // it is necessary to descease the size of group.

				// groupMap.get(group).setText( String.format("%s_%d %d", group.name, group.ID,
				// group.size()));
				removeAllVisitors();
				group = vgroup.find(groupID);
				groupName = group.toString();
				addAllVisitors(changeGroupMsg.others);
				groupMap.get(group).setSelected(true);
				clientWindow
						.setTitle(String.format("%s:  Representative [%s_%d] Vistor [%s]", titleStr, group.name, group.ID, v));
			} else if (changeGroupMsg.others == null) { // This client is in the visitor of the original group..
				// System.out.printf("%s is to go removed since others == null\n", v );
				removeOneVisitor(changeGroupMsg.body);
				repaintVisitorPanel();
			} else { // For the visitors in the new group of the visitor of changing group.
				addOneVisitor(v);
			}
			// groupMap.get(group).setText( String.format("%s_%d %d", group.name, group.ID,
			// group.size()));
			repaintVisitorPanel();
		}

		void processGroupChanged2(Message changeGroupMsg) { // group list is rececived.
			Visitor v = Visitor.toVisitor(changeGroupMsg.body);
			if (v.ID == visitor2.ID) {
				privateMessage2 = false;
				removeOneVisitor2(changeGroupMsg.body); // it is necessary to descease the size of group.

				// groupMap.get(group).setText( String.format("%s_%d %d", group.name, group.ID,
				// group.size()));
				removeAllVisitors2();
				group2 = vgroup2.find(groupID2);
				groupName2 = group2.toString();
				addAllVisitors2(changeGroupMsg.others);
				groupMap.get(group2).setSelected(true);
				repDialog
						.setTitle(String.format("%s:  Representative [%s_%d] Vistor [%s]", titleStr2, group2.name, group2.ID, v));
			} else if (changeGroupMsg.others == null) { // This client is in the visitor of the original group..
				// System.out.printf("%s is to go removed since others == null\n", v );
				removeOneVisitor2(changeGroupMsg.body);
				repaintVisitorPanel2();
			} else { // For the visitors in the new group of the visitor of changing group.
				addOneVisitor2(v);
			}
			// groupMap.get(group).setText( String.format("%s_%d %d", group.name, group.ID,
			// group.size()));
			repaintVisitorPanel2();
		}

		void repaintVisitorPanel() {
			plVisitors.revalidate();
			plVisitors.repaint();
		}

		void repaintGroupPanel() {
			plGroups.revalidate();
			plGroups.repaint();
		}
		
		
		void repaintVisitorPanel2() {
			plVisitors2.revalidate();
			plVisitors2.repaint();
		}

		void repaintGroupPanel2() {
			plGroups2.revalidate();
			plGroups2.repaint();
		}

		public void run() {
			if (in == null || msgBoard == null )
				return;
			try {
				while (true) {

					// Read message
					try {
						message = (Message) in.readObject();
						// message2 = (Message) in2.readObject();
					} catch (ClassNotFoundException nfdEx) {
						msgBoard.append("Error in readObject() from socket client agent.\n" + nfdEx);
						msgBoard2.append("Error in readObject() from socket client agent.\n" + nfdEx);

						return;
					}

					switch (message.type) {

						case Message.GETGROUPS: // message: ( GETGROUP, null, group_list)
							addAllGroups(message.others); // group list is rececived.

							addAllGroups2(message.others);
							break;

						case Message.ADDGROUP: // message: (ADDGROUP, new_group_info, group_list )
							addOneGroup(message.body); // add the new group.
							synchronizeGroups(message.others); // update groups, sizes with new counts.

							addOneGroup2(message.body);
							synchronizeGroups2(message.body);
							break;

						case Message.REMOVEGROUP: // message: (REMOVEGROUP, removed_gID,, group_list).
							removeOneGroup(message.body); // removethe new group.
							synchronizeGroups(message.others); // update groups, sizes with new counts.

							removeOneGroup2(message.body); // removethe new group.
							synchronizeGroups2(message.others); // update groups, sizes with new counts
							break;
						case Message.SYNCHRONIZE: // message: (SYNCHRONIZE, null,, group_list).
							synchronizeGroups(message.others); // update groups, sizes with new counts.

							synchronizeGroups2(message.others);
							break;

						case Message.CHANGEGROUP: // message received: (CHANGEGROUP, v_info, list_v_info_of_toGroup))
							processGroupChanged(message); // group list is rececived.
							repaintVisitorPanel();

							processGroupChanged2(message); // group list is rececived.
							repaintVisitorPanel2();
							break;

						case Message.LOGIN: // message: (LOGIN, login_visitor, group_member_list)
							group = vgroup.find(groupID);
							// System.out.printf("message.body : [%s] to converted to a
							// visitor\n",message.body);
							Visitor v = Visitor.toVisitor(message.body);
							if ((v.name.compareTo(tfNameMsg[0].getText().trim()) == 0) && !loggedIn) {
								loginSetup(v);
								addAllVisitors(message.others);
							} else {
								addOneVisitor(v);
							}
							repaintVisitorPanel();
							// groupMap.get(group).setText( String.format("%s_%d %d", group.name, group.ID,
							// group.size()));

							///////////////
							msgBoard2.append(message.body + ": logged in.\n");

							group2 = vgroup2.find(groupID2);
							// System.out.printf("message.body : [%s] to converted to a
							// visitor\n",message.body);
							// Visitor v2 = Visitor.toVisitor(message.body);
							if ((v.name.compareTo(tfNameMsg2[0].getText().trim()) == 0) && !loggedIn2) {
								loginSetup(v);
								addAllVisitors2(message.others);
							} else {
								addOneVisitor2(v);
							}
							repaintVisitorPanel2();
							// groupMap.get(group).setText( String.format("%s_%d %d", group.name, group.ID,
							// group.size()));
							msgBoard2.append(message.body + ": logged in.\n");
							break;

						case Message.PUBLIC:
							msgBoard.append(message.body + '\n');
							msgBoard2.append(message.body + '\n');
							break;

						case Message.PRIVATE:
							msgBoard.append(message.body + '\n');
							msgBoard2.append(message.body + '\n');
							break;

						case Message.LOGOUT: // messge Received: LOGOUT, out_visitor, remaing_visitors ).
							removeOneVisitor(message.body);
							// groupMap.get(group).setText( String.format("%s_%d %d", group.name, group.ID,
							// group.size()));
							repaintVisitorPanel();
							msgBoard.append(message.body + ": logged out.\n");

							removeOneVisitor2(message.body);
							// groupMap.get(group).setText( String.format("%s_%d %d", group.name, group.ID,
							// group.size()));
							repaintVisitorPanel2();
							msgBoard2.append(message.body + ": logged out.\n");
							break;
					}
					if (msgBoard.getText().length() > 0)
						msgBoard.setCaretPosition(msgBoard.getText().length() - 1);

					if (msgBoard2.getText().length() > 0)
						msgBoard2.setCaretPosition(msgBoard2.getText().length() - 1);
				} // end of
			} catch (IOException e) {
				msgBoard.append("Chatroom server is down.\n");
				msgBoard2.append("Chatroom server is down.\n");

				return;
			}
		} // end of run()
	} // end of class CAgent, the client agent or receiver.

} // end of class Client
