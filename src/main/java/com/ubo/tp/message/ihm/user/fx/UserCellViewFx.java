package main.java.com.ubo.tp.message.ihm.user.fx;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.common.fx.AbstractCellViewFx;

public class UserCellViewFx extends AbstractCellViewFx<User> {

    private Label nameLabel;
    private Label tagLabel;
    private Circle statusIndicator;

    public UserCellViewFx(User user) {
        super(user);
    }

    @Override
    protected void buildContent() {
        // Indicateur de statut (pastille ronde en JavaFX)
        statusIndicator = new Circle(6); // Rayon de 6px
        updateStatusIndicator();

        // Panel pour les informations utilisateur
        VBox infoPanel = new VBox(3); // 3px d'espacement vertical
        infoPanel.setAlignment(Pos.CENTER_LEFT);

        nameLabel = new Label(item.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        tagLabel = new Label("@" + item.getUserTag());
        tagLabel.setFont(Font.font("Arial", 12));
        tagLabel.setTextFill(Color.GRAY);

        infoPanel.getChildren().addAll(nameLabel, tagLabel);

        // Placement dans le BorderPane (hérité de AbstractCellViewFx)
        this.setLeft(statusIndicator);
        this.setCenter(infoPanel);

        // Centrer verticalement la pastille
        BorderPane.setAlignment(statusIndicator, Pos.CENTER);
        // Ajouter un peu de marge entre la pastille et le texte
        BorderPane.setMargin(statusIndicator, new javafx.geometry.Insets(0, 10, 0, 0));
    }

    private void updateStatusIndicator() {
        if (item.isOnline()) {
            statusIndicator.setFill(Color.web("#43B581")); // Vert
            Tooltip.install(statusIndicator, new Tooltip("En ligne"));
        } else {
            statusIndicator.setFill(Color.LIGHTGRAY);
            Tooltip.install(statusIndicator, new Tooltip("Hors ligne"));
        }
    }

    @Override
    public void refresh() {
        nameLabel.setText(item.getName());
        tagLabel.setText("@" + item.getUserTag());
        updateStatusIndicator();
    }
}