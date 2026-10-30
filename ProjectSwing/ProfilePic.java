package ProjectSwing;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ProfilePic {
    static final String[] TYPES = {"png", "jpg", "jpeg"};
    static final long MAX_BYTES = 5L * 1024 * 1024;
    static final HashMap<String,ImageIcon> cache = new HashMap<>();// 5 MB

    private static String name(String role, int id){
        return role + "_" + id;
    }

    // path of the saved picture, or "" when the user has none
    public static String find(String role, int id){
        for (String ext : TYPES) {
            File file = new File(ManageFiles.PICTURE_FOLDER, name(role, id) + "." + ext);
            if (file.exists()) {
                return file.getAbsolutePath();
            }
        }
        return "";
    }

    // opens the JFileChooser, returns the chosen path or "" when cancelled or invalid
    public static String browse(Component parent){
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose a profile picture");
        chooser.setFileFilter(new FileNameExtensionFilter("Images (png, jpg, jpeg)", TYPES));
        if (chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return "";
        }
        File file = chooser.getSelectedFile();
        if (file.length() > MAX_BYTES) {
            JOptionPane.showMessageDialog(parent, "Picture is too big (max 5 MB)");
            return "";
        }
        try{
            if (ImageIO.read(file) == null) {
                JOptionPane.showMessageDialog(parent, "That file is not a valid image");
                return "";
            }
        }catch(IOException e){
            JOptionPane.showMessageDialog(parent, "Cannot read that file");
            return "";
        }
        return file.getAbsolutePath();
    }

    // copies the chosen picture into the Profile Pictures folder
    public static void save(String role, int id, String sourcePath){
        if (sourcePath == null || sourcePath.isEmpty()) {
            return;
        }
        if (new File(sourcePath).getAbsolutePath().equals(find(role, id))) {
            return;   // they picked the picture they already have
        }
        delete(role, id);   // the old one may have a different extension
        ManageFiles.copyFile(sourcePath, ManageFiles.PICTURE_FOLDER, name(role, id));
    }

    public static void delete(String role, int id){
        for (String ext : TYPES) {
            new File(ManageFiles.PICTURE_FOLDER, name(role, id) + "." + ext).delete();
        }
    }

    public static ImageIcon load(String role, int id, int w, int h){
        return fromFile(find(role, id), w, h);
    }

    // scales to fit the box (keeps the shape). ImageIO is used on purpose:
    // new ImageIcon(path) caches, so a changed picture would not refresh.
    public static ImageIcon fromFile(String path, int w, int h){
    if (path == null || path.isEmpty()) {
        return null;
    }
    File file = new File(path);
    if (!file.exists()) {
        return null;
    }
    String key = path + "|" + file.lastModified() + "|" + w + "x" + h;
    ImageIcon cached = cache.get(key);
    if (cached != null) {
        return cached;
    }
    try{
        BufferedImage img = ImageIO.read(file);
        if (img == null) {
            return null;
        }
        // max, not min: the picture covers the whole frame, the extra is cropped from the center
        double scale = Math.max((double) w / img.getWidth(), (double) h / img.getHeight());
        int sw = (int) Math.ceil(img.getWidth() * scale);
        int sh = (int) Math.ceil(img.getHeight() * scale);
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img, (w - sw) / 2, (h - sh) / 2, sw, sh, null);
        g2.dispose();
        ImageIcon icon = new ImageIcon(out);
        cache.put(key, icon);
        return icon;
    }catch(IOException e){
        return null;
    }
  }
}