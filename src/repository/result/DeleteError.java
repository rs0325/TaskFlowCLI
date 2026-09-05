package repository.result;

public enum DeleteError {
    IO_ERROR("ファイルの削除に失敗しました。"),
    ACCESS_DENIED("ファイルへのアクセスが拒否されました。");

    private final String message;

    DeleteError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
