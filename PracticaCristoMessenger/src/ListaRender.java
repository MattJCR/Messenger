
import java.awt.Component;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Matt Workstation
 */
public class ListaRender extends JLabel implements ListCellRenderer{
    
    static ImageIcon avatarDefault = new ImageIcon("src\\Images\\logo.jpg");
    static ImageIcon avatar = new ImageIcon("src\\Images\\logo.jpg");
    ListaRender(){
        Image image = avatarDefault.getImage();
        Image newimg = image.getScaledInstance(50, 50,  java.awt.Image.SCALE_SMOOTH);
        avatarDefault = new ImageIcon(newimg);
        image = avatar.getImage();
        newimg = image.getScaledInstance(50, 50,  java.awt.Image.SCALE_SMOOTH);
        avatar = new ImageIcon(newimg);
    }
    @Override
    public Component getListCellRendererComponent(JList list, Object value, 
            int index, boolean isSelected, boolean cellHasFocus) {
        String valor = value.toString();
        this.setText(valor);
        if (!valor.equals("")) {
            this.setIcon(avatarDefault);
        }else{
            
            this.setIcon(avatar);
        }
        if (isSelected) {
            this.setBackground(list.getSelectionBackground());
            this.setForeground(list.getSelectionForeground());
        }else{
            this.setBackground(list.getBackground());
            this.setForeground(list.getForeground());
        }
        this.setEnabled(list.isEnabled());
        this.setFont(list.getFont());
        this.setOpaque(true);
        return this;
    }
    
}
