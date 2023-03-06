package net.Phoenix.features.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.Phoenix.Main;
import net.Phoenix.utilities.TableBuilder;
import net.Phoenix.utilities.Utilities;
import net.Phoenix.utilities.annotations.BridgeCommand;
import net.Phoenix.utilities.paginators.embeds.MultiPagedEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@BridgeCommand(name = "activity",
        description = "Get the (in)activity of a guild",
        options = {
                @BridgeCommand.CommandOption(type = OptionType.STRING,
                        name = "guild",
                        description = "The guild to extract activity for :D"),
                @BridgeCommand.CommandOption(type = OptionType.INTEGER,
                        name = "time",
                        description = "The time the player needs less than")
        }
)
public class Activity {


    @BridgeCommand.invoke
    public static void handleCommand(SlashCommandInteractionEvent event,
                                     @BridgeCommand.OptionValue(name = "guild") String guildName,
                                     @BridgeCommand.OptionValue(name = "time") Integer time) {
        if (guildName == null) {
            guildName = "HackForums";
        }
        if (time == null) {
            time = 180;
        }

        HashMap<UUID, String> uuids = getGuildUUIDS(guildName);
        /*
        SELECT uuid, SUM(playtime) FROM playtime
WHERE timestamp BETWEEN
        date_trunc('week', NOW() - INTERVAL '1 week') + INTERVAL '1 day'
    AND date_trunc('week', NOW()) + INTERVAL '1 day'
  AND uuid = ANY(?)
GROUP BY uuid;

TO BE RELEASED ON WEDNESDAY (enough data collected)
         */
        String sqlQuery = """
                WITH week_list AS (
                        SELECT generate_series(1, 4) AS week_number
                ), weekly_playtime AS (
                        SELECT
                        uuid,
                        week_number,
                        SUM(playtime) AS total_playtime
                        FROM
                        playtime
                            INNER JOIN week_list ON timestamp BETWEEN date_trunc('week', NOW() - INTERVAL '1 week' * week_number) + INTERVAL '1 day'
                                 AND date_trunc('week', NOW() - INTERVAL '1 week' * (week_number - 1)) + INTERVAL '1 day'
                        GROUP BY
                            uuid,
                        week_number
                )
                SELECT
                    uuid
                FROM
                    weekly_playtime
                WHERE
                    week_number <= (SELECT max(week_number) FROM week_list)
                    AND total_playtime < 120
                    AND uuid = ANY(?);
                """;

        LinkedHashMap<UUID, Integer> map = new LinkedHashMap<>();

        try {
            PreparedStatement statement = Main.database.prepareStatement(sqlQuery);
            statement.setArray(1, Main.database.createArrayOf("uuid", uuids.keySet().toArray()));
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                UUID uuid = set.getObject(1, UUID.class);
                int intValue = set.getInt(2);
                map.put(uuid, intValue);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (UUID uuid : uuids.keySet()) {
            if (!map.containsKey(uuid)) {
                map.put(uuid, 0);
            }
        }
        HashMap<UUID, Integer> readOnly = (HashMap<UUID, Integer>) map.clone();
        for (Map.Entry<UUID, Integer> entry : readOnly.entrySet()) {
            if (entry.getValue() >= time) {
                map.remove(entry.getKey());
            }
        }

        map = map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        MultiPagedEmbedBuilder builder = new MultiPagedEmbedBuilder()
                .setChannel(event.getMessageChannel())
                .deleteOnFinish(false);

        for (Map<UUID, Integer> hashMap : splitMap(map, 10)) {
            TableBuilder table = new TableBuilder()
                    .addHeaders("Username", "Playtime")
                    .setName(guildName + " activity")
                    .setBorders(TableBuilder.Borders.HEADER_FRAME)
                    .frame(true);
            Map<String, String> rows = new LinkedHashMap<>();

            for (Map.Entry<UUID, Integer> entry : hashMap.entrySet()) {
                rows.put(uuids.get(entry.getKey()), LocalTime.MIN.plus(Duration.ofMinutes(entry.getValue())).toString().replace(":", "h") + "m");
            }

            table.setValues(rows.entrySet()
                    .stream()
                    .map(e -> new String[]{e.getKey(), e.getValue()})
                    .toArray(String[][]::new));

            builder.addEmbed(new EmbedBuilder()
                    .setDescription("```\n" + table.build() + "```")
                    .setTitle(guildName)
                    .setColor(Color.black)
                    .build());
        }

        builder.create().send();

    }

    private static HashMap<UUID, String> getGuildUUIDS(String guildName) {
        HashMap<UUID, String> uuids = new HashMap<>();
        JsonObject guild = null;
        try {
            guild = JsonParser.parseString(Utilities.queryAPI("https://api.wynncraft.com/public_api.php?action=guildStats&command=" + guildName)).getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (JsonElement player : guild.getAsJsonArray("members")) {
            uuids.put(UUID.fromString(player.getAsJsonObject().get("uuid").getAsString()), player.getAsJsonObject().get("name").getAsString());
        }

        return uuids;
    }

    private static <K, V> List<Map<K, V>> splitMap(Map<K, V> map, int size) {
        List<Map<K, V>> result = new ArrayList<>();
        int i = 0;
        Map<K, V> currentMap = new HashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            currentMap.put(entry.getKey(), entry.getValue());
            i++;
            if (i % size == 0) {
                result.add(currentMap);
                currentMap = new HashMap<>();
            }
        }
        if (!currentMap.isEmpty()) {
            result.add(currentMap);
        }
        return result;
    }


}
