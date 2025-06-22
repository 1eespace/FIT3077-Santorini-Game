package Board;

import Player.Player;
import frontend.SelectedStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the base game board composed of cells.
 * Each board supports cell access, building, and perimeter checks.
 */
public class Board {
    private final int rows;
    private final int cols;
    private final List<Cell> cells = new ArrayList<>();
    private Cell lastBuiltCell = null;
    private Cell lastMovedCell = null;

    /**
     * Constructs a board with the given dimensions.
     * @param rows number of rows
     * @param cols number of columns
     */
    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                cells.add(new DefaultCell(row, col));
            }
        }
    }

    /**
     * @return number of rows in the board
     */
    public int getRows() { return rows; }

    /**
     * @return number of columns in the board
     */
    public int getCols() { return cols; }

    /**
     * Retrieves a cell at the specified coordinates.
     * @param row row index
     * @param col column index
     * @return the cell, or null if out of bounds
     */
    public Cell getCell(int row, int col) {
        return cells.stream()
                .filter(cell -> cell.getRow() == row && cell.getCol() == col)
                .findFirst().orElse(null);
    }

    /**
     * @return all cells on the board
     */
    public List<Cell> getAllCells() {
        return cells;
    }

    /**
     * Checks whether a given position is within board bounds.
     * @param row row index
     * @param col column index
     * @return true if valid
     */
    public boolean isValidPosition(int row, int col) {
        return getCell(row, col) != null;
    }

    /**
     * Attempts to build on the specified cell.
     * @param row row index
     * @param col column index
     */
    public void build(int row, int col) {
        Cell targetCell = getCell(row, col);
        if (targetCell != null && targetCell.canBuild()) {
            targetCell.getBlock().build();
            lastBuiltCell = targetCell;
        }
    }

    /**
     * @return currently selected cell
     */
    public Cell getSelected() {
        return cells.stream()
                .filter(cell -> cell.getStatus() == SelectedStatus.SELECTED)
                .findFirst().orElse(null);
    }

    /**
     * @return the cell last built upon
     */
    public Cell getLastBuiltCell() {
        return lastBuiltCell;
    }

    /**
     * @return the cell last moved to
     */
    public Cell getLastMovedCell() {
        return this.lastMovedCell;
    }

    /**
     * Sets the cell that was last moved to.
     * @param lastMovedCell the moved cell
     */
    public void setLastMovedCell(Cell lastMovedCell) {
        this.lastMovedCell = lastMovedCell;
    }

    /// ////////////////////////////////////////////////////////////////////////////////
    // Extension

    /**
     * Checks if a cell is on the perimeter of the board.
     * @param cell the cell to check
     * @return true if the cell is a perimeter cell
     */
    public boolean isPerimeter(Cell cell) {
        int row = cell.getRow();
        int col = cell.getCol();
        return row == 0 || row == 4 || col == 0 || col == 4;
    }

    /**
     * Stub for wrath handling; overridden in ExtensionBoard.
     * @param cell the selected cell
     */
    public void handleWrathCellSelection(Cell cell) {}

    /**
     * @return false; overridden in ExtensionBoard
     */
    public boolean isWrathMode() { return false; }

    /**
     * Stub for enabling wrath selection; overridden in ExtensionBoard.
     * @param enable whether to enable wrath
     * @param player the wrath owner
     * @param count number of selections
     */
    public void setWrathSelectionMode(boolean enable, Player player, int count) {
        System.out.println("Wrath is not supported on this board.");
    }
}
