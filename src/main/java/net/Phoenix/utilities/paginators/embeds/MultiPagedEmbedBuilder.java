package net.Phoenix.utilities.paginators.embeds;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.ArrayList;
import java.util.List;

public class MultiPagedEmbedBuilder {

    public MessageChannel messageChannel;
    public Emoji forwards = Emoji.fromUnicode("▶");
    public Emoji backwards = Emoji.fromUnicode("◀");
    public Emoji start = Emoji.fromUnicode("⏪");
    public Emoji finish = Emoji.fromUnicode("❌");
    public Emoji end = Emoji.fromUnicode("⏩");
    public List<MessageEmbed> embeds = new ArrayList<>();
    public boolean deleteOnFinish = false;


    public MultiPagedEmbedBuilder loadEmbeds(List<MessageEmbed> embed) {
        embeds = embed;
        return this;
    }

    public MultiPagedEmbedBuilder splitEmbed(MessageEmbed embed) {
        embeds = new ArrayList<>();

        int totalLength = embed.getLength();
        int maxTotalLength = 6000;

        if (totalLength > maxTotalLength) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(embed.getTitle())
                    .setDescription(embed.getDescription())
                    .setColor(embed.getColor())
                    .setFooter(embed.getFooter().getText(), embed.getFooter().getIconUrl());

            int chunkLength = embed.getTitle().length() + embed.getDescription().length();
            for (MessageEmbed.Field field : embed.getFields()) {
                String name = field.getName();
                String value = field.getValue();
                boolean inline = field.isInline();
                int fieldLength = name.length() + value.length();
                if (chunkLength + fieldLength > maxTotalLength) {
                    embeds.add(builder.build());
                    builder.clear()
                            .setColor(embed.getColor())
                            .setFooter(embed.getFooter().getText(), embed.getFooter().getIconUrl());

                    chunkLength = 0;
                }
                builder.addField(name, value, inline);
                chunkLength += fieldLength;
            }
            embeds.add(builder.build());
        } else {
            embeds.add(embed);
        }

        return this;
    }

    public MultiPagedEmbedBuilder setChannel(MessageChannel channel) {
        this.messageChannel = channel;
        return this;
    }

    public MultiPagedEmbedBuilder setForwardsEmoji(Emoji forwards) {
        this.forwards = forwards;
        return this;
    }

    public MultiPagedEmbedBuilder setBackwardsBuilder(Emoji backwards) {
        this.backwards = backwards;
        return this;
    }

    public MultiPagedEmbedBuilder setStartEmoji(Emoji start) {
        this.start = start;
        return this;
    }

    public MultiPagedEmbedBuilder setFinishEmoji(Emoji finish) {
        this.finish = finish;
        return this;
    }

    public MultiPagedEmbedBuilder setEndEmoji(Emoji end) {
        this.end = end;
        return this;
    }

    public MultiPagedEmbedBuilder deleteOnFinish(boolean deleteOnFinish) {
        this.deleteOnFinish = deleteOnFinish;
        return this;
    }


    public MultiPagedEmbed create() {
        return new MultiPagedEmbed(
                embeds,
                messageChannel,
                start,
                backwards,
                finish,
                forwards,
                end,
                deleteOnFinish
        );
    }

    public MultiPagedEmbed createAndSend() {
        return new MultiPagedEmbed(
                embeds,
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

