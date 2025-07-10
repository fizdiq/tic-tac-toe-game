package com.fizdiq.tictactoegame.viewmodels;

import com.fizdiq.tictactoegame.dto.request.GameConfig;
import com.fizdiq.tictactoegame.services.TicTacToeService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "config", layout = MainLayout.class)
public class GameConfigView extends VerticalLayout {

    @Autowired
    private TicTacToeService ticTacToeService;

    public GameConfigView() {
        TextField boardSizeField = new TextField("Board Size (e.g., 3 for 3x3)");
        boardSizeField.setRequired(true);
        boardSizeField.setPlaceholder("Enter size... (e.g., 3 for 3x3)");
        boardSizeField.setValue("3");

        TextField winStreakField = new TextField("Win Streak (e.g., 3 for three in a row)");
        winStreakField.setRequired(true);
        winStreakField.setPlaceholder("Enter win streak... (e.g., 3 for three in a row)");
        winStreakField.setValue("3");

        TextField player1SymbolField = new TextField("First Player Symbol (e.g., X)");
        player1SymbolField.setRequired(true);
        player1SymbolField.setPlaceholder("Enter player 1 symbol... (e.g., X)");
        player1SymbolField.setValue("X");

        TextField player2SymbolField = new TextField("Second Player Symbol (e.g., O)");
        player2SymbolField.setRequired(true);
        player2SymbolField.setPlaceholder("Enter player 2 symbol... (e.g., O)");
        player2SymbolField.setValue("O");

        Checkbox singlePlayerCheckbox = new Checkbox("Single Player Mode (vs Computer)");
        singlePlayerCheckbox.setValue(true);

        Button startGameButton = new Button("Start Game", click -> {
            try {
                int boardSize = Integer.parseInt(boardSizeField.getValue());
                int winStreak = Integer.parseInt(winStreakField.getValue());
                String player1Symbol = player1SymbolField.getValue();
                String player2Symbol = player2SymbolField.getValue();
                boolean isSinglePlayer = singlePlayerCheckbox.getValue();

                if (boardSize < 3 || winStreak > boardSize) {
                    Notification.show("Invalid Board Size or Win Streak! Must be >= 3 and win streak <= board size.");
                    return;
                }

                if (player1Symbol.equals(player2Symbol)) {
                    Notification.show("Player 1 and Player 2 symbols must be different!");
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

        add(boardSizeField, winStreakField, player1SymbolField, player2SymbolField, singlePlayerCheckbox, startGameButton);
    }
}
