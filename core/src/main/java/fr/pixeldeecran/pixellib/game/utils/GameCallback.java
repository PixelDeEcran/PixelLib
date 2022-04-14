package fr.pixeldeecran.pixellib.game.utils;

import fr.pixeldeecran.pixellib.game.PGame;

public interface GameCallback<G extends PGame<?, ?>, T> {

    void run(G game, T other);
}
