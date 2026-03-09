package com.ubo.tp.message.ihm.message.fx;

import javafx.scene.layout.BorderPane;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.common.fx.AbstractListViewFx;

import java.util.List;

public class MessageListViewFx extends AbstractListViewFx<Message> {

    public MessageListViewFx() {
        super("Discussion");
    }

    @Override
    protected BorderPane createCell(Message item) {
        return new MessageCellViewFx(item);
    }

    @Override
    protected String getEmptyMessage() {
        return "Aucun message dans ce canal.";
    }

    @Override
    protected int getCellHeight() {
        // On laisse une hauteur flexible ou assez grande pour le texte
        return 80;
    }

    public void refresh(List<Message> messages) {
        updateList(messages);
    }

    @Override
    protected boolean matchSearch(Message item, String query) {
        return item.getText() != null && item.getText().toLowerCase().contains(query);
    }
}