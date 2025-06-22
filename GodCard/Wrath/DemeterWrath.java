package GodCard.Wrath;

import Board.Board;
import Board.Cell;
import Player.Player;

import java.util.List;

/**
 * Demeter's Wrath effect: Collapse (lower) the level of selected cells by 1.
 */
public class DemeterWrath implements WrathEffect {

    /**
     * Applies the wrath effect by decreasing the tower level of each selected cell by 1,
     * unless it is already at ground level.
     *
     * @param board   The current game board.
     * @param targets The list of selected target cells for wrath.
     * @param owner   The player who triggered the wrath.
     */
    @Override
    public boolean apply(Board board, List<Cell> targets, Player owner) {
        if (targets.size() != 3) {
            System.out.println("Demeter Wrath: You must select exactly 3 target cells.");
            return false;
        }

        boolean collapsedAny = false;

        for (Cell cell : targets) {
            if (cell.getLevel() > 0) {
                cell.setLevel(cell.getLevel() - 1);
                collapsedAny = true;
            }
        }

        if (collapsedAny) {
            System.out.println("Demeter Wrath: Collapsed selected cells by -1 level.");
            return true;
        } else {
            System.out.println("Demeter Wrath: All selected buildings are already at ground level.");
            return false;
        }
    }

}
