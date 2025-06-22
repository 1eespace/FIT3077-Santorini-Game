package GodCard.Wrath;

import Board.Board;
import Board.Cell;
import Player.Player;
import Player.Worker;
import frontend.HighlightType;
import frontend.SelectedStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Triton's Wrath effect:
 * Pushes an opponent's worker from a selected cell
 * and floods the original cell and up to two adjacent cells.
 */
public class TritonWrath implements WrathEffect {

    /** 8-direction vectors for adjacent cell checking */
    private static final int[][] DIRECTIONS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            { 0, -1},          { 0, 1},
            { 1, -1}, { 1, 0}, { 1, 1}
    };

    /**
     * Applies Triton's wrath effect.
     * Attempts to push an opponent's worker
     * and flood the cell it was pushed from and nearby unoccupied cells.
     *
     * @param board   The current game board.
     * @param targets The list of wrath-selected cells (uses the first).
     * @param owner   The player who triggered the wrath.
     * @return true if the wrath was successfully applied; false otherwise.
     */
    @Override
    public boolean apply(Board board, List<Cell> targets, Player owner) {
        Cell target = targets.get(0);
        Worker worker = target.getOccupiedBy();

        // Must target an enemy worker
        if (worker == null || worker.getOwner() == owner) {
            System.out.println("Invalid target");
            return false;
        }

        // Attempt to push the worker
        Cell floodedCell = tryPushWorker(board, target, worker);
        if (floodedCell == null) {
            System.out.println("Push failed");
            targets.clear();
            target.setWrathSelected(false);
            return false;
        }

        floodAfterPush(board, floodedCell);
        return true;
    }

    /**
     * Attempts to push a worker to a valid adjacent cell.
     *
     * @param board  The board instance.
     * @param from   The current cell containing the worker.
     * @param worker The worker to push.
     * @return The original cell if push succeeded; null otherwise.
     */
    private Cell tryPushWorker(Board board, Cell from, Worker worker) {
        for (int[] dir : DIRECTIONS) {
            int destRow = from.getRow() + dir[0];
            int destCol = from.getCol() + dir[1];

            if (!board.isValidPosition(destRow, destCol)) continue;

            Cell dest = board.getCell(destRow, destCol);
            if (isValidPushDestination(dest, from)) {
                worker.move(dest); // Perform push
                dest.setHighlightType(HighlightType.NONE);
                dest.setStatus(SelectedStatus.NONE);
                return from;

            }
        }
        return null;
    }

    /**
     * Checks if a cell is a valid push destination from a given source cell.
     *
     * @param dest The destination cell.
     * @param from The original cell where the worker is currently standing.
     * @return true if the destination is valid; false otherwise.
     */
    private boolean isValidPushDestination(Cell dest, Cell from) {
        return !dest.isOccupied()
                && !dest.getBlock().hasDome()
                && !dest.isFlooded()
                && !dest.isSealed()
                && dest.getBlock().getLevel() <= from.getBlock().getLevel();
    }

    /**
     * Floods the original pushed-from cell and up to two nearby unoccupied cells.
     *
     * @param board  The board instance.
     * @param center The cell the worker was pushed from.
     */
    private void floodAfterPush(Board board, Cell center) {
        center.flood(); // Always flood the center cell
        center.sealPermanently();  // The original pushed-from cell sealPermanently due to Flooded

        List<Cell> floodCandidates = new ArrayList<>();
        for (int[] dir : DIRECTIONS) {
            int r = center.getRow() + dir[0];
            int c = center.getCol() + dir[1];

            if (!board.isValidPosition(r, c)) continue;

            Cell neighbor = board.getCell(r, c);
            if (!neighbor.isOccupied() && !neighbor.isFlooded()) {
                floodCandidates.add(neighbor);
            }
        }

        Collections.shuffle(floodCandidates);
        // 2 random cells
        int floodCount = Math.min(2, floodCandidates.size());
        for (int i = 0; i < floodCount; i++) {
            Cell floodedCell = floodCandidates.get(i);
            floodedCell.flood();

            if (floodedCell.getHighlightType() == HighlightType.BUILD) {
                floodedCell.setHighlightType(HighlightType.NONE);
            }
        }

        System.out.println("Triton Wrath: Flooded " + (1 + floodCount) + " cells.");
    }
}
