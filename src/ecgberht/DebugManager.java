package ecgberht;

import bwem.Base;
import bwem.ChokePoint;
import bwem.Mineral;
import cameraModule.CameraModule;
import cameraModule.CameraModuleFactory;
import cameraModule.CameraModuleImpl;
import ecgberht.Agents.*;
import ecgberht.Util.ColorUtil;
import ecgberht.Util.MutablePair;
import ecgberht.Util.Util;
import org.openbw.bwapi4j.*;
import org.openbw.bwapi4j.type.Color;
import org.openbw.bwapi4j.type.Order;
import org.openbw.bwapi4j.type.Race;
import org.openbw.bwapi4j.type.UnitType;
import org.openbw.bwapi4j.unit.*;

import java.util.List;
import java.util.Map;

public class DebugManager {

    private MapDrawer mapDrawer;
    private InteractionHandler iH;
    private TilePosition startPos;
    protected BW game;
    private CameraModule skycladObserver;
    TextSetting _textsetting;
    DebugManager(MapDrawer mapDrawer, InteractionHandler iH, TilePosition startPos, BW game) {
        this.mapDrawer = mapDrawer;
        this.iH = iH;
        this.skycladObserver = CameraModuleFactory.createCameraModule(startPos, game);
    }

    public void keyboardInteraction(String text) {
        setInteractionText(text);
        _textsetting.doInteraction(skycladObserver);
    }
    
    public void setInteractionText(String text){
        switch(text){
            case "dt": _textsetting = new TextSetting_dt(); break;
            case "dc": _textsetting = new TextSetting_dc(); break;
            case "ds": _textsetting = new TextSetting_ds(); break;
            case "obs": _textsetting = new TextSetting_obs(); break;
            case "sounds": _textsetting = new TextSetting_sounds(); break;
            case "noattack": _textsetting = new TextSetting_noattack(); break;
        }
    }
    private void debugScreen(GameState gameState) {
        try {
            if (!ConfigManager.getConfig().ecgConfig.debugScreen) return;
            if (gameState.naturalArea != null) {
                print(gameState.naturalArea.getTop().toTilePosition(), Color.RED);
                for (ChokePoint c : gameState.naturalArea.getChokePoints()) {
                    if (c.getGeometry().size() > 2)
                        mapDrawer.drawLineMap(c.getGeometry().get(0).toPosition(), c.getGeometry().get(c.getGeometry().size() - 1).toPosition(), Color.GREY);
                }
            }
            for (ChokePoint c : gameState.bwem.getMap().getChokePoints()) {
                if (c.getGeometry().size() > 2)
                    mapDrawer.drawLineMap(c.getGeometry().get(0).toPosition(), c.getGeometry().get(c.getGeometry().size() - 1).toPosition(), Color.GREEN);
            }
        /*for(Entry<Base, MutablePair<MineralPatch, MineralPatch>> u : fortressSpecialBLs.entrySet()){
            if(u.getValue().first != null) bw.getMapDrawer().drawLineMap(u.getKey().getLocation().toPosition(), u.getValue().first.getPosition(),Color.RED);
            if(u.getValue().second != null)bw.getMapDrawer().drawLineMap(u.getKey().getLocation().toPosition(), u.getValue().second.getPosition(),Color.ORANGE);
        }*/
            for (MineralPatch m : gameState.walkingMinerals) print(m, Color.RED);
            for (MineralPatch d : gameState.blockingMinerals.values()) print(d, Color.RED);
            int counter = 0;
            for (Base b : gameState.BLs) {
                mapDrawer.drawTextMap(Util.getUnitCenterPosition(b.getLocation().toPosition(), UnitType.Terran_Command_Center), ColorUtil.formatText(Integer.toString(counter), ColorUtil.White));
                for (Mineral m : b.getBlockingMinerals()) print(m.getUnit(), Color.RED);
                counter++;
            }
            for (Building b : gameState.buildingLot) print(b, Color.PURPLE);
            for (Unit u : gameState.enemyInBase) print(u, Color.RED);
            for (Base b : gameState.islandBases)
                mapDrawer.drawTextMap(b.getLocation().toPosition(), ColorUtil.formatText("Island", ColorUtil.White));
            for (Unit u : gameState.islandCCs.values()) {
                print(u, Color.YELLOW);
            }
            
            for (Agent ag : gameState.agents.values()) {
                ag.drawAgentOnMap(ag, mapDrawer);
            }
            if (gameState.enemyStartBase != null)
                mapDrawer.drawTextMap(gameState.enemyStartBase.getLocation().toPosition(), ColorUtil.formatText("EnemyStartBase", ColorUtil.White));
            if (gameState.disrupterBuilding != null)
                mapDrawer.drawTextMap(gameState.disrupterBuilding.getPosition().add(new Position(0, -8)), ColorUtil.formatText("BM!", ColorUtil.White));
            if (gameState.enemyNaturalBase != null)
                mapDrawer.drawTextMap(gameState.enemyNaturalBase.getLocation().toPosition(), ColorUtil.formatText("EnemyNaturalBase", ColorUtil.White));
            if (gameState.mainChoke != null) {
                mapDrawer.drawTextMap(gameState.mainChoke.getCenter().toPosition(), ColorUtil.formatText("MainChoke", ColorUtil.White));
                //bw.getMapDrawer().drawTextMap(mainChoke.getCenter().toPosition(), ColorUtil.formatText(Double.toString(Util.getChokeWidth(mainChoke)), ColorUtil.White));
            }
            if (gameState.naturalChoke != null)
                mapDrawer.drawTextMap(gameState.naturalChoke.getCenter().toPosition(), ColorUtil.formatText("NatChoke", ColorUtil.White));
            if (gameState.chosenHarasser != null) {
                mapDrawer.drawTextMap(gameState.chosenHarasser.getPosition(), ColorUtil.formatText("Harasser", ColorUtil.White));
                print(gameState.chosenHarasser, Color.BLUE);
            }
            for (Map.Entry<SCV, MutablePair<UnitType, TilePosition>> u : gameState.workerBuild.entrySet()) {
                print(u.getKey(), Color.TEAL);
                mapDrawer.drawTextMap(u.getKey().getPosition(), ColorUtil.formatText("Building " + u.getValue().first.toString(), ColorUtil.White));
                print(u.getValue().second, u.getValue().first, Color.TEAL);
                mapDrawer.drawLineMap(u.getKey().getPosition(), Util.getUnitCenterPosition(u.getValue().second.toPosition(), u.getValue().first), Color.RED);
            }
            if (gameState.chosenUnitToHarass != null) {
                print(gameState.chosenUnitToHarass, Color.RED);
                mapDrawer.drawTextMap(gameState.chosenUnitToHarass.getPosition(), ColorUtil.formatText("UnitToHarass", ColorUtil.White));
            }

            for (Map.Entry<SCV, Mechanical> r : gameState.repairerTask.entrySet()) {
                print(r.getKey(), Color.YELLOW);
                mapDrawer.drawTextMap(r.getKey().getPosition(), ColorUtil.formatText("Repairer", ColorUtil.White));
                if (r.getValue() == null || !r.getValue().exists()) continue;
                print(r.getValue(), Color.YELLOW);
                mapDrawer.drawLineMap(r.getKey().getPosition(), r.getValue().getPosition(), Color.YELLOW);
            }
            for (UnitInfo ui : gameState.unitStorage.getEnemyUnits().values()) {
                mapDrawer.drawTextMap(ui.lastPosition.add(new Position(0, 16)), ColorUtil.formatText(ui.unitType.toString(), ColorUtil.White));
                print(ui.unit, Color.RED);
            }
            /*for (UnitInfo ui : gameState.unitStorage.getAllyUnits().values()) {
                mapDrawer.drawTextMap(ui.position.add(new Position(0, 16)), ColorUtil.formatText(ui.unitType.toString(), ColorUtil.White));
                print(ui.unit, Color.BLUE);
            }*/
            /*for (UnitInfo ui : gameState.myArmy) {
                mapDrawer.drawTextMap(ui.position.add(new Position(0, 16)), ColorUtil.formatText(ui.unitType.toString(), ColorUtil.White));
                print(ui.unit, Color.BLUE);
            }*/
            if (gameState.chosenScout != null) {
                mapDrawer.drawTextMap(gameState.chosenScout.getPosition(), ColorUtil.formatText("Scouter", ColorUtil.White));
                print(gameState.chosenScout, Color.PURPLE);
            }
            if (gameState.chosenRepairer != null)
                mapDrawer.drawTextMap(gameState.chosenRepairer.getPosition(), ColorUtil.formatText("ChosenRepairer", ColorUtil.White));
            for (ChokePoint c : gameState.bwem.getMap().getChokePoints()) {
                List<WalkPosition> sides = c.getGeometry();
                if (sides.size() == 3) {
                    mapDrawer.drawLineMap(sides.get(1).toPosition(), sides.get(2).toPosition(), Color.GREEN);
                }
            }
            for (Unit u : gameState.CCs.values()) {
                print(u, Color.YELLOW);
                mapDrawer.drawCircleMap(u.getPosition(), 500, Color.ORANGE);
            }
            for (Unit u : gameState.DBs.keySet()) {
                mapDrawer.drawCircleMap(u.getPosition(), 300, Color.ORANGE);
            }
            for (Unit u : gameState.workerIdle) print(u, Color.ORANGE);
            for (Map.Entry<SCV, Building> u : gameState.workerTask.entrySet()) {
                print(u.getKey(), Color.TEAL);
                mapDrawer.drawTextMap(u.getKey().getPosition(), ColorUtil.formatText("Tasked: " + u.getValue().getType().toString(), ColorUtil.White));
                print(u.getValue(), Color.TEAL);
                mapDrawer.drawLineMap(u.getKey().getPosition(), u.getValue().getPosition(), Color.RED);
            }
            for (Worker u : gameState.workerDefenders.keySet()) {
                print(u, Color.PURPLE);
                mapDrawer.drawTextMap(u.getPosition(), ColorUtil.formatText("SpartanSCV", ColorUtil.White));
            }
            for (Map.Entry<Worker, MineralPatch> u : gameState.workerMining.entrySet()) {
                print(u.getKey(), Color.CYAN);
                mapDrawer.drawLineMap(u.getKey().getPosition(), u.getValue().getPosition(), Color.CYAN);
            }
            for (Map.Entry<Worker, GasMiningFacility> u : gameState.workerGas.entrySet()) {
                if (u.getKey().getOrder() == Order.HarvestGas) continue;
                print(u.getKey(), Color.GREEN);
                mapDrawer.drawLineMap(u.getKey().getPosition(), u.getValue().getPosition(), Color.GREEN);
            }
            for (Map.Entry<VespeneGeyser, Boolean> u : gameState.vespeneGeysers.entrySet()) {
                print(u.getKey(), Color.GREEN);
                if (gameState.refineriesAssigned.containsKey(u.getKey())) {
                    int gas = gameState.refineriesAssigned.get(u.getKey());
                    mapDrawer.drawTextMap(u.getKey().getPosition(), ColorUtil.formatText(Integer.toString(gas), ColorUtil.White));
                }
            }
            gameState.sim.drawClusters();
            for (Squad s : gameState.sqManager.squads.values()) {
                if (s.status == Squad.Status.ATTACK && s.attack != null)
                    mapDrawer.drawLineMap(s.getSquadCenter(), s.attack, Color.ORANGE);
            }
            for (Squad s : gameState.sqManager.squads.values()) {
                if (s.members.isEmpty()) continue;
                Position center = s.getSquadCenter();
                //mapDrawer.drawCircleMap(center, 90, Color.GREEN);
                mapDrawer.drawTextMap(center.add(new Position(0, UnitType.Terran_Marine.dimensionUp())), ColorUtil.formatText(s.status.toString(), ColorUtil.White));
                mapDrawer.drawTextMap(center.add(new Position(0, UnitType.Terran_Marine.dimensionUp() * 2)), ColorUtil.formatText(s.lose ? "Lose" : "Win", ColorUtil.White));
            }
            for (Map.Entry<MineralPatch, Integer> m : gameState.mineralsAssigned.entrySet()) {
                print(m.getKey(), Color.CYAN);
                if (m.getValue() == 0) continue;
                mapDrawer.drawTextMap(m.getKey().getPosition(), ColorUtil.formatText(m.getValue().toString(), ColorUtil.White));
            }
        } catch (Exception e) {
            System.err.println("debugScreen Exception");
            e.printStackTrace();
        }
    }

    private void debugText(GameState gameState) {
        try {
            if (!ConfigManager.getConfig().ecgConfig.debugText) return;
            mapDrawer.drawTextScreen(320, 5, ColorUtil.formatText(gameState.supplyMan.getSupplyUsed() + "/" + gameState.supplyMan.getSupplyTotal(), ColorUtil.White));
            mapDrawer.drawTextScreen(320, 20, ColorUtil.formatText(gameState.getArmySize() + "/" + gameState.getStrategyFromManager().armyForAttack, ColorUtil.White));
            String defending = gameState.defense ? ColorUtil.formatText("Defense", ColorUtil.Green) : ColorUtil.formatText("Defense", ColorUtil.Red);
            mapDrawer.drawTextScreen(320, 35, defending);
            mapDrawer.drawTextScreen(320, 50, ColorUtil.formatText("I want to train: " + gameState.chosenUnit.toString(), ColorUtil.White));
            mapDrawer.drawTextScreen(320, 65, ColorUtil.formatText("I want to build: " + gameState.chosenToBuild.toString(), ColorUtil.White));
            mapDrawer.drawTextScreen(320, 80, ColorUtil.formatText("Max_Goliaths: " + gameState.maxGoliaths, ColorUtil.White));
            mapDrawer.drawTextScreen(320, 95, ColorUtil.formatText("Max_Vessels: " + gameState.maxVessels, ColorUtil.White));
            if (gameState.enemyRace == Race.Zerg)
                mapDrawer.drawTextScreen(320, 110, ColorUtil.formatText("Max_Firebats: " + gameState.maxBats, ColorUtil.White));
            if (gameState.ih.allies().size() + gameState.ih.enemies().size() == 1) {
                mapDrawer.drawTextScreen(10, 5,
                        ColorUtil.formatText(gameState.ih.self().getName(), ColorUtil.getColor(gameState.ih.self().getColor())) +
                                ColorUtil.formatText(" vs ", ColorUtil.White) +
                                ColorUtil.formatText(gameState.ih.enemy().getName(), ColorUtil.getColor(gameState.ih.enemy().getColor())));
            }
            if (gameState.chosenScout != null) {
                mapDrawer.drawTextScreen(10, 20, ColorUtil.formatText("Scouting: ", ColorUtil.White) + ColorUtil.formatText("Yes", ColorUtil.Green));
            } else {
                mapDrawer.drawTextScreen(10, 20, ColorUtil.formatText("Scouting: ", ColorUtil.White) + ColorUtil.formatText("No", ColorUtil.Red));
            }
            if (gameState.enemyMainBase != null) {
                mapDrawer.drawTextScreen(10, 35, ColorUtil.formatText("Enemy Base Found: ", ColorUtil.White) + ColorUtil.formatText("Yes", ColorUtil.Green));
            } else {
                mapDrawer.drawTextScreen(10, 35, ColorUtil.formatText("Enemy Base Found: ", ColorUtil.White) + ColorUtil.formatText("No", ColorUtil.Red));
            }
            mapDrawer.drawTextScreen(10, 50, ColorUtil.formatText("Framecount: ", ColorUtil.White) + ColorUtil.formatText(Integer.toString(gameState.frameCount), ColorUtil.Yellow));
            mapDrawer.drawTextScreen(10, 65, ColorUtil.formatText("FPS: ", ColorUtil.White) + ColorUtil.formatText(Integer.toString(gameState.ih.getFPS()), ColorUtil.Yellow));
            mapDrawer.drawTextScreen(65, 65, ColorUtil.formatText("APM: ", ColorUtil.White) + ColorUtil.formatText(Integer.toString(gameState.ih.getAPM()), ColorUtil.Yellow));
            mapDrawer.drawTextScreen(10, 80, ColorUtil.formatText("Strategy: ", ColorUtil.White) + ColorUtil.formatText(gameState.getStrategyFromManager().name, ColorUtil.Yellow));
            mapDrawer.drawTextScreen(10, 95, ColorUtil.formatText("EnemyStrategy: ", ColorUtil.White) + ColorUtil.formatText(IntelligenceAgency.getEnemyStrat().toString(), ColorUtil.Yellow));
            mapDrawer.drawTextScreen(10, 110, ColorUtil.formatText("SimTime(ms): ", ColorUtil.White) + ColorUtil.formatText(String.valueOf(gameState.sim.time), ColorUtil.Teal));
            if (gameState.enemyRace == Race.Zerg && gameState.learningManager.isNaughty()) {
                mapDrawer.drawTextScreen(10, 125, ColorUtil.formatText("Naughty Zerg: ", ColorUtil.White) + ColorUtil.formatText("yes", ColorUtil.Green));
            }
        } catch (Exception e) {
            System.err.println("debugText Exception");
            e.printStackTrace();
        }
    }

    private void print(Unit u, Color color) {
        mapDrawer.drawBoxMap(u.getLeft(), u.getTop(), u.getRight(), u.getBottom(), color);
    }

    private void print(TilePosition u, UnitType type, Color color) {
        Position leftTop = new Position(u.getX() * TilePosition.SIZE_IN_PIXELS, u.getY() * TilePosition.SIZE_IN_PIXELS);
        Position rightBottom = new Position(leftTop.getX() + type.tileWidth() * TilePosition.SIZE_IN_PIXELS, leftTop.getY() + type.tileHeight() * TilePosition.SIZE_IN_PIXELS);
        mapDrawer.drawBoxMap(leftTop, rightBottom, color);
    }

    private void print(TilePosition u, Color color) {
        Position leftTop = new Position(u.getX() * TilePosition.SIZE_IN_PIXELS, u.getY() * TilePosition.SIZE_IN_PIXELS);
        Position rightBottom = new Position(leftTop.getX() + TilePosition.SIZE_IN_PIXELS, leftTop.getY() + TilePosition.SIZE_IN_PIXELS);
        mapDrawer.drawBoxMap(leftTop, rightBottom, color);
    }

    public void onFrame(GameState gs) {
        debugScreen(gs);
        debugText(gs);
    }
}

abstract class TextSetting{
    protected boolean setting;

    protected abstract void setting_ConfigManager();
    protected abstract void sendText();
    protected abstract void setting_debugText();

    public void doInteraction(CameraModule skycladObserver){
        setting_ConfigManager();
        sendText();
        setting_debugText();
    }
}

class TextSetting_dt extends TextSetting{
    @Override
    protected void setting_ConfigManager(){
        setting = ConfigManager.getConfig().ecgConfig.debugText;
    }
    @Override
    protected void sendText(){
        Util.sendText(!setting ? "debugText enabled" : "debugText disabled");
    }
    @Override
    protected void setting_debugText(){
        ConfigManager.getConfig().ecgConfig.debugText = !setting;
    }
}
class TextSetting_dc extends TextSetting{
    @Override
    protected void setting_ConfigManager(){
        setting = ConfigManager.getConfig().ecgConfig.debugConsole;
    }
    @Override
    protected void sendText(){
        Util.sendText(!setting ? "debugConsole enabled" : "debugConsole disabled");
    }
    @Override
    protected void setting_debugText(){
        ConfigManager.getConfig().ecgConfig.debugConsole = !setting;
    }
}
class TextSetting_ds extends TextSetting{
    @Override
    protected void setting_ConfigManager(){
        setting = ConfigManager.getConfig().ecgConfig.debugScreen;
    }
    @Override
    protected void sendText(){
        Util.sendText(!setting ? "debugScreen enabled" : "debugScreen disabled");
    }
    @Override
    protected void setting_debugText(){
        ConfigManager.getConfig().ecgConfig.debugScreen = !setting;
    }
}
class TextSetting_obs extends TextSetting{
    @Override
    protected void setting_ConfigManager(){
        setting = ConfigManager.getConfig().ecgConfig.enableSkyCladObserver;
    }
    @Override
    protected void sendText(){
        Util.sendText(!setting ? "Observer enabled" : "Observer disabled");
    }
    @Override
    protected void setting_debugText(){
        ConfigManager.getConfig().ecgConfig.enableSkyCladObserver = !setting;
    }
    @Override
    public void doInteraction(CameraModule skycladObserver){
        super.doInteraction(skycladObserver);
        skycladObserver.toggle();
    }
}
class TextSetting_sounds extends TextSetting{
    @Override
    protected void setting_ConfigManager(){
        setting = ConfigManager.getConfig().ecgConfig.sounds;
    }
    @Override
    protected void sendText(){
        Util.sendText(!setting ? "Sounds Effects enabled" : "Sounds Effects disabled");
    }
    @Override
    protected void setting_debugText(){
        ConfigManager.getConfig().ecgConfig.sounds = !setting;
    }
    
}
class TextSetting_noattack extends TextSetting{
    @Override
    protected void setting_ConfigManager(){
        setting = ConfigManager.getConfig().ecgConfig.debugDisableAttack;
    }
    @Override
    protected void sendText(){
        Util.sendText(!setting ? "Debug Attack enabled" : "Debug Attack disabled");
    }
    @Override
    protected void setting_debugText(){
        ConfigManager.getConfig().ecgConfig.debugDisableAttack = !setting;
    }
    
}
       
