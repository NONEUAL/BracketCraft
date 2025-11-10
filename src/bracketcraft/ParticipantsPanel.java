package bracketcraft;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ParticipantsPanel extends JPanel {
    private final MainFrame mainFrame;
    private final DefaultListModel<String> participantListModel;
    private final JList<String> participantList;
    private final JRadioButton randomButton;
    private final JRadioButton seededButton;

    public ParticipantsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.participantListModel = new DefaultListModel<>();

        setBackground(AppTheme.BACKGROUND_SIDEBAR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setLayout(new BorderLayout(10, 20));

        // --- Top Panel (Title and Seeding Options) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("PARTICIPANTS");
        titleLabel.setFont(AppTheme.FONT_H1);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel seedingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        seedingPanel.setOpaque(false);
        seedingPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        randomButton = createRadioButton("Random");
        seededButton = createRadioButton("Seeded");
        ButtonGroup seedingGroup = new ButtonGroup();
        seedingGroup.add(randomButton);
        seedingGroup.add(seededButton);
        seedingPanel.add(randomButton);
        seedingPanel.add(seededButton);
        randomButton.setSelected(true); // Default selection
        topPanel.add(seedingPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // --- Participant List (Center) ---
        participantList = new JList<>(participantListModel);
        participantList.setBackground(AppTheme.BACKGROUND_INPUT);
        participantList.setForeground(AppTheme.TEXT_PRIMARY);
        participantList.setFont(AppTheme.FONT_BODY_PLAIN);

        // Custom renderer to style the list items and selection color
        participantList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                if (isSelected) {
                    label.setBackground(AppTheme.ACCENT_PRIMARY);
                    label.setForeground(AppTheme.TEXT_ON_ACCENT);
                }
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(participantList);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR));
        add(scrollPane, BorderLayout.CENTER);
        
        // --- Bottom Panel (Buttons) ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);
        
        JPanel addRemovePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        addRemovePanel.setOpaque(false);
        JButton addButton = createButton("Add");
        JButton removeButton = createButton("Remove");
        addRemovePanel.add(addButton);
        addRemovePanel.add(removeButton);
        bottomPanel.add(addRemovePanel, BorderLayout.CENTER);

        JButton generateBracketButton = new JButton("Generate Bracket");
        generateBracketButton.setFont(AppTheme.FONT_BUTTON);
        generateBracketButton.setBackground(AppTheme.ACCENT_PRIMARY);
        generateBracketButton.setForeground(AppTheme.TEXT_ON_ACCENT);
        bottomPanel.add(generateBracketButton, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
        
        // --- ACTIONS (LOGIC TO BE IMPLEMENTED NEXT) ---
        addButton.addActionListener(e -> {
            // Placeholder: JOptionPane to add name
            System.out.println("Add button clicked");
        });
        
        removeButton.addActionListener(e -> {
            // Placeholder: Remove selected from list model
            System.out.println("Remove button clicked");
        });
        
        generateBracketButton.addActionListener(e -> {
            // Placeholder: Get list and seeding, then call MainFrame
            System.out.println("Generate Bracket button clicked");
        });
    }

    private JRadioButton createRadioButton(String text) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(AppTheme.FONT_BODY_PLAIN);
        rb.setForeground(AppTheme.TEXT_SECONDARY);
        rb.setOpaque(false);
        rb.setFocusPainted(false);
        return rb;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(AppTheme.FONT_BUTTON);
        button.setBackground(AppTheme.BACKGROUND_INPUT);
        button.setForeground(AppTheme.TEXT_PRIMARY);
        return button;
    }
}