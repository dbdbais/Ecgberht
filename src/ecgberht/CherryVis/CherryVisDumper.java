package ecgberht.CherryVis;

import com.github.luben.zstd.Zstd;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ecgberht.ConfigManager;
import ecgberht.GameState;
import ecgberht.Util.Util;
import org.openbw.bwapi4j.unit.Unit;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CherryVisDumper {
    private GameState gameState;
    private TraceData traceData;
    private final String dir = "bwapi-data/write/cherryvis";
    private Writing writing = new Writing();

    public CherryVisDumper(GameState gameState) {
        this.gameState = gameState;
        traceData = new TraceData();
    }



    public void onFrame() {
        String frame = String.valueOf(gameState.frameCount);
        fillBoard(frame);
    }

    public void onUnitShow(Unit u) {
        String frame = String.valueOf(gameState.getIH().getFrameCount());
        UnitSeenInfo unitSeenInfo = new UnitSeenInfo(u);
        traceData.units_first_seen.computeIfAbsent(frame, s -> new ArrayList<>()).add(unitSeenInfo);
    }


    private String getDumpDirectory(String opponentName) {
        int i = 1;
        while (i <= 1000) {
            String path = dir + "/" + gameState.getPlayer().getName() + "_vs_" + opponentName + "_" + i + ".rep.cvis/";
            File directory = new File(path);
            if (directory.exists()) {
                i++;
                continue;
            }
            boolean created = directory.mkdir();
            if (created) return path;
        }
        return null;
    }

    public void onEnd(boolean win, String startStrat) {
        if (!ConfigManager.getConfig().ecgConfig.enableCherryVisDump) return;
        File directory = new File(dir);
        boolean created = directory.exists();
        if (!created) created = directory.mkdir();
        if (!created) {
            System.err.println("Couldnt create CherryVis dump folder");
            return;
        }
        String opponentName = gameState.getIH().enemy().getName();
        String path = getDumpDirectory(opponentName);
        if (path != null) {
            Util.sendText("Writing traceData to: " + path);
            writing.setWriteStrategy(new WriteJsonCompressedStrategy());
            writing.write(traceData, path + "trace.json");
        }
    }

    private void fillBoard(String frame) {
        HashMap<String, String> board = traceData.board_updates.computeIfAbsent(frame, s -> new HashMap<>());
        board.put("chosenUpgrade", getStringObject(gameState.chosenUpgrade));
        board.put("chosenResearch", getStringObject(gameState.chosenResearch));
        board.put("chosenTrainingFacility", getStringUnitTypeUnit(gameState.chosenTrainingFacility));
        board.put("explore", getStringObject(gameState.explore));
        board.put("firstExpand", getStringObject(gameState.firstExpand));
        board.put("maxFirebats", getStringObject(gameState.maxBats));
        board.put("maxGoliaths", getStringObject(gameState.maxGoliaths));
        board.put("maxVessels", getStringObject(gameState.maxVessels));
        board.put("maxWraiths", getStringObject(gameState.maxWraiths));
        board.put("iReallyWantToExpand", getStringObject(gameState.iReallyWantToExpand));
        board.put("firstScout", getStringObject(gameState.firstScout));
        board.put("chosenToBuild", getStringObject(gameState.chosenToBuild));
        board.put("deltaCash", getStringObject(gameState.deltaCash));
        board.put("proxyBuilding", getStringUnitTypeUnit(gameState.proxyBuilding));
        board.put("islandExpand", getStringObject(gameState.islandExpand));
        board.put("APM", getStringObject(gameState.getIH().getFrameCount()));
        board.put("islandBases", getStringObject(gameState.islandBases.size()));
        // TODO more board variables
    }

    private String getStringObject(Object obj) {
        return Objects.toString(obj, "None");
    }

    private String getStringUnitTypeUnit(Unit unit) {
        return unit != null ? unit.getType().toString() : "None";
    }


}
