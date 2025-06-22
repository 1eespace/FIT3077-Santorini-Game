package GameMode;

import Board.Board;
import Board.ExtensionBoard;
import Board.BoardHighlighter;
import Board.Cell;
import Board.BoardUtils;
import Player.Player;
import Player.Worker;
import GodCard.GodCard;
import GodCard.PowerPhase;
import frontend.SelectedStatus;

import java.awt.*;
import java.util.Random;
import java.util.Vector;

/**
 * Handles two-player game logic, including setup, turn handling, and god power usage.
 */
public class TwoPlayerConfig extends Config {
    private Board board;
    private BoardHighlighter highlighter;
    private boolean moved = false;
    private boolean built = false;
    private boolean godPowerUsedOrSkipped = false;
    private Player[] players;
    private int currentPlayerIndex;

    /**
     * Constructs a TwoPlayerConfig with specified player names and god cards.
     */
    public TwoPlayerConfig(Vector<String> playerNames, Vector<GodCard> gods) {
        super(playerNames, gods);
        this.boardHeight = 5;
        this.boardWidth = 5;
        this.numPlayers = 2;
    }

    /**
     * Sets up the game board, highlighter, and initializes players.
     */
    @Override
    public void setup() {
        this.board = new ExtensionBoard(boardWidth, boardHeight);
        this.highlighter = new BoardHighlighter(board);

        players = new Player[2];
        players[0] = new Player(playerNames.get(0), gods.get(0), Color.BLUE);
        players[1] = new Player(playerNames.get(1), gods.get(1), Color.RED);

        currentPlayerIndex = new Random().nextInt(2);
    }

    @Override
    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

    @Override
    public Player[] getPlayers() {
        return players;
    }

    @Override
    public Board getBoard() {
        return board;
    }

    /**
     * Tries to activate the current player's god power.
     */
    @Override
    public void useGodPower() {
        Player current = getCurrentPlayer();
        GodCard god = current.getGod();

        if (!moved && god.getPowerPhase() == PowerPhase.MOVE) {
            System.out.println("You must move before using god power.");
            return;
        }

        if (!built && god.getPowerPhase() == PowerPhase.BUILD) {
            System.out.println("You must build before using god power.");
            return;
        }

        if (godPowerUsedOrSkipped) {
            System.out.println("God power cannot be activated or skipped.");
            return;
        }

        if (god.availableGodPower(board, current)) {
            god.usingGodPower(board, current, highlighter);
            System.out.println("Use your god power now");

            if (moved && built && godPowerUsedOrSkipped && god.getPowerPhase() == PowerPhase.BUILD) {
                endTurn();
            }
        } else {
            if (god.isRepeatableMoveGod()) {
                System.out.println("Triton can only use god power if your worker moved to a perimeter space.");
            } else {
                System.out.println("God power not available.");
            }
        }
    }

    /**
     * Skips the use of god power and proceeds to the next phase or ends turn.
     */
    @Override
    public void skipGodPower() {
        if (built && !godPowerUsedOrSkipped) {
            highlighter.clearMarkings();
            godPowerUsedOrSkipped = true;
            System.out.println("God power skipped.");

            if (moved && built) {
                endTurn();
                return;
            }
        }

        if (!built && moved && !godPowerUsedOrSkipped) {
            Cell selected = board.getSelected();
            if (selected != null && selected.getOccupiedBy() != null) {
                highlighter.clearMarkings();
                highlighter.highlightBuildable(selected.getRow(), selected.getCol());
            }

            godPowerUsedOrSkipped = true;
        }
    }

    /**
     * Ends the current player's turn if all required actions are complete.
     */
    @Override
    public void endTurn() {
        if (!moved || !built || !godPowerUsedOrSkipped) {
            System.out.println("You must move, build, and use or skip god power before ending the turn.");
            return;
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % 2;
        moved = false;
        built = false;
        godPowerUsedOrSkipped = false;
    }

    @Override
    public Player getWinner() {
        for (Player p : players) {
            if (p.isWinner()) return p;
        }
        return null;
    }

    /**
     * Checks whether the current player has no valid move left.
     *
     * @return true if no valid move found for any worker, false otherwise
     */
    private boolean currentWorkerHasNoMovable() {
        Player current = getCurrentPlayer();

        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                Cell cell = board.getCell(row, col);
                Worker worker = cell.getOccupiedBy();

                if (worker != null && worker.getOwner() == current) {
                    //// Check if the current worker has any valid adjacent cell to move.
                    //// If a valid move is found, the player is not stuck.
                    boolean canMove = BoardUtils.hasValidAdjacentAction(cell, board, worker::canMoveTo);
                    if (canMove) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks if the current player is stuck (no moves or no builds).
     * If stuck, sets the opponent as the winner.
     *
     * @return true if the player was stuck and the game ended, false otherwise
     */
    private boolean checkIfCurrentPlayerStuck() {
        if (!moved && currentWorkerHasNoMovable()) {
            setWinner(players[(currentPlayerIndex + 1) % players.length]);
            System.out.println("No valid moves. You lose!");
            return true;
        }

        if (moved && !built) {
            Cell selected = board.getSelected();
            if (selected == null || selected.getOccupiedBy() == null) {
                setWinner(players[(currentPlayerIndex + 1) % players.length]);
                System.out.println("No worker selected. You lose!");
                return true;
            }

            Worker worker = selected.getOccupiedBy();

            boolean canBuild = BoardUtils.hasValidAdjacentAction(selected, board, worker::canBuildOn);

            if (!canBuild) {
                setWinner(players[(currentPlayerIndex + 1) % players.length]);
                System.out.println("No valid builds for selected worker. You lose!");
                return true;
            }
        }

        return false;
    }

    /**
     * Handles the player's click interaction during their turn.
     */
    @Override
    public void handleClick(int row, int col) {
        if (board.isWrathMode()) {
            board.handleWrathCellSelection(board.getCell(row, col));

            if (!board.isWrathMode()) {
                if (checkIfCurrentPlayerStuck()) return;
            }
            return;
        }

        if (checkIfCurrentPlayerStuck()) return;

        Cell clicked = board.getCell(row, col);
        Cell selected = board.getSelected();
        Player current = getCurrentPlayer();
        GodCard god = current.getGod();

        if (!moved) {
            handleMovePhaseClick(clicked, selected, current, god);
        } else if (!built) {
            handleBuildPhaseClick(clicked, selected, current, god);
        } else if (!godPowerUsedOrSkipped) {
            handleGodPowerPhaseClick(clicked, current, god);
        } else {
            endTurn();
        }
    }

    /**
     * Handles the move phase of the player's turn.
     */
    private void handleMovePhaseClick(Cell clicked, Cell selected, Player current, GodCard god) {
        if (clicked.isOccupied() && clicked.getOccupiedBy().getOwner() == current) {
            highlighter.clearMarkings();
            clicked.setStatus(SelectedStatus.SELECTED);
            board.setLastMovedCell(clicked);
            highlighter.highlightMovable(clicked.getRow(), clicked.getCol());
            return;
        }

        if (clicked.getStatus() == SelectedStatus.HIGHLIGHTED &&
                selected != null &&
                selected.isOccupied() &&
                selected.getOccupiedBy().canMoveTo(clicked)) {

            boolean reachedLevel3 = selected.getOccupiedBy().move(clicked);
            current.checkAndSetWinner(reachedLevel3);

            if (god.isRepeatableMoveGod()) {
                board.setLastMovedCell(clicked);
                if (!board.isPerimeter(clicked)) godPowerUsedOrSkipped = true;
            }

            highlighter.clearMarkings();
            clicked.setStatus(SelectedStatus.SELECTED);
            highlighter.highlightBuildable(clicked.getRow(), clicked.getCol());
            moved = true;
        }
    }

    /**
     * Handles the build phase of the player's turn.
     */
    private void handleBuildPhaseClick(Cell clicked, Cell selected, Player current, GodCard god) {
        if (god.getPowerPhase() == PowerPhase.MOVE &&
                !godPowerUsedOrSkipped &&
                clicked.getStatus() == SelectedStatus.HIGHLIGHTED) {

            if (god.performExtraAction(board, current, clicked)) {
                board.setLastMovedCell(clicked);
                highlighter.clearMarkings();
                clicked.setStatus(SelectedStatus.SELECTED);

                if (god.isRepeatableMoveGod() && god.availableGodPower(board, current)) {
                    god.usingGodPower(board, current, highlighter);
                    return;
                }

                highlighter.highlightBuildable(clicked.getRow(), clicked.getCol());
                godPowerUsedOrSkipped = true;
                moved = true;
            } else {
                godPowerUsedOrSkipped = true;
            }
            return;
        }

        if (clicked.getStatus() == SelectedStatus.HIGHLIGHTED &&
                selected != null &&
                selected.getOccupiedBy().canBuildOn(clicked)) {

            board.build(clicked.getRow(), clicked.getCol());
            highlighter.clearMarkings();
            selected.setStatus(SelectedStatus.SELECTED);
            built = true;

            if (god.getPowerPhase() == PowerPhase.MOVE) {
                endTurn();
            }
        }
    }

    /**
     * Handles the god power phase click, e.g. Demeter's second build.
     */
    private void handleGodPowerPhaseClick(Cell clicked, Player current, GodCard god) {
        if (god.getPowerPhase() == PowerPhase.BUILD &&
                clicked.getStatus() == SelectedStatus.HIGHLIGHTED) {

            if (god.performExtraAction(board, current, clicked)) {
                highlighter.clearMarkings();
                godPowerUsedOrSkipped = true;
                endTurn();
            }
        }
    }

    /** @return true if the player has moved this turn */
    public boolean hasMoved() {
        return moved;
    }

    /** @return true if the player has built this turn */
    public boolean hasBuilt() {
        return built;
    }

    /** @return true if the player used or skipped god power this turn */
    public boolean isGodPowerUsedOrSkipped() {
        return godPowerUsedOrSkipped;
    }
}
