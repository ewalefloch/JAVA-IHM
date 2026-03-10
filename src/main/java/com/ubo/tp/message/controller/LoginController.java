package com.ubo.tp.message.controller;

import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.session.Session;
import com.ubo.tp.message.datamodel.AuthResult;
import com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur pour la gestion du Login / Inscription.
 */
public class LoginController {

    protected DataManager mDataManager;
    private final Session session;

    public LoginController(DataManager dataManager, Session session) {
        this.mDataManager = dataManager;
        this.session = session;
    }

    /**
     * Point d'entrée pour l'inscription.
     */
    public AuthResult register(String tag, String name, String password) {
        // Validation des données
        AuthResult validation = validateRegistrationData(tag, name, password);
        if (!validation.isSuccess()) {
            return validation;
        }

        // Traitement de la création
        return processRegistration(tag, name, password);
    }

    /**
     * Point d'entrée pour la connexion.
     */
    public AuthResult login(String tag, String password) {
        // Validation des champs
        if (isInputEmpty(tag) || isInputEmpty(password)) {
            return new AuthResult(false, "Champs obligatoires.");
        }

        // Traitement de l'authentification
        return processLogin(tag, password);
    }

    // --- Méthodes de traitement (Logic) ---

    private AuthResult processRegistration(String tag, String name, String password) {
        if (userExists(tag)) {
            return new AuthResult(false, "Ce Tag est déjà pris.");
        }

        User newUser = new User(tag, password, name);
        mDataManager.sendUser(newUser);

        return new AuthResult(true, "création compte réussie");
    }

    private AuthResult processLogin(String tag, String password) {
        User foundUser = findUserByTag(tag);

        if (foundUser != null && foundUser.getUserPassword().equals(password)) {
            completeLoginSession(foundUser);
            return new AuthResult(true, "Connexion réussie");
        }

        return new AuthResult(false, "Identifiant ou mot de passe incorrect.");
    }

    private void completeLoginSession(User user) {
        user.setOnline(true);
        session.connect(user);
        mDataManager.sendUser(user);
    }

    // --- Méthodes de validation et utilitaires ---

    private AuthResult validateRegistrationData(String tag, String name, String password) {
        if (isInputEmpty(tag)) return new AuthResult(false, "Tag obligatoire");
        if (isInputEmpty(name)) return new AuthResult(false, "Nom obligatoire");
        if (isInputEmpty(password)) return new AuthResult(false, "Mot de passe requis");
        return new AuthResult(true, "");
    }

    private boolean isInputEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }

    private boolean userExists(String tag) {
        return findUserByTag(tag) != null;
    }

    private User findUserByTag(String tag) {
        for (User user : mDataManager.getUsers()) {
            if (user.getUserTag().equals(tag)) {
                return user;
            }
        }
        return null;
    }
}