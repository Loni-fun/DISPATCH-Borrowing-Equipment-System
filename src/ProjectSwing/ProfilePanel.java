package ProjectSwing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
 // creates the profile panel for all the users
public class ProfilePanel extends JPanel {
    // GENDER: saved in its own small file, one line per user: Role|ID|Gender
    static final String GENDER_FILE = ManageFiles.BASE + "Admin\\Registered Users\\Gender.txt";
    static final String GENDER_NONE = "Not set";

    JLabel pFull = new JLabel(); //fullName
    JLabel txtID =  new JLabel(); // id
    JLabel txtRole = new JLabel();
    JLabel txtFName = new JLabel();
    JLabel txtMName = new JLabel();
    JLabel txtLName = new JLabel(); // last name
    JLabel txtGender = new JLabel();   // GENDER
    JLabel titleA = new JLabel();   // "Course" for students, "Department" for teachers
    JLabel valueA = new JLabel();
    JLabel titleB = new JLabel();   // "Year" for students, unused for teachers
    JLabel valueB = new JLabel();
    JLabel picture = new JLabel("No Photo", JLabel.CENTER);
    JButton editProfile = new JButton("Edit Profile");
    JButton editPassword = new JButton("Change Password");
    JPanel infoPane = new JPanel(null);

    public ProfilePanel(){
        this.setLayout(null);
        this.setPreferredSize(new Dimension(1190, 1000));
        this.setBackground(Color.WHITE);

        JLabel text = new JLabel("Profile");
        UserDisplay.setMediumHeader(text);
        text.setBounds(40, 20, 100, 40);

        // top box: picture, full name and ID
        JLabel topBox = new JLabel();
        topBox.setLayout(null);
        topBox.setBounds(40, 80, 1200, 200);
        topBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        picture.setBounds(30, 20, 130, 150);
        picture.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        pFull.setBounds(190, 40, 500, 30);
        txtID.setBounds(190, 100, 500, 30);
        txtID.setFont(new Font("Segoe UI", Font.BOLD, 14));
        UserDisplay.setMediumHeader(pFull);
        topBox.add(picture);
        topBox.add(pFull);
        topBox.add(txtID);

        // bottom box: personal information
        infoPane.setBounds(40, 300, 1200, 400);
        infoPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        infoPane.setBackground(Color.WHITE);
        JLabel tInfo = new JLabel("Personal Information");
        UserDisplay.setMediumHeader(tInfo);
        tInfo.setBounds(30, 20, 500, 30);
        infoPane.add(tInfo);
        addField("First Name", txtFName, 105, 110);
        addField("Middle Name", txtMName, 570, 110);
        addField("Last Name", txtLName, 1050, 110);
        addField("User Role", txtRole, 105, 200);
        addField(titleA, valueA, 570, 200);          
        addField(titleB, valueB, 1050, 200);
        addField("Gender", txtGender, 105, 290);   // GENDER: third row, always visible
        showExtras(null, "", null, "");
        addButton(editProfile, 850);
        addButton(editPassword, 1030);

        this.add(text);
        this.add(topBox);
        this.add(infoPane);
    }

    private void addField(String title, JLabel value, int centerX, int y){
    addField(new JLabel(title), value, centerX, y);
    }
    
    private void addField(JLabel label, JLabel value, int centerX, int y){
    label.setHorizontalAlignment(JLabel.CENTER);
    label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    label.setBounds(centerX - 100, y, 200, 30);
    value.setHorizontalAlignment(JLabel.CENTER);
    value.setFont(new Font("Segoe UI", Font.BOLD, 14));
    value.setBounds(centerX - 100, y + 40, 200, 30);
    infoPane.add(label);
    infoPane.add(value);
}

    public void addButton(JButton button, int x){
        UserDisplay.setButtonProfile(button, x, 15, 150, 40);
        infoPane.add(button);
    }

    public void show(String role, int id, String last, String first, String middle){
        txtRole.setText(role);
        txtID.setText("ID No. " + id);
        txtFName.setText(first);
        txtMName.setText(middle);
        txtLName.setText(last);
        txtGender.setText(getGender(role, id));   // GENDER
        pFull.setText(last + ", " + first + " " + middle);
        ImageIcon icon = ProfilePic.load(role, id, 128, 148);
        picture.setIcon(icon);
                           // if null true No photo // else ""
        picture.setText(icon == null ? "No Photo" : "");
    }
    public void show(String role, int id, String last, String first, String middle,
                 String tA, String a, String tB, String b){
    show(role, id, last, first, middle);
    showExtras(tA, a, tB, b);
    }
    public void showExtras(String tA, String a, String tB, String b){
    titleA.setText(tA == null ? "" : tA);
    valueA.setText(a == null || a.isEmpty() ? "-" : a);
    titleA.setVisible(tA != null);
    valueA.setVisible(tA != null);
    titleB.setText(tB == null ? "" : tB);
    valueB.setText(b == null || b.isEmpty() ? "-" : b);
    titleB.setVisible(tB != null);
    valueB.setVisible(tB != null);
    }

    // ---------- GENDER (saved in Gender.txt) ----------

    // the saved gender of this user, or "Not set"
    public static String getGender(String role, int id){
        for (String[] d : ManageFiles.loadFile(GENDER_FILE)) {
            if (d.length == 3 && d[0].equals(role) && d[1].equals(String.valueOf(id))) {
                return d[2];
            }
        }
        return GENDER_NONE;
    }

    // replaces the old value of this user, keeps everyone else's
    public static void setGender(String role, int id, String gender){
        ArrayList<String> lines = otherUsersGender(role, id);
        lines.add(ManageFiles.join(role, id, gender));
        ManageFiles.writeLines(GENDER_FILE, lines);
    }

    // call this when an account is deleted
    public static void deleteGender(String role, int id){
        ManageFiles.writeLines(GENDER_FILE, otherUsersGender(role, id));
    }

    // every line of the file except this user's
    private static ArrayList<String> otherUsersGender(String role, int id){
        ArrayList<String> lines = new ArrayList<>();
        for (String[] d : ManageFiles.loadFile(GENDER_FILE)) {
            if (d.length == 3 && !(d[0].equals(role) && d[1].equals(String.valueOf(id)))) {
                lines.add(ManageFiles.join(d[0], d[1], d[2]));
            }
        }
        return lines;
    }

    // ---------- dialogs ----------

    public static String[] askNames(Component parent, String role, int id, String last, String first, String middle){
    return askNames(parent, role, id, last, first, middle, null, "", null, "");
    }
    
    public static String[] askNames(Component parent, String role, int id, String last, String first, String middle,
                                String titleA, String valueA, String titleB, String valueB){
    JTextField fName = new JTextField(first);
    JTextField mName = new JTextField(middle);
    JTextField lName = new JTextField(last);
    JTextField extraA = new JTextField(valueA == null ? "" : valueA);
    JTextField extraB = new JTextField(valueB == null ? "" : valueB);

    String[] chosen = {""};
    boolean[] remove = {false};
    JLabel preview = new JLabel("No Photo", JLabel.CENTER);
    preview.setPreferredSize(new Dimension(150, 200));
    preview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    ImageIcon current = ProfilePic.load(role, id, 148, 198);
    if (current != null) {
        preview.setText("");
        preview.setIcon(current);
    }
    JButton choose = new JButton("Choose Picture");
    JButton removePic = new JButton("Remove Picture");
    removePic.setEnabled(current != null);
    choose.addActionListener(ev -> {
        String path = ProfilePic.browse(parent);
        if (!path.isEmpty()) {
            chosen[0] = path;
            remove[0] = false;
            preview.setText("");
            preview.setIcon(ProfilePic.fromFile(path, 148, 198));
            removePic.setEnabled(true);
        }
    });
    removePic.addActionListener(ev -> {
        chosen[0] = "";
        remove[0] = true;
        preview.setIcon(null);
        preview.setText("No Photo");
        removePic.setEnabled(false);
    });
    JPanel buttons = new JPanel(new GridLayout(2, 1, 0, 5));
    buttons.add(choose);
    buttons.add(removePic);
    JPanel pictureBox = new JPanel(new BorderLayout(0, 5));
    pictureBox.add(preview, BorderLayout.CENTER);
    pictureBox.add(buttons, BorderLayout.SOUTH);

    // GENDER: two radio buttons in one ButtonGroup, so only one can be selected
    String savedGender = getGender(role, id);
    JRadioButton male = new JRadioButton("Male");
    JRadioButton female = new JRadioButton("Female");
    male.setFocusable(false);
    female.setFocusable(false);
    ButtonGroup genderGroup = new ButtonGroup();
    genderGroup.add(male);
    genderGroup.add(female);
    male.setSelected(savedGender.equals("Male"));
    female.setSelected(savedGender.equals("Female"));
    JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    genderPanel.add(male);
    genderPanel.add(female);

    JPanel fields = new JPanel(new GridLayout(0, 2, 5, 5));
    fields.add(new JLabel("First Name:"));  fields.add(fName);
    fields.add(new JLabel("Middle Name:")); fields.add(mName);
    fields.add(new JLabel("Last Name:"));   fields.add(lName);
    fields.add(new JLabel("Gender:"));      fields.add(genderPanel);   // GENDER
    if (titleA != null) {
        fields.add(new JLabel(titleA + ":")); fields.add(extraA);
    }
    if (titleB != null) {
        fields.add(new JLabel(titleB + ":")); fields.add(extraB);
    }

    JPanel panel = new JPanel(new BorderLayout(15, 0));
    panel.add(pictureBox, BorderLayout.WEST);
    panel.add(fields, BorderLayout.CENTER);

    int choice = JOptionPane.showConfirmDialog(parent, panel, "Edit Profile",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (choice != JOptionPane.OK_OPTION) {
        return null;
    }
    String newFirst = fName.getText().trim();
    String newMiddle = mName.getText().trim();
    String newLast = lName.getText().trim();
    if (newFirst.isEmpty() || newLast.isEmpty()) {
        JOptionPane.showMessageDialog(parent, "First name and last name cannot be empty");
        return null;
    }
    if (!newFirst.matches("[\\p{L} ]+") || !newLast.matches("[\\p{L} ]+") || !newMiddle.matches("[\\p{L} ]*")) {
        JOptionPane.showMessageDialog(parent, "Names can only contain letters and spaces");
        return null;
    }
    if (remove[0]) {
        ProfilePic.delete(role, id);
    } else {
        ProfilePic.save(role, id, chosen[0]);
    }
    // GENDER: saved only after the names passed validation
    if (male.isSelected()) {
        setGender(role, id, "Male");
    } else if (female.isSelected()) {
        setGender(role, id, "Female");
    }
    return new String[]{newLast, newFirst, newMiddle,
            ManageFiles.clean(extraA.getText()), ManageFiles.clean(extraB.getText())};
}

    public static String[] askPasswords(Component parent){
        JPasswordField oldPass = new JPasswordField();
        JPasswordField newPass = new JPasswordField();
        JPasswordField conPass = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Current password:")); panel.add(oldPass);
        panel.add(new JLabel("New password:"));     panel.add(newPass);
        panel.add(new JLabel("Confirm password:")); panel.add(conPass);

        int choice = JOptionPane.showConfirmDialog(parent, panel, "Change Password",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return null;
        }
        String oldText = new String(oldPass.getPassword());
        String newText = new String(newPass.getPassword()).trim();
        String conText = new String(conPass.getPassword()).trim();

        String problem;
        if (oldText.isEmpty()) {
            problem = "Enter your current password";
        } else {
            problem = PasswordReset.checkNewPassword(newText, conText);
        }
        if (problem == null && newText.equals(oldText)) {
            problem = "The new password must be different";
        }
        if (problem != null) {
            JOptionPane.showMessageDialog(parent, problem, "Warning", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return new String[]{oldText, newText};
    }
}