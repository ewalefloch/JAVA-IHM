package com.ubo.tp.message.ihm.message.fx;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.util.Duration;

public class EasterEggAnimationFx {
    private EasterEggAnimationFx(){}
    /**
     * Fait trembler un composant (Earthquake)
     */
    public static void playEarthquake(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(-5);
        tt.setToX(5);
        tt.setCycleCount(20);
        tt.setAutoReverse(true);
        tt.setOnFinished(e -> node.setTranslateX(0));
        tt.play();
    }

    /**
     * Retourne l'interface à 180° (Flip)
     */
    public static void playFlip(Node node) {
        RotateTransition rt = new RotateTransition(Duration.seconds(1), node);
        rt.setByAngle(360);
        rt.setInterpolator(Interpolator.EASE_BOTH);
        rt.play();
    }

    /**
     * Simule un effet de fête (Party) en faisant scintiller l'opacité ou les couleurs
     */
    public static void playParty(Node node) {
        // 1. Effet Arc-en-ciel (Modification de la teinte)
        ColorAdjust colorAdjust = new ColorAdjust();
        // On booste un peu la saturation pour un effet plus fluo/techno
        colorAdjust.setSaturation(0.5);
        node.setEffect(colorAdjust);

        // On fait tourner la teinte de -1.0 à 1.0 très rapidement
        Timeline rainbowTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.hueProperty(), -1.0, Interpolator.LINEAR)),
                new KeyFrame(Duration.millis(300), new KeyValue(colorAdjust.hueProperty(), 1.0, Interpolator.LINEAR))
        );
        // 300ms * 33 cycles = environ 10 secondes d'arc-en-ciel
        rainbowTimeline.setCycleCount(33);
        rainbowTimeline.setAutoReverse(false);

        // 2. Effet de Battement de basse (Pulse)
        ScaleTransition beatTransition = new ScaleTransition(Duration.millis(150), node);
        beatTransition.setFromX(1.0);
        beatTransition.setFromY(1.0);
        beatTransition.setToX(1.03); // Zoom léger de 3%
        beatTransition.setToY(1.03);
        // 150ms * 66 = environ 10 secondes de battements
        beatTransition.setCycleCount(66);
        beatTransition.setAutoReverse(true);
        beatTransition.setInterpolator(Interpolator.EASE_BOTH);

        rainbowTimeline.setOnFinished(e -> node.setEffect(null));
        beatTransition.setOnFinished(e -> {
            node.setScaleX(1.0);
            node.setScaleY(1.0);
        });

        rainbowTimeline.play();
        beatTransition.play();
    }
}