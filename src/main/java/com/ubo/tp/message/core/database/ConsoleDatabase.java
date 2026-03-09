package main.java.com.ubo.tp.message.core.database;

import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

public class ConsoleDatabase implements IDatabaseObserver {
    @Override
    public void notifyMessageAdded(Message addedMessage) {
        System.out.println("Nouveau message ajouté : " + addedMessage.toString());
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        System.out.println("Message supprimé : " + deletedMessage.toString());
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        System.out.println("Message modifié : " + modifiedMessage.toString());
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        System.out.println("User ajouté : " + addedUser.toString());
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        System.out.println("User supprimé : " + deletedUser.toString());

    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        System.out.println("User modifié : " + modifiedUser.toString());
    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {
        System.out.println("Channel ajouté : " + addedChannel.toString());
    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        System.out.println("Channel supprimé : " + deletedChannel.toString());
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        System.out.println("Channel modifié : " + modifiedChannel.toString());
    }
}
