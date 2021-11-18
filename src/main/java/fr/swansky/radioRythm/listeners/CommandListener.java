package fr.swansky.radioRythm.listeners;

import fr.swansky.discordCommandIOC.Commands.CommandManager;
import fr.swansky.discordCommandIOC.DiscordCommandIOC;
import fr.swansky.radioRythm.settings.Settings;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandListener extends ListenerAdapter {
    private final CommandManager commandManager;
    private final Settings settings;

    public CommandListener(Settings settings) {
        this.commandManager = DiscordCommandIOC.getCommandManager();
        this.settings = settings;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        Message message = event.getMessage();

        if (message.getContentDisplay().startsWith(settings.getTag())) {
            if (commandManager.commandUser(message.getAuthor(), message.getContentDisplay().replaceFirst(settings.getTag(), ""), message)) {
                event.getMessage().addReaction("\uD83D\uDC4D").queue();
            } else {
                event.getMessage().addReaction("\uD83D\uDC4E").queue();
            }
        }

    }
}
