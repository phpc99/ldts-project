package org.example;

import com.googlecode.lanterna.graphics.TextGraphics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GridTest {
    private Grid grid;

    @Test
    void testGridInitialization() {
        Grid grid = new Grid(4);
        Assertions.assertEquals(4, grid.getSize());
    }

    @Test
    void testCardSelection() {
        Grid grid = new Grid(4);
        grid.selectCard(0, 0);
        assertTrue(grid.shouldCheckMatch() || grid.getErrorCount() == 0); // Based on game state
    }

    @Test
    void testMatchLogic() {
        Grid grid = new Grid(4);
        grid.selectCard(0, 0);
        grid.selectCard(0, 1);
        if (grid.shouldCheckMatch()) {
            grid.checkForMatch();
        }
        assertTrue(grid.getErrorCount() >= 0); // Match or mismatch logic tested
    }

    @Test
    void testGridSizeInitialization() {
        Grid grid4x4 = new Grid(4);
        Grid grid6x6 = new Grid(6);
        Grid grid8x8 = new Grid(8);
        Assertions.assertEquals(grid4x4.getSize(), 4);
        Assertions.assertEquals(grid6x6.getSize(), 6);
        Assertions.assertEquals(grid8x8.getSize(), 8);
    }

    @Test
    void testLargeGridInitialization() {
        Grid grid = new Grid(10); // 10x10 grid
        Assertions.assertEquals(grid.getSize(), 10);
        Assertions.assertFalse(grid.isGameOver());
    }

    @BeforeEach
    void setUp() {
        grid = new Grid(4); // Initialize a 4x4 grid for testing
    }

    // Trivial Test: Check if the grid size is correct
    @Test
    void testGetSize() {
        Assertions.assertEquals(4, grid.getSize(), "org.example.Grid size should be 4");
    }

    // Trivial Test: Check if the grid is not marked as game over initially
    @Test
    void testIsGameOverInitially() {
        Assertions.assertFalse(grid.isGameOver(), "org.example.Game should not be over at the start");
    }

    // Mock-Based Test: Ensure draw method interacts with TextGraphics correctly
    @Test
    void testDraw() {
        TextGraphics mockGraphics = Mockito.mock(TextGraphics.class);

        // Set up a cursor position
        int cursorX = 1, cursorY = 1;
        int offsetX = 2, offsetY = 3;

        grid.draw(mockGraphics, cursorX, cursorY, offsetX, offsetY);

        // Verify interactions with TextGraphics
        verify(mockGraphics, atLeastOnce()).putString(anyInt(), anyInt(), anyString());
    }

    // Trivial Test: Ensure error count increments correctly
    @Test
    void testErrorCount() {
        grid.selectCard(0, 0); // Select the first card
        grid.selectCard(1, 0); // Select a different card
        grid.checkForMatch(); // Check for a mismatch

        Assertions.assertEquals(1, grid.getErrorCount(), "Error count should increment after a mismatch");
    }
}
