package ecgberht.MoveToBuild;

import java.util.Map.Entry;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Action;
import org.iaie.btree.util.GameHandler;
import org.openbw.bwapi4j.TilePosition;
import org.openbw.bwapi4j.type.UnitType;
import org.openbw.bwapi4j.unit.Barracks;
import org.openbw.bwapi4j.unit.Building;
import org.openbw.bwapi4j.unit.GasMiningFacility;
import org.openbw.bwapi4j.unit.Refinery;
import org.openbw.bwapi4j.util.Pair;

import ecgberht.GameState;

public class ChooseRefinery extends Action {

	public ChooseRefinery(String name, GameHandler gh) {
		super(name, gh);
	}

	@Override
	public State execute() {
		try {
			if(((GameState)this.handler).getPlayer().supplyUsed() < ((GameState)this.handler).strat.supplyForFirstRefinery || ((GameState)this.handler).getCash().second >= 300) {
				return State.FAILURE;
			}
			if(((GameState)this.handler).refineriesAssigned.size() == 1) {
				boolean found = false;
				for(Pair<UnitType, TilePosition> w:((GameState)this.handler).workerBuild.values()) {
					if(w.first == UnitType.Terran_Barracks) {
						found = true;
						break;
					}
				}
				for(Building w:((GameState)this.handler).workerTask.values()) {
					if(w instanceof Barracks) {
						found = true;
						break;
					}
				}
				if(((GameState)this.handler).MBs.isEmpty() && found == false) {
					return State.FAILURE;
				}
			}
			int count = 0;
			GasMiningFacility geyser = null;
			for(Entry<GasMiningFacility, Pair<Integer, Boolean>> r: ((GameState)this.handler).refineriesAssigned.entrySet()) {
				if(r.getValue().second) {
					count++;
				}
				else{
					geyser = r.getKey();
				}
			}
			if(count == ((GameState)this.handler).refineriesAssigned.size()) {
				return State.FAILURE;
			}
			for(Pair<UnitType, TilePosition> w:((GameState)this.handler).workerBuild.values()) {
				if(w.first == UnitType.Terran_Refinery) {
					return State.FAILURE;
				}
			}
			for(Building w:((GameState)this.handler).workerTask.values()) {
				if(w instanceof Refinery && w.getPosition().equals(geyser.getPosition())) {
					return State.FAILURE;
				}
			}

			((GameState)this.handler).chosenToBuild = UnitType.Terran_Refinery;
			return State.SUCCESS;

		} catch(Exception e) {
			System.err.println(this.getClass().getSimpleName());
			System.err.println(e);
			return State.ERROR;
		}
	}
}
