package ProjectSwing;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Student extends User implements SavesLoad{
    static final String role = "Student";
    static ArrayList<Student> studentList = new ArrayList<>();
    private String course = "";
    private String year = "";
    private String borrowed = "";
    private String studentStatus = "";
    static String[] Scolumns = {"Student ID","Photo","Last Name","First Name","Middle Name","Gender","Course","Year","Borrowed","Status"};
    static String[] status = {"Active","Inactive"};
    
    
    @Override
    public String getRole() {
        return role;
    }

    @Override
    public void save() {
        saveStudentFile();
    }

    @Override
    public void remove() {
        studentList.remove(this);
        saveStudentFile();
    }
    
    public void setDetails(int idNO,String status){
        this.idNo = idNO;
        this.studentStatus = status;
    }

    public static Student addStudentToFile(String idNo, String status){
        if (catchLetter(idNo) == false) {
            return null;
        }
        loadStudentInfo();
        int id = Integer.parseInt(idNo.trim());
        if (findIDNumber(id) != null) {
            JOptionPane.showMessageDialog(null, "This ID already exists!");
            return null;
        }
        Student get = new Student();
        get.setDetails(id, status);
        studentList.add(get);
        saveStudentFile();
        return get;
    }

    public static void saveStudentFile(){
        ManageFiles.saveFile(ManageFiles.STUDENT, studentList);
    }

    public static void loadStudentInfo(){
        studentList.clear();
        for (String[] data : ManageFiles.loadFile(ManageFiles.STUDENT)) {
            try{
                Student get = new Student();
                if (data.length >= 8 && data.length <= 11) {
                    String savedPassword = "";
                    if (data.length >= 9) {
                        savedPassword = data[8];
                    }
                    get.setSavedStudentInfo(Integer.parseInt(data[0]), data[1], data[2], data[3], data[4], data[5], data[6], data[7], savedPassword);
                    if (data.length >= 10) {
                        get.failedTries = Integer.parseInt(data[9]);
                    }
                    if (data.length == 11) {
                        get.mustChange = Boolean.parseBoolean(data[10]);
                    }
                }else if(data.length == 2){   // newly added student ID
                    get.setDetails(Integer.parseInt(data[0]), data[1]);
                }else{
                    continue;
                }
                studentList.add(get);
            }catch(NumberFormatException e){
                System.out.println("");
            }
        }
         SearchHelper.insertionSort(studentList, (a, b) -> Integer.compare(a.getIdNumber(), b.getIdNumber()));
    }

    public void refreshStudentTable(DefaultTableModel studentDtm){
        loadStudentInfo();
        BlockedUsers.loadBlockedUsers();
        ManageFiles.fillTable(studentDtm, studentList);
    }

    public static boolean catchLetter(String input){
        int idCheck;
        try{
            idCheck = Integer.parseInt(input.trim());
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Invalid Student ID"); // if letter
            return false;
        }
        if (input.length() != 10 || idCheck <= 0) {
            JOptionPane.showMessageDialog(null, "Invalid Student ID");
            return false;
        }
        return true;
    }

    public static Student findIDNumber(int id){
        for (Student e: studentList) {
            if (e.idNo == id) {
                return e;
            }
        }
        return null;
    }

    public void setSavedStudentInfo(int idNo, String lName, String fName, String mName, String course, String year, String borrowed, String studentStatus, String password){
        this.idNo = idNo;
        this.lName = lName;
        this.fName = fName;
        this.mName = mName;
        this.course = course;
        this.year = year;
        this.borrowed = borrowed;
        this.studentStatus = studentStatus;
        this.password = password;
    }

    public static boolean registerAccount(String id, String Lname, String Fname, String Mname, String pass, String confirmPass){
        String error = PasswordReset.checkNewPassword(pass, confirmPass);
        if (error != null) {
            JOptionPane.showMessageDialog(null, error, "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!catchLetter(id)) {
            return false;
        }
        loadStudentInfo();
        Student s = findIDNumber(Integer.parseInt(id.trim()));
        if (s == null) {
            JOptionPane.showMessageDialog(null, "This ID number is not registered within the system","Warning",JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if(s.getStudentStatus().equals("Inactive")){
            JOptionPane.showMessageDialog(null, "This ID number is still Inactive","Warning",JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (s.hasAccount()) {
            JOptionPane.showMessageDialog(null, "An account already exists for this ID","Warning",JOptionPane.WARNING_MESSAGE);
            return false;
        }
        s.lName = Lname;
        s.fName = Fname;
        s.mName = Mname;
        s.password = pass;
        saveStudentFile();
        return true;
    }

    public static boolean unBlock(int id){
        loadStudentInfo();
        Student t = findIDNumber(id);
        if (t == null) {
            return false;
        }
        t.failedTries = 0;
        saveStudentFile();
        return BlockedUsers.unblock(String.valueOf(id), role);
    }

    public static boolean changeStatus(int id, String newStatus){
        loadStudentInfo();
        Student t = findIDNumber(id);
        if (t == null) {
            return false;
        }
        t.studentStatus = newStatus;
        saveStudentFile();
        return true;
    }

    public static boolean updateProfile(int id, String newLast, String newFirst, String newMiddle, String newCourse, String newYear){
        loadStudentInfo();
        Student s = findIDNumber(id);
        if (s == null) {
            return false;
        }
        s.lName = newLast;
        s.fName = newFirst;
        s.mName = newMiddle;
        s.course = ManageFiles.clean(newCourse);
        s.year = ManageFiles.clean(newYear);
        saveStudentFile();
        return true;
    }

    
    public static String changePassword(int id, String oldPass, String newPass){
        loadStudentInfo();
        Student s = findIDNumber(id);
        if (s == null) {
            return "Student ID was not found";
        }
        if (!s.password.equals(oldPass)) {
            return "Current password is incorrect";
        }
        s.password = newPass;
        saveStudentFile();
        return null;
    }

    public String getStudentCourse(){
        return course;
    }

    public String getYear(){
        return year;
    }

    public String getBorrowed(){
        return borrowed;
    }

    public String getStudentStatus(){
        return studentStatus;
    }

    @Override
    public String toLine(){
        return ManageFiles.join(idNo, ManageFiles.clean(lName), ManageFiles.clean(fName), ManageFiles.clean(mName), course, year,
        borrowed, studentStatus, password, failedTries, mustChange);
    }

    @Override
    public Object[] toRow(){
        String shownStatus = studentStatus;
        if (BlockedUsers.isBlockedNow(String.valueOf(idNo), role)) {
        shownStatus = "Blocked";
    }
    return new Object[]{idNo, ProfilePic.load(role, idNo, 59, 59), lName, fName, mName,ProfilePanel.getGender(role, idNo), course, year, borrowed, shownStatus};
    }

   
}