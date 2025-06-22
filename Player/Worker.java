package Player;
import Board.Board;
import Board.Cell;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static GodCard.GodName.TRITON;


/**
 * Represents a worker controlled by a player.
 * A worker can move between cells on the board.
 */
public class Worker {
    final private Player owner;      // The player who owns this worker
    private Cell position;           // The current position of the worker on the board
    final private int id;            // Unique ID (0 or 1) to distinguish between two workers

    /**
     * Constructs a worker with an owner and a unique ID.
     * @param owner The player who owns this worker.
     * @param id The unique ID of the worker (typically 0 or 1).
     */
    public Worker(Player owner, int id) {
        this.owner = owner;
        this.id = id;
        this.position = null;  // Initially not placed on any cell
    }

    public static void placeMultipleRandomly(Board board, List<Player> players) {
        List<Point> available = new ArrayList<>();

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Cell cell = board.getCell(r, c);
                if (!cell.isOccupied() && !cell.getBlock().hasDome()) {
                    available.add(new Point(r, c));
                }
            }
        }

        Collections.shuffle(available);

        for (int i = 0; i < 4; i++) {
            Point p = available.get(i);
            Player owner = players.get(i < 2 ? 0 : 1);
            int workerId = i % 2;

            Worker w = new Worker(owner, workerId);
            w.move(board.getCell(p.x, p.y));

            System.out.println(owner.getName() + " placed Worker " + (workerId + 1) + " at (" + p.x + "," + p.y + ")");
        }
    }

    /**
     * Moves the worker to a new cell.
     * Updates both the previous cell and the new cell's occupancy.
     * @param newPosition The target cell to move to.
     */
    public boolean move(Cell newPosition) {
        if (this.getPosition() != null) {
            this.getPosition().setOccupiedBy(null);
        }
        this.setPosition(newPosition);
        newPosition.setOccupiedBy(this);
        return newPosition.getBlock().getLevel() == 3;
    }

    public Cell getPosition() {
        return this.position;
    }

    public void setPosition(Cell position) {
        this.position = position;
    }

    /// //////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Checks whether this worker can move to the given cell.
     * Conditions: not sealed, not flooded (unless Triton), unoccupied, no dome, height difference ≤ 1.
     */
    public boolean canMoveTo(Cell newPosition) {
        boolean isTriton = this.owner.getGod().getGodName() == TRITON;
        return !newPosition.isSealed() &&
                (isTriton || !newPosition.isFlooded()) &&
                newPosition.getOccupiedBy() == null &&
                !newPosition.getBlock().hasDome() &&
                newPosition.getBlock().getLevel() <= this.position.getBlock().getLevel() + 1;
    }

    /**
     * Checks whether this worker can build on the given cell.
     * Conditions:
     * - Not sealed
     * - Not flooded (even Triton cannot build here)
     * - Unoccupied
     * - No dome
     */
    public boolean canBuildOn(Cell newPosition) {
        return !newPosition.isSealed() &&
                !newPosition.isFlooded() &&
                newPosition.getOccupiedBy() == null &&
                !newPosition.getBlock().hasDome();
    }
    /// //////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets the player who owns this worker.
     * @return The Player who owns the worker.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Gets the worker's unique ID.
     * @return The ID of the worker (0 or 1), or -1 if unspecified.
     */
    public int getId() {
        return id;
    }

    
}
