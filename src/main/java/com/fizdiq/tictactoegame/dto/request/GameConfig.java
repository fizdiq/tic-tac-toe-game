package com.fizdiq.tictactoegame.dto.request;

public record GameConfig(
        int boardSize,
        int winStreak,
        boolean isSinglePlayer,
        String player1Symbol,
        String player2Symbol,
        boolean isPlayer2Computer
) {
}
