package repository.result;

import org.eclipse.jdt.annotation.Nullable;

public sealed interface SaveAllResult permits SaveAllResult.Success, SaveAllResult.Failure {

	record Success() implements SaveAllResult {}

	record Failure(SaveError error, @Nullable String fileName) implements SaveAllResult {}

}
