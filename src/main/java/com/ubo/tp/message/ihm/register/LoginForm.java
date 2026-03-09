package com.ubo.tp.message.ihm.register;

import com.ubo.tp.message.controller.LoginController;
import com.ubo.tp.message.datamodel.AuthResult;

import javax.swing.*;
import java.awt.*;

public class LoginForm extends JPanel {
    private final LoginController controller;
    private final AuthView parentView;

    private JTextField tagField;
    private JPasswordField passField;
    private JLabel errorLabel;

    public LoginForm(LoginController controller, AuthView parentView) {
        this.controller = controller;
        this.parentView = parentView;
        this.setLayout(new GridBagLayout());
        initGui();
    }

    private void initGui() {
        // Titre
        JLabel title = new JLabel("Connexion", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        this.add(title, new GridBagConstraints(0,0,2,1,0.0,0.0,
                GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                new Insets(5,5,5,5),0,0));

        this.add(new JLabel("Tag :"), new GridBagConstraints(0,1,1,1,0.0,0.0,
                GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                new Insets(5,5,5,5),0,0));

        tagField = new JTextField(15);
        this.add(tagField, new GridBagConstraints(1,1,1,1,0.0,0.0,
                GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                new Insets(5,5,5,5),0,0));

        this.add(new JLabel("Mot de passe :"), new GridBagConstraints(0,2,1,1,0.0,0.0,
                GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                new Insets(5,5,5,5),0,0));

        passField = new JPasswordField(15);
        this.add(passField, new GridBagConstraints(1,2,1,1,0.0,0.0,
                GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                new Insets(5,5,5,5),0,0));


        JButton loginBtn = new JButton("Se connecter");
        loginBtn.addActionListener(e -> doLogin());
        this.add(loginBtn, new GridBagConstraints(0,3,2,1,0.0,0.0,
                GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                new Insets(5,5,5,5),0,0));

        // Lien vers inscription
        JButton goToRegister = new JButton("Pas de compte ? S'inscrire");
        goToRegister.setBorderPainted(false);
        goToRegister.setContentAreaFilled(false);
        goToRegister.setForeground(Color.BLUE);
        goToRegister.addActionListener(e -> parentView.showRegister());
        this.add(goToRegister, new GridBagConstraints(0,4,2,1,0.0,0.0,
                GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                new Insets(5,5,5,5),0,0));

        // Zone d'erreur
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        this.add(errorLabel, new GridBagConstraints(0,5,2,1,0.0,0.0,
                GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,
                new Insets(5,5,5,5),0,0));
    }

    private void doLogin() {
        String tag = tagField.getText();
        String pass = new String(passField.getPassword());

        AuthResult result = controller.login(tag, pass);

        if (!result.isSuccess()) {
            errorLabel.setText(result.getMessage());
        } else {
            errorLabel.setText("");
        }
    }
}