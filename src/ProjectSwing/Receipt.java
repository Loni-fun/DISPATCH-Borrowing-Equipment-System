package ProjectSwing;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class Receipt {

    public static String buildReceipt(AdminDisplay.Request r){
        Account user = PasswordReset.findAccount(r.getUserId(), r.getRole());
        String name = "-";
        if (user != null && !user.getFullName().isEmpty()) {
            name = user.getFullName();
        }
        String extra = "";
        if (r.getRole().equals(Student.role)) {
            Student.loadStudentInfo();
            Student s = Student.findIDNumber(r.getUserId());
            if (s != null) {
                extra = "Course: " + s.getStudentCourse() + "   Year: " + s.getYear();
            }
        } else {
        Teacher.loadTeacherInfo();
        Teacher t = Teacher.findIDNumber(r.getUserId());
        if (t != null) {
            extra = "Department: " + t.getDepartment();
    }
}
 String line = "------------------------------------------\n";
        return "                 DISPATCH\n"
            + "        Equipment Borrowing Receipt\n"
            + line
            + "Request No      : " + r.getRequestNo() + "\n"
            + "Borrower Name   : " + name + "\n"
            + "Role / ID       : " + r.getRole() + " " + r.getUserId() + "\n"
            + extra + "\n"
            + line
            + "Equipment       : " + r.getEquipmentId() + " " + r.getEquipmentName() + "\n"
            + "Quantity        : " + r.getQuantity() + "\n"
            + "Requested Date  : " + r.getDateRequested() + "\n"
            + "Approved        : " + r.getDateApproved() + "\n"
            + "Pick up by      : " + r.getPickupDeadLine() + "\n"
            + "Status          : " + r.getStatus() + "\n"
            + chargeLines(r)
            + line
            + "Admin Note      : " + r.getComment() + "\n\n\n"
            + "Show this receipt to the admin\n"
            + "at the office to get your equipment.\n";
    }

    // only shown for returned items that were damaged or lost
    private static String chargeLines(AdminDisplay.Request r){
        if (r.getChargeAmount() <= 0) {
            return "";
        }
        return "Condition       : " + r.getCondition() + "\n"
            + "Amount charged  : " + AdminDisplay.Request.peso(r.getChargeAmount()) + "\n"
            + "Amount paid     : " + AdminDisplay.Request.peso(r.getAmountPaid()) + "\n";
    }

    public static void show(Component parent, AdminDisplay.Request r){
        String status = r.getStatus();
        if (!status.equals("Approved") && !status.equals("Borrowed") && !status.equals("Overdue") && !status.equals("Returned")) {
            JOptionPane.showMessageDialog(parent, "A receipt is available after the request is approved\nThis request is " + status);
            return;
        }
        JTextArea area = new JTextArea(buildReceipt(r));
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setEditable(false);
        area.setOpaque(false);   // blends in with the dialog
        JOptionPane.showMessageDialog(parent, area, "Receipt " + r.getRequestNo(), JOptionPane.PLAIN_MESSAGE);
    }
}