/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ProjectSwing;

import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ADMIN
 */
public class DeleteRequests implements SavesLoad{

    @Override
    public String toLine(){
        return ManageFiles.join(userId, role, ManageFiles.clean(reason), date, status);
    }

    @Override
    public Object[] toRow(){
        return new Object[]{userId, role, reason, date, status};
    }
    
    static String[] columns = {"User ID","Role","Reason","Date","Status"};
    static ArrayList<DeleteRequests> list = new ArrayList<>();
    private int userId;
    private String role;
    private String reason;
    private String date;
    private String status;
    
    public DeleteRequests(int userId,String role ,String reason, String date,String status){
        this.userId = userId;
        this.role = role;
        this.reason = reason;
        this.date = date;
        this.status = status;
    }
    public int getUserId(){ return userId; }
    public String getRole(){ return role; }
    public String getReason(){ return reason; }
    public String getDate(){ return date; }
    public String getStatus(){ return status; }
    
    public static void save(){
        ManageFiles.saveFile(ManageFiles.DELETE, list); // gotta go back
        
    }
    public static void load(){
        list.clear();
        for (String[] d : ManageFiles.loadFile(ManageFiles.DELETE)) {
            if (d.length != 5) {
                continue;
            }
            try{
                list.add(new DeleteRequests(Integer.parseInt(d[0]), d[1], d[2], d[3], d[4]));
            }catch(NumberFormatException e){
                System.out.println("Skipped a bad delete request line");
            }
        }
    }
    
    public static DeleteRequests findPending(int id, String role){
        for (DeleteRequests r : list) {
            if (r.userId == id && r.role.equals(role) && r.status.equals("Pending")) {
                return r;
            }
        }
        return null;
    }

    public static void add(int id, String role, String reason){
        load();
        list.add(new DeleteRequests(id, role, ManageFiles.clean(reason), LocalDate.now().toString(), "Pending"));
        save();
    }

    public void setStatus(String newStatus){
        this.status = newStatus;
        save();
    }

    // the admin table only shows the requests that still wait for an answer
    public static void loadTable(DefaultTableModel dtm){
        load();
        ArrayList<DeleteRequests> pending = new ArrayList<>();
        for (DeleteRequests r : list) {
            if (r.status.equals("Pending")) {
                pending.add(r);
            }
        }
        ManageFiles.fillTable(dtm, pending);
    }   
}
