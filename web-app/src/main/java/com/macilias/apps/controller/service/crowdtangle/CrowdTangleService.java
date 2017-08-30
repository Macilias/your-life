package com.macilias.apps.controller.service.crowdtangle;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.macilias.apps.model.Settings;
import com.macilias.apps.controller.service.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class CrowdTangleService implements Service {

    private final Client client;
    private final String listId;

    public CrowdTangleService() {
        this.client = new Client(Settings.CROWD_TANGLE_API_TOKEN);
        this.listId = Settings.CROWD_TANGLE_LIST_ID;
    }

    public CrowdTangleService(Client client, String listId) {
        this.client = client;
        this.listId = listId;
    }

    public int directContactCount(Optional<String> where, Optional<String> since) throws IOException {
        JsonObject json = client.get(new URL("https://api.crowdtangle.com/leaderboard?listId=" + listId));

        JsonObject result = json.getAsJsonObject("result");
        JsonArray accountStatistics = result.getAsJsonArray("accountStatistics");

        int followerCount = 0;

        for (JsonElement accountStatistic : accountStatistics) {
            JsonObject account = accountStatistic.getAsJsonObject().getAsJsonObject("account");

            followerCount += account.get("subscriberCount").getAsInt();
        }

        return followerCount;
    }
}
