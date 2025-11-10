package bracketcraft;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParticipantsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final JPanel listContainerPanel;
    private ParticipantRowPanel draggedPanel = null;
    
    private final JButton shuffleButton;
    private final JButton addButton;
    private final JButton startTournamentButton;

    public ParticipantsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(AppTheme.BACKGROUND_SIDEBAR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setLayout(new BorderLayout(10, 20));

        JLabel titleLabel = new JLabel("PARTICIPANTS");
        titleLabel.setFont(AppTheme.FONT_H1);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        listContainerPanel = new JPanel();
        listContainerPanel.setLayout(new BoxLayout(listContainerPanel, BoxLayout.Y_AXIS));
        listContainerPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(listContainerPanel);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new BorderLayout(10, 0));
        bottomBar.setOpaque(false);
        this.shuffleButton = createIconButton("resources/shuffle_icon.png", "Randomize Seeds");
        this.addButton = new JButton("+ ADD PARTICIPANT");
        stylePrimaryButton(addButton);
        bottomBar.add(shuffleButton, BorderLayout.WEST);
        bottomBar.add(addButton, BorderLayout.EAST);

        this.startTournamentButton = new JButton("Start Tournament");
        stylePrimaryButton(startTournamentButton);

        JPanel actionPanel = new JPanel(new BorderLayout(10, 10));
        actionPanel.setOpaque(false);
        actionPanel.add(bottomBar, BorderLayout.NORTH);
        actionPanel.add(startTournamentButton, BorderLayout.SOUTH);
        add(actionPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addParticipantRow());
        shuffleButton.addActionListener(e -> shuffleParticipants());
        startTournamentButton.addActionListener(e -> mainFrame.startTournament());

        // --- FIXED: Add default participants without triggering a premature update ---
        listContainerPanel.add(new ParticipantRowPanel("Team 1"));
        listContainerPanel.add(new ParticipantRowPanel("Team 2"));
        listContainerPanel.add(new ParticipantRowPanel("Team 3"));
        listContainerPanel.add(new ParticipantRowPanel("Team 4"));
        renumberRows(); // Manually renumber after the initial add.
    }

    public List<String> getParticipantNames() {
        List<String> names = new ArrayList<>();
        for (Component comp : listContainerPanel.getComponents()) {
            if (comp instanceof ParticipantRowPanel) {
                String name = ((ParticipantRowPanel) comp).getParticipantName();
                if (name != null && !name.trim().isEmpty()) {
                    names.add(name);
                }
            }
        }
        return names;
    }
    
    public void setControlsEnabled(boolean enabled) {
        shuffleButton.setEnabled(enabled);
        addButton.setEnabled(enabled);
        
        for(Component comp : listContainerPanel.getComponents()) {
            if (comp instanceof ParticipantRowPanel) {
                ((ParticipantRowPanel) comp).setInteractionsEnabled(enabled);
            }
        }
    }

    private void addParticipantRow() {
        addParticipantRow("Team " + (listContainerPanel.getComponentCount() + 1));
    }

    private void addParticipantRow(String name) {
        ParticipantRowPanel newRow = new ParticipantRowPanel(name);
        listContainerPanel.add(newRow);
        renumberRows();
        listContainerPanel.revalidate();
        listContainerPanel.repaint();
        mainFrame.updateLiveBracketPreview(); // LIVE SYNC
    }
    
    private void removeParticipantRow(ParticipantRowPanel row) {
        listContainerPanel.remove(row);
        renumberRows();
        listContainerPanel.revalidate();
        listContainerPanel.repaint();
        mainFrame.updateLiveBracketPreview(); // LIVE SYNC
    }
    
    private void renumberRows() {
        for (int i = 0; i < listContainerPanel.getComponentCount(); i++) {
            if (listContainerPanel.getComponent(i) instanceof ParticipantRowPanel) {
                ((ParticipantRowPanel) listContainerPanel.getComponent(i)).setSeed(i + 1);
            }
        }
    }
    
    private void shuffleParticipants() {
        List<String> names = getParticipantNames();
        Collections.shuffle(names);

        Component[] components = listContainerPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof ParticipantRowPanel) {
                if (i < names.size()) {
                    ((ParticipantRowPanel) components[i]).setParticipantName(names.get(i));
                }
            }
        }
        mainFrame.updateLiveBracketPreview(); // LIVE SYNC
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(AppTheme.FONT_BUTTON);
        button.setBackground(AppTheme.ACCENT_PRIMARY);
        button.setForeground(AppTheme.TEXT_ON_ACCENT);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        Color original = AppTheme.ACCENT_PRIMARY;
        Color hover = original.brighter();
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { button.setBackground(hover); }
            public void mouseExited(MouseEvent evt) { button.setBackground(original); }
        });
    }

    private JButton createIconButton(String iconPath, String toolTip) {
        JButton button = new JButton();
        button.setToolTipText(toolTip);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            button.setText("\u21C6");
            button.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 16));
        }
        button.setBackground(AppTheme.BACKGROUND_INPUT);
        button.setForeground(AppTheme.TEXT_PRIMARY);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color original = AppTheme.BACKGROUND_INPUT;
        Color hover = AppTheme.BACKGROUND_SIDEBAR_HOVER;
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { button.setBackground(hover); }
            public void mouseExited(MouseEvent evt) { button.setBackground(original); }
        });

        return button;
    }

    private class ParticipantRowPanel extends JPanel {
        private final JLabel seedLabel;
        private final JTextField nameField;
        private final JLabel dragHandle;
        private final JButton removeButton;
        private final Border defaultBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        private final Border dragBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.ACCENT_PRIMARY, 1),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        );

        public ParticipantRowPanel(String name) {
            setLayout(new BorderLayout(10, 0));
            setOpaque(false);
            setMaximumSize(new Dimension(Short.MAX_VALUE, 45));
            setBorder(defaultBorder);

            dragHandle = new JLabel("\u2630");
            dragHandle.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 20));
            dragHandle.setForeground(AppTheme.TEXT_SECONDARY);
            dragHandle.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            dragHandle.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            add(dragHandle, BorderLayout.WEST);
            
            JPanel contentPanel = new JPanel(new BorderLayout(10, 0));
            contentPanel.setOpaque(false);
            
            seedLabel = new JLabel();
            seedLabel.setFont(AppTheme.FONT_BODY_BOLD);
            seedLabel.setForeground(AppTheme.TEXT_SECONDARY);
            contentPanel.add(seedLabel, BorderLayout.WEST);

            nameField = new JTextField(name);
            nameField.setFont(AppTheme.FONT_BODY_PLAIN);
            nameField.setBackground(AppTheme.BACKGROUND_INPUT);
            nameField.setForeground(AppTheme.TEXT_PRIMARY);
            nameField.setCaretColor(AppTheme.TEXT_PRIMARY);
            nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
            contentPanel.add(nameField, BorderLayout.CENTER);
            add(contentPanel, BorderLayout.CENTER);

            removeButton = new JButton("\u00D7");
            removeButton.setFont(AppTheme.FONT_H1);
            removeButton.setForeground(AppTheme.TEXT_SECONDARY);
            removeButton.setOpaque(false);
            removeButton.setContentAreaFilled(false);
            removeButton.setBorderPainted(false);
            removeButton.setFocusPainted(false);
            removeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            removeButton.addActionListener(e -> removeParticipantRow(this));
            removeButton.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { if(removeButton.isEnabled()) removeButton.setForeground(Color.RED); }
                public void mouseExited(MouseEvent e) { removeButton.setForeground(AppTheme.TEXT_SECONDARY); }
            });
            add(removeButton, BorderLayout.EAST);
            
            DragListener listener = new DragListener();
            dragHandle.addMouseListener(listener);
            dragHandle.addMouseMotionListener(listener);
        }

        public String getParticipantName() { return nameField.getText(); }
        public void setParticipantName(String name) { nameField.setText(name); }
        public void setSeed(int seed) { seedLabel.setText(String.format("%02d", seed)); }
        
        public void setInteractionsEnabled(boolean enabled) {
            nameField.setEditable(enabled);
            removeButton.setEnabled(enabled);
            dragHandle.setCursor(enabled ? new Cursor(Cursor.MOVE_CURSOR) : Cursor.getDefaultCursor());
            nameField.setBackground(enabled ? AppTheme.BACKGROUND_INPUT : AppTheme.BACKGROUND_SIDEBAR);
        }

        private class DragListener extends MouseAdapter {
             @Override
            public void mouseReleased(MouseEvent e) {
                if (draggedPanel != null) {
                    draggedPanel.setBorder(defaultBorder);
                    renumberRows();
                    mainFrame.updateLiveBracketPreview(); // LIVE SYNC
                }
                draggedPanel = null;
            }
            
             @Override
            public void mousePressed(MouseEvent e) {
                if (!startTournamentButton.isEnabled()) return;
                draggedPanel = ParticipantRowPanel.this;
                draggedPanel.setBorder(dragBorder);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedPanel == null) return;
                
                Point mousePos = SwingUtilities.convertPoint(draggedPanel, e.getPoint(), listContainerPanel);
                int targetIndex = -1;
                for (int i = 0; i < listContainerPanel.getComponentCount(); i++) {
                    Component comp = listContainerPanel.getComponent(i);
                    if (mousePos.y >= comp.getY() && mousePos.y <= comp.getY() + comp.getHeight()) {
                        targetIndex = i;
                        break;
                    }
                }
                
                if (targetIndex != -1 && targetIndex != listContainerPanel.getComponentZOrder(draggedPanel)) {
                    listContainerPanel.setComponentZOrder(draggedPanel, targetIndex);
                    listContainerPanel.revalidate();
                    listContainerPanel.repaint();
                }
            }
        }
    }
}