package wang.group.study;

import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.util.HashMap;

/**
 * GroupButtonMap class has two Hashmaps to provide easy access from RadoButton
 * to Group and from Group ID to Radio button.
 */
class GroupButtonMap {

    int groupID = -1;
    String groupName = null;
    ButtonGroup buttonGroup = new ButtonGroup();
    public HashMap<JRadioButton, Group> buttonMap = new HashMap<JRadioButton, Group>();
    public HashMap<Group, JRadioButton> groupMap = new HashMap<Group, JRadioButton>();

    public GroupButtonMap() {
    }

    public void add(JRadioButton button, Group group) {
        button.setFont(new Font("verdana", Font.PLAIN, 10));
        buttonGroup.add(button);
        buttonMap.put(button, group);
        groupMap.put(group, button);
    }

    public void remove(Group group) {
        buttonGroup.remove(groupMap.get(group));
        buttonMap.remove(groupMap.get(group));
        groupMap.remove(group);
    }

    public Group get(JRadioButton button) {
        return buttonMap.get(button);
    }

    public JRadioButton get(Group group) {
        return groupMap.get(group);
    }

    public Object[] getGroupArray() {
        return groupMap.keySet().toArray();
    }
}
