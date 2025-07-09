package com.fizdiq.tictactoegame.services;

import com.fizdiq.tictactoegame.dto.request.GameConfig;
import com.fizdiq.tictactoegame.dto.request.PlayerMove;
import com.fizdiq.tictactoegame.model.Game;

public interface TicTacToeService {

    Game startNewGame(GameConfig gameConfig);
    String makeMove(Long gameId, PlayerMove playerMove);
    Game getGame(Long gameId);
}
