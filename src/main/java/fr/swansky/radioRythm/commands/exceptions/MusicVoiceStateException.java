package fr.swansky.radioRythm.commands.exceptions;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class MusicVoiceStateException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public MusicVoiceStateException(Member member) {
        super("No voice state for member: "+member.getId()+" possible because net.dv8tion.jda.api.utils.cache.CacheFlag.VOICE_STATE is disabled");
    }
}
