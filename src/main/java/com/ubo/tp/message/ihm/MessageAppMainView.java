package main.java.com.ubo.tp.message.ihm;

import main.java.com.ubo.tp.message.controller.MenuActionController;
import main.java.com.ubo.tp.message.controller.MessageApp;

import javax.swing.*;
import java.awt.*;

/**
 * Vue principale de l'application.
 */
public class MessageAppMainView extends JFrame {

    private final MenuActionController menuActionController;

    public MessageAppMainView(MenuActionController controller) {
        this.menuActionController = controller;
        this.initGui();
        this.initMenu();
    }

    private void initGui() {
        this.setTitle("MessageApp");
        this.setSize(new Dimension(800, 800));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            ImageIcon image = new ImageIcon("src/main/resources/images/logo_20.png");
            if (image.getIconWidth() > 0) {
                this.setIconImage(image.getImage());
            }
        } catch (Exception e) {
            // ignore
        }

        // Centrage de la fenêtre
        this.setLocationRelativeTo(null);
    }

    /**
     * Initialise le menu de l'application.
     */
    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();

        // === Menu Fichier ===
        JMenu menuFichier = new JMenu("Fichier");

        // Déconnexion
        JMenuItem decoItem = new JMenuItem("Déconnexion");
        decoItem.setToolTipText("Déconnexion du compte");
        decoItem.addActionListener(e -> handleLogout());

        // Suppr Compte

        JMenuItem deleteItem = new JMenuItem("Supprimer le compte");
        deleteItem.setToolTipText("Supprimer définitivement votre compte");
        deleteItem.setForeground(Color.RED);
        deleteItem.addActionListener(e -> handleDeleteAccount());

        // modifier tag Compte

        JMenuItem updateTagItem = new JMenuItem("Modifier nom");
        updateTagItem.setToolTipText("Changer votre nom d'utilisateur");
        updateTagItem.setForeground(Color.GREEN);
        updateTagItem.addActionListener(e -> handleModifyTag());


        // Quitter
        JMenuItem quitItem = new JMenuItem("Quitter");
        quitItem.setToolTipText("Fermer l'application");
        quitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));

        try {
            Icon quitIcon = new ImageIcon("src/main/resources/images/exitIcon_20.png");
            quitItem.setIcon(quitIcon);
        } catch (Exception e) {
            // ignore
        }

        quitItem.addActionListener(e -> handleQuit());

        menuFichier.add(decoItem);
        menuFichier.addSeparator();
        menuFichier.add(updateTagItem);
        menuFichier.addSeparator();
        menuFichier.add(deleteItem);
        menuFichier.addSeparator();
        menuFichier.add(quitItem);


        // === Menu Aide ===
        JMenu helpMenu = new JMenu("?");

        JMenuItem aboutItem = new JMenuItem("À propos");
        aboutItem.setAccelerator(KeyStroke.getKeyStroke("F1"));
        aboutItem.addActionListener(e -> showAboutDialog());

        helpMenu.add(aboutItem);

        // Ajout des menus à la barre
        menuBar.add(menuFichier);
        menuBar.add(helpMenu);

        this.setJMenuBar(menuBar);
    }

    private void handleModifyTag() {
        String newTag = JOptionPane.showInputDialog(
                this,
                "Entrez votre nouveau nom d'utilisateur :",
                "Modification du nom",
                JOptionPane.QUESTION_MESSAGE
        );

        if (newTag != null && !newTag.trim().isEmpty()) {
            menuActionController.modifyUserTag(newTag.trim());
        }
    }

    private void handleDeleteAccount() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            menuActionController.deleteAccount();
        }
    }

    private void handleLogout() {
        menuActionController.logout();
    }

    private void handleQuit() {
        menuActionController.exit();
    }


    private void showAboutDialog() {
        String message = "MessageApp\n\n" +
                "UBO M2-TIIL\n" +
                "Département Informatique\n\n" +
                "Application de messagerie";

        ImageIcon icon = null;
        try {
            icon = new ImageIcon("src/main/resources/images/logo_50.png");
            if (icon.getIconWidth() <= 0) {
                icon = null;
            }
        } catch (Exception e) {
            //ignore
        }

        JOptionPane.showMessageDialog(
                this,
                message,
                "À propos",
                JOptionPane.INFORMATION_MESSAGE,
                icon);
    }

    public JFrame getFrame() {
        return this;
    }
}