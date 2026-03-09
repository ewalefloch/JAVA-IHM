package main.java.com.ubo.tp.message.ihm.register.fx;

import javafx.animation.PauseTransition;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import main.java.com.ubo.tp.message.controller.LoginController;
import main.java.com.ubo.tp.message.datamodel.AuthResult;

public class RegisterFormFx extends GridPane {
    private final LoginController controller;
    private final AuthViewFx parentView;

    private TextField tagField, nameField;
    private PasswordField passField;
    private Label infoLabel;

    public RegisterFormFx(LoginController controller, AuthViewFx parentView) {
        this.controller = controller;
        this.parentView = parentView;
        initGui();
    }

    private void initGui() {
        this.setAlignment(Pos.CENTER);
        this.setHgap(10);
        this.setVgap(15);
        this.setPadding(new Insets(20));

        Label title = new Label("Inscription");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        GridPane.setHalignment(title, HPos.CENTER);
        this.add(title, 0, 0, 2, 1);

        this.add(new Label("Tag :"), 0, 1);
        tagField = new TextField();
        this.add(tagField, 1, 1);

        this.add(new Label("Nom :"), 0, 2);
        nameField = new TextField();
        this.add(nameField, 1, 2);

        this.add(new Label("Mot de passe :"), 0, 3);
        passField = new PasswordField();
        this.add(passField, 1, 3);

        Button regBtn = new Button("Créer mon compte");
        regBtn.setMaxWidth(Double.MAX_VALUE);
        regBtn.setOnAction(e -> doRegister());
        this.add(regBtn, 0, 4, 2, 1);

        Hyperlink backBtn = new Hyperlink("Déjà un compte ? Connexion");
        GridPane.setHalignment(backBtn, HPos.CENTER);
        backBtn.setOnAction(e -> parentView.showLogin());
        this.add(backBtn, 0, 5, 2, 1);

        infoLabel = new Label();
        GridPane.setHalignment(infoLabel, HPos.CENTER);
        this.add(infoLabel, 0, 6, 2, 1);
    }

    private void doRegister() {
        AuthResult result = controller.register(tagField.getText(), nameField.getText(), passField.getText());

        if (result.isSuccess()) {
            infoLabel.setTextFill(Color.GREEN);
            infoLabel.setText("Compte créé ! Veuillez vous connecter.");

            // Équivalent du Timer Swing en JavaFX
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> {
                parentView.showLogin();
                tagField.clear(); nameField.clear(); passField.clear();
                infoLabel.setText("");
            });
            pause.play();
        } else {
            infoLabel.setTextFill(Color.RED);
            infoLabel.setText(result.getMessage());
        }
    }
}