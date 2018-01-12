package ecgberht.MoveToBuild;

import java.util.List;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Action;
import org.iaie.btree.util.GameHandler;
import ecgberht.GameState;

import bwapi.Game;
import bwapi.Pair;
import bwapi.Player;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;

public class ChoosePosition extends Action {

	public ChoosePosition(String name, GameHandler gh) {
		super(name, gh);
	}

	@Override
	public State execute() {
		try {
			Game juego = ((GameState)this.handler).getGame();
			Player jugador = ((GameState)this.handler).getPlayer();
			TilePosition origin = null;
			if(((GameState)this.handler).chosenToBuild.isRefinery()) {
				if(!((GameState)this.handler).refineriesAssigned.isEmpty()) {
					for (Pair<Pair<Unit,Integer>,Boolean> g : ((GameState)this.handler).refineriesAssigned) {
						if (!g.second) {
							((GameState)this.handler).chosenPosition = g.first.first.getTilePosition();
							return State.SUCCESS;
						}
					}
				}

			} else {
				if(!((GameState)this.handler).workerBuild.isEmpty()) {
					for(Pair<Unit,Pair<UnitType,TilePosition> > w : ((GameState)this.handler).workerBuild) {
						((GameState)this.handler).testMap.actualizaMapa(w.second.second, w.second.first, false);
					}
				}

				if(!((GameState)this.handler).chosenToBuild.equals(UnitType.Terran_Bunker) && !((GameState)this.handler).chosenToBuild.equals(UnitType.Terran_Missile_Turret)) {
					origin = BWTA.getRegion(jugador.getStartLocation()).getCenter().toTilePosition();
				}
				else{
					if(((GameState)this.handler).chosenToBuild.equals(UnitType.Terran_Missile_Turret)) {
						if(((GameState)this.handler).DBs.isEmpty()) {
							origin = BWTA.getNearestChokepoint(jugador.getStartLocation()).getCenter().toTilePosition();
						}
						else {
							for(Pair<Unit,List<Unit> > b : ((GameState)this.handler).DBs) {
								origin = b.first.getTilePosition();
								break;
							}
						}
					}
					else {
						if(((GameState)this.handler).Ts.isEmpty()) {
							if(((GameState)this.handler).closestChoke != null) {
								origin = ((GameState)this.handler).closestChoke;
							}
//							else {
//								origin = BWTA.getNearestChokepoint(jugador.getStartLocation()).getCenter().toTilePosition();
//							}

						}
						else {
							for(Unit b : ((GameState)this.handler).Ts) {
								origin = b.getTilePosition();
								break;
							}
						}

					}

				}
				TilePosition posicion = ((GameState)this.handler).testMap.buscaPosicion(((GameState)this.handler).chosenToBuild, origin);
				((GameState)this.handler).testMap = ((GameState)this.handler).map.clone();
				if(posicion != null) {
					((GameState)this.handler).chosenPosition = posicion;
					return State.SUCCESS;
				}
			}

			TilePosition posicion = juego.getBuildLocation(((GameState)this.handler).chosenToBuild, BWTA.getRegion(jugador.getStartLocation()).getCenter().toTilePosition(), 500);
			if(posicion != null) {
				if(juego.canBuildHere(posicion, ((GameState)this.handler).chosenToBuild)) {
					((GameState)this.handler).chosenPosition = posicion;
					return State.SUCCESS;
				}
			}
			return State.FAILURE;
		} catch(Exception e) {
			System.err.println(this.getClass().getSimpleName());
			System.err.println(e);
			return State.ERROR;
		}
	}
}