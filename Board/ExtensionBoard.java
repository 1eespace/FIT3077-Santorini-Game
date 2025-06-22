package Board;

import GodCard.GodName;
import GodCard.Wrath.*;
import Player.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ExtensionBoard supports wrath mechanics triggered by specific gods.
 * Players can select multiple cells to apply unique wrath effects based on their god.
 */
public class ExtensionBoard extends Board {

    private boolean wrathMode = false;
    private int wrathSelectionsNeeded = 0;
    private final List<Cell> wrathTargets = new ArrayList<>();
    private Player wrathOwner = null;

    /**
     * Mapping of god names to their wrath effect implementations.
     */
    private final Map<GodName, WrathEffect> wrathEffectMap = Map.of(
            GodName.DEMETER, new DemeterWrath(),
            GodName.ARTEMIS, new ArtemisWrath(),
            GodName.TRITON, new TritonWrath()
    );

    /**
     * Constructs an ExtensionBoard with wrath support.
     * @param rows board height
     * @param cols board width
     */
    public ExtensionBoard(int rows, int cols) {
        super(rows, cols);
    }

    /**
     * Enables or disables wrath selection mode.
     * @param enable true to activate wrath mode
     * @param player the player activating wrath
     * @param count number of cells to select
     */
    public void setWrathSelectionMode(boolean enable, Player player, int count) {
        this.wrathMode = enable;
        this.wrathOwner = player;
        this.wrathSelectionsNeeded = count;
        this.wrathTargets.clear();
    }

    /**
     * Returns whether wrath mode is currently active.
     */
    public boolean isWrathMode() {
        return wrathMode;
    }

    /**
     * Handles cell selection during wrath mode.
     * @param cell the selected cell
     */
    @Override
    public void handleWrathCellSelection(Cell cell) {
        if (!validateWrathSelection(cell)) return;

        selectWrathTarget(cell);

        if (wrathTargets.size() >= wrathSelectionsNeeded) {
            boolean success = executeWrathEffect();
            if (success) {
                cleanupWrath();
            } else {
                // Wrath failed → clear selection but keep wrath mode on
                for (Cell c : wrathTargets) {
                    c.setWrathSelected(false);
                }
                wrathTargets.clear();
                System.out.println("Wrath failed. Please select a new target.");
            }
        }
    }

    /**
     * Validates whether the given cell can be selected for wrath.
     * @param cell the cell to validate
     * @return true if valid
     */
    private boolean validateWrathSelection(Cell cell) {
        if (!wrathMode || wrathOwner == null || wrathSelectionsNeeded <= 0) return false;

        if (cell.getBlock().hasDome()) return false;
        if (wrathTargets.contains(cell)) return false;
        if (wrathOwner.getGod().getGodName() == GodName.ARTEMIS && cell.getOccupiedBy() != null) return false;

        return true;
    }

    /**
     * Marks the given cell as wrath-selected.
     * @param cell the selected cell
     */
    private void selectWrathTarget(Cell cell) {
        cell.setWrathSelected(true);
        wrathTargets.add(cell);
        System.out.println("Wrath target selected: (" + cell.getRow() + "," + cell.getCol() + ")");
    }

    /**
     * Executes the wrath effect associated with the current player's god.
     */
    private boolean executeWrathEffect() {
        GodName god = wrathOwner.getGod().getGodName();
        WrathEffect effect = wrathEffectMap.get(god);
        if (effect != null) {
            return effect.apply(this, wrathTargets, wrathOwner);
        } else {
            System.out.println("No wrath effect found for: " + god);
            return false;
        }
    }


    /**
     * Clears wrath state after execution.
     */
    private void cleanupWrath() {
        for (Cell c : wrathTargets) {
            c.setWrathSelected(false);
        }

        wrathMode = false;
        wrathSelectionsNeeded = 0;
        wrathOwner = null;
        wrathTargets.clear();
    }
}
