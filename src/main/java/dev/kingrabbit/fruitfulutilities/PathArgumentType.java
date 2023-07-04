package dev.kingrabbit.fruitfulutilities;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.kingrabbit.fruitfulutilities.pathviewer.PathScreen;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class PathArgumentType implements ArgumentType<String> {

    private static final DynamicCommandExceptionType INVALID_PATH_EXCEPTION = new DynamicCommandExceptionType(path -> Text.of("Invalid path \"" + path + "\""));

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String string = reader.readUnquotedString();
        if (Arrays.asList(PathScreen.section_order).contains(string)) return string;
        throw INVALID_PATH_EXCEPTION.createWithContext(reader, string);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(PathScreen.section_order, builder);
    }

}
