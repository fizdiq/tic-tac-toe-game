package com.fizdiq.tictactoegame.viewmodels;

import com.fizdiq.tictactoegame.dto.request.PlayerMove;
import com.fizdiq.tictactoegame.model.Game;
import com.fizdiq.tictactoegame.services.TicTacToeService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
//    private Div boardDiv;
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
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(new NativeLabel("Game ID: " + currentGame.getId()));

        gameStatus = new NativeLabel();
        gameStatus.getStyle().set("font-weight", "bold");
        updateUI(null);

        int boardSize = currentGame.getBoardSize();

//        Div gridContainer = new Div();
//        gridContainer.getStyle()
//                .set("display", "flex")
//                .set("align-items", "center")
//                .set("justify-content", "center")
//                .set("width", "100%")
//                .set("height", "100%")
//                .set("max-width", "600px")
//                .set("max-height", "600px")
//                .set("padding", "20px")
//                .set("box-sizing", "border-box");



        grid = new Grid<>();
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.removeAllHeaderRows();
        grid.setAllRowsVisible(true);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.getStyle().setTextAlign(Style.TextAlign.CENTER);
        grid.setAllRowsVisible(true);
//        grid.setHeightFull();

//        grid.setSizeFull();
//        grid.getStyle()
//                .set("width", "100%")
//                .set("height", "100%")
//                .set("margin", "0")
//                .set("box-sizing", "border-box");
//        grid.getStyle()
//                .set("grid-template-columns", "repeat(" + boardSize + ", 1fr)")
//                .set("grid-template-rows", "repeat(" + boardSize + ", 1fr)");
//        grid.getElement().getStyle()
//                .set("--vaadin-grid-row-height", "50px");

        IntStream.range(0, boardSize).forEach(col ->
                grid.addColumn(row -> row[col] == null ? "" : row[col])
                        .setHeader("")
                        .setKey(String.valueOf(col))
                        .setFlexGrow(1)
                        .setResizable(false)
                        .setAutoWidth(true));


        grid.addItemClickListener(event -> {
            String[] row = event.getItem();
            int rowIndex = grid.getGenericDataView().getItems().toList().indexOf(row);
            int colIndex = event.getColumn().getKey() == null ? -1 : Integer.parseInt(event.getColumn().getKey());
            handlePlayerMove(rowIndex, colIndex);
        });

        refreshBoard();
//        gridContainer.add(grid);
//        add(gameStatus, gridContainer);
        add(gameStatus, grid);

//        boardDiv = new Div();
//        boardDiv.getStyle()
//                .set("display", "grid")
//                .set("grid-template-columns", "repeat(" + boardSize + ", 1fr)")
//                .set("grid-gap", "10px")
//                .set("width", "50%");

//        String[][] boardArray = currentGame.getBoardArray();
//
//        for (int row = 0; row < boardArray.length; row++) {
//            for (int col = 0; col < boardArray[row].length; col++) {
//                Button cellButton = new Button();
//                int finalRow = row;
//                int finalCol = col;
//
//                if (boardArray[row][col] != null) {
//                    cellButton.setText(boardArray[row][col]);
//                    cellButton.setEnabled(false);
//                }
//
//                cellButton.addClickListener(click -> handlePlayerMove(finalRow, finalCol, gameStatus, cellButton));
//                boardDiv.add(cellButton);
//            }
//        }
//        add(gameStatus, boardDiv);
    }

//    private void handlePlayerMove(int row, int col, NativeLabel gameStatus, Button cellButton) {
//        boolean gameOver = currentGame.isGameOver();
//
//        if (gameOver) {
//            updateGameStatus(gameStatus);
//            return;
//        }
//
//        String currentPlayer = getCurrentPlayerSymbol();
//
//        PlayerMove playerMove = new PlayerMove(row, col, currentPlayer);
//        Long gameId = currentGame.getId();
//        String moveResult = ticTacToeService.makeMove(gameId, playerMove);
//
//        refreshBoard();
//        refreshGameState();
//
//        if (gameOver) {
//            updateGameStatus(gameStatus);
//            return;
//        } else {
//            gameStatus.setText("Game Status: " + moveResult);
//        }
//
//
//        cellButton.setText(currentPlayer);
//        cellButton.setEnabled(false);
//
//        boolean singlePlayer = currentGame.isSinglePlayer();
//        boolean player2Computer = currentGame.isPlayer2Computer();
//        String player2Symbol = currentGame.getPlayer2Symbol();
////        if (!gameOver && singlePlayer && player2Computer) {
////            String aiMoveResult = ticTacToeService.makeMove(gameId,
////                    new PlayerMove(-1, -1, player2Symbol));
////            refreshBoard();
////            refreshGameState();
////            gameStatus.setText("Game Status: " + aiMoveResult);
////        }
//        refreshBoard();
//        refreshGameState();
//        updateGameStatus(gameStatus);
//    }

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
//        initializeGameBoard();
//        UI.getCurrent().refreshCurrentRoute(false);
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
