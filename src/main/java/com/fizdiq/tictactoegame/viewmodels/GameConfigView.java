package com.fizdiq.tictactoegame.viewmodels;

import com.fizdiq.tictactoegame.dto.request.GameConfig;
import com.fizdiq.tictactoegame.services.TicTacToeService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "config", layout = MainLayout.class)
public class GameConfigView extends VerticalLayout {

    @Autowired
    private TicTacToeService ticTacToeService;

    public GameConfigView() {
        IntegerField boardSizeField = new IntegerField("Board Size (e.g., 3 for 3x3)");
        boardSizeField.setRequiredIndicatorVisible(true);
        boardSizeField.setI18n(new IntegerField.IntegerFieldI18n().setRequiredErrorMessage("Board size is required!")
                .setBadInputErrorMessage("Invalid number format"));
        boardSizeField.setPlaceholder("Enter size... (e.g., 3 for 3x3)");
        boardSizeField.setWidth("30%");
        boardSizeField.setClearButtonVisible(true);

        IntegerField winStreakField = new IntegerField("Win Streak (e.g., 3 for three in a row)");
        winStreakField.setRequiredIndicatorVisible(true);
        winStreakField.setI18n(new IntegerField.IntegerFieldI18n().setRequiredErrorMessage("Win streak is required!")
                .setBadInputErrorMessage("Invalid number format"));
        winStreakField.setPlaceholder("Enter win streak... (e.g., 3 for three in a row)");
        winStreakField.setWidth("30%");
        winStreakField.setClearButtonVisible(true);

        TextField player1SymbolField = new TextField("First Player Symbol (e.g., X)");
        player1SymbolField.setRequiredIndicatorVisible(true);
        player1SymbolField.setPattern("\\S");
        player1SymbolField.setI18n(new TextField.TextFieldI18n().setRequiredErrorMessage("Player 1 symbol is " +
                "required!").setPatternErrorMessage("Invalid input"));
        player1SymbolField.setPlaceholder("Enter player 1 symbol... (e.g., X)");
        player1SymbolField.setWidth("30%");
        player1SymbolField.setClearButtonVisible(true);

        TextField player2SymbolField = new TextField("Second Player Symbol (e.g., O)");
        player2SymbolField.setRequiredIndicatorVisible(true);
        player2SymbolField.setPattern("\\S");
        player2SymbolField.setI18n(new TextField.TextFieldI18n().setRequiredErrorMessage("Player 2 symbol is " +
                "required!").setPatternErrorMessage("Invalid input"));
        player2SymbolField.setPlaceholder("Enter player 2 symbol... (e.g., O)");
        player2SymbolField.setWidth("30%");
        player2SymbolField.setClearButtonVisible(true);

        Checkbox singlePlayerCheckbox = new Checkbox("Single Player Mode (vs Computer)");
        singlePlayerCheckbox.setValue(true);

        Button startGameButton = new Button("Start Game", click -> {
            try {
                if (boardSizeField.getValue() == null || winStreakField.getValue() == null) {
                    Notification.show("Board Size or Win Streak cannot be empty.");
                    return;
                }

                int boardSize = boardSizeField.getValue();
                int winStreak = winStreakField.getValue();
                String player1Symbol = player1SymbolField.getValue();
                String player2Symbol = player2SymbolField.getValue();
                boolean isSinglePlayer = singlePlayerCheckbox.getValue();

                if (boardSize < 3 || winStreak > boardSize) {
                    Notification.show("Invalid Board Size or Win Streak! Must be >= 3 and win streak <= board size.");
                    return;
                }

                if (player1Symbol.equals(player2Symbol)) {
                    Notification.show("Player 1 and Player 2 symbols must be different and cannot be empty!");
                    return;
                }

                GameConfig config = new GameConfig(boardSize, winStreak, isSinglePlayer, player1Symbol, player2Symbol,
                        isSinglePlayer);
                var game = ticTacToeService.startNewGame(config);

                Notification.show("Game started! Game ID: " + game.getId());
                getUI().ifPresent(ui -> ui.navigate("game/" + game.getId()));
            } catch (NumberFormatException e) {
                Notification.show("Invalid input! Please enter valid numbers for Board Size and Win Streak.");
            }
        });

        Button homeButton = new Button("Home", click -> getUI().ifPresent(ui -> ui.navigate("")));

        HorizontalLayout buttonLayout = new HorizontalLayout(startGameButton, homeButton);

        add(boardSizeField, winStreakField, player1SymbolField, player2SymbolField, singlePlayerCheckbox, buttonLayout);
    }
}
