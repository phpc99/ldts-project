package org.example;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameTest {
    private Game game;
    private Screen mockScreen;

    @BeforeEach
    void setUp() throws IOException {
        // Mock Screen to avoid initializing the actual terminal
        mockScreen = mock(Screen.class);
        game = new Game();
        game = Mockito.spy(game); // Spy to test private method interactions
        doReturn(mockScreen).when(game).getScreen(); // Inject mock Screen
    }

    // Mock-Based Test: Ensure processMenuKey handles up and down navigation
    @Test
    void testProcessMenuKeyNavigation() {
        KeyStroke upKey = new KeyStroke(KeyType.ArrowUp);
        KeyStroke downKey = new KeyStroke(KeyType.ArrowDown);

        // Initial state: selectedOption = 0
        Assertions.assertEquals(0, game.getSelectedOption(), "Initial selected menu option should be 0");

        game.processMenuKey(upKey); // Move up
        Assertions.assertEquals(0, game.getSelectedOption(), "Selected option should not go below 0");

        game.processMenuKey(downKey); // Move down
        Assertions.assertEquals(1, game.getSelectedOption(), "Selected option should move to 1");

        game.processMenuKey(downKey); // Move down again
        Assertions.assertEquals(2, game.getSelectedOption(), "Selected option should move to 2");

        game.processMenuKey(downKey); // Move down again (boundary check)
        Assertions.assertEquals(2, game.getSelectedOption(), "Selected option should not exceed 2");
    }

    @Test
    void testFormatDuration() {
        Game game = new Game();
        String formatted = game.formatDuration(Duration.ofSeconds(125));
        assertEquals("02:05", formatted);
    }
}
