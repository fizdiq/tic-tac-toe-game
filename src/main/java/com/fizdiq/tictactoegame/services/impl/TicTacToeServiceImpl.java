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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
        boolean singlePlayer = gameConfig.isSinglePlayer();
        String player1Symbol = gameConfig.player1Symbol();
        String player2Symbol = gameConfig.player2Symbol();
        boolean player2Computer = gameConfig.isPlayer2Computer();

        Game game = new Game();
        game.setBoardSize(boardSize);
        game.setWinStreak(winStreak);
        game.setSinglePlayer(singlePlayer);
        game.setPlayer1Symbol(player1Symbol);
        game.setPlayer2Symbol(player2Symbol);
        game.setPlayer2Computer(player2Computer);

        String[][] emptyBoard = new String[boardSize][boardSize];
        game.setBoardArray(emptyBoard);

        gameRepository.saveAndFlush(game);

        log.info("New game started: {}, Board Size: {}, Win Streak: {}, Single Player: {}",
                game.getId(),
                gameConfig.boardSize(),
                gameConfig.winStreak(),
                gameConfig.isSinglePlayer());

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
            String message = "The game is over. Winner: " + game.getWinner();
            log.info(message);
            return message;
        }

        int row = playerMove.row();
        int col = playerMove.col();
        String player = playerMove.player();
        String player1Symbol = game.getPlayer1Symbol();
        boolean singlePlayer = game.isSinglePlayer();
        String player2Symbol = game.getPlayer2Symbol();

//        if (!player.equals(player1Symbol) &&
//                (!singlePlayer || !player.equals(player2Symbol))) {
//            String errorMessage = "Invalid symbol used! Allowed symbols: " + player1Symbol
//                    + (game.isSinglePlayer() ? ", " + game.getPlayer2Symbol() : "");
//            log.error(errorMessage);
//            return errorMessage;
//        }

        if (boardArray[row][col] != null) {
            String errorMessage = "Cell is already occupied";
            log.error(errorMessage);
            return errorMessage;
        }

        boardArray[row][col] = player;

        game.setBoardArray(boardArray);

        if (checkWin(game, playerMove)) {
            game.setWinner(player);
            game.setGameOver(true);
            gameRepository.saveAndFlush(game);
            String winningMessage = "Player " + player + " has won the game";
            log.info(winningMessage);
            return winningMessage;
        }

        if (isBoardFull(boardArray)) {
            game.setWinner("Draw");
            game.setGameOver(true);
            gameRepository.saveAndFlush(game);
            String drawMessage = "The game is a draw";
            log.info(drawMessage);
            return drawMessage;
        }

        boolean player2Computer = game.isPlayer2Computer();
        if (singlePlayer && !game.isGameOver() && player2Computer && player.equals(player1Symbol)) {
            String aiMoveResult = makeAIMove(game);
            if (!game.isGameOver()) {
                String message = aiMoveResult + ". Your turn!";
                log.info(message);
                return message;
            } else {
                log.info(aiMoveResult);
                return aiMoveResult;
            }
        }

        gameRepository.saveAndFlush(game);
        String moveMessage = "Player " + player + " has made a move";
        log.info(moveMessage);
        return moveMessage;
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

    private String makeAIMove(Game game) {
        String[][] boardArray = game.getBoardArray();

        String aiPlayer = game.getPlayer2Symbol();

        List<int[]> emptyCells = new ArrayList<>();

        int length = boardArray.length;
        for (int row = 0; row < length; row++) {
            for (int col = 0; col < boardArray[row].length; col++) {
                if (boardArray[row][col] == null) {
                    emptyCells.add(new int[]{row, col});
                }
            }
        }

        if (emptyCells.isEmpty()) {
            String drawMessage = "The game is a draw";
            log.info(drawMessage);
            return drawMessage;
        }

        Random random = new Random();
        int[] move = emptyCells.get(random.nextInt(emptyCells.size()));

        int aiRow = move[0];
        int aiCol = move[1];
        boardArray[aiRow][aiCol] = aiPlayer;

        game.setBoardArray(boardArray);

        if (checkWin(game, new PlayerMove(aiRow, aiCol, aiPlayer))) {
            game.setWinner("AI");
            game.setGameOver(true);
            gameRepository.saveAndFlush(game);
            String winningMessage = "AI (" + aiPlayer + ") has won the game!";
            log.info(winningMessage);
            return winningMessage;
        }

        gameRepository.saveAndFlush(game);
        String aiMoveMessage = "AI (" + aiPlayer + ") placed at (" + aiRow + ", " + aiCol + ").";
        log.info(aiMoveMessage);
        return aiMoveMessage;
    }
}
