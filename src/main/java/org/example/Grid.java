package org.example;

import com.googlecode.lanterna.graphics.TextGraphics;
import org.example.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Grid {
    private final int size; // Size of the grid (4, 6, or 8)
    private final Card[][] cards; // 2D array of cards
    private Card firstSelectedCard = null; // First card selected for matching
    private Card secondSelectedCard = null; // Second card selected for matching
    private boolean isCheckingMatch = false; // Flag to indicate if matching is in progress
    private int errorCount = 0; // Track errors
    private boolean waitToCheckMatch = false; // Flag to track if we’re waiting to reveal the second card

    public Grid(int size) {
        this.size = size;
        this.cards = new Card[size][size];
        initializeCards();
    }

    public int getSize() {
        return size;
    }

    public void selectCard(int x, int y) {
        Card selectedCard = cards[y][x];

        if (!selectedCard.isRevealed()) {
            selectedCard.reveal();
            if (firstSelectedCard == null) {
                firstSelectedCard = selectedCard;
            } else if (secondSelectedCard == null) {
                secondSelectedCard = selectedCard;
                waitToCheckMatch = true; // Set flag to check match in the game loop after a delay
            }
        }
    }

    public boolean shouldCheckMatch() {
        return waitToCheckMatch; // Check if we’re waiting to verify a match
    }

    public int getErrorCount() {
        return errorCount;
    }

    public boolean checkForMatch() {
        if (firstSelectedCard != null && secondSelectedCard != null) {
            if (firstSelectedCard.matches(secondSelectedCard)) {
                firstSelectedCard.setMatched();
                secondSelectedCard.setMatched();
            } else {
                firstSelectedCard.hide();
                secondSelectedCard.hide();
                errorCount++;
            }
            firstSelectedCard = null;
            secondSelectedCard = null;
            waitToCheckMatch = false; // Reset flag after checking match
            return true; // Indicate that a check was performed
        }
        return false;
    }

    private void initializeCards() {
        List<String> values = new ArrayList<>();

        // Create pairs of values for cards
        for (int i = 0; i < (size * size) / 2; i++) {
            String value = String.valueOf((char) ('A' + i)); // Use letters as example values
            values.add(value);
            values.add(value); // Add two of each for matching pairs
        }

        // Shuffle the list of values
        Collections.shuffle(values);

        // Assign values to cards in the grid
        int index = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                cards[row][col] = new Card(values.get(index++));
            }
        }
    }

    public boolean update() {
        if (isCheckingMatch && firstSelectedCard != null && secondSelectedCard != null) {
            if (firstSelectedCard.matches(secondSelectedCard)) {
                firstSelectedCard.setMatched();
                secondSelectedCard.setMatched();
            } else {
                firstSelectedCard.hide();
                secondSelectedCard.hide();
                errorCount++; // Increment error count if no match
            }

            // Reset the selected cards and matching flag
            firstSelectedCard = null;
            secondSelectedCard = null;
            isCheckingMatch = false;
            return true; // Indicate that a match/mismatch check occurred
        }
        return false; // No match check needed
    }

    public boolean isGameOver() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (!cards[row][col].isMatched()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void draw(TextGraphics graphics, int cursorX, int cursorY, int offsetX, int offsetY) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Card card = cards[row][col];
                boolean isSelected = (row == cursorY && col == cursorX);
                card.draw(graphics, row + offsetY, col + offsetX, isSelected);
            }
        }
    }
}