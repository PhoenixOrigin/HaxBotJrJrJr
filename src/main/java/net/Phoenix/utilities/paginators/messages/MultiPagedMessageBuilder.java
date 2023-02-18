package net.Phoenix.utilities.paginators.messages;

import com.google.common.base.Splitter;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class MultiPagedMessageBuilder {

    public MessageChannel messageChannel;
    public Emoji forwards = Emoji.fromUnicode("▶");
    public Emoji backwards = Emoji.fromUnicode("◀");
    public Emoji start = Emoji.fromUnicode("⏪");
    public Emoji finish = Emoji.fromUnicode("❌");
    public Emoji end = Emoji.fromUnicode("⏩");
    public List<String> messages = new ArrayList<>();
    public boolean deleteOnFinish = false;


    public MultiPagedMessageBuilder loadStrings(List<String> embed) {
        messages = embed;
        return this;
    }

    public MultiPagedMessageBuilder splitMessage(String message) {
        messages = StreamSupport
                .stream(Splitter.fixedLength(2000).split(message).spliterator(), false)
                .toList();
        return this;
    }

    public MultiPagedMessageBuilder setChannel(MessageChannel channel) {
        this.messageChannel = channel;
        return this;
    }

    public MultiPagedMessageBuilder setForwardsEmoji(Emoji forwards) {
        this.forwards = forwards;
        return this;
    }

    public MultiPagedMessageBuilder setBackwardsBuilder(Emoji backwards) {
        this.backwards = backwards;
        return this;
    }

    public MultiPagedMessageBuilder setStartEmoji(Emoji start) {
        this.start = start;
        return this;
    }

    public MultiPagedMessageBuilder setFinishEmoji(Emoji finish) {
        this.finish = finish;
        return this;
    }

    public MultiPagedMessageBuilder setEndEmoji(Emoji end) {
        this.end = end;
        return this;
    }

    public MultiPagedMessageBuilder deleteOnFinish(boolean deleteOnFinish) {
        this.deleteOnFinish = deleteOnFinish;
        return this;
    }


    public MultiPagedMessage create() {
        return new MultiPagedMessage(
                messages,
                messageChannel,
                start,
                backwards,
                finish,
                forwards,
                end,
                deleteOnFinish
        );
    }

    public MultiPagedMessage createAndSend() {
        return new MultiPagedMessage(
                messages,
                messageChannel,
                start,
                backwards,
                finish,
                forwards,
                end,
                deleteOnFinish
        ).send();
    }
}

