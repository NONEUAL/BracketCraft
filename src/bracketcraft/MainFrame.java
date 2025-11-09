package bracketcraft;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends javax.swing.JFrame {

    // --- UI Panels ---
    private JPanel iconSidebar;
    private JPanel infoContainerPanel; // The retractable panel
    private CardLayout infoCardLayout;
    private BracketDisplayPanel bracketDisplayPanel;
    private BracketInfoPanel bracketInfoPanel;
    private ParticipantsPanel participantsPanel;

    // --- Animation and State ---
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
        iconSidebar = new JPanel();
        iconSidebar.setLayout(new BoxLayout(iconSidebar, BoxLayout.Y_AXIS));
        iconSidebar.setBackground(AppTheme.BACKGROUND_SIDEBAR);
        iconSidebar.setPreferredSize(new Dimension(70, 0));
        iconSidebar.add(createNavButton("Bracket Information", "resources/bracket_icon.png"));
        iconSidebar.add(createNavButton("Participants", "resources/participants_icon.png"));
        iconSidebar.add(Box.createVerticalGlue());
        iconSidebar.add(createNavButton("Settings", "resources/settings_icon.png"));
        iconSidebar.add(createNavButton("Back", "resources/back_icon.png"));
        getContentPane().add(iconSidebar, BorderLayout.WEST);

        // --- Main Content Area ---
        JPanel mainContentArea = new JPanel(new BorderLayout());
        mainContentArea.setBackground(AppTheme.BACKGROUND_MAIN);
        getContentPane().add(mainContentArea, BorderLayout.CENTER);

        // --- Retractable Info Panel ---
        infoCardLayout = new CardLayout();
        infoContainerPanel = new JPanel(infoCardLayout);
        infoContainerPanel.setPreferredSize(new Dimension(INFO_PANEL_WIDTH, 0));

        bracketInfoPanel = new BracketInfoPanel(this);
        participantsPanel = new ParticipantsPanel(this);
        infoContainerPanel.add(bracketInfoPanel, "Bracket Information");
        infoContainerPanel.add(participantsPanel, "Participants");

        // --- Bracket Display Panel ---
        bracketDisplayPanel = new BracketDisplayPanel();
        mainContentArea.add(infoContainerPanel, BorderLayout.WEST);
        mainContentArea.add(bracketDisplayPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    public void generateAndShowBracket(List<String> participantNames, String seedingOption) {
        List<Participant> participants = new ArrayList<>();
        participantNames.forEach(name -> participants.add(new Participant(name)));
        Tournament newTournament = new Tournament(bracketInfoPanel.getBracketName(), participants);
        newTournament.generateBracket(seedingOption);
        bracketDisplayPanel.setSportName(bracketInfoPanel.getSportGameName());
        bracketDisplayPanel.setTournament(newTournament);
        if (isInfoPanelVisible) {
            toggleInfoPanel();
        }
    }
    
    public void showRulesDialog() {
        RulesDialog rulesDialog = new RulesDialog(this);
        rulesDialog.setVisible(true);
    }

    private void toggleInfoPanel() {
        if (animationTimer != null && animationTimer.isRunning()) return;
        int startWidth = infoContainerPanel.getWidth();
        int targetWidth = isInfoPanelVisible ? 0 : INFO_PANEL_WIDTH;
        isInfoPanelVisible = !isInfoPanelVisible;

        long startTime = System.currentTimeMillis();
        animationTimer = new Timer(5, e -> {
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
            System.err.println("Icon not found: " + iconPath);
            button.setText(toolTipText.substring(0, 1));
        }

        // Style for transparency and hover
        button.setBackground(AppTheme.BACKGROUND_SIDEBAR);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        button.setMaximumSize(new Dimension(Short.MAX_VALUE, 70));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setOpaque(true);
                button.setBackground(AppTheme.BACKGROUND_SIDEBAR_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setOpaque(false);
                button.setBackground(AppTheme.BACKGROUND_SIDEBAR);
            }
        });

        button.addActionListener(e -> {
            Component currentVisible = null;
            for(Component comp : infoContainerPanel.getComponents()){
                if(comp.isVisible()){
                    currentVisible = comp;
                    break;
                }
            }
            String currentPanelName = (currentVisible instanceof BracketInfoPanel) ? "Bracket Information" : "Participants";

            if (toolTipText.equals(currentPanelName) && isInfoPanelVisible) {
                toggleInfoPanel();
            } else if (!isInfoPanelVisible) {
                infoCardLayout.show(infoContainerPanel, toolTipText);
                toggleInfoPanel();
            } else {
                 infoCardLayout.show(infoContainerPanel, toolTipText);
            }
        });
        return button;
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Handle exception
        }
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }
}