package frontend;

/**
 * Interface for listening to cell click events on the game board.
 */
public interface CellClickListener {
    void onCellClicked(int row, int col);
}
