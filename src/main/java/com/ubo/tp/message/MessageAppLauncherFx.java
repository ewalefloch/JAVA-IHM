package com.ubo.tp.message;

import javafx.application.Application;
import javafx.stage.Stage;
import com.ubo.tp.message.controller.MessageAppFx;
import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.database.ConsoleDatabase;
import com.ubo.tp.message.core.database.Database;
import com.ubo.tp.message.core.database.EntityManager;

public class MessageAppLauncherFx extends Application {

	protected static final boolean IS_MOCK_ENABLED = false;

	public static void main(String[] args) {
		Application.launch(MessageAppLauncherFx.class, args);
	}

	@Override
	public void start(Stage primaryStage) {
		Database database = new Database();
		EntityManager entityManager = new EntityManager(database);
		DataManager dataManager = new DataManager(database, entityManager);

		ConsoleDatabase consoleDatabase = new ConsoleDatabase();

		dataManager.addObserver(consoleDatabase);

		MessageAppFx messageApp = new MessageAppFx(dataManager);
		messageApp.init();
	}
}