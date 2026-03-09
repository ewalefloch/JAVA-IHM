package com.ubo.tp.message.ihm.register;


import com.ubo.tp.message.controller.LoginController;
import javax.swing.*;
import java.awt.*;

public class AuthView extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel container;

    public AuthView(LoginForm login, RegisterForm registerForm) {
        this.cardLayout = new CardLayout();
        this.container = new JPanel(cardLayout);

        this.setLayout(new BorderLayout());
        this.add(container, BorderLayout.CENTER);

        // Ajout des deux vues
        container.add(registerForm);
        container.add(login);

        // login par défaut
        showLogin();
    }

    public void showLogin() {
        cardLayout.show(container, "LOGIN");
    }

    public void showRegister() {
        cardLayout.show(container, "REGISTER");
    }
}