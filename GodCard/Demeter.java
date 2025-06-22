package GodCard;

import Board.Cell;
import Board.Board;
import Board.BoardHighlighter;
import Player.Player;

/**
 * Represents the god Demeter, who allows the player to build twice per turn,
 * with the constraint that the second build must be on a different space.
 * Also includes a Wrath power to collapse multiple buildings.
 */
public class Demeter extends GodCard {

    private Cell firstBuildCell;
    private boolean awaitingSecondBuild = false;

    /**
     * Constructs a Demeter god instance.
     */
    public Demeter() {
        super(PowerPhase.BUILD, GodName.DEMETER);
    }

    /**
     * Determines if Demeter's god power is available to use.
     * For Demeter, it is always available after a build.
     *
     * @param board  the current game board
     * @param player the current player
     * @return always true for Demeter
     */
    @Override
    public boolean availableGodPower(Board board, Player player) {
        return true;
    }

    /**
     * Not used – Demeter relies on the overload that accepts BoardHighlighter.
     */
    @Override
    public void usingGodPower(Board board, Player player) {
        // fallback - not used in Demeter
    }

    /**
     * Activates Demeter's god power, allowing the player to build a second time
     * on a different space.
     * Highlights valid buildable cells, excluding the first.
     *
     * @param board       the current game board
     * @param player      the current player
     * @param highlighter the board highlighter used for UI
     */
    @Override
    public void usingGodPower(Board board, Player player, BoardHighlighter highlighter) {
        Cell selected = board.getSelected();
        if (selected != null && selected.isOccupied()) {
            this.firstBuildCell = board.getLastBuiltCell();
            this.awaitingSecondBuild = true;
            highlighter.highlightBuildableExclude(firstBuildCell);
            System.out.println("Demeter: Select a different space to build again.");
        } else {
            System.out.println("No worker selected for Demeter's power.");
        }
    }

    /**
     * Performs the second build action if Demeter's power is active.
     *
     * @param board  the game board
     * @param player the current player
     * @param target the cell to build upon
     * @return true if the build is valid and executed, false otherwise
     */
    @Override
    public boolean performExtraAction(Board board, Player player, Cell target) {
        if (!awaitingSecondBuild) return false;

        if (target != null && target != firstBuildCell &&
                target.getOccupiedBy() == null && !target.getBlock().hasDome()) {

            target.build();
            awaitingSecondBuild = false;
            System.out.println("Demeter built successfully on (" + target.getRow() + "," + target.getCol() + ")");
            return true;
        }

        System.out.println("Demeter: Invalid second build.");
        return false;
    }

    /**
     * Checks whether Demeter's Wrath ability is available.
     *
     * @param board  the game board
     * @param player the current player
     * @return true if Wrath has not yet been used
     */
    /// Extension 3: Wrath Power
    @Override
    public boolean canUseGodWrath(Board board, Player player) {
        return !godWrathUsed;
    }

    /**
     * Activates Demeter's Wrath power, allowing the player to select
     * three cells to collapse by one level each.
     *
     * @param board  the game board
     * @param player the current player
     */
    @Override
    public void useGodWrath(Board board, Player player) {
        if (godWrathUsed) return;

        godWrathUsed = true;
        System.out.println("Demeter's Wrath activated. Please select 3 cells to collapse (-1 level each).");
        board.setWrathSelectionMode(true, player, 3);
    }
}
