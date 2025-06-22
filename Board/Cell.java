package Board;

import Block.Block;
import GodCard.Wrath.WrathStatus;
import Player.Worker;
import frontend.HighlightType;
import frontend.SelectedStatus;

/**
 * Abstract base class representing a single cell on the game board.
 * Each cell holds a block and may be occupied by a worker.
 */
public abstract class Cell {
    protected Block block;
    private Worker occupiedBy;
    private final int row;
    private final int col;
    private SelectedStatus selectStatus;
    private HighlightType highlightType = HighlightType.NONE;
    private final WrathStatus wrathStatus = new WrathStatus();

    /**
     * Constructs a Cell at the specified (row, col) position.
     * Initializes with a standard block and no worker.
     *
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     */
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.occupiedBy = null;
        this.block = null;
        this.selectStatus = SelectedStatus.NONE;
    }

    /** @return the row index of this cell */
    public int getRow() { return this.row; }

    /** @return the column index of this cell */
    public int getCol() { return this.col; }

    /** @return the block associated with this cell */
    public Block getBlock() { return this.block; }

    /**
     * @return the worker occupying this cell, or null if unoccupied
     */
    public Worker getOccupiedBy() { return this.occupiedBy; }

    /**
     * Sets or clears the worker occupying this cell.
     *
     * @param worker the worker to set, or null to clear
     */
    public void setOccupiedBy(Worker worker) { this.occupiedBy = worker; }

    /** @return true if the cell is currently occupied by a worker */
    public boolean isOccupied() { return occupiedBy != null; }

    /** @return the level of the block in this cell */
    public int getLevel() { return block.getLevel(); }

    /**
     * Sets the level of the block in this cell.
     *
     * @param level the new level to assign
     */
    public void setLevel(int level) { block.setLevel(level); }

    /** @return the selected status of the cell */
    public SelectedStatus getStatus() { return this.selectStatus; }

    /**
     * Sets the selected status of the cell.
     *
     * @param status the status to be applied
     */
    public void setStatus(SelectedStatus status) { this.selectStatus = status; }

    /** @return the highlight type applied to the cell */
    public HighlightType getHighlightType() { return this.highlightType; }

    /**
     * Applies a highlight type to this cell.
     *
     * @param type the highlight type to set
     */
    public void setHighlightType(HighlightType type) { this.highlightType = type; }

    /**
     * Builds a level on the block in this cell.
     */
    public void build() {
        if (block != null) block.build();
    }

    /**
     * @return whether the cell is buildable according to game rules
     */
    public abstract boolean canBuild();

    /// ///////////////////////////////////////////////////////////////////////////////////

    /** Seals this cell permanently */
    public void sealPermanently() { wrathStatus.seal(); }

    /** @return true if the cell is sealed */
    public boolean isSealed() { return wrathStatus.isSealed(); }

    /** Floods this cell permanently */
    public void flood() { wrathStatus.flood(); }

    /** @return true if the cell is flooded */
    public boolean isFlooded() { return wrathStatus.isFlooded(); }

    /**
     * Marks this cell as wrath-selected.
     *
     * @param selected true to mark as selected
     */
    public void setWrathSelected(boolean selected) { wrathStatus.setWrathSelected(selected); }

    /** @return true if this cell is selected for wrath targeting */
    public boolean isWrathSelected() { return wrathStatus.isWrathSelected(); }
}
