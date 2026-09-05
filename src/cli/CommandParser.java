package cli;

import java.util.function.LongFunction;

public class CommandParser {
    private static final String TAG_OPTION = "--tag";

    public ParseResult parse(String input) {
        if (input == null) {
            return new ParseResult.Failure(ParseError.EMPTY_INPUT);
        }

        String trimmed = input.trim();

        if (trimmed.isEmpty()) {
            return new ParseResult.Failure(ParseError.EMPTY_INPUT);
        }

        String[] tokens = trimmed.split("\\s+");

        return switch (tokens[0]) {
            case "add" -> new ParseResult.Success(new Command.Add());
            case "list" -> parseList(tokens);
            case "show" -> parseId(tokens, Command.Show::new);
            case "edit" -> parseId(tokens, Command.Edit::new);
            case "done" -> parseId(tokens, Command.Done::new);
            case "delete" -> parseId(tokens, Command.Delete::new);
            case "exit" -> new ParseResult.Success(new Command.Exit());
            default -> new ParseResult.Failure(ParseError.UNKNOWN_COMMAND);
        };
    }

    private ParseResult parseList(String[] tokens) {
        if (tokens.length == 1) {
            return new ParseResult.Success(new Command.List(null));
        }

        if (!TAG_OPTION.equals(tokens[1])) {
            return new ParseResult.Failure(ParseError.UNKNOWN_OPTION);
        }

        if (tokens.length < 3) {
            return new ParseResult.Failure(ParseError.TAG_REQUIRED);
        }

        return new ParseResult.Success(new Command.List(tokens[2]));
    }

    private ParseResult parseId(String[] tokens, LongFunction<Command> factory) {
        if (tokens.length < 2) {
            return new ParseResult.Failure(ParseError.ID_REQUIRED);
        }

        try {
            return new ParseResult.Success(factory.apply(Long.parseLong(tokens[1])));
        } catch (NumberFormatException e) {
            return new ParseResult.Failure(ParseError.INVALID_ID);
        }
    }
}
