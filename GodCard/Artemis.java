package GodCard;

import Board.Cell;
import Board.Board;
import Board.BoardHighlighter;
import Player.Player;
import Player.Worker;

/**
 * Represents the god Artemis who grants a player the ability to move twice during their turn,
 * with the restriction that the second move cannot be to the original space.
 * Also includes a Wrath ability that allows the player to seal selected cells.
 */
public class Artemis extends GodCard {

    private Cell originalCell;
    private boolean awaitingSecondMove = false;

    /**
     * Constructs an Artemis god instance.
     */
    public Artemis() {
        super(PowerPhase.MOVE, GodName.ARTEMIS);
    }

    /**
     * Determines if Artemis' power is available to use.
     *
     * @param board The current board instance.
     * @param player The player attempting to use the god power.
     * @return true if Artemis' power can be used.
     */
    @Override
    public boolean availableGodPower(Board board, Player player) {
        return true;
    }

    /**
     * Activates Artemis' special power (MOVE phase only).
     *
     * @param board The current board instance.
     * @param player The player using the god power.
     */
    @Override
    public void usingGodPower(Board board, Player player) {
        // fallback - not used in Artemis
    }

    /**
     * Activates Artemis' special power with highlighting.
     *
     * @param board The current board instance.
     * @param player The player using the god power.
     * @param highlighter The BoardHighlighter to apply UI effects.
     */
    @Override
    public void usingGodPower(Board board, Player player, BoardHighlighter highlighter) {
        Cell selected = board.getLastMovedCell();

        if (selected != null && !selected.isOccupied()) {
            this.originalCell = selected;
            this.awaitingSecondMove = true;
            highlighter.highlightMovableExclude(originalCell);
            System.out.println("Artemis: Select a different cell to move again.");
        } else {
            System.out.println("No worker selected for Artemis's power.");
        }
    }

    /**
     * Performs the extra action of moving to a new cell.
     * @param board The board instance.
     * @param player The player who moved.
     * @param target The target cell for the extra move.
     * @return true if the move was successful, false otherwise.
     */
    @Override
    public boolean performExtraAction(Board board, Player player, Cell target) {
        if (!awaitingSecondMove) return false;

        Worker worker = board.getSelected().getOccupiedBy();
        if (worker != null && target != originalCell && worker.canMoveTo(target)) {
            boolean reachedLevel3 = worker.move(target);
            player.checkAndSetWinner(reachedLevel3);
            awaitingSecondMove = false;
            return true;
        }

        System.out.println("Artemis: Invalid extra move.");
        return false;
    }

    /**
     * Checks whether Artemis's Wrath ability can be used.
     *
     * @param board  the current board
     * @param player the player attempting to use the Wrath power
     * @return true if Wrath has not yet been used
     */
    /// Extension 3: Wrath Power
    @Override
    public boolean canUseGodWrath(Board board, Player player) {
        return !godWrathUsed;
    }

    /**
     * Activates Artemis's Wrath power, allowing the player to seal 3 cells.
     *
     * @param board  the current board
     * @param player the current player
     */
    @Override
    public void useGodWrath(Board board, Player player) {
        if (godWrathUsed) return;

        godWrathUsed = true;
        System.out.println("Artemis's Wrath activated. Select 3 Cells to seal.");
        board.setWrathSelectionMode(true, player, 3);
    }
}
