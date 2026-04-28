package hr.algebra.trirp1.tictactoe.tictactoe3rp11.utils;

import hr.algebra.trirp1.tictactoe.tictactoe3rp11.TicTacToeApplication;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.controller.TicTacToeMainController;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.exception.GameDataException;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.model.*;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.thread.SaveTheLastGameMoveThread;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.util.Duration;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class GameUtils {

    private GameUtils() {}

    public static final Integer NUMBER_OF_ROWS = 3;
    public static final Integer NUMBER_OF_COLUMNS = 3;
    private static final String SAVE_GAME_FILE_PATH = "game/save.dat";

    public static void startNewGame(Button[][] board) {

        TicTacToeApplication.loggedInPlayer.setGameNumber(TicTacToeApplication.loggedInPlayer.getGameNumber() + 1);

        for (int i = 0; i < NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                board[i][j].setText("");
            }
        }

        TicTacToeApplication.loggedInPlayer.setTurn(TicTacToeApplication.loggedInPlayer.getGameNumber() % 2 != 0 ? Symbol.O : Symbol.X);

        if(!TicTacToeApplication.loggedInPlayer.getPlayerType().name().equals(PlayerType.SINGLE_PLAYER.name())) {
            GameState gameState = generateGameState(board, TicTacToeApplication.loggedInPlayer.getTurn(), true, 0);
            if(TicTacToeApplication.loggedInPlayer.getPlayerType().name().equals(PlayerType.PLAYER_1.name())) {
                TicTacToeApplication.sendRequestPlayerOne(gameState);
            }
            else if (TicTacToeApplication.loggedInPlayer.getPlayerType().name().equals(PlayerType.PLAYER_2.name())) {
                TicTacToeApplication.sendRequestPlayerTwo(gameState);
            }
            TicTacToeMainController.disableButtons(false);
        }
    }

    public static boolean winnerExists(Button[][] board, Symbol turn) {

        boolean winnerExists = false;

        for (int i = 0; i < GameUtils.NUMBER_OF_COLUMNS; i++) {
            if (board[i][0].getText().equals(turn.name()) &&
                    board[i][1].getText().equals(turn.name()) &&
                    board[i][2].getText().equals(turn.name())) {
                return true;
            }

            if (board[0][i].getText().equals(turn.name()) &&
                    board[1][i].getText().equals(turn.name()) &&
                    board[2][i].getText().equals(turn.name())) {
                return true;
            }
        }

        if (board[0][0].getText().equals(turn.name()) &&
                board[1][1].getText().equals(turn.name()) &&
                board[2][2].getText().equals(turn.name())) {
            return true;
        }

        if (board[2][0].getText().equals(turn.name()) &&
                board[1][1].getText().equals(turn.name()) &&
                board[0][2].getText().equals(turn.name())) {
            return true;
        }
        return winnerExists;
    }

    public static boolean winnerExists(String[][] board, Symbol turn) {

        boolean winnerExists = false;

        for (int i = 0; i < GameUtils.NUMBER_OF_COLUMNS; i++) {
            if (board[i][0].equals(turn.name()) &&
                    board[i][1].equals(turn.name()) &&
                    board[i][2].equals(turn.name())) {
                return true;
            }

            if (board[0][i].equals(turn.name()) &&
                    board[1][i].equals(turn.name()) &&
                    board[2][i].equals(turn.name())) {
                return true;
            }
        }

        if (board[0][0].equals(turn.name()) &&
                board[1][1].equals(turn.name()) &&
                board[2][2].equals(turn.name())) {
            return true;
        }

        if (board[2][0].equals(turn.name()) &&
                board[1][1].equals(turn.name()) &&
                board[0][2].equals(turn.name())) {
            return true;
        }
        return winnerExists;
    }

    public static void saveGame(GameState gameState) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_GAME_FILE_PATH))) {
            oos.writeObject(gameState);
        } catch (IOException e) {
            throw new GameDataException("An error occured while save the game state!", e);
        }
    }

    public static GameState loadGame() {
        GameState loadedGameState;
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_GAME_FILE_PATH))) {
            loadedGameState =  (GameState) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new GameDataException("Error occured while loading game state!", e);
        }

        return loadedGameState;
    }

    public static GameState generateGameState(Button[][] board, Symbol turn, Boolean newGame, Integer numberOfTurns) {
        String[][] symbolBoard = new String[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];

        for (int i = 0; i < NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                symbolBoard[i][j] = board[i][j].getText();
            }
        }

        return new GameState(symbolBoard, turn, newGame, numberOfTurns);
    }

    public static void startReplayGame(Button[][] board) {
        List<GameMove> gameMoveList = XmlUtils.loadGameMoves();

        AtomicInteger index = new AtomicInteger(0);

        Timeline replayTimeLine = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            GameMove gameMove = gameMoveList.get(index.get());
            board[gameMove.getPosition().getRow()][gameMove.getPosition().getColumn()]
                    .setText(gameMove.getSymbol().name());
            index.set(index.get() + 1);
        }), new KeyFrame(Duration.seconds(2)));

        replayTimeLine.setCycleCount(gameMoveList.size());
        replayTimeLine.play();
    }

    public static void createGameAndSaveWithThread(Optional<Position> positionOptional, Symbol turn) {
        GameMove gameMove = new GameMove(turn, positionOptional.get());
        XmlUtils.saveNewMove(gameMove);
        SaveTheLastGameMoveThread thread = new SaveTheLastGameMoveThread(gameMove);
        Thread saveTheLastGameMoveThreadRunner = new Thread(thread);
        saveTheLastGameMoveThreadRunner.start();
    }

    public static Integer loadGameState(GameState gameState, Button[][] board) {
        for (int i = 0; i < NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                board[i][j].setText(gameState.getBoard()[i][j]);
            }
        }

        return gameState.getNumberOfTurns();
    }
}