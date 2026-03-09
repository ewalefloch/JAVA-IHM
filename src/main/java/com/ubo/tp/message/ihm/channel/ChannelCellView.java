package main.java.com.ubo.tp.message.ihm.channel;

import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.ihm.common.AbstractCellView;

import javax.swing.*;
import java.awt.*;

public class ChannelCellView extends AbstractCellView<Channel> {

    private JLabel nameLabel;
    private JLabel infoLabel;

    public ChannelCellView(Channel channel) {
        super(channel);
    }

    @Override
    protected void buildContent() {
        // Icône du canal
        JLabel iconLabel = new JLabel("#");
        iconLabel.setFont(new Font("Arial", Font.BOLD, 18));
        iconLabel.setForeground(new Color(88, 101, 242));

        // Panel pour les informations du canal
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // Nom du canal (utilisation de 'item' venant de AbstractCellView)
        nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Informations supplémentaires
        int userCount = item.getUsers().size();
        String creatorTag = item.getCreator().getUserTag();
        infoLabel = new JLabel(userCount + " membre(s) • Créé par @" + creatorTag);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        infoLabel.setForeground(Color.GRAY);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(infoLabel);

        add(iconLabel, BorderLayout.WEST);
        add(infoPanel, BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        int userCount = item.getUsers().size();
        String creatorTag = item.getCreator().getUserTag();
        infoLabel.setText(userCount + " membre(s) • Créé par @" + creatorTag);
        revalidate();
        repaint();
    }
}