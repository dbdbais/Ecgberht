package ecgberht.BehaviourTrees.Build;

import ecgberht.Agents.WorkerScoutAgent;
import ecgberht.GameState;
import ecgberht.Util.Util;
import org.iaie.btree.BehavioralTree.State;
import org.iaie.btree.task.leaf.Action;
import org.openbw.bwapi4j.Player;
import org.openbw.bwapi4j.type.TechType;
import org.openbw.bwapi4j.type.UnitType;

public class ChooseFactory extends Action {

    public ChooseFactory(String name, GameState gh) {
        super(name, gh);
    }

    @Override
    public State execute() {
        try {
            String strat = gameState.getStrategyFromManager().getName();
            if (strat.equals("FullMech") || strat.equals("MechGreedyFE")) {
                Player self = gameState.getPlayer();
                if (Util.countBuildingAll(UnitType.Terran_Factory) > 1 &&
                        !self.isResearching(TechType.Tank_Siege_Mode) && !self.hasResearched(TechType.Tank_Siege_Mode)) {
                    return State.FAILURE;
                }
            }
            if (gameState.MBs.isEmpty() || gameState.getStrategyFromManager().getNumRaxForFac() > Util.countBuildingAll(UnitType.Terran_Barracks) ||
                    (Util.countBuildingAll(UnitType.Terran_Factory) > 0 && gameState.getStrategyFromManager().getFacPerCC() == 0)) {
                return State.FAILURE;
            }
            if (Util.countBuildingAll(UnitType.Terran_Factory) == 0 && gameState.getStrategyFromManager().getFacPerCC() == 0) {
                gameState.chosenToBuild = UnitType.Terran_Factory;
                return State.SUCCESS;
            } else if (Util.countBuildingAll(UnitType.Terran_Factory) < gameState.getStrategyFromManager().getFacPerCC() * Util.getNumberCCs()) {
                if (strat.equals("TwoPortWraith") && gameState.naughtySCV != null && gameState.Fs.isEmpty() && gameState.proxyBuilding == null) {
                    WorkerScoutAgent w = (WorkerScoutAgent) gameState.agents.get(gameState.naughtySCV);
                    if (w != null && w.statusToString().equals("Proxying")) {
                        gameState.chosenToBuild = UnitType.None;
                        return State.SUCCESS;
                    }
                }
                gameState.chosenToBuild = UnitType.Terran_Factory;
                return State.SUCCESS;
            }
            return State.FAILURE;
        } catch (Exception e) {
            System.err.println(this.getClass().getSimpleName());
            e.printStackTrace();
            return State.ERROR;
        }
    }
}
