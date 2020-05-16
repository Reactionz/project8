package wang.group.study;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Chatroom server: Basic functionalities: - Maintain a list of visitors (name,
 * checked, assocoated OutputStream)
 */
public class Server {

	static int nextVisitorID = 0;
	static int nextGroupID = 0;
	static int count = 0;

	static ServerSocket svrSocket = null;
	static VGroup vgroup = new VGroup();
	static Group defaultGroup = new Group();

	Server() {
	}

	static void reloadGroups() {
		Server.nextGroupID = 0;
		Server.nextVisitorID = 0;
		for (int i = 0; i < Names.group.length; i++)
			vgroup.add(new Group(++nextGroupID, Names.group[i]));

	}

	public static void main(String arg[]) {
		// Server fm = new Server() ;
		reloadGroups();
		// ----------------------------------------------------------------
		// Create a server socket listenning on port 8900 which is
		// special port number that allows client and server programs run
		// on the same machine. However, TCP/IP must be installed on that
		// machine even if the client and server are running on the same
		// machine.
		// ----------------------------------------------------------------

		try {
			svrSocket = new ServerSocket(9766);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Socket socket = null;
		Thread thread = null;
		while (true) {
			try {
				socket = svrSocket.accept();
				thread = new Thread(new SAgent(socket, vgroup));
				thread.start();
				System.out.printf("\nClient [%d] is connected.", ++count);
			} catch (IOException e) {
				System.out.printf("Address is already used.");
				// System.exit(0); //arg)e.printStackTrace();
			}
		}
	} // End of Server class

	// -------------------------------------------------------------------------
	// Inner class SAgent is a agent who is reseponsible to collect message from
	// chat client and broadcast to other chaters in the group.
	// -------------------------------------------------------------------------

}

class SAgent implements Runnable {
	VGroup vgroup = null;
	Group group = null;
	Visitor visitor = null;
	Socket socket = null;
	ObjectInputStream in;
	ObjectOutputStream out;
	Message message = null;

	public SAgent(Socket s, VGroup vgroup) {

		socket = s;
		this.vgroup = vgroup;
		try {
			group = Server.defaultGroup;
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
			visitor = new Visitor(++Server.nextVisitorID, "unknown", false, out);
			// visitor = new Visitor( out );
			Server.defaultGroup.add(visitor); // Java vector is thread safe.
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // end of constructor

	public void run() {
		while (true) {
			try {
				// System.out.printf("\nInside SAgent run() before readObject()");
				try {
					message = (Message) in.readObject();
					/// str = (String) in.readObject();
				} catch (ClassNotFoundException nfd) {
				} catch (IOException ioE) {
				} catch (NullPointerException nullPtr) {
					return;
				}

				if (message == null) {
					socket.close();
					return;
				}

				switch (message.type) {

					case Message.GETGROUPS: // (GETGROUPS, null, null )
						// System.out.printf("GETGROUPS-Message received.\n");
						vgroup.synchronize();
						out.writeObject(new Message(message.type, null, vgroup.toString()));
						// System.out.printf("GETGROUPS-Message sent to client..\n");
						break;

					case Message.ADDGROUP: // message received: (ADDGROUP, gName, null)
						// message sent: {ADDGROUP, new_group_info, allgroup_list)
						Group newGroup = new Group(++Server.nextGroupID, new String(message.body), 0);
						vgroup.add(newGroup);
						synchronizeGroupInfo(newGroup.toString());
						break;

					case Message.REMOVEGROUP: // message received: ( REMOVEGROUP, group_ID, null)
						// message sent: ( REMOVEGROUP, removed_group_ID, allgroup_list)
						Group rmdGroup = vgroup.find(Integer.parseInt(message.body));
						if (rmdGroup == null) {
							return;
						}
						vgroup.remove(rmdGroup);
						synchronizeGroupInfo(message.body);
						break;

					case Message.SYNCHRONIZE: // Message received { SYNCHRONIZE, null, null )
						synchronizeGroupInfo(null);
						break;

					case Message.CHANGEGROUP: // (CHANGEGROUP, toGroupID, null )
						int toGroupID = Integer.parseInt(message.body);
						Group toGroup = vgroup.find(toGroupID);
						boolean suc = vgroup.changeGroup(visitor, group, toGroup);
						if (suc) {
							// Let new group know the incoming visitor, and nw visitor need the list of new
							// group.
							vgroup.synchronize();
							toGroup.broadcast(
									new Message(Message.CHANGEGROUP, visitor.toString(), toGroup.visitorList()));
							// Let the original group the outgoing visitor.
							group.broadcast(new Message(Message.CHANGEGROUP, visitor.toString(), null));
							group = toGroup;
						}
						synchronizeGroupInfo(null);
						break;

					case Message.LOGIN: // Message(type, groupID, Vname) received.
						visitor.name = new String(message.others);
						group = vgroup.find(Integer.parseInt(message.body));
						group.add(visitor);
						Server.defaultGroup.remove(visitor);
						// Broadcase message (LOGIN, vName_IDs_list, new_visitor) to visitors in the
						// vgroup.
						group.broadcast(new Message(Message.LOGIN, visitor.toString(), group.visitorList()));
						synchronizeGroupInfo(null);
						break;

					case Message.LOGOUT:
						String outVisitor_info = new String(visitor.toString());
						group.remove(visitor);
						if (group.size() < 1 && Server.defaultGroup.size() < 1) {
							removeGroup();
							if (vgroup.size() < 1)
								Server.reloadGroups();
						} else {
							Message logoutMsg = new Message(message.type, outVisitor_info, group.visitorList());
							group.broadcast(logoutMsg);
						}
						clearAndRemoveVisitor();
						synchronizeGroupInfo(null);
						break;
					case Message.PUBLIC: // msg: PUBLIC, message, null
						group.broadcast(new Message(message.type, message.body, visitor.toString()));
						break;
					case Message.PRIVATE:
						// get a list of visitors from its private list, and boradcast.
						group.broadcast(new Message(message.type, message.body, visitor.toString()), message.others);

						break;
				} // end of switch statement
			} catch (Exception e) {
				if (group != null) {
					// System.out.printf("Group has [%d] visittors before remove.\n", group.size());
					group.remove(visitor);
					// System.out.printf("Group has [%d] visittors after remove.\n", group.size());
				}
				return;
			}
		} // end of while -- loop in run()
	} // end of run()

	void synchronizeGroupInfo(String msgBody) {
		vgroup.synchronize();
		Message msg = new Message(Message.SYNCHRONIZE, null, vgroup.toString());
		vgroup.broadcast(msg);
		Server.defaultGroup.broadcast(msg);
	}

	void clearAndRemoveVisitor() {
		try {
			out.close();
			in.close();
			socket.close();
			visitor = null;
			in = null;
			out = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void removeGroup() {
		String rmdGroupInfo = group.toString();
		vgroup.remove(group);
		group = null;
		// i if ( vgroup.size() > 0 ) // broadcast group removed message other vgroup.
		// vgroup.broadcast( new Message( Message.REMOVEGROUP, vgroup.toString(),
		// rmdGroupInfo ) );
	}

} // end of SSAgent inner class
