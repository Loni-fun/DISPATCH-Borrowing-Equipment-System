package ProjectSwing;

import java.util.ArrayList;

public class Admin extends User {
    static final String role = "Admin";
    static final int ADMIN_ID = 2026001022;
    private static final String DEFAULT_PASSWORD = "asd1234acjn";  
    private static final Admin instance = new Admin();             

    private Admin() {
        idNo = ADMIN_ID;
        lName = "Admin";
        fName = "System";
        mName = "";
        password = DEFAULT_PASSWORD;
    }

    public static Admin getInstance() {
        return instance;
    }

    public static boolean isAdminId(int id) {
        return id == ADMIN_ID;
    }


    public void load() {
        for (String[] data : ManageFiles.loadFile(ManageFiles.ADMIN)) {
            if (data.length == 4) {
                password = data[0];
                lName = data[1];
                fName = data[2];
                mName = data[3];
            }
            return;
        }
    }

    @Override
    public void save() {
        ArrayList<String> lines = new ArrayList<>();
        lines.add(ManageFiles.join(password, ManageFiles.clean(lName), ManageFiles.clean(fName), ManageFiles.clean(mName)));
        ManageFiles.writeLines(ManageFiles.ADMIN, lines);
    }

    @Override
    public String getRole() {
        return role;
    }

 
    @Override
    public boolean isBlocked() {
        return false;   
    }

    @Override
    public void recordFailedLogin() {
    }

    @Override
    public void resetTries() {
    }

    @Override
    public void clearAccount() {
    }

    @Override
    public void remove() {
    }
}