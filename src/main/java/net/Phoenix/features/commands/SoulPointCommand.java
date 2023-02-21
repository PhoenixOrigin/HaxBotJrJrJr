package net.Phoenix.features.commands;

import net.Phoenix.api.AthenaAPI;
import net.Phoenix.api.objects.AthenaServerList;
import net.Phoenix.handlers.ConfigHandler;
import net.Phoenix.utilities.TableBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SoulPointCommand {

    public static void handleEvent(SlashCommandInteractionEvent event) {
        // Creating a map that keeps order
        LinkedHashMap<AthenaServerList.Server, Long> serverSoulPoints = new LinkedHashMap<>();
        // Querying wynntils athena api to get online servers
        AthenaServerList serverList = AthenaAPI.getAvailableServers();
        // Looping through and adding soul point times to map
        for (AthenaServerList.Server server : serverList.getServers()) {
            // Remove it if you don't want YT server to be excluded
            if (Objects.equals(server.getServer(), "YT")) {
                continue;
            }
            // If offset parameter there, use it else no offset
            int offset = event.getInteraction().getOption("offset") == null ? 0 : event.getInteraction().getOption("offset").getAsInt();
            // Put soul point timers
            serverSoulPoints.put(server, 20 - (server.getUptimeMinutes() + offset) % 20);
        }

        // Get server count wanted default 10
        int count = event.getInteraction().getOption("count") == null ? 10 : event.getInteraction().getOption("count").getAsInt();

        // Some complicatedish sorting + limiting to number
        serverSoulPoints =
                serverSoulPoints.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .limit(count)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));

        // Making embed builder
        EmbedBuilder builder = new EmbedBuilder();
        // Setting author tooo, you!
        builder.setFooter(ConfigHandler.getConfigString("author"));
        // Black embed bitch
        builder.setColor(Color.BLACK);
        // Setting title
        builder.setTitle(String.format("Top %s soul point servers", count));
        // Initialising stringBuilder
        StringBuilder sb = new StringBuilder("```\n");

        TableBuilder table = new TableBuilder()
                         .addHeaders("World", "Timer")
                         .setName("Soul Point Regen Timers")
                        .setBorders(TableBuilder.Borders.HEADER_FRAME)
                         .frame(true);
        Map<String, String> rows = new LinkedHashMap<>();


        for (Map.Entry<AthenaServerList.Server, Long> e : serverSoulPoints.entrySet()) {
            rows.put(e.getKey().getServer(), e.getValue().toString());
        }

        table.setValues(rows.entrySet()
                .stream()
                .map(e -> new String[]{e.getKey(),e.getValue()})
                .toArray(String[][]::new));
        sb.append(table.build());
        // Closing block
        sb.append("```");
        // Adding servers
        builder.setDescription(sb.toString());
        // Updating message
        event.getHook().editOriginalEmbeds(builder.build()).queue();
    }

}
