package GodCard.Wrath;

import Board.Board;
import Board.Cell;
import Player.Player;

import java.util.List;

/**
 * Artemis's Wrath effect: Permanently seals selected cells so they can no longer be built on or occupied.
 * Can typically target up to 3 different unoccupied cells.
 */
public class ArtemisWrath implements WrathEffect {

    /**
     * Applies the wrath effect by permanently sealing each unoccupied selected cell.
     * Sealing a cell prevents any future interaction (move/build) with that cell.
     *
     * @param board   The current game board.
     * @param targets The list of wrath-selected target cells.
     * @param owner   The player who activated the wrath effect.
     */
    @Override
    public boolean apply(Board board, List<Cell> targets, Player owner) {
        if (targets.size() != 3) {
            System.out.println("Artemis Wrath: You must select exactly 3 unoccupied cells.");
            return false;
        }

        boolean sealedAny = false;

        for (Cell cell : targets) {
            if (!cell.isOccupied() && !cell.isSealed()) {
                cell.sealPermanently();
                sealedAny = true;
            }
        }

        if (sealedAny) {
            System.out.println("Artemis Wrath: Sealed selected cells permanently.");
            return true;
        } else {
            System.out.println("Artemis Wrath: No valid cells to seal.");
            return false;
        }
    }
}
