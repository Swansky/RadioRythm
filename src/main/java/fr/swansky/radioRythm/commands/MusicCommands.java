package fr.swansky.radioRythm.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.swansky.discordCommandIOC.Commands.annotations.Command;
import fr.swansky.discordCommandIOC.Commands.annotations.CommandsContainer;
import fr.swansky.radioRythm.RadioRythm;
import fr.swansky.radioRythm.commands.exceptions.MusicVoiceStateException;
import fr.swansky.radioRythm.music.AudioHandler;
import fr.swansky.radioRythm.music.MusicManager;
import fr.swansky.radioRythm.music.MusicPlayer;
import fr.swansky.radioRythm.settings.TranslationManager;
import fr.swansky.radioRythm.settings.exceptions.TranslationKeyNotFoundException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;
import org.apache.log4j.Logger;

import java.awt.*;

@CommandsContainer
public class MusicCommands {
    private static final Logger LOGGER = Logger.getLogger(RadioRythm.class);
    private static final int MAX_VOLUME = 100;
    private static final int MIN_VOLUME = 10;
    private final MusicManager manager = new MusicManager();
    private final TranslationManager translationManager;
    private String translationErrorMessage = "Internal error pls contact administrator code type: command.error.translation";

    public MusicCommands() {
        this.translationManager = RadioRythm.getINSTANCE().getTranslationManager();
        try {
            this.translationErrorMessage = translationManager.getTranslationByKey("commands.error.translation");
        } catch (TranslationKeyNotFoundException e) {
            LOGGER.fatal("Music commands ", e);
        }
    }

    private static String getTimestamp(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds); //set format
        else
            return String.format("%02d:%02d", minutes, seconds);    //set format
    }

    @Command(name = "play", description = "commands.play.description")
    private void playCommand(Guild guild, TextChannel textChannel, User user, String command, String[] args) {
        String translation = "";
        try {
            translation = translationManager.getTranslationByKey("commands.play.error.permission");
            if (guild.getMember(user).getVoiceState() == null) {
                LOGGER.fatal("No voice state for user", new MusicVoiceStateException(guild.getMember(user)));
                return;
            }
            if (guild.getMember(user).getVoiceState().getChannel() == null) {
                textChannel.sendMessage(translationManager.getTranslationByKey("commands.play.error.voice")).queue();
            }
            if (args.length == 0) {
                textChannel.sendMessage(translationManager.getTranslationByKey("commands.play.error.noArgs")).queue();
            }

            VoiceChannel voiceChannel = guild.getMember(user).getVoiceState().getChannel();
            AudioHandler mng = manager.getPlayer(guild).getAudioHandler();
            if (!guild.getAudioManager().isConnected()) {
                guild.getAudioManager().setSendingHandler(mng);
                guild.getAudioManager().openAudioConnection(voiceChannel);
            }
            manager.loadTrack(textChannel, command.replaceFirst("play ", ""));
        } catch (PermissionException ex) {
            if (ex.getPermission() == Permission.VOICE_CONNECT) {
                textChannel.sendMessage(translation).queue();
            }
        } catch (TranslationKeyNotFoundException ex) {
            textChannel.sendMessage(translationErrorMessage).queue();
            LOGGER.fatal("Play command", ex);
        } catch (Exception e) {
            LOGGER.error("Play command", e);
        }
    }

    @Command(name = "skip", description = "commands.skip.description")
    private void skip(Guild guild, TextChannel textChannel) {
        try {
            if (!guild.getAudioManager().isConnected()) {
                textChannel.sendMessage(translationManager.getTranslationByKey("commands.skip.error.noMusic")).queue();
                return;
            }
            manager.getPlayer(guild).skipTrack();
            textChannel.sendMessage(translationManager.getTranslationByKey("commands.skip.nextMusic")).queue();
        } catch (TranslationKeyNotFoundException e) {
            textChannel.sendMessage(translationErrorMessage).queue();
            LOGGER.fatal("Skip command", e);
        }
    }

    @Command(name = "disconnect", description = "commands.disconnect.description")
    private void stop(Guild guild, TextChannel textChannel) {
        try {
            if (guild.getAudioManager().isConnected()) {
                manager.getPlayer(guild).getAudioPlayer().stopTrack();
                manager.getPlayer(guild).getListener().getTracks().clear();
                guild.getAudioManager().closeAudioConnection();
                textChannel.sendMessage(translationManager.getTranslationByKey("commands.disconnect.message")).queue();
            } else {
                textChannel.sendMessage(translationManager.getTranslationByKey("commands.disconnect.error.alreadyDisconnect")).queue();
            }
        } catch (TranslationKeyNotFoundException e) {
            textChannel.sendMessage(translationErrorMessage).queue();
            LOGGER.fatal("disconnect command", e);
        }
    }

    @Command(name = "volume", description = "commands.volume.description")
    private void volume(Guild guild, TextChannel textChannel, String[] command) {
        try {
            AudioPlayer player = manager.getPlayer(guild).getAudioPlayer();

            if (command.length == 0) {
                textChannel.sendMessage(String.format(translationManager.getTranslationByKey("commands.volume.actualVolume"), player.getVolume())).queue();
            } else {
                try {
                    int newVolume = Math.max(MIN_VOLUME, Math.min(MAX_VOLUME, Integer.parseInt(command[0])));
                    int oldVolume = manager.getPlayer(guild).getAudioPlayer().getVolume();
                    manager.getPlayer(guild).getAudioPlayer().setVolume(newVolume);
                    textChannel.sendMessage(String.format(translationManager.getTranslationByKey("commands.volume.set"), oldVolume, newVolume)).queue();
                } catch (NumberFormatException e) {
                    textChannel.sendMessage(
                            String.format(
                                    translationManager.getTranslationByKey("commands.volume.error.invalidValue"),
                                    command[0], MIN_VOLUME, MAX_VOLUME)
                    ).queue();
                }
            }
        } catch (TranslationKeyNotFoundException e) {
            textChannel.sendMessage(translationErrorMessage).queue();
            LOGGER.fatal("volume command", e);
        }
    }

    @Command(name = "clear", description = "commands.clear.description")
    private void clear(TextChannel textChannel) {
        try {
            MusicPlayer player = manager.getPlayer(textChannel.getGuild());

            if (player.getListener().getTracks().isEmpty()) {
                textChannel.sendMessage(translationManager.getTranslationByKey("commands.clear.error.empty")).queue();
                return;
            }
            player.getListener().getTracks().clear();
            textChannel.sendMessage(translationManager.getTranslationByKey("commands.clear.success")).queue();
        } catch (TranslationKeyNotFoundException e) {
            textChannel.sendMessage(translationErrorMessage).queue();
            LOGGER.fatal("clear command", e);
        }

    }

    @Command(name = "np", description = "commands.np.description")
    private void nowPlaying(TextChannel textChannel, Guild guild) {
        try {
            AudioTrack currentTrack = manager.getPlayer(guild).getAudioPlayer().getPlayingTrack();
            if (currentTrack != null) {
                String guiNow = GuiTime(currentTrack.getDuration(), currentTrack.getPosition());
                String position = getTimestamp(currentTrack.getPosition());
                String duration = getTimestamp(currentTrack.getDuration());
                String nowPlayingTime = String.format("\n` %s / %s `", position, duration);

                EmbedBuilder builder = new EmbedBuilder();

                builder.setTitle(translationManager.getTranslationByKey("commands.np.title"));
                builder.setColor(Color.GREEN);
                String uri = thumbnailYoutube(currentTrack.getInfo().uri);
                builder.setThumbnail(uri);
                builder.addField(translationManager.getTranslationByKey("commands.np.music.title"), "[" + currentTrack.getInfo().title + "](" + currentTrack.getInfo().uri + ")", true);
                builder.addField(translationManager.getTranslationByKey("commands.np.time.title"), "`" + guiNow + "`" + nowPlayingTime, true);

                textChannel.sendMessageEmbeds(builder.build()).queue();
            } else
                textChannel.sendMessage(translationManager.getTranslationByKey("commands.np.error.noMusic")).queue();
        } catch (TranslationKeyNotFoundException e) {
            textChannel.sendMessage(translationErrorMessage).queue();
            LOGGER.fatal("nowPlaying command", e);
        }


    }

    private String thumbnailYoutube(String url) {
        String[] urlParam = url.split("v="); //split url on setting url
        String[] param = urlParam[1].split("&");
        String idVideo = param[0];


        return "https://img.youtube.com/vi/" + idVideo + "/0.jpg";
    }

    private String GuiTime(long timeSong, long timeNow) {
        int delay = Math.toIntExact(timeNow * 30 / timeSong); //calcule proportinal delay.
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            String c = "▬";
            if (delay == i) {
                c = "\uD83D\uDD18";
            }
            sb.append(c);
        }
        return String.valueOf(sb);
    }


}
