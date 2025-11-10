package bracketcraft;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParticipantsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final JPanel listContainerPanel; // The panel that holds the ParticipantRowPanels
    private ParticipantRowPanel draggedPanel = null; // The panel currently being dragged
    private Point dragOffset; // To make the drag feel smooth

    public ParticipantsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(AppTheme.BACKGROUND_SIDEBAR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setLayout(new BorderLayout(10, 20));

        // --- Top Title ---
        JLabel titleLabel = new JLabel("PARTICIPANTS");
        titleLabel.setFont(AppTheme.FONT_H1);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- Main list container with Scroll Pane ---
        listContainerPanel = new JPanel();
        listContainerPanel.setLayout(new BoxLayout(listContainerPanel, BoxLayout.Y_AXIS));
        listContainerPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(listContainerPanel);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(null);
        // Important: Increase scroll speed for a better feel
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Action Panel ---
        JPanel bottomBar = new JPanel(new BorderLayout(10, 0));
        bottomBar.setOpaque(false);
        JButton shuffleButton = createIconButton("resources/shuffle_icon.png", "Randomize Seeds");
        JButton addButton = new JButton("+ ADD PARTICIPANT");
        stylePrimaryButton(addButton);
        bottomBar.add(shuffleButton, BorderLayout.WEST);
        bottomBar.add(addButton, BorderLayout.EAST);

        JButton generateBracketButton = new JButton("Generate Bracket");
        stylePrimaryButton(generateBracketButton);

        JPanel actionPanel = new JPanel(new BorderLayout(10, 10));
        actionPanel.setOpaque(false);
        actionPanel.add(bottomBar, BorderLayout.NORTH);
        actionPanel.add(generateBracketButton, BorderLayout.SOUTH);
        add(actionPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        addButton.addActionListener(e -> addParticipantRow());
        shuffleButton.addActionListener(e -> shuffleParticipants());
        generateBracketButton.addActionListener(e -> mainFrame.generateAndShowBracket());

        // --- Add some default participants for testing ---
        addParticipantRow("Team Alpha");
        addParticipantRow("Team Bravo");
        addParticipantRow("Team Charlie");
        addParticipantRow("Team Delta");
    }

    /**
     * Iterates through the ParticipantRowPanels and collects their names in order.
     * @return A list of participant names.
     */
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

    // --- Core Logic Methods ---

    /** Adds a new, empty participant row to the list. */
    private void addParticipantRow() {
        addParticipantRow("Participant " + (listContainerPanel.getComponentCount() + 1));
    }

    /** Adds a participant row with a predefined name to the list. */
    private void addParticipantRow(String name) {
        ParticipantRowPanel newRow = new ParticipantRowPanel(name);
        listContainerPanel.add(newRow);
        renumberRows();
        listContainerPanel.revalidate();
        listContainerPanel.repaint();
    }
    
    /** Removes a specific row from the list. */
    private void removeParticipantRow(ParticipantRowPanel row) {
        listContainerPanel.remove(row);
        renumberRows();
        listContainerPanel.revalidate();
        listContainerPanel.repaint();
    }
    
    /** Iterates through all rows and updates their seed number. */
    private void renumberRows() {
        for (int i = 0; i < listContainerPanel.getComponentCount(); i++) {
            if (listContainerPanel.getComponent(i) instanceof ParticipantRowPanel) {
                ((ParticipantRowPanel) listContainerPanel.getComponent(i)).setSeed(i + 1);
            }
        }
    }
    
    /** Randomly shuffles the names of the existing participants. */
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
    }

    // --- UI Styling Methods ---

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
            button.setText("\u21C6"); // Shuffle symbol
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

    // =========================================================================
    // == INNER CLASS: ParticipantRowPanel
    // =========================================================================
    /**
     * A self-contained component representing a single row in the participant list.
     * It includes a drag handle, seed number, name field, and a remove button.
     */
    private class ParticipantRowPanel extends JPanel {
        private final JLabel seedLabel;
        private final JTextField nameField;
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

            // --- Drag Handle ---
            JLabel dragHandle = new JLabel("\u2630"); // Trigram for heaven symbol (looks like a handle)
            dragHandle.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 20));
            dragHandle.setForeground(AppTheme.TEXT_SECONDARY);
            dragHandle.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            dragHandle.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            add(dragHandle, BorderLayout.WEST);

            // --- Main Content (Seed + Name) ---
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

            // --- Remove Button ---
            JButton removeButton = new JButton("\u00D7"); // Multiplication sign (X)
            removeButton.setFont(AppTheme.FONT_H1);
            removeButton.setForeground(AppTheme.TEXT_SECONDARY);
            removeButton.setOpaque(false);
            removeButton.setContentAreaFilled(false);
            removeButton.setBorderPainted(false);
            removeButton.setFocusPainted(false);
            removeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            removeButton.addActionListener(e -> removeParticipantRow(this));
            removeButton.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { removeButton.setForeground(Color.RED); }
                public void mouseExited(MouseEvent e) { removeButton.setForeground(AppTheme.TEXT_SECONDARY); }
            });
            add(removeButton, BorderLayout.EAST);
            
            // --- Drag and Drop Listeners ---
            DragListener listener = new DragListener();
            dragHandle.addMouseListener(listener);
            dragHandle.addMouseMotionListener(listener);
        }

        public String getParticipantName() { return nameField.getText(); }
        public void setParticipantName(String name) { nameField.setText(name); }
        public void setSeed(int seed) { seedLabel.setText(String.format("%02d", seed)); }

        // --- Inner class for handling drag-and-drop logic ---
        private class DragListener extends MouseAdapter {
            @Override
            public void mousePressed(MouseEvent e) {
                draggedPanel = ParticipantRowPanel.this;
                dragOffset = e.getPoint(); // Get mouse pos relative to the handle
                draggedPanel.setBorder(dragBorder);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (draggedPanel != null) {
                    draggedPanel.setBorder(defaultBorder);
                    renumberRows(); // Finalize seed numbers after drop
                }
                draggedPanel = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedPanel == null) return;
                
                // Get the mouse cursor's position relative to the main container
                Point mousePos = SwingUtilities.convertPoint(draggedPanel, e.getPoint(), listContainerPanel);
                
                // Find which component is under the cursor
                int targetIndex = -1;
                for (int i = 0; i < listContainerPanel.getComponentCount(); i++) {
                    Component comp = listContainerPanel.getComponent(i);
                    if (comp.getBounds().contains(mousePos)) {
                        targetIndex = i;
                        break;
                    }
                }
                
                int currentIndex = listContainerPanel.getComponentZOrder(draggedPanel);
                
                // If a valid target is found and it's not the current position
                if (targetIndex != -1 && targetIndex != currentIndex) {
                    // Move the component in the container's component list
                    listContainerPanel.setComponentZOrder(draggedPanel, targetIndex);
                    // Re-layout and repaint to show the change
                    listContainerPanel.revalidate();
                    listContainerPanel.repaint();
                }
            }
        }
    }
}