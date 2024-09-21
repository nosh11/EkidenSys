package com.github.nosh11.ekidensys.api;

import com.github.nosh11.ekidensys.EkidenSys;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class ApiManager {
    private static final ApiManager instance = new ApiManager();
    public static ApiManager getInstance() {
        return instance;
    }
    private final Map<Integer, ApiMember> members = new HashMap<>();
    private final Map<Integer, ApiTeam> teams = new HashMap<>();

    public ApiTeam getTeam(int team_id) {
        if (!this.teams.containsKey(team_id)) return null;
        return this.teams.get(team_id);
    }
    public ApiTeam getRandomTeam() {
        int size = this.teams.size();
        int team_id = this.teams.keySet().stream().toList().get(new Random().nextInt(size));
        return this.teams.get(team_id);
    }

    public ApiMember getMember(int member_id) {
        return this.members.get(member_id);
    }
    public Collection<ApiMember> getMembers() {
        return this.members.values();
    }

    public Collection<ApiTeam> getTeams() {
        return teams.values();
    }

    private static final HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(EkidenSys.getInstance().apiUrl))
            .build();
    public void update() {
        try (HttpClient client = HttpClient.newBuilder()
                .build()){
            CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            responseFuture.thenAccept(response -> update(new JSONObject(response.body())));
            responseFuture.exceptionally(e -> {
                System.out.println("エラーが発生しました: " + e.getMessage());
                return null;
            });
        }
    }

    // 試合中 10s毎に呼び出すメソッドです。
    public void update(JSONObject json) {
        if (json == null) return;

        // 現在の区間ID
        int session_id = EkidenSys.getInstance().getCurrentSessionId();

        JSONArray teamsJson = json.getJSONArray("teams");
        for (int i = 0; i < teamsJson.length(); i++) {
            JSONObject teamJson = teamsJson.getJSONObject(i);
            int team_id = teamJson.getInt("id");
            ApiTeam team = teams.get(team_id);
            ApiSession session = team.getCurrentSession();
            ApiMember member = team.getCurrentMember();
            int member_id = member.id;

            JSONObject membersJson = json.getJSONObject("members");
            JSONObject memberJson = membersJson.getJSONObject(String.valueOf(member_id));
            JSONArray sessionsJson = teamJson.getJSONArray("sessions");
            JSONObject sessionJson = sessionsJson.getJSONObject(session_id);

            int stamina = memberJson.getInt("stamina");
            int rank = teamJson.getInt("rank");
            int point = sessionJson.getInt("point");

            if (member.stamina != stamina || session.point != point) {
                JSONArray levelsJson = sessionJson.getJSONArray("levels");
                for (int k = 0; k < levelsJson.length(); k++) {
                    JSONObject levelJson = levelsJson.getJSONObject(k);
                    session.levels.get(k).time = levelJson.getInt("time");
                    session.levels.get(k).death = levelJson.getInt("death");
                    session.levels.get(k).prize = levelJson.getInt("prize");
                }
                if (member.stamina != stamina) {
                    member.stamina = stamina;
                    team.onFail();
                }
                if (session.point != point) {
                    int added_point = point - session.point;
                    session.point = point;
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            team.onSuccess(added_point);
                        }
                    }.runTaskLater(EkidenSys.getInstance(), 1L);
                }
            }
            // 順位の変動
            if (team.rank != rank) {
                team.onRankChanged(team.rank, rank);
                team.rank = rank;
            }
        }
    }

    private static JSONObject sendRequest() {
        try (HttpClient client = HttpClient.newBuilder()
                .build()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(EkidenSys.getInstance().apiUrl))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            File f = new File("plugins/EkidenSys/test.json");
            try (FileWriter filewriter = new FileWriter(f, StandardCharsets.UTF_8)){
                filewriter.write(response.body());
            } catch (IOException e) {
                EkidenSys.getInstance().getLogger().info("かきこみみすった");
            }

            return new JSONObject(response.body());
        } catch (IOException | InterruptedException e) {
            EkidenSys.getInstance().getLogger().log(Level.WARNING, "APIの取得に失敗しました");
            return null;
        }
    }

    public void reloadAll() {
        JSONObject jsonObject = sendRequest();
        if (jsonObject == null) return;
        members.clear();
        teams.clear();
        JSONObject membersJson = jsonObject.getJSONObject("members");
        for (String key : membersJson.keySet()) {
            int member_id = Integer.parseInt(key);
            JSONObject memberJson = membersJson.getJSONObject(key);
            members.put(member_id, new ApiMember(
                    member_id,
                    memberJson.getString("name"),
                    memberJson.getInt("stamina"),
                    memberJson.getInt("team_id")));
        }
        EkidenSys.getInstance().getLogger().info(members.size() + " members loaded");


        int max = 1000;
        JSONArray teamsJson = jsonObject.getJSONArray("teams");
        for (int i = 0; i < teamsJson.length(); i++) {
            JSONObject teamJson = teamsJson.getJSONObject(i);
            int id = teamJson.getInt("id");
            String name = teamJson.getString("name");
            int rank = teamJson.getInt("rank");
            int point = teamJson.getInt("point");
            int time = teamJson.getInt("time");

            // member_idsをパース
            List<Integer> memberIds = new ArrayList<>();
            JSONArray memberIdsJson = teamJson.getJSONArray("member_ids");
            for (int j = 0; j < memberIdsJson.length(); j++) {
                memberIds.add(memberIdsJson.getInt(j));
            }

            // sessionsをパース
            List<ApiSession> sessions = new ArrayList<>();
            JSONArray sessionsJson = teamJson.getJSONArray("sessions");
            for (int j = 0; j < sessionsJson.length(); j++) {
                JSONObject sessionJson = sessionsJson.getJSONObject(j);
                int memberId = sessionJson.getInt("member_id");
                int sessionPoint = sessionJson.getInt("point");
                int sessionTime = sessionJson.getInt("time");

                // levelsをパース
                List<ApiLevel> levels = new ArrayList<>();
                JSONArray levelsJson = sessionJson.getJSONArray("levels");
                for (int k = 0; k < levelsJson.length(); k++) {
                    JSONObject levelJson = levelsJson.getJSONObject(k);
                    int levelTime = levelJson.getInt("time");
                    int levelDeath = levelJson.getInt("death");
                    int levelPrize = levelJson.getInt("prize");
                    levels.add(new ApiLevel(levelTime, levelDeath, levelPrize));
                }

                int prizeCount = sessionJson.getInt("prize_count");
                sessions.add(new ApiSession(memberId, sessionPoint, sessionTime, levels, prizeCount));
            }

            if (max < id) max = id;
            ApiTeam team = new ApiTeam(id, name, rank, point, time, memberIds, sessions);
            teams.put(id, team);
        }

        for (ApiTeam team : teams.values()) {
            team.transition = (team.id - 1000f) / (max - 1000f);
        }

        EkidenSys.getInstance().getLogger().info(teams.size() + " teams loaded");

    }
}