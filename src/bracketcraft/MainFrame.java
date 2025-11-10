package bracketcraft;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends javax.swing.JFrame {

    private CardLayout infoCardLayout;
    private JPanel infoContainerPanel;
    private ParticipantsPanel participantsPanel;
    private BracketInfoPanel bracketInfoPanel;
    private BracketDisplayPanel bracketDisplayPanel;

    private boolean isInfoPanelVisible = true;
    private Timer animationTimer;
    private static final int INFO_PANEL_WIDTH = 350;
    private static final int ANIMATION_DURATION_MS = 200;

    public MainFrame() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BracketCraft");
        setMinimumSize(new Dimension(1280, 720));
        
        // --- Icon Sidebar ---
        JPanel iconSidebar = new JPanel();
        iconSidebar.setLayout(new BoxLayout(iconSidebar, BoxLayout.Y_AXIS));
        iconSidebar.setBackground(AppTheme.BACKGROUND_SIDEBAR);
        iconSidebar.setPreferredSize(new Dimension(70, 0));
        iconSidebar.add(createNavButton("Bracket Information", "resources/bracket_icon.png"));
        iconSidebar.add(createNavButton("Participants", "resources/participants_icon.png"));
        iconSidebar.add(Box.createVerticalGlue());
        iconSidebar.add(createNavButton("Settings", "resources/settings_icon.png"));
        iconSidebar.add(createNavButton("Back", "resources/back_icon.png")); // This is now the toggle
        getContentPane().add(iconSidebar, BorderLayout.WEST);

        // --- Main Content Area ---
        JPanel mainContentArea = new JPanel(new BorderLayout());
        mainContentArea.setBackground(AppTheme.BACKGROUND_MAIN);
        getContentPane().add(mainContentArea, BorderLayout.CENTER);

        infoCardLayout = new CardLayout();
        infoContainerPanel = new JPanel(infoCardLayout);
        infoContainerPanel.setPreferredSize(new Dimension(INFO_PANEL_WIDTH, 0));

        bracketInfoPanel = new BracketInfoPanel(this);
        participantsPanel = new ParticipantsPanel(this);
        infoContainerPanel.add(bracketInfoPanel, "Bracket Information");
        infoContainerPanel.add(participantsPanel, "Participants");

        bracketDisplayPanel = new BracketDisplayPanel();
        mainContentArea.add(infoContainerPanel, BorderLayout.WEST);
        mainContentArea.add(bracketDisplayPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    public void generateAndShowBracket() {
        List<String> participantNames = participantsPanel.getParticipantNames();
        String bracketName = bracketInfoPanel.getBracketName();
        String sportName = bracketInfoPanel.getSportGameName();

        if (participantNames.size() < 2) {
            JOptionPane.showMessageDialog(this, "You need at least 2 participants.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Participant> participants = new ArrayList<>();
        participantNames.forEach(name -> participants.add(new Participant(name)));
        Tournament tournament = new Tournament(bracketName, participants);
        tournament.generateBracket("Seeded"); // This will be updated in Phase 3
        bracketDisplayPanel.setTournament(tournament);
        bracketDisplayPanel.setSportName(sportName);
        if (isInfoPanelVisible) {
            toggleInfoPanel();
        }
    }

    /**
     * Creates and displays the modal dialog for viewing or editing tournament rules.
     */
    public void showRulesDialog() {
        // Create an instance of your RulesDialog, passing 'this' (the MainFrame) as its owner
        RulesDialog dialog = new RulesDialog(this);
        // Make the dialog visible to the user
        dialog.setVisible(true);
    }

    private void toggleInfoPanel() {
        if (animationTimer != null && animationTimer.isRunning()) return;
        int startWidth = infoContainerPanel.getWidth();
        int targetWidth = isInfoPanelVisible ? 0 : INFO_PANEL_WIDTH;
        isInfoPanelVisible = !isInfoPanelVisible;

        long startTime = System.currentTimeMillis();
        animationTimer = new Timer(5, (ActionEvent e) -> {
            long elapsedTime = System.currentTimeMillis() - startTime;
            double progress = (double) elapsedTime / ANIMATION_DURATION_MS;
            if (progress >= 1.0) {
                progress = 1.0;
                ((Timer) e.getSource()).stop();
            }
            int newWidth = (int) (startWidth + (targetWidth - startWidth) * progress);
            infoContainerPanel.setPreferredSize(new Dimension(newWidth, 0));
            infoContainerPanel.revalidate();
        });
        animationTimer.start();
    }

    private JButton createNavButton(String toolTipText, String iconPath) {
        JButton button = new JButton();
        button.setToolTipText(toolTipText);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            button.setText(toolTipText.substring(0, 1));
        }

        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        button.setMaximumSize(new Dimension(Short.MAX_VALUE, 70));

        // --- Rewritten Action and Hover Logic ---
        button.addActionListener(e -> {
            if ("Back".equals(toolTipText)) {
                // The "Back" button is ONLY for toggling.
                toggleInfoPanel();
            } else {
                // Other buttons switch the view...
                infoCardLayout.show(infoContainerPanel, toolTipText);
                // ...and expand the panel if it's currently hidden.
                if (!isInfoPanelVisible) {
                    toggleInfoPanel();
                }
            }
        });
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setContentAreaFilled(true);
                button.setBackground(AppTheme.BACKGROUND_SIDEBAR_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setContentAreaFilled(false);
            }
        });

        return button;
    }

    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) {}
        EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }
}