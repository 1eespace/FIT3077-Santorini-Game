package frontend;

import GameMode.Config;
import GodCard.GodCard;
import GodCard.PowerPhase;
import Player.Player;
import Player.Worker;
import Board.Board;
import Board.Cell;
import Timer.TurnTimerManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main game board panel. Renders cells, manages timer UI, and handles user interactions.
 */
public class BoardPanel extends JPanel implements CellClickListener {
    private final Config config;
    private final Board board;
    private final String[] playerNames;
    private JLabel statusLabel;
    private final TurnTimerUIController timerUI;
    private final List<Player> players;

    private boolean timerStarted = false;

    /**
     * Constructs and sets up the game board UI.
     *
     * @param playerNames Names of the two players.
     * @param config      Game configuration object.
     */
    public BoardPanel(String[] playerNames, Config config) {
        this.playerNames = playerNames;
        this.config = config;
        this.board = config.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();

        setLayout(null);
        setPreferredSize(new Dimension(cols * 100, rows * 100 + 40));

        int cellSize = 100;
        int boardWidth = cols * cellSize;
        int boardHeight = rows * cellSize;
        int offsetX = (getPreferredSize().width - boardWidth) / 2;

        // Create and position cell panels
        for (Cell cell : board.getAllCells()) {
            CellPanel panel = new CellPanel(cell, this);
            List<CellPanel> cellPanels = new ArrayList<>();
            cellPanels.add(panel);
            add(panel);
            int x = offsetX + cell.getCol() * cellSize;
            int y = cell.getRow() * cellSize;
            panel.setBounds(x, y, cellSize, cellSize);
        }

        // Place workers randomly
        Worker.placeMultipleRandomly(board, Arrays.asList(config.getPlayers()));

        // Setup timer UI labels
        JLabel timerLabel1 = new JLabel(playerNames[0] + ": 5:00");
        JLabel timerLabel2 = new JLabel(playerNames[1] + ": 5:00");
        timerLabel1.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel2.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel1.setHorizontalAlignment(SwingConstants.LEFT);
        timerLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
        timerLabel1.setForeground(Color.BLUE);
        timerLabel2.setForeground(Color.RED);

        JPanel timerPanel = new JPanel(new GridLayout(1, 2));
        timerPanel.setBounds(0, boardHeight + 5, boardWidth, 30);
        timerPanel.add(timerLabel1);
        timerPanel.add(timerLabel2);
        add(timerPanel);

        // Init timer logic and UI
        players = Arrays.asList(config.getPlayers());
        TurnTimerManager timerManager = new TurnTimerManager(players);
        timerUI = new TurnTimerUIController(timerManager, players, timerLabel1, timerLabel2);
        timerUI.setTimeoutCallback(() -> {
            Player loser = config.getCurrentPlayer();
            for (Player p : players) {
                if (!p.equals(loser)) {
                    config.setWinner(p);
                    SwingUtilities.invokeLater(() -> onPlayerWin(p));
                    break;
                }
            }
        });

        updateStatus();

        Player first = config.getCurrentPlayer();
        System.out.println(first.getName() + " goes first (" + first.getGod().getName() + ")");
    }

    /**
     * Sets the status label used for turn updates.
     *
     * @param label JLabel to display game status.
     */
    public void setStatusLabel(JLabel label) {
        this.statusLabel = label;
        updateStatus();
    }

    /**
     * Updates the status message showing current player's name and god power hints.
     */
    private void updateStatus() {
        if (statusLabel == null) return;

        Player currentPlayer = config.getCurrentPlayer();
        String name = currentPlayer.getName();
        String god = currentPlayer.getGod().getName();
        GodCard godCard = currentPlayer.getGod();
        String turnMessage = getString(name, god);
        String godHint = "";

        // Show hint for god power if not yet used/skipped
        if (!config.isGodPowerUsedOrSkipped()) {
            PowerPhase phase = godCard.getPowerPhase();
            boolean moved = config.hasMoved();
            boolean built = config.hasBuilt();

            if (phase == PowerPhase.MOVE && moved && !built) {
                if (godCard.isRepeatableMoveGod()) {
                    if (godCard.availableGodPower(config.getBoard(), currentPlayer)) {
                        godHint = "<br><span style='color:#2c3e50; font-size:11px;'>You may use " + godCard.getName() + "'s power to move again (you're on a perimeter).</span>";
                    } else {
                        godHint = "<br><span style='color:#2c3e50; font-size:11px;'>" + godCard.getName() + "'s power is only available after moving onto a perimeter space.</span>";
                    }
                } else {
                    godHint = "<br><span style='color:#2c3e50; font-size:11px;'>You may now use " + godCard.getName() + "'s move power or build to skip.</span>";
                }
            } else if (phase == PowerPhase.BUILD && moved && built) {
                godHint = "<br><span style='color:#2c3e50; font-size:11px;'>You may now use " + godCard.getName() + "'s build power or skip to end your turn.</span>";
            }
        }

        statusLabel.setText("<html>" + turnMessage + godHint + "</html>");
    }

    /**
     * Highlights the current player's name in colour.
     */
    private String getString(String name, String god) {
        String colouredName = name;
        if (playerNames.length == 2) {
            if (name.equals(playerNames[0])) {
                colouredName = "<font color='blue'>" + name + "</font>";
            } else if (name.equals(playerNames[1])) {
                colouredName = "<font color='red'>" + name + "</font>";
            }
        }
        return "<span style='font-size:16px;'><b>" + colouredName + "</b> (" + god + ")'s Turn</span>";
    }

    /**
     * Called when player uses their god power.
     */
    public void useGodPower() {
        config.useGodPower();
        updateStatus();
        repaint();
        timerUI.pause();
        timerUI.startTurn(config.getCurrentPlayer());
    }

    /**
     * Called when player skips their god power.
     */
    public void skipGodPower() {
        config.skipGodPower();
        updateStatus();
        repaint();
        timerUI.pause();
        timerUI.startTurn(config.getCurrentPlayer());
    }

    /**
     * Executes god wrath if available.
     */
    public void useGodWrath() {
        Player current = config.getCurrentPlayer();
        GodCard god = current.getGod();

        if (!god.canUseGodWrath(config.getBoard(), current)) {
            JOptionPane.showMessageDialog(this, "Wrath is not available or already used.");
            return;
        }

        god.useGodWrath(config.getBoard(), current);
        updateStatus();
        repaint();
    }

    /**
     * Handles a click on a cell. Starts timer if this is the first action.
     */
    @Override
    public void onCellClicked(int row, int col) {
        if (!timerStarted) {
            Cell clickedCell = board.getCell(row, col);
            Worker worker = clickedCell.getOccupiedBy();
            Player current = config.getCurrentPlayer();

            if (worker != null && worker.getOwner() == current) {
                timerStarted = true;
                timerUI.startTurn(current);
            } else {
                JOptionPane.showMessageDialog(this, "You must select your own worker to start the game.");
            }
        }

        config.handleClick(row, col);
        updateStatus();
        repaint();

        Player winner = config.getWinner();
        timerUI.pause();
        if (winner != null) {
            SwingUtilities.invokeLater(() -> onPlayerWin(winner));
        } else {
            timerUI.startTurn(config.getCurrentPlayer());
        }
    }

    /**
     * Called when a player wins the game.
     */
    public void onPlayerWin(Player winner) {
        setEnabled(false);
        int option = JOptionPane.showOptionDialog(
                this,
                winner.getName() + " wins!\nWhat would you like to do?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Play Again", "Exit"},
                "Play Again"
        );

        if (option == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(() -> {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                topFrame.dispose();
                new GameSetUpMenu();
            });
        } else if (option == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }
}
