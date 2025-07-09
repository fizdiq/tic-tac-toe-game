package com.fizdiq.tictactoegame.dto.request;

public record GameConfig(
        int boardSize,
        int winStreak
) {
}
