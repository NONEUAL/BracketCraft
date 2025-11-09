package bracketcraft;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

public class BracketInfoPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTextField bracketNameField;
    private JTextField sportGameField;

    public BracketInfoPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
    }
    
    public String getBracketName() { return bracketNameField.getText(); }
    public String getSportGameName() { return sportGameField.getText(); }

    private void initComponents() {
        setBackground(AppTheme.BACKGROUND_SIDEBAR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        int y = 0;

        gbc.insets = new Insets(0, 0, 30, 0);
        gbc.gridy = y++;
        add(createHeaderLabel("BRACKET INFORMATION"), gbc);
        gbc.insets = new Insets(0, 0, 15, 0);

        gbc.gridy = y++; add(createInputLabel("Bracket Name"), gbc);
        gbc.gridy = y++; bracketNameField = createTextField("Untitled Bracket"); add(bracketNameField, gbc);

        gbc.gridy = y++; add(createInputLabel("Bracket Type"), gbc);
        gbc.gridy = y++; add(createComboBox(new String[]{"Single Elimination", "Double Elimination"}), gbc);

        gbc.gridy = y++; add(createInputLabel("Sport / Game"), gbc);
        gbc.gridy = y++; sportGameField = createTextField(""); add(sportGameField, gbc);

        // --- Rules Button ---
        gbc.gridy = y++;
        JButton rulesButton = new JButton("View/Edit Rules");
        rulesButton.setFont(AppTheme.FONT_BUTTON);
        rulesButton.setBackground(AppTheme.BACKGROUND_INPUT);
        rulesButton.setForeground(AppTheme.TEXT_PRIMARY);
        rulesButton.setFocusPainted(false);
        rulesButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        
        // ***** FIX FOR BUTTON BACKGROUND *****
        // This forces the button to paint the background we assigned.
        rulesButton.setOpaque(true);
        
        rulesButton.addActionListener(e -> mainFrame.showRulesDialog());
        add(rulesButton, gbc);
        
        gbc.weighty = 1.0;
        gbc.gridy = y++;
        add(new JLabel(), gbc);
    }
    
    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.FONT_H1);
        label.setForeground(AppTheme.TEXT_PRIMARY);
        return label;
    }

    private JLabel createInputLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.FONT_INPUT_LABEL);
        label.setForeground(AppTheme.TEXT_SECONDARY);
        return label;
    }

    private JTextField createTextField(String defaultText) {
        JTextField tf = new JTextField(defaultText);
        tf.setFont(AppTheme.FONT_BODY_PLAIN);
        tf.setBackground(AppTheme.BACKGROUND_INPUT);
        tf.setForeground(AppTheme.TEXT_PRIMARY);
        tf.setCaretColor(AppTheme.TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        return tf;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(AppTheme.FONT_BODY_PLAIN);
        cb.setBackground(AppTheme.BACKGROUND_INPUT);
        cb.setForeground(AppTheme.TEXT_PRIMARY);
        cb.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR));

        // Custom UI to remove the default arrow button and fix background
        cb.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("\u25BC"); // Unicode for down-triangle
                button.setBackground(AppTheme.BACKGROUND_INPUT);
                button.setForeground(AppTheme.TEXT_SECONDARY);
                button.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                return button;
            }
        });

        // ***** FIX FOR COMBOBOX BACKGROUND *****
        // This custom renderer ensures both the selected item view and the
        // dropdown list are styled correctly.
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                // This call is crucial for text rendering
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                // Style for both the main view and the dropdown list items
                setBackground(AppTheme.BACKGROUND_INPUT);
                setForeground(AppTheme.TEXT_PRIMARY);
                setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); // Consistent padding
                
                // Style for hover/selection in the dropdown
                if (isSelected) {
                    setBackground(AppTheme.BACKGROUND_SIDEBAR_HOVER);
                }
                return this;
            }
        });
        return cb;
    }
}