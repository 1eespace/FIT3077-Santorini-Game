package frontend;

import javax.swing.*;
import java.awt.*;

/**
 * Simple panel that shows control buttons with one-time wrath support.
 */
public class Buttons extends JPanel {
    private final JButton useButton;
    private final JButton skipButton;
    private final JButton exitButton;
    private final JButton wrathButton;

    public Buttons() {
        setLayout(new GridLayout(2, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        useButton = new JButton("Use God Power");
        skipButton = new JButton("Skip God Power");
        exitButton = new JButton("Exit Game");
        wrathButton = new JButton("⚡ Use God Wrath");

        Dimension buttonSize = new Dimension(200, 60);
        Font font = new Font("Arial", Font.BOLD, 16);

        JButton[] buttons = { useButton, skipButton, exitButton, wrathButton };
        for (JButton b : buttons) {
            b.setPreferredSize(buttonSize);
            b.setFont(font);
        }

        add(useButton);
        add(skipButton);
        add(wrathButton);
        add(exitButton);
    }

    public JButton getUseButton() {
        return useButton;
    }

    public JButton getSkipButton() {
        return skipButton;
    }

    public JButton getExitButton() {
        return exitButton;
    }

    public JButton getWrathButton() {
        return wrathButton;
    }
}
