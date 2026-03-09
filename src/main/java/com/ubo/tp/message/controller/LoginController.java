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

    public AuthResult register(String tag, String name, String password) {
        //SRS-MAP-USR-002
        if (tag == null || tag.trim().isEmpty()) return new AuthResult(false,"Tag obligatoire");
        if (name == null || name.trim().isEmpty()) return new AuthResult(false,"Nom obligatoire");
        if (password == null || password.isEmpty()) return new AuthResult(false,"Mot de passe requis");

        //SRS-MAP-USR-003
        if (userExists(tag)) {
            return new AuthResult(false,"Ce Tag est déjà pris.");
        }

        // 3. Création
        User newUser = new User(tag, password, name);
        mDataManager.sendUser(newUser);

        return new AuthResult(true,"création compte réussie"); // Succès
    }

    public AuthResult login(String tag, String password) {
        if (tag == null || tag.trim().isEmpty() || password == null || password.isEmpty()) {
            return new AuthResult(false,"Champs obligatoires.");
        }

        User foundUser = findUserByTag(tag);

        if (foundUser != null && foundUser.getUserPassword().equals(password)) {
            //SRS-MAP-USR-004
            foundUser.setOnline(true);
            session.connect(foundUser);
            mDataManager.sendUser(foundUser);
            return new AuthResult(true,"Connexion réussie");
        } else {
            return new AuthResult(false,"Identifiant ou mot de passe incorrect.");
        }
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