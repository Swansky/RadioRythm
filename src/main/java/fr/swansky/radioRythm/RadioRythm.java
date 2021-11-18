package fr.swansky.radioRythm;

import fr.swansky.ioccontainer.SwansIOC;
import fr.swansky.radioRythm.listeners.CommandListener;
import fr.swansky.radioRythm.settings.Settings;
import fr.swansky.radioRythm.settings.SettingsManager;
import fr.swansky.swansAPI.exception.InstanceCreationException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import javax.security.auth.login.LoginException;

public class RadioRythm {
    private static final Logger LOGGER = Logger.getLogger(RadioRythm.class);
    private static RadioRythm INSTANCE;
    private final JDA jda;

    public RadioRythm() throws InstanceCreationException, LoginException {
        SwansIOC swansIOC = SwansIOC.CreateIOC(RadioRythm.class);
        swansIOC.CreateIOC();


        Settings settings = SettingsManager.loadSettings();

        this.jda = JDABuilder.createDefault(settings.getToken()).addEventListeners(new CommandListener(settings)).build();
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
            INSTANCE = new RadioRythm();
        } catch (InstanceCreationException | LoginException e) {
            e.printStackTrace();
        }
    }

    public static RadioRythm getINSTANCE() {
        return INSTANCE;
    }

    public JDA getJda() {
        return jda;
    }
}
