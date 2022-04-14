package fr.pixeldeecran.pixellib.game.utils;

import fr.pixeldeecran.pixellib.game.PGame;

public interface GameFunction<G extends PGame<?, ?>, T, R> {

    R apply(G game, T other);
}
