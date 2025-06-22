package frontend;

import Player.Player;
import Timer.TurnTimerManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controls timer UI updates using Swing Timer and JLabel.
 */
public class TurnTimerUIController {
    private final TurnTimerManager timerManager;
    private final Map<Player, JLabel> playerLabels = new HashMap<>();
    private Timer swingTimer;
    private Runnable timeoutCallback;

    /**
     * Initializes UI controller for player timers.
     *
     * @param manager  Timer logic manager.
     * @param players  List of players.
     * @param label1   Label for player 1.
     * @param label2   Label for player 2.
     */
    public TurnTimerUIController(TurnTimerManager manager, List<Player> players,
                                 JLabel label1, JLabel label2) {
        this.timerManager = manager;
        for (int i = 0; i < players.size(); i++) {
            playerLabels.put(players.get(i), i == 0 ? label1 : label2);
        }
    }

    /**
     * Starts the UI timer for the given player.
     *
     * @param player The player whose turn starts.
     */
    public void startTurn(Player player) {
        timerManager.startTurn(player);
        updateLabel(player);

        if (swingTimer != null) swingTimer.stop();

        swingTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timerManager.tick();
                if (timerManager.isTimeUp()) {
                    playerLabels.get(player).setText(player.getName() + ": TIME OUT!");
                    swingTimer.stop();
                    if (timeoutCallback != null) timeoutCallback.run();
                } else {
                    updateLabel(player);
                }
            }
        });

        swingTimer.start();
    }

    /**
     * Pauses the timer and stops the UI update.
     */
    public void pause() {
        timerManager.pause();
        if (swingTimer != null) swingTimer.stop();
    }

    /**
     * Sets the action to run when time runs out.
     *
     * @param callback Runnable callback to execute on timeout.
     */
    public void setTimeoutCallback(Runnable callback) {
        this.timeoutCallback = callback;
    }

    /**
     * Updates the label to show remaining time.
     *
     * @param player The player whose timer is updated.
     */
    private void updateLabel(Player player) {
        long millis = timerManager.getRemainingMillis();
        long sec = millis / 1000;
        String time = String.format("%02d:%02d", sec / 60, sec % 60);
        playerLabels.get(player).setText(player.getName() + ": " + time);
    }
}
