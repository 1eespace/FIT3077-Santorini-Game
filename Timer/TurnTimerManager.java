package Timer;

import Player.Player;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages remaining time for each player in a turn-based game.
 */
public class TurnTimerManager {
    private final Map<Player, Long> playerTimes = new HashMap<>();
    private Player currentPlayer;
    private long remainingMillis;

    /**
     * Initializes each player's time.
     *
     * @param players List of game players.
     */
    public TurnTimerManager(List<Player> players) {
        for (Player player : players) {
            playerTimes.put(player, 5 * 60 * 1000L); // 5min per player
        }
    }

    /**
     * Starts the timer for the given player.
     *
     * @param player The player whose turn is starting.
     */
    public void startTurn(Player player) {
        if (currentPlayer != null) saveRemainingTime();
        currentPlayer = player;
        remainingMillis = playerTimes.getOrDefault(player, 30 * 1000L);
    }

    /**
     * Decreases the timer by 1 second.
     */
    public void tick() {
        if (remainingMillis > 0) remainingMillis -= 1000;
    }

    /**
     * Saves the current player's remaining time.
     */
    public void pause() {
        if (currentPlayer != null) saveRemainingTime();
    }

    /**
     * Checks if the current player's time is up.
     *
     * @return true if time is up; false otherwise.
     */
    public boolean isTimeUp() {
        return remainingMillis <= 0;
    }

    /**
     * Gets the current player's remaining time in milliseconds.
     *
     * @return Remaining time in ms.
     */
    public long getRemainingMillis() {
        return remainingMillis;
    }

    private void saveRemainingTime() {
        playerTimes.put(currentPlayer, remainingMillis);
    }
}
