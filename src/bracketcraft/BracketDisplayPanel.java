package bracketcraft;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BracketDisplayPanel extends JPanel {
    private Tournament tournament;
    private final JPanel drawingCanvas;
    private final JLabel footerLabel;

    private static final int MATCH_WIDTH = 200;
    private static final int MATCH_HEIGHT = 80;
    private static final int HORIZONTAL_GAP = 120;
    private static final int VERTICAL_GAP = 20;

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
            viewOffset.x = (drawingCanvas.getWidth() - bounds.width * scale) / 2.0 - bounds.x * scale + 50;
            viewOffset.y = (drawingCanvas.getHeight() - bounds.height * scale) / 2.0 - bounds.y * scale + 50;
        } else {
            viewOffset.x = 0;
            viewOffset.y = 0;
        }
        repaint();
    }

    public void setSportName(String sportName) {
        footerLabel.setText(sportName != null && !sportName.trim().isEmpty() ? sportName.toUpperCase() + " " : "");
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
        
        // Draw connectors first
        for (Map.Entry<Match, Point> entry : matchPositions.entrySet()) {
            Match match = entry.getKey();
            Point pos = entry.getValue();
            
            if (match.getNextMatch() != null && matchPositions.containsKey(match.getNextMatch())) {
                drawConnector(g2d, pos, matchPositions.get(match.getNextMatch()), match.getWinner() != null);
            }
        }
        
        // Draw matches on top
        for (Map.Entry<Match, Point> entry : matchPositions.entrySet()) {
            Match match = entry.getKey();
            Point pos = entry.getValue();
            drawMatch(g2d, match, pos);
        }
    }

    private void calculateAllMatchPositions() {
        matchPositions.clear();
        if (tournament == null || tournament.getRounds().isEmpty()) return;

        List<List<Match>> rounds = tournament.getRounds();
        
        // Use consistent base spacing (for that good shit) 
        int baseSpacing = MATCH_HEIGHT + VERTICAL_GAP;
        
        // Calculate positions left to right (round 0 is leftmost)
        for (int roundIndex = 0; roundIndex < rounds.size(); roundIndex++) {
            List<Match> roundMatches = rounds.get(roundIndex);
            int x = roundIndex * (MATCH_WIDTH + HORIZONTAL_GAP);
            
            for (int matchIndex = 0; matchIndex < roundMatches.size(); matchIndex++) {
                Match match = roundMatches.get(matchIndex);
                
                // Calculate Y position
                int y;
                if (roundIndex == 0) {
                    // First round: evenly spaced
                    y = matchIndex * baseSpacing;
                } else {
                    // Later rounds: center perfectly between the two feeding matches 
                    List<Match> prevRound = rounds.get(roundIndex - 1);
                    int feeder1Index = matchIndex * 2;
                    int feeder2Index = matchIndex * 2 + 1;
                    
                    if (feeder1Index < prevRound.size() && feeder2Index < prevRound.size()) {
                        Point pos1 = matchPositions.get(prevRound.get(feeder1Index));
                        Point pos2 = matchPositions.get(prevRound.get(feeder2Index));
                        if (pos1 != null && pos2 != null) {
                            // centering that shi: midpoint of the two match centers
                            int center1 = pos1.y + MATCH_HEIGHT / 2;
                            int center2 = pos2.y + MATCH_HEIGHT / 2;
                            y = (center1 + center2) / 2 - MATCH_HEIGHT / 2;
                        } else {
                            y = matchIndex * baseSpacing * (int) Math.pow(2, roundIndex);
                        }
                    } else {
                        y = matchIndex * baseSpacing * (int) Math.pow(2, roundIndex);
                    }
                }
                
                matchPositions.put(match, new Point(x, y));
            }
        }
    }

    private void drawConnector(Graphics2D g2d, Point p1, Point p2, boolean hasWinner) {
        int x1 = p1.x + MATCH_WIDTH;
        int y1 = p1.y + MATCH_HEIGHT / 2;
        int x2 = p2.x;
        int y2 = p2.y + MATCH_HEIGHT / 2;
        int midX = (x1 + x2) / 2;
        
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.setColor(new Color(90, 95, 100));
        
        // Draw connector lines 
        g2d.drawLine(x1, y1, midX, y1);
        g2d.drawLine(midX, y1, midX, y2);
        g2d.drawLine(midX, y2, x2, y2);
    }

    private void drawMatch(Graphics2D g2d, Match match, Point pos) {
        // Draw match background with color
        g2d.setColor(new Color(55, 60, 65));
        g2d.fill(new RoundRectangle2D.Float(pos.x, pos.y, MATCH_WIDTH, MATCH_HEIGHT, 10, 10));
        
        // Draw border 
        g2d.setColor(new Color(75, 80, 85));
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(new RoundRectangle2D.Float(pos.x, pos.y, MATCH_WIDTH, MATCH_HEIGHT, 10, 10));
        
        drawParticipantSlot(g2d, match.getParticipant1(), match.getScore1(), pos, 0, match.getWinner());
        drawParticipantSlot(g2d, match.getParticipant2(), match.getScore2(), pos, 1, match.getWinner());
    }

    private void drawParticipantSlot(Graphics2D g2d, Participant p, int score, Point pos, int slotIndex, Participant winner) {
        int slotY = pos.y + (slotIndex * (MATCH_HEIGHT / 2));
        int slotHeight = MATCH_HEIGHT / 2;
        boolean isWinner = (p != null && p.equals(winner));
        
        // white text para sa bulag
        g2d.setColor(new Color(245, 245, 245));
        Font nameFont = new Font("Segoe UI", Font.PLAIN, 15);
        g2d.setFont(nameFont);
        
        String name = (p != null) ? p.getName() : "---";
        g2d.drawString(name, pos.x + 18, slotY + slotHeight / 2 + 6);
        
        // Draw score (still need to update)
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2d.drawString(String.valueOf(score), pos.x + MATCH_WIDTH - 35, slotY + slotHeight / 2 + 6);
        
        // Draw divider line between participants
        if (slotIndex == 0) {
            g2d.setColor(new Color(85, 90, 95));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawLine(pos.x + 12, pos.y + slotHeight, pos.x + MATCH_WIDTH - 12, pos.y + slotHeight);
        }
    }
    
    private Rectangle getBracketBounds() {
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