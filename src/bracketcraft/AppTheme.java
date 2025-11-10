package bracketcraft;

import java.awt.Color;
import java.awt.Font;

public class AppTheme {
    // --- The Raw Color Palette ---
    private static final Color DARK_CHARCOAL = new Color(0x2D3336);
    private static final Color SLATE_GREY = new Color(0x4C5357);
    private static final Color MEDIUM_GREY = new Color(0x6E7579);
    private static final Color LIGHT_GREY = new Color(0xA9AEB1);
    private static final Color OFF_WHITE = new Color(0xECEFF1);
    private static final Color VIBRANT_GREEN = new Color(0x61C239);
    
    // --- Semantic Colors (Mapped for UI components) ---
    public static final Color BACKGROUND_MAIN = SLATE_GREY;
    public static final Color BACKGROUND_SIDEBAR = DARK_CHARCOAL;
    public static final Color BACKGROUND_SIDEBAR_HOVER = new Color(0x3C4449); 
    public static final Color BACKGROUND_INPUT = MEDIUM_GREY;
    public static final Color BORDER_COLOR = LIGHT_GREY;
    public static final Color ACCENT_PRIMARY = VIBRANT_GREEN;
    
    public static final Color TEXT_PRIMARY = OFF_WHITE;
    public static final Color TEXT_SECONDARY = LIGHT_GREY;
    public static final Color TEXT_ACCENT = VIBRANT_GREEN;
    public static final Color TEXT_ON_ACCENT = Color.BLACK;

    // --- NEWLY ADDED: Semantic Colors for the Bracket Display ---
    public static final Color BACKGROUND_PANEL = DARK_CHARCOAL; // For match boxes
    public static final Color BRACKET_LINE_COLOR = DARK_CHARCOAL; // For connector lines
    public static final Color WINNER_ACCENT_BACKGROUND = VIBRANT_GREEN; // For the winner advancement indicator
    
    // --- Semantic Fonts ---
    public static final Font FONT_H1 = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY_PLAIN = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_INPUT_LABEL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    
    // --- NEWLY ADDED: Semantic Fonts for the Bracket Display ---
    public static final Font FONT_BRACKET_HEADER = new Font("Segoe UI", Font.BOLD, 14);
}