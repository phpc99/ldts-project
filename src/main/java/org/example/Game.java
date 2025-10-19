package org.example;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.terminal.swing.AWTTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.AWTTerminalFrame;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.net.URISyntaxException;
import java.net.URL;

public class Game {
    private Screen screen;
    private Grid grid;
    private int selectedOption = 0; // Tracks selected menu option (0 = 4x4, 1 = 6x6, 2 = 8x8)
    private Instant startTime; // To track the start time of the game
    private Duration totalTime; // To store the total time taken when the game is completed

    public Game() {
        try {
            // Load the custom font
            URL resource = getClass().getClassLoader().getResource("square.ttf");
            File fontFile = new File(resource.toURI());
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);

            // Configure terminal with the custom font
            Font loadedFont = font.deriveFont(Font.PLAIN, 25);
            AWTTerminalFontConfiguration fontConfig = AWTTerminalFontConfiguration.newInstance(loadedFont);
            DefaultTerminalFactory factory = new DefaultTerminalFactory()
                    .setTerminalEmulatorFontConfiguration(fontConfig)
                    .setForceAWTOverSwing(true)
                    .setInitialTerminalSize(new TerminalSize(60, 30));

            Terminal terminal = factory.createTerminal();
            ((AWTTerminalFrame) terminal).addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    e.getWindow().dispose();
                }
            });

            screen = new TerminalScreen(terminal);
            screen.setCursorPosition(null);
            screen.startScreen();
            screen.doResizeIfNecessary();
        } catch (IOException | FontFormatException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    void displayMainMenu() throws IOException {
        screen.clear();
        TextGraphics graphics = screen.newTextGraphics();
        graphics.setForegroundColor(TextColor.ANSI.CYAN);

        // Display title
        graphics.putString(25, 5, "Memory Card");

        // Display options
        String[] options = { "4x4", "6x6", "8x8" };
        for (int i = 0; i < options.length; i++) {
            if (i == selectedOption) {
                graphics.setForegroundColor(TextColor.ANSI.YELLOW); // Highlight selected option
            } else {
                graphics.setForegroundColor(TextColor.ANSI.WHITE);
            }
            graphics.putString(29, 8 + i, options[i]);
        }

        screen.refresh();
    }

    int getSelectedOption() {
        return selectedOption;
    }

    void processMenuKey(com.googlecode.lanterna.input.KeyStroke key) {
        switch (key.getKeyType()) {
            case ArrowUp -> selectedOption = (selectedOption > 0) ? selectedOption - 1 : selectedOption;
            case ArrowDown -> selectedOption = (selectedOption < 2) ? selectedOption + 1 : selectedOption;
            case Enter -> startGameWithSelectedOption(); // Start the game with the chosen grid size
        }
    }

    void startGameWithSelectedOption() {
        int gridSize;
        switch (selectedOption) {
            case 0 -> gridSize = 4;
            case 1 -> gridSize = 6;
            case 2 -> gridSize = 8;
            default -> throw new IllegalStateException("Unexpected value: " + selectedOption);
        }

        grid = new Grid(gridSize); // Initialize grid with selected size
        startTime = Instant.now(); // Start the timer
        runGameLoop();
    }

    private void runGameLoop() {
        try {
            int cursorX = 0, cursorY = 0;

            while (true) {
                draw(cursorX, cursorY); // Draw the current game state
                com.googlecode.lanterna.input.KeyStroke key = screen.readInput();
                if (key.getKeyType() == KeyType.Character && key.getCharacter() == 'q') break;
                if (key.getKeyType() == KeyType.EOF) break;

                // Process key and update cursor position
                switch (key.getKeyType()) {
                    case ArrowUp -> cursorY = Math.max(0, cursorY - 1);
                    case ArrowDown -> cursorY = Math.min(grid.getSize() - 1, cursorY + 1);
                    case ArrowLeft -> cursorX = Math.max(0, cursorX - 1);
                    case ArrowRight -> cursorX = Math.min(grid.getSize() - 1, cursorX + 1);
                    case Enter -> grid.selectCard(cursorX, cursorY);
                }

                // Check if we need to delay before checking for a match
                if (grid.shouldCheckMatch()) {
                    draw(cursorX, cursorY); // Draw with both cards revealed
                    screen.refresh();
                    Thread.sleep(1000); // 1-second delay to display the second card
                    grid.checkForMatch(); // Now check for a match after the delay
                }

                draw(cursorX, cursorY); // Redraw after match check

                if (grid.isGameOver()) {
                    totalTime = Duration.between(startTime, Instant.now());
                    displayEndScreen();
                    break;
                }
            }
            screen.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void draw(int cursorX, int cursorY) throws IOException {
        screen.clear();
        TextGraphics graphics = screen.newTextGraphics();

        // Terminal and grid size
        TerminalSize terminalSize = screen.getTerminalSize();
        int terminalWidth = terminalSize.getColumns();
        int terminalHeight = terminalSize.getRows();
        int gridSize = grid.getSize(); // 4, 6, or 8

        // Calculate offsets for centering the grid
        int gridOffsetX = (terminalWidth - 2 * gridSize) / 4; // Each card is 2 characters wide
        int gridOffsetY = (terminalHeight - gridSize) / 4;

        // Draw the error counter
        graphics.setForegroundColor(TextColor.ANSI.RED);
        graphics.putString((terminalWidth - 2 * gridSize) / 4, 2, "Errors: " + grid.getErrorCount());

        // Draw the grid with calculated offsets
        grid.draw(graphics, cursorX, cursorY, gridOffsetX, gridOffsetY);

        screen.refresh();
    }

    void displayEndScreen() throws IOException {
        screen.clear();
        TextGraphics graphics = screen.newTextGraphics();
        graphics.setForegroundColor(TextColor.ANSI.GREEN);

        // Display "org.example.Game Finished" message
        graphics.putString(25, 10, "Game Finished");

        // Display the time taken
        String timeTaken = formatDuration(totalTime);
        graphics.putString(23, 12, "Time taken: " + timeTaken);

        // Display the error count
        int totalErrors = grid.getErrorCount();
        graphics.putString(23, 14, "Errors made: " + totalErrors);

        // Display quit option
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(23, 14, "Press 'q' to quit");

        screen.refresh();

        // Wait for player to press 'q' to quit
        while (true) {
            com.googlecode.lanterna.input.KeyStroke key = screen.readInput();
            if (key.getKeyType() == KeyType.Character && key.getCharacter() == 'q') {
                break;
            }
        }
        screen.close();
    }

    String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds); // Format as MM:SS
    }

    public Screen getScreen() {
        return screen;
    }

    public Grid getGrid() {
        return grid;
    }

    public void run() {
        try {
            // Display the main menu and process menu selection
            while (true) {
                displayMainMenu();
                com.googlecode.lanterna.input.KeyStroke key = screen.readInput();
                processMenuKey(key);
                if (key.getKeyType() == KeyType.Character && key.getCharacter() == 'q') {
                    screen.close();
                    break;
                }
                if (key.getKeyType() == KeyType.EOF) {
                    screen.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
