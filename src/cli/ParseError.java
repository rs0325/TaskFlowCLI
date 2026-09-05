package cli;

public enum ParseError {
    EMPTY_INPUT("コマンドが入力されていません。"),
    UNKNOWN_COMMAND("不明なコマンドです。"),
    UNKNOWN_OPTION("不明なオプションです。"),
    ID_REQUIRED("IDを指定してください。"),
    INVALID_ID("IDは数値で指定してください。"),
    TAG_REQUIRED("タグを指定してください。");

    private final String message;

    ParseError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
