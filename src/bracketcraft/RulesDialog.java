package bracketcraft;

import javax.swing.*;
import java.awt.*;

public class RulesDialog extends JDialog {

    private JTextArea rulesTextArea;

    /**
     * -- PHASE 3: Updated Constructor --
     * Creates the Rules Dialog, dynamically setting its state based on whether the tournament has started.
     * @param owner The parent frame.
     * @param isReadOnly If true, the text area will be non-editable.
     */
    public RulesDialog(Frame owner, boolean isReadOnly) {
        super(owner, "Tournament Rules", true);
        setSize(500, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(AppTheme.BACKGROUND_SIDEBAR);

        JLabel titleLabel = new JLabel("", SwingConstants.CENTER); // Text set below
        titleLabel.setFont(AppTheme.FONT_H1);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        rulesTextArea = new JTextArea("1. All matches are Best of 3.\n2. No substitutions allowed.\n3. Organizer's decision is final.");
        rulesTextArea.setFont(AppTheme.FONT_BODY_PLAIN);
        rulesTextArea.setForeground(AppTheme.TEXT_PRIMARY);
        rulesTextArea.setLineWrap(true);
        rulesTextArea.setWrapStyleWord(true);
        rulesTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(rulesTextArea);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        JButton actionButton = new JButton(); // Text set below
        actionButton.setFont(AppTheme.FONT_BUTTON);
        actionButton.addActionListener(e -> dispose());
        
        // --- PHASE 3: Conditional State Logic ---
        if (isReadOnly) {
            titleLabel.setText("View Rules");
            actionButton.setText("Close");
            rulesTextArea.setEditable(false);
            // Use a slightly different background to visually indicate it's read-only.
            rulesTextArea.setBackground(AppTheme.BACKGROUND_SIDEBAR); 
            actionButton.setBackground(AppTheme.BACKGROUND_INPUT);
            actionButton.setForeground(AppTheme.TEXT_PRIMARY);
        } else {
            titleLabel.setText("View/Edit Rules");
            actionButton.setText("Save & Close");
            rulesTextArea.setEditable(true);
            rulesTextArea.setBackground(AppTheme.BACKGROUND_INPUT);
            actionButton.setBackground(AppTheme.ACCENT_PRIMARY);
            actionButton.setForeground(AppTheme.TEXT_ON_ACCENT);
            // In the future, you would add the save logic here before dispose().
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.add(actionButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}