package Board;

import Player.Worker;
import frontend.HighlightType;
import frontend.SelectedStatus;

/**
 * BoardHighlighter is responsible for managing cell highlights on the board
 * during movement and building phases. It is used to visually indicate valid
 * actions to the player.
 */
public class BoardHighlighter {
    private final Board board;

    /**
     * Constructs a BoardHighlighter for the specified board.
     *
     * @param board the board to operate on
     */
    public BoardHighlighter(Board board) {
        this.board = board;
    }

    /**
     * Highlights all valid movable cells around the given coordinates.
     *
     * @param x row of the center cell
     * @param y column of the center cell
     */
    public void highlightMovable(int x, int y) {
        highlightNeighbors(x, y, HighlightType.MOVE, null);
    }

    /**
     * Highlights all valid buildable cells around the given coordinates.
     *
     * @param x row of the center cell
     * @param y column of the center cell
     */
    public void highlightBuildable(int x, int y) {
        highlightNeighbors(x, y, HighlightType.BUILD, null);
    }

    /**
     * Highlights all valid movable cells, excluding the specified cell.
     *
     * @param excluded the cell to exclude from highlighting
     */
    public void highlightMovableExclude(Cell excluded) {
        Cell selected = board.getSelected();
        if (selected != null) {
            highlightNeighbors(selected.getRow(), selected.getCol(), HighlightType.MOVE, excluded);
        }
    }

    /**
     * Highlights all valid buildable cells, excluding the specified cell.
     *
     * @param excluded the cell to exclude from highlighting
     */
    public void highlightBuildableExclude(Cell excluded) {
        Cell selected = board.getSelected();
        if (selected != null) {
            highlightNeighbors(selected.getRow(), selected.getCol(), HighlightType.BUILD, excluded);
        }
    }

    /**
     * Clears all highlighting and selection markings from the board.
     */
    public void clearMarkings() {
        for (Cell cell : board.getAllCells()) {
            cell.setStatus(SelectedStatus.NONE);
            cell.setHighlightType(HighlightType.NONE);
        }
    }

    /**
     * Highlights neighbor cells around the center cell for the specified action type.
     *
     * @param centerX  row of the center cell
     * @param centerY  column of the center cell
     * @param type     the type of highlight (MOVE or BUILD)
     * @param excluded a cell to exclude from highlighting (nullable)
     */
    private void highlightNeighbors(int centerX, int centerY, HighlightType type, Cell excluded) {
        Cell center = board.getCell(centerX, centerY);
        if (center == null || center.getOccupiedBy() == null) return;

        center.setStatus(SelectedStatus.SELECTED);
        Worker worker = center.getOccupiedBy();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int newX = centerX + dx;
                int newY = centerY + dy;
                Cell target = board.getCell(newX, newY);
                if (target == null || target == excluded) continue;

                boolean valid = switch (type) {
                    case MOVE -> worker.canMoveTo(target);
                    case BUILD -> worker.canBuildOn(target);
                    default -> false;
                };

                if (valid) {
                    target.setStatus(SelectedStatus.HIGHLIGHTED);
                    target.setHighlightType(type);
                } else {
                    target.setStatus(SelectedStatus.NONE);
                    target.setHighlightType(HighlightType.NONE);
                }
            }
        }

        if (excluded != null) {
            excluded.setStatus(SelectedStatus.NONE);
            excluded.setHighlightType(HighlightType.NONE);
        }
    }
}