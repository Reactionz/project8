package wang.group.study;

import java.io.Serializable;
import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;
import java.util.StringTokenizer;

public class VGroup implements Serializable {
	public Vector<Group> vgroup = new Vector<Group>(5);

	public void add(Group grp) {
		vgroup.add(grp);
	}

	public void remove(Group grp) {
		grp = find(grp.ID);
		grp.clear();
		vgroup.remove(grp);
	}

	public void remove(int gID, int vID) {
		Group grp = find(gID);
		if (grp == null)
			return;
		Visitor v = grp.find(vID);
		if (v != null)
			grp.remove(v);
		if (grp.size() == 0)
			vgroup.remove(grp);
	}

	public int size() {
		return vgroup.size();
	}

	public void clear() {
		// move/clear all visitors in each group.
		for (int i = 0; i < vgroup.size(); i++)
			vgroup.get(i).clear();
		vgroup.clear(); // remove all group from this v_group.
	}

	public Group find(int gID) {
		for (int i = 0; i < vgroup.size(); i++)
			if (gID == vgroup.get(i).ID)
				return vgroup.get(i);
		return null;
	}

	public Group get(int k) {
		if (k < 0 || k >= vgroup.size())
			return null;
		return vgroup.get(k);
	}

	public boolean changeGroup(Visitor v, Group fromGroup, Group toGroup) {
		if (fromGroup == null || toGroup == null || !fromGroup.vecVisitor.contains(v))
			return false;
		toGroup.add(v);
		fromGroup.remove(v);
		return true;
	}

	// Make sure that the count is the same as size of visitors ineach group.
	public void synchronize() {
		Group grp;
		for (int i = 0; i < size(); i++) {
			grp = get(i);
			grp.count = grp.size();
		}
	}

	// List vgroup in short-form.
	public String toString() {
		StringBuffer buf = new StringBuffer();
		int size = vgroup.size() - 1;
		for (int i = 0; i <= size; i++)
			buf.append(vgroup.get(i).toString() + (i == size ? "" : "; "));
		return buf.toString();
	}

	public String groupList() {
		return this.toString();
	}

	static public VGroup toVGroup(String groupList) {
		VGroup vg = new VGroup();
		StringTokenizer tzer = new StringTokenizer(groupList, ";");
		while (tzer.hasMoreTokens())
			vg.add(Group.toGroup(tzer.nextToken()));
		return vg;
	}

	public void add(String grpList) {
		StringTokenizer tzer = new StringTokenizer(grpList, ";");
		while (tzer.hasMoreTokens())
			vgroup.add(Group.toGroup(tzer.nextToken()));
		/*
		 * String gName; int gID, i = 0; while ( tzer.hasMoreTokens() ) { gName = new
		 * String( tzer.nextToken() ); gID = Integer.parseInt( tzer.nextToken() );
		 * tzer.nextToken(); // get the number of visitors vgroup.add( new Group( gID,
		 * gName )); }
		 */
	}

	public void broadcast(Message addOrRmGrpMsg) {
		int msgType = addOrRmGrpMsg.type;

		// if (msgType != Message.ADDGROUP && msgType != Message.REMOVEGROUP ) return;
		for (int i = 0; i < vgroup.size(); i++)
			vgroup.get(i).broadcast(addOrRmGrpMsg);
	}

}
