package hr.algebra.trirp1.tictactoe.tictactoe3rp11;

import hr.algebra.trirp1.tictactoe.tictactoe3rp11.controller.TicTacToeMainController;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.jndi.ConfigurationKey;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.jndi.ConfigurationReader;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.model.GameState;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.model.LoggedInPlayerGameContext;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.model.PlayerType;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.utils.DialogUtils;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.utils.GameUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicTacToeApplication extends Application {

    public static final LoggedInPlayerGameContext loggedInPlayer = new LoggedInPlayerGameContext();
    private static final Logger logger = Logger.getLogger(TicTacToeApplication.class.getName());

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TicTacToeApplication.class.getResource("MainScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 800);
        stage.setTitle("Križić - kružić - " + loggedInPlayer.getPlayerType().toString());
        stage.setScene(scene);
        stage.show();

        if(!PlayerType.SINGLE_PLAYER.name().equals(loggedInPlayer.getPlayerType().name())) {
            if (PlayerType.PLAYER_2.name().equals(loggedInPlayer.getPlayerType().name())) {
                Thread serverThread = new Thread(() -> acceptRequests(
                        ConfigurationReader.getIntegerValueForKey(ConfigurationKey.PLAYER_2_SERVER_PORT)));
                serverThread.start();
            }
            else {
                Thread serverThread = new Thread(() -> acceptRequests(
                        ConfigurationReader.getIntegerValueForKey(ConfigurationKey.PLAYER_1_SERVER_PORT)));
                serverThread.start();
            }
        }
    }

    public static void main(String[] args) {

        String firstCommandLineArg = args[0];

        Boolean playerTypeExists = false;

        for (PlayerType playerType : PlayerType.values()) {
            if (firstCommandLineArg.equals(playerType.toString())) {
                playerTypeExists = true;
                break;
            }
        }

        if(Boolean.FALSE.equals(playerTypeExists)) {
            logger.log(Level.INFO, "You provided a player type that does not exist!");
            JOptionPane.showMessageDialog(null,
                    "You provided a player type that does not exist!");
            System.exit(0);
        }
        else {
            loggedInPlayer.setPlayerType(PlayerType.valueOf(firstCommandLineArg));
            launch();
        }
    }

    private static void acceptRequests(Integer port) {
        try (ServerSocket serverSocket = new ServerSocket(port)){
            logger.log(Level.INFO, "Server listening on port: {0}", serverSocket.getLocalPort());

            while (!PlayerType.SINGLE_PLAYER.name().equals(loggedInPlayer.getPlayerType().name())) {
                Socket clientSocket = serverSocket.accept();
                logger.log(Level.INFO, "Client connected from port {0}", clientSocket.getPort());
                new Thread(() ->  processSerializableClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processSerializableClient(final Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());){
            GameState gameState = (GameState)ois.readObject();
            TicTacToeMainController.restoreGameState(gameState);

            Boolean winnerExists = GameUtils.winnerExists(gameState.getBoard(), gameState.getTurn());
            TicTacToeMainController.disableButtons(true);

            if(gameState.getTurn().name().equals(TicTacToeApplication.loggedInPlayer.getTurn().name())) {
                TicTacToeMainController.disableButtons(false);
            }

            if(Boolean.TRUE.equals(winnerExists)) {
                Platform.runLater(() ->
                            DialogUtils.showDialog("Pobijedio je " + gameState.getTurn().name(),
                                    "Pobjednik je igrač koji koristi simbol " + gameState.getTurn().name(),
                                    Alert.AlertType.INFORMATION));

                    TicTacToeMainController.disableButtons(true);
            }

            logger.log(Level.INFO, "Game state received from Player 1");
            oos.writeObject("Success");

            logger.log(Level.INFO, "Winner exists: {0}", winnerExists);
            logger.log(Level.INFO,"Turn: {0}", gameState.getTurn());

            if(Boolean.FALSE.equals(winnerExists) && !gameState.getNewGame()) {
                logger.info("WinnerExists & Not a new game");
                Platform.runLater(() -> TicTacToeMainController.disableButtons(false));
            }

            if(Boolean.FALSE.equals(winnerExists) && gameState.getNumberOfTurns() ==
                    GameUtils.NUMBER_OF_COLUMNS * GameUtils.NUMBER_OF_ROWS) {
                Platform.runLater(() -> DialogUtils.showDialog("Neriješeno!", "Nema pobjednika!",
                        Alert.AlertType.INFORMATION));
                TicTacToeMainController.disableButtons(true);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void sendRequestPlayerOne(final GameState gameState) {
        try (Socket clientSocket = new Socket(
                ConfigurationReader.getStringValueForKey(ConfigurationKey.HOSTNAME),
                ConfigurationReader.getIntegerValueForKey(ConfigurationKey.PLAYER_2_SERVER_PORT))){
            logger.info("Client is connecting to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

            sendSerializableRequest(clientSocket, gameState);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void sendRequestPlayerTwo(final GameState gameState) {
        try (Socket clientSocket = new Socket(ConfigurationReader.getStringValueForKey(ConfigurationKey.HOSTNAME),
                ConfigurationReader.getIntegerValueForKey(ConfigurationKey.PLAYER_1_SERVER_PORT)))
        {
            logger.info("Client is connecting to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

            sendSerializableRequest(clientSocket, gameState);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void sendSerializableRequest(Socket client, GameState gameState) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
        oos.writeObject(gameState);
        logger.log(Level.INFO, "GameState sent to Player2");
        logger.log(Level.INFO, "Object value read: {0}", ois.readObject());
    }
}