package repository.result;

public enum LoadError {
    FILE_NOT_FOUND("ファイルまたはディレクトリが存在しません。"),
    INVALID_JSON("JSONの形式が不正です。"),
    IO_ERROR("ファイルまたはディレクトリの読み込みに失敗しました。"),
    ACCESS_DENIED("ファイルまたはディレクトリへのアクセスが拒否されました。");

    private final String message;

    LoadError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
