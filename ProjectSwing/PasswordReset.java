package ProjectSwing;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.security.SecureRandom;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class PasswordReset {
    static final String ADMIN_OFFICE = "the Admin Office, Center Building";
    static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";   // no 0/O or 1/I, easy to read out loud
    static final int CODE_LENGTH = 8;
    static final SecureRandom random = new SecureRandom();

    // 8-character code for student/teacher resets
    public static String generateCode(){
        String code = "";
            for (int i = 0; i < CODE_LENGTH; i++) {
                code += CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length()));
            }
        return code;
}
    // the one place that decides if Student or Teacher
    public static Account findAccount(int id, String role){
        if (role.equals(Student.role)) {
            Student.loadStudentInfo();
            return Student.findIDNumber(id);
        }
        Teacher.loadTeacherInfo();
        return Teacher.findIDNumber(id);
    }

    // returns null when the password is fine, otherwise the message to show
    public static String checkNewPassword(String pass, String confirm){
        if (pass.length() < 6) {
            return "Password must be at least 6 characters";
        }
        if (pass.contains("|")) {
            return "Password cannot contain the | symbol";
        }
        if (!pass.equals(confirm)) {
            return "Passwords do not match";
        }
        return null;
    }

    // shown on the login page and the profile page
    public static void showRequestInstructions(Component parent){
        JOptionPane.showMessageDialog(parent,
                "Meet the admin at " + ADMIN_OFFICE + "\n\n"
                + "1. Show your face to the admin\n"
                + "2. Bring your school ID\n\n"
                + "When the admin confirms it is really you, you will be given a code.\n"
                + "Use that code as your password when you log in,\n"
                + "then the system will ask you to set a new password.",
                "Request Password Reset", JOptionPane.INFORMATION_MESSAGE);
    }

    // the admin clicked Reset Password on a selected student or teacher
    public static void adminReset(Component parent, int id, String role){
        Account user = findAccount(id, role);
        if (user == null) {
            JOptionPane.showMessageDialog(parent, role + " ID " + id + " was not found.");
            return;
        }
        if (!user.hasAccount()) {
            JOptionPane.showMessageDialog(parent, "This ID has no account yet\nThe user can register on the Sign Up page");
            return;
        }
        if (user.isBlocked()) {
            JOptionPane.showMessageDialog(parent, "This " + role.toLowerCase() + " is blocked\nUnblock the user first in the Blocked Users tab");
            return;
        }
        JOptionPane.showMessageDialog(parent, "A code is being generated for " + role + " " + id,
                "Reset Password", JOptionPane.INFORMATION_MESSAGE);

        String code = generateCode();
        user.setPassword(code, true);   // true = the user must change it at the next login
        user.save();

        JTextField codeField = new JTextField(code);
        codeField.setEditable(false);
        codeField.setHorizontalAlignment(JTextField.CENTER);
        codeField.setFont(new Font("Segoe UI", Font.BOLD, 28));
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel(role + " " + id + " - give this code to the user in person:"));
        panel.add(codeField);
        panel.add(new JLabel("This code is shown only once. The user logs in with it as the password."));
        JOptionPane.showMessageDialog(parent, panel, "Reset Code", JOptionPane.INFORMATION_MESSAGE);
    }

    // called by the login page when the typed password is a reset code
    // returns true when the user chose a new password, false when they cancelled
    public static boolean forceChange(Component parent, int id, String role){
        JOptionPane.showMessageDialog(parent, "A reset code was detected for your account.\nYou must set a new password to continue.",
                "Reset Code Detected", JOptionPane.INFORMATION_MESSAGE);

        JPasswordField newPass = new JPasswordField();
        JPasswordField conPass = new JPasswordField();
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("New password:"));     panel.add(newPass);
        panel.add(new JLabel("Confirm password:")); panel.add(conPass);

        while (true) {
            int choice = JOptionPane.showConfirmDialog(parent, panel, "Set New Password",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (choice != JOptionPane.OK_OPTION) {
                return false;   // the code stays valid, the user can try again at the next login
            }
            String newText = new String(newPass.getPassword()).trim();
            String conText = new String(conPass.getPassword()).trim();
            String problem = checkNewPassword(newText, conText);
            Account user = findAccount(id, role);
            if (problem == null && user.passwordMatches(newText)) {
                problem = "The new password must be different from the reset code";
            }
            if (problem != null) {
                JOptionPane.showMessageDialog(parent, problem, "Warning", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            user.setPassword(newText, false);   // false = no longer a reset code
            user.save();
            JOptionPane.showMessageDialog(parent, "Your password has been changed");
            return true;
        }
    }
}