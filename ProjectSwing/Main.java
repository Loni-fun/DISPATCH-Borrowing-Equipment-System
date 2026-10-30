/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ProjectSwing;

import javax.swing.*;
//
public class Main { 
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        System.setProperty("sun.java2d.opengl", "true");
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("DISPATCH");
            showLoginPage loginPage = new LoginPage(); 
            loginPage.showLogin(frame);
        });
    }
}
