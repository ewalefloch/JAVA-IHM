package com.ubo.tp.message.ihm.register;

import com.ubo.tp.message.controller.LoginController;
import com.ubo.tp.message.datamodel.AuthResult;

import javax.swing.*;
import java.awt.*;

public class RegisterForm extends JPanel {
    private final LoginController controller;
    private AuthView parentView;

    private JTextField tagField;
    private JTextField nameField;
    private JPasswordField passField;
    private JLabel infoLabel;

    public RegisterForm(LoginController controller) {
        this.controller = controller;
        this.setLayout(new GridBagLayout());
        initGui();
    }

    private void initGui() {
        JLabel title = new JLabel("Inscription", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        this.add(title, new GridBagConstraints(0,0,2,1,0.0,0.0,
                GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                new Insets(5,5,5,5),0,0));

        // Tag
        this.add(new JLabel("Tag :"), new GridBagConstraints(0,1,1,1,0.0,0.0,
                GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                new Insets(5,5,5,5),0,0));

        tagField = new JTextField(15);
        this.add(tagField, new GridBagConstraints(1,1,1,1,0.0,0.0,
                GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                new Insets(5,5,5,5),0,0));

        //Nom
        this.add(new JLabel("Nom :"), new GridBagConstraints(0,2,1,1,0.0,0.0,
                GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                new Insets(5,5,5,5),0,0));

        nameField = new JTextField(15);

        this.add(nameField, new GridBagConstraints(1,2,1,1,0.0,0.0,
                GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                new Insets(5,5,5,5),0,0));


        //Mdp

        this.add(new JLabel("Mot de passe :"), new GridBagConstraints(0,3,1,1,0.0,0.0,
                GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                new Insets(5,5,5,5),0,0));

         passField = new JPasswordField(15);

         this.add(passField, new GridBagConstraints(1,3,1,1,0.0,0.0,
                 GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                 new Insets(5,5,5,5),0,0));

        // Bouton
        JButton regBtn = new JButton("Créer mon compte");
        regBtn.addActionListener(e -> doRegister());
        this.add(regBtn, new GridBagConstraints(0,4,2,1,0.0,0.0,
                GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                new Insets(5,5,5,5),0,0));

        // Retour Login
        JButton backBtn = new JButton("Déjà un compte ? Connexion");
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setForeground(Color.BLUE);
        backBtn.addActionListener(e -> parentView.showLogin());
        this.add(backBtn, new GridBagConstraints(0,5,2,1,0.0,0.0,
                GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                new Insets(5,5,5,5),0,0));

        infoLabel = new JLabel("");
        this.add(infoLabel, new GridBagConstraints(0,6,2,1,0.0,0.0,
                GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                new Insets(5,5,5,5),0,0));
    }

    //SRS-MAP-USR-001
    private void doRegister() {
        AuthResult result = controller.register(tagField.getText(), nameField.getText(), new String(passField.getPassword()));

        if (result.isSuccess()) {
            infoLabel.setForeground(Color.GREEN);
            infoLabel.setText("Compte créé ! Veuillez vous connecter.");
            Timer t = new Timer(1000, e -> parentView.showLogin());
            t.setRepeats(false);
            t.start();
        } else {
            infoLabel.setForeground(Color.RED);
            infoLabel.setText(result.getMessage());
        }
    }

    public void setParentView(AuthView authView){
        this.parentView = authView;
    }
}
