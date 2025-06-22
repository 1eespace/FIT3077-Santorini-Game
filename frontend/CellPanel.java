package frontend;

import Board.Cell;
import Player.Worker;

import javax.swing.*;
import java.awt.*;

/**
 * A single square on the game board grid.
 * Handles rendering of buildings, domes, and workers, and responds to clicks.
 */
public class CellPanel extends JPanel {
    private final Cell cell;
    private final CellClickListener listener;

    // Constants for sizing and spacing
    private static final int PANEL_SIZE = 100;
    private static final int BASE_BLOCK_SIZE = 80;
    private static final int BLOCK_HEIGHT = 12;
    private static final int BLOCK_STEP = 12;
    private static final int DOME_SIZE = 30;
    private static final int DOME_OFFSET_Y = 8;
    private static final int WORKER_SIZE = 40;

    /**
     * Creates a visual panel linked to a specific game board cell.
     *
     * @param cell     The logical cell.
     * @param listener Click listener to notify when cell is clicked.
     */
    public CellPanel(Cell cell, CellClickListener listener) {
        this.cell = cell;
        this.listener = listener;

        setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // ToolTip: Sealed
        ToolTipManager.sharedInstance().registerComponent(this);

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (cell.isSealed()) {
                    JOptionPane.showMessageDialog(CellPanel.this,
                            "This cell is sealed for Wrath and cannot be selected.");
                    return;
                }

                if (listener != null) {
                    listener.onCellClicked(cell.getRow(), cell.getCol());
                }

                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(cell.getStatus().getColor(cell));

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int baseX = getWidth() / 2;
        int baseY = getHeight() / 2;

        int level = cell.getBlock().getLevel();
        boolean dome = cell.getBlock().hasDome();

        // Draw building levels (stacked coloured squares)
        for (int i = 0; i < level; i++) {
            Color levelColor = switch (i) {
                case 0 -> new Color(173, 216, 230);
                case 1 -> new Color(250, 227, 135);
                case 2 -> new Color(180, 70, 129);
                default -> new Color(197, 70, 70);
            };
            g2.setColor(levelColor);

            int size = BASE_BLOCK_SIZE - i * BLOCK_STEP;
            int x = baseX - size / 2;
            int y = baseY - (i + 1) * BLOCK_HEIGHT - size / 2;

            g2.fillRect(x, y, size, size);

            // Draw "L#" on each block
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            String label = "L" + (i + 1);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, x + (size - fm.stringWidth(label)) / 2, y + size - 4);
        }

        // Draw dome
        if (dome) {
            int x = baseX - DOME_SIZE / 2;
            int y = baseY - DOME_SIZE / 2 - level * BLOCK_HEIGHT - DOME_OFFSET_Y;

            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillOval(x, y, DOME_SIZE, DOME_SIZE);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            FontMetrics fm = g2.getFontMetrics();
            String dLabel = "D";
            g2.drawString(dLabel, x + (DOME_SIZE - fm.stringWidth(dLabel)) / 2,
                    y + (DOME_SIZE + fm.getAscent()) / 2);
        }

        // Draw worker
        Worker w = cell.getOccupiedBy();
        if (w != null) {
            Color workerColor = w.getOwner().getColor();
            int x = baseX - WORKER_SIZE / 2;
            int y = baseY - WORKER_SIZE / 2 - level * BLOCK_HEIGHT - (dome ? 12 : 0);

            g2.setColor(workerColor);
            g2.fillRect(x, y, WORKER_SIZE, WORKER_SIZE);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            String wLabel = "W" + (w.getId() + 1);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(wLabel, x + (WORKER_SIZE - fm.stringWidth(wLabel)) / 2,
                    y + (WORKER_SIZE + fm.getAscent()) / 2 - 3);
        }

        // Wrath: Selecting Cell
        if (cell.isWrathSelected()) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
        }

        // Sealed takes priority over flooded
        if (cell.isSealed()) {
            g2.setColor(new Color(101, 0, 0, 255));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.drawString("SEALED", 10, 20);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.drawString("Blocked for All", 10, 35);
        } else if (cell.isFlooded()) {
            g2.setColor(new Color(125, 178, 230, 136));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.drawString("FLOODED", 10, 20);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.drawString("Move: Triton", 10, 35);
            g2.drawString("Build: Blocked", 10, 50);
        }

    }
}
