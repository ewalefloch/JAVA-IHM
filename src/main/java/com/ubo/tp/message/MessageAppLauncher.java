package com.ubo.tp.message;

import com.ubo.tp.message.controller.MessageAppFx;
import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.database.Database;
import com.ubo.tp.message.core.database.DbConnector;
import com.ubo.tp.message.core.database.EntityManager;
import com.ubo.tp.message.core.database.ConsoleDatabase;
import com.ubo.tp.message.controller.MessageApp;

/**
 * Classe de lancement de l'application.
 *
 * @author S.Lucas
 */
public class MessageAppLauncher {

	/**
	 * Indique si le mode bouchoné est activé.
	 */
	protected static boolean IS_MOCK_ENABLED = false;


	/**
	 * Launcher.
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		Database database = new Database();

		EntityManager entityManager = new EntityManager(database);

		DataManager dataManager = new DataManager(database, entityManager);

		DbConnector dbConnector = new DbConnector(database);

		ConsoleDatabase consoleDatabase = new ConsoleDatabase();

		dataManager.addObserver(consoleDatabase);

		MessageApp messageApp = new MessageApp(dataManager);
		messageApp.init();

	}
}
