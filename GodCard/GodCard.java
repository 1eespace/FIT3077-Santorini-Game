package GodCard;

import Board.Cell;
import Board.Board;
import Board.BoardHighlighter;
import Player.Player;

/**
 * Abstract base class representing a God with special powers.
 */
public abstract class GodCard {
    protected final GodName godName;
    private final PowerPhase powerPhase;

    public GodCard(PowerPhase powerPhase, GodName godName) {
        this.powerPhase = powerPhase;
        this.godName = godName;
    }

    /**
     * Gets the display name of the god, e.g., "Triton".
     */
    public String getName() {
        // Convert enum name (e.g., TRITON) to "Triton"
        String raw = godName.name().toLowerCase();
        return raw.substring(0, 1).toUpperCase() + raw.substring(1);
    }

    public GodName getGodName() {
        return godName;
    }

    public PowerPhase getPowerPhase() {
        return this.powerPhase;
    }

    /**
     * Determines if the player can use the god's power at this time.
     */
    public abstract boolean availableGodPower(Board board, Player player);

    /**
     * Applies the god's special power effect to the game.
     */
    public abstract void usingGodPower(Board board, Player player);

    /**
     * Overload for MOVE phase gods that require highlighting UI.
     * Subclasses may override if needed.
     */
    public void usingGodPower(Board board, Player player, BoardHighlighter highlighter) {
        usingGodPower(board, player); // fallback to default
    }

    /**
     * Performs the special action enabled by the god (e.g., extra move/build).
     * Returns true if action was successful.
     */
    public boolean performExtraAction(Board board, Player player, Cell target) {
        return false;
    }

    /**
     * Indicates whether the god has repeatable move ability (e.g., Triton).
     */
    public boolean isRepeatableMoveGod() {
        return false;
    }

    /// Extension 3: Wrath Power
    protected boolean godWrathUsed = false;
    public abstract boolean canUseGodWrath(Board board, Player player);

    public abstract void useGodWrath(Board board, Player player);
}
