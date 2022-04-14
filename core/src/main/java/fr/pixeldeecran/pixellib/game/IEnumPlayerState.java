package fr.pixeldeecran.pixellib.game;

public interface IEnumPlayerState<G extends PGame<?, ?>> {

    PPlayerStateManager<G> getStateManager();
}
