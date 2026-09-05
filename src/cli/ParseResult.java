package cli;

public sealed interface ParseResult
        permits ParseResult.Success, ParseResult.Failure {

    record Success(Command command) implements ParseResult {}

    record Failure(ParseError error) implements ParseResult {}
}
