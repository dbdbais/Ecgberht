package ecgberht.Strategies;

import bwapi.UnitType;
import ecgberht.AStrategy;

public class ProxyBBS extends AStrategy{

	public ProxyBBS() {
		initStrategy();
	}
	
	@Override
	public void initStrategy() {
		name = "ProxyBBS";
		armyForBay = 0;
		armyForTurret = 0;
		numBays = 0;
		raxPerCC = 2;
		facPerCC = 0;
		numRaxForFac = 0;
		bunker = false;
		proxy = true;
		supplyForFirstRefinery = 400;
		armyForAttack = 8;
		armyForExpand = 100;
		initTrainUnits();
		initBuildUnits();
		initBuildAddons();
		initTechToResearch();
		initUpgradesToResearch();
	}
	
	@Override
	public void initTrainUnits() {
		trainUnits.add(UnitType.Terran_Marine);
	}

	@Override
	public void initBuildUnits() {
		if(bunker) {
			buildUnits.add(UnitType.Terran_Bunker);
		}
	}

	@Override
	public void initBuildAddons() {

	}

	@Override
	public void initTechToResearch() {
		
	}

	@Override
	public void initUpgradesToResearch() {
		
	}
}