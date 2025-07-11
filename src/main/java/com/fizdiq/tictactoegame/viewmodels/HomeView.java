package com.fizdiq.tictactoegame.viewmodels;

import com.fizdiq.tictactoegame.config.Common;
import com.fizdiq.tictactoegame.model.Game;
import com.fizdiq.tictactoegame.services.TicTacToeService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
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

        NativeLabel helperText = new NativeLabel("Click on a game row to go to its game board view.");
        gamesGrid = new Grid<>(Game.class, false);
        gamesGrid.addColumn(Game::getId).setHeader("Game ID").setAutoWidth(true).setResizable(false);
        gamesGrid.addColumn(Game::getBoardSize)
                .setHeader("Board Size")
                .setAutoWidth(true)
                .setResizable(false);
        gamesGrid.addColumn(Game::getWinStreak)
                .setHeader("Win Streak")
                .setAutoWidth(true)
                .setResizable(false);
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
                .setHeader("Mode").setAutoWidth(true).setResizable(false);
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
        }).setHeader("Game Status").setAutoWidth(true).setResizable(false);
        gamesGrid.addColumn(Game::getPlayer1Symbol)
                .setHeader("Player 1 Symbol")
                .setAutoWidth(true)
                .setResizable(false);
        gamesGrid.addColumn(Game::getPlayer2Symbol)
                .setHeader("Player 2 Symbol")
                .setAutoWidth(true)
                .setResizable(false);
        gamesGrid.addColumn(game -> {
            String winner = game.getWinner();
            return (game.isGameOver() && winner != null) ? winner : "N/A";
        }).setHeader("Winner").setAutoWidth(true).setResizable(false);
        gamesGrid.addColumn(game -> Common.formatDateTime(game.getCreatedDate()))
                .setHeader("Created Date")
                .setAutoWidth(true)
                .setResizable(false);
        gamesGrid.addColumn(game -> Common.formatDateTime(game.getUpdatedDate()))
                .setHeader("Updated Date")
                .setAutoWidth(true)
                .setResizable(false);
        gamesGrid.addComponentColumn(game -> {
            Button deleteButton = new Button("Delete", VaadinIcon.TRASH.create());
            deleteButton.getStyle().set("color", "red");

            deleteButton.addClickListener(click -> {
                ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.setConfirmButtonTheme("error primary");
                confirmDialog.setCancelButtonTheme("tertiary");
                confirmDialog.setCancelable(true);
                confirmDialog.setHeader("Delete Game " + game.getId() + " Confirmation");
                confirmDialog.setText("Are you sure you want to delete this game?");
                confirmDialog.setConfirmText("Delete");
                confirmDialog.setCancelText("Cancel");

                confirmDialog.addConfirmListener(event -> {
                    ticTacToeService.deleteGame(game.getId());
                    refreshGrid();
                    Notification.show("Game " + game.getId() + " deleted successfully");
                });

                confirmDialog.open();
            });
            return deleteButton;
        }).setHeader("Actions").setAutoWidth(true).setResizable(false);

        gamesGrid.setEmptyStateText("No games found. Start a new game to see it here");

        Div gridContainer = new Div(gamesGrid);
        gridContainer.setWidth("80%");
        gridContainer.setHeight("400px");
        gridContainer.getStyle().set("overflow", "auto");

        List<Game> games = ticTacToeService.getGamesOrdered();
        gamesGrid.setItems(games);
        gamesGrid.setSelectionMode(Grid.SelectionMode.SINGLE);


        gamesGrid.addItemClickListener(event -> {
            Long gameId = event.getItem().getId();
            getUI().ifPresent(ui -> ui.navigate("game/" + gameId));
        });

        add(newGameButton, helperText, gridContainer);
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
