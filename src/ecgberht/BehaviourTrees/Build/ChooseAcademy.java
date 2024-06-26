package ecgberht.BehaviourTrees.Build;

import ecgberht.GameState;
import ecgberht.IntelligenceAgency;
import ecgberht.Strategy;
import ecgberht.Util.MutablePair;
import ecgberht.Util.Util;
import org.iaie.btree.BehavioralTree.State;
import org.iaie.btree.task.leaf.Action;
import org.openbw.bwapi4j.TilePosition;
import org.openbw.bwapi4j.type.UnitType;
import org.openbw.bwapi4j.unit.Academy;
import org.openbw.bwapi4j.unit.Building;

public class ChooseAcademy extends Action {

    public ChooseAcademy(String name, GameState gh) {
        super(name, gh);
    }

    @Override
    public State execute() {
        try {
            if (Util.countBuildingAll(UnitType.Terran_Refinery) == 0 || Util.countBuildingAll(UnitType.Terran_Academy) > 0) {
                return State.FAILURE;
            }
            Strategy strat = gameState.getStrategyFromManager();
            if (strat.getName().equals("FullMech") || strat.getName().equals("MechGreedyFE")) {
                if (gameState.Fs.size() >= strat.getFacPerCC()
                        || IntelligenceAgency.enemyHasType(UnitType.Protoss_Dark_Templar)
                        || IntelligenceAgency.enemyHasType(UnitType.Zerg_Lurker)) {
                    gameState.chosenToBuild = UnitType.Terran_Academy;
                    return State.SUCCESS;
                } else return State.FAILURE;
            }
            if (Util.countBuildingAll(UnitType.Terran_Barracks) >= gameState.getStrategyFromManager().getNumRaxForAca()) {
                for (MutablePair<UnitType, TilePosition> w : gameState.workerBuild.values()) {
                    if (w.first == UnitType.Terran_Academy) return State.FAILURE;
                }
                for (Building w : gameState.workerTask.values()) {
                    if (w instanceof Academy) return State.FAILURE;
                }
                gameState.chosenToBuild = UnitType.Terran_Academy;
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
