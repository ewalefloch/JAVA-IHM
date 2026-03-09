package main.java.com.ubo.tp.message.ihm.channel;

import main.java.com.ubo.tp.message.controller.ChannelListController;
import main.java.com.ubo.tp.message.controller.observer.IChannelListObserver;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.ihm.common.AbstractListView;

import javax.swing.*;

public class ChannelListView extends AbstractListView<Channel> implements IChannelListObserver {

    private final ChannelListController controller;

    public ChannelListView(ChannelListController controller) {
        super("Canaux");
        this.controller = controller;
        this.controller.addObserver(this);
    }

    @Override
    protected JPanel createCell(Channel item) {
        return new ChannelCellView(item);
    }

    @Override
    protected String getEmptyMessage() {
        return "Aucun canal disponible";
    }

    @Override
    protected int getCellHeight() {
        return 70;
    }

    public void cleanup() {
        controller.removeObserver(this);
    }

    @Override
    public void onChannelListChanged() {
        SwingUtilities.invokeLater(() -> updateList(controller.getChannels()));
    }

    @Override
    protected boolean matchSearch(Channel item, String query) {
        return item.getName() != null && item.getName().toLowerCase().contains(query);
    }
}