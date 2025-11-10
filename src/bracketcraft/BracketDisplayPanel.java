package bracketcraft;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class BracketDisplayPanel extends JPanel {
    private Tournament tournament;
    private final JPanel drawingCanvas;
    private final JLabel footerLabel;

    // --- NEW: Constants and variables for rendering and interaction ---
    private static final int MATCH_WIDTH = 200;
    private static final int MATCH_HEIGHT = 80;
    private static final int HORIZONTAL_GAP = 80;
    private static final int VERTICAL_GAP = 40;

    // --- NEW: Zoom and Pan variables ---
    private double scale = 1.0;
    private Point2D.Double viewOffset = new Point2D.Double(0, 0);
    private Point lastDragPoint;

    public BracketDisplayPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        add(createHeaderFooter("OCT | TOURNAMENT BRACKET", AppTheme.TEXT_PRIMARY, AppTheme.ACCENT_PRIMARY, FlowLayout.RIGHT), BorderLayout.NORTH);

        JPanel footerPanel = createHeaderFooter("", AppTheme.TEXT_ACCENT, AppTheme.ACCENT_PRIMARY, FlowLayout.RIGHT);
        this.footerLabel = (JLabel) footerPanel.getComponent(0);
        add(footerPanel, BorderLayout.SOUTH);
        
        drawingCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBracket(g);
            }
        };
        drawingCanvas.setOpaque(false);
        add(drawingCanvas, BorderLayout.CENTER);

        // --- NEW: Add mouse listeners for zoom and pan ---
        addInteractionListeners();
    }

    public void setTournament(Tournament newTournament) {
        this.tournament = newTournament;
        // Reset view when a new tournament is set
        resetView();
        repaint();
    }
    
    private void resetView() {
        scale = 1.0;
        lastDragPoint = null;
        if (tournament != null && !tournament.getRounds().isEmpty()) {
            // Center the view on the bracket
            int totalWidth = calculateTotalBracketWidth();
            int totalHeight = calculateTotalBracketHeight();
            viewOffset.x = (drawingCanvas.getWidth() - totalWidth) / 2.0;
            viewOffset.y = (drawingCanvas.getHeight() - totalHeight) / 2.0;
        } else {
            viewOffset.x = 0;
            viewOffset.y = 0;
        }
        repaint();
    }

    public void setSportName(String sportName) {
        footerLabel.setText(sportName != null && !sportName.trim().isEmpty() ? sportName.toUpperCase() + " |" : "");
    }
    
    private void addInteractionListeners() {
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double oldScale = scale;
                // Get mouse position relative to the canvas
                Point2D.Double mousePoint = new Point2D.Double(e.getX() - viewOffset.x, e.getY() - viewOffset.y);
                
                // Calculate new scale
                double zoomFactor = e.getWheelRotation() < 0 ? 1.1 : 1 / 1.1;
                scale *= zoomFactor;
                scale = Math.max(0.1, Math.min(scale, 5.0)); // Clamp scale

                // Adjust offset to zoom towards the mouse pointer
                viewOffset.x = e.getX() - mousePoint.x * scale / oldScale;
                viewOffset.y = e.getY() - mousePoint.y * scale / oldScale;

                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e) || SwingUtilities.isLeftMouseButton(e)) {
                    lastDragPoint = e.getPoint();
                    drawingCanvas.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastDragPoint = null;
                drawingCanvas.setCursor(Cursor.getDefaultCursor());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastDragPoint != null) {
                    int dx = e.getX() - lastDragPoint.x;
                    int dy = e.getY() - lastDragPoint.y;
                    viewOffset.x += dx;
                    viewOffset.y += dy;
                    lastDragPoint = e.getPoint();
                    repaint();
                }
            }
        };
        drawingCanvas.addMouseWheelListener(adapter);
        drawingCanvas.addMouseListener(adapter);
        drawingCanvas.addMouseMotionListener(adapter);
    }
    
    private void drawBracket(Graphics g) {
        if (tournament == null || tournament.getRounds().isEmpty()) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- NEW: Apply zoom and pan transformations ---
        g2d.translate(viewOffset.x, viewOffset.y);
        g2d.scale(scale, scale);

        List<List<Match>> rounds = tournament.getRounds();
        Point[][] matchPositions = calculateMatchPositions(rounds);

        g2d.setStroke(new BasicStroke(2));
        for (int i = 0; i < rounds.size() - 1; i++) {
            for (int j = 0; j < rounds.get(i).size(); j++) {
                Match currentMatch = rounds.get(i).get(j);
                if (currentMatch.getNextMatch() != null) {
                    drawConnector(g2d, matchPositions[i][j], matchPositions[i + 1][j / 2], currentMatch.getWinner() != null);
                }
            }
        }

        for (int i = 0; i < rounds.size(); i++) {
            for (int j = 0; j < rounds.get(i).size(); j++) {
                drawMatch(g2d, rounds.get(i).get(j), matchPositions[i][j]);
            }
        }
    }

    // --- NEW: Dynamic Layout Calculation ---
    private int calculateTotalBracketWidth() {
        if (tournament == null || tournament.getRounds().isEmpty()) return 0;
        return (tournament.getRounds().size() * (MATCH_WIDTH + HORIZONTAL_GAP));
    }
    
    private int calculateTotalBracketHeight() {
        if (tournament == null || tournament.getRounds().isEmpty()) return 0;
        int maxMatchesInRound = 0;
        for(List<Match> round : tournament.getRounds()) {
            maxMatchesInRound = Math.max(maxMatchesInRound, round.size());
        }
        return (maxMatchesInRound * (MATCH_HEIGHT + VERTICAL_GAP));
    }
    
    private Point[][] calculateMatchPositions(List<List<Match>> rounds) {
        Point[][] positions = new Point[rounds.size()][];
        
        int totalBracketHeight = calculateTotalBracketHeight();

        for (int i = 0; i < rounds.size(); i++) {
            List<Match> roundMatches = rounds.get(i);
            positions[i] = new Point[roundMatches.size()];
            
            int roundHeight = roundMatches.size() * (MATCH_HEIGHT + VERTICAL_GAP) - VERTICAL_GAP;
            int x = i * (MATCH_WIDTH + HORIZONTAL_GAP);
            int yOffset = (totalBracketHeight - roundHeight) / 2;

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

        g2d.setColor(AppTheme.BRACKET_LINE_COLOR);
        g2d.drawLine(x1, y1, midX, y1);
        g2d.drawLine(midX, y1, midX, y2);
        g2d.drawLine(midX, y2, x2, y2);

        if (hasWinner) {
            g2d.setColor(AppTheme.WINNER_ACCENT_BACKGROUND);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawLine(x1, y1, midX, y1);
        }
        g2d.setStroke(new BasicStroke(2)); // Reset stroke
    }

    private void drawMatch(Graphics2D g2d, Match match, Point pos) {
        g2d.setColor(AppTheme.BACKGROUND_PANEL);
        g2d.fill(new RoundRectangle2D.Float(pos.x, pos.y, MATCH_WIDTH, MATCH_HEIGHT, 15, 15));
        
        // Draw winner accent on the box itself
        if (match.getWinner() != null) {
            g2d.setColor(AppTheme.WINNER_ACCENT_BACKGROUND);
            g2d.setStroke(new BasicStroke(3));
            g2d.draw(new RoundRectangle2D.Float(pos.x, pos.y, MATCH_WIDTH, MATCH_HEIGHT, 15, 15));
        }

        drawParticipantSlot(g2d, match.getParticipant1(), match.getScore1(), pos, 0, match.getWinner());
        drawParticipantSlot(g2d, match.getParticipant2(), match.getScore2(), pos, 1, match.getWinner());
    }

    private void drawParticipantSlot(Graphics2D g2d, Participant p, int score, Point pos, int slotIndex, Participant winner) {
        int slotY = pos.y + (slotIndex * (MATCH_HEIGHT / 2));
        int slotHeight = MATCH_HEIGHT / 2;

        // Determine colors based on winner/loser status
        boolean isWinner = (p != null && p.equals(winner));
        boolean hasWinner = (winner != null);

        Color nameColor = AppTheme.TEXT_PRIMARY;
        if (hasWinner) {
            nameColor = isWinner ? AppTheme.TEXT_PRIMARY : AppTheme.TEXT_SECONDARY;
        }
        Font nameFont = isWinner ? AppTheme.FONT_BODY_BOLD : AppTheme.FONT_BODY_PLAIN;

        g2d.setFont(nameFont);
        g2d.setColor(nameColor);
        String name = (p != null) ? p.getName() : "---";
        g2d.drawString(name, pos.x + 15, slotY + slotHeight / 2 + 5);
        
        g2d.setFont(AppTheme.FONT_BODY_BOLD);
        g2d.drawString(String.valueOf(score), pos.x + MATCH_WIDTH - 25, slotY + slotHeight / 2 + 5);

        if (slotIndex == 0) {
            g2d.setColor(AppTheme.BACKGROUND_MAIN.brighter());
            g2d.drawLine(pos.x + 10, pos.y + slotHeight, pos.x + MATCH_WIDTH - 10, pos.y + slotHeight);
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