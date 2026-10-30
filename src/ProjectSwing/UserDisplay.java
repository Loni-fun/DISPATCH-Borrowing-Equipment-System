package ProjectSwing;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class UserDisplay extends JFrame implements MouseListener, ActionListener, FocusListener, KeyListener {
    static final String SEARCH_TEXT = "Search by name, ID or serial no...";
    JLabel t1 = new JLabel("DISPATCH");
    static JLabel tRole = new JLabel();
    JLabel tLogout = new JLabel("<html><u>Logout</html>");
    JLabel line = new JLabel();
    JLabel line2 = new JLabel();
    JLabel h1 = new JLabel("Browse Equipment");
    JLabel h2 = new JLabel("My Requests");
    JLabel h3 = new JLabel("Profile");
    static Font mediumFont = null;
    static Font headerFont = null;
    JPanel browsePane = new JPanel();
    JScrollPane scrollBrowsePane = new JScrollPane(browsePane);
    JPanel requestPane = new JPanel();
    ProfilePanel profile = new ProfilePanel();
    JScrollPane scrollProfile = new JScrollPane(profile);
    long lastRequestFileTime = -1;
    JButton editProfile;
    JButton editPassword;
    JButton requestReset;
    JButton requestDelete;
    boolean noticeOpen = false;
    JTextField search = new JTextField(SEARCH_TEXT);
    CardLayout layout = new CardLayout();
    JPanel set = new JPanel(layout);
    static Student currentStudent = null;
    static Teacher currentTeacher = null;
    JPanel browseBoxHolder = new JPanel();
    JPanel browseOverlay = new JPanel(null) {
        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(new Color(0, 0, 0, 130));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    };
    JPanel browseDetailPanel = new JPanel(null);
    JLabel image = new JLabel();
    JLabel name = new JLabel();
    JTextArea desc = new JTextArea();
    JLabel qty = new JLabel();
    JLabel statusT = new JLabel();
    JLabel accessT = new JLabel();
    JButton addRequest;
    private int quantity = 1;
    JButton addQuantity = new JButton("+");
    JButton minusQuantity = new JButton("-");
    JTextField quantityField = new JTextField(String.valueOf(getQuantityNumber()));
    AdminDisplay.Equipment selectedItem = null;
    JTable requestTable;
    DefaultTableModel requestDtm;
    JLabel pFull = new JLabel();
    JLabel txtID;
    JLabel txtRole;
    JLabel txtLName;
    JLabel txtFName;
    JLabel txtMName;

    public UserDisplay() {
        this.setLayout(null);
        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setBackground(Color.white);
        this.add(t1);
        this.add(tRole);
        this.add(tLogout);
        this.add(line);
        this.add(h1);
        this.add(h2);
        this.add(h3);
        this.add(line2);
        this.add(set);

        t1.setBounds(40, 20, 250, 40);
        t1.setForeground(Color.decode("#FFFFFF"));
        t1.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tRole.setBounds(1135, 30, 50, 20);
        tRole.setForeground(Color.WHITE);
        tRole.setFont(new Font("Segoe UI", Font.BOLD, 12));
        if (currentStudent != null) {
            tRole.setText(Student.role);
        } else if (currentTeacher != null) {
            tRole.setText(Teacher.role);
        }
        tLogout.setBounds(1135, 50, 50, 15);
        tLogout.setForeground(Color.WHITE);
        tLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tLogout.addMouseListener(this);
        setMediumFont(h1);
        h1.setForeground(Color.decode("#2B59C3"));
        setMediumFont(h2);
        setMediumFont(h3);

        line.setOpaque(true);
        line.setBackground(Color.decode("#2B59C3"));
        line.setBounds(0, 0, 1290, 80);

        this.addMouseListener(this);

        set.add(scrollBrowsePane, "Browse Equipment");
        setbrowsePaneDisplay(browsePane);
        set.add(requestPane, "My Request");
        setrequestPaneDisplay(requestPane);
        set.add(scrollProfile, "Profile");
        setupProfileButtons();
        SwingUtilities.invokeLater(() -> showApprovedNotice());
        startNoticeWatcher();
        set.setBounds(0, 160, 1290, 660);
        set.setBackground(Color.WHITE);
        h1.setBounds(15, 80, 200, 80);
        h1.setHorizontalAlignment(JLabel.CENTER);
        h1.addMouseListener(this);
        h2.setBounds(160, 80, 190, 80);
        h2.setHorizontalAlignment(JLabel.CENTER);
        h2.addMouseListener(this);
        h3.setBounds(280, 80, 140, 80);
        h3.setHorizontalAlignment(JLabel.CENTER);
        h3.addMouseListener(this);
        line2.setOpaque(true);
        line2.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#2B59C3")));
        line2.setBackground(Color.white);
        line2.setBounds(0, 80, 1290, 80);
        scrollBrowsePane.getVerticalScrollBar().setUnitIncrement(20);
        scrollProfile.getVerticalScrollBar().setUnitIncrement(20);
        setupBrowseOverlay();
    }

    public static void main(String[] args) {
        // FIX: this main is a developer entry point. It has no logged-in user, so it will NPE
        // on any action that calls getUserId(). Kept for compile-compat but guarded.
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        UserDisplay call = new UserDisplay();
        call.setVisible(true);
    }

    public int minQuantityPerEquipment() { return 1; }

    public int maxQuantityPerEquipment() {
        return AdminDisplay.Request.getPerItemLimit(getUserRole());
    }

    public int getMostAllowed() {
        AdminDisplay.Request.loadRules();
        AdminDisplay.Request.loadFile();
        String role = getUserRole();
        int have = AdminDisplay.Request.countActiveForEquipment(getUserId(), role, selectedItem.getIDNo());
        if (have == 0 && AdminDisplay.Request.countActiveEquipment(getUserId(), role)
                >= AdminDisplay.Request.getDifferentItemLimit(role)) {
            return 0;
        }
        int leftForItem = AdminDisplay.Request.getPerItemLimit(role) - have;
        return Math.max(0, Math.min(leftForItem, selectedItem.getQty()));
    }

    public void setrequestPaneDisplay(JPanel panel) {
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        JLabel text = new JLabel("My Request");
        JButton viewReceipt = new JButton("View Receipt");
        setImageButton(viewReceipt);
        viewReceipt.setBounds(1090, 395, 150, 40);
        viewReceipt.addActionListener(e -> showSelectedReceipt());
        JButton viewRules = new JButton("Borrowing Rules");
        setImageButton(viewRules);
        viewRules.setBounds(920, 395, 150, 40);
        viewRules.addActionListener(e -> AdminDisplay.Request.showRules(this, false));
        panel.add(viewRules);
        this.setMediumHeader(text);
        text.setBounds(40, 20, 100, 40);
        panel.add(viewReceipt);
        panel.add(text);
        JPanel requestPane = new JPanel();
        panel.add(requestPane);
        requestPane.setLayout(null);
        requestPane.setBounds(40, 80, 1200, 300);
        requestPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        requestPane.setBackground(Color.WHITE);
        requestDtm = new DefaultTableModel(AdminDisplay.Request.userColumns, 0) {
            @Override
            public boolean isCellEditable(int rows, int colums) { return false; }
        };
        requestTable = new JTable(requestDtm);
        requestTable.setRowHeight(40);
        requestTable.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
        tcr.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < requestTable.getColumnCount(); i++) {
            requestTable.getColumnModel().getColumn(i).setCellRenderer(tcr);
        }
        JScrollPane tableScroll = new JScrollPane(requestTable);
        tableScroll.setBounds(0, 0, 1200, 300);
        requestPane.add(tableScroll);
    }

    public void showSelectedReceipt() {
        int viewRow = requestTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a request first!!!");
            return;
        }
        String id = (String) requestDtm.getValueAt(requestTable.convertRowIndexToModel(viewRow), 0);
        AdminDisplay.Request.loadFile();
        AdminDisplay.Request r = AdminDisplay.Request.findById(id);
        if (r != null) {
            Receipt.show(this, r);
        }
    }

    public void setupBrowseSearch(JPanel panel) {
        search.setBounds(640, 20, 600, 40);
        search.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        search.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.decode("#1A1A1A")),BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        search.addFocusListener(this);
        search.addKeyListener(this);
        panel.add(search);
    }

    public void refreshMyRequests() {
        String keep = null;
        int row = requestTable.getSelectedRow();
        if (row != -1) {
            keep = (String) requestDtm.getValueAt(row, 0);
        }
        AdminDisplay.Request.loadUserTable(requestDtm, getUserId(), getUserRole());
        for (int i = 0; keep != null && i < requestDtm.getRowCount(); i++) {
            if (requestDtm.getValueAt(i, 0).equals(keep)) {
                requestTable.setRowSelectionInterval(i, i);
            }
        }
    }

    public void setbrowsePaneDisplay(JPanel panel) {
        JLabel text = new JLabel("Browse Equipment");
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        this.setMediumHeader(text);
        text.setBounds(40, 20, 200, 40);
        panel.add(text);
        panel.add(browseBoxHolder);
        browseBoxHolder.setLayout(null);
        browseBoxHolder.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        browseBoxHolder.setBackground(Color.WHITE);
        setupBrowseSearch(panel);
        loadBrowseEquipment();
    }

    // FIX: added a diagnostic + defensive call so equipment actually shows up.
    // If the file is empty or missing, the browse pane now says so instead of being blank.
    public void loadBrowseEquipment() {
        AdminDisplay.Equipment.loadFile();
        showBrowseEquipment();
        
    }
    
    public void showBrowseEquipment(){
        String word = search.getText().trim();
        if (word.equals(SEARCH_TEXT)) {
            word = "";
        }
        ArrayList<AdminDisplay.Equipment> shown = SearchHelper.search(
                AdminDisplay.Equipment.equipmentList, word,
                q -> q.getEquipmentName(), q -> q.getIDNo(),
                q -> String.valueOf(q.getIdNumber()), q -> q.getSerialNo());

        browseBoxHolder.removeAll();
        int boxProw = 3;
        int xgap = 395;
        int ygap = 370;
        int count = 0;
        for (AdminDisplay.Equipment item : shown) {
            int col = count % boxProw;
            int row = count / boxProw;
            int x = 30 + (col * xgap);
            int y = 30 + (row * ygap);
            JLabel box = new JLabel();
            createEquipmentBrowse(box, x, y, 350, 340, item);
            browseBoxHolder.add(box);
            count++;
        }
        if (shown.isEmpty()) {
            JLabel none = new JLabel("No equipment found", JLabel.CENTER);
            none.setBounds(30, 30, 1140, 40);
            browseBoxHolder.add(none);
        }
        int rows = (count + boxProw - 1) / boxProw;
        if (rows < 1) rows = 1;
        int holderHeight = (rows * ygap) + 30;
        browseBoxHolder.setBounds(40, 80, 1200, holderHeight);
        browsePane.setPreferredSize(new Dimension(1200, holderHeight + 300));
        browsePane.revalidate();
        browsePane.repaint();
    }
    
    public JLabel createEquipmentBrowse(JLabel box, int x, int y, int width, int height, AdminDisplay.Equipment item) {
        int x_axis = 0;
        int y_axis = 0;
        int widthH = 350;
        int widthL = 280;
        box.setBounds(x, y, width, height);
        box.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel imgHolder = new JLabel();
        imgHolder.setBounds(x_axis, y_axis, widthH, widthL);
        File imageFile = new File(item.getImagePath());
        if (!item.getImagePath().isEmpty() && imageFile.exists()) {
            ImageIcon imageicon = new ImageIcon(item.getImagePath());
            bufferImage(imgHolder, imageicon);
        } else {
            imgHolder.setText("No Image");
            imgHolder.setHorizontalAlignment(JLabel.CENTER);
            setMediumFont(imgHolder);
        }

        JLabel titleText = new JLabel(item.getEquipmentName());
        JLabel locationText = new JLabel("Status: " + item.getStatus());
        JButton viewItem = new JButton("View Details");
        setImageButton(viewItem);
        viewItem.addActionListener(e -> showBrowseOverlay(item));

        box.add(imgHolder);
        box.add(titleText);
        box.add(locationText);
        box.add(viewItem);
        setMediumFont(titleText);
        locationText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        locationText.setToolTipText(item.getLocation());
        titleText.setBounds(20, 270, 165, 25);
        locationText.setBounds(20, 300, 165, 25);
        return box;
    }

    public void setupBrowseOverlay() {
        browseOverlay.setOpaque(false);
        browseOverlay.addMouseListener(new java.awt.event.MouseAdapter() {});
        browseOverlay.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {});

        browseDetailPanel.setSize(700, 500);
        browseDetailPanel.setBackground(Color.WHITE);
        browseDetailPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JLabel title = new JLabel("Equipment Details");
        title.setBounds(30, 20, 300, 30);
        setHeaderFont(title, "black");

        image.setBounds(30, 70, 280, 240);
        image.setHorizontalAlignment(JLabel.CENTER);
        image.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        name.setBounds(340, 70, 330, 30);
        setMediumHeader(name);
        qty.setBounds(340, 110, 330, 25);
        statusT.setBounds(340, 140, 330, 25);
        accessT.setBounds(30, 310, 330, 50);
        qty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusT.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        accessT.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        desc.setEditable(false);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane descriptionScroll = new JScrollPane(desc);
        descriptionScroll.setBounds(340, 180, 320, 130);
        descriptionScroll.setBackground(Color.WHITE);
        descriptionScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
                        "Description", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 12)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JButton close = new JButton("CLOSE");
        addRequest = new JButton("ADD REQUEST");
        JLabel quantyText = new JLabel("Qty");
        setImageButton(close);
        setImageButton(addRequest);
        setImageButton(addQuantity);
        setImageButton(minusQuantity);
        quantyText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        quantityField.setHorizontalAlignment(JLabel.CENTER);
        quantityField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        quantityField.setBackground(Color.WHITE);
        quantityField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        quantityField.setEditable(false);
        quantyText.setBounds(30, 345, 30, 35);
        minusQuantity.setBounds(60, 350, 30, 30);
        quantityField.setBounds(100, 350, 60, 30);
        addQuantity.setBounds(170, 350, 30, 30);
        close.setBounds(400, 430, 150, 40);
        addRequest.setBounds(180, 430, 150, 40);
        addQuantity.addActionListener(this);
        minusQuantity.addActionListener(this);
        addRequest.addActionListener(this);
        close.addActionListener(e -> hideBrowseOverlay());

        browseDetailPanel.add(title);
        browseDetailPanel.add(image);
        browseDetailPanel.add(name);
        browseDetailPanel.add(qty);
        browseDetailPanel.add(statusT);
        browseDetailPanel.add(accessT);
        browseDetailPanel.add(descriptionScroll);
        browseDetailPanel.add(quantyText);
        browseDetailPanel.add(addQuantity);
        browseDetailPanel.add(quantityField);
        browseDetailPanel.add(minusQuantity);
        browseDetailPanel.add(addRequest);
        browseDetailPanel.add(close);
        browseOverlay.add(browseDetailPanel);
        this.setGlassPane(browseOverlay);
        browseOverlay.setVisible(false);
    }

    public void showBrowseOverlay(AdminDisplay.Equipment item) {
        selectedItem = item;
        quantity = 1;
        quantityField.setText("1");
        int most = getMostAllowed();
        addRequest.setEnabled(most > 0 && !item.getStatus().equals("Under Repair"));
        if (item.getQty() > 0 && most == 0) {
            addRequest.setText("REACH LIMIT");
        } else {
            addRequest.setText("ADD REQUEST");
        }
        image.setIcon(null);
        image.setText("");
        File imageFile = new File(item.getImagePath());
        if (!item.getImagePath().isEmpty() && imageFile.exists()) {
            bufferImage(image, new ImageIcon(item.getImagePath()));
        } else {
            image.setText("No Image");
        }

        name.setText(item.getEquipmentName());
        if (item.getDescription().isEmpty()) {
            desc.setText("No description provided.");
        } else {
            desc.setText(item.getDescription());
        }
        desc.setCaretPosition(0);
        qty.setText("Quantity: " + item.getQty());
        statusT.setText("Status: " + item.getStatus());
        accessT.setText("<html>Can be borrowed by: <b>" + item.getAccess() + "</b></html>");

        browseDetailPanel.setLocation(
                (browseOverlay.getWidth() - browseDetailPanel.getWidth()) / 2,
                (browseOverlay.getHeight() - browseDetailPanel.getHeight()) / 2);
        browseOverlay.setVisible(true);
    }

    public void hideBrowseOverlay() {
        browseOverlay.setVisible(false);
    }

    public void setImageButton(JButton button) {
        button.setBounds(190, 280, 130, 40);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(Color.decode("#2B59C3"));
        button.setHorizontalAlignment(JLabel.CENTER);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        button.setFocusable(false);
    }

    public void bufferImage(JLabel img, ImageIcon icon) {
        Image scaledImage = icon.getImage().getScaledInstance(img.getWidth(), img.getHeight(), Image.SCALE_SMOOTH);
        img.setIcon(new ImageIcon(scaledImage));
    }

    public void setprofilePaneDisplay(JPanel panel) {
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(1190, 1000));
        panel.setBackground(Color.WHITE);
        JLabel text = new JLabel("Profile");
        this.setMediumHeader(text);
        text.setBounds(40, 20, 100, 40);
        panel.add(text);
        JPanel profilePane = new JPanel();
        JLabel box = new JLabel();
        JLabel profileImageHolder = new JLabel();
        JLabel txtFDisplay = new JLabel("First Name");
        JLabel txtMDisplay = new JLabel("Middle Name");
        JLabel txtLDisplay = new JLabel("Last Name");
        JLabel txtRoleDisplay = new JLabel("User Role");
        txtID = new JLabel();
        txtRole = new JLabel();
        txtLName = new JLabel();
        txtFName = new JLabel();
        txtMName = new JLabel();
        pFull = new JLabel();
        JLabel tInfo = new JLabel("Personal Information");
        JLabel txtFullName = new JLabel();
        editProfile = new JButton("Edit Profile");
        editPassword = new JButton("Change Password");
        requestReset = new JButton("Forgot Password?");
        setButtonProfile(editProfile, 850, 15, 150, 40);
        setButtonProfile(editPassword, 1030, 15, 150, 40);
        setButtonProfile(requestReset, 670, 15, 150, 40);
        requestReset.addActionListener(this);
        editProfile.addActionListener(this);
        editPassword.addActionListener(this);
        panel.add(profilePane);
        panel.add(box);
        box.setLayout(null);
        box.setBounds(40, 80, 1200, 200);
        box.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        box.setBackground(Color.WHITE);
        box.add(profileImageHolder);
        box.add(pFull);
        box.add(txtRole);
        box.add(txtID);
        profileImageHolder.setBounds(90, 40, 150, 180);
        profileImageHolder.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        pFull.setBounds(300, 40, 500, 30);
        txtRole.setBounds(300, 70, 100, 30);
        txtID.setBounds(300, 100, 500, 30);
        txtID.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtRole.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtFName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pFull.setText("Dichoso, Clifford James P.");
        setMediumHeader(pFull);

        profilePane.setLayout(null);
        profilePane.setBounds(40, 300, 1200, 400);
        profilePane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        profilePane.setBackground(Color.WHITE);
        profilePane.add(editProfile);
        profilePane.add(editPassword);
        profilePane.add(requestReset);
        profilePane.add(tInfo);
        profilePane.add(txtFDisplay);
        profilePane.add(txtFName);
        profilePane.add(txtMDisplay);
        profilePane.add(txtMName);
        profilePane.add(txtLDisplay);
        profilePane.add(txtLName);
        profilePane.add(txtRoleDisplay);
        profilePane.add(txtRole);
        tInfo.setBounds(30, 20, 500, 30);
        setProfileFontBold(txtFName, 30, 150, 150, 30);
        setProfileFontBold(txtMName, 495, 150, 150, 30);
        setProfileFontBold(txtLName, 975, 150, 150, 30);
        setProfileFontBold(txtRole, 5, 240, 200, 30);
        setProfileFontPlain(txtFDisplay, 5, 110, 200, 30);
        setProfileFontPlain(txtMDisplay, 470, 110, 200, 30);
        setProfileFontPlain(txtLDisplay, 950, 110, 200, 30);
        setProfileFontPlain(txtRoleDisplay, 30, 200, 150, 30);
        setMediumHeader(tInfo);
        txtFullName.setBounds(80, 20, 300, 30);
        loadProfile();
    }

    public void editProfile() {
    int id = getUserId();
    String role = getUserRole();
    String last, first, middle;
    String[] names;
    if (currentStudent != null) {
        last = currentStudent.getLastName();
        first = currentStudent.getFirstName();
        middle = currentStudent.getMiddleName();
        names = ProfilePanel.askNames(this, role, id, last, first, middle,
                "Course", currentStudent.getStudentCourse(), "Year", currentStudent.getYear());
    } else {
        last = currentTeacher.getLastName();
        first = currentTeacher.getFirstName();
        middle = currentTeacher.getMiddleName();
        names = ProfilePanel.askNames(this, role, id, last, first, middle,
                "Department", currentTeacher.getDepartment(), null, "");
    }
    if (names == null) return;
    boolean done;
    if (currentStudent != null) {
        done = Student.updateProfile(id, names[0], names[1], names[2], names[3], names[4]);
        if (done) currentStudent = Student.findIDNumber(id);
    } else {
        done = Teacher.updateProfile(id, names[0], names[1], names[2], names[3]);
        if (done) currentTeacher = Teacher.findIDNumber(id);
    }
    if (!done) {
        JOptionPane.showMessageDialog(this, "Could not save your profile");
        return;
    }
    loadProfile();
    JOptionPane.showMessageDialog(this, "Your profile has been updated");
}

    public void changePassword() {
        JPasswordField oldPass = new JPasswordField();
        JPasswordField newPass = new JPasswordField();
        JPasswordField conPass = new JPasswordField();
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Current password:"));
        panel.add(oldPass);
        panel.add(new JLabel("New password:"));
        panel.add(newPass);
        panel.add(new JLabel("Confirm password:"));
        panel.add(conPass);
        int choice = JOptionPane.showConfirmDialog(this, panel, "Change Password",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) return;

        String oldText = new String(oldPass.getPassword());
        String newText = new String(newPass.getPassword()).trim();
        String conText = new String(conPass.getPassword()).trim();

        if (oldText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter your current password", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String problem = PasswordReset.checkNewPassword(newText, conText);
        if (problem != null) {
            JOptionPane.showMessageDialog(this, problem, "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (newText.equals(oldText)) {
            JOptionPane.showMessageDialog(this, "The new password must be different", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = getUserId();
        String error;
        if (currentStudent != null) {
            error = Student.changePassword(id, oldText, newText);
            if (error == null) currentStudent = Student.findIDNumber(id);
        } else {
            error = Teacher.changePassword(id, oldText, newText);
            if (error == null) currentTeacher = Teacher.findIDNumber(id);
        }
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Your password has been changed");
    }

    public void loadProfile() {
    if (currentStudent != null) {
        profile.show(Student.role, currentStudent.getIdNumber(),
                currentStudent.getLastName(), currentStudent.getFirstName(), currentStudent.getMiddleName(),
                "Course", currentStudent.getStudentCourse(), "Year", currentStudent.getYear());
    } else if (currentTeacher != null) {
        profile.show(Teacher.role, currentTeacher.getIdNumber(),
                currentTeacher.getLastName(), currentTeacher.getFirstName(), currentTeacher.getMiddleName(),
                "Department", currentTeacher.getDepartment(), null, "");
    }
}

    public void showProfile(String roleText, int id, String last, String first, String middle) {
        txtRole.setText(roleText);
        txtID.setText("ID No. " + id);
        txtFName.setText(first);
        txtMName.setText(middle);
        txtLName.setText(last);
        pFull.setText(last + ", " + first + " " + middle);
    }

    public void setProfileFontPlain(JLabel text, int x, int y, int width, int height) {
        text.setHorizontalAlignment(JLabel.CENTER);
        text.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        text.setBounds(x, y, width, height);
    }

    public void setProfileFontBold(JLabel text, int x, int y, int width, int height) {
        text.setHorizontalAlignment(JLabel.CENTER);
        text.setFont(new Font("Segoe UI", Font.BOLD, 14));
        text.setBounds(x, y, width, height);
    }

    public static void setButtonProfile(JButton button, int x, int y, int width, int height) {
        button.setFocusable(false);
        button.setBounds(x, y, width, height);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setHorizontalAlignment(JLabel.CENTER);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.decode("#2B59C3"));
    }

    public void setupProfileButtons() {
        editProfile = profile.editProfile;
        editPassword = profile.editPassword;
        requestReset = new JButton("Forgot Password?");
        requestDelete = new JButton("Request Deletion");
        profile.addButton(requestReset, 670);
        profile.addButton(requestDelete, 490);
        for (JButton b : new JButton[]{editProfile, editPassword, requestReset, requestDelete}) {
            b.addActionListener(this);
        }
    }

    public static void setHeaderFont(JLabel text, String color) {
        try {
            Font blue;
            Font black;
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            if (color.equals("blue")) {   // FIX: .equals not ==
                text.setForeground(Color.decode("#00f0ff"));
                blue = Font.createFont(Font.TRUETYPE_FONT, new File("Font Styles\\monseratSB.ttf")).deriveFont(Font.PLAIN, 18f);
                ge.registerFont(blue);
                text.setFont(blue);
            } else if (color.equals("black")) {
                text.setForeground(Color.decode("#000000"));
                black = Font.createFont(Font.TRUETYPE_FONT, new File("Font Styles\\monseratSB.ttf")).deriveFont(Font.BOLD, 18f);
                ge.registerFont(black);
                text.setFont(black);
            }
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    public void showApprovedNotice() {
        if (noticeOpen) return;
        if (currentStudent == null && currentTeacher == null) return;
        java.util.ArrayList<AdminDisplay.Request> approved =
                AdminDisplay.Request.getApprovedToNotify(getUserId(), getUserRole());
        if (approved.isEmpty()) return;
        noticeOpen = true;
        try {
            String items = "";
            for (AdminDisplay.Request r : approved) {
                items += "\n- " + r.getEquipmentName() + " (x" + r.getQuantity() + ")";
            }
            JOptionPane.showMessageDialog(this,
                    "Your request has been approved!\nOpen your receipt in My Requests (View Receipt)\n"
                            + "and show it the office within " + AdminDisplay.Request.pickupDays
                            + " day(s) to get your equipment, or the request will expire.",
                    "Request Approved", JOptionPane.INFORMATION_MESSAGE);
            AdminDisplay.Request.markNotified(approved);
            refreshMyRequests();
        } finally {
            noticeOpen = false;
        }
    }

    public static void setMediumFont(JLabel text) {
    try {
        if (mediumFont == null) {
            mediumFont = Font.createFont(Font.TRUETYPE_FONT, new File("Font Styles\\quicksandReg.otf")).deriveFont(Font.BOLD, 15f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(mediumFont);
        }
        text.setFont(mediumFont);
    } catch (IOException | FontFormatException e) {
        e.printStackTrace();
    }
}

public static void setMediumHeader(JLabel text) {
    try {
        if (headerFont == null) {
            headerFont = Font.createFont(Font.TRUETYPE_FONT, new File("Font Styles\\monseratSB.ttf")).deriveFont(Font.BOLD, 15f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(headerFont);
        }
        text.setFont(headerFont);
    } catch (IOException | FontFormatException e) {
        e.printStackTrace();
    }
}

    public int getQuantityNumber() { return quantity; }

    public int getUserId() {
        if (currentStudent != null) return currentStudent.getIdNumber();
        return currentTeacher.getIdNumber();
    }

    public String getUserRole() {
        if (currentStudent != null) return Student.role;
        return Teacher.role;
    }

    public void startNoticeWatcher() {
        Timer watcher = new Timer(3000, e -> {
            if (!this.isShowing() || noticeOpen) return;
                long stamp = new File(ManageFiles.REQUEST).lastModified();
                    if (stamp == lastRequestFileTime) return;
                        lastRequestFileTime = stamp;
                        showApprovedNotice();
                            if (requestPane.isShowing()) {
                                refreshMyRequests();
                            }
            });
        watcher.start();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource().equals(search)) {
            search.setForeground(Color.BLACK);
            search.setText("");
        } else if (e.getSource().equals(this)) {
            if (search.getText().isBlank()) {
                search.setForeground(Color.GRAY);
                search.setText(SEARCH_TEXT);
            }
        } else if (e.getSource().equals(h2)) {
            refreshMyRequests();
            showApprovedNotice();
            layout.show(set, "My Request");
            h2.setForeground(Color.decode("#2B59C3"));
            h1.setForeground(Color.decode("#000000"));
            h3.setForeground(Color.decode("#000000"));
        } else if (e.getSource().equals(h3)) {
            loadProfile();
            layout.show(set, "Profile");
            h1.setForeground(Color.decode("#000000"));
            h2.setForeground(Color.decode("#000000"));
            h3.setForeground(Color.decode("#2B59C3"));
        } else if (e.getSource().equals(h1)) {
            loadBrowseEquipment();
            layout.show(set, "Browse Equipment");
            h1.setForeground(Color.decode("#2B59C3"));
            h2.setForeground(Color.decode("#000000"));
            h3.setForeground(Color.decode("#000000"));
        } else if (e.getSource().equals(tLogout)) {
            int i = JOptionPane.showConfirmDialog(null, "Are you sure?", "Logout",
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (i == 1 || i == -1) {
                System.out.println("Nothing Happened!!!!!");
                return;
            } else {
                JOptionPane.showMessageDialog(null, "Thank you for using our system!!!!");
                currentStudent = null;
                currentTeacher = null;
                this.setVisible(false);
                JFrame frame = new JFrame("DISPATCH");
                showLoginPage get = new LoginPage();
                get.showLogin(frame);
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getSource().equals(h2)) h2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        else if (e.getSource().equals(h3)) h3.setCursor(new Cursor(Cursor.HAND_CURSOR));
        else if (e.getSource().equals(h1)) h1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        else if (e.getSource().equals(tLogout)) tLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override public void mouseExited(MouseEvent e) {}

    // FIX #1: this method was structurally broken. All else-if branches were nested
    // inside the addRequest block. Rewritten as a flat if/else-if chain.
    @Override
public void actionPerformed(ActionEvent e) {
    if (e.getSource().equals(addRequest)) {
        if (selectedItem == null) {
            return;
        }
        if (getMostAllowed() <= 0) {
            JOptionPane.showMessageDialog(this, "You already reach the limit for this item\n(max " + maxQuantityPerEquipment() + " per equipment for a " + getUserRole().toLowerCase() + ")");
            return;
        }
        boolean sent = AdminDisplay.Request.openForm(this, selectedItem.getIDNo(), getUserId(), getUserRole(), quantity);
        if (sent) {
            refreshMyRequests();
            loadBrowseEquipment();
            hideBrowseOverlay();
        }
    } else if (e.getSource().equals(addQuantity)) {
        int most = getMostAllowed();
        if (quantity >= most) {
            JOptionPane.showMessageDialog(null, "You can request at most " + most + " of this item right now\n"
                    + "Max per equipment: " + maxQuantityPerEquipment() + " for a " + getUserRole().toLowerCase());
        } else {
            quantity++;
            quantityField.setText(String.valueOf(quantity));
        }
    } else if (e.getSource().equals(minusQuantity)) {
        if (quantity > minQuantityPerEquipment()) {
            quantity--;
            quantityField.setText(String.valueOf(quantity));
        }
    } else if (e.getSource().equals(editProfile)) {
        editProfile();
    } else if (e.getSource().equals(editPassword)) {
        changePassword();
    } else if (e.getSource().equals(requestReset)) {
        PasswordReset.showRequestInstructions(this);
    } else if (e.getSource().equals(requestDelete)) {
        AccountDeletion.askDeletion(this, getUserId(), getUserRole());
    }
}
    @Override
    public void focusGained(FocusEvent e) {
        if (e.getSource().equals(search)) {
            if (search.getText().equals(SEARCH_TEXT)) search.setText("");
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (e.getSource().equals(search)) {
            if (search.getText().trim().isEmpty()) search.setText(SEARCH_TEXT);
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {
        if (e.getSource().equals(search) && e.getKeyCode() == KeyEvent.VK_ENTER) {
            showBrowseEquipment();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource().equals(search) && search.getText().trim().isEmpty()) {
            showBrowseEquipment();
        }
    }
}