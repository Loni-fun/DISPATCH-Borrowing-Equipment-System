package ProjectSwing;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.Dimension;
import java.util.Comparator;
public class AdminDisplay extends JFrame implements MouseListener, ActionListener, FocusListener, KeyListener {
    static private final int adminAccount = 2026001022;   // only account for admin (unchanged)
    static private String password = "asd1234acjn";       // default until the admin changes it, then it is read from the file
    static String adminLast = "Admin";
    static String adminFirst = "System";
    static String adminMiddle = "";
    static final String name = "Admin";
    JLabel user = new JLabel(name);
    JLabel logout = new JLabel("<html><u>Logout</u></html>");
    CardLayout layout = new CardLayout();
    JPanel mainPane = new JPanel(layout);
    JPanel dashboardPane = new JPanel();
    JPanel equipmentPane = new JPanel();
    JPanel requestPane = new JPanel();
    JPanel systemPane = new JPanel();
    ProfilePanel adminProfile = new ProfilePanel();
    JLabel line1 = new JLabel();
    JLabel line2 = new JLabel();
    JTextField search;
    JLabel h1 = new JLabel("DISPATCH");
    JLabel h2= new JLabel("Dashboard");
    JLabel h3= new JLabel("Equipment");
    JLabel h4= new JLabel("Request");
    JLabel h5= new JLabel("System");
    JLabel h6 = new JLabel("Profile");
    long lastRequestFileTime = -1;
    JLabel hStudent;
    JLabel hTeacher;
    JLabel hBlockUsers;
    JPanel pane;
    JPanel userCard;
    CardLayout userPanels;
    JTable equipmentTable;
    JTable studentTable;
    JTable teacherTable;
    JTable blockUserTable;
    JComboBox categories;
    DefaultTableModel dtm ;
    DefaultTableModel studentDtm;
    DefaultTableModel teacherDtm ;
    DefaultTableModel blockedUserDtm;
    JTable recentTable;
    DefaultTableModel recentDtm;
    int lastRequestCount = -1;
    static final int RECENT_LIMIT = 10;
    String selectedCategory;
    String selectedStatus;
    JPanel panelAdd;
    JPanel panelAddT;
    private JPanel proofViewPanel = new JPanel(null);   // the big picture shown on the overlay
    JLabel proofFull = new JLabel("", JLabel.CENTER);
    String currentProofPath = ""; 
    JTextField teacherID;
    JTextField studentID;
    JTextField inputStudentPer;
    JTextField inputTeacherPer;
    JTextField inputPickup;
    JComboBox<String> statusBox;
    JComboBox<String> requestFilter;
    JComboBox<String> accessBox;
    JComboBox<String> categoryBox;
    JComboBox<String> status;
    JComboBox<String> statusBoxT;// Student status box, teacher, status, category
    JTextField seNoField;
    JTextField qtyField;
    JTextField eqNameField;
    JTextField searchStudent;
    JTextField searchTeacher;
    JTextField searchBlocked;
    JTextField locationField;
    JTextArea descriptionArea;
    JTextArea detailArea;
    JTextArea reasonField;
    JLabel imageName;
    String selectedImagePath = "";
    JButton changeImage;
    String editImagePath;
    JLabel box1,box2,box3,box4,box5,proofPicture;
    JTable requestTable;
    DefaultTableModel requestDtm; 
    JTextField inputMaxStudent;
    JTextField inputMaxTeacher;
    JTextField inputMaxDays;
    JTextField inputPenalty;
    JButton resetStudent;
    JButton editStudent;
    JButton editTeacher;
    JButton resetTeacher;
    JButton deleteStudent;
    JButton rbutton;
    JButton deleteTeacher;
    JButton approveDelete;
    JButton rejectDelete;
    JLabel hDelete;
    JTable deleteTable;
    DefaultTableModel deleteDtm;
    int lastQueueSize = -1;
    int lastQueueFront = -1;
    
    
    private JPanel overlay = new JPanel(null) {
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(new Color(0, 0, 0, 130)); // black, ~50% transparent
        g.fillRect(0, 0, getWidth(), getHeight());
    }
};
    private JPanel addEquipmentPanel = new JPanel(null);
    @Override
    public void mouseClicked(MouseEvent e) {
    Object src = e.getSource();
        if (src.equals(h2)) {
            Equipment.loadFile();
            refreshDashboard();
            showTab("Dashboard",h2);
        } else if(src.equals(h3)){
          loadEquipmentTable();
          showTab("Equipment",h3);    
        }else if(src.equals(h4)){
          loadRequestTable();
          showTab("Request",h4);
        }else if(src.equals(h5)){
        refreshUserTables();
        BlockedUsers.loadBlockUsersTable(blockedUserDtm);
        showTab("System",h5);  
        }else if(src.equals(h6)){
        loadAdminProfile();
        showTab("Profile", h6);
        }else if(src.equals(logout)){
        int i = JOptionPane.showConfirmDialog(null, "Are you sure?", "Logout", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (i == 1 || i == -1) { // 1 - no -1 - x button in the top right of the joptionpane
        System.out.println("Nothing Happened!!!!!");
        } else{
        JOptionPane.showMessageDialog(null, "Thank you for using our system!!!!");
        this.setVisible(false);
        JFrame frame = new JFrame("DISPATCH");
        LoginPage get = new LoginPage();
        get.showLogin(frame);
        }
    }else if(src.equals(hStudent)){
        showUserTab("Students", hStudent);
    }else if(src.equals(hTeacher)){
        showUserTab("Teachers", hTeacher);
    }else if(src.equals(hBlockUsers)){
        showUserTab("Blocked Users", hBlockUsers);
    }else if(src.equals(hDelete)){
        DeleteRequests.loadTable(deleteDtm);
        showUserTab("Delete Requests", hDelete);
    }
}
    public void showUserTab(String card, JLabel tab){
     if (card.equals("Students")) {
         new Student().refreshStudentTable(studentDtm);
     } else if (card.equals("Teachers")) {
         new Teacher().refreshTeacherTable(teacherDtm);
     } else if (card.equals("Blocked Users")) {
         BlockedUsers.loadBlockUsersTable(blockedUserDtm);
     }
     userPanels.show(userCard, card);
     highlight(tab, hStudent, hTeacher, hBlockUsers, hDelete);
}
    
    public void showTab(String card, JLabel tab){
    layout.show(mainPane, card);
    highlight(tab, h2, h3, h4, h5, h6);
}
    
    public String getAdminPassword(){
        return password;
    }
    public void highlight(JLabel selected, JLabel... group){
    for (JLabel l : group) {
        if (l == selected) {
            l.setForeground(Color.decode("#2B59C3"));
        }else{
            l.setForeground(Color.decode("#000000"));
        }
    }
}

    @Override
    public void mousePressed(MouseEvent e) {
       // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    ((JComponent) e.getSource()).setCursor(new Cursor(Cursor.HAND_CURSOR));
}

    @Override
    public void mouseExited(MouseEvent e) {
       // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
      @Override
    public void actionPerformed(ActionEvent e) {
        String select = (String) categories.getSelectedItem();
          if (e.getSource() == categories && categories != null) {
              searchEquipment();
          }
          switch(e.getActionCommand()){
              case "+ Add Equipment": this.showAddEquipment();
              break;
              case "Delete": deleteEquipment();
              break;
              case "Edit": editEquipment();
              break;
          }
          if (e.getActionCommand().equals("Add Student ID")) {
              studentID.setText("");
              int choice = JOptionPane.showConfirmDialog(this, panelAdd, "Add Student",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (choice != JOptionPane.OK_OPTION) return;

            String idText = studentID.getText().trim();
            String statusText = (String) statusBox.getSelectedItem();

            Student added = Student.addStudentToFile(idText, statusText);
            if (added == null) return;

            added.refreshStudentTable(studentDtm);
            JOptionPane.showMessageDialog(this, "Student ID: " + idText + " has been added");
         }else if (e.getActionCommand().endsWith("Add Teacher ID")) {
             teacherID.setText("");
              int choice = JOptionPane.showConfirmDialog(this, panelAddT, "Add Teacher",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (choice != JOptionPane.OK_OPTION) return;

            String idText = teacherID.getText().trim();
            String statusText = (String) statusBoxT.getSelectedItem();

            Teacher added = Teacher.addTeacherToFile(idText, statusText);
            if (added == null) return;

            added.refreshTeacherTable(teacherDtm);
            JOptionPane.showMessageDialog(this, "Teacher ID: " + idText + " has been added");
          }else if (e.getSource() == status) {
            selectedStatus = (String) status.getSelectedItem();
        }else if (e.getSource() == categoryBox) {
            selectedCategory = (String) categoryBox.getSelectedItem();
        }else if (e.getSource().equals(editStudent)) {
            editStudentStatus();
        }else if (e.getSource().equals(editTeacher)) {
            editTeacherStatus();
            }else if (e.getActionCommand().equals("Unblock User")) {
            unblockUser();
        }else if(e.getSource().equals(changeImage)){
            String path = BrowseImage();
              if (!path.isEmpty()) {
                  editImagePath = path;
                  changeImage.setText(new File(path).getName());
              }
        }else if(e.getSource().equals(rbutton)){
            returnRequest();
        }else if (e.getSource().equals(resetStudent)) {
            resetUserPassword(studentTable, studentDtm, Student.role);
        }else if (e.getSource().equals(resetTeacher)) {
            resetUserPassword(teacherTable, teacherDtm, Teacher.role);
        }else if (e.getSource().equals(deleteStudent)) {
            deleteUserAccount(studentTable, studentDtm, Student.role);
        }else if (e.getSource().equals(deleteTeacher)) {
            deleteUserAccount(teacherTable, teacherDtm, Teacher.role);
        }else if (e.getSource().equals(approveDelete)) {
            approveDeleteRequest();
        }else if (e.getSource().equals(rejectDelete)) {
            rejectDeleteRequest();
        }
}
  public  AdminDisplay(){
        //main frame

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setBackground(Color.white);
        this.setLayout(null);
        //add components
     
        this.add(user);
        this.add(logout);
        this.add(h1);
        this.add(h2);
        this.add(h3);
        this.add(h4);
        this.add(h5);
        this.add(h6);
        this.add(line1);
        this.add(line2);

        this.add(mainPane);
        user.setBounds(1135, 30, 50, 20);
        user.setForeground(Color.WHITE);
        user.setFont(new Font("Segoe UI",Font.BOLD,12));
        logout.setBounds(1120, 50, 70, 20);
        logout.setHorizontalAlignment(JLabel.CENTER);
        logout.setForeground(Color.WHITE);
        logout.setFont(new Font("Segoe UI",Font.PLAIN,11));
        logout.addMouseListener(this);
        mainPane.setBounds(0, 160, 1290, 660);
        mainPane.add(dashboardPane, "Dashboard");
        setDashboardPane(dashboardPane);
        mainPane.add(equipmentPane, "Equipment");
        setEquipmentPane(equipmentPane);
        loadEquipmentTable();
        mainPane.add(requestPane, "Request");
        setRequestPane(requestPane);
        JScrollPane scroll = new JScrollPane(systemPane);
        setSystemPane(systemPane);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        mainPane.add(scroll, "System");
        JScrollPane profileScroll = new JScrollPane(adminProfile);
        profileScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainPane.add(profileScroll, "Profile");
        setupAdminProfile();
        setlocations(line1, line2, h1, h2, h3, h4, h5,h6);
        UserDisplay.setMediumFont(h2);
        UserDisplay.setMediumFont(h3);
        UserDisplay.setMediumFont(h4);
        UserDisplay.setMediumFont(h5);
        UserDisplay.setMediumFont(h6);
        setupAddEquipmentOverlay();
        startQueueWatcher();
    }
  
  
   public void setlocations(JLabel l1, JLabel l2, JLabel h1, JLabel h2, JLabel h3, JLabel h4, JLabel h5,JLabel h6){
       l1.setBounds(0, 0, 1290, 80);
       l1.setOpaque(true);
       l1.setBackground(Color.decode("#2B59C3"));
       l1.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#2B59C3")));
       l2.setOpaque(true);
       l2.setBackground(Color.WHITE);
       l2.setBounds(0, 80, 1290, 80);
       l2.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#2B59C3")));

       h1.setBounds(40, 20, 250, 40);
       h1.setForeground(Color.decode("#FFFFFF"));
       h1.setFont(new Font("Segoe UI",Font.BOLD,20));
       
       h2.setBounds(40, 80, 100, 80);
       h2.setForeground(Color.decode("#2B59C3"));
       h2.addMouseListener(this);
       h3.setBounds(160, 80, 100, 80);
       h3.addMouseListener(this);
       h4.setBounds(280, 80, 100, 80);
       h4.addMouseListener(this);
       h5.setBounds(380, 80, 100, 80);
       h5.addMouseListener(this);
       h6.setBounds(480, 80, 100, 80);
       h6.addMouseListener(this);

  }
  
  public void refreshDashboard(){
    Request.loadFile();   // reads the requests (this also marks late items as Overdue)
    box1.setText("Total Equipment: " + Equipment.getTotalItems());
    box2.setText("Available: " + Equipment.countByStatus("Available"));
    box3.setText("Borrowed: " + Equipment.countByStatus("Borrowed"));
    box4.setText("Overdue: " + Request.countByStatus("Overdue"));
    box5.setText("Pending Request: " + Request.countByStatus("Pending"));
    loadRecentActivity();
    lastRequestCount = Request.requestList.size();
}
  
  public void setDashboardPane(JPanel dashboard){
      
     dashboard.setLayout(null);
     dashboard.setBackground(Color.WHITE);
     JLabel header = new JLabel("Dashboard");
     

     UserDisplay.setMediumHeader(header);
     header.setBounds(40, 20, 100, 40);
     box1 = new JLabel("Total Equipment");
     box1.setHorizontalAlignment(JLabel.CENTER);

     box2 = new JLabel("Available");
     box2.setHorizontalAlignment(JLabel.CENTER);
     box3 = new JLabel("Borrowed");
     box3.setHorizontalAlignment(JLabel.CENTER);
     box4 = new JLabel("Overdue");
     box4.setHorizontalAlignment(JLabel.CENTER);
     box5 = new JLabel("Pending Request");
     box5.setHorizontalAlignment(JLabel.CENTER);
     JPanel pane = new JPanel();

     box1.setBounds(30, 100, 230, 80);
     box1.setOpaque(true);
     box1.setBorder(BorderFactory.createLineBorder(Color.decode("#2B59C3"), 2));
     box1.setBackground(Color.WHITE);
     box2.setOpaque(true);
     box2.setBounds(280, 100, 230, 80);
     box2.setBorder(BorderFactory.createLineBorder(Color.decode("#2B59C3"), 2));
     box2.setBackground(Color.WHITE);
     box3.setOpaque(true);
     box3.setBounds(530, 100, 230, 80);
     box3.setBorder(BorderFactory.createLineBorder(Color.decode("#2B59C3"), 2));
     box3.setBackground(Color.WHITE);
     box4.setOpaque(true);
     box4.setBounds(780, 100, 230, 80);
     box4.setBorder(BorderFactory.createLineBorder(Color.decode("#2B59C3"), 2));
     box4.setBackground(Color.WHITE);
     box5.setOpaque(true);
     box5.setBounds(1030, 100, 230, 80);
     box5.setBorder(BorderFactory.createLineBorder(Color.decode("#2B59C3"), 2));
     box5.setBackground(Color.WHITE);
     dashboard.add(header);
     dashboard.add(box1);
     dashboard.add(box2);
     dashboard.add(box3);
     dashboard.add(box4);
     dashboard.add(box5);

     dashboard.add(setRecentActivityTables(pane));
  }

  public void setEquipmentPane(JPanel equipmentPane){
      equipmentPane.setLayout(null);
      equipmentPane.setBackground(Color.WHITE);
      JLabel header = new JLabel("Equipment");
      JButton addE = new JButton("+ Add Equipment");
      String[] cgNames = {"All","Available","Partially Borrowed","Borrowed","Under Repair"};
      categories = new JComboBox(cgNames);
      DefaultListCellRenderer renderer = new DefaultListCellRenderer();
      JPanel panelHolder = new JPanel();
      panelHolder.setLayout(null);
      renderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
      search = new JTextField("Search by name, ID, or serial no...");
      JButton delete = new JButton("Delete");
      JButton edit = new JButton("Edit");
      header.setBounds(40, 20, 150, 40);
      UserDisplay.setMediumHeader(header);


      setButtons(addE,1090, 20, 150, 40);
      setButtons(delete,1090, 100, 100, 40);
      setButtons(edit,970, 100, 100, 40);
      search.setBounds(40, 100, 700, 40);
      categories.setBounds(760, 100, 150, 40);
      categories.setRenderer(renderer);
      categories.setFocusable(false);
      categories.addActionListener(this);
      panelHolder.setBounds(40, 160, 1200, 370);
      panelHolder.setBorder(BorderFactory.createLineBorder(Color.decode("#000000")));
      setEquipmentTables(panelHolder);
      search.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.decode("#1A1A1A")),
      BorderFactory.createEmptyBorder(4, 10, 4, 10)));

      equipmentPane.add(header);
      equipmentPane.add(addE);
      equipmentPane.add(categories);
      equipmentPane.add(search);
      equipmentPane.add(delete);
      equipmentPane.add(edit);
      equipmentPane.add(panelHolder);
      search.addFocusListener(this);
      search.addKeyListener(this);
  }

  public void setRequestPane(JPanel requestPane){
      requestPane.setLayout(null);
      requestPane.setBackground(Color.WHITE);
      JLabel header = new JLabel("Requests");
      String[] list ={"All","Pending","Approved","Borrowed","Returned","Overdue","Rejected","Expired"};
      requestFilter = new JComboBox<>(list);
      DefaultListCellRenderer renderer = new DefaultListCellRenderer();
      JPanel panelHolder = new JPanel();
      JPanel sidePanel = new JPanel();
      reasonField = new JTextArea();
      JScrollPane comment = new JScrollPane(reasonField);
      JButton approve = new JButton("Approve");
      JButton reject = new JButton("Reject");
      JButton blockUser = new JButton("Block User");
      JButton deleteRequest = new JButton("Delete Request");
      detailArea = new JTextArea();
      JScrollPane detailScroll = new JScrollPane(detailArea);
      proofPicture = new JLabel("No proof image", JLabel.CENTER);
      proofPicture.setCursor(new Cursor(Cursor.HAND_CURSOR));
      proofPicture.setToolTipText("Click to enlarge");
      proofPicture.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e){
        showProofOverlay();
    }
});
      // table
      String[] columns = {"Request ID","User ID","Role","Equipment","Qty","Date Requested","Status"};
      panelHolder.setLayout(null);
      requestTable = makeTable(panelHolder, columns, 40, 800, 300);
      requestDtm = (DefaultTableModel) requestTable.getModel();
      requestTable.getColumnModel().getColumn(5).setPreferredWidth(160);
      requestTable.getSelectionModel().addListSelectionListener(e -> {
          if (!e.getValueIsAdjusting()) {
              showRequestDetails();
          }
      });

      //Add Components
      requestPane.add(header);
      requestPane.add(requestFilter);
      requestPane.add(panelHolder);
      requestPane.add(sidePanel);

      //Side Panel W 350 x 370 H
      sidePanel.setLayout(null);
      sidePanel.setBackground(Color.WHITE);
      sidePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      sidePanel.add(detailScroll);
      sidePanel.add(proofPicture);
      sidePanel.add(comment);
      sidePanel.add(approve);
      sidePanel.add(reject);
      sidePanel.add(blockUser);
      sidePanel.add(deleteRequest);

      detailArea.setEditable(false);
      detailArea.setLineWrap(true);
      detailArea.setWrapStyleWord(true);
      detailArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
      detailScroll.setBounds(10, 10, 330, 90);
      proofPicture.setBounds(10, 105, 330, 90);
      proofPicture.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      comment.setBounds(10, 200, 330, 100);
      comment.setBackground(Color.WHITE);
      comment.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
      "Comments",
      TitledBorder.LEFT,
      TitledBorder.TOP,
      new Font("Segoe UI", Font.BOLD, 12)),
      BorderFactory.createEmptyBorder(10, 5, 5, 10)));

      reasonField.setLineWrap(true);
      reasonField.setWrapStyleWord(true);
      
      JButton release = new JButton("Release");
      sidePanel.add(release);

        // row 1
      setSidePanelButtons(approve, 10, 320,100);
      setSidePanelButtons(reject, 120, 320,100);
      setSidePanelButtons(release, 230, 320,100);
        // row 2
      setSidePanelButtons(blockUser, 10, 370,100);
      setSidePanelButtons(deleteRequest, 150, 370,150);

      approve.addActionListener(e -> approveRequest());
      reject.addActionListener(e -> rejectRequest());
      release.addActionListener(e -> releaseRequest());
      blockUser.addActionListener(e -> blockRequestUser());
      deleteRequest.addActionListener(e -> deleteSelectedRequest());
      
      //Set Bounds
      header.setBounds(40, 20, 100, 40);
      UserDisplay.setMediumHeader(header);
      requestFilter.setBounds(40, 80, 200, 40);
      panelHolder.setBounds(40, 140, 800, 300);
      sidePanel.setBounds(900, 20, 350, 420);
      
      //Set Components
      renderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
      requestFilter.setFont(new Font("Segoe UI", Font.BOLD,14));
      requestFilter.setBackground(Color.WHITE);
      requestFilter.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      requestFilter.setRenderer(renderer);
      requestFilter.setFocusable(false);
      requestFilter.addActionListener(e -> loadRequestTable());
      panelHolder.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      panelHolder.setBackground(Color.WHITE); 
      
  }
  
  public void releaseRequest(){
    Request r = getSelectedRequest();
    if (r == null) {
        JOptionPane.showMessageDialog(null, "Select a request first!!!");
        return;
    }
    String error = Request.markBorrowed(r.getRequestNo());
    if (error != null) {
        JOptionPane.showMessageDialog(null, error);
        return;
    }
    loadRequestTable();
    JOptionPane.showMessageDialog(null, "Equipment released to the borrower");
}
  
  public void blockRequestUser(){
    Request r = getSelectedRequest();
    if (r == null) {
        JOptionPane.showMessageDialog(null, "Select a request first!!!");
        return;
    }
    String id = String.valueOf(r.getUserId());
    if (BlockedUsers.isBlocked(id, r.getRole())) {
        JOptionPane.showMessageDialog(null, "This user is already blocked");
        return;
    }
    int i = JOptionPane.showConfirmDialog(null, "Block " + r.getRole() + " " + id + " ?\nThey will not be able to log in.",
            "Block User", JOptionPane.YES_NO_OPTION);
    if (i != JOptionPane.YES_OPTION) {
        return;
    }
    String reason = reasonField.getText().trim();   // the Comments box
    if (reason.isEmpty()) {
        reason = "Blocked by admin";
    }
    BlockedUsers.block(id, r.getRole(), reason);
    reasonField.setText("");
    BlockedUsers.loadBlockUsersTable(blockedUserDtm);
    refreshUserTables();   // the Status column of the student and teacher tables shows "Blocked"
    JOptionPane.showMessageDialog(null, r.getRole() + " " + id + " has been blocked");
}
  
  public void deleteSelectedRequest(){
    Request r = getSelectedRequest();
    if (r == null) {
        JOptionPane.showMessageDialog(null, "Select a request first!!!");
        return;
    }
    String status = r.getStatus();
    if (!status.equals("Returned") && !status.equals("Rejected") && !status.equals("Expired")) {
        JOptionPane.showMessageDialog(null, "Only Returned, Rejected or Expired requests can be deleted\nThis request is " + status);
        return;
    }
    if (!confirmAdminPassword()) {
        return;
    }
    int i = JOptionPane.showConfirmDialog(null, "Delete " + r.getRequestNo() + " ?\nThis cannot be undone.", "Delete Request", JOptionPane.YES_NO_OPTION);
    if (i != JOptionPane.YES_OPTION) {
        return;
    }
    String error = Request.delete(r.getRequestNo());
    if (error != null) {
        JOptionPane.showMessageDialog(null, error);
        return;
    }
    loadRequestTable();
    JOptionPane.showMessageDialog(null, "Request deleted");
}
  
  public void loadRequestTable(){
      Request.loadFile();
      lastQueueSize = RequestQueue.size();
      lastQueueFront = RequestQueue.peek();
      String filter = (String) requestFilter.getSelectedItem();
      ArrayList<Request> shown = new ArrayList<>();
      for (int i = Request.requestList.size() - 1; i >= 0; i--) {   // newest first
          Request r = Request.requestList.get(i);
          if (filter.equals("All") || r.getStatus().equals(filter)) {
              shown.add(r);
          }
      }
      ManageFiles.fillTable(requestDtm, shown);
      showRequestDetails();
  }
 
  public void startQueueWatcher(){
    Timer watcher = new Timer(2000, e -> {
        long stamp = new File(ManageFiles.REQUEST).lastModified();
        if (stamp == lastRequestFileTime) {
            return;   // nothing changed, skip the file read
        }
        lastRequestFileTime = stamp;
        if (requestPane.isShowing()) {
            Request.loadFile();
            if (RequestQueue.size() != lastQueueSize || RequestQueue.peek() != lastQueueFront) {
                loadRequestTable();
            }
        } else if (dashboardPane.isShowing()) {
            Request.loadFile();
            if (Request.requestList.size() != lastRequestCount) {
                refreshDashboard();
            }
        }
    });
    watcher.start();
}
  
  public JTable makeTable(JPanel holder, String[] columns, int rowHeight, int width, int height){
      DefaultTableModel model = new DefaultTableModel(columns, 0){
          @Override
          public boolean isCellEditable(int rows, int colums){
              return false;
          }
      };
      JTable table = new JTable(model);
      table.setRowHeight(rowHeight);
      table.getTableHeader().setReorderingAllowed(false);
      DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
      tcr.setHorizontalAlignment(JLabel.CENTER);
      tcr.setVerticalAlignment(JLabel.CENTER);
      for (int i = 0; i < table.getColumnCount(); i++) {
          table.getColumnModel().getColumn(i).setCellRenderer(tcr);
      }
      JScrollPane scrollP = new JScrollPane(table);
      scrollP.setBounds(0, 0, width, height);
      holder.add(scrollP);
      return table;
  }
  
  public Request getTargetRequest(){
    Request r = getSelectedRequest();   // the row the admin clicked
    if (r != null) {
        return r;
    }
    return Request.getNextInQueue();    // otherwise whoever is first in line
}
  
  public void showRequestDetails(){
    Request r = getSelectedRequest();
    String heading = "";
    if (r == null) {
        r = Request.getNextInQueue();
        heading = "NEXT IN QUEUE (" + RequestQueue.size() + " waiting)\n";
    }
    if (r == null) {
        detailArea.setText("");
        currentProofPath = "";
        proofPicture.setIcon(null);
        proofPicture.setText("No proof image");
        return;
    }
    currentProofPath = r.getProofImage();
    String proof = r.getProofText();
      if (proof.isEmpty()) {
          proof = "(no text)";
      }
    detailArea.setText(heading + "Borrower: " + r.getRole() + " " + r.getUserId()
            + "\nItem: " + r.getEquipmentName() + "  x" + r.getQuantity()
            + "\nRequested: " + r.getDateRequested()
            + "\nProof: " + proof);
    detailArea.setCaretPosition(0);
    ImageIcon icon = loadProofIcon(r.getProofImage(), 320, 80);
    if (icon == null) {
        proofPicture.setIcon(null);
        proofPicture.setText("No proof image");
    } else {
        proofPicture.setText("");
        proofPicture.setIcon(icon);
    }
}
  
  public void loadQueueRequestTable(){
      Request.loadFile();
      requestDtm.setRowCount(0);
      
  }
  
  public Request getSelectedRequest(){
      int viewRow = requestTable.getSelectedRow();
      if (viewRow == -1) {
          return null;
      }
      int row = requestTable.convertRowIndexToModel(viewRow);
      return Request.findById((String) requestDtm.getValueAt(row, 0));
  }

  public ImageIcon loadProofIcon(String path, int maxW, int maxH){
      return ManageFiles.loadScaledIcon(path, maxW, maxH);
  }

  public void approveRequest(){
      Request r = getTargetRequest();
      if (r == null) {
          JOptionPane.showMessageDialog(null, "Select a request first!!!");
          return;
      }
      String error = Request.approve(r.getRequestNo(), reasonField.getText().trim());
      if (error != null) {
          JOptionPane.showMessageDialog(null, error);
          return;
      }
      reasonField.setText("");
      loadRequestTable();
      JOptionPane.showMessageDialog(null, "Request approved");
      loadEquipmentTable();
  }
  
  public void resetUserPassword(JTable table, DefaultTableModel model, String role){
    int id = getSelectedUserId(table, model, role);
    if (id == -1) {
        return;
    }
    PasswordReset.adminReset(this, id, role);
}

  public void deleteUserAccount(JTable table, DefaultTableModel model, String role){
    int id = getSelectedUserId(table, model, role);
    if (id == -1 || !confirmAdminPassword()) {
        return;
    }
    if (AccountDeletion.adminDelete(this, id, role)) {
        refreshUserTables();
        JOptionPane.showMessageDialog(this, role + " " + id + " has been deleted");
    }
}
  
  public void rejectRequest(){
      Request r = getTargetRequest();
      if (r == null) {
          JOptionPane.showMessageDialog(null, "Select a request first!!!");
          return;
      }
      String error = Request.reject(r.getRequestNo(), reasonField.getText().trim());
      if (error != null) {
          JOptionPane.showMessageDialog(null, error);
          return;
      }
      reasonField.setText("");
      loadRequestTable();
      loadEquipmentTable();   // the units went back to stock
      JOptionPane.showMessageDialog(null, "Request rejected, stock restored");
      loadEquipmentTable();
  }

  public void applyRules(){
    JTextField[] fields = {inputMaxStudent, inputMaxTeacher, inputStudentPer, inputTeacherPer, inputMaxDays, inputPickup, inputPenalty};
    int[] values = new int[fields.length];
    try{
        for (int i = 0; i < fields.length; i++) {
            values[i] = Integer.parseInt(fields[i].getText().trim());
        }
    }catch(NumberFormatException ex){
        JOptionPane.showMessageDialog(null, "All fields must be valid numbers", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }
    for (int i = 0; i < values.length; i++) {
        boolean isPenalty = (i == values.length - 1);   // only the penalty may be 0
        if (values[i] < 0 || (values[i] == 0 && !isPenalty)) {
            JOptionPane.showMessageDialog(null, "Limits and days must be greater than 0", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
    }
    Request.maxStudentItems = values[0];
    Request.maxTeacherItems = values[1];
    Request.maxStudentPerEquipment = values[2];
    Request.maxTeacherPerEquipment = values[3];
    Request.maxDays = values[4];
    Request.pickupDays = values[5];
    Request.penalty = values[6];
    Request.saveRules();
    JOptionPane.showMessageDialog(null, "Borrowing rules updated");
}
  
  public void setSidePanelButtons(JButton button, int x, int y,int width){
      button.setFocusable(false);
      button.setFont(new Font("Segoe UI", Font.BOLD, 10));
      button.setForeground(Color.WHITE);
      button.setBackground(Color.decode("#2B59C3"));
      button.setBounds(x, y, width, 40);
  }
  
  public DeleteRequests getSelectedDeleteRequest(){
    int viewRow = deleteTable.getSelectedRow();
    if (viewRow == -1) {
        JOptionPane.showMessageDialog(null, "Select a request first!!!");
        return null;
    }
    int row = deleteTable.convertRowIndexToModel(viewRow);
    DeleteRequests.load();
    return DeleteRequests.findPending((int) deleteDtm.getValueAt(row, 0), (String) deleteDtm.getValueAt(row, 1));
}

  public void approveDeleteRequest(){
    DeleteRequests req = getSelectedDeleteRequest();
    if (req == null) {
        return;
    }
    String problem = AccountDeletion.checkCanDelete(req.getUserId(), req.getRole());
    if (problem != null) {
        JOptionPane.showMessageDialog(null, problem);
        return;
    }
    int i = JOptionPane.showConfirmDialog(null, "Remove the account of " + req.getRole() + " " + req.getUserId() + " ?", "Delete", JOptionPane.YES_NO_OPTION);
    if (i != JOptionPane.YES_OPTION) {
        return;
    }
    AccountDeletion.removeAccount(req.getUserId(), req.getRole(), false);   // the ID stays, the owner can sign up again
    req.setStatus("Approved");
    DeleteRequests.loadTable(deleteDtm);
    refreshUserTables();
    JOptionPane.showMessageDialog(null, "Account removed");
}

  public void rejectDeleteRequest(){
    DeleteRequests req = getSelectedDeleteRequest();
    if (req == null) {
        return;
    }
    req.setStatus("Rejected");
    DeleteRequests.loadTable(deleteDtm);
}
  
  public void setSystemPane(JPanel systemPane){
      
      systemPane.setLayout(null);
      systemPane.setBackground(Color.WHITE);
      systemPane.setPreferredSize(new Dimension(1290,1250));

      JLabel header = new JLabel("System");
      JPanel pane1 = new JPanel(); // for return borrowed items 
      JPanel pane3 = new JPanel(); // for borrowing rules
      JPanel pane4 = new JPanel();
      systemPane.add(header);
      systemPane.add(pane1);  
      systemPane.add(pane3);
      systemPane.add(pane4);

      // pane1 components
     JLabel textS = new JLabel("Return Equipment");
     rbutton = new JButton("Returned Borrowed Item");
     rbutton.addActionListener(this);
     // add components
     pane1.add(textS);
     pane1.add(rbutton);
     
     // set pane1 components
     pane1.setLayout(null);
     
     
     pane1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
     pane1.setBackground(Color.WHITE);
     textS.setFont(new Font("Segoe UI",Font.PLAIN,14));
     rbutton.setBackground(Color.decode("#2B59C3"));
     rbutton.setForeground(Color.WHITE);
     rbutton.setFocusable(false);
     textS.setBounds(20, 10, 150, 30);     
     rbutton.setBounds(20, 80, 200, 30);
     header.setBounds(40, 20, 100, 40);
     pane1.setBounds(420, 80, 270, 150);
     UserDisplay.setMediumHeader(header);

    

     // pane 3 Components
     pane3.setLayout(null);
     pane3.setBounds(40, 80, 350, 340);
     pane3.setBorder(BorderFactory.createLineBorder(Color.BLACK));
     pane3.setBackground(Color.WHITE);
     JLabel header3 = new JLabel("Borrowing Rules");
     header3.setBounds(20, 10, 150, 30);
     header3.setFont(new Font("Segoe UI",Font.PLAIN,14));
     pane3.add(header3);

     inputMaxStudent = new JTextField(String.valueOf(Request.maxStudentItems));
     inputMaxTeacher = new JTextField(String.valueOf(Request.maxTeacherItems));
     inputStudentPer = new JTextField(String.valueOf(Request.maxStudentPerEquipment));
     inputTeacherPer = new JTextField(String.valueOf(Request.maxTeacherPerEquipment));
     inputMaxDays = new JTextField(String.valueOf(Request.maxDays));
     inputPickup = new JTextField(String.valueOf(Request.pickupDays));
     inputPenalty = new JTextField(String.valueOf(Request.penalty));   // pesos
     String[] ruleNames = {"Student: different items", "Teacher: different items", "Student: qty per item",
                      "Teacher: qty per item", "Max borrow days", "Pickup within (days)", "Penalty per late day"};
    JTextField[] ruleFields = {inputMaxStudent, inputMaxTeacher, inputStudentPer, inputTeacherPer, inputMaxDays, inputPickup, inputPenalty};
    for (int i = 0; i < ruleFields.length; i++) {
            JLabel label = new JLabel(ruleNames[i]);
            label.setBounds(20, 40 + (i * 35), 170, 30);
            label.setFont(new Font("Segoe UI",Font.PLAIN,13));
            ruleFields[i].setBounds(200, 40 + (i * 35), 120, 30);
            ruleFields[i].setHorizontalAlignment(JLabel.CENTER);
            ruleFields[i].setFont(new Font("Segoe UI",Font.PLAIN,12));
            pane3.add(label);
            pane3.add(ruleFields[i]);
}
     JButton setRules = new JButton("Apply");
     setRules.setBounds(230, 295, 100, 30);
     setRules.setBackground(Color.decode("#2B59C3"));
     setRules.setForeground(Color.WHITE);
     setRules.setFocusable(false);
     setRules.addActionListener(e -> applyRules());
     pane3.add(setRules);

     //Pane 4 tables
     pane4.setLayout(null);
     pane4.setBackground(Color.WHITE);
     pane4.setBounds(40, 440, 1190, 700);
     userPanels = new CardLayout();
     userCard = new JPanel(userPanels);
     JPanel studentHolder = new JPanel();
     JPanel teacherHolder = new JPanel();
     JPanel blockUserHolder = new JPanel();
     pane = new JPanel();
     JButton addStudent = new JButton("Add Student ID");
     editStudent = new JButton("Edit Status");
     searchStudent = new JTextField("Search Student by ID, name, year, or course");
     deleteStudent = new JButton("Delete Student");
     resetStudent = new JButton("Reset Password"); 

     setButtons(addStudent, 1010, 15, 150, 40);
     setButtons(editStudent, 840, 15, 150, 40);
     setButtons(resetStudent, 670, 15, 150, 40);
     setButtons(deleteStudent, 500, 15, 150, 40);
     searchStudent.setBounds(0, 15, 400, 40);
     searchStudent.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(2, 10, 2, 2)));
     searchStudent.setFont(new Font("Segoe UI",Font.PLAIN,12));
     searchStudent.addFocusListener(this);
     searchStudent.addKeyListener(this);
         
     pane.setLayout(null);
     pane.setBounds(0, 0, 1200, 370);
     pane.setBackground(Color.WHITE);
     pane.add(studentHolder);
     pane.add(searchStudent);
     pane.add(addStudent);
     pane.add(editStudent);
     pane.add(resetStudent);
     pane.add(deleteStudent);
     panelAdd = new JPanel(new GridLayout(0, 2, 5, 5));
     studentID = new JTextField();
     statusBox = new JComboBox<>(Student.status);
     Student callS = new Student();
//     String selected = callS.getgetStatusBox(statusBox);
     panelAdd.add(new JLabel("Student ID:")); panelAdd.add(studentID);
     panelAdd.add(new JLabel("Status:"));       panelAdd.add(statusBox);


     JPanel paneTeacher = new JPanel();
     JButton addTeacher= new JButton("Add Teacher ID");
     editTeacher= new JButton("Edit Status");
     resetTeacher = new JButton("Reset Password");
     deleteTeacher = new JButton("Delete Account");
     paneTeacher.add(resetTeacher);
     setButtons(addTeacher, 1010, 15, 150, 40);
     setButtons(editTeacher, 840, 15, 150, 40);
     setButtons(resetTeacher, 670, 15, 150, 40);
     setButtons(deleteTeacher, 500, 15, 150, 40);
     paneTeacher.setLayout(null);
     paneTeacher.setBounds(0, 0, 1200, 370);
     paneTeacher.setBackground(Color.WHITE);
     paneTeacher.add(teacherHolder);
     paneTeacher.add(addTeacher);
     paneTeacher.add(editTeacher);
     paneTeacher.add(deleteTeacher);

     
     panelAddT = new JPanel(new GridLayout(0, 2, 5, 5));
     teacherID = new JTextField();
     statusBoxT = new JComboBox<>(Teacher.status);
     Teacher call2 = new Teacher();
//     String selection = call2.getTeacherStatusBox(statusBoxT);
     panelAddT.add(new JLabel("Teacher ID:")); panelAddT.add(teacherID);
     panelAddT.add(new JLabel("Status:"));     panelAddT.add(statusBoxT);

     JPanel paneBlockUser = new JPanel();
     JButton unBlockUser = new JButton("Unblock User");
     setButtons(unBlockUser, 1010, 15, 150, 40);
     paneBlockUser.setLayout(null);
     paneBlockUser.setBounds(0, 0, 1200, 370);
     paneBlockUser.setBackground(Color.WHITE);
     paneBlockUser.add(blockUserHolder);
     paneBlockUser.add(unBlockUser);

     userCard.setBounds(0, 90, 1200, 450);
     userCard.add(pane,"Students");
     userCard.add(paneTeacher,"Teachers");
     userCard.add(paneBlockUser,"Blocked Users");

     setRegisteredStudentID(studentHolder,Student.Scolumns);
     Student call3 = new Student();
     call3.refreshStudentTable(studentDtm);
     setRegisteredTeacherID(teacherHolder,Teacher.Tcolumns);
     Teacher call4 = new Teacher();
     call4.refreshTeacherTable(teacherDtm);
     setBlockedUsers(blockUserHolder, BlockedUsers.blockUserscolumns);
     BlockedUsers.loadBlockUsersTable(blockedUserDtm);


     hStudent = new JLabel("Student");
     hStudent.setForeground(Color.decode("#2B59C3"));
     hTeacher = new JLabel("Teacher");
     hBlockUsers = new JLabel("Blocked Users");

     JLabel line = new JLabel();
     hStudent.setBounds(100, 30, 100, 40);
     hStudent.addMouseListener(this);
     hTeacher.setBounds(400, 30, 100, 40);
     hTeacher.addMouseListener(this);
     hBlockUsers.setBounds(700, 30, 150, 40);
     hBlockUsers.addMouseListener(this);
     line.setBounds(0,80,1200,1);
     UserDisplay.setMediumFont(hStudent);
     UserDisplay.setMediumFont(hTeacher);
     UserDisplay.setMediumFont(hBlockUsers);

     line.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
     studentHolder.setLayout(null);
     studentHolder.setBounds(0, 80, 1190, 400);

     teacherHolder.setLayout(null);
     teacherHolder.setBounds(0, 80, 1190, 400);
     blockUserHolder.setLayout(null);
     blockUserHolder.setBounds(0, 80, 1190, 400);
     searchTeacher = new JTextField("Search Teacher by ID, name, or department");
     searchTeacher.setBounds(0, 15, 400, 40);
     searchTeacher.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(2, 10, 2, 2)));
     searchTeacher.setFont(new Font("Segoe UI",Font.PLAIN,12));
     searchTeacher.addFocusListener(this);
     searchTeacher.addKeyListener(this);
     paneTeacher.add(searchTeacher);

     searchBlocked = new JTextField("Search by ID, role, reason, or date");
     searchBlocked.setBounds(0, 15, 400, 40);
     searchBlocked.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(2, 10, 2, 2)));
     searchBlocked.setFont(new Font("Segoe UI",Font.PLAIN,12));
     searchBlocked.addFocusListener(this);
     searchBlocked.addKeyListener(this); 
     paneBlockUser.add(searchBlocked);
     
     JPanel deleteHolder = new JPanel();
     JPanel paneDelete = new JPanel();
     approveDelete = new JButton("Approve Delete");
     rejectDelete = new JButton("Reject");
     paneDelete.setLayout(null);
     paneDelete.setBounds(0, 0, 1200, 370);
     paneDelete.setBackground(Color.WHITE);
     paneDelete.add(deleteHolder);
     paneDelete.add(approveDelete);
     paneDelete.add(rejectDelete);
     deleteHolder.setLayout(null);
     deleteHolder.setBounds(0, 80, 1190, 400);
     deleteTable = makeTable(deleteHolder, DeleteRequests.columns, 60, 1190, 370);
     deleteDtm = (DefaultTableModel) deleteTable.getModel();
     DeleteRequests.loadTable(deleteDtm);
     setButtons(approveDelete, 1010, 15, 150, 40);
     setButtons(rejectDelete, 840, 15, 150, 40);
     userCard.add(paneDelete, "Delete Requests");
     hDelete = new JLabel("Delete Requests");
     hDelete.addMouseListener(this); 
     hDelete.setBounds(1000, 30, 180, 40);
     UserDisplay.setMediumFont(hDelete);
     pane4.add(hDelete);
     pane4.add(hStudent);
     pane4.add(hTeacher);
     pane4.add(hBlockUsers);
     pane4.add(line);
     pane4.add(userCard);

  }
  
  public static void loadAdmin(){
    for (String[] data : ManageFiles.loadFile(ManageFiles.ADMIN)) {
        if (data.length == 4) {
            password = data[0];
            adminLast = data[1];
            adminFirst = data[2];
            adminMiddle = data[3];
        }
        return;
    }
}

  public static void saveAdmin(){
    ArrayList<String> lines = new ArrayList<>();
    lines.add(ManageFiles.join(password, ManageFiles.clean(adminLast), ManageFiles.clean(adminFirst), ManageFiles.clean(adminMiddle)));
    ManageFiles.writeLines(ManageFiles.ADMIN, lines);
}
 
  public static boolean checkAdminPassword(String typed){
    loadAdmin();
    return password.equals(typed);
}
  
  public void returnRequest(){
    Request.loadFile();
    String input = JOptionPane.showInputDialog(this, "Enter Request ID (e.g. REQ-3):", "Return Equipment", JOptionPane.QUESTION_MESSAGE);
    if (input == null) return;
    Request r = Request.findById(input.trim().toUpperCase());
    if (r == null) {
        JOptionPane.showMessageDialog(null, "Request " + input.trim() + " was not found");
        return;
    }
    if (!r.getStatus().equals("Borrowed") && !r.getStatus().equals("Overdue")) {
        JOptionPane.showMessageDialog(null, "Only borrowed items can be returned\nThis request is " + r.getStatus());
        return;  
    }
    JComboBox<String> condition = new JComboBox<>(new String[]{"Good", "Damaged", "Lost"});
    JTextArea remarks = new JTextArea();
    JScrollPane remarksScroll = new JScrollPane(remarks);
    remarksScroll.setPreferredSize(new Dimension(50, 60));
    long late = r.getDaysLate();

    JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
    panel.add(new JLabel("Request ID:"));   panel.add(new JLabel(r.getRequestNo()));
    panel.add(new JLabel("Borrower:"));     panel.add(new JLabel(r.getRole() + " " + r.getUserId()));
    panel.add(new JLabel("Equipment:"));    panel.add(new JLabel(r.getEquipmentId() + " - " + r.getEquipmentName()));
    panel.add(new JLabel("Quantity:"));     panel.add(new JLabel(String.valueOf(r.getQuantity())));
    panel.add(new JLabel("Due date:"));     panel.add(new JLabel(r.getDueDate()));
    panel.add(new JLabel("Days late:"));    panel.add(new JLabel(String.valueOf(late)));
    panel.add(new JLabel("Penalty:"));      panel.add(new JLabel("PHP " + (late * Request.penalty)));
    panel.add(new JLabel("Condition:"));    panel.add(condition);
    panel.add(new JLabel("Remarks:"));      panel.add(remarksScroll);

    int choice = JOptionPane.showConfirmDialog(this, panel, "Return Equipment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (choice != JOptionPane.OK_OPTION) return;

    String cond = (String) condition.getSelectedItem();
    double unitCost = 0;
    double charge = 0;
    double paid = 0;

    if (!cond.equals("Good")) {
        // ---- 2nd JOptionPane: the admin enters the item cost (PHP) ----
        JTextField costField = new JTextField();
        JLabel chargeLabel = new JLabel(Request.peso(0));
        chargeLabel.setFont(chargeLabel.getFont().deriveFont(Font.BOLD));
        String ruleText = cond.equals("Lost") ? "Lost item: borrower pays the FULL cost" : "Damaged item: borrower pays HALF the cost";
        int qty = r.getQuantity();
        costField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void update() {
                double c = Request.parseMoney(costField.getText());
                chargeLabel.setText(c < 0 ? "-" : Request.peso(Request.calcCharge(cond, c, qty)));
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });
        JPanel costPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        costPanel.add(new JLabel("Equipment:"));        costPanel.add(new JLabel(r.getEquipmentId() + " - " + r.getEquipmentName()));
        costPanel.add(new JLabel("Condition:"));        costPanel.add(new JLabel(cond));
        costPanel.add(new JLabel("Quantity:"));         costPanel.add(new JLabel(String.valueOf(qty)));
        costPanel.add(new JLabel("Rule:"));             costPanel.add(new JLabel(ruleText));
        costPanel.add(new JLabel("Item cost per unit (PHP):")); costPanel.add(costField);
        costPanel.add(new JLabel("Amount to pay:"));    costPanel.add(chargeLabel);

        while (true) {
            int c2 = JOptionPane.showConfirmDialog(this, costPanel, cond + " Item - Set Cost", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (c2 != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Return cancelled, nothing was saved");
                return;
            }
            unitCost = Request.parseMoney(costField.getText());
            if (unitCost <= 0) {
                JOptionPane.showMessageDialog(null, "Enter a valid item cost greater than 0", "Warning", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            break;
        }
        charge = Request.calcCharge(cond, unitCost, qty);

        // ---- 3rd JOptionPane: the admin enters the money received from the borrower ----
        JTextField paidField = new JTextField(String.format("%.2f", charge));
        JPanel payPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        payPanel.add(new JLabel("Borrower:"));       payPanel.add(new JLabel(r.getRole() + " " + r.getUserId()));
        payPanel.add(new JLabel("Item:"));           payPanel.add(new JLabel(r.getEquipmentName() + " x" + qty + " (" + cond + ")"));
        payPanel.add(new JLabel("Item cost / unit:")); payPanel.add(new JLabel(Request.peso(unitCost)));
        JLabel dueLabel = new JLabel(Request.peso(charge));
        dueLabel.setFont(dueLabel.getFont().deriveFont(Font.BOLD));
        payPanel.add(new JLabel("Amount due:"));     payPanel.add(dueLabel);
        payPanel.add(new JLabel("Money received (PHP):")); payPanel.add(paidField);

        while (true) {
            int c3 = JOptionPane.showConfirmDialog(this, payPanel, "Collect Payment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (c3 != JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Payment not collected\nReturn cancelled, nothing was saved");
                return;
            }
            paid = Request.parseMoney(paidField.getText());
            if (paid < 0) {
                JOptionPane.showMessageDialog(null, "Enter a valid amount", "Warning", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            if (paid + 0.001 < charge) {
                String need = cond.equals("Lost") ? "A lost item must be paid in full" : "A damaged item must be paid at half the item cost";
                JOptionPane.showMessageDialog(null, need + "\nAmount due: " + Request.peso(charge) + "\nReceived: " + Request.peso(paid), "Not enough", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            break;
        }
    }

    String error = Request.markReturned(r.getRequestNo(), cond, remarks.getText().trim(), unitCost, paid);
    if (error != null) {
        JOptionPane.showMessageDialog(null, error);
        return;
    }
    loadRequestTable();
    loadEquipmentTable();
    if (charge > 0) {
        JOptionPane.showMessageDialog(null, "Equipment returned (" + cond + ")\n"
            + "Amount due : " + Request.peso(charge) + "\n"
            + "Received   : " + Request.peso(paid) + "\n"
            + "Change     : " + Request.peso(paid - charge));
    } else {
        JOptionPane.showMessageDialog(null, "Equipment returned");
    }
}
  
  public void setEquipmentTables(JPanel holder){
      String[] columns ={"ID No.","Name","Item Image","Category","Serial No.","Location","QTY","Access","Status","Item Description"};
      equipmentTable = makeTable(holder, columns, 80, 1200, 370);
      dtm = (DefaultTableModel) equipmentTable.getModel();
      equipmentTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      for (int i = 0; i < equipmentTable.getColumnCount(); i++) {
          equipmentTable.getColumnModel().getColumn(i).setPreferredWidth(200);
          if (i == 9) {
              equipmentTable.getColumnModel().getColumn(i).setPreferredWidth(500);   // description only (Status was also 500 by mistake)
          }
      }
      equipmentTable.getColumnModel().getColumn(2).setCellRenderer(new ImageTableCellRenderer());
  }

  public void setRegisteredStudentID(JPanel holder, String[] columns){
    studentTable = makeTable(holder, columns, 60, 1190, 370);
    studentDtm = (DefaultTableModel) studentTable.getModel();
    fixPhotoColumn(studentTable);
}

  public void setRegisteredTeacherID(JPanel holder, String[] columns){
    teacherTable = makeTable(holder, columns, 60, 1190, 370);
    teacherDtm = (DefaultTableModel) teacherTable.getModel();
    fixPhotoColumn(teacherTable);
}

 public void fixPhotoColumn(JTable table){
     javax.swing.table.TableColumn photo = table.getColumnModel().getColumn(1);
     photo.setCellRenderer(new ImageTableCellRenderer());
     photo.setMinWidth(60);
     photo.setMaxWidth(60);
     photo.setPreferredWidth(60);
}

  public void setBlockedUsers(JPanel holder, String[] columns){
      blockUserTable = makeTable(holder, columns, 60, 1190, 370);
      blockedUserDtm = (DefaultTableModel) blockUserTable.getModel();
  }

  public void setButtons(JButton button , int x, int y, int width ,int height){
      button.setFocusable(false);
      button.setBounds(x,y,width,height);
      button.setFont(new Font("Segoe UI", Font.BOLD, 14));
      button.setForeground(Color.WHITE);
      button.setBackground(Color.decode("#2B59C3"));
      button.setBorder(BorderFactory.createEmptyBorder());
      button.setBorder(BorderFactory.createLineBorder(Color.black, 1));
      button.addActionListener(this);
  }

  public JPanel setRecentActivityTables(JPanel pane){
    JLabel header = new JLabel("Recent Activity");
    String[] columns = {"Borrower","Equipment","Date Requested","Status"};

    recentDtm = new DefaultTableModel(columns, 0){
        @Override
        public boolean isCellEditable(int rows, int colums){
            return false;
        }
    };
    recentTable = new JTable(recentDtm);
    JScrollPane scrollP = new JScrollPane(recentTable);
    DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
    recentTable.getTableHeader().setReorderingAllowed(false);
    recentTable.setRowHeight(50);
    recentTable.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,15));
    recentTable.setFont(new Font("Segoe UI",Font.PLAIN,13));

    tcr.setHorizontalAlignment(JLabel.CENTER);
    tcr.setVerticalAlignment(JLabel.CENTER);
    for (int i = 0; i < recentTable.getColumnCount(); i++) {
        recentTable.getColumnModel().getColumn(i).setCellRenderer(tcr);
    }
    pane.setLayout(null);
    pane.setBounds(30, 200, 900, 330);
    pane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    header.setBounds(10,10,150,30);
    UserDisplay.setMediumHeader(header);
    pane.add(header);
    pane.add(scrollP);
    scrollP.setBounds(5, 50, 890, 270);
    pane.setBackground(Color.WHITE);
    return pane;
}
  
  public void loadRecentActivity(){
    Student.loadStudentInfo();   // loaded once, then every row only looks the user up
    Teacher.loadTeacherInfo();
    recentDtm.setRowCount(0);
    int shown = 0;
    for (int i = Request.requestList.size() - 1; i >= 0 && shown < RECENT_LIMIT; i--) {
        Request r = Request.requestList.get(i);
        recentDtm.addRow(new Object[]{getBorrowerName(r), r.getEquipmentName(), r.getDateRequested(), r.getStatus()});
        shown++;
    }
}
  
  public void setupAdminProfile(){
    adminProfile.editProfile.addActionListener(e -> editAdminProfile());
    adminProfile.editPassword.addActionListener(e -> changeAdminPassword());
    loadAdminProfile();
}

  public void loadAdminProfile(){
    loadAdmin();
    adminProfile.show("Admin", adminAccount, adminLast, adminFirst, adminMiddle);
}

  public void editAdminProfile(){
    String[] names = ProfilePanel.askNames(this, "Admin", adminAccount, adminLast, adminFirst, adminMiddle);
    if (names == null) {
        return;
    }
    adminLast = names[0];
    adminFirst = names[1];
    adminMiddle = names[2];
    saveAdmin();
    loadAdminProfile();
    JOptionPane.showMessageDialog(this, "Your profile has been updated");
}

  public void changeAdminPassword(){
    String[] pass = ProfilePanel.askPasswords(this);
    if (pass == null) {
        return;
    }
    if (!checkAdminPassword(pass[0])) {
        JOptionPane.showMessageDialog(this, "Current password is incorrect", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }
    password = pass[1];
    saveAdmin();
    JOptionPane.showMessageDialog(this, "Your password has been changed");
}
// "Last, First" of the borrower, or "Student 2026001234" if the name is not in the file
  public String getBorrowerName(Request r){
    Account user;
    if (r.getRole().equals(Student.role)) {
        user = Student.findIDNumber(r.getUserId());
    } else {
        user = Teacher.findIDNumber(r.getUserId());
    }
    if (user == null || user.getFullName().isEmpty()) {
        return r.getRole() + " " + r.getUserId();
    }
    return user.getFullName();
}

  public void setupAddEquipmentOverlay() {
    overlay.setOpaque(false);
    // swallow mouse events so nothing underneath can be clicked
    overlay.addMouseListener(new java.awt.event.MouseAdapter() {});
    overlay.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {});

    addEquipmentPanel.setSize(700, 620);
    addEquipmentPanel.setBackground(Color.WHITE);
    addEquipmentPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#000000"), 1));
    addEquipmentPanel.setLayout(null);
    JLabel title = new JLabel("Add Equipment");
    title.setBounds(30, 20, 300, 30);
    UserDisplay.setHeaderFont(title, "black");
    addEquipmentPanel.add(title);
    eqNameField = new JTextField("e.g. Projector");
    eqNameField.addFocusListener(this);
    setTitledBorderTextfield(eqNameField,"Equipment name*");

    String[] list = {"Computers", "Audio Visuals", "Accessories","Networking","Laboratory", "Sports","Musical Instruments", "Furniture", "Office Supplies","Tools"};
    JLabel categoryText = new JLabel("Category");
    categoryText.setFont(new Font("Segoe UI",Font.BOLD,14));
    JComboBox<String> categoryBox = new JComboBox<>(list);
    setComboBox(categoryBox);
    categoryBox.addActionListener(this);

    seNoField = new JTextField("Must be unique");
    seNoField.addFocusListener(this);
    setTitledBorderTextfield(seNoField,"Serial number*");

    qtyField = new JTextField("Must be a number");
    qtyField.addFocusListener(this);
    qtyField.addKeyListener(this);
    setTitledBorderTextfield(qtyField,"Quantity*");

    String[] slist = {"Available", "Under Repair", "Borrowed"};
    JLabel statusText = new JLabel("Status");
    statusText.setFont(new Font("Segoe UI",Font.BOLD,14));
    JComboBox<String> status = new JComboBox<>(slist);
    setComboBox(status);
    status.addActionListener(this);

    locationField = new JTextField("e.g. Room 304, Cabinet B");
    locationField.addFocusListener(this);
    setTitledBorderTextfield(locationField,"Storage location*");
    
    JLabel accessText = new JLabel("Access");
    accessText.setFont(new Font("Segoe UI",Font.BOLD,14));
    accessBox = new JComboBox<>(Equipment.accessList);
    setComboBox(accessBox);
    
    JButton addB = new JButton("Add");
    JButton cancel = new JButton("Cancel");
    JButton chooseImage = new JButton("Choose Image");
    imageName = new JLabel("No image selected");
    imageName.setFont(new Font("Segoe UI",Font.PLAIN,11));
    
    descriptionArea = new JTextArea();
    JScrollPane comment = new JScrollPane(descriptionArea);
    setJTextArea(comment, "Item Description");

    //set bounds
    eqNameField.setBounds(30, 70, 630, 50);
    categoryText.setBounds(30, 130, 200, 30);
    categoryBox.setBounds(30, 160, 250, 40);
    seNoField.setBounds(30, 220, 250, 50);
    qtyField.setBounds(350, 220, 310, 50);
    statusText.setBounds(350, 130, 250, 30);
    status.setBounds(350, 160, 250, 40);
    chooseImage.setBounds(30, 290, 150, 40);
    imageName.setBounds(190, 290, 150, 40);
    accessText.setBounds(350, 265, 250, 30);
    accessBox.setBounds(350, 295, 250, 40);
    locationField.setBounds(30, 350, 630, 50);
    comment.setBounds(30, 420, 630, 100);
    addB.setBounds(240, 550, 100, 40);
    cancel.setBounds(360, 550, 100, 40);

    for (JButton b : new JButton[]{addB, cancel, chooseImage}) {
        b.setFocusable(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    addB.setBackground(Color.decode("#2B59C3"));
    addB.setForeground(Color.WHITE);
    chooseImage.setBackground(Color.decode("#2B59C3"));
    chooseImage.setForeground(Color.WHITE);
      //add Components
      addEquipmentPanel.add(eqNameField);
      addEquipmentPanel.add(categoryText);
      addEquipmentPanel.add(categoryBox);
      addEquipmentPanel.add(seNoField);
      addEquipmentPanel.add(qtyField);
      addEquipmentPanel.add(statusText);
      addEquipmentPanel.add(status);
      addEquipmentPanel.add(chooseImage);
      addEquipmentPanel.add(imageName);
      addEquipmentPanel.add(accessText);
      addEquipmentPanel.add(accessBox);
      addEquipmentPanel.add(status);
      addEquipmentPanel.add(locationField);
      addEquipmentPanel.add(comment);

    cancel.addActionListener(e -> hideAddEquipment());
    chooseImage.addActionListener(e -> chooseEquipmentImage());
    addB.addActionListener(e -> {
        String newName = eqNameField.getText().trim();
        String newSerial = seNoField.getText().trim();
        String newLoc = locationField.getText().trim();
        String newQtyText = qtyField.getText().trim();
       if (newName.isEmpty() || newName.equals("e.g. Projector") || newSerial.isEmpty() || newSerial.equals("Must be unique") || newLoc.isEmpty() || newLoc.equals("e.g. Room 304, Cabinet B")){
            JOptionPane.showMessageDialog(null, "Please fill in all the fields.");
            return;
        }
        int qty;
        try{
            qty = Integer.parseInt(newQtyText);
        } catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(null, "Quantity must be a valid number","Warning",JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (qty <= 0 ) {
            JOptionPane.showMessageDialog(null, "Quantity must be greater than 0","Warning",JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (Equipment.findBySerial(newSerial) != null) {
            JOptionPane.showMessageDialog(null, "This serial number already exists");
            return;
        }
            for (Equipment other: Equipment.equipmentList) {
                if (other.getSerialNo().equalsIgnoreCase(newSerial)) {
                    JOptionPane.showMessageDialog(null, "This serial number already exists");
                    return;
                }
            }
         selectedCategory =(String) categoryBox.getSelectedItem();
         selectedStatus = (String) status.getSelectedItem();
         
         String imagePath = Equipment.copyImage(selectedImagePath, newSerial);
         String description = descriptionArea.getText().trim();
         String access = (String) accessBox.getSelectedItem();
         Equipment get = Equipment.addEquipment(eqNameField.getText(), selectedCategory , seNoField.getText(), locationField.getText(), qty, selectedStatus, imagePath, description, access);
         addEquipment(get);

         eqNameField.setText("e.g. Projector");
         categoryBox.setSelectedIndex(0);
         seNoField.setText("Must be unique");
         locationField.setText("e.g. Room 304, Cabinet B");
         qtyField.setText("Must be a number");
         status.setSelectedIndex(0);
         accessBox.setSelectedIndex(0);
         descriptionArea.setText("");
         selectedImagePath = "";
         imageName.setText("No image selected");
        hideAddEquipment();
    });

    addEquipmentPanel.add(addB);
    addEquipmentPanel.add(cancel);
    overlay.add(addEquipmentPanel);
    setupProofOverlay();
    this.setGlassPane(overlay);
    overlay.setVisible(false);


}
  
  public void setupProofOverlay(){
    proofViewPanel.setSize(900, 620);
    proofViewPanel.setBackground(Color.WHITE);
    proofViewPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#000000"), 1));

    JLabel title = new JLabel("Proof Image");
    title.setBounds(30, 20, 300, 30);
    UserDisplay.setHeaderFont(title, "black");

    proofFull.setBounds(30, 70, 840, 450);
    proofFull.setBorder(BorderFactory.createLineBorder(Color.BLACK));

    JButton close = new JButton("Close");
    close.setBounds(375, 550, 150, 40);
    close.setFocusable(false);
    close.setFont(new Font("Segoe UI", Font.BOLD, 12));
    close.setBackground(Color.decode("#2B59C3"));
    close.setForeground(Color.WHITE);
    close.addActionListener(e -> hideOverlay());

    proofViewPanel.add(title);
    proofViewPanel.add(proofFull);
    proofViewPanel.add(close);
    proofViewPanel.setVisible(false);
    overlay.add(proofViewPanel);
}
  
  public void showProofOverlay(){
    ImageIcon icon = loadProofIcon(currentProofPath, 840, 450);
    if (icon == null) { // if icon is null
        JOptionPane.showMessageDialog(null, "No image available");
        return; // stop here
    }
    proofFull.setIcon(icon);
    showOverlay(proofViewPanel);
}

// shows the dark overlay with ONE panel in the middle
  public void showOverlay(JPanel panel){
    addEquipmentPanel.setVisible(panel == addEquipmentPanel);
    proofViewPanel.setVisible(panel == proofViewPanel);
    panel.setLocation(
        (overlay.getWidth() - panel.getWidth()) / 2,
        (overlay.getHeight() - panel.getHeight()) / 2);
    overlay.setVisible(true);
    overlay.requestFocusInWindow();
}

  public void hideOverlay(){
    overlay.setVisible(false);
}
    
  public void chooseEquipmentImage(){
   String path = BrowseImage();
    if (path.isEmpty()) {
        return;
    }
    selectedImagePath = path;
    imageName.setText(new File(path).getName());
}

  public String BrowseImage(){
    JFileChooser chooser = new JFileChooser("C:\\Users\\ADMIN\\OneDrive\\Documents\\Project OOP\\Images");
    chooser.setFileFilter(new FileNameExtensionFilter("Images (png,jpg,jpeg)","png","jpg","jpeg"));
    int result = chooser.showOpenDialog(this);
    if (result != JFileChooser.APPROVE_OPTION) {
        return "";
    }
    return chooser.getSelectedFile().getAbsolutePath();
}

  public ImageIcon getEquipmentIcon(String path){
    if (path == null || path.isEmpty()) {
        return null; // if empty or null, return null
    }
    try {
        File file = new File(path);
        if (!file.exists()) { // if file does not exits, return null to ImageIcon
            return null;
        }
        BufferedImage img = ImageIO.read(file);
        if (img == null) {
            return null;
        }
        return new ImageIcon(img);
    }catch(IOException e){
        return null;
    }
    
}

  public void loadEquipmentTable(){
      Equipment.loadFile();
      for (Equipment e : Equipment.equipmentList) {
          e.refreshStatus();
      }
      ManageFiles.fillTable(dtm, Equipment.equipmentList);
  }

  public void deleteEquipment(){
    int row = equipmentTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(null, "Select an equipment first!!!");
        return;
    }
    
    
    String id = (String) dtm.getValueAt(row, 0);
    String name = (String) dtm.getValueAt(row, 1);
    String status = (String) dtm.getValueAt(row, 8);    
    Equipment item = Equipment.findById(id);
    if (item != null && item.getQty() < item.getTotalQty()) {
    JOptionPane.showMessageDialog(null, "Some units are still being borrowed\ncannot delete " + name + " at this moment");
    return;
}
    if (status.equals("Borrowed") || status.equals("Partially Borrowed")) {
                JOptionPane.showMessageDialog(null, "Equipment is still being borrowed\ncannot delete " + name + " at this moment");
                 return;
        }
      if (!confirmAdminPassword()) {
          return;
      }
    int i = JOptionPane.showConfirmDialog(null, "Are you Sure?\nYou want to delete " + name + " ?", "Delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (i == 0) {
        Equipment.removeEquipment(id);
        dtm.removeRow(row);
        refreshDashboard();
        JOptionPane.showMessageDialog(null, name + " has been sucessfully deleted");
    }else{
        JOptionPane.showMessageDialog(null, "Deletion is cancelled");
    }
            
            
        

}

  public void editEquipment() {
    int viewRow = equipmentTable.getSelectedRow();
    if (viewRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a row first.");
        return;
    }
    int row = equipmentTable.convertRowIndexToModel(viewRow);   // model index, convert once

    String id = dtm.getValueAt(row, 0).toString().trim();
    Equipment find = Equipment.findById(id);
    if (find == null) {
        JOptionPane.showMessageDialog(this, "Equipment " + id + " was not found.");
        return;
    }

    String[] cg   = {"Computers", "Audio Visuals", "Accessories","Networking","Laboratory", "Sports","Musical Instruments", "Furniture", "Office Supplies","Tools"};
    String[] stat = {"Available", "Under Repair"};

    boolean borrowed = find.getStatus().equals("Borrowed");
    if (borrowed) {
        JOptionPane.showMessageDialog(null, "The Item is being borrowed\nCannot edit it at this time");
        return;
    }
    // pre-fill from the componetnts, not from the table text
    JTextField name         = new JTextField(find.getEquipmentName());
    JTextField serialNumber = new JTextField(find.getSerialNo());
    JTextField location     = new JTextField(find.getLocation());
    JTextField qty          = new JTextField(String.valueOf(find.getTotalQty()));
    JComboBox<String> categories = new JComboBox<>(cg);
    categories.setSelectedItem(find.getCategory());
    JComboBox<String> status = new JComboBox<>(stat);
    JComboBox<String> accessEdit = new JComboBox<>(Equipment.accessList);
    JTextArea editDescription = new JTextArea();
    JScrollPane show = new JScrollPane(editDescription);
     editImagePath = "";
    editDescription.setText(find.getDescription());
    editImagePath = "";
    show.setPreferredSize(new Dimension(50,50));
    changeImage = new JButton("Change Image");
    changeImage.setFocusable(false);
    changeImage.setBackground(Color.decode("#2B59C3"));
    changeImage.setForeground(Color.WHITE);
    changeImage.setFont(new Font("Segoe UI",Font.PLAIN,12));
    changeImage.addActionListener(this);
    accessEdit.setSelectedItem(find.getAccess());
    status.setSelectedItem(find.getStatus());

    JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5)); //
    panel.add(new JLabel("Equipment name:")); panel.add(name);
    panel.add(new JLabel("Category:"));       panel.add(categories);
    panel.add(new JLabel("Serial Number:"));  panel.add(serialNumber);
    panel.add(new JLabel("Location:"));       panel.add(location);
    panel.add(new JLabel("Total Quantity:")); panel.add(qty);
    panel.add(new JLabel("Status:"));         panel.add(status);
    panel.add(new JLabel("Available to:"));   panel.add(accessEdit);
    panel.add(new JLabel("Description:"));    panel.add(show);
    panel.add(new JLabel("Image:"));   panel.add(changeImage);
    

    int choice = JOptionPane.showConfirmDialog(this, panel, "Edit Row",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (choice != JOptionPane.OK_OPTION) return;

    String newName   = name.getText().trim();
    String newSerial = serialNumber.getText().trim();
    String newLoc    = location.getText().trim();

    if (newName.isEmpty() || newSerial.isEmpty() || newLoc.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill in all the fields.");
        return;
    }

    int newQty;
    try {
        newQty = Integer.parseInt(qty.getText().trim());
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Quantity must be a valid number.");
        return;
    }
    int borrowedUnits = find.getTotalQty() - find.getQty();
    if (newQty < borrowedUnits) {
        JOptionPane.showMessageDialog(this, borrowedUnits + " unit(s) are still requested or borrowed\nTotal cannot be lower than that.");
        return;
    }

    Equipment sameSerial = Equipment.findBySerial(newSerial);
    if (sameSerial != null && sameSerial != find) {
        JOptionPane.showMessageDialog(this, "This serial number already exists!");
        return;
    }

    
    find.updateDetails(newName, (String) categories.getSelectedItem(),newSerial, newLoc, newQty, (String) status.getSelectedItem(),(String) accessEdit.getSelectedItem(), editDescription.getText().trim());
    find.setTotalQty(newQty);
    find.refreshStatus();
      if (!editImagePath.isEmpty()) {
          find.setImagePath(Equipment.copyImage(editImagePath, newSerial));
      }
         
    dtm.setValueAt(find.getEquipmentName(), row, 1);
    dtm.setValueAt(find.getIcon(),          row, 2);
    dtm.setValueAt(find.getCategory(),      row, 3);
    dtm.setValueAt(find.getSerialNo(),      row, 4);
    dtm.setValueAt(find.getLocation(),      row, 5);
    dtm.setValueAt(find.getQTY(),           row, 6);   // see note below
    dtm.setValueAt(find.getAccess(),        row, 7);
    dtm.setValueAt(find.getStatus(),        row, 8);
    dtm.setValueAt(find.getDescription(),   row, 9);  
    refreshDashboard();

    JOptionPane.showMessageDialog(this, newName + " has been updated.");
} 

  public void editStudentStatus(){
    int row = studentTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(null, "Select a student first!!!");
        return;
    }
    int id = (int) studentDtm.getValueAt(row, 0);
    Student.loadStudentInfo();
    Student find = Student.findIDNumber(id);
    if (find == null) {
        JOptionPane.showMessageDialog(this, "Student ID " + id + " was not found.");
        return;
    }
    if (find.isBlocked()) {
        JOptionPane.showMessageDialog(null, "This student is blocked\nUnblock the student first in the Blocked Users tab");
        return;
    }
    JComboBox<String> newStatus = new JComboBox<>(Student.status);
    newStatus.setSelectedItem(find.getStudentStatus());
 
    JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
    panel.add(new JLabel("Student ID:")); panel.add(new JLabel(String.valueOf(id)));
    panel.add(new JLabel("Status:"));     panel.add(newStatus);
 
    int choice = JOptionPane.showConfirmDialog(this, panel, "Edit Status",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (choice != JOptionPane.OK_OPTION) return;
 
    String statusText = (String) newStatus.getSelectedItem();
    if (Student.changeStatus(id, statusText)) {
        Student refresh = new Student();
        refresh.refreshStudentTable(studentDtm);
        JOptionPane.showMessageDialog(this, "Student ID: " + id + " is now " + statusText);
    }
}

  public void setJTextArea(JScrollPane holder, String title){
    holder.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
    title,
    TitledBorder.LEFT,
    TitledBorder.TOP,
    new Font("Segoe UI", Font.BOLD, 12)),
    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
}

  public void setTitledBorderTextfield(JTextField field, String title){
    field.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
    title,
    TitledBorder.LEFT,
    TitledBorder.TOP,
    new Font("Segoe UI", Font.BOLD, 12)),
    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
}

  public void editTeacherStatus(){
    int row = teacherTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(null, "Select a teacher first!!!");
        return;
    }
    int id = (int) teacherDtm.getValueAt(row, 0);
    Teacher.loadTeacherInfo();
    Teacher find = Teacher.findIDNumber(id);
    if (find == null) {
        JOptionPane.showMessageDialog(this, "Teacher ID " + id + " was not found.");
        return;
    }
    if (find.isBlocked()) {
        JOptionPane.showMessageDialog(null, "This teacher is blocked\nUnblock the teacher first in the Blocked Users tab");
        return;
    }
    JComboBox<String> newStatus = new JComboBox<>(Teacher.status);
    newStatus.setSelectedItem(find.getTeacherStatus());
 
    JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
    panel.add(new JLabel("Teacher ID:")); panel.add(new JLabel(String.valueOf(id)));
    panel.add(new JLabel("Status:"));     panel.add(newStatus);
 
    int choice = JOptionPane.showConfirmDialog(this, panel, "Edit Status",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (choice != JOptionPane.OK_OPTION) return;
 
    String statusText = (String) newStatus.getSelectedItem();
    if (Teacher.changeStatus(id, statusText)) {
        Teacher refresh = new Teacher();
        refresh.refreshTeacherTable(teacherDtm);
        JOptionPane.showMessageDialog(this, "Teacher ID: " + id + " is now " + statusText);
    }
}

  public void unblockUser(){
    int row = blockUserTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(null, "Select a user first!!!");
        return;
    }
    String user = (String) blockedUserDtm.getValueAt(row, 0);
    String roleText = (String) blockedUserDtm.getValueAt(row, 1);
    String statusText = (String) blockedUserDtm.getValueAt(row, 4);
    if (!statusText.equals("Blocked")) {
        JOptionPane.showMessageDialog(null, "This user is not blocked");
        return;
    }
    int i = JOptionPane.showConfirmDialog(null, "Unblock " + roleText + " " + user + " ?", "Unblock", JOptionPane.YES_NO_OPTION);
    if (i != 0) {
        return;
    }
    int id = Integer.parseInt(user);
    boolean done = false;
    if (roleText.equals(Student.role)) {
        done = Student.unBlock(id);
    }else if(roleText.equals(Teacher.role)){
        done = Teacher.unBlock(id);
    }
    if (done) {
        BlockedUsers.loadBlockUsersTable(blockedUserDtm);
        Student refreshS = new Student();
        refreshS.refreshStudentTable(studentDtm);
        Teacher refreshT = new Teacher();
        refreshT.refreshTeacherTable(teacherDtm);
        JOptionPane.showMessageDialog(null, roleText + " " + user + " has been unblocked");
    }
}

  public void setTitledBorderTextfield(JPanel field, String title){
    field.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
    title,
    TitledBorder.LEFT,
    TitledBorder.TOP,
    new Font("Segoe UI", Font.BOLD, 12)),
    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
}

  public void setComboBox(JComboBox comboBox){
     DefaultListCellRenderer renderer = new DefaultListCellRenderer();
    renderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
    comboBox.setBounds(30, 160, 250, 50);
    comboBox.setFont(new Font("Segoe UI", Font.BOLD, 12));
    comboBox.setBackground(Color.WHITE);
    comboBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    comboBox.setRenderer(renderer);
    comboBox.setLightWeightPopupEnabled(false);
    comboBox.setFocusable(false);
}

  public void showAddEquipment() {
    showOverlay(addEquipmentPanel);
    equipmentPane.setEnabled(false);
}

  public void addEquipment(Equipment e){
      dtm.addRow(e.toRow());
  }

  public void hideAddEquipment() {
    hideOverlay();
    equipmentPane.setEnabled(true);
}
  
  public String getKeyword(JTextField field, String hint){
    String text = field.getText().trim();
    if (text.equals(hint)) {
        return "";
    }
    return text;
}
  
  public int getSelectedUserId(JTable table, DefaultTableModel model, String role){
    int viewRow = table.getSelectedRow();
    if (viewRow == -1) {
        JOptionPane.showMessageDialog(null, "Select a " + role.toLowerCase() + " first!!!");
        return -1;
    }
    return (int) model.getValueAt(table.convertRowIndexToModel(viewRow), 0);
}

  public void refreshUserTables(){
    Student refreshS = new Student();
    refreshS.refreshStudentTable(studentDtm);
    Teacher refreshT = new Teacher();
    refreshT.refreshTeacherTable(teacherDtm);
}

// the password box that deleteEquipment had inline, now shared
  public boolean confirmAdminPassword(){
    JPasswordField passField = new JPasswordField();
    JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
    panel.add(new JLabel("Password:")); panel.add(passField);
    int choice = JOptionPane.showConfirmDialog(this, panel, "Confirm Password",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (choice != JOptionPane.OK_OPTION) {
        return false;
    }
    if (!new String(passField.getPassword()).equals(getAdminPassword())) {
        JOptionPane.showMessageDialog(this, "Incorrect password", "Warning", JOptionPane.WARNING_MESSAGE);
        return false;
    }
    return true;
}
  
  
    @Override
    public void focusGained(FocusEvent e) {
        if (e.getSource().equals(search)) {
            if (search.getText().equals("Search by name, ID, or serial no...")) {
                search.setText("");
            }
        }else if(e.getSource().equals(searchStudent)){
            if (searchStudent.getText().equals("Search Student by ID, name, year, or course")) {
                searchStudent.setText("");
            }
        }else if (e.getSource().equals(searchTeacher)) {
            if (searchTeacher.getText().equals("Search Teacher by ID, name, or department")) searchTeacher.setText("");
        }else if (e.getSource().equals(searchBlocked)) {
            if (searchBlocked.getText().equals("Search by ID, role, reason, or date")) searchBlocked.setText("");
        }else if (e.getSource().equals(locationField)) {
            if (locationField.getText().equals("e.g. Room 304, Cabinet B")) {
                locationField.setText("");
            }
       }else if (e.getSource().equals(seNoField)) {
            if (seNoField.getText().equals("Must be unique")) {
                seNoField.setText("");
            }
       }else if (e.getSource().equals(qtyField)) {
            if (qtyField.getText().equals("Must be a number")) {
                qtyField.setText("");
            }
       }else if (e.getSource().equals(eqNameField)) {
             if(eqNameField.getText().equals("e.g. Projector")){
                 eqNameField.setText("");
                }
       }
    }
    @Override
    public void focusLost(FocusEvent e) {
         if (e.getSource().equals(search)) {
            if (search.getText().isEmpty()) {
                search.setText("Search by name, ID, or serial no...");
            }
        }else if(e.getSource().equals(searchStudent)){
            if (searchStudent.getText().isEmpty()) {
                searchStudent.setText("Search Student by ID, name, year, or course");
            }
        }else if (e.getSource().equals(searchTeacher)) {
            if (searchTeacher.getText().isEmpty()) searchTeacher.setText("Search Teacher by ID, name, or department");
        }else if (e.getSource().equals(searchBlocked)) {
            if (searchBlocked.getText().isEmpty()) searchBlocked.setText("Search by ID, role, reason, or date");
        }else if (e.getSource().equals(locationField)) {
            if (locationField.getText().isEmpty()) {
                locationField.setText("e.g. Room 304, Cabinet B");
            }
       }else if (e.getSource().equals(seNoField)) {
            if (seNoField.getText().isEmpty()) {
                seNoField.setText("Must be unique");
            }
       }else if (e.getSource().equals(qtyField)) {
            if (qtyField.getText().isEmpty()) {
                qtyField.setText("Must be a number");
            }
       }else if (e.getSource().equals(eqNameField)) {
            if (eqNameField.getText().isEmpty()){
                 eqNameField.setText("e.g. Projector");
            }
       }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (e.getSource().equals(qtyField)) {
            if (Character.isLetter(c)) {
            e.consume(); // consume or get rid of the input if letter
        }
      }

    }

    @Override
    public void keyPressed(KeyEvent e) {
       // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void keyReleased(KeyEvent e) {
    if (e.getSource().equals(search)) {
        searchEquipment();
    }else if(e.getSource().equals(searchStudent)){
        searchStudents();
    }else if(e.getSource().equals(searchTeacher)){
        searchTeachers();
    }else if(e.getSource().equals(searchBlocked)){
        searchBlockedUsers();
    }
}

    public void searchEquipment(){
        String status = (String) categories.getSelectedItem();
        ArrayList<Equipment> byStatus = new ArrayList<>();
        for (Equipment e : Equipment.equipmentList) {
        if (status.equals("All") || e.getStatus().equals(status)) {
            byStatus.add(e);
        }
    }
        ArrayList<Equipment> shown = SearchHelper.search(byStatus, getKeyword(search, "Search by name, ID, or serial no..."),
            e -> e.getEquipmentName(), e -> e.getIDNo(), e -> String.valueOf(e.getIdNumber()), e -> e.getSerialNo());
        ManageFiles.fillTable(dtm, shown);
}

    public void searchStudents(){
        Student.loadStudentInfo();
        BlockedUsers.loadBlockedUsers();   // the table shows "Blocked" in the status column
        ArrayList<Student> shown = SearchHelper.search(Student.studentList, getKeyword(searchStudent, "Search Student by ID, name, year, or course"),
        s -> String.valueOf(s.getIdNumber()), s -> s.getLastName(), s -> s.getFirstName(),
        s -> s.getMiddleName(), s -> ProfilePanel.getGender(Student.role, s.getIdNumber()),
        s -> s.getStudentCourse(), s -> s.getYear());
        ManageFiles.fillTable(studentDtm, shown);
    }

    public void searchTeachers(){
        Teacher.loadTeacherInfo();
        BlockedUsers.loadBlockedUsers();
        ArrayList<Teacher> shown = SearchHelper.search(Teacher.teacherList, getKeyword(searchTeacher, "Search Teacher by ID, name, or department"),
        t -> String.valueOf(t.getIdNumber()), t -> t.getLastName(), t -> t.getFirstName(),
        t -> t.getMiddleName(), t -> ProfilePanel.getGender(Teacher.role, t.getIdNumber()),
        t -> t.getDepartment());
        ManageFiles.fillTable(teacherDtm, shown);
    }

    public void searchBlockedUsers(){
        BlockedUsers.loadBlockedUsers();
        ArrayList<BlockedUsers> shown = SearchHelper.search(BlockedUsers.getBlockedList(), getKeyword(searchBlocked, "Search by ID, role, reason, or date"),
            b -> b.getUser(), b -> b.getRole(), b -> b.getReason(), b -> b.getDate());
        ManageFiles.fillTable(blockedUserDtm, shown);
    }   



     public static class Equipment implements SavesLoad{
     //Array of Objects
  private int idNo = 0;
  private ImageIcon icon = null;
  static String[] accessList = {"Students and Teachers","Teachers Only"};
  private String eqName;
  private String category;
  private String serialNo;
  private String location;
  private int qty = 0;
  private int totalQty = 0;
  private String status;
  private String imagePath = "";
  private String description = "";
  private String access = "Students and Teachers";
  static int counter = 0;
  static final int MAX_STUDENT_ITEM_PER_EQUIPMENT = 1;
  static final int MAX_TEACHER_ITEM_PER_EQUIPMENT = 3;
  static ArrayList<Equipment> equipmentList = new ArrayList<>();

  public void setDetails(String eqName, String category, String serialNo, String location, int qty, String status){
         counter++;
         this.idNo = counter;
         this.eqName = eqName;
         this.category = category;
         this.serialNo = serialNo;
         this.location = location;
         this.qty = qty;
         this.status = status;
         this.totalQty = qty;
     }
  
   public void setExtras(String imagePath, String description, String access){
      this.imagePath = imagePath;
      this.description = description;
      this.access = accessList[0];
      for (String a : accessList) {
          if (a.equalsIgnoreCase(access.trim())) {
              this.access = a;
          }
      }
      loadIcon();
  }
  
        
  public static Equipment addEquipment(String name, String category, String serialNo, String location, int qty, String status, String imagePath, String description, String access){
         Equipment get = new Equipment();
         get.setDetails(name, category, serialNo, location, qty, status);
         get.setExtras(imagePath, description, access);
         equipmentList.add(get);
         saveFile();
         return get;
     }

  public void setSaveEquipment(int idNo, String eqName, String category, String serialNo, String location, int qty, String status ){
         this.idNo = idNo;
         if (idNo > counter) {
             counter = idNo;
         }
         this.eqName = eqName;
         this.category = category;
         this.serialNo = serialNo;
         this.location = location;
         this.qty = qty;
         this.totalQty = qty;
         this.status = status;
     }

  
      


  public static void removeEquipment(String id){
         for (int i = 0; i < equipmentList.size(); i++) {
             if (equipmentList.get(i).getIDNo().equals(id)) {
                 equipmentList.remove(i);
                 break;
             }
         }
         saveFile();
     }
 // Data Structure
    public static Equipment findById(String id) { // binary search
    int target = SearchHelper.parseId(id, "EQ-");
    if (target == -1) {
        return null;
    }
    int[] ids = new int[equipmentList.size()];
    for (int i = 0; i < ids.length; i++) {
        ids[i] = equipmentList.get(i).getIdNumber();
    }
    int index = SearchHelper.binarySearch(ids, 0, ids.length - 1, target);
    if (index == -1) {
        return null;
    }
    return equipmentList.get(index);
}


  
  public void setImagePath(String imagePath){
      this.imagePath = imagePath;
      loadIcon();
  }
  
  public void updateDetails(String eqName, String category, String serialNo,String location, int qty, String status, String access, String description){
    this.eqName = eqName;
    this.category = category;
    this.serialNo = serialNo;
    this.location = location;
    this.qty = qty;
    this.status = status;
    this.access = access;
    this.description = ManageFiles.clean(description);
}
  public static void saveFile(){
      ManageFiles.saveFile(ManageFiles.EQUIPMENT, equipmentList);
  }
   public static void loadFile(){
      equipmentList.clear();
      counter = 0;
      for (String[] data : ManageFiles.loadFile(ManageFiles.EQUIPMENT)) {
          if (data.length != 7 && data.length != 10 && data.length != 11) {
              continue;
          }
          try{
              Equipment get = new Equipment();
              get.setSaveEquipment(Integer.parseInt(data[0]), data[1], data[2], data[3], data[4], Integer.parseInt(data[5]), data[6]);
              if (data.length >= 10) {
                  get.setExtras(data[7], data[8], data[9]);
              }
              if (data.length == 11) {
                  get.setTotalQty(Integer.parseInt(data[10]));
              }
              equipmentList.add(get);
          }catch(NumberFormatException e){
              System.out.println("Skipped a bad equipment line");
          }
      }
        Comparator<Equipment> byId = (a, b) -> Integer.compare(a.idNo, b.idNo);
      if (!SearchHelper.isSorted(equipmentList, byId)) {          // findById is a binary search, it needs this order
          SearchHelper.selectionSort(equipmentList, byId);
      }      
      
  }

  public void loadIcon(){
      icon = ManageFiles.loadScaledIcon(imagePath, 180, 70);
  }

  public static String copyImage(String sourcePath, String serialNo){
      return ManageFiles.copyFile(sourcePath, ManageFiles.IMAGE_FOLDER, "EQ_" + serialNo.replaceAll("[^a-zA-Z0-9]", ""));
  }

  // your duplicate serial checks were looping or calling findById (which is for EQ-1, not serials)
  public static Equipment findBySerial(String serial){
      for (Equipment e : equipmentList) {
          if (e.serialNo.equalsIgnoreCase(serial.trim())) {
              return e;
          }
      }
      return null;
  }
  
   @Override
        public Object[] toRow() {
             return new Object[]{getIDNo(), eqName, icon, category, serialNo, location, getQTY(), access, status, description};
        }
        
     @Override
        public String toLine(){
        return ManageFiles.join(idNo, ManageFiles.clean(eqName), category, ManageFiles.clean(serialNo), ManageFiles.clean(location),
              qty, status, imagePath, ManageFiles.clean(description), access, totalQty);
  }
        
  public void setQty(int qty){
      this.qty = qty;
  }
  
  public void setTotalQty(int totalQty){
      this.totalQty = totalQty;
  }
  
  public void setStatus(String status){
      this.status = status;
  }
  
  public void refreshStatus(){
      if (status.equals("Under Repair")) {
          return;
      }
     if (qty <= 0) {
        status = "Borrowed";
    }else if(qty < totalQty){
        status = "Partially Borrowed";
    }else{
        status = "Available";
    }
  }
  
  public static int getTotalItems(){
      return equipmentList.size();
  }
  
  public static int countByStatus(String status){
      int total = 0;
      for (Equipment e: equipmentList) {
          if (e.getStatus().equals(status)) {
              total++;
          }
      }
      return total;
  }
  
  public int minusQtyEquipment(int removed){
      int number = qty - removed;
      setQty(number);
      return this.getQty();
  } 
  public static int countBorrowedItems(){
    int total = 0;
    for (Equipment e : equipmentList) {
        if (e.getQty() < e.getTotalQty()) {
            total++;
        }
    }
    return total;
}

  public String getIDNo(){

       return "EQ-" + idNo;
    }

  public int getIdNumber(){
      return idNo;
  }
  public int getTotalQty(){
      return totalQty;
  }

  public int getQty(){
      return qty;
  }

   public String getEquipmentName(){
        return eqName;
    }

    public String getCategory(){
        return category;
    }

     public String getSerialNo(){
        return serialNo;
    }

      public String getLocation(){
        return location;
    }

     public String getQTY(){
        return qty + " / " + totalQty;
    }

     public String getStatus(){
        return status;
    }
     
    public String getImagePath(){
        return imagePath;
    }
     
    public String getDescription(){
        return description;
    }
    
     public String getAccess(){
        return access;
    }
     public ImageIcon getIcon(){
         return icon;
     }

        

       
 }
     

     public static boolean findAdminID(int id){
         if (id == adminAccount) {
             return true;
         }
         return false;
     }

     class ImageTableCellRenderer extends JLabel implements TableCellRenderer {

        ImageTableCellRenderer(){
        this.setOpaque(true);
        this.setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        if (value instanceof ImageIcon) {
            ImageIcon icon = (ImageIcon) value; // typecasting
            setText("");
            setIcon(icon);
        } else {
            setIcon(null);
            setText("No image");
        }
        return this;
    }
  }
  public static class Request implements SavesLoad{
  static final String FILE_PATH = "C:\\Users\\ADMIN\\OneDrive\\Documents\\Project OOP\\Admin\\Requests\\requests.txt";
  static final String RULES_PATH = "C:\\Users\\ADMIN\\OneDrive\\Documents\\Project OOP\\Admin\\Requests\\rules.txt";
  static final String PROOF_FOLDER = "C:\\Users\\ADMIN\\OneDrive\\Documents\\Project OOP\\Request Proofs";
  static String[] userColumns = {"Request ID","Equipment","Qty","Date Requested","Status"};
  static int counter = 0;
  static int maxStudentItems = 3;
  static int maxTeacherItems = 5;
  static int maxDays = 7;
  static int penalty = 10; // pesos per late day
  static int maxStudentPerEquipment = 1;
  static int maxTeacherPerEquipment = 10;
  static int pickupDays = 2;
  static ArrayList<Request> requestList = new ArrayList<>(); 
  static int front = 0;
  static int rear = -1;
  private int requestId = 0;
  private int userId;
  private String role;
  private String equipmentId;
  private String equipmentName;
  private int quantity;
  private String proofText = "";
  private String proofImage = "";
  private String dateRequested;
  private String status = "Pending";
  private String comment = "";
  private String dateApproved = "";
  private String dateBorrowed = "";
  private String dateReturned = "";
  private String condition = "";
  private int penaltyAmount = 0;
  private double chargeAmount = 0;   // damaged / lost fee (PHP)
  private double amountPaid = 0;     // money the borrower handed to the admin (PHP)
  private boolean userNotified = false;
  static final java.time.format.DateTimeFormatter FORMAT = java.time.format.DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm a");  
 
  public void setDetails(int userId, String role, Equipment eq, int quantity, String proofText, String proofImage){
      counter++;
      this.requestId = counter;
      this.userId = userId;
      this.role = role;
      this.equipmentId = eq.getIDNo();
      this.equipmentName = eq.getEquipmentName();
      this.quantity = quantity;
      this.proofText = ManageFiles.clean(proofText);
      this.proofImage = proofImage;
      this.dateRequested = java.time.LocalDateTime.now().format(FORMAT);
      this.status = "Pending";
  }

  public void setSaveRequest(int requestId, int userId, String role, String equipmentId, String equipmentName, int quantity, String proofText, String proofImage, String dateRequested, String status, String comment){
      this.requestId = requestId;
      if (requestId > counter) {
          counter = requestId;
      }
      this.userId = userId;
      this.role = role;
      this.equipmentId = equipmentId;
      this.equipmentName = equipmentName;
      this.quantity = quantity;
      this.proofText = proofText;
      this.proofImage = proofImage;
      this.dateRequested = dateRequested;
      this.status = status;
      this.comment = comment;
  }
  
  public void setReturnInfo(String dateBorrowed, String dateReturned, String condition, int penaltyAmount){
    this.dateBorrowed = dateBorrowed;
    this.dateReturned = dateReturned;
    this.condition = condition;
    this.penaltyAmount = penaltyAmount;
}
  
  public static void saveFile(){
      ManageFiles.saveFile(ManageFiles.REQUEST, requestList);
  }

  public static void loadFile(){
      requestList.clear();
      counter = 0;
      for (String[] data : ManageFiles.loadFile(ManageFiles.REQUEST)) {
          if (data.length == 17 && data[11].isEmpty() && (data[16].equals("true") || data[16].equals("false"))) {
              
              String[] fixed = new String[16];
              for (int i = 0; i < 11; i++) {
                  fixed[i] = data[i];
              }
              for (int i = 12; i < 17; i++) {
                  fixed[i - 1] = data[i];
              }
              data = fixed;
          }
          if (data.length != 11 && data.length != 15 && data.length != 16 && data.length != 17 && data.length != 19) {
              continue;
          }
          try{
              Request get = new Request();
              get.setSaveRequest(Integer.parseInt(data[0]), Integer.parseInt(data[1]), data[2], data[3], data[4], Integer.parseInt(data[5]),
                      data[6], data[7], data[8], data[9], data[10]);
              if (data.length >= 15) {
                  get.setReturnInfo(data[11], data[12], data[13], Integer.parseInt(data[14]));
              }
              if (data.length >= 16) {
                  get.userNotified = Boolean.parseBoolean(data[15]);
              } else {
                  get.userNotified = !data[9].equals("Pending");
              }
              if (data.length >= 17) {
                  get.dateApproved = data[16];
              }
              if (data.length == 19) {
                  get.chargeAmount = Double.parseDouble(data[17]);
                  get.amountPaid = Double.parseDouble(data[18]);
              }
              requestList.add(get);
          }catch(NumberFormatException e){
              System.out.println("Skipped a bad request line");
          }
      }
      Comparator<Request> byRequestId = (a, b) -> Integer.compare(a.requestId, b.requestId);
      if (!SearchHelper.isSorted(requestList, byRequestId)) {
          SearchHelper.selectionSort(requestList, byRequestId);
      }
      refreshOverdue();
      refreshExpired();
      buildQueue();
  }

  public static void saveRules(){
    ArrayList<String> lines = new ArrayList<>();
    lines.add(ManageFiles.join(maxStudentItems, maxTeacherItems, maxDays, penalty,maxStudentPerEquipment, maxTeacherPerEquipment, pickupDays));
    ManageFiles.writeLines(ManageFiles.RULES, lines);
}

  public static void loadRules(){
    for (String[] data : ManageFiles.loadFile(ManageFiles.RULES)) {
        if (data.length >= 4) {
            try{
                maxStudentItems = Integer.parseInt(data[0]);
                maxTeacherItems = Integer.parseInt(data[1]);
                maxDays = Integer.parseInt(data[2]);
                penalty = Integer.parseInt(data[3]);
                if (data.length >= 7) {
                    maxStudentPerEquipment = Integer.parseInt(data[4]);
                    maxTeacherPerEquipment = Integer.parseInt(data[5]);
                    pickupDays = Integer.parseInt(data[6]);
                }
            }catch(NumberFormatException e){
                System.out.println("Cannot read rules, using defaults");
            }
        }
        return;   
    }
}

  public boolean isActive(){
      return status.equals("Pending") || status.equals("Approved") || status.equals("Borrowed") || status.equals("Overdue");
  }
  
  // Binary Search for DSA
  public static Request findById(String requestNo){
    int target = SearchHelper.parseId(requestNo, "REQ-");
    if (target == -1) {
        return null;
    }
    int[] ids = new int[requestList.size()];
    for (int i = 0; i < ids.length; i++) {
        ids[i] = requestList.get(i).requestId;
    }
    int index = SearchHelper.binarySearch(ids, 0, ids.length - 1, target);
    if (index == -1) {
        return null;
    }
    return requestList.get(index);
}

  public static String copyProof(String sourcePath){
      return ManageFiles.copyFile(sourcePath, ManageFiles.PROOF_FOLDER, "PROOF_" + System.currentTimeMillis());
  }

  // returns null when it worked, otherwise the message to show
  public static String createRequest(int userId, String role, String equipmentId, int amount, String proofText, String proofImage){
      loadRules();
      Equipment.loadFile();   // fresh quantities
      loadFile();             // fresh requests
      Equipment eq = Equipment.findById(equipmentId);
      if (eq == null) {
          return "This equipment no longer exists";
      }
      if (amount <= 0) {
          return "Quantity must be greater than 0";
      }
      if (eq.getStatus().equals("Under Repair")) {
          return "This equipment is under repair";
      }
      if (eq.getQty() <= 0) {
          return "This equipment is out of stock";
      }
      if (amount > eq.getQty()) {
          return "Only " + eq.getQty() + " left in stock";
      }
      if (role.equals(Student.role) && eq.getAccess().equals("Teachers Only")) {
          return "This equipment is for teachers only";
      }
      int maxPerItem = getPerItemLimit(role);
      int haveOfItem = countActiveForEquipment(userId, role, equipmentId);
            if (haveOfItem + amount > maxPerItem) {
                    return "You can only have " + maxPerItem + " of this item at a time\n(you already have " + haveOfItem + ")";
        }
      int limit = getDifferentItemLimit(role);  
      if (haveOfItem == 0 && countActiveEquipment(userId, role) >= limit) {
            return "You can only borrow " + limit + " different items at a time";
      }
      if (proofText.trim().isEmpty() && proofImage.isEmpty()) {
          return "Send a proof first (text, image or both)";
      }

      eq.minusQtyEquipment(amount);
      eq.refreshStatus();
      Equipment.saveFile();

      Request get = new Request();
      get.setDetails(userId, role, eq, amount, proofText, copyProof(proofImage));
      requestList.add(get);
      RequestQueue.enqueue(get.requestId);
      saveFile();
      return null;
  }

  public static String approve(String requestNo, String comment){
      loadFile();
      Request r = findById(requestNo);
      if (r == null) {
          return "Request was not found";
      }
      if (!r.status.equals("Pending")) {
          return "This request is already " + r.status;
      }
      r.status = "Approved";
      r.dateApproved = java.time.LocalDateTime.now().format(FORMAT);
      leaveQueue(r);
      r.userNotified = false;
      r.comment = comment;
      saveFile();
      return null;
  }
  
  public static void refreshExpired(){
    boolean changed = false;
    for (Request r : requestList) {
        if (!r.status.equals("Approved") || r.dateApproved.isEmpty()) {
            continue;
        }
        try{
            java.time.LocalDate deadline = java.time.LocalDateTime.parse(r.dateApproved, FORMAT).toLocalDate().plusDays(pickupDays);
            if (java.time.LocalDate.now().isAfter(deadline)) {
                r.status = "Expired";
                r.comment = "Not picked up in time";
                giveBack(r);
                changed = true;
            }
        }catch(java.time.format.DateTimeParseException e){
            System.out.println("Bad approved date in " + r.getRequestNo());
        }
    }
    if (changed) {
        saveFile();
    }
}
  
  public static ArrayList<Request> getApprovedToNotify(int userId, String role){
            loadFile();
            ArrayList<Request> results = new ArrayList<>();
                for (Request r : requestList) {
                        if (r.userId == userId && r.role.equals(role) && r.status.equals("Approved") && !r.userNotified) {
                        results.add(r);
                    }
                }
            return results;
}

public static void markNotified(ArrayList<Request> list){
    loadFile();   // fresh objects, so look each one up again
    for (Request seen : list) {
        Request r = findById(seen.getRequestNo());
        if (r != null) {
            r.userNotified = true;
        }
    }
    saveFile();
}

  public static String reject(String requestNo, String comment){
      loadFile();
      Request r = findById(requestNo);
      if (r == null) {
          return "Request was not found";
      }
      if (!r.status.equals("Pending")) {
          return "This request is already " + r.status;
      }
      r.status = "Rejected";
      r.comment = comment;
      leaveQueue(r);
      giveBack(r);
      saveFile();
      return null;
  }

  public static String delete(String requestNo){
    loadFile();
    Request r = findById(requestNo);
    if (r == null) {
        return "Request was not found";
    }
    if (r.isActive()) {
        return "Pending, approved or borrowed requests cannot be deleted";
    }
    requestList.remove(r);
    saveFile();
    return null;
}
  
  public static String markReturned(String requestNo){
      loadFile();
      Request r = findById(requestNo);
      if (r == null) {
          return "Request was not found";
      }
      if (!r.isActive() || r.status.equals("Pending")) {
          return "Only approved or borrowed requests can be returned";
      }
      r.status = "Returned";
      giveBack(r);
      saveFile();
      return null;
  }
// puts the units back in stock
  private static void giveBack(Request r){
      Equipment.loadFile();
      Equipment eq = Equipment.findById(r.equipmentId);
      if (eq != null) {
          eq.setQty(Math.min(eq.getTotalQty(), eq.getQty() + r.quantity));
          eq.refreshStatus();
          Equipment.saveFile();
      }
  }
  // fills the "my requests" JTable of the student or teacher in the userDisplay
  public static void loadUserTable(DefaultTableModel model, int userId, String role){
      loadFile();
      model.setRowCount(0);
      for (int i = requestList.size() - 1; i >= 0; i--) {   // newest first
          Request r = requestList.get(i);
          if (r.userId == userId && r.role.equals(role)) {
              model.addRow(new Object[]{r.getRequestNo(), r.equipmentName, r.quantity, r.dateRequested, r.status});
          }
      }
  }
  // the dialog the user sees after clicking an equipment
  public static boolean openForm(Component parent, String equipmentId, int userId, String role, int startAmount){
      Equipment.loadFile();
      Equipment eq = Equipment.findById(equipmentId);
      if (eq == null) {
          JOptionPane.showMessageDialog(parent, "This equipment no longer exists");
          return false;
      }
      if (eq.getQty() <= 0) {
          JOptionPane.showMessageDialog(parent, eq.getEquipmentName() + " is out of stock", "Unavailable", JOptionPane.WARNING_MESSAGE);
          return false;
      }
      if (!showRules(parent, true)) return false;   // user must read the rules first
      JTextField amountField = new JTextField(String.valueOf(startAmount));
      JTextArea proofArea = new JTextArea();
      proofArea.setLineWrap(true);
      proofArea.setWrapStyleWord(true);
      JScrollPane proofScroll = new JScrollPane(proofArea);
      proofScroll.setPreferredSize(new Dimension(50, 80));
      String[] proofPath = {""};   // array so the button can change it
      JButton chooseProof = new JButton("Choose Image");
      chooseProof.setFocusable(false);
      chooseProof.setBackground(Color.decode("#2B59C3"));
      chooseProof.setForeground(Color.WHITE);
      chooseProof.addActionListener(ev -> {
          JFileChooser chooser = new JFileChooser();
          chooser.setFileFilter(new FileNameExtensionFilter("Images (png,jpg,jpeg)","png","jpg","jpeg"));
          if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
              proofPath[0] = chooser.getSelectedFile().getAbsolutePath();
              chooseProof.setText(chooser.getSelectedFile().getName());
          }
      });

      JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
      panel.add(new JLabel("Equipment:"));       panel.add(new JLabel(eq.getEquipmentName()));
      panel.add(new JLabel("Available:"));       panel.add(new JLabel(String.valueOf(eq.getQty())));
      panel.add(new JLabel("Quantity:"));        panel.add(amountField);
      panel.add(new JLabel("Why you need it:")); panel.add(proofScroll);
      panel.add(new JLabel("Proof image:"));     panel.add(chooseProof);

      int choice = JOptionPane.showConfirmDialog(parent, panel, "Request Equipment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
      if (choice != JOptionPane.OK_OPTION) return false;

      int amount;
      try{
          amount = Integer.parseInt(amountField.getText().trim());
      }catch(NumberFormatException ex){
          JOptionPane.showMessageDialog(parent, "Quantity must be a valid number", "Warning", JOptionPane.WARNING_MESSAGE);
          return false;
      }
      String error = createRequest(userId, role, equipmentId, amount, proofArea.getText().trim(), proofPath[0]);
      if (error != null) {
          JOptionPane.showMessageDialog(parent, error, "Cannot request", JOptionPane.WARNING_MESSAGE);
          return false;
      }
      JOptionPane.showMessageDialog(parent, "Request sent!\nStatus: Pending");
      return true;
  }
  
  public static String markBorrowed(String requestNo){
    loadFile();
    Request r = findById(requestNo);
    if (r == null) {
        return "Request was not found";
    }
    if (!r.status.equals("Approved")) {
        return "Only approved requests can be released";
    }
    r.status = "Borrowed";
    r.dateBorrowed = java.time.LocalDateTime.now().format(FORMAT);
    saveFile();
    return null;
}
 
  public long getDaysLate(){
    if (dateBorrowed.isEmpty()) {
        return 0;
    }
    java.time.LocalDate due = java.time.LocalDateTime.parse(dateBorrowed, FORMAT).toLocalDate().plusDays(maxDays);
    long late = java.time.temporal.ChronoUnit.DAYS.between(due, java.time.LocalDate.now());
    return Math.max(0, late);
}

  public String getDueDate(){
    if (dateBorrowed.isEmpty()) {
        return "-";
    }
    return java.time.LocalDateTime.parse(dateBorrowed, FORMAT).toLocalDate().plusDays(maxDays).toString();
}
  
  public static String markReturned(String requestNo, String condition, String remarks){
    return markReturned(requestNo, condition, remarks, 0, 0);
}

  // unitCost = PHP cost of one unit (entered by the admin), amountPaid = cash received from the borrower
  public static String markReturned(String requestNo, String condition, String remarks, double unitCost, double amountPaid){
    loadFile();
    Request r = findById(requestNo);
    if (r == null) {
        return "Request was not found";
    }
    if (!r.status.equals("Borrowed") && !r.status.equals("Overdue")) {
        return "Only borrowed items can be returned";
    }
    double charge = calcCharge(condition, unitCost, r.quantity);
    if (!condition.equals("Good")) {
        if (unitCost <= 0) {
            return "Enter the cost of the item first";
        }
        if (amountPaid + 0.001 < charge) {
            return "The borrower must pay " + peso(charge) + " before the item can be returned";
        }
    }
    r.penaltyAmount = (int) r.getDaysLate() * penalty;
    r.condition = condition;
    r.chargeAmount = charge;
    r.amountPaid = charge > 0 ? amountPaid : 0;
    String note = ManageFiles.clean(remarks);
    if (charge > 0) {
        note = (note + " [" + condition + ": item cost " + peso(unitCost) + "/unit, charge " + peso(charge) + ", paid " + peso(amountPaid) + "]").trim();
    }
    r.comment = note;
    r.dateReturned = java.time.LocalDateTime.now().format(FORMAT);
    r.status = "Returned";

    if (condition.equals("Good")) {
        giveBack(r);
    } else {
        Equipment.loadFile();
        Equipment eq = Equipment.findById(r.equipmentId);
        if (eq != null) {
            eq.setTotalQty(Math.max(0, eq.getTotalQty() - r.quantity));
            eq.refreshStatus();
            Equipment.saveFile();
        }
    }
    saveFile();
    return null;
}
  
  public static int countActiveForEquipment(int userId, String role, String equipmentId){
    int total = 0;
    for (Request r : requestList) {
        if (r.userId == userId && r.role.equals(role) && r.equipmentId.equals(equipmentId) && r.isActive()) {
            total += r.quantity;
        }
    }
    return total;
}
  
  public static int countByStatus(String status){
      int total = 0;
      for (Request r : requestList) {
          if (r.status.equals(status)) {
              total++;
          }
      }
      return total;
  }
  
  public static void refreshOverdue(){
      loadRules();   // maxDays must be the saved value, not the default 7
      boolean changed = false;
      for (Request r : requestList) {
          if (!r.status.equals("Borrowed")) {
              continue;
          }
          try{
              if (r.getDaysLate() > 0) {
                  r.status = "Overdue";
                  changed = true;
              }
          }catch(java.time.format.DateTimeParseException e){
              System.out.println("Bad borrowed date in " + r.getRequestNo());
          }
      }
      if (changed) {
          saveFile();
      }
  }
  
  public static void buildQueue(){
    RequestQueue.clear();
    for (Request r : requestList) {
        if (r.status.equals("Pending")) {
            RequestQueue.enqueue(r.requestId);
        }
    }
}

  public static Request getNextInQueue(){
    if (RequestQueue.isEmpty()) {
        return null;
    }
    return findById("REQ-" + RequestQueue.peek());
}
  // the request leaves the line when it is approved or rejected
  private static void leaveQueue(Request r){
    if (!RequestQueue.isEmpty() && RequestQueue.peek() == r.requestId) {
        RequestQueue.dequeue();
    }
}
    
  public static int getPerItemLimit(String role){
      //                               if role == maxTeacher else maxStudent
      return role.equals(Teacher.role) ? maxTeacherPerEquipment: maxStudentPerEquipment;
  }  
                                        // If role == maxTeacher else maxStudent
  public static int getDifferentItemLimit(String role){
      return role.equals(Teacher.role) ? maxTeacherItems : maxStudentItems;
  }
  
  public static int countActiveEquipment(int userId, String role){
      ArrayList<String> ids = new ArrayList<>();
      for (Request r: requestList) {
          if (r.userId == userId && r.role.equals(role) && r.isActive() && !ids.contains(r.equipmentId)) {
              ids.add(r.equipmentId);
          }
      }
      return ids.size();
  }
  public String getRequestNo(){ return "REQ-" + requestId; }
  public int getUserId(){ return userId; }
  public String getRole(){ return role; }
  public String getEquipmentId(){ return equipmentId; }
  public String getEquipmentName(){ return equipmentName; }
  public int getQuantity(){ return quantity; }
  public String getProofText(){ return proofText; }
  public String getProofImage(){ return proofImage; }
  public String getDateRequested(){ 
      return dateRequested;
  
  }
  
  public double getChargeAmount(){ return chargeAmount; }
  public double getAmountPaid(){ return amountPaid; }
  public String getCondition(){ return condition; }

  public static String peso(double amount){
      return "PHP " + String.format("%,.2f", amount);
  }

  // Lost = the full cost, Damaged = half of the cost, Good = nothing
  public static double calcCharge(String condition, double unitCost, int quantity){
      if (condition.equals("Lost")) {
          return unitCost * quantity;
      }
      if (condition.equals("Damaged")) {
          return unitCost * quantity * 0.5;
      }
      return 0;
  }

  // -1 means the text is not a valid amount
  public static double parseMoney(String text){
      try{
          double value = Double.parseDouble(text.trim().replace(",", ""));
          if (Double.isNaN(value) || Double.isInfinite(value) || value < 0) {
              return -1;
          }
          return Math.round(value * 100) / 100.0;
      }catch(NumberFormatException e){
          return -1;
      }
  }

  // the rules the borrower must read before sending a request (uses the saved admin limits)
  public static String getRulesText(){
      loadRules();
      return "BORROWING RULES AND PROCESS\n"
          + "==========================================\n\n"
          + "HOW THE SYSTEM WORKS\n"
          + "1. Browse the equipment and send a request (state why you need it).\n"
          + "2. The admin reviews it. Your request is Approved or Rejected.\n"
          + "3. If approved, claim the equipment at the office within " + pickupDays + " day(s)\n"
          + "    and show your receipt. Unclaimed requests Expire and the stock is restored.\n"
          + "4. Once released, the status is Borrowed. Use it with care.\n"
          + "5. Return it to the admin on or before the due date.\n"
          + "6. The admin checks the condition (Good, Damaged or Lost) and marks it Returned.\n\n"
          + "BORROWING LIMITS\n"
          + "- Students: up to " + maxStudentItems + " different equipment, " + maxStudentPerEquipment + " unit per equipment.\n"
          + "- Teachers: up to " + maxTeacherItems + " different equipment, " + maxTeacherPerEquipment + " units per equipment.\n"
          + "- Borrowing period: " + maxDays + " day(s).\n\n"
          + "LATE RETURN\n"
          + "- A penalty of " + peso(penalty) + " per day late.\n\n"
          + "DAMAGED EQUIPMENT\n"
          + "- You pay HALF (50%) of the item cost, per unit damaged.\n"
          + "- The admin sets the item cost and collects the payment when you return it.\n\n"
          + "LOST EQUIPMENT\n"
          + "- You pay the FULL (100%) cost of the item, per unit lost.\n"
          + "- The admin sets the item cost and collects the payment when you return it.\n\n"
          + "PAYMENT\n"
          + "- Payment is handed to the admin in cash at the office.\n"
          + "- The same rules apply to students and teachers.\n"
          + "- The return is only completed once the full amount is paid.\n";
  }

  // shows the rules, returns true when the user wants to continue
  public static boolean showRules(Component parent, boolean askToContinue){
      JTextArea area = new JTextArea(getRulesText());
      area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
      area.setEditable(false);
      area.setCaretPosition(0);
      JScrollPane scroll = new JScrollPane(area);
      scroll.setPreferredSize(new Dimension(520, 380));
      if (!askToContinue) {
          JOptionPane.showMessageDialog(parent, scroll, "Borrowing Rules", JOptionPane.INFORMATION_MESSAGE);
          return true;
      }
      Object[] options = {"I understand, continue", "Cancel"};
      int i = JOptionPane.showOptionDialog(parent, scroll, "Borrowing Rules", JOptionPane.YES_NO_OPTION,
              JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
      return i == 0;
  }

  public String getPickupDeadLine(){
      if (dateApproved.isEmpty()) {
          return "-";          
      }
      return java.time.LocalDateTime.parse(dateApproved, FORMAT).toLocalDate().plusDays(pickupDays).toString();
  }
  
  public String getDateApproved(){
      if (dateApproved.isEmpty()) {
          return "-";
      }else{
          return dateApproved;
      }
  }
  
  public String getStatus(){ return status; }
  public String getComment(){ return comment; }

        @Override
        public String toLine(){
            return ManageFiles.join(requestId, userId, role, equipmentId, ManageFiles.clean(equipmentName), quantity,
              ManageFiles.clean(proofText), proofImage, dateRequested, status, ManageFiles.clean(comment),
              dateBorrowed, dateReturned, condition, penaltyAmount, userNotified, dateApproved, chargeAmount, amountPaid);
  }

        @Override
        public Object[] toRow(){
      return new Object[]{getRequestNo(), userId, role, equipmentName, quantity, dateRequested, status};
  }
    }
}
