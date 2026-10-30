/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ProjectSwing;

import javax.swing.JFrame;

public interface showLoginPage {
    void showLogin(JFrame frame); // shows to login page
}
interface SavesLoad{
    String toLine();  // add save file |
    Object[] toRow(); // add to row table
}

interface Account{
    String getFullName(); // get full name
    int getIdNumber(); 
    String getRole();
    boolean hasAccount(); // check if one has account
    boolean isBlocked();
    boolean passwordMatches(String typed); //--
    boolean isMustChange();// true while the password is still an admin reset code
    void setPassword(String newPass, boolean mustChange);
    void recordFailedLogin();
    void resetTries();
    int getFailedTries();
    void save();
    void clearAccount();
    void remove();
}

