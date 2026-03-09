package main.java.com.ubo.tp.message.ihm.register.fx;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.java.com.ubo.tp.message.controller.LoginController;
import main.java.com.ubo.tp.message.datamodel.AuthResult;

public class LoginFormFx extends GridPane {
    private final LoginController controller;
    private final AuthViewFx parentView;

    private TextField tagField;
    private PasswordField passField;
    private Label errorLabel;

    public LoginFormFx(LoginController controller, AuthViewFx parentView) {
        this.controller = controller;
        this.parentView = parentView;
        initGui();
    }

    private void initGui() {
        this.setAlignment(Pos.CENTER);
        this.setHgap(10);
        this.setVgap(15);
        this.setPadding(new Insets(20));

        // Titre
        Label title = new Label("Connexion");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        GridPane.setHalignment(title, HPos.CENTER);
        this.add(title, 0, 0, 2, 1); // col, row, colspan, rowspan

        // Tag
        this.add(new Label("Tag :"), 0, 1);
        tagField = new TextField();
        this.add(tagField, 1, 1);

        // Mot de passe
        this.add(new Label("Mot de passe :"), 0, 2);
        passField = new PasswordField();
        this.add(passField, 1, 2);

        // Bouton Connexion
        Button loginBtn = new Button("Se connecter");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setOnAction(e -> doLogin());
        this.add(loginBtn, 0, 3, 2, 1);

        // Lien Inscription (Équivalent du bouton transparent Swing)
        Hyperlink goToRegister = new Hyperlink("Pas de compte ? S'inscrire");
        GridPane.setHalignment(goToRegister, HPos.CENTER);
        goToRegister.setOnAction(e -> parentView.showRegister());
        this.add(goToRegister, 0, 4, 2, 1);

        // Label d'erreur
        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        GridPane.setHalignment(errorLabel, HPos.CENTER);
        this.add(errorLabel, 0, 5, 2, 1);
    }

    private void doLogin() {
        String tag = tagField.getText();
        String pass = passField.getText();

        AuthResult result = controller.login(tag, pass);

        if (!result.isSuccess()) {
            errorLabel.setText(result.getMessage());
        } else {
            errorLabel.setText("");
            tagField.clear();
            passField.clear();
        }
    }
}