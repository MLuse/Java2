package hr.algebra.trirp1.tictactoe.tictactoe3rp11.thread;

import hr.algebra.trirp1.tictactoe.tictactoe3rp11.model.GameMove;
import javafx.application.Platform;
import javafx.scene.control.Label;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class ReadTheLastGameMoveThread extends AbstractTheLastGameMoveThread implements Runnable {

    private Label label;

    @Override
    public void run() {

        List<GameMove> gameMoves = loadGameMoves();

        if (!gameMoves.isEmpty()) {

            GameMove lastMove = gameMoves.getLast();
            Platform.runLater(
                    () -> label.setText(lastMove.getSymbol() + " position: ("
                            + lastMove.getPosition().getRow() + ", "
                            + lastMove.getPosition().getColumn() + ")")
            );

        }
    }
}
