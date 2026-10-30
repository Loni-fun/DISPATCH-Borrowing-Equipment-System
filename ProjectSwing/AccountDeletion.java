package ProjectSwing;

import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AccountDeletion {
    public static String checkCanDelete(int id, String role){
        AdminDisplay.Request.loadFile();
        if (AdminDisplay.Request.countActiveEquipment(id, role) > 0) {
            return "There are still pending, approved or borrowed items on this account.\nThose must be finished first.";
        }
        return null;
    }

    public static void removeAccount(int id, String role, boolean removeId){
        Account user = PasswordReset.findAccount(id, role);
        if (user == null) {
            return;
        }
        ProfilePic.delete(role, id);   // no pictures left behind
        ProfilePanel.deleteGender(role, id);
        if (removeId) {
            user.remove();
        } else {
            user.clearAccount();
            user.save();
        }
    }

    public static boolean adminDelete(Component parent, int id, String role){
        String problem = checkCanDelete(id, role);
        if (problem != null) {
            JOptionPane.showMessageDialog(parent, problem);
            return false;
        }
        Object[] options = {"Remove account only", "Delete ID completely", "Cancel"};
        int pick = JOptionPane.showOptionDialog(parent,
                "What do you want to do with " + role + " " + id + "?\n\n"
                + "Remove account only: the name and password are erased,\n"
                + "the ID stays so the real owner can sign up again.\n\n"
                + "Delete ID completely: the ID is removed from the system.",
                "Delete " + role, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        if (pick != 0 && pick != 1) {
            return false;
        }
        removeAccount(id, role, pick == 1);
        return true;
    }

    public static void askDeletion(Component parent, int id, String role){
        DeleteRequests.load();
        if (DeleteRequests.findPending(id, role) != null) {
            JOptionPane.showMessageDialog(parent, "You already sent a request\nPlease wait for the admin to answer it");
            return;
        }
        String problem = checkCanDelete(id, role);
        if (problem != null) {
            JOptionPane.showMessageDialog(parent, problem);
            return;
        }
        JTextArea reason = new JTextArea();
        reason.setLineWrap(true);
        reason.setWrapStyleWord(true);
        JScrollPane reasonScroll = new JScrollPane(reason);
        reasonScroll.setPreferredSize(new java.awt.Dimension(300, 80));
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Why do you want to delete your account?"));
        panel.add(reasonScroll);

        int choice = JOptionPane.showConfirmDialog(parent, panel, "Request Account Deletion",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }
        DeleteRequests.add(id, role, reason.getText());
        JOptionPane.showMessageDialog(parent, "Request sent!\nThe admin will review it.\nYou can still use your account until it is approved.");
    }
}