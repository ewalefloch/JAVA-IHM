package com.ubo.tp.message.ihm.message.fx;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.common.fx.AbstractCellViewFx;

public class MessageCellViewFx extends AbstractCellViewFx<Message> {

    public MessageCellViewFx(Message item) {
        super(item);
    }

    @Override
    protected void buildContent() {
        // Conteneur vertical pour l'auteur et le texte
        VBox content = new VBox(5);
        content.setAlignment(Pos.TOP_LEFT);

        // Label de l'auteur (Gras)
        Label authorLabel = new Label(item.getSender().getName() + " :");
        authorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        // Label du texte du message (Gère le retour à la ligne automatique)
        Label messageText = new Label(item.getText());
        messageText.setWrapText(true);
        messageText.setFont(Font.font("Arial", 13));

        // S'assurer que le texte prend toute la largeur disponible pour le wrap
        messageText.setMaxWidth(1000);

        content.getChildren().addAll(authorLabel, messageText);

        // On place le tout au centre de notre BorderPane (hérité de AbstractCellViewFx)
        this.setCenter(content);
    }

    @Override
    public void refresh() {
        // Pas de logique spécifique ici pour l'instant
    }
}