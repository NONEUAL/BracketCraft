package bracketcraft;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
// --- FIXED: Added missing import statements ---
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BracketDisplayPanel extends JPanel {
    private Tournament tournament;
    private final JPanel drawingCanvas;
    private final JLabel footerLabel;

    private static final int MATCH_WIDTH = 180;
    private static final int MATCH_HEIGHT = 70;
    private static final int HORIZONTAL_GAP = 90;
    private static final int VERTICAL_GAP = 40;

    private double scale = 1.0;
    private Point2D.Double viewOffset = new Point2D.Double(0, 0);
    private Point lastDragPoint;
    
    private Map<Match, Point> matchPositions;

    public BracketDisplayPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        this.matchPositions = new HashMap<>();

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

        addInteractionListeners();
    }

    public void setTournament(Tournament newTournament) {
        this.tournament = newTournament;
        calculateAllMatchPositions();
        resetView();
    }
    
    private void resetView() {
        scale = 1.0;
        lastDragPoint = null;
        if (tournament != null && !tournament.getRounds().isEmpty()) {
            Rectangle bounds = getBracketBounds();
            viewOffset.x = (drawingCanvas.getWidth() - bounds.width) / 2.0 - bounds.x + 50;
            viewOffset.y = (drawingCanvas.getHeight() - bounds.height) / 2.0 - bounds.y + 50;
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
                Point2D.Double mousePointBeforeZoom = screenToWorld(e.getPoint());
                
                double zoomFactor = e.getWheelRotation() < 0 ? 1.1 : 1 / 1.1;
                scale *= zoomFactor;
                scale = Math.max(0.2, Math.min(scale, 3.0));

                Point2D.Double mousePointAfterZoom = screenToWorld(e.getPoint());
                
                viewOffset.x += (mousePointAfterZoom.x - mousePointBeforeZoom.x) * scale;
                viewOffset.y += (mousePointAfterZoom.y - mousePointBeforeZoom.y) * scale;

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
    
    private Point2D.Double screenToWorld(Point screenPoint) {
        return new Point2D.Double((screenPoint.x - viewOffset.x) / scale, (screenPoint.y - viewOffset.y) / scale);
    }
    
    private void drawBracket(Graphics g) {
        if (tournament == null || tournament.getRounds().isEmpty()) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.translate(viewOffset.x, viewOffset.y);
        g2d.scale(scale, scale);
        
        for (Map.Entry<Match, Point> entry : matchPositions.entrySet()) {
            Match match = entry.getKey();
            Point pos = entry.getValue();
            
            if (match.getNextMatch() != null && matchPositions.containsKey(match.getNextMatch())) {
                drawConnector(g2d, pos, matchPositions.get(match.getNextMatch()), match.getWinner() != null);
            }
            
            drawMatch(g2d, match, pos);
        }
    }

    private void calculateAllMatchPositions() {
        matchPositions.clear();
        if (tournament == null || tournament.getRounds().isEmpty()) return;

        List<List<Match>> rounds = tournament.getRounds();
        int numRounds = rounds.size();
        Match finalMatch = rounds.get(numRounds - 1).get(0);
        
        calculateMatchPosRecursive(finalMatch, numRounds - 1, 0);
    }

    private int calculateMatchPosRecursive(Match match, int roundIndex, int lastY) {
        int x = roundIndex * (MATCH_WIDTH + HORIZONTAL_GAP);
        List<Object> feeders = findFeedersFor(match);

        int y;
        if (feeders.isEmpty()) {
            y = lastY + MATCH_HEIGHT + VERTICAL_GAP;
        } else {
            int y1 = -1;
            if(feeders.get(0) instanceof Match){
                 y1 = calculateMatchPosRecursive((Match) feeders.get(0), roundIndex - 1, lastY);
            }
            
            int lastYForSecondChild = (y1 != -1) ? y1 : lastY;
            
            int y2 = -1;
            if (feeders.size() > 1 && feeders.get(1) instanceof Match){
                 y2 = calculateMatchPosRecursive((Match) feeders.get(1), roundIndex - 1, lastYForSecondChild);
            }

            if (y1 != -1 && y2 != -1) {
                y = (y1 + y2) / 2;
            } else if (y1 != -1) {
                 y = y1;
            } else if (y2 != -1) {
                 y = y2;
            } else {
                 y = lastYForSecondChild + MATCH_HEIGHT + VERTICAL_GAP;
            }
        }
        
        matchPositions.put(match, new Point(x, y));
        return y;
    }
    
    private List<Object> findFeedersFor(Match targetMatch) {
        List<Object> feeders = new ArrayList<>();
        if (tournament == null) return feeders;
        
        int targetRoundIndex = -1;
        List<List<Match>> rounds = tournament.getRounds();
        for(int i = 0; i < rounds.size(); i++){
            if(rounds.get(i).contains(targetMatch)){
                targetRoundIndex = i;
                break;
            }
        }
        
        if(targetRoundIndex < 1) return feeders;

        for(Match potentialFeeder : rounds.get(targetRoundIndex-1)) {
            if (targetMatch.equals(potentialFeeder.getNextMatch())) {
                feeders.add(potentialFeeder);
            }
        }
        
        if (targetMatch.getParticipant1() != null && feeders.stream().noneMatch(f -> (f instanceof Match) && ((Match)f).getWinner() == targetMatch.getParticipant1() )) feeders.add(targetMatch.getParticipant1());
        if (targetMatch.getParticipant2() != null && feeders.stream().noneMatch(f -> (f instanceof Match) && ((Match)f).getWinner() == targetMatch.getParticipant2() )) feeders.add(targetMatch.getParticipant2());
        
        return feeders;
    }

    private void drawConnector(Graphics2D g2d, Point p1, Point p2, boolean hasWinner) {
        // (This method is unchanged)
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
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(x1, y1, midX, y1);
        }
        g2d.setStroke(new BasicStroke(2));
    }

    private void drawMatch(Graphics2D g2d, Match match, Point pos) {
        // (This method is unchanged)
        if (match.getParticipant1() == null && match.getParticipant2() == null && match.getNextMatch() == null) return;
        g2d.setColor(AppTheme.BACKGROUND_PANEL);
        g2d.fill(new RoundRectangle2D.Float(pos.x, pos.y, MATCH_WIDTH, MATCH_HEIGHT, 15, 15));
        if (match.getWinner() != null) {
            g2d.setColor(AppTheme.WINNER_ACCENT_BACKGROUND);
            g2d.setStroke(new BasicStroke(3));
            g2d.draw(new RoundRectangle2D.Float(pos.x, pos.y, MATCH_WIDTH, MATCH_HEIGHT, 15, 15));
        }
        drawParticipantSlot(g2d, match.getParticipant1(), match.getScore1(), pos, 0, match.getWinner());
        drawParticipantSlot(g2d, match.getParticipant2(), match.getScore2(), pos, 1, match.getWinner());
    }

    private void drawParticipantSlot(Graphics2D g2d, Participant p, int score, Point pos, int slotIndex, Participant winner) {
        // (This method is unchanged)
        int slotY = pos.y + (slotIndex * (MATCH_HEIGHT / 2));
        int slotHeight = MATCH_HEIGHT / 2;
        boolean isWinner = (p != null && p.equals(winner));
        boolean hasWinner = (winner != null);
        Color nameColor = hasWinner ? (isWinner ? AppTheme.TEXT_PRIMARY : AppTheme.TEXT_SECONDARY) : AppTheme.TEXT_PRIMARY;
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
    
    private Rectangle getBracketBounds() {
        // (This method is unchanged)
        if (matchPositions.isEmpty()) return new Rectangle(0, 0, 0, 0);
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (Point p : matchPositions.values()) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            maxX = Math.max(maxX, p.x + MATCH_WIDTH);
            maxY = Math.max(maxY, p.y + MATCH_HEIGHT);
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    private JPanel createHeaderFooter(String text, Color textColor, Color accentColor, int align) {
        // (This method is unchanged)
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