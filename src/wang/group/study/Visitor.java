package wang.group.study;

//
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.StringTokenizer;

public class Visitor implements Comparable<Visitor>, Serializable {

    public int ID;
    public String name; // visitor's name
    public boolean checked = true; // check to receive private message
    public ObjectOutputStream out; // output stream to the visitor

    // used on chatroom server side to add visitor to server list.
    public Visitor(ObjectOutputStream out) {
        set(-1, null, true, out);
    }

    public Visitor() {
        set(-1, null, true, null);
    }

    public Visitor(int id) {
        set(id, "", true, null);
    }

    public Visitor(Visitor v) {
        set(v.ID, v.name, v.checked, v.out);
    }

    public Visitor(String name) {
        set(-1, name, true, null);
    }

    public Visitor(String name, boolean c) {
        set(-1, name, c, null);
    }

    public Visitor(int id, String name, boolean c, ObjectOutputStream out) {
        set(id, name, c, out);
    }

    public void set(int id, String n, boolean ck, ObjectOutputStream o) {
        ID = id;
        name = n == null ? null : new String(n);
        checked = ck;
        out = o == null ? null : o;
    }

    @Override
    public int compareTo(Visitor v) {
        return ID - v.ID;
    }

    @Override
    public boolean equals(Object v) {
        return ID == ((Visitor) v).ID;
    }

    public String toString() {
        return String.format("%s_%d", name, ID);
    }

    public String longString() {
        return String.format("%10s_%4d %5s %s", name, ID, (checked ? "true" : false),
                (out == null ? "null" : "not null"));
    }

    public static Visitor toVisitor(String vName_vId) {
        if (vName_vId == null)
            return null;
        StringTokenizer tk = new StringTokenizer(vName_vId, "_; ");
        if (!tk.hasMoreTokens())
            return null;

        String name = tk.nextToken();
        return new Visitor(Integer.parseInt(tk.nextToken()), name, false, null);
    }
}
