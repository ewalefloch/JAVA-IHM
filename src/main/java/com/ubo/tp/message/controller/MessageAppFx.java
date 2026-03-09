package main.java.com.ubo.tp.message.controller;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.MainPanelViewFx;
import main.java.com.ubo.tp.message.ihm.MessageAppMainViewFx;
import main.java.com.ubo.tp.message.ihm.register.fx.AuthViewFx;

import java.io.File;
import java.util.Set;

public class MessageAppFx implements ISessionObserver {

	protected DataManager mDataManager;
	protected MessageAppMainViewFx mMainView;
	protected Session session;
	protected LoginController loginController;
	protected AuthViewFx mAuthView;
	protected MainPanelViewFx mainPanelView;

	public MessageAppFx(DataManager dataManager) {
		this.mDataManager = dataManager;
		this.session = new Session();
		// On passe "this" pour que la vue puisse appeler les actions du menu (logout, etc.)
		this.mMainView = new MessageAppMainViewFx(new MenuActionController(dataManager, session));
	}

	public void init() {
		this.session.addObserver(this);
		this.initDirectory();
		this.initViews();

		this.mMainView.setContentPane(mAuthView);
		this.mMainView.show(); // Afficher la fenêtre JavaFX
	}

	protected void initViews() {
		this.loginController = new LoginController(mDataManager, session);
		this.mAuthView = new AuthViewFx(loginController);
	}

	protected void initDirectory() {
		// Remplacement de JFileChooser
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Sélectionnez le répertoire d'échange");

		boolean isValid = false;

		while (!isValid) {
			// Ouvre la boîte de dialogue (on lui donne la fenêtre parente)
			File selectedDirectory = chooser.showDialog(mMainView);

			if (selectedDirectory != null) {
				if (isValidExchangeDirectory(selectedDirectory)) {
					this.initDirectory(selectedDirectory.getAbsolutePath());
					isValid = true;
				} else {
					// Remplacement de JOptionPane par Alert
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Erreur");
					alert.setHeaderText(null);
					alert.setContentText("Le répertoire sélectionné est invalide (lecture/écriture impossible).");
					alert.showAndWait();
				}
			} else {
				System.exit(0);
			}
		}
	}

	protected boolean isValidExchangeDirectory(File directory) {
		return directory != null && directory.exists() && directory.isDirectory() && directory.canRead() && directory.canWrite();
	}

	protected void initDirectory(String directoryPath) {
		mDataManager.setExchangeDirectory(directoryPath);
	}

	@Override
	public void notifyLogin(User connectedUser) {
		this.mainPanelView = new MainPanelViewFx(new MainPanelControllerFx(mDataManager, session));

		this.mMainView.setTitle("MessageApp - " + connectedUser.getUserTag());
		this.mMainView.setContentPane(mainPanelView);
	}

	@Override
	public void notifyLogout() {
		this.mainPanelView = null;

		this.mMainView.setTitle("MessageApp - Connexion");
		this.mMainView.setContentPane(mAuthView);

		mAuthView.showLogin();
	}
}