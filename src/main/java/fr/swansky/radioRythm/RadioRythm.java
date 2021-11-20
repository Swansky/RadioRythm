package fr.swansky.radioRythm;

import fr.swansky.discordCommandIOC.Commands.CommandManager;
import fr.swansky.discordCommandIOC.config.DiscordCommandIOCConfig;
import fr.swansky.ioccontainer.SwansIOC;
import fr.swansky.radioRythm.listeners.CommandListener;
import fr.swansky.radioRythm.settings.Settings;
import fr.swansky.radioRythm.settings.SettingsManager;
import fr.swansky.radioRythm.settings.TranslationManager;
import fr.swansky.swansAPI.exception.InstanceCreationException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import javax.security.auth.login.LoginException;

public class RadioRythm {
    private static final Logger LOGGER = Logger.getLogger(RadioRythm.class);
    private static RadioRythm INSTANCE;
    private final TranslationManager translationManager;
    private final Settings settings;

    public RadioRythm() throws InstanceCreationException, LoginException {
        INSTANCE = this;

        this.settings = SettingsManager.loadSettings();
        this.translationManager = new TranslationManager(settings.getLanguage());

        DiscordCommandIOCConfig discordCommandIOCConfig = new DiscordCommandIOCConfig();
        discordCommandIOCConfig.setDisableDefaultHelpCommand(true);

        SwansIOC swansIOC = SwansIOC.CreateIOC(RadioRythm.class);
        swansIOC.getConfigExtensionManager().addConfigExtension(discordCommandIOCConfig);
        swansIOC.CreateIOC();


        JDA jda = JDABuilder.createDefault(settings.getToken()).addEventListeners(new CommandListener(settings)).build();
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("--setup-settings")) {
                SettingsManager.createSampleSettings();
                return;
            }
        }
        try {
            new RadioRythm();
        } catch (InstanceCreationException | LoginException e) {
            LOGGER.fatal("", e);
        }
    }

    public static RadioRythm getINSTANCE() {
        return INSTANCE;
    }

    public TranslationManager getTranslationManager() {
        return translationManager;
    }

    public Settings getSettings() {
        return settings;
    }
}
