package GodCard.Wrath;

import Board.Board;
import Board.Cell;
import Player.Player;
import java.util.List;

/**
 * WrathEffect is a strategy interface for applying special god wrath effects in the game.
 * Each god that supports a wrath ability should implement this interface with custom logic.
 */
public interface WrathEffect {

    /**
     * Applies the god-specific wrath effect to the selected target cells.
     *
     * @param board   The current game board where the effect will take place.
     * @param targets The list of cells selected by the player during wrath mode.
     * @param owner   The player who activated the wrath ability.
     */
    boolean apply(Board board, List<Cell> targets, Player owner);
}
