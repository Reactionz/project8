package wang.group.study;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;

public class Group implements Comparable<Group>, Serializable {

	// data members of group
	public int ID = -1; // group ID assigned by Server.
	public String name = null;
	public int count = 0;
	public Vector<Visitor> vecVisitor = new Vector<Visitor>(5); // new Vector<Visitor>(5);

	public Visitor get(int k) {
		if (vecVisitor.size() < 0 || k >= vecVisitor.size())
			return null;
		return vecVisitor.get(k);
	}

	public void add(Visitor v) {
		count++;
		vecVisitor.add(v);
	}

	public void remove(Visitor v) {
		count--;
		vecVisitor.remove(v);
	}

	public int size() {
		return vecVisitor.size();
	}

	public void clear() {
		count = 0;
		if (vecVisitor == null || vecVisitor.size() < 1)
			return;
		Visitor v;
		for (int i = 0; i < vecVisitor.size(); i++) {
			v = vecVisitor.get(i);
			try {
				if (v.out != null)
					v.out.close();
			} catch (IOException e) {
			}
		}
		vecVisitor.clear();
	}

	public void add(String visitorList) {
		// System.out.printf("Group.add(%s).\n", visitorList);
		StringTokenizer tzer = new StringTokenizer(visitorList, ";_ ");
		String vName;
		int vID, i = 0;
		while (tzer.hasMoreTokens()) {
			vName = new String(tzer.nextToken());
			vID = Integer.parseInt(tzer.nextToken());
			add(new Visitor(vID, vName, false, null));
			count++;
		}
	}

	public Visitor find(int vID) {
		for (int i = 0; i < vecVisitor.size(); i++)
			if (vID == vecVisitor.get(i).ID)
				return vecVisitor.get(i);
		return null;
	}

	public static Group toGroup(String gname_ID_size) {
		StringTokenizer tzer = new StringTokenizer(gname_ID_size, "_ ");
		String gName = new String(tzer.nextToken());
		int gID = Integer.parseInt(tzer.nextToken());
		int size = Integer.parseInt(tzer.nextToken()); // get the number of visitors
		return new Group(gID, gName, size);
	}

	// constructor(s) and other function members.
	public Group() {
	}

	public Group(Group grp) {
		ID = grp.ID;
		name = new String(grp.name);
		count = grp.count;
	}

	public Group(int id) {
		ID = id;
	}

	public Group(int id, String name) {
		ID = id;
		this.name = new String(name);
	}

	public Group(int id, String name, int cnt) {
		ID = id;
		this.name = new String(name);
		this.count = cnt;
	}

	// Return a string of vname_ID; vname_ID; ....
	public String visitorList() {
		return visitorList(true) + "; " + visitorList(false);
	}

	// Return a string of vname_ID; vname_ID; .... of selected or not-selected
	// vecVisitor.
	public String visitorList(boolean cked) {
		StringBuffer strBuff = new StringBuffer();
		Visitor v;
		int size = vecVisitor.size() - 1;
		for (int i = 0; i <= size; i++) {
			v = vecVisitor.get(i);
			if (v.checked == cked)
				strBuff.append(v + (i < size ? "; " : ""));
		}
		return strBuff.toString();
	}

	// Input string: vname_id; vname_id; ...
	// Create and Return a group with visitors which are creted based up input
	// string.
	public Group visitorListToGroup() {
		String str = this.visitorList();
		Group grp = new Group();
		StringTokenizer tzer = new StringTokenizer(str, ";_ ");
		String vName;
		int vID;
		while (tzer.hasMoreTokens()) {
			vName = new String(tzer.nextToken());
			vID = Integer.parseInt(tzer.nextToken());
			grp.add(new Visitor(vID, vName, false, null));
		}
		return grp;
	}

	public String toString() {
		return String.format("%s_%d %d", name, ID, count);
	}

	// Make a list of VID1; VID2; ...; VIDn from vecVisitors of a group
	public String visitorIDList() {
		StringBuffer buf = new StringBuffer();
		int size = vecVisitor.size() - 1;
		for (int i = 0; i <= size; i++)
			buf.append(vecVisitor.get(i).ID + (i < size ? "; " : ""));
		return buf.toString();
	}

	// Return a list of VIDs of selected or not selected visitors .
	public String visitorIDList(boolean ck) {
		StringBuffer buf = new StringBuffer();
		int count = 0, size = vecVisitor.size() - 1;
		for (int i = 0; i <= size; i++) {
			if (vecVisitor.get(i).checked == ck) {
				buf.append((count == 0 ? "" : "; ") + vecVisitor.get(i).ID);
				count++;
			}
		}
		return buf.toString();
	}

	public int[] visitorIDListToArray(String vidList) {
		int size = vecVisitor.size();
		// The array size may be smaller than size of visitors
		// since some of visitorm may not checked.
		int[] a = new int[10]; // new int[size()];
		int count = 0;
		StringTokenizer tkn = new StringTokenizer(vidList, "; ");
		while (tkn.hasMoreTokens()) {
			a[count] = Integer.parseInt(tkn.nextToken());
			count++;
		}
		if (count < 1)
			return null;
		return Arrays.copyOfRange(a, 0, count);
	}

	public int compareTo(Group g) {
		return ID - g.ID;
	}

	public boolean equals(Group g) {
		return ID == g.ID;
	}

	public synchronized void broadcast(Message message) {
		if (message == null)
			return;
		Iterator<Visitor> itr = vecVisitor.iterator();
		ObjectOutputStream out;
		Visitor vst;
		// System.out.printf("\n\n\t\tMessage [%s] is broadcasted to [%d] visitors\n",
		// message, vecVisitor.size() );
		while (itr.hasNext()) {
			try {
				vst = itr.next();
				out = vst.out;
				if (out == null) {
					System.out.printf("\t\t\t[%s] sent to [%s]\n", message.body, vst);
				} else {
					out.writeObject(message);
					out.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} // end of while
	}

	public synchronized void broadcast(Message message, String vIDList) {

		if (message == null || vIDList == null || vIDList.trim().length() < 1 || vecVisitor.size() < 1)
			return;
		int[] vIDArray = visitorIDListToArray(vIDList);
		// keep both list in order.
		Arrays.sort(vIDArray);
		Collections.sort(vecVisitor);
		Iterator<Visitor> itr = vecVisitor.iterator();

		ObjectOutputStream out;

		int count = 0, aID, vID;
		Visitor vst;
		while (count < vIDArray.length) {
			aID = vIDArray[count];
			do {
				if (!itr.hasNext())
					return;
				vst = itr.next();
				vID = vst.ID;
			} while (aID > vID);
			if (aID == vID) {
				try {
					out = vst.out;
					if (out == null)
						System.out.printf("\t\t\t[%s] sent to [%s]\n", message.body, vst);
					else {
						out.writeObject(message);
						out.flush();
					}
				} catch (IOException e) {
				}
				count++;
			}
		} // end of while-loop
	} // endo of broadcast( Message, string )
}
