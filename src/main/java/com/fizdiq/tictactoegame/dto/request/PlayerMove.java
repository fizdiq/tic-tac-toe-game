package com.fizdiq.tictactoegame.dto.request;

public record PlayerMove(
        int row,
        int col,
        String player
) {
}
