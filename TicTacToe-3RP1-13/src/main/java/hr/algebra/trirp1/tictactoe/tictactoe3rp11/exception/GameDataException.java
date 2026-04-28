package hr.algebra.trirp1.tictactoe.tictactoe3rp11.exception;

public class GameDataException extends RuntimeException {

    public GameDataException() {
    }

    public GameDataException(String message) {
        super(message);
    }

    public GameDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameDataException(Throwable cause) {
        super(cause);
    }

    public GameDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
