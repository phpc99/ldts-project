package org.example;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.TextColor;

public class Card {
    private final String value;
    private boolean isRevealed = false;
    private boolean isMatched = false;

    public Card(String value) {
        this.value = value;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void reveal() {
        isRevealed = true;
    }

    public void hide() {
        isRevealed = false;
    }

    public void setMatched() {
        isMatched = true;
    }

    public boolean matches(Card other) {
        return this.value.equals(other.value);
    }

    public void draw(TextGraphics graphics, int row, int col, boolean isSelected) {
        String displayValue = isRevealed ? value : "*"; // Show value if revealed, otherwise a placeholder
        if (isSelected) {
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        } else {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
        }
        graphics.putString(2 * col, row, displayValue);
    }
}