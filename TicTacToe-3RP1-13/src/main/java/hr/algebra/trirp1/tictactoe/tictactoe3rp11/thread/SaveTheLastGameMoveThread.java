package hr.algebra.trirp1.tictactoe.tictactoe3rp11.thread;

import hr.algebra.trirp1.tictactoe.tictactoe3rp11.exception.GameDataException;
import hr.algebra.trirp1.tictactoe.tictactoe3rp11.model.GameMove;
import lombok.AllArgsConstructor;

import java.io.FileNotFoundException;

@AllArgsConstructor
public class SaveTheLastGameMoveThread extends AbstractTheLastGameMoveThread implements Runnable {

    private GameMove gameMove;

    @Override
    public void run() {
        try {
            saveTheLastGameMove(gameMove);
        } catch (FileNotFoundException e) {
            throw new GameDataException("An error occured while saving the last game move!", e);
        }
    }
}
