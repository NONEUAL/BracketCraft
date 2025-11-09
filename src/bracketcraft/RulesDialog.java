package bracketcraft;

import javax.swing.*;
import java.awt.*;

public class RulesDialog extends JDialog {

    private JTextArea rulesTextArea;

    public RulesDialog(Frame owner) {
        super(owner, "Tournament Rules", true);
        setSize(500, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(AppTheme.BACKGROUND_SIDEBAR);

        JLabel titleLabel = new JLabel("View/Edit Rules", SwingConstants.CENTER);
        titleLabel.setFont(AppTheme.FONT_H1);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        rulesTextArea = new JTextArea("1. All matches are Best of 3.\n2. No substitutions allowed.\n3. Organizer's decision is final.");
        rulesTextArea.setBackground(AppTheme.BACKGROUND_INPUT);
        rulesTextArea.setForeground(AppTheme.TEXT_PRIMARY);
        rulesTextArea.setFont(AppTheme.FONT_BODY_PLAIN);
        rulesTextArea.setLineWrap(true);
        rulesTextArea.setWrapStyleWord(true);
        rulesTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(rulesTextArea);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Save & Close");
        closeButton.setFont(AppTheme.FONT_BUTTON); // Uses the corrected font
        closeButton.setBackground(AppTheme.ACCENT_PRIMARY);
        closeButton.setForeground(AppTheme.TEXT_ON_ACCENT);
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}