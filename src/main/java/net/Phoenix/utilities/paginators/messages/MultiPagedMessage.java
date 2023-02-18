package net.Phoenix.utilities.paginators.messages;

import net.Phoenix.Main;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

public class MultiPagedMessage {

    public List<MessageEmbed> embeds;
    final public MessageChannel channel;
    public int page;
    final public Emoji forwards;
    final public Emoji backwards;
    final public Emoji start;
    final public Emoji finish;
    final public Emoji end;
    public Message message;
    public boolean deleteOnFinish;


    public MultiPagedMessage(List<MessageEmbed> embeds,
                           MessageChannel channel,
                           Emoji start,
                           Emoji backwards,
                           Emoji finish,
                           Emoji forwards,
                           Emoji end,
                           boolean deleteOnFinish
    ) {
        this.embeds = embeds;
        this.channel = channel;
        this.start = start;
        this.forwards = forwards;
        this.backwards = backwards;
        this.end = end;
        this.finish = finish;
        this.deleteOnFinish = deleteOnFinish;
        this.page = 0;
    }

    public MultiPagedMessage send() {
        message = channel.sendMessageEmbeds(embeds.get(0))
                .addActionRow(
                        Button.primary("start", start.getName()),
                        Button.primary("previous", backwards.getName()),
                        Button.success("finish", finish.getName()),
                        Button.primary("next", forwards.getName()),
                        Button.primary("end", end.getName())
                )
                .complete();
        Main.handler.messages.put(message.getIdLong(), this);
        return this;
    }

    public void nextPage() {
        if (page == embeds.size()) return;

        page += 1;

        message.editMessageEmbeds(embeds.get(page)).queue();
    }

    public void previousPage() {
        if (page == 0) return;

        page -= 1;

        message.editMessageEmbeds(embeds.get(page)).queue();
    }

    public void startPage() {
        page = 0;

        message.editMessageEmbeds(embeds.get(page)).queue();
    }

    public void endPage() {
        page = embeds.size() - 1;

        message.editMessageEmbeds(embeds.get(page)).queue();
    }

    public void movePage(Button button) {
        String name = button.getLabel();
        if (name.contains(backwards.getName())) {
            previousPage();
        } else if (name.contains(forwards.getName())) {
            nextPage();
        } else if (name.contains(start.getName())) {
            startPage();
        } else if (name.contains(end.getName())) {
            endPage();
        } else if (name.contains(end.getName())) {
            if (deleteOnFinish) {
                message.delete().queue();
            } else {
                Main.handler.messages.remove(message.getIdLong());
                message.editMessageEmbeds(embeds.get(page)).setComponents().queue();
            }
        }
    }

    public MultiPagedMessage deleteOnFinish(boolean yes) {
        this.deleteOnFinish = yes;
        return this;
    }


}

