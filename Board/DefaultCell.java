package Board;

import Block.StandardBlock;

/**
 * Represents a standard, buildable, and movable cell on the game board.
 * Default cells allow normal moves and construction unless occupied or domed.
 */
public class DefaultCell extends Cell {

    /**
     * Constructs a DefaultCell at the specified (row, col) position.
     *
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     */
    public DefaultCell(int row, int col) {
        super(row, col);
        this.block = new StandardBlock();
    }

    /**
     * Determines if a building can be constructed on this cell.
     * Construction is allowed if the cell is not occupied and has no dome.
     *
     * @return true if the cell is not occupied and has no dome.
     */
    @Override
    public boolean canBuild() {
        return !isOccupied() && !getBlock().hasDome();
    }
}
