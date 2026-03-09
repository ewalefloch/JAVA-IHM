package main.java.com.ubo.tp.message.ihm.user;

import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.common.AbstractCellView;

import javax.swing.*;
import java.awt.*;

public class UserCellView extends AbstractCellView<User> {

    private JLabel nameLabel;
    private JLabel tagLabel;
    private JLabel statusIndicator;

    public UserCellView(User user) {
        super(user);
    }

    @Override
    protected void buildContent() {
        // Indicateur de statut (pastille colorée)
        statusIndicator = new JLabel();
        statusIndicator.setPreferredSize(new Dimension(12, 12));
        statusIndicator.setOpaque(true);
        updateStatusIndicator();

        // Panel pour les informations utilisateur
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // Nom de l'utilisateur
        nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Tag de l'utilisateur
        tagLabel = new JLabel("@" + item.getUserTag());
        tagLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        tagLabel.setForeground(Color.GRAY);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(tagLabel);

        add(statusIndicator, BorderLayout.WEST);
        add(infoPanel, BorderLayout.CENTER);
    }

    private void updateStatusIndicator() {
        //SRS-MAP-CHN-010
        if (item.isOnline()) {
            statusIndicator.setBackground(new Color(67, 181, 129));
            statusIndicator.setToolTipText("En ligne");
        } else {
            statusIndicator.setBackground(Color.LIGHT_GRAY);
            statusIndicator.setToolTipText("Hors ligne");
        }
    }

    @Override
    public void refresh() {
        nameLabel.setText(item.getName());
        tagLabel.setText("@" + item.getUserTag());
        updateStatusIndicator();
        revalidate();
        repaint();
    }
}