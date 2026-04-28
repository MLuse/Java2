package hr.algebra.trirp1.tictactoe.tictactoe3rp11.utils;

import hr.algebra.trirp1.tictactoe.tictactoe3rp11.model.Position;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.thread.ReadTheLastGameMoveThread;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileUtils {

    private FileUtils() {}

    public static final String GAME_MOVE_HISTORY_FILE_NAME = "dat/gameMoves.dat";
    public static final AtomicBoolean FILE_ACCESS_IN_PROGRESS = new AtomicBoolean(false);

    public static Optional<Position> determineButtonPosition(Button button, Button[][] board) {
        Optional<Position> position = Optional.empty();

        for(Integer i = 0; i < GameUtils.NUMBER_OF_ROWS; i++) {
            for(Integer j = 0; j < GameUtils.NUMBER_OF_COLUMNS; j++) {
                if(board[i][j] == button) {
                    position = Optional.of(new Position(i, j));
                }
            }
        }

        return position;
    }

    public static Timeline getTheLastGameMoveRefreshTimeline(Label label)
    {
        Timeline showTheLastGameMoveTimeline = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            ReadTheLastGameMoveThread thread = new ReadTheLastGameMoveThread(label);
            Thread theLastGameMoveThreadRunner = new Thread(thread);
            theLastGameMoveThreadRunner.start();
        }), new KeyFrame(Duration.seconds(5)));

        showTheLastGameMoveTimeline.setCycleCount(Animation.INDEFINITE);
        return showTheLastGameMoveTimeline;
    }
}
