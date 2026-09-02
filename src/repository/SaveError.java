package repository;

public enum SaveError {
    IO_ERROR("ファイルの保存に失敗しました。"),
    ACCESS_DENIED("ファイルへのアクセスが拒否されました。"),
    DIRECTORY_CREATE_FAILED("保存ディレクトリの作成に失敗しました。");

    private final String message;

    SaveError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
