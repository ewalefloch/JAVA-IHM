package main.java.com.ubo.tp.message.ihm.register.fx;

import javafx.scene.layout.BorderPane;
import main.java.com.ubo.tp.message.controller.LoginController;

public class AuthViewFx extends BorderPane {

    private final LoginFormFx loginForm;
    private final RegisterFormFx registerForm;

    public AuthViewFx(LoginController controller) {
        this.loginForm = new LoginFormFx(controller, this);
        this.registerForm = new RegisterFormFx(controller, this);

        // Afficher le login par défaut
        showLogin();
    }

    public void showLogin() {
        this.setCenter(loginForm);
    }

    public void showRegister() {
        this.setCenter(registerForm);
    }
}