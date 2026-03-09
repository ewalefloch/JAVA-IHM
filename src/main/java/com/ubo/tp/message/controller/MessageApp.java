package main.java.com.ubo.tp.message.controller;

import java.io.File;
import java.util.Set;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.MainPanelView;
import main.java.com.ubo.tp.message.ihm.register.AuthView;
import main.java.com.ubo.tp.message.ihm.MessageAppMainView;

import javax.swing.*;


/**
 * Classe principale de l'application.
 * Gère le cycle de vie de l'application et la coordination entre les composants.
 *
 * @author S.Lucas
 */
public class MessageApp implements ISessionObserver {

	protected DataManager mDataManager;
	protected MessageAppMainView mMainView;
	protected Session session;
	protected LoginController loginController;
	protected AuthView mAuthView;
	protected MainPanelView mainPanelView;

	public MessageApp(DataManager dataManager) {
		this.mDataManager = dataManager;
		this.session = new Session();
		this.mMainView = new MessageAppMainView(new MenuActionController(mDataManager,session));
	}

	public void init() {
		this.initLookAndFeel();
		this.session.addObserver(this);

		this.initDirectory();

		this.initViews();

		this.mMainView.setContentPane(mAuthView);
		mMainView.setVisible(true);
	}

	protected void initLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException |
				 InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

	protected void initViews() {
		this.loginController = new LoginController(mDataManager, session);

		this.mAuthView = new AuthView(loginController);
	}

	protected void initDirectory() {
		JFileChooser chooser = new JFileChooser();

		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("Sélectionnez le répertoire d'échange");

		boolean isValid = false;

		while (!isValid) {
			int returnVal = chooser.showOpenDialog(mMainView);

			if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
				File selectedDirectory = chooser.getSelectedFile();

				if (isValidExchangeDirectory(selectedDirectory)) {
					this.initDirectory(selectedDirectory.getAbsolutePath());
					isValid = true;
				} else {
					JOptionPane.showMessageDialog(mMainView.getFrame(),
							"Le répertoire sélectionné est invalide (lecture/écriture impossible).",
							"Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				System.exit(0);
			}
		}
	}

	protected boolean isValidExchangeDirectory(File directory) {
		return directory != null && directory.exists() && directory.isDirectory() && directory.canRead()
				&& directory.canWrite();
	}

	protected void initDirectory(String directoryPath) {
		mDataManager.setExchangeDirectory(directoryPath);
	}


	@Override
	public void notifyLogin(User connectedUser) {
		this.mainPanelView = new MainPanelView(new MainPanelController(mDataManager, session));

		this.mMainView.setTitle("MessageApp - " + connectedUser.getUserTag());
		this.mMainView.setContentPane(mainPanelView);
		this.mMainView.revalidate();
		this.mMainView.repaint();
	}

	@Override
	public void notifyLogout() {
		this.mainPanelView = null;

		this.mMainView.setTitle("MessageApp - Connexion");
		this.mMainView.setContentPane(mAuthView);
		this.mMainView.revalidate();
		this.mMainView.repaint();

		mAuthView.showLogin();
	}

}