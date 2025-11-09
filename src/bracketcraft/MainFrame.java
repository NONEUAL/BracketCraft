package bracketcraft;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The main window for the Bracket Maker application.a
 * Handles all UI setup, user interactions, and visual display of the tournament.
 */
public class MainFrame extends javax.swing.JFrame {

    // --- Core Application Data ---
    private Tournament currentTournament;
    private List<JTextField> participantFields;

    // --- UI Components ---
    private JMenuBar menuBar;
    private JPanel setupPanel;
    private JTextField tournamentNameField;
    private JSpinner participantsSpinner;
    private JButton createTournamentButton;
    private JPanel participantEntryPanel;
    private JScrollPane participantScrollPane;
    private JButton generateBracketButton;
    private JPanel bracketPanel;

    /**
     * Creates new form MainFrame.
     */
    public MainFrame() {
        this.participantFields = new ArrayList<>();
        initComponents();
    }

    /**
     * Initializes and lays out all user interface components.
     */
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Bracket Maker v1.0");
        setMinimumSize(new Dimension(800, 600));

        // --- Menu Bar Setup ---
        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveMenuItem = new JMenuItem("Save Tournament");
        JMenuItem loadMenuItem = new JMenuItem("Load Tournament");
        saveMenuItem.addActionListener(this::saveTournamentAction);
        loadMenuItem.addActionListener(this::loadTournamentAction);
        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // --- Main Layout ---
        getContentPane().setLayout(new BorderLayout(10, 10));

        // --- 1. Tournament Setup Panel (Top) ---
        setupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        setupPanel.setBorder(BorderFactory.createTitledBorder("1. Tournament Setup"));
        setupPanel.add(new JLabel("Tournament Name:"));
        tournamentNameField = new JTextField(20);
        setupPanel.add(tournamentNameField);
        setupPanel.add(new JLabel("Number of Participants:"));
        participantsSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 128, 1));
        setupPanel.add(participantsSpinner);
        createTournamentButton = new JButton("Create");
        createTournamentButton.addActionListener(this::createTournamentAction);
        setupPanel.add(createTournamentButton);
        getContentPane().add(setupPanel, BorderLayout.NORTH);

        // --- Center Panel (holds participant entry and bracket) ---
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        // --- 2. Participant Entry Panel (Left) ---
        JPanel leftPanel = new JPanel(new BorderLayout(0, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("2. Participants"));
        participantEntryPanel = new JPanel();
        participantEntryPanel.setLayout(new BoxLayout(participantEntryPanel, BoxLayout.Y_AXIS));
        participantScrollPane = new JScrollPane(participantEntryPanel);
        participantScrollPane.setPreferredSize(new Dimension(250, 0));
        generateBracketButton = new JButton("Generate Bracket");
        generateBracketButton.addActionListener(this::generateBracketAction);
        generateBracketButton.setVisible(false);
        leftPanel.add(participantScrollPane, BorderLayout.CENTER);
        leftPanel.add(generateBracketButton, BorderLayout.SOUTH);
        centerPanel.add(leftPanel, BorderLayout.WEST);

        // --- 3. Bracket Panel (Center) ---
        bracketPanel = new JPanel();
        bracketPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        bracketPanel.setBorder(BorderFactory.createTitledBorder("3. Bracket"));
        JScrollPane bracketScrollPane = new JScrollPane(bracketPanel);
        centerPanel.add(bracketScrollPane, BorderLayout.CENTER);

        getContentPane().add(centerPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null); // Center the frame
    }

    /**
     * Generates text fields based on the number of participants selected.
     */
    private void createTournamentAction(ActionEvent e) {
        int numParticipants = (Integer) participantsSpinner.getValue();
        participantEntryPanel.removeAll();
        participantFields.clear();

        for (int i = 0; i < numParticipants; i++) {
            JTextField field = new JTextField("Participant " + (i + 1), 15);
            participantEntryPanel.add(field);
            participantEntryPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            participantFields.add(field);
        }

        generateBracketButton.setVisible(true);
        participantEntryPanel.revalidate();
        participantEntryPanel.repaint();
    }

    /**
     * Collects participant names, creates a Tournament object, and displays the bracket.
     */
    private void generateBracketAction(ActionEvent e) {
        if (tournamentNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a tournament name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Participant> participants = new ArrayList<>();
        for (JTextField field : participantFields) {
            participants.add(new Participant(field.getText().trim()));
        }

        currentTournament = new Tournament(tournamentNameField.getText(), participants);
        currentTournament.generateBracket();
        displayBracket();
    }

    /**
     * Clears and redraws the entire bracket panel based on the current tournament state.
     */
    private void displayBracket() {
        bracketPanel.removeAll();
        if (currentTournament == null) {
            bracketPanel.revalidate();
            bracketPanel.repaint();
            return;
        }

        setTitle("Bracket Maker v1.0 - " + currentTournament.getTournamentName());

        // Create a visual column for each round
        for (int i = 0; i < currentTournament.getRounds().size(); i++) {
            JPanel roundPanel = new JPanel();
            roundPanel.setLayout(new BoxLayout(roundPanel, BoxLayout.Y_AXIS));
            roundPanel.setBorder(BorderFactory.createTitledBorder("Round " + (i + 1)));

            for (Match match : currentTournament.getRounds().get(i)) {
                roundPanel.add(createMatchPanel(match));
                roundPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
            }
            bracketPanel.add(roundPanel);
        }

        bracketPanel.revalidate();
        bracketPanel.repaint();
    }

    /**
     * Creates a single JPanel to visually represent one match.
     */
    private JPanel createMatchPanel(Match match) {
        JPanel matchPanel = new JPanel(new BorderLayout(5, 5));
        matchPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        Participant p1 = match.getParticipant1();
        Participant p2 = match.getParticipant2();

        JButton p1Button = new JButton(p1 != null ? p1.getName() : "TBD");
        p1Button.setEnabled(p1 != null && p2 != null && match.getWinner() == null);
        p1Button.addActionListener(e -> declareWinner(match, p1));

        JButton p2Button = new JButton(p2 != null ? p2.getName() : "TBD");
        p2Button.setEnabled(p1 != null && p2 != null && match.getWinner() == null);
        p2Button.addActionListener(e -> declareWinner(match, p2));

        matchPanel.add(p1Button, BorderLayout.NORTH);
        matchPanel.add(new JLabel("vs", SwingConstants.CENTER), BorderLayout.CENTER);
        matchPanel.add(p2Button, BorderLayout.SOUTH);

        // Highlight the winner if one has been declared
        if (match.getWinner() != null) {
            if (match.getWinner() == p1) {
                p1Button.setBackground(Color.GREEN);
            } else if (match.getWinner() == p2) {
                p2Button.setBackground(Color.GREEN);
            }
        }
        return matchPanel;
    }

    /**
     * Sets a match's winner and advances them to the next round.
     */
    private void declareWinner(Match match, Participant winner) {
        match.setWinner(winner);
        Match nextMatch = match.getNextMatch();

        if (nextMatch != null) {
            // Fill the next available slot in the next match
            if (nextMatch.getParticipant1() == null) {
                nextMatch.setParticipant1(winner);
            } else {
                nextMatch.setParticipant2(winner);
            }
        } else {
            // This was the final match
            JOptionPane.showMessageDialog(this, winner.getName() + " is the champion!", "Tournament Over", JOptionPane.INFORMATION_MESSAGE);
        }

        displayBracket(); // Refresh the UI
    }

    /**
     * Saves the current Tournament object to a file.
     */
    private void saveTournamentAction(ActionEvent e) {
        if (currentTournament == null) return;
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileChooser.getSelectedFile()))) {
                oos.writeObject(currentTournament);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file.", "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Loads a Tournament object from a file and updates the UI.
     */
    private void loadTournamentAction(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileChooser.getSelectedFile()))) {
                currentTournament = (Tournament) ois.readObject();
                tournamentNameField.setText(currentTournament.getTournamentName());
                participantEntryPanel.removeAll();
                generateBracketButton.setVisible(false);
                displayBracket();
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Error loading file.", "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Main entry point of the application.
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }
}