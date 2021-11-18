package fr.swansky.radioRythm.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.swansky.discordCommandIOC.Commands.annotations.Command;
import fr.swansky.discordCommandIOC.Commands.annotations.CommandsContainer;
import fr.swansky.radioRythm.music.AudioHandler;
import fr.swansky.radioRythm.music.MusicManager;
import fr.swansky.radioRythm.music.MusicPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.awt.*;

@CommandsContainer
public class MusicCommands {
    private final MusicManager manager = new MusicManager();

    private static String getTimestamp(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds); //set format
        else
            return String.format("%02d:%02d", minutes, seconds);    //set format
    }

    @Command(name = "play", description = "Play fr.swansky.radioRythm.music with bot ")
    private void playCommand(Guild guild, TextChannel textChannel, User user, String command, String[] args) {
        VoiceChannel voiceChannel = guild.getMember(user).getVoiceState().getChannel();
        try {
            if (args.length == 0)
                throw new Exception("Vous n'avez pas precisé de lien de musique ou de nom de fichier mp3");

            if (voiceChannel == null) throw new Exception("Vous devez être connecté a un channel vocal");

            AudioHandler mng = manager.getPlayer(guild).getAudioHandler();

            if (!guild.getAudioManager().isConnected()) {
                guild.getAudioManager().setSendingHandler(mng);
                guild.getAudioManager().openAudioConnection(voiceChannel);
            }


            manager.loadTrack(textChannel, command.replaceFirst("play ", ""));
        } catch (PermissionException ex) {
            if (ex.getPermission() == Permission.VOICE_CONNECT) {
                textChannel.sendMessage("Le bot n'a pas la permission de rejoindre ce channel." + voiceChannel.getName()).queue();
            }
        } catch (Exception ex) {
            textChannel.sendMessage(ex.getMessage()).queue();
        }
    }

    @Command(name = "skip", description = "Skip a fr.swansky.radioRythm.music")
    private void skip(Guild guild, TextChannel textChannel) {
        if (!guild.getAudioManager().isConnected()) {
            textChannel.sendMessage("Le player n'as pas de piste en cours.").queue();
            return;
        }
        manager.getPlayer(guild).skipTrack();


        textChannel.sendMessage("La lecture est passé à la piste suivante. :track_next: ").queue();
    }

    @Command(name = "disconnect", description = "disconnect bot fr.swansky.radioRythm.music")
    private void stop(Guild guild, TextChannel textChannel) {
        if (guild.getAudioManager().isConnected()) {
            manager.getPlayer(guild).getAudioPlayer().stopTrack();
            manager.getPlayer(guild).getListener().getTracks().clear();
            guild.getAudioManager().closeAudioConnection();
            textChannel.sendMessage("Le bot a été déconnecté.").queue();
        } else {
            textChannel.sendMessage("Le bot n'est pas connecté.").queue();
        }
    }

    @Command(name = "volume", description = "Increase or decrease Volume of fr.swansky.radioRythm.music")
    private void volume(Guild guild, TextChannel textChannel, String[] command) {
        AudioPlayer player = manager.getPlayer(guild).getAudioPlayer();

        if (command.length == 0) {
            textChannel.sendMessage("Volume actuelle: **" + player.getVolume() + "**:speaker:").queue();
        } else {
            try {
                int newVolume = Math.max(10, Math.min(100, Integer.parseInt(command[0]))); //reset newVolume
                int oldVolume = manager.getPlayer(guild).getAudioPlayer().getVolume();
                manager.getPlayer(guild).getAudioPlayer().setVolume(newVolume);  //set new volume
                textChannel.sendMessage("Changement du volume de `" + oldVolume + "` a `" + newVolume + "` :speaker:").queue();
            } catch (NumberFormatException e) {
                textChannel.sendMessage("`" + command[0] + "` ce n'ai pas une valeur valide. **(10 - 100)**").queue();
            }
        }
    }

    @Command(name = "clear", description = "clear all queue")
    private void clear(TextChannel textChannel) {
        MusicPlayer player = manager.getPlayer(textChannel.getGuild());

        if (player.getListener().getTracks().isEmpty()) {
            textChannel.sendMessage("Il n'y a pas de piste dans la liste d'attente.").queue();
            return;
        }
        player.getListener().getTracks().clear();
        textChannel.sendMessage("La liste d'attente à été vidé.").queue();
    }

    @Command(name = "np", description = "now playing")
    private void nowplaying(TextChannel textChannel, Guild guild) {
        AudioTrack currentTrack = manager.getPlayer(guild).getAudioPlayer().getPlayingTrack();

        if (currentTrack != null) {
            String guiNow = GuiTime(currentTrack.getDuration(), currentTrack.getPosition());
            String position = getTimestamp(currentTrack.getPosition());
            String duration = getTimestamp(currentTrack.getDuration());
            String nowplayingTime = String.format("\n` %s / %s `", position, duration);

            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle("**Now Playing**");
            builder.setColor(Color.BLUE);
            if (!currentTrack.getIdentifier().endsWith(".mp3")) {
                String uri = thumbnailYoutube(currentTrack.getInfo().uri);
                builder.setThumbnail(uri);
                builder.addField("**Musique: **", "[" + currentTrack.getInfo().title + "](" + currentTrack.getInfo().uri + ")", true);
            } else {
                builder.addField("**Musique: **", "[" + currentTrack.getIdentifier().replace("music/", "") + "](" + currentTrack.getInfo().uri + ")", true);
            }
            builder.addField("**Temps: **", "now time: `" + guiNow + "`" + nowplayingTime, true);

            textChannel.sendMessageEmbeds(builder.build()).queue();
        } else
            textChannel.sendMessage("**Aucune musique est en lecture!**  ").queue();
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
