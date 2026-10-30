package ProjectSwing;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ManageFiles {
    static final String BASE = "C:\\Users\\ADMIN\\OneDrive\\Documents\\Project OOP\\";
    static final String EQUIPMENT = BASE + "Admin\\Equipments\\equipment.txt";
    static final String REQUEST = BASE + "Admin\\Requests\\requests.txt";
    static final String RULES = BASE + "Admin\\Requests\\rules.txt";
    static final String STUDENT = BASE + "Admin\\Registered Users\\Student.txt";
    static final String TEACHER = BASE + "Admin\\Registered Users\\Teacher.txt";
    static final String BLOCKED = BASE + "Admin\\Blocked Users\\BlockedUsers.txt";
    static final String IMAGE_FOLDER = BASE + "Equipment Images";
    static final String PROOF_FOLDER = BASE + "Request Proofs";
    static final String PICTURE_FOLDER = BASE + "Profile Pictures";   // NEW
    static final String ADMIN = BASE + "Admin\\Admin.txt";
    static final String DELETE = BASE + "Admin\\Registered Users\\DeleteRequests.txt";

    // makes the text safe for a "|" separated file
    public static String clean(String text){
        if (text == null) {
            return "";
        }
        return text.replace("|", "/").replace("\r", " ").replace("\n", " ").trim();
    }

    // joins any fields into one line: join(1, "Cord", 5) -> 1|Cord|5
    public static String join(Object... fields){
        String line = "";
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                line += "|";
            }
            line += fields[i];
        }
        return line;
    }

    
    public static void saveFile(String path, ArrayList<? extends SavesLoad> list){
        ArrayList<String> lines = new ArrayList<>();
        for (SavesLoad item : list) {
            lines.add(item.toLine());
        }
        writeLines(path, lines);
    }

    
    public static void writeLines(String path, ArrayList<String> lines){
        try{
            File file = new File(path);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            PrintWriter save = new PrintWriter(new FileWriter(file));
            for (String line : lines) {
                save.println(line);
            }
            save.close();
        }catch(IOException e){
            JOptionPane.showMessageDialog(null, "Cannot save " + path);
        }
    }
    
    public static ArrayList<String[]> loadFile(String path){
        ArrayList<String[]> rows = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) {
            return rows;
        }
        try{
            BufferedReader read = new BufferedReader(new FileReader(file));
            String line;
            while((line = read.readLine()) != null){
                if (line.trim().isEmpty()) {
                    continue;
                }
                rows.add(line.split("\\|", -1));  
            }
            read.close();
        }catch(IOException e){
            JOptionPane.showMessageDialog(null, "Cannot read " + path);
        }
        return rows;
    }

   
    public static void fillTable(DefaultTableModel dtm, ArrayList<? extends SavesLoad> list){
        dtm.setRowCount(0);
        for (SavesLoad item : list) {
            dtm.addRow(item.toRow());
        }
    }

    // COPY: replaces Equipment.copyImage and Request.copyProof
    public static String copyFile(String sourcePath, String folderPath, String newName){
        if (sourcePath == null || sourcePath.isEmpty()) {
            return "";
        }
        try{
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            String extension = "";
            if (sourcePath.lastIndexOf(".") != -1) {
                extension = sourcePath.substring(sourcePath.lastIndexOf("."));
            }
            File target = new File(folder, newName + extension);
            Files.copy(new File(sourcePath).toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return target.getAbsolutePath();
        }catch(IOException e){
            return sourcePath;
        }
    }

    
    public static ImageIcon loadScaledIcon(String path, int maxW, int maxH){
        if (path == null || path.isEmpty()) {
            return null;
        }
        try{
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }
            BufferedImage img = ImageIO.read(file);
            if (img == null) {
                return null;
            }
            double scale = Math.min((double) maxW / img.getWidth(), (double) maxH / img.getHeight());
            scale = Math.min(scale, 1.0);   // never enlarge
            int width = Math.max(1, (int) (img.getWidth() * scale));
            int height = Math.max(1, (int) (img.getHeight() * scale));
            return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
        }catch(IOException e){
            return null;
        }
    }
}