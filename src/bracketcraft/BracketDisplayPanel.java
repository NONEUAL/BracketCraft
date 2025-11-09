package bracketcraft;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * A custom JPanel that visually draws the entire tournament bracket.
 * Its appearance is controlled by the AppTheme class.
 */
public class BracketDisplayPanel extends JPanel {
    private Tournament tournament;
    private final JPanel drawingCanvas;
    private final JLabel footerLabel; // Reference to the footer label

    // --- Constants for drawing ---
    private static final int MATCH_WIDTH = 180;
    private static final int MATCH_HEIGHT = 70;
    private static final int HORIZONTAL_GAP = 60;
    private static final int VERTICAL_GAP = 20;

    public BracketDisplayPanel() {
        setLayout(new BorderLayout());
        setOpaque(false); // Make transparent to see MainFrame's background

        // --- Header ---
        add(createHeaderFooter("OCT | TOURNAMENT BRACKET", AppTheme.TEXT_PRIMARY, AppTheme.ACCENT_PRIMARY, FlowLayout.RIGHT), BorderLayout.NORTH);

        // --- Footer (with a stored reference to the label) ---
        JPanel footerPanel = createHeaderFooter("", AppTheme.TEXT_ACCENT, AppTheme.ACCENT_PRIMARY, FlowLayout.RIGHT);
        this.footerLabel = (JLabel) footerPanel.getComponent(0); // Get the label from the panel
        add(footerPanel, BorderLayout.SOUTH);
        
        // --- Drawing Canvas ---
        drawingCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBracket(g);
            }
        };
        drawingCanvas.setOpaque(false);
        add(drawingCanvas, BorderLayout.CENTER);
    }

    public void setTournament(Tournament newTournament) {
        this.tournament = newTournament;
        repaint(); // Redraw the panel whenever the tournament data changes
    }

    public void setSportName(String sportName) {
        if (sportName != null && !sportName.trim().isEmpty()) {
            footerLabel.setText(sportName.toUpperCase() + " |");
        } else {
            footerLabel.setText("");
        }
    }

    private void drawBracket(Graphics g) {
        if (tournament == null || tournament.getRounds().isEmpty()) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<List<Match>> rounds = tournament.getRounds();
        Point[][] matchPositions = calculateMatchPositions(rounds);

        // Pass 1: Draw connectors behind the match boxes
        g2d.setStroke(new BasicStroke(2));
        for (int i = 0; i < rounds.size() - 1; i++) {
            for (int j = 0; j < rounds.get(i).size(); j++) {
                Match currentMatch = rounds.get(i).get(j);
                boolean hasWinner = currentMatch.getWinner() != null;
                drawConnector(g2d, matchPositions[i][j], matchPositions[i + 1][j / 2], hasWinner);
            }
        }

        // Pass 2: Draw match boxes on top of the connectors
        for (int i = 0; i < rounds.size(); i++) {
            for (int j = 0; j < rounds.get(i).size(); j++) {
                drawMatch(g2d, rounds.get(i).get(j), matchPositions[i][j]);
            }
        }
    }

    private Point[][] calculateMatchPositions(List<List<Match>> rounds) {
        Point[][] positions = new Point[rounds.size()][];
        int maxRoundHeight = 0;
        for (List<Match> round : rounds) {
            maxRoundHeight = Math.max(maxRoundHeight, round.size() * (MATCH_HEIGHT + VERTICAL_GAP));
        }
        int totalWidth = getWidth();
        int totalHeight = getHeight();

        for (int i = 0; i < rounds.size(); i++) {
            List<Match> roundMatches = rounds.get(i);
            positions[i] = new Point[roundMatches.size()];
            int roundHeight = roundMatches.size() * (MATCH_HEIGHT + VERTICAL_GAP) - VERTICAL_GAP;
            int x = i * (MATCH_WIDTH + HORIZONTAL_GAP) + HORIZONTAL_GAP;
            int yOffset = (totalHeight - roundHeight) / 2;

            for (int j = 0; j < roundMatches.size(); j++) {
                int y = yOffset + j * (MATCH_HEIGHT + VERTICAL_GAP);
                positions[i][j] = new Point(x, y);
            }
        }
        return positions;
    }

    private void drawConnector(Graphics2D g2d, Point p1, Point p2, boolean hasWinner) {
        int x1 = p1.x + MATCH_WIDTH;
        int y1 = p1.y + MATCH_HEIGHT / 2;
        int x2 = p2.x;
        int y2 = p2.y + MATCH_HEIGHT / 2;
        int midX = x1 + HORIZONTAL_GAP / 2;

        // Draw black lines
        g2d.setColor(AppTheme.BRACKET_LINE_COLOR);
        g2d.drawLine(x1, y1, midX, y1);
        g2d.drawLine(midX, y1, midX, y2);
        g2d.drawLine(midX, y2, x2, y2);

        // If the match has a winner, draw the green accent on top
        if (hasWinner) {
            g2d.setColor(AppTheme.WINNER_ACCENT_BACKGROUND);
            g2d.fillRect(x1 - 5, y1 - 10, 10, 20);
        }
    }

    private void drawMatch(Graphics2D g2d, Match match, Point pos) {
        g2d.setColor(AppTheme.BACKGROUND_PANEL);
        g2d.fill(new RoundRectangle2D.Float(pos.x, pos.y, MATCH_WIDTH, MATCH_HEIGHT, 10, 10));

        drawParticipantSlot(g2d, match.getParticipant1(), 1, pos, 0);
        drawParticipantSlot(g2d, match.getParticipant2(), 2, pos, 1);
    }

    private void drawParticipantSlot(Graphics2D g2d, Participant p, int seed, Point pos, int slotIndex) {
        int slotY = pos.y + (slotIndex * (MATCH_HEIGHT / 2));
        int slotHeight = MATCH_HEIGHT / 2;

        g2d.setFont(AppTheme.FONT_BRACKET_HEADER);
        g2d.setColor(AppTheme.TEXT_PRIMARY);
        String name = (p != null) ? p.getName() : "";
        g2d.drawString(name, pos.x + 15, slotY + slotHeight / 2 + 5);
        g2d.drawString("0", pos.x + MATCH_WIDTH - 20, slotY + slotHeight / 2 + 5);

        // Draw horizontal divider
        if (slotIndex == 0) {
            g2d.setColor(AppTheme.BACKGROUND_MAIN.brighter());
            g2d.drawLine(pos.x + 5, pos.y + slotHeight, pos.x + MATCH_WIDTH - 5, pos.y + slotHeight);
        }
    }

    private JPanel createHeaderFooter(String text, Color textColor, Color accentColor, int align) {
        JPanel panel = new JPanel(new FlowLayout(align));
        panel.setOpaque(false);
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(textColor);
        panel.add(label);
        JPanel accent = new JPanel();
        accent.setBackground(accentColor);
        accent.setPreferredSize(new Dimension(8, 28));
        panel.add(accent);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 50));
        return panel;
    }
}