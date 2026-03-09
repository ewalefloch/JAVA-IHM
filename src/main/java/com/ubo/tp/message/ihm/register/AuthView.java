package com.ubo.tp.message.ihm.register;


import com.ubo.tp.message.controller.LoginController;
import javax.swing.*;
import java.awt.*;

public class AuthView extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel container;

    public AuthView(LoginController controller) {
        this.cardLayout = new CardLayout();
        this.container = new JPanel(cardLayout);

        this.setLayout(new BorderLayout());
        this.add(container, BorderLayout.CENTER);

        // Ajout des deux vues
        container.add(new LoginForm(controller, this), "LOGIN");
        container.add(new RegisterForm(controller, this), "REGISTER");

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