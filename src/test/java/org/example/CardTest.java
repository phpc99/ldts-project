package org.example;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.TextColor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardTest {
    private Card card;

    @BeforeEach
    void setUp() {
        card = new Card("A"); // Initialize a card with value "A"
    }

    // Trivial Test: Verify the initial state of a card
    @Test
    void testInitialState() {
        Assertions.assertFalse(card.isRevealed(), "org.example.Card should not be revealed initially");
        Assertions.assertFalse(card.isMatched(), "org.example.Card should not be matched initially");
    }

    // Trivial Test: Test reveal method
    @Test
    void testReveal() {
        card.reveal();
        Assertions.assertTrue(card.isRevealed(), "org.example.Card should be revealed after calling reveal()");
    }

    // Trivial Test: Test hide method
    @Test
    void testHide() {
        card.reveal();
        card.hide();
        Assertions.assertFalse(card.isRevealed(), "org.example.Card should be hidden after calling hide()");
    }

    // Trivial Test: Test setMatched method
    @Test
    void testSetMatched() {
        card.setMatched();
        Assertions.assertTrue(card.isMatched(), "org.example.Card should be marked as matched after calling setMatched()");
    }

    // Trivial Test: Test matches method for matching values
    @Test
    void testMatchesWithSameValue() {
        Card otherCard = new Card("A");
        Assertions.assertTrue(card.matches(otherCard), "Cards with the same value should match");
    }

    // Trivial Test: Test matches method for non-matching values
    @Test
    void testMatchesWithDifferentValue() {
        Card otherCard = new Card("B");
        Assertions.assertFalse(card.matches(otherCard), "Cards with different values should not match");
    }

    // Mock-Based Test: Ensure draw interacts with TextGraphics correctly for hidden card
    @Test
    void testDrawHiddenCard() {
        TextGraphics mockGraphics = Mockito.mock(TextGraphics.class);

        int row = 2;
        int col = 4;
        int expectedX = 2 * col; // Adjusted for org.example.Card.draw logic
        int expectedY = row;

        card.draw(mockGraphics, row, col, false);

        // Verify placeholder ("*") is drawn for hidden card
        verify(mockGraphics).setForegroundColor(TextColor.ANSI.WHITE);
        verify(mockGraphics).putString(expectedX, expectedY, "*");
    }

    // Mock-Based Test: Ensure draw interacts with TextGraphics correctly for revealed card
    @Test
    void testDrawRevealedCard() {
        TextGraphics mockGraphics = Mockito.mock(TextGraphics.class);

        card.reveal();
        int row = 2;
        int col = 4;
        int expectedX = 2 * col; // Adjusted for org.example.Card.draw logic
        int expectedY = row;

        card.draw(mockGraphics, row, col, false);

        // Verify card value is drawn for revealed card
        verify(mockGraphics).setForegroundColor(TextColor.ANSI.WHITE);
        verify(mockGraphics).putString(expectedX, expectedY, "A");
    }

    // Mock-Based Test: Ensure draw interacts with TextGraphics correctly for selected card
    @Test
    void testDrawSelectedCard() {
        TextGraphics mockGraphics = Mockito.mock(TextGraphics.class);

        int row = 2;
        int col = 4;
        int expectedX = 2 * col; // Adjusted for org.example.Card.draw logic
        int expectedY = row;

        card.draw(mockGraphics, row, col, true);

        // Verify placeholder is drawn with highlight color for selected card
        verify(mockGraphics).setForegroundColor(TextColor.ANSI.YELLOW);
        verify(mockGraphics).putString(expectedX, expectedY, "*");
    }

    @Test
    void testCardReveal() {
        Card card = new Card("A");
        Assertions.assertFalse(card.isRevealed());
        card.reveal();
        Assertions.assertTrue(card.isRevealed());
    }

    @Test
    void testCardHide() {
        Card card = new Card("A");
        card.reveal();
        card.hide();
        Assertions.assertFalse(card.isRevealed());
    }

    @Test
    void testCardMatch() {
        Card card1 = new Card("A");
        Card card2 = new Card("A");
        Card card3 = new Card("B");
        Assertions.assertTrue(card1.matches(card2));
        Assertions.assertFalse(card1.matches(card3));
    }
}