package hr.algebra.trirp1.tictactoe.tictactoe3rp11.constants;

public enum Messages {

    FILE_ACCESS_ERROR_MESSAGE("Waiting during file access attempt failed!");

    private String message;

    Messages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
