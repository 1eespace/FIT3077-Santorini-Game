package Board;

public class BoardUtils {
    @FunctionalInterface
    public interface ActionChecker {
        boolean isActionPossible(Cell target);
    }

    public static boolean hasValidAdjacentAction(Cell cell, Board board, ActionChecker checker) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int newRow = cell.getRow() + dx;
                int newCol = cell.getCol() + dy;

                if (board.isValidPosition(newRow, newCol)) {
                    Cell target = board.getCell(newRow, newCol);
                    if (checker.isActionPossible(target)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
