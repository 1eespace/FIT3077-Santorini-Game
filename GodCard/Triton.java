package GodCard;

import Board.Board;
import Board.BoardHighlighter;
import Board.Cell;
import Player.Player;
import Player.Worker;
import frontend.SelectedStatus;

/**
 * Triton is a GodCard that grants the player the ability to move again
 * if their worker ends a move on a perimeter cell.
 * This class also supports a Wrath ability that allows the player to push an opponent's worker.
 */
public class Triton extends GodCard {

    private boolean awaitingExtraMove = false;

    public Triton() {
        super(PowerPhase.MOVE, GodName.TRITON);
    }

    /**
     * Determines whether Triton's god power is available.
     * It is available only if the worker's last move ended on a perimeter cell.
     *
     * @param board  the game board
     * @param player the current player
     * @return true if the power can be used, false otherwise
     */
    @Override
    public boolean availableGodPower(Board board, Player player) {
        Cell selected = board.getSelected();
        return selected != null && board.isPerimeter(selected);
    }

    /**
     * Not used – Triton relies on the overload that accepts BoardHighlighter.
     */
    @Override
    public void usingGodPower(Board board, Player player) {
        // fallback - not used in Triton
    }

    /**
     * Called when the player chooses to use Triton's god power.
     * Highlights movable positions if the worker is on the perimeter.
     *
     * @param board      the game board
     * @param player     the current player
     * @param highlighter the board highlighter for showing move options
     */
    @Override
    public void usingGodPower(Board board, Player player, BoardHighlighter highlighter) {
        Cell selected = board.getSelected();
        if (selected != null && board.isPerimeter(selected)) {
            awaitingExtraMove = true;
            highlighter.highlightMovable(selected.getRow(), selected.getCol());
            System.out.println("Triton: You may move again (landed on perimeter).\n");
        } else {
            awaitingExtraMove = false;
            System.out.println("Triton: No additional move (not on perimeter).\n");
        }
    }

    /**
     * Executes the extra move logic when god power is active.
     * Allows repeat moves as long as the worker lands on the perimeter.
     *
     * @param board  the game board
     * @param player the current player
     * @param target the cell the worker is attempting to move to
     * @return true if the move was successful, false otherwise
     */
    @Override
    public boolean performExtraAction(Board board, Player player, Cell target) {
        if (!awaitingExtraMove) return false;

        Cell current = board.getSelected();
        if (current == null) return false;

        Worker worker = current.getOccupiedBy();
        if (worker == null) return false;

        if (worker.canMoveTo(target)) {
            boolean reachedLevel3 = worker.move(target);
            board.setLastMovedCell(target);

            BoardHighlighter highlighter = new BoardHighlighter(board);

            if (reachedLevel3) {
                highlighter.clearMarkings();
                player.checkAndSetWinner(true);
                return true;
            }

            if (board.isPerimeter(target)) {
                highlighter.clearMarkings();
                board.getCell(target.getRow(), target.getCol()).setStatus(SelectedStatus.SELECTED);
                highlighter.highlightMovable(target.getRow(), target.getCol());
                System.out.println("Triton: Still on perimeter. You may move again.");
            } else {
                awaitingExtraMove = false;
                System.out.println("Triton: Moved off perimeter. Power ends.\n");
            }
            return true;
        }

        System.out.println("Triton: Invalid move.");
        return false;
    }

    /**
     * Indicates that Triton allows repeat moves during the MOVE phase.
     *
     * @return true always
     */
    @Override
    public boolean isRepeatableMoveGod() {
        return true;
    }

    /**
     * Checks if the Wrath power can be used.
     *
     * @param board  the game board
     * @param player the current player
     * @return true if the wrath power has not yet been used
     */
    /// Extension 3: Wrath Power
    @Override
    public boolean canUseGodWrath(Board board, Player player) {
        return !godWrathUsed;
    }

    /**
     * Activates Triton's Wrath power, allowing the player to push an opponent’s worker.
     *
     * @param board  the game board
     * @param player the current player
     */
    @Override
    public void useGodWrath(Board board, Player player) {
        if (godWrathUsed) return;

        godWrathUsed = true;
        System.out.println("Triton Wrath: Water-storm activated. Select a worker to push!\n");
        board.setWrathSelectionMode(true, player, 1);
    }
}
