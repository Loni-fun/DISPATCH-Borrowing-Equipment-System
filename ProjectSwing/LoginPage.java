/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ProjectSwing;

/**
 *
 * @author ADMIN
 */


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

public class LoginPage extends JPanel implements ActionListener, MouseListener, FocusListener, KeyListener, showLoginPage{
    JFrame frame; // the window that Main gives us in showLogin
    CardLayout layout = new CardLayout();
    JPanel Panel = new JPanel(layout);
    JPanel login = new JPanel();
    JPanel signup = new JPanel();
    JLabel tRegister;
    JLabel forgotPass;
    JTextField typeId;
    JPasswordField typePass;
    JButton pLogin;
    JLabel backToLogin;
    JTextField FName;
    JTextField LName;
    JTextField MName;
    JTextField IDInput;
    JPasswordField pass;
    JPasswordField conPass;
    JButton bRegister;
    

   public LoginPage(){   
        Panel.add(login,"login");
        CreateLoginPanel();
        Panel.add(signup,"Sign Up");
        CreateRegisterPanel();
        this.setLayout(new BorderLayout());
        this.add(Panel, BorderLayout.CENTER); // CENTER makes Panel fill the whole window
    }
    
    @Override
    public void showLogin(JFrame frame) {
        this.frame = frame;
        frame.setSize(1000,650);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false); 
        frame.add(this);     
        frame.setVisible(true); 
    }
    
    public void CreateLoginPanel(){      
        login.setLayout(null);
        JLabel title = new JLabel("DISPATCH");
        JPanel sidePanel = new JPanel();
        JLabel t1 = new JLabel("<html><span style= 'font-family: Segoe UI; color: red;'>*</span> <span style = 'font-family: Segoe UI; font-size: 10px; font-weight:normal;'>USER ID</span></html>");
        typeId = new JTextField("Enter ID Number");      
        JLabel t2 = new JLabel("<html><span style= 'font-family: Segoe UI; color: red;'>*</span> <span style = 'font-family: Segoe UI; font-size: 10px; font-weight:normal;'>PASSWORD</span></html>");
        typePass = new JPasswordField();
        pLogin = new JButton("SIGN IN");
        JLabel rDesc = new JLabel("Don't have an account?");
        tRegister = new JLabel("Sign Up");
        forgotPass = new JLabel("<html><u>Forgot password?</u></html>");
        
        forgotPass.addMouseListener(this);
        //Set Header
        setFont(title);
        title.setBounds(180, 30, 150, 40);
        
        //User ID
        t1.setBounds(100, 190, 100, 10);
        typeId.setBounds(100, 210, 300, 40);      
        typeId.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.decode("#1A1A1A")),BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        typeId.addFocusListener(this); // so the window does not focus it (and clear the placeholder) when it opens
        typeId.addMouseListener(this);
        //Password
        t2.setBounds(100, 280, 100, 30);
        typePass.setBounds(100, 320, 300, 40);
        typePass.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.decode("#1A1A1A")),BorderFactory.createEmptyBorder(4, 10, 4, 10)));      
        forgotPass.setBounds(100, 365, 150, 20);
        forgotPass.setForeground(Color.decode("#1269cc"));
        forgotPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        //Login Button
        pLogin.setBounds(100, 430, 300, 40);  
        setButton(pLogin);
        pLogin.addActionListener(this);
        
        // below the button
        rDesc.setBounds(140, 500, 150, 30);  
        tRegister.setBounds(300, 500, 100, 30);
        tRegister.setForeground(Color.decode("#1269cc"));
        tRegister.addMouseListener(this);
        rDesc.setFont(new Font("Segoe Ui", Font.PLAIN,12));
        setFont(tRegister, typeId);
        
        //Side Panel
        JLabel aboutT = new JLabel("BORROW NOW");
        JLabel desc = new JLabel("Request, track, and return school equipment, all in one place");
        sidePanel.setLayout(null);
        sidePanel.setBounds(500, 0, 500, 700);
        setMediumFont(aboutT); 
        aboutT.setBounds(150, 230, 250, 40);
        desc.setBounds(60, 280, 400, 30);
        desc.setFont(new Font("Segoe UI", Font.ITALIC | Font.BOLD,13));
        desc.setForeground(Color.decode("#FFFFFF"));
        
        aboutT.setForeground(Color.decode("#FFFFFF"));
        sidePanel.setBackground(Color.decode("#2B59C3"));
        
        sidePanel.add(aboutT);
        sidePanel.add(desc);
        
        //adding to the LoginPanel      
        addPanelComponents(login, title,t1,typeId,t2,typePass,forgotPass,pLogin,tRegister,rDesc,sidePanel);     
    }
    
     // error handling
    public void setTextPassFieldCondition(JTextField userID, JPasswordField userPass){
        String getPass = new String(userPass.getPassword());
        String getUser = realText(userID,"Enter ID Number");
        if (getPass.isEmpty() && getUser.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No username and password detected","Warning",JOptionPane.WARNING_MESSAGE);
            return;
        }else if(getUser.isEmpty()){
            JOptionPane.showMessageDialog(null, "User ID not detected","Warning",JOptionPane.WARNING_MESSAGE);
            return;
        }
        int convert;  
        
        try{
        convert = Integer.parseInt(getUser);   
        }catch(NumberFormatException e){
         JOptionPane.showMessageDialog(null, "ID must be a number","Error",JOptionPane.ERROR_MESSAGE);  
         return;
        }
        
        Student.loadStudentInfo();
        Teacher.loadTeacherInfo();
        Student s = Student.findIDNumber(convert);
        Teacher t = Teacher.findIDNumber(convert);

        if (s == null && t == null ) {
            if (AdminDisplay.findAdminID(convert)) {
                if (!AdminDisplay.checkAdminPassword(getPass)) {
                    JOptionPane.showMessageDialog(null, "Incorrect Password");
                    return;
                }
               JOptionPane.showMessageDialog(null, "Welcome Back, Admin");
               frame.setVisible(false);
               AdminDisplay get = new AdminDisplay();
               get.setVisible(true);
               return;
            }
            JOptionPane.showMessageDialog(null, "This ID number is not registered within the system");   
        }else if(s != null){
            login(s, getPass);
        }else{
            login(t, getPass);
        }
    }
    
    
    public void login(Account a, String typed){
    String role = a.getRole();
    if (!a.hasAccount()) {
        JOptionPane.showMessageDialog(null, "Valid " + role + " ID\nTo log in, create an account first");
    }else if(a.isBlocked()){
        JOptionPane.showMessageDialog(null, "Your account is currently blocked!","Blocked",JOptionPane.WARNING_MESSAGE);
    }else if(!a.passwordMatches(typed)){
        a.recordFailedLogin();
        if (a.isBlocked()) {
            JOptionPane.showMessageDialog(null, "Too many failed attempts\nYour account has been blocked","Blocked",JOptionPane.WARNING_MESSAGE);
        }else{
            int left = BlockedUsers.MAX_TRIES - a.getFailedTries();
            JOptionPane.showMessageDialog(null, "Incorrect Password\n" + left + " tries left");
        }
    }else{
        a.resetTries();
        if (a.isMustChange() && !PasswordReset.forceChange(frame, a.getIdNumber(), role)) {
            return;   // cancelled, the reset code stays valid
        }
        JOptionPane.showMessageDialog(null, "Welcome " + role + " :" + a.getIdNumber());
        openUserDisplay(role, a.getIdNumber());
    }
}
    
    public void openUserDisplay(String role, int id){
    Student.loadStudentInfo();   
    Teacher.loadTeacherInfo();
    UserDisplay.currentStudent = null;
    UserDisplay.currentTeacher = null;
        if (role.equals(Student.role)) {
            UserDisplay.currentStudent = Student.findIDNumber(id);
        }else{
            UserDisplay.currentTeacher = Teacher.findIDNumber(id);
        }
   
    frame.setVisible(false);
    UserDisplay get = new UserDisplay();
    get.setVisible(true);
}
    
    public String realText(JTextField field, String text){
        String Text = field.getText().trim();
        if (Text.equals(text)) {
            return "";
        }
        return Text;
    }
    
    public void RegisterUser(){
        String id = realText(IDInput,"Enter User ID");
        String lastName = realText(LName,"Enter Last Name");
        String firstName = realText(FName,"Enter First Name");
        String middleName = realText(MName,"Enter Middle Name");
        String password = new String(pass.getPassword()).trim();
        String confirmPassword = new String(conPass.getPassword()).trim();
        if (id.isEmpty() || lastName.isEmpty() || firstName.isEmpty() || middleName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all the fields.","Warning",JOptionPane.WARNING_MESSAGE);
            return;
        }
        int convert;
        try{
            convert = Integer.parseInt(id);
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Invalid ID Input","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }  
        Student.loadStudentInfo();
        Teacher.loadTeacherInfo();
        boolean tF = false;
        if (Student.findIDNumber(convert) != null) {
            tF = Student.registerAccount(id, lastName, firstName, middleName, password, confirmPassword);
        }else if(Teacher.findIDNumber(convert) != null){
            tF = Teacher.registerAccount(id, lastName, firstName, middleName, password, confirmPassword);
        }else{
            JOptionPane.showMessageDialog(null, "This ID number is not registered within the system","Warning",JOptionPane.WARNING_MESSAGE);
        }
        
        if (tF) {
            JOptionPane.showMessageDialog(null, "Account Created!\nYou can now login into the system","Sucess",JOptionPane.PLAIN_MESSAGE);   
            layout.show(Panel, "login");
        }
    }
    
    public void setFont(JLabel text){
        // Bold Font
        text.setFont(new Font("Segoe UI", Font.BOLD,20));
    }
     
    public void setFont(JLabel text, JTextField text2){
        //Normal Font - Login Page
        text.setFont(new Font("Segoe UI", Font.PLAIN,14));
        text2.setFont(new Font("Segoe UI", Font.PLAIN,14));
    }
    
    public void setFont(JLabel text, JLabel text2){
        //Side Panel - Register Panel
        setMediumFont(text);
        text2.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC,12));    
        text.setForeground(Color.WHITE);
        text2.setForeground(Color.WHITE);
        
    }
    
    public void setFont(JLabel text, JPasswordField field){
        //Normal Font - Login Page
        text.setFont(new Font("Segoe UI", Font.PLAIN,14));
        field.setFont(new Font("Segoe UI", Font.PLAIN,14));
    }
    
    public void setMediumFont(JLabel text){
        try{
          Font medium = Font.createFont(Font.TRUETYPE_FONT, new File("Font Styles\\w6.ttf")).deriveFont(Font.BOLD,25);
          GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
          ge.registerFont(medium);
          text.setFont(medium);         
        }catch(IOException | FontFormatException e){                
            e.printStackTrace();       
        }
    }
    
    public void CreateRegisterPanel(){
        //Initialize var.
        signup.setBackground(Color.decode("#E2E8F0"));
        signup.setLayout(null);
        JLabel title = new JLabel("Sign Up");
        JLabel text2 = new JLabel("<html><span style= 'font-family: Segoe UI; color: red;'>*</span> <span style = 'font-family: Segoe UI; font-size: 11px; font-weight:normal;'>Last Name</span></html>");
        LName = new JTextField("Enter Last Name");
        LName.addKeyListener(this);
        JLabel text3 = new JLabel("<html><span style= 'font-family: Segoe UI; color: red;'>*</span> <span style = 'font-family: Segoe UI; font-size: 11px; font-weight:normal;'>First Name</span></html>");
        FName = new JTextField("Enter First Name");
        FName.addKeyListener(this);
        JLabel text4 = new JLabel("<html><span style= 'font-family: Segoe UI; color: red;'>*</span> <span style = 'font-family: Segoe UI; font-size: 11px; font-weight:normal;'>Middle Name</span></html>");
        MName = new JTextField("Enter Middle Name");  
        MName.addKeyListener(this);
        JLabel text5 = new JLabel("<html><span style= 'font-family: Segoe UI; color: red;'>*</span> <span style = 'font-family: Segoe UI; font-size: 11px; font-weight:normal;'>User ID</span></html>");
        IDInput = new JTextField("Enter User ID");
        JLabel text6 = new JLabel("<html><span style= 'font-family: Segoe UI; color: red;'>*</span> <span style = 'font-family: Segoe UI; font-size: 11px; font-weight:normal;'>Password</span></html>");
        pass = new JPasswordField();
        JLabel text7 = new JLabel("<html><span style= 'font-family: Segoe UI; color: red;'>*</span> <span style = 'font-family: Segoe UI; font-size: 11px; font-weight:normal;'>Confirm Password</span></html>");
        conPass = new JPasswordField();
        JLabel text8 = new JLabel("Already have an account?");
        JLabel text9 = new JLabel("REGISTER NOW");
        JLabel text10 = new JLabel("Request, track, and return school equipment, all in one place");     
        JLabel logoText = new JLabel("DISPATCH");
        backToLogin = new JLabel("<html><u>Sign In</u></html>");     
        bRegister = new JButton("Register");
        bRegister.addActionListener(this);
        
        setFont(text9,text10);
        UserDisplay.setMediumFont(logoText);
        MName.addFocusListener(this);
        FName.addFocusListener(this);
        LName.addFocusListener(this);
        IDInput.addFocusListener(this);
       
        //Side Panel
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(null);
        sidePanel.setBackground(Color.decode("#2B59C3"));   
        text8.setForeground(Color.WHITE);
        backToLogin.setForeground(Color.WHITE);
        backToLogin.setFont(new Font("Segoe UI",Font.PLAIN,12));
        logoText.setForeground(Color.WHITE);
        backToLogin.addMouseListener(this);
        //Locations
        title.setBounds(560, 50, 380, 30);
        title.setHorizontalAlignment(JLabel.CENTER);
        sidePanel.setBounds(0, 0, 500, 650);
        logoText.setBounds(40, 20, 150, 30);
        text8.setBounds(165, 460, 200, 30);
        text9.setBounds(130, 250, 250, 30);
        text10.setBounds(70, 290, 400, 30);
        backToLogin.setBounds(220, 500, 100, 30);
        text2.setBounds(560, 120, 100, 20);
        LName.setBounds(560, 142, 380, 35);
        text3.setBounds(560, 200, 180, 15);
        FName.setBounds(560, 222, 180, 35);
        text4.setBounds(760, 200, 180, 15);
        MName.setBounds(760, 222, 180, 35);
        text5.setBounds(560, 280, 100, 15);
        IDInput.setBounds(560, 302, 380, 35);

        text6.setBounds(560, 360, 150, 15);
        pass.setBounds(560, 382, 380, 35);

        text7.setBounds(560, 440, 150, 15);
        conPass.setBounds(560, 462, 380, 35);
        bRegister.setBounds(560, 530, 380, 40);
       
        // Set Texts
        setFont(title);
        setFont(text2, LName);        
        setFont(text3, FName);        
        setFont(text4, MName);        
        setFont(text5, IDInput);        
        setFont(text6, pass);        
        setFont(text7, conPass);        
        IDInput.setFont(new Font("Segoe UI",Font.PLAIN,14));
        
        //Edit JTextField 
        LName.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.decode("#1A1A1A")),BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        FName.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.decode("#1A1A1A")),BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        MName.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.decode("#1A1A1A")),BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        IDInput.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.decode("#1A1A1A")),BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        pass.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.decode("#1A1A1A")), BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        conPass.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.decode("#1A1A1A")),BorderFactory.createEmptyBorder(4, 10, 4, 10)));     
        //Edit Button
        setButton(bRegister);
        //Add components
        addPanelComponents(signup, sidePanel,title,text2,LName,text3,FName,text4,MName,text5,IDInput,text6,pass,text7,conPass,bRegister);   
        addPanelComponents(sidePanel,logoText, text8,text9,text10, backToLogin); 
 }
    
    public void addPanelComponents(JPanel panel, JComponent... components){
        for (JComponent comp : components) {
            panel.add(comp);
        }
    }
    public JPanel displayForgotPassword(){
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setPreferredSize(new Dimension(500,500));
        panel.setBackground(Color.red);
        return panel;
    }
   
    
    public void setButton(JButton button){
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.decode("#1A1A1A")),BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        button.setFocusable(false);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.decode("#2B59C3"));             
        button.setFont(new Font("Segoe UI",Font.BOLD,14));
    }
    
     @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("SIGN IN")){
            setTextPassFieldCondition(typeId,  typePass);
                  
        }else if(e.getSource().equals(bRegister)){
            RegisterUser();
        }
    
}
    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getSource().equals(tRegister)){
            layout.show(Panel, "Sign Up");      
        }else if(e.getSource().equals(backToLogin)){
            layout.show(Panel, "login");
            typeId.setText("Enter ID Number");    
        }else if(e.getSource().equals(typeId)){
            typeId.requestFocusInWindow(); 
        }else if(e.getSource().equals(forgotPass)){
            PasswordReset.showRequestInstructions(this);
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {
      //  throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getSource().equals(tRegister)) {
            tRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));  
        }else if(e.getSource().equals(backToLogin)){
            backToLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }else if(e.getSource().equals(forgotPass)){
            forgotPass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }
    @Override
    public void mouseExited(MouseEvent e) {
       // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void focusGained(FocusEvent e) {
        Object src = e.getSource();
        if (src.equals(typeId) && typeId.getText().equals("Enter ID Number")) {
            typeId.setText("");
        }else if (src.equals(LName) && LName.getText().equals("Enter Last Name")) {
            LName.setText("");
        }else if (src.equals(FName) && FName.getText().equals("Enter First Name")) {
            FName.setText("");
        }else if (src.equals(MName) && MName.getText().equals("Enter Middle Name") ) {
            MName.setText("");
        }else if (src.equals(IDInput) && IDInput.getText().equals("Enter User ID")) {
            IDInput.setText("");
        }
   
    }

    // every box is checked on its own, so each empty box gets its placeholder back
    @Override
    public void focusLost(FocusEvent e) {
        if (typeId.getText().isEmpty()) {
            typeId.setText("Enter ID Number");
        } 
        if (LName.getText().isEmpty()) {
            LName.setText("Enter Last Name");
        }
        if (FName.getText().isEmpty()) {
            FName.setText("Enter First Name");
        }
        if (MName.getText().isEmpty()) {        
            MName.setText("Enter Middle Name");
        }
        if (IDInput.getText().isEmpty()) {
            IDInput.setText("Enter User ID");
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (!Character.isLetter(c) && c != KeyEvent.VK_SPACE) {
            e.consume(); // consume or get rid of the input if number in lname, fname,Mname 
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getSource().equals(typePass) && e.getKeyCode() == KeyEvent.VK_ENTER) {
            setTextPassFieldCondition(typeId, typePass);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

   
}