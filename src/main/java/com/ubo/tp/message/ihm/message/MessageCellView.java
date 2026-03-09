package main.java.com.ubo.tp.message.ihm.message;

import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.ihm.common.AbstractCellView;

import javax.swing.*;
import java.awt.*;

public class MessageCellView extends AbstractCellView<Message> {
    public MessageCellView(Message item) {
        super(item);
    }

    @Override
    protected void buildContent() {
        JLabel authorLabel = new JLabel(item.getSender().getName() + " :");
        authorLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JTextArea messageText = new JTextArea(item.getText());
        messageText.setEditable(false);
        messageText.setLineWrap(true);
        messageText.setOpaque(false);

        add(authorLabel, BorderLayout.NORTH);
        add(messageText, BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        revalidate();
        repaint();
    }
}
