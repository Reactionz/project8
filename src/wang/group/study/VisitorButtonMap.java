package wang.group.study;

import javax.swing.JCheckBox;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.util.HashMap;
import java.util.Set;

/**
 * VisitorButtonMap class has two Hashmaps to provide easy access from
 * RadoButton to Group and from Group ID to Radio button.
 */
class VisitorButtonMap {

   public int selected = 0;
   JCheckBox button = null;
   HashMap<JCheckBox, Visitor> buttonMap = new HashMap<JCheckBox, Visitor>();
   HashMap<Visitor, JCheckBox> visitorMap = new HashMap<Visitor, JCheckBox>();

   public VisitorButtonMap() {
   }

   public void clear() {
      selected = 0;
      buttonMap.clear();
      visitorMap.clear();
   }

   public Object[] getAllCheckBox() {
      Set<JCheckBox> set = buttonMap.keySet();
      return set.toArray();
   }

   public void add(JCheckBox button, Visitor visitors) {
      button.setFont(new Font("verdana", Font.PLAIN, 10));
      buttonMap.put(button, visitors);
      visitorMap.put(visitors, button);
   }

   public void remove(Visitor visitor) {
      button = visitorMap.get(visitor);
      if (button.isSelected())
         selected--;
      buttonMap.remove(button);
      visitorMap.remove(visitor);
   }

   public Visitor get(JCheckBox chkBox) {
      return buttonMap.get(chkBox);
   }

   public JCheckBox get(Visitor visitor) {
      return visitorMap.get(visitor);
   }

   public void setStatus(JCheckBox button) {
      buttonMap.get(button).checked = button.isSelected();
      selected += button.isSelected() ? 1 : -1;
   }

}
