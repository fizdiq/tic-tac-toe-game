package com.fizdiq.tictactoegame.viewmodels;

import com.fizdiq.tictactoegame.model.Game;
import com.fizdiq.tictactoegame.services.TicTacToeService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@UIScope
@Component
@Route(value = "", layout = MainLayout.class)
public class HomeView extends VerticalLayout implements BeforeEnterObserver {

    private final TicTacToeService ticTacToeService;

    private Grid<Game> gamesGrid;

    @Autowired
    public HomeView(TicTacToeService ticTacToeService) {
        this.ticTacToeService = ticTacToeService;
        setSpacing(false);
        setPadding(true);
        setAlignItems(Alignment.CENTER);

        Button newGameButton = new Button("Start New Game", click -> {
            getUI().ifPresent(ui -> ui.navigate("config"));
        });
        newGameButton.getStyle().set("margin-bottom", "20px");

        gamesGrid = new Grid<>(Game.class, false);
        gamesGrid.addColumn(Game::getId).setHeader("Game ID");
        gamesGrid.addColumn(Game::getBoardSize).setHeader("Board Size");
        gamesGrid.addColumn(Game::getWinStreak).setHeader("Win Streak");
        gamesGrid.addComponentColumn(game -> {
                    Icon icon;
                    if (game.isSinglePlayer()) {
                        icon = VaadinIcon.USER.create();
                        icon.setColor("lime");
                    } else {
                        icon = VaadinIcon.GROUP.create();
                        icon.setColor("blue");
                    }
                    return icon;
                })
                .setTooltipGenerator(game -> game.isSinglePlayer() ? "Single Player Mode" : "Multiplayer Mode")
                .setHeader("Mode");
        gamesGrid.addComponentColumn(game -> {
            Div container = new Div();
            Text text;
            if (game.isGameOver()) {
                text = new Text("Over");
                container.getElement().getStyle().set("color", "red");
            } else {
                text = new Text("Ongoing");
                container.getElement().getStyle().set("color", "green");
            }
            container.getElement().getStyle().set("font-weight", "bold");
            container.add(text);
            return container;
        }).setHeader("Game Status");
        gamesGrid.addColumn(Game::getPlayer1Symbol).setHeader("Player 1 Symbol");
        gamesGrid.addColumn(Game::getPlayer2Symbol).setHeader("Player 2 Symbol");
        gamesGrid.addColumn(game -> {
            String winner = game.getWinner();
            return (game.isGameOver() && winner != null) ? winner : "N/A";
        }).setHeader("Winner");

        Div gridContainer = new Div(gamesGrid);
        gridContainer.setWidth("80%");
        gridContainer.setHeight("400px");
        gridContainer.getStyle().set("overflow", "auto");

        List<Game> games = ticTacToeService.getGamesOrdered();
        gamesGrid.setItems(games);

        gamesGrid.addItemClickListener(event -> {
            Long gameId = event.getItem().getId();
            getUI().ifPresent(ui -> ui.navigate("game/" + gameId));
        });

        add(newGameButton, gridContainer);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        refreshGrid();
    }

    private void refreshGrid() {
        List<Game> games = ticTacToeService.getGamesOrdered();
        gamesGrid.setItems(games);
    }
}
