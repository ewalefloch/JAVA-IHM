package main.java.com.ubo.tp.message;

import javafx.application.Application;
import javafx.stage.Stage;
import main.java.com.ubo.tp.message.controller.MessageAppFx;
import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.ConsoleDatabase;
import main.java.com.ubo.tp.message.core.database.Database;
import main.java.com.ubo.tp.message.core.database.DbConnector;
import main.java.com.ubo.tp.message.core.database.EntityManager;

public class MessageAppLauncherFx extends Application {

	protected static boolean IS_MOCK_ENABLED = false;

	public static void main(String[] args) {
		// La méthode main ne fait plus RIEN d'autre que lancer JavaFX.
		// Cela va initialiser le Toolkit et appeler la méthode start() ci-dessous.
		Application.launch(MessageAppLauncherFx.class, args);
	}

	@Override
	public void start(Stage primaryStage) {
		// --- À partir d'ici, nous sommes sur le bon thread (JavaFX Thread) ! ---
		// On peut donc créer nos données et instancier nos fenêtres Fx sans erreur.

		Database database = new Database();
		EntityManager entityManager = new EntityManager(database);
		DataManager dataManager = new DataManager(database, entityManager);
		DbConnector dbConnector = new DbConnector(database);
		ConsoleDatabase consoleDatabase = new ConsoleDatabase();

		dataManager.addObserver(consoleDatabase);

		MessageAppFx messageApp = new MessageAppFx(dataManager);
		messageApp.init();
	}
}