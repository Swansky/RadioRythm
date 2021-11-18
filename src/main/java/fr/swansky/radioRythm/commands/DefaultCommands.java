package fr.swansky.radioRythm.commands;

import fr.swansky.discordCommandIOC.Commands.annotations.Command;
import fr.swansky.discordCommandIOC.Commands.annotations.CommandsContainer;
import net.dv8tion.jda.api.entities.TextChannel;

@CommandsContainer
public class DefaultCommands {
    @Command(name = "info", description = "show info about bot")
    public void info(TextChannel textChannel) {
        textChannel.sendMessage("test").queue();
    }
}
