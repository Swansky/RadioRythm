package fr.swansky.radioRythm.commands;

import fr.swansky.discordCommandIOC.Commands.SimpleCommand;
import fr.swansky.discordCommandIOC.Commands.annotations.Command;
import fr.swansky.discordCommandIOC.Commands.annotations.CommandsContainer;
import fr.swansky.discordCommandIOC.DiscordCommandIOC;
import fr.swansky.radioRythm.RadioRythm;
import fr.swansky.radioRythm.settings.Settings;
import fr.swansky.radioRythm.settings.TranslationManager;
import fr.swansky.radioRythm.settings.exceptions.TranslationKeyNotFoundException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.entities.UserImpl;
import org.apache.log4j.Logger;

import java.awt.*;

@CommandsContainer
public class DefaultCommands {
    private static final Logger LOGGER = Logger.getLogger(DefaultCommands.class);
    private final TranslationManager translationManager;
    private final Settings settings;

    public DefaultCommands() {
        RadioRythm radioRythm = RadioRythm.getINSTANCE();
        this.translationManager = radioRythm.getTranslationManager();
        this.settings = radioRythm.getSettings();
    }

    @Command(name = "help", description = "commands.help.description")
    public void help(TextChannel textChannel, User user) {
        try {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(translationManager.getTranslationByKey("commands.help.title"));
            embedBuilder.setColor(Color.GREEN);
            embedBuilder.addField("", String.format(translationManager.getTranslationByKey("commands.help.explanation"), RadioRythm.getINSTANCE().getSettings().getTag()), false);
            for (SimpleCommand command : DiscordCommandIOC.getCommandManager().getCommands()) {
                String description = command.getDescription();
                if (!description.contains(" ") && !description.isEmpty()) {
                    description = translationManager.getTranslationByKey(command.getDescription());
                }
                embedBuilder.addField("**" + command.getName() + "**", "" + description + "", true);
            }
            if (!user.hasPrivateChannel()) {
                user.openPrivateChannel().complete();
                ((UserImpl) user).getPrivateChannel().sendMessageEmbeds(embedBuilder.build()).queue();
            }
            textChannel.sendMessage(user.getAsMention() + translationManager.getTranslationByKey("commands.help.message")).queue();

        } catch (TranslationKeyNotFoundException e) {
            LOGGER.error("help command", e);
        }

    }
}
