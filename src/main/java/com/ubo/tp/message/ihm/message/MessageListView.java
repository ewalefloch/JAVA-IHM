package main.java.com.ubo.tp.message.ihm.message;

import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.ihm.common.AbstractListView;

import javax.swing.*;
import java.util.List;

public class MessageListView extends AbstractListView<Message>{

    public MessageListView() {
        super("Discussion");
    }

    @Override
    protected JPanel createCell(Message item) {
        return new MessageCellView(item);
    }

    @Override
    protected String getEmptyMessage() {
        return "Aucun message dans ce canal.";
    }

    @Override
    protected int getCellHeight() {
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
