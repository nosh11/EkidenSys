package com.github.nosh11.ekidensys.cameraman;

import com.github.nosh11.ekidensys.EkidenSys;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CameraManBoardManager {
    private static final CameraManBoardManager instance = new CameraManBoardManager();
    private final Map<UUID, FastBoard> boards = new HashMap<>();

    public static CameraManBoardManager getInstance() {
        return instance;
    }

    public void add(Player p) {
        FastBoard board = new FastBoard(p);
        board.updateTitle(MiniMessage.miniMessage().deserialize(
                "<gradient:#6666fe:#aaaabb:#8888ee>競プロ駅伝 2024"
        ));
        boards.put(p.getUniqueId(), board);
    }
    public void remove(Player p) {
        FastBoard board = boards.remove(p.getUniqueId());
        if (board != null)
            board.delete();
    }
    public void update() {
        for (FastBoard board : boards.values())
            board.updateLines(
                Component.text(""),
                Component.text("現在 " + (EkidenSys.getInstance().getCurrentSessionId()+1) + "区 出走中"),
                Component.text(""),
                Component.text("残り時間 " + EkidenSys.getInstance().getRemainingTimeAsString())
            );
    }

    public void hide() {
        for (FastBoard board : boards.values())
            board.updateLines(
                    Component.text("試合は現在行われていません")
            );
    }
}
