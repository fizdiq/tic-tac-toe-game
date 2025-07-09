package com.fizdiq.tictactoegame.services.impl;

import com.fizdiq.tictactoegame.dto.request.GameConfig;
import com.fizdiq.tictactoegame.dto.request.PlayerMove;
import com.fizdiq.tictactoegame.model.Game;
import com.fizdiq.tictactoegame.repositories.GameRepository;
import com.fizdiq.tictactoegame.services.TicTacToeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TicTacToeServiceImpl implements TicTacToeService {

    private final GameRepository gameRepository;

    @Override
    public Game startNewGame(GameConfig gameConfig) {
        int boardSize = gameConfig.boardSize();
        int winStreak = gameConfig.winStreak();

        Game game = new Game();
        game.setBoardSize(boardSize);
        game.setWinStreak(winStreak);

        String[][] emptyBoard = new String[boardSize][boardSize];
        game.setBoardArray(emptyBoard);

        gameRepository.saveAndFlush(game);

        log.info("New game of tic-tac-toe has started with ID: {}, Board Size: {}, Win Streak: {}",
                game.getId(),
                boardSize,
                winStreak);

        return game;
    }

    @Override
    public String makeMove(Long gameId, PlayerMove playerMove) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);

        if (gameOptional.isEmpty()) {
            String errorMessage = "Game with ID: " + gameId + " not found";
            log.error(errorMessage);
            return errorMessage;
        }

        Game game = gameOptional.get();

        String[][] boardArray = game.getBoardArray();

        if (game.isGameOver()) {
            return "Game is over. Winner: " + game.getWinner();
        }

        int row = playerMove.row();
        int col = playerMove.col();
        if (boardArray[row][col] != null) {
            return "Cell is already occupied";
        }

        String player = playerMove.player();
        boardArray[row][col] = player;

        game.setBoardArray(boardArray);

        if (checkWin(game, playerMove)) {
            game.setWinner(player);
            game.setGameOver(true);
            gameRepository.saveAndFlush(game);
            return "Player " + player + " has won the game";
        }

        if (isBoardFull(boardArray)) {
            game.setWinner("Draw");
            game.setGameOver(true);
            gameRepository.saveAndFlush(game);
            return "The game is a draw";
        }

        gameRepository.saveAndFlush(game);
        return "Player " + player + " has made a move";
    }

    @Override
    public Game getGame(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game with ID: " + gameId + " not found"));
    }

    private boolean checkWin(Game game, PlayerMove playerMove) {
        int streak = game.getWinStreak();
        String[][] boardArray = game.getBoardArray();
        String player = playerMove.player();
        int row = playerMove.row();
        int col = playerMove.col();

        return checkDirection(boardArray, row, col, 1, 0, player, streak) ||
                checkDirection(boardArray, row, col, 0, 1, player, streak) ||
                checkDirection(boardArray, row, col, 1, 1, player, streak) ||
                checkDirection(boardArray, row, col, 1, -1, player, streak);
    }

    private boolean checkDirection(String[][] boardArray, int row, int col, int dx, int dy, String player, int streak) {
        int count = 1;
        int size = boardArray.length;

        for (int i = 1; i < streak; i++) {
            int r = row + i * dx;
            int c = col + i * dy;

            if (r < 0 || r >= size || c < 0 || c >= size || !player.equals(boardArray[r][c]) ) {
                break;
            }
            count++;
        }

        for (int i = 1; i < streak; i++) {
            int r = row - i * dx;
            int c = col - i * dy;

            if (r < 0 || r >= size || c < 0 || c >= size || !player.equals(boardArray[r][c]) ) {
                break;
            }
            count++;
        }
        return count >= streak;
    }

    private boolean isBoardFull(String[][] boardArray) {
        int size = boardArray.length;
        for (String[] row : boardArray) {
            for (String cell : row) {
                if (cell == null) {
                    return false;
                }
            }
        }
        return true;
    }
}
