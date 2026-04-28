package hr.algebra.trirp1.tictactoe.tictactoe3rp11.model;

public enum GameMoveTag {
    GAME_MOVE("GameMove"), SYMBOL("Symbol"),
    POSITION_X("PositionX"), POSITION_Y("PositionY");

    private String tagName;

    GameMoveTag(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }
}
