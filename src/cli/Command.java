package cli;

import org.eclipse.jdt.annotation.Nullable;

public sealed interface Command
        permits Command.Add, Command.List, Command.Show, Command.Edit,
                Command.Done, Command.Delete, Command.Exit {

    record Add() implements Command {}

    record List(@Nullable String tag) implements Command {}

    record Show(long id) implements Command {}

    record Edit(long id) implements Command {}

    record Done(long id) implements Command {}

    record Delete(long id) implements Command {}

    record Exit() implements Command {}
}
