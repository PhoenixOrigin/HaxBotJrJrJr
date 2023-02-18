package net.Phoenix.utilities.paginators.messages;

import net.Phoenix.Main;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

public class MultiPagedMessage {

    public List<String> messages;
    final public MessageChannel channel;
    public int page;
    final public Emoji forwards;
    final public Emoji backwards;
    final public Emoji start;
    final public Emoji finish;
    final public Emoji end;
    public long message;
    public boolean deleteOnFinish;


    public MultiPagedMessage(List<String> messages,
                             MessageChannel channel,
                             Emoji start,
                             Emoji backwards,
                             Emoji finish,
                             Emoji forwards,
                             Emoji end,
                             boolean deleteOnFinish
    ) {
        this.messages = messages;
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
        message = channel.sendMessage(messages.get(0))
                .addActionRow(
                        Button.primary("start", start.getName()),
                        Button.primary("previous", backwards.getName()),
                        Button.success("finish", finish.getName()),
                        Button.primary("next", forwards.getName()),
                        Button.primary("end", end.getName())
                )
                .complete().getIdLong();
        Main.multiPagedMessageHandler.messages.put(message, this);
        return this;
    }

    public void nextPage() {
        if (page == messages.size()) return;

        page += 1;

        channel.editMessageById(message, messages.get(page)).queue();
    }

    public void previousPage() {
        if (page == 0) return;

        page -= 1;

        channel.editMessageById(message, messages.get(page)).queue();
    }

    public void startPage() {
        page = 0;

        channel.editMessageById(message, messages.get(page)).queue();
    }

    public void endPage() {
        page = messages.size() - 1;

        channel.editMessageById(message, messages.get(page)).queue();
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
                channel.deleteMessageById(message).queue();
            } else {
                Main.multiPagedMessageHandler.messages.remove(message);
                channel.editMessageById(message, messages.get(page)).setComponents().queue();
            }
        }
    }


}

