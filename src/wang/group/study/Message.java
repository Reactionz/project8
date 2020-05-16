/* Message class define a set of messages sent between the chatroom server
 *      and  chatroom visitors.
 *  Data members:
 *  	. type    : an integer to indicate kind of message
 *  	. body    : mainly contan the  message sent out to some or all visitors.
 * 	. others  : a string contains other information that not in message body. 
*/

package wang.group.study;

import java.io.*;
import java.util.*;

public class Message implements Serializable {

	public static final int ADDGROUP = 10, // Message: server received : (ADDGROUP, new_group_name, null).
			// visitor receied : (ADDGROUP, new_group_info, group_list)

			REMOVEGROUP = 11, // Message: server received : (REMOVEGROUP, to_remove_group_ID, null).
			// visitor receied : (REMOVEGROUP, removed_group_ID, remaininggroup_list)

			GETGROUPS = 12, // Message: server received : (GETGROUPS, null, null)
			// visitor received: (GETGROUPS, null, group_listl )
			// The visitor's interface sends this message to server as the first message.
			// Then the client software will converts the group_list into VGroup objec which
			// is
			// a vector of groups with related functions.,
			// and uses the VGroup object to display the list of groups

			SYNCHRONIZE = 13, // Message: server received : (SYNCHRONIZED, null, null).
			// visitor receied : (SYNCHRONIZED, null, group_list)

			CHANGEGROUP = 14, // Message: server received : ( CHANGEGROUP, to_gId, null)
			// visitro received: ( CHANGEGROUP, visitor_info, to_group_visitors)
			// The server received the message from a visitors for changing group. The
			// server change the group. The list of visitor (vId_v_name) are sent back
			// to client. The visitor requested the change will renew the list of visitors
			// and the other vistors will either delete or add the requester to or from
			// their visitor list.
			PUBLIC = 0, // Message: server received : (PUBLIC, body, null).
			// visitor received: (PUBLIC, body, sender_info);
			PRIVATE = 1, // Message: server received : (PRIVATE, body, vID_list)
			// visitor received: (PRIVATE, body, vID_list );
			LOGIN = 2, // Message: Server received : (LOGIN, group_id, login_visitor_name)
			// visitor received: (LOGIN, loginz_visitorinfo, group_visitor_list ).
			// When client software receiced login body, it uses grou_visitor_list
			// to create a vector of visitors, and display the list of vectors.
			LOGOUT = 3; // Message: server received : (LOGOUT, null, null))
	// visitor received: (LOGOUT, logout_sisitor, visitor_list)
	// The client uses the visitor_list to update its visitor list.

	public static final String NAME_SEPARATOR = ";"; // string used to separate visitor names.
	public int type = PUBLIC; // type of message defined above

	// body contains message to receivers, or name if message type is
	// eiehr LOGIN or LOGOUT type.
	public String body = "";

	// hold list of receicer names. If it is null or empty, all visitors
	// receivers.
	public String others = ""; // null, one or more visitor names.

	public Message() {
		set(PUBLIC, "invalid", null);
	}

	public Message(Message m) {
		set(m.type, m.body, m.others);
	}

	public Message(int type, String body, String others) {
		set(type, body, others);
	}

	public void set(int type, String body, String others) {
		this.type = type;
		this.body = body == null ? null : new String(body);
		this.others = (others == null) ? null : new String(others);

	}

	public String toString() {
		return String.format("%3d %s %s", type, body, others);
	}

	public Vector<String> getReceiverNames() {
		if (others == null)
			return null;
		Vector<String> v = new Vector<String>(10);
		if (others.trim().equals(""))
			return v;
		else {
			StringTokenizer token = new StringTokenizer(others, NAME_SEPARATOR);
			while (token.hasMoreTokens())
				v.add(token.nextToken());
		}
		Collections.sort(v);
		return v;
	}
}
