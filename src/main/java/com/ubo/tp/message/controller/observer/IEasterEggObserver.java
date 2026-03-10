package com.ubo.tp.message.controller.observer;

public interface IEasterEggObserver {
    default void onEasterEggTriggered(String command) {}
}
