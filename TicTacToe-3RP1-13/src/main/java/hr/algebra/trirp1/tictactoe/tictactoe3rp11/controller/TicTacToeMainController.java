package hr.algebra.trirp1.tictactoe.tictactoe3rp11.controller;

import hr.algebra.trirp1.tictactoe.tictactoe3rp11.TicTacToeApplication;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.model.*;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.rmi.ChatRemoteService;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.utils.*;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

import static hr.algebra.trirp1.tictactoe.tictactoe3rp11.utils.GameUtils.NUMBER_OF_COLUMNS;
import static hr.algebra.trirp1.tictactoe.tictactoe3rp11.utils.GameUtils.NUMBER_OF_ROWS;

public class TicTacToeMainController {

    private static final Logger log = LoggerFactory.getLogger(TicTacToeMainController.class);

    @FXML
    private Button buttontl;

    @FXML
    private Button buttontc;

    @FXML
    private Button buttontr;

    @FXML
    private Button buttonml;

    @FXML
    private Button buttonmc;

    @FXML
    private Button buttonmr;

    @FXML
    private Button buttonbl;

    @FXML
    private Button buttonbc;

    @FXML
    private Button buttonbr;

    @FXML
    private Button chatMessageButton;

    @FXML
    private TextField chatMessageTextField;

    @FXML
    private TextArea chatMessageTextArea;

    @FXML
    private Label theLastGameMoveLabel;

    private Integer numberOfMoves = 0;
    private ChatRemoteService chatRemoteService;

    public void initialize() {
        TicTacToeApplication.loggedInPlayer.getBoard()[0][0] = buttontl;
        TicTacToeApplication.loggedInPlayer.getBoard()[0][1] = buttontc;
        TicTacToeApplication.loggedInPlayer.getBoard()[0][2] = buttontr;
        TicTacToeApplication.loggedInPlayer.getBoard()[1][0] = buttonml;
        TicTacToeApplication.loggedInPlayer.getBoard()[1][1] = buttonmc;
        TicTacToeApplication.loggedInPlayer.getBoard()[1][2] = buttonmr;
        TicTacToeApplication.loggedInPlayer.getBoard()[2][0] = buttonbl;
        TicTacToeApplication.loggedInPlayer.getBoard()[2][1] = buttonbc;
        TicTacToeApplication.loggedInPlayer.getBoard()[2][2] = buttonbr;

        numberOfMoves = 0;

        if (PlayerType.PLAYER_2.name().equals(TicTacToeApplication.loggedInPlayer.getPlayerType().name())) {
            disableButtons(true);
        } else if (PlayerType.PLAYER_1.name().equals(TicTacToeApplication.loggedInPlayer.getPlayerType().name())) {
            disableButtons(false);
        }

        TicTacToeApplication.loggedInPlayer.setGameNumber(TicTacToeApplication.loggedInPlayer.getGameNumber() + 1);

        if(!TicTacToeApplication.loggedInPlayer.getPlayerType().equals(PlayerType.SINGLE_PLAYER)) {
            Optional<ChatRemoteService> chatRemoteServiceOptional = ChatUtils.initializeChatRemoteService();
            chatRemoteServiceOptional.ifPresent(remoteService -> chatRemoteService = remoteService);

            Timeline chatMessagesRefreshTimeLine = ChatUtils.getChatRefreshTimeline(chatRemoteService, chatMessageTextArea);
            chatMessagesRefreshTimeLine.play();
        }
        else {
            Timeline showTheLastGameMoveTimeline = FileUtils.getTheLastGameMoveRefreshTimeline(theLastGameMoveLabel);
            showTheLastGameMoveTimeline.play();
            chatMessageTextField.setVisible(false);
            chatMessageTextArea.setVisible(false);
            chatMessageButton.setVisible(false);
        }
    }

    public void buttonPressed(ActionEvent event) {

        numberOfMoves++;
        boolean winnerExists = false;

        if (event.getSource() instanceof Button b && b.getText().isEmpty()) {

            Optional<Position> positionOptional = FileUtils.determineButtonPosition(b, TicTacToeApplication.loggedInPlayer.getBoard());

            if(positionOptional.isPresent()) {
                GameUtils.createGameAndSaveWithThread(positionOptional, TicTacToeApplication.loggedInPlayer.getTurn());
            }

            b.setText(TicTacToeApplication.loggedInPlayer.getTurn().name());

            GameState gameState = GameUtils.generateGameState(TicTacToeApplication.loggedInPlayer.getBoard(), TicTacToeApplication.loggedInPlayer.getTurn(), false, numberOfMoves);

            if (TicTacToeApplication.loggedInPlayer.getPlayerType().name().equals(PlayerType.PLAYER_1.name())) {
                TicTacToeApplication.sendRequestPlayerOne(gameState);
            } else if (TicTacToeApplication.loggedInPlayer.getPlayerType().name().equals(PlayerType.PLAYER_2.name())) {
                TicTacToeApplication.sendRequestPlayerTwo(gameState);
            }
        }

        winnerExists = GameUtils.winnerExists(TicTacToeApplication.loggedInPlayer.getBoard(), TicTacToeApplication.loggedInPlayer.getTurn());

        if (winnerExists) {
            numberOfMoves = 0;
            DialogUtils.showDialog("Pobijedio je " + TicTacToeApplication.loggedInPlayer.getTurn().name(), "Pobjednik je igrač koji koristi simbol "
                    + TicTacToeApplication.loggedInPlayer.getTurn().name(), Alert.AlertType.INFORMATION);
        }

        if (!winnerExists && numberOfMoves == NUMBER_OF_ROWS * NUMBER_OF_COLUMNS) {
            DialogUtils.showDialog("Nema pobjednika!", "Nijedan igrač nije pobijedio!",
                    Alert.AlertType.INFORMATION);
            numberOfMoves = 0;
        }

        TicTacToeApplication.loggedInPlayer.setTurn(TicTacToeApplication.loggedInPlayer.getTurn().equals(Symbol.X) ? Symbol.O : Symbol.X);

        if(!TicTacToeApplication.loggedInPlayer.getPlayerType().name().equals(PlayerType.SINGLE_PLAYER.name())) {
            disableButtons(true);
        }
    }

    public void startNewGame() {
        GameUtils.startNewGame(TicTacToeApplication.loggedInPlayer.getBoard());
    }

    public void saveGame() {
        GameState gameStateToSave = GameUtils.generateGameState(TicTacToeApplication.loggedInPlayer.getBoard(), TicTacToeApplication.loggedInPlayer.getTurn(), false, numberOfMoves);
        GameUtils.saveGame(gameStateToSave);
    }

    public void loadGame() {
        numberOfMoves = GameUtils.loadGameState(GameUtils.loadGame(), TicTacToeApplication.loggedInPlayer.getBoard());
    }

    public void generateDocumentation() {
        try {
            DocumentationUtils.generateHtmlDocumentationFile();
            DialogUtils.showDialog("Uspješno generirana dokumentacija!",
                    "HTML dokumentacija za aplikaciju je uspješno generirana!", Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            String message = "Došlo je do pogreške kod generiranja HTML dokumentacije!";
            DialogUtils.showDialog("Pogreška!", message, Alert.AlertType.INFORMATION);
            log.error(message, e);
        }
    }

    public static void restoreGameState(GameState gameState) {
        Platform.runLater(() -> GameUtils.loadGameState(gameState, TicTacToeApplication.loggedInPlayer.getBoard()));
    }

    public static void disableButtons(Boolean disable) {
        for (int i = 0; i < NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                TicTacToeApplication.loggedInPlayer.getBoard()[i][j].setDisable(disable);
            }
        }
    }

    public void sendChatMessage() {
        ChatUtils.sendChatMessage(chatRemoteService, chatMessageTextField);
    }

    public void replayGame() {
        GameUtils.startReplayGame(TicTacToeApplication.loggedInPlayer.getBoard());
    }
}