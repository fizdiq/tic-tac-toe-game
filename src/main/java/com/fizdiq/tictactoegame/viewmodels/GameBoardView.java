package com.fizdiq.tictactoegame.viewmodels;

import com.fizdiq.tictactoegame.dto.request.PlayerMove;
import com.fizdiq.tictactoegame.model.Game;
import com.fizdiq.tictactoegame.services.TicTacToeService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.IntStream;

@Slf4j
@Route(value = "game", layout = MainLayout.class)
public class GameBoardView extends VerticalLayout implements HasUrlParameter<Long> {

    @Autowired
    private TicTacToeService ticTacToeService;

    private Game currentGame;
    private Grid<String[]> grid;
    private NativeLabel gameStatus;

    @Override
    public void setParameter(BeforeEvent event, Long gameId) {
        currentGame = ticTacToeService.getGame(gameId);

        if (currentGame == null) {
            add(new NativeLabel("Game with ID: " + gameId + " not found"));
            return;
        }

        initializeGameBoard();
    }

    private void initializeGameBoard() {

        add(new NativeLabel("Game ID: " + currentGame.getId()));

        gameStatus = new NativeLabel();
        gameStatus.getStyle().set("font-weight", "bold");
        updateUI(null);

        int boardSize = currentGame.getBoardSize();

        grid = new Grid<>();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.removeAllHeaderRows();
        grid.setAllRowsVisible(true);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.getStyle().setTextAlign(Style.TextAlign.CENTER);
        grid.setAllRowsVisible(true);

        int size = 600 / boardSize;
        String player1Symbol = currentGame.getPlayer1Symbol();
        String player2Symbol = currentGame.getPlayer2Symbol();
        String player1Color = "green";
        String player2Color = "red";

        IntStream.range(0, boardSize).forEach(col ->
                grid.addColumn(new ComponentRenderer<>(row -> {
                    String cellValue = row[col] == null ? "" : row[col];

                    Button cellButton = new Button(cellValue);
                    cellButton.setWidth(size + "px");
                    cellButton.setHeight(size + "px");
                    cellButton.setEnabled(cellValue.isEmpty());

                    if (cellValue.equals(player1Symbol)) {
                        cellButton.getStyle().set("background-color", player1Color);
                        cellButton.getStyle().set("color", "white");
                    } else if (cellValue.equals(player2Symbol)) {
                        cellButton.getStyle().set("background-color", player2Color);
                        cellButton.getStyle().set("color", "white");
                    }

                    cellButton.addClickListener(click -> {
                        int rowIndex = grid.getGenericDataView().getItems().toList().indexOf(row);
                        handlePlayerMove(rowIndex, col);
                    });
                    return cellButton;
                }))
        .setHeader("")
        .setKey(String.valueOf(col))
        .setFlexGrow(0)
        .setResizable(false)
        .setAutoWidth(true));

        refreshBoard();

        Button homeButton = new Button("Home", click -> getUI().ifPresent(ui -> ui.navigate("")));

        add(gameStatus, grid, homeButton);
    }

    private void handlePlayerMove(int row, int col) {
        if (currentGame.isGameOver()) {
            updateUI(null);
            return;
        }

        String currentPlayer = getCurrentPlayerSymbol();

        String moveResult = ticTacToeService.makeMove(currentGame.getId(), new PlayerMove(row, col, currentPlayer));

        if (currentGame.isGameOver()) {
            updateGameStatus(gameStatus);
            return;
        } else {
            gameStatus.setText("Game Status: " + moveResult);
        }

        refreshBoard();
        refreshGameState();
        updateUI(moveResult);

        if (currentGame.isGameOver()) {
            updateUI(moveResult);
            refreshBoard();
            refreshGameState();
            return;
        }
        refreshBoard();
        refreshGameState();
        updateUI(moveResult);
    }

    private void updateUI(String moveResult) {
        if (moveResult != null && !moveResult.isEmpty()) {
            String message = "Game Status: " + moveResult;
            log.info(message);
            gameStatus.setText(message);
            return;
        }
        if (currentGame.isGameOver()) {
            String winner = currentGame.getWinner();
            String message;
            if (winner.equalsIgnoreCase("Draw")) {
                message = "Game Status: The game ended in a draw.";
            } else {
                message = "Game Status: Player " + winner + " has won the game.";
            }
            log.info(message);
            gameStatus.setText(message);
        } else {
            String message = "Game Status: Ongoing";
            log.info(message);
            gameStatus.setText(message);
        }
    }

    private void updateGameStatus(NativeLabel gameStatus) {
        String winner = currentGame.getWinner();
        String message;
        if (winner.equalsIgnoreCase("Draw")) {
            message = "Game Status: The game ended in a draw.";
        } else {
            message = "Game Status: Player " + winner + " has won the game.";
        }
        log.info(message);
        gameStatus.setText(message);
        return;
    }

    private void refreshGameState() {
        currentGame = ticTacToeService.getGame(currentGame.getId());
    }

    private void refreshBoard() {
        String[][] boardArray = currentGame.getBoardArray();

        grid.setItems(boardArray);
        grid.getDataProvider().refreshAll();
        grid.removeAllHeaderRows();
    }

    private String getCurrentPlayerSymbol() {
        String[][] boardArray = currentGame.getBoardArray();
        int moveCount = 0;

        for (String[] row : boardArray) {
            for (String cell : row) {
                if (cell != null) {
                    moveCount++;
                }
            }
        }

        if (moveCount % 2 == 0) {
            return currentGame.getPlayer1Symbol();
        } else {
            return currentGame.getPlayer2Symbol();
        }
    }
}
