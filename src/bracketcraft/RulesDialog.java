package bracketcraft;

import javax.swing.*;
import java.awt.*;

public class RulesDialog extends JDialog {

    private final JTextArea rulesTextArea;
    private final Tournament tournament; 

    /**
     * -- REFINED CONSTRUCTOR --
     * Now accepts the tournament object to read from and save to.
     * @param owner The parent frame.
     * @param isReadOnly If true, the text area will be non-editable.
     * @param tournament The tournament object holding the rules data.
     */
    public RulesDialog(Frame owner, boolean isReadOnly, Tournament tournament) {
        super(owner, "Tournament Rules", true);
        this.tournament = tournament; // Store the reference ( remove if have database :< )
        
        setSize(500, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(AppTheme.BACKGROUND_SIDEBAR);

        JLabel titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(AppTheme.FONT_H1);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        rulesTextArea = new JTextArea();
        // --- Load rules from the tournament object ---
        rulesTextArea.setText(tournament.getRules());
        rulesTextArea.setFont(AppTheme.FONT_BODY_PLAIN);
        rulesTextArea.setForeground(AppTheme.TEXT_PRIMARY);
        rulesTextArea.setLineWrap(true);
        rulesTextArea.setWrapStyleWord(true);
        rulesTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(rulesTextArea);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        JButton actionButton = new JButton();
        actionButton.setFont(AppTheme.FONT_BUTTON);
        
        if (isReadOnly) {
            titleLabel.setText("View Rules");
            actionButton.setText("Close");
            rulesTextArea.setEditable(false);
            rulesTextArea.setBackground(AppTheme.BACKGROUND_SIDEBAR); 
            actionButton.setBackground(AppTheme.BACKGROUND_INPUT);
            actionButton.setForeground(AppTheme.TEXT_PRIMARY);
            actionButton.addActionListener(e -> dispose());
        } else {
            titleLabel.setText("View/Edit Rules");
            actionButton.setText("Save & Close");
            rulesTextArea.setEditable(true);
            rulesTextArea.setBackground(AppTheme.BACKGROUND_INPUT);
            actionButton.setBackground(AppTheme.ACCENT_PRIMARY);
            actionButton.setForeground(AppTheme.TEXT_ON_ACCENT);
            // --- Save rules before closing ---
            actionButton.addActionListener(e -> {
                tournament.setRules(rulesTextArea.getText());
                dispose();
            });
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.add(actionButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}