package ProjectSwing;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Teacher extends User implements SavesLoad {
    static final String role = "Teacher";
    static ArrayList<Teacher> teacherList = new ArrayList<>();
    static String[] Tcolumns ={"Teacher ID","Photo","Last Name","First Name","Middle Name","Gender","Department","Borrowed","Status"};
    static String[] status = {"Active","Inactive"};
    private String department = "";
    private String borrowed = "";
    private String teacherStatus = "";

    public void setDetails(int idNO,String status){
        this.idNo = idNO;
        this.teacherStatus = status;
    }

    public static Teacher addTeacherToFile(String idText, String status){
        if (catchLetter(idText) == false) {
            return null;
        }
        loadTeacherInfo();
        int id = Integer.parseInt(idText.trim());
        if (findIDNumber(id) != null) {
            JOptionPane.showMessageDialog(null, "This ID already exists!");
            return null;
        }
        Teacher get = new Teacher();
        get.setDetails(id, status);
        teacherList.add(get);
        saveTeacherFile();
        return get;
    }

    public static void saveTeacherFile(){
        ManageFiles.saveFile(ManageFiles.TEACHER, teacherList);
    }

    public static boolean catchLetter(String input){
        int id;
        try{
            id = Integer.parseInt(input.trim());
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Teacher ID must be numbers");
            return false;
        }
        if (input.length() != 10 ) {
            JOptionPane.showMessageDialog(null, "Teacher must be exactly 10 numbers");
            return false;
        }else if(id <= 0){
            JOptionPane.showMessageDialog(null, "Invalid Teacher ID");
            return false;
        }
        return true;
    }

    public static Teacher findIDNumber(int id){
        for (Teacher t: teacherList) {
            if (t.idNo == id) {
                return t;
            }
        }
        return null;
    }

    public static void loadTeacherInfo(){
        teacherList.clear();
        for (String[] data : ManageFiles.loadFile(ManageFiles.TEACHER)) {
            try{
                Teacher get = new Teacher();
                if (data.length >= 7 && data.length <= 10) {
                    String savedPassword = "";
                    if (data.length >= 8) {
                        savedPassword = data[7];
                    }
                    get.setSavedTeacherInfo(Integer.parseInt(data[0]), data[1], data[2], data[3], data[4], data[5], data[6], savedPassword);
                    if (data.length >= 9) {
                        get.failedTries = Integer.parseInt(data[8]);
                    }
                    if (data.length == 10) {
                        get.mustChange = Boolean.parseBoolean(data[9]);
                    }
                }else if(data.length == 2){
                    get.setDetails(Integer.parseInt(data[0]), data[1]);
                }else{
                    continue;
                }
                teacherList.add(get);
            }catch(NumberFormatException e){
                System.out.println("Skipped a bad teacher line");
            }
        }
                SearchHelper.insertionSort(teacherList, (a, b) -> Integer.compare(a.getIdNumber(), b.getIdNumber()));
    }

    public void setSavedTeacherInfo(int idNo, String lName, String fName, String mName, String department, String borrowed, String status, String password){
        this.idNo = idNo;
        this.lName = lName;
        this.fName = fName;
        this.mName = mName;
        this.department = department;
        this.borrowed = borrowed;
        this.teacherStatus = status;
        this.password = password;
    }

    public void refreshTeacherTable(DefaultTableModel TeacherDtm){
        loadTeacherInfo();
        BlockedUsers.loadBlockedUsers();
        ManageFiles.fillTable(TeacherDtm, teacherList);
    }

    public static boolean registerAccount(String id, String Lname, String Fname, String Mname, String pass, String conPass){
        String error = PasswordReset.checkNewPassword(pass, conPass);
        if (error != null) {
            JOptionPane.showMessageDialog(null, error, "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!catchLetter(id)) {
            return false;
        }
        loadTeacherInfo();
        Teacher s = findIDNumber(Integer.parseInt(id.trim()));
        if (s == null) {
            JOptionPane.showMessageDialog(null, "This ID number is not registered within the system","Warning",JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if(s.getTeacherStatus().equals("Inactive")){
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
        saveTeacherFile();
        return true;
    }

    public static boolean unBlock(int id){
        loadTeacherInfo();
        Teacher t = findIDNumber(id);
        if (t == null) {
            return false;
        }
        t.failedTries = 0;
        saveTeacherFile();
        return BlockedUsers.unblock(String.valueOf(id), role);
    }

    public static boolean changeStatus(int id, String newStatus){
        loadTeacherInfo();
        Teacher t = findIDNumber(id);
        if (t == null) {
            return false;
        }
        t.teacherStatus = newStatus;
        saveTeacherFile();
        return true;
    }

    public static boolean updateProfile(int id, String newLast, String newFirst, String newMiddle, String newDepartment){
        loadTeacherInfo();
        Teacher t = findIDNumber(id);
        if (t == null) {
            return false;
        }
        t.lName = newLast;
        t.fName = newFirst;
        t.mName = newMiddle;
        t.department = ManageFiles.clean(newDepartment);
        saveTeacherFile();
        return true;
}

    public static String changePassword(int id, String oldPass, String newPass){
        loadTeacherInfo();
        Teacher t = findIDNumber(id);
        if (t == null) {
            return "Teacher ID was not found";
        }
        if (!t.password.equals(oldPass)) {
            return "Current password is incorrect";
        }
        t.password = newPass;
        saveTeacherFile();
        return null;
    }

    public String getDepartment(){
        return department;
    }

    public String getBorrowed(){
        return borrowed;
    }

    public String getTeacherStatus(){
        return teacherStatus;
    }

    @Override
    public String toLine(){
        return ManageFiles.join(idNo, ManageFiles.clean(lName), ManageFiles.clean(fName), ManageFiles.clean(mName), department,
        borrowed, teacherStatus, password, failedTries, mustChange);
    }

    @Override
    public Object[] toRow(){
       String shownStatus = teacherStatus;
    if (BlockedUsers.isBlockedNow(String.valueOf(idNo), role)) {
        shownStatus = "Blocked";
    }
    return new Object[]{idNo, ProfilePic.load(role, idNo, 59, 59), lName, fName, mName,ProfilePanel.getGender(role, idNo), department, borrowed, shownStatus};
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public void save() {
        saveTeacherFile();
    }

    @Override
    public void remove() {
        teacherList.remove(this);
        saveTeacherFile();
    }
}