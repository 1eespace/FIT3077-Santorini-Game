package frontend;

import GameMode.Config;
import GameMode.TwoPlayerConfig;
import GodCard.Artemis;
import GodCard.Demeter;
import GodCard.Triton;
import GodCard.GodCard;
import Player.Player;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.Objects;
import java.util.Vector;

/**
 * Game setup menu for entering player names and starting the game.
 */
public class GameSetUpMenu extends JFrame {
    private final JTextField player1NameField;
    private final JTextField player2NameField;

    public GameSetUpMenu() {
        setTitle("Game Setup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 320);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Image
        JLabel logoLabel = new JLabel();
        ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/Assets/title.png")));

        Image scaledImage = originalIcon.getImage().getScaledInstance(200, 60, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        logoLabel.setIcon(scaledIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(logoLabel, gbc);

        // Player 1 input
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel player1Label = new JLabel("Player 1:");
        player1Label.setForeground(Color.BLUE);
        mainPanel.add(player1Label, gbc);

        gbc.gridx = 1;
        player1NameField = new JTextField("Player 1", 15);
        mainPanel.add(player1NameField, gbc);

        // Player 2 input
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel player2Label = new JLabel("Player 2:");
        player2Label.setForeground(Color.RED);
        mainPanel.add(player2Label, gbc);

        gbc.gridx = 1;
        player2NameField = new JTextField("Player 2", 15);
        mainPanel.add(player2NameField, gbc);

        // Start Game Button
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton startGameButton = new JButton("Start Game");
        startGameButton.setFocusPainted(false);
        startGameButton.addActionListener(e -> launchGame());
        mainPanel.add(startGameButton, gbc);

        add(mainPanel);
        setVisible(true);

    }

    /**
     * Launches the full game after entering player names.
     */
    private void launchGame() {
        String player1Name = player1NameField.getText().trim();
        String player2Name = player2NameField.getText().trim();

        if (player1Name.isEmpty() || player2Name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter names for both players.");
            return;
        }

        if (player1Name.length() > 20 || player2Name.length() > 20) {
            JOptionPane.showMessageDialog(this, "Player names must be 20 characters or less.");
            return;
        }

        if (player1Name.equalsIgnoreCase(player2Name)) {
            JOptionPane.showMessageDialog(this, "Player names must be different.");
            return;
        }


        Vector<String> playerVector = new Vector<>();
        playerVector.add(player1Name);
        playerVector.add(player2Name);
        String[] playerNames = playerVector.toArray(new String[0]);

        Vector<GodCard> godCards = new Vector<>();
        godCards.add(new Artemis());
        godCards.add(new Demeter());
        godCards.add(new Triton());  // New GodCard

        // Randomly allocate the godCard
        Collections.shuffle(godCards);

        Config config = new TwoPlayerConfig(playerVector, godCards);
        config.setup();

        // Modal shows assigned god cards
        Player[] players = config.getPlayers();
        // Randomly allocated the turn of player
        showGodCardIntro(players[0], players[1]);

        // Create main game window
        JFrame frame = new JFrame("Santorini Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLayout(new BorderLayout());

        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        statusLabel.setPreferredSize(new Dimension(600, 50));

        // Crete the board panel
        BoardPanel boardPanel = new BoardPanel(playerNames, config);
        boardPanel.setStatusLabel(statusLabel);

        Buttons buttons = new Buttons();
        buttons.getUseButton().addActionListener(e -> boardPanel.useGodPower());
        buttons.getSkipButton().addActionListener(e -> boardPanel.skipGodPower());
        buttons.getWrathButton().addActionListener(e -> {
            boardPanel.useGodWrath();
        });
        buttons.getExitButton().addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to exit the game and return to main menu? The current game will be lost.",
                    "Exit Game",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                frame.dispose(); // closes the game window
                new GameSetUpMenu();  // optional: exit the whole app
            }
        });

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.add(boardPanel);

        frame.add(statusLabel, BorderLayout.NORTH);
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setPreferredSize(new Dimension(800, 50));
        topBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        topBar.add(statusLabel, BorderLayout.CENTER);

        JButton infoButton = new JButton("⚡ Info");
        infoButton.setPreferredSize(new Dimension(100, 30));
        infoButton.setFocusPainted(false);
        infoButton.addActionListener(e -> showWrathInfoModal());

        topBar.add(infoButton, BorderLayout.EAST);

        frame.add(topBar, BorderLayout.NORTH);
        frame.add(centerWrapper, BorderLayout.CENTER);
        frame.add(buttons, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        dispose();
    }

    /**
     * Displays a modal with each player's god card and name.
     */
    private ImageIcon getIconByGodName(String godName) {
        return switch (godName) {
            case "Artemis" -> new ImageIcon(Objects.requireNonNull(getClass().getResource("/Assets/artemis.png")));
            case "Demeter" -> new ImageIcon(Objects.requireNonNull(getClass().getResource("/Assets/demeter.png")));
            case "Triton" -> new ImageIcon(Objects.requireNonNull(getClass().getResource("/Assets/triton.png")));
            default -> null;
        };
    }

    private void showGodCardIntro(Player p1, Player p2) {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        ImageIcon p1Icon = getIconByGodName(p1.getGod().getName());
        ImageIcon p2Icon = getIconByGodName(p2.getGod().getName());

        Image p1Scaled = p1Icon.getImage().getScaledInstance(120, 180, Image.SCALE_SMOOTH);
        Image p2Scaled = p2Icon.getImage().getScaledInstance(120, 180, Image.SCALE_SMOOTH);

        // Player 1 Panel
        JPanel p1Panel = new JPanel(new BorderLayout(5, 5));
        JLabel p1Image = new JLabel(new ImageIcon(p1Scaled));
        JLabel p1Label = new JLabel(
                "<html><div style='text-align:center;'><b style='color:blue'>" +
                        p1.getName() + "</b><br>(" + p1.getGod().getName() + ")</div></html>",
                SwingConstants.CENTER
        );
        p1Panel.add(p1Label, BorderLayout.NORTH);
        p1Panel.add(p1Image, BorderLayout.CENTER);

        // Player 2 Panel
        JPanel p2Panel = new JPanel(new BorderLayout(5, 5));
        JLabel p2Image = new JLabel(new ImageIcon(p2Scaled));
        JLabel p2Label = new JLabel(
                "<html><div style='text-align:center;'><b style='color:red'>" +
                        p2.getName() + "</b><br>(" + p2.getGod().getName() + ")</div></html>",
                SwingConstants.CENTER
        );
        p2Panel.add(p2Label, BorderLayout.NORTH);
        p2Panel.add(p2Image, BorderLayout.CENTER);

        // Add to main
        mainPanel.add(p1Panel);
        mainPanel.add(p2Panel);

        JOptionPane.showMessageDialog(this, mainPanel, "Assigned God Cards", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWrathInfoModal() {
        JTextArea textArea = new JTextArea(
                """
                ⚡ GOD WRATH GUIDE
                ──────────────────────────────
                
                TIP (IMPORTANT):
                • You can use ⚡ God Wrath only ONCE PER GAME.
                
                • It can be used on the SAME TURN as your god power.
                • Use it wisely — it is powerful but limited!
              
                ──────────────────────────────
       
                Triton (Water God):
                Push an opponent’s worker to same or lower level (e.g., from level 2 to 2 or 1).
                After the push:
                1. The original cell (where the opponent's worker was before being pushed) will be sealed.
                
                2. 2 random adjacent cells (8 directions) will be flooded.
                   If there are fewer than 2 valid neighbor cells (e.g., in a corner), fewer cells may be flooded.
              
                Flooded Cell: Only Triton can move but no allow to build.
        
                Artemis (Huntress):
                Seal 3 cells permanently. Can't sealed any workers. No one can not move and build.
                
                Demeter (Harvest Goddess):
                Select 3 any cells/buildings you want to collapse (-1 level each). Occupied buildings allowed.
                """
        );
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 15));
        textArea.setBackground(null);
        textArea.setBorder(null);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(
                null,
                scrollPane,
                "God Wrath Description",
                JOptionPane.INFORMATION_MESSAGE
        );

    }

}