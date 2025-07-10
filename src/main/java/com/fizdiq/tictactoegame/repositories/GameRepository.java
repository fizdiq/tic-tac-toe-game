package com.fizdiq.tictactoegame.repositories;

import com.fizdiq.tictactoegame.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("select g from Game g order by g.isGameOver, g.id desc")
    List<Game> findByOrderByIsGameOverAscIdAsc();
}
