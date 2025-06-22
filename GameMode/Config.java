package GameMode;

import Board.Board;
import Player.Player;
import GodCard.GodCard;

import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * Abstract base class for managing overall game flow.
 * Handles board setup, player management, and winner status.
 * Supports polymorphic behavior for different game modes and god powers.
 */
public abstract class Config {

    protected Vector<String> playerNames;
    protected Vector<GodCard> gods;

    protected int boardWidth;
    protected int boardHeight;
    protected int numPlayers;

    protected Board board;
    protected Player[] players;
    protected int currentPlayerIndex;

    protected Player winner = null;

    /**
     * Constructs the game configuration with player names and their assigned god cards.
     *
     * @param playerNames List of player names
     * @param gods        List of corresponding god cards
     */
    public Config(Vector<String> playerNames, Vector<GodCard> gods) {
        this.playerNames = playerNames;
        this.gods = gods;
    }

    /**
     * Initialises the board and players.
     * Randomises the first player and assigns colours.
     */
    public void setup() {
        this.board = new Board(boardWidth, boardHeight);
        this.numPlayers = playerNames.size();
        this.players = new Player[numPlayers];

        List<Color> defaultColors = List.of(Color.BLUE, Color.RED);

        for (int i = 0; i < numPlayers; i++) {
            players[i] = new Player(playerNames.get(i), gods.get(i), defaultColors.get(i));
        }

        this.currentPlayerIndex = new Random().nextInt(numPlayers);
    }

    /**
     * Called when a cell is clicked by the player.
     *
     * @param row Row index of clicked cell.
     * @param col Column index of clicked cell.
     */
    public abstract void handleClick(int row, int col);

    /**
     * Returns the currently active player
     */
    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

    /**
     * Returns the array of all players.
     */
    public Player[] getPlayers() {
        return players;
    }

    /**
     * Returns the game board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Sets the winner and flags them as the winner.
     */
    public void setWinner(Player winner) {
        this.winner = winner;
        if (winner != null) {
            winner.setWinner();
        }
    }

    /**
     * Returns the winning player, or null if no one has won yet.
     */
    public Player getWinner() {
        return winner;
    }

    // === Abstract methods for god power and turn control ===

    /**
     * Called when a player chooses to use their god power.
     */
    public abstract void useGodPower();

    /**
     * Called when a player chooses to skip their god power.
     */
    public abstract void skipGodPower();

    /**
     * Called at the end of a player's turn.
     */
    public abstract void endTurn();

    /**
     * Indicates whether the player has either used or skipped their god power for this turn.
     */
    public boolean isGodPowerUsedOrSkipped() {
        return true; // default: treated as already used/skipped
    }

    /**
     * Indicates whether the player has performed a move action this turn.
     * Default implementation returns false.
     */
    public boolean hasMoved() {
        return false;
    }

    /**
     * Indicates whether the player has performed a build action this turn.
     * Default implementation returns false.
     */
    public boolean hasBuilt() {
        return false;
    }

}
