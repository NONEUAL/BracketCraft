package bracketcraft;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParticipantsPanel extends JPanel {
    private final DefaultListModel<String> participantListModel;
    private final MainFrame mainFrame;
    private final JRadioButton randomButton;
    private final JRadioButton seededButton;

    public ParticipantsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.participantListModel = new DefaultListModel<>();

        setBackground(AppTheme.BACKGROUND_SIDEBAR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setLayout(new BorderLayout(10, 20));

        // Title and Seeding Options
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("PARTICIPANTS");
        titleLabel.setFont(AppTheme.FONT_H1);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel seedingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        seedingPanel.setOpaque(false);
        randomButton = createRadioButton("Random");
        seededButton = createRadioButton("Seeded");
        ButtonGroup seedingGroup = new ButtonGroup();
        seedingGroup.add(randomButton);
        seedingGroup.add(seededButton);
        seedingPanel.add(randomButton);
        seedingPanel.add(seededButton);
        randomButton.setSelected(true); // Default
        topPanel.add(seedingPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // List and Buttons from previous response...
        
        JButton generateBracketButton = new JButton("Generate Bracket");
        generateBracketButton.addActionListener(e -> {
            String seeding = randomButton.isSelected() ? "Random" : "Seeded";
            mainFrame.generateAndShowBracket(getParticipants(), seeding);
        });
        // Remainder of panel setup...
    }
    
    private List<String> getParticipants() { /*...*/ return List.of(); }

    private JRadioButton createRadioButton(String text) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(AppTheme.FONT_BODY_PLAIN);
        rb.setForeground(AppTheme.TEXT_SECONDARY);
        rb.setOpaque(false);
        return rb;
    }
}