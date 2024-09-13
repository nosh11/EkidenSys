package com.github.nosh11.ekidensys.api;

import com.github.nosh11.ekidensys.EkidenSys;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.logging.Level;

public class ApiManager {
    private static final ApiManager instance = new ApiManager();
    public static ApiManager getInstance() {
        return instance;
    }
    private final Map<Integer, ApiMember> members = new HashMap<>();
    private final Map<Integer, ApiTeam> teams = new HashMap<>();

    public ApiTeam getTeam(int team_id) {
        return this.teams.get(team_id);
    }
    public ApiMember getMember(int member_id) {
        return this.members.get(member_id);
    }

    public Collection<ApiTeam> getTeams() {
        return teams.values();
    }
    private static JSONObject sendRequest() {
        try (HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build()){
            EkidenSys.getInstance().getLogger().info("ooo");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://ekiden2024.event.techful-programming.com/api/ranking"))
                    .timeout(Duration.ofSeconds(5))
                    .build();
            EkidenSys.getInstance().getLogger().info("eee");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            EkidenSys.getInstance().getLogger().info("iii");
            return new JSONObject(response.body());
        } catch (IOException | InterruptedException e) {
            EkidenSys.getInstance().getLogger().log(Level.WARNING, "APIの取得に失敗しました");
            return null;
        }
    }

    // 試合中 10s毎に呼び出すメソッドです。
    public void update() {
        JSONObject json = sendRequest();
        if (json == null) return;
        JSONArray teamsJson = json.getJSONArray("teams");

        // 現在の区間ID
        int session_id = EkidenSys.getInstance().getCurrentSessionId();

        for (int i = 0; i < teamsJson.length(); i++) {
            JSONObject teamJson = teamsJson.getJSONObject(i);
            int team_id = teamJson.getInt("id");
            ApiTeam team = teams.get(team_id);
            int member_id = team.memberIds.get(session_id);
            ApiMember member = members.get(member_id);
            ApiSession session = team.sessions.get(session_id);

            JSONObject membersJson = json.getJSONObject("members");
            JSONObject memberJson = membersJson.getJSONObject(String.valueOf(member_id));
            JSONArray sessionsJson = teamJson.getJSONArray("sessions");
            JSONObject sessionJson = sessionsJson.getJSONObject(session_id);

            int stamina = memberJson.getInt("stamina");
            int rank = teamJson.getInt("rank");
            int point = sessionJson.getInt("point");

            // 不正解時 -> スタミナが下がる
            if (member.stamina != stamina) {
                member.stamina = stamina;
                team.onFail();
            }

            // 順位の変動
            if (team.rank != rank) {
                team.rank = rank;
                team.onFail();
            }

            // 正解時 -> ポイントが増える
            if (session.point != point) {
                session.point = point;
                team.onSuccess();
            }
        }
    }



    public void reloadAll() {
        JSONObject jsonObject = sendRequest();
        if (jsonObject == null) return;

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

            ApiTeam team = new ApiTeam(id, name, rank, point, time, memberIds, sessions);
            teams.put(id, team);
        }
        EkidenSys.getInstance().getLogger().info(teams.size() + " teams loaded");

    }
}