package com.fizdiq.tictactoegame.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int boardSize;

    private int winStreak;

    @Lob
    private String board;

    private boolean isGameOver = false;
    private String winner = null;

    public String[][] getBoardArray() {
        if (board == null || board.isEmpty()) {
            return new String[boardSize][boardSize];
        }

        String[] flatBoard = board.replace("[", "").replace("]", "").split(",");

        String[][] boardArray = new String[boardSize][boardSize];

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                String value = flatBoard[i * boardSize + j];
                boardArray[i][j] = value.equals("null") ? null : value;
            }
        }
        return boardArray;
    }

    public void setBoardArray(String[][] boardArray) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("[");
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                stringBuilder.append(boardArray[i][j] == null ? "null" : boardArray[i][j]);

                if (i != boardSize - 1 || j != boardSize - 1) {
                    stringBuilder.append(",");
                }
            }
        }
        stringBuilder.append("]");
        this.board = stringBuilder.toString();
    }
}
