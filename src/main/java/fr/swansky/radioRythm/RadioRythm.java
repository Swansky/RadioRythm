package fr.swansky.radioRythm;

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
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.util.Collections;
import java.util.List;

public class RadioRythm {
    private static final Logger LOGGER = Logger.getLogger(RadioRythm.class);
    private static RadioRythm INSTANCE;
    private final Settings settings;
    private TranslationManager translationManager;

    public RadioRythm() throws InstanceCreationException, LoginException {
        INSTANCE = this;

        this.settings = SettingsManager.loadSettings();
        if (this.settings.isEmpty()) {
            LOGGER.warn("Please configurer settings file.");
            return;
        }
        this.translationManager = new TranslationManager(settings.getLanguage());

        DiscordCommandIOCConfig discordCommandIOCConfig = new DiscordCommandIOCConfig();
        discordCommandIOCConfig.setDisableDefaultHelpCommand(true);

        SwansIOC swansIOC = SwansIOC.InitIOC(RadioRythm.class);
        swansIOC.getConfigExtensionManager().addConfigExtension(discordCommandIOCConfig);
        swansIOC.startIOC();


        JDA jda = JDABuilder.createDefault(settings.getToken()).addEventListeners(new CommandListener(settings)).build();
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        for (Logger logger : loggers) {
            logger.setLevel(Level.WARN);
        }

        SettingsManager.createSampleSettings();


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
