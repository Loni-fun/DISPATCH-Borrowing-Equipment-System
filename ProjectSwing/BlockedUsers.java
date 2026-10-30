package ProjectSwing;

import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

public class BlockedUsers implements SavesLoad {
    static String[] blockUserscolumns ={"User","Role","Reason","Date","Status"};
    static final int MAX_TRIES = 3;
    static ArrayList<BlockedUsers> blockUsers = new ArrayList<>();
    private String user;
    private String role;
    private String reason;
    private String date;
    private String status;

    public BlockedUsers(String user, String role, String reason, String date, String status){
        this.user = user;
        this.role = role;
        this.reason = reason;
        this.date = date;
        this.status = status;
    }
    public String getUser(){ return user;}
    public String getRole(){ return role;}
    public String getReason(){ return reason;}
    public String getDate(){ return date;}
    public String getStatus(){ return status;}

    public static BlockedUsers find(String id,String role){
        for (BlockedUsers u: blockUsers) {
            if (u.user.equals(id)&& u.role.equals(role)) {
                return u;
            }
        }
        return null;
    }
    public static ArrayList<BlockedUsers> getBlockedList(){
        ArrayList<BlockedUsers> shown = new ArrayList<>();
        for (BlockedUsers b : blockUsers) {
            if (b.status.equals("Blocked")) {
                shown.add(b);
            }
        }
        return shown;
    }
    public static void saveBlockedUsers(){
        ManageFiles.saveFile(ManageFiles.BLOCKED, blockUsers);
    }
    public static void loadBlockedUsers(){
        blockUsers.clear();
            for (String[] d : ManageFiles.loadFile(ManageFiles.BLOCKED)) {
        if (d.length == 5) {
            blockUsers.add(new BlockedUsers(d[0], d[1], d[2], d[3], d[4]));
        } else if (d.length == 4) {   // old format without Status, treat as blocked
            blockUsers.add(new BlockedUsers(d[0], d[1], d[2], d[3], "Blocked"));
        }
    }
}
    public static void loadBlockUsersTable(DefaultTableModel dtm){
        loadBlockedUsers();
        ManageFiles.fillTable(dtm, getBlockedList());
    }
    public static void block(String id, String role, String reason){
        loadBlockedUsers();
        BlockedUsers u = find(id,role);
        String today = LocalDate.now().toString();
        String block = "Blocked";
        if (u == null) {
            blockUsers.add(new BlockedUsers(id,role,reason,today, block));
        } else{
            u.reason = reason;
            u.date = today;
            u.status = block;
        }
        saveBlockedUsers();
    }
    public static boolean isBlocked(String user, String role){
        loadBlockedUsers();
        return isBlockedNow(user, role);
    }
    public static boolean isBlockedNow(String user, String role){
        BlockedUsers b = find(user, role);
        if (b == null) {
            return false;
        }
        return b.status.equals("Blocked");
    }
    public static boolean unblock(String id,String role){
        loadBlockedUsers();
        BlockedUsers b = find(id,role);
        if (b == null) {
            return false;
        }
        b.status = "Unblocked";
        saveBlockedUsers();
        return true;
    }

    @Override
    public String toLine(){
        return ManageFiles.join(user, role, ManageFiles.clean(reason), date, status);
    }

    @Override
    public Object[] toRow(){
        return new Object[]{user, role, reason, date, status};
    }
}