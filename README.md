# 코드 리팩토링 / 테스팅 프로젝트

- 리팩토링: 오픈소스 프로젝트를 Fork해 학습한 Design Pattern을 적용하거나 Code Smell을 없애는 작업을 수행한다.
- 테스팅 : 기능을 변경하지 않으면서 리팩토링을 하였는지 JUnit5와 JACOCO를 이용해 테스트한다.

## 1. SingleTon Pattern & Builder Pattern 적용

```java
public class StrategyManager {
	private static StrategyManager managerInstance;
	  private StrategyManager() {
	    }
	  public static StrategyManager getInstance() {
		  if(!managerUsed) {
			  managerInstance = new StrategyManager();
			  managerUsed = true;
		  }
			return managerInstance;
		}
	
	private static boolean managerUsed = false;

```

```java
  public static class BioGreedyFEBuilder{
		private boolean bunker = false;
		private boolean proxy = false;
		private boolean harass = true;
		private int armyForAttack = 0;
		private int armyForExpand = 20;
		private int armyForTurret = 0;
		private int facPerCC = 0;
		private int numBays = 0;
		private int numCCForPort = 0;
		private int workerGas = 3;
		private int numCCForScience = 0;
		private int numRaxForAca = 2;
		private int numRaxForFac = 0;
		private int numFacForPort = 1;
		private int extraSCVs = 3;
		private int portPerCC = 0;
		private int raxPerCC = 0;
		private int supplyForFirstRefinery = 0;
		private String name;
		private int armyForBay = 0;
		private int facForArmory = 0;
		private int numArmories = 0;
		private Set<TechType> techToResearch = new HashSet<>();
		private Set<UnitType> buildAddons = new HashSet<>();
		private Set<UnitType> buildUnits = new HashSet<>();
		private Set<UnitType> trainUnits = new HashSet<>();
		private Set<UpgradeType> upgradesToResearch = new HashSet<>();

		public BioGreedyFEBuilder(String name, int armyForBay, int armyForTurret, int numBays, int raxPerCC, int facPerCC,
				int numRaxForAca, int numRaxForFac, int supplyForFirstRefinery, int armyForAttack, int armyForExpand,
				int numCCForPort, int numCCForScience, int portPerCC, int facForArmory, int numArmories,
				boolean bunker) {
			this.name=name;
			this.armyForBay=armyForBay;
			this.armyForTurret =armyForTurret;
			this.numBays = numBays;
			this.raxPerCC = raxPerCC;
			this.facPerCC = facPerCC;
			this.numRaxForAca = numRaxForAca;
			this.numRaxForFac = numRaxForFac;
			this.supplyForFirstRefinery = supplyForFirstRefinery;
			this.armyForAttack = armyForAttack;
			this.armyForExpand = armyForExpand;
			this.numCCForPort = numCCForPort;
			this.numCCForScience = numCCForScience;
			this.portPerCC = portPerCC;
			this.facForArmory =facForArmory;
			this.numArmories = numArmories;
			this.bunker = bunker;
		}
		public BioGreedyFE build() {
			return new BioGreedyFE(this);
		}

    }
	private BioGreedyFE(BioGreedyFEBuilder builder) 
	{
		super(builder.name,builder.armyForBay,builder.armyForTurret,builder.numBays,builder.raxPerCC
				,builder.facPerCC,builder.numRaxForAca,builder.numRaxForFac,builder.supplyForFirstRefinery,
				 builder.armyForAttack,builder.armyForExpand,builder.numCCForPort,builder.numCCForScience,
				 builder.portPerCC,builder.facForArmory,builder.numArmories,builder.bunker);

		initStrategy();
	}
}
```

![singleton   Builder](https://github.com/dbdbais/Ecgberht/assets/99540674/a0a937b2-e57f-4535-8695-f51e35e540c5)



## 2. Template Methon Pattern 적용

```java
package ecgberht;
import static ecgberht.Ecgberht.getGs;
import java.util.Map;
import java.util.TreeMap;
import org.openbw.bwapi4j.Player;
import org.openbw.bwapi4j.unit.PlayerUnit;
import org.openbw.bwapi4j.unit.SiegeTank;
import org.openbw.bwapi4j.unit.Unit;
public abstract class OnUnitAction {
	Unit unit;
	UnitStorage storage;
	protected Map<Unit, UnitInfo> ally = new TreeMap<>();
    protected Map<Unit, UnitInfo> enemy = new TreeMap<>();
	public OnUnitAction(Unit unit) {
		this.unit = unit;
	}
	public void setAlly(Map<Unit, UnitInfo> ally) {	//user input NULL for testcase
		this.ally = ally;
	}
	public void setEnemy(Map<Unit, UnitInfo> enemy) {	//user input NULL for testcase
		this.enemy = enemy;
	}
	abstract void action();
	protected void pushCreatedAllyUnit(Unit unit) {
		UnitInfo u = new UnitInfo((PlayerUnit) unit);
        ally.put(unit, u);
	}
	protected void pushCreatedEnemyUnit(Unit unit) {
		UnitInfo u = new UnitInfo((PlayerUnit) unit);
        enemy.put(unit, u);
	}
}
```

![TemplateMethod](https://github.com/dbdbais/Ecgberht/assets/99540674/405e4645-9255-45c1-af3c-ae31e9341264)


## 3. Factory Method Pattern 적용

```java
package ecgberht.BehaviourTrees.Training;

import org.openbw.bwapi4j.type.UnitType;
import org.openbw.bwapi4j.unit.TrainingFacility;

import ecgberht.GameState;
import ecgberht.Util.Util;

public class StateProxyFactory {
	public static StateProxy getStateProxy(TrainingFacility currentFacility, GameState gameState) {
		StateProxy proxy = null;
		final int barraksForEightRax = 0;
		boolean gt16_Supplies = gameState.supplyMan.getSupplyUsed() >= 16;	
		final int barraksForBBS = 2;	
		boolean noSupplyDepot = Util.countBuildingAll(UnitType.Terran_Supply_Depot) == 0;	
		final String bbs = "ProxyBBS";
		final String eightRax = "ProxyEightRax";
		String strategyName = gameState.getStrategyFromManager().name;
		switch(strategyName){
		case bbs: proxy = new StateProxyBBS(currentFacility,barraksForBBS,noSupplyDepot,gameState);
		break;
		case eightRax : proxy = new StateProxyEightRax(currentFacility,barraksForEightRax,gt16_Supplies,gameState);
		break;
		}
		return proxy;
	}
}

```

```java
package ecgberht.BehaviourTrees.Training;

import org.iaie.btree.BehavioralTree.State;
import org.openbw.bwapi4j.type.UnitType;
import org.openbw.bwapi4j.unit.TrainingFacility;

import ecgberht.GameState;
import ecgberht.Util.Util;

public abstract class StateProxy {
	protected TrainingFacility facility;
	protected int numberOfBarraks;
	protected boolean condition;
	protected GameState gameState;
	public StateProxy(TrainingFacility currentFacility, int numberOfBarraks, boolean condition ,GameState gameState){
		this.facility =currentFacility;
		this.numberOfBarraks = numberOfBarraks;
		this.condition = condition;
		this.gameState = gameState;
	}
	protected State checkProxyStrategy(GameState gameState) {
		{
			if (Util.countBuildingAll(UnitType.Terran_Barracks) == numberOfBarraks && condition) {
			    trainFail(gameState);	
			}
				if (gameState.getSupply() > 0) {
				    facility.train(gameState.chosenUnit);
				    return State.SUCCESS;
				}

			return State.FAILURE;
		}
	}
	protected State trainFail(GameState gameState) {
		gameState.chosenToBuild = UnitType.None;
		return State.FAILURE;
	}
}
```

```java
package ecgberht.BehaviourTrees.Training;

import org.iaie.btree.BehavioralTree.State;
import org.openbw.bwapi4j.type.UnitType;
import org.openbw.bwapi4j.unit.TrainingFacility;

import ecgberht.GameState;
import ecgberht.Util.Util;

public class StateProxyBBS extends StateProxy{
	public StateProxyBBS(TrainingFacility currentFacility, int numberOfBarraks, boolean condition,
			GameState gameState) {
		super(currentFacility, numberOfBarraks, condition, gameState);
	}
}
```

```java
package ecgberht.BehaviourTrees.Training;

import org.iaie.btree.BehavioralTree.State;
import org.openbw.bwapi4j.type.UnitType;
import org.openbw.bwapi4j.unit.TrainingFacility;

import ecgberht.GameState;
import ecgberht.Util.Util;

public class StateProxyEightRax extends StateProxy{
	public StateProxyEightRax(TrainingFacility currentFacility, int numberOfBarraks, boolean condition,
			GameState gameState) {
		super(currentFacility, numberOfBarraks, condition, gameState);
	}
}
```

![FactoryMethod](https://github.com/dbdbais/Ecgberht/assets/99540674/27d401ea-fac0-46e5-90b5-1d2893af1bc5)


## 4. 그외 Code Smell Refactoring
```java
package ecgberht.BehaviourTrees.Training;

import ecgberht.GameState;
import ecgberht.Util.MutablePair;
import ecgberht.Util.Util;
import org.iaie.btree.BehavioralTree.State;
import org.iaie.btree.task.leaf.Action;
import org.openbw.bwapi4j.TilePosition;
import org.openbw.bwapi4j.type.UnitType;
import org.openbw.bwapi4j.unit.TrainingFacility;

public class TrainUnit extends Action {
    public TrainUnit(String name, GameState gamestate) {
        super(name, gamestate);
    }
    @Override
    public State execute() {
        try {
            if (gameState.chosenUnit == UnitType.None) return State.FAILURE;
            TrainingFacility currentFacility = gameState.chosenTrainingFacility;
            StateProxy proxy = StateProxyFactory.getStateProxy(currentFacility, gameState);	
            proxy.checkProxyStrategy(gameState);	   
            final int conditionForTrain_supply = 4;	
            final int conditionForTrain_totalSupply = 400;	
			boolean gtFourSupplies = gameState.getSupply() > conditionForTrain_supply;	
			boolean gtTotalFourHundredSupplies = gameState.getPlayer().supplyTotal() >= conditionForTrain_totalSupply;	
			if (gtFourSupplies || gameState.checkSupply() || gtTotalFourHundredSupplies) {
                final boolean gameStateNotDefense = !gameState.defense;	
				final boolean chooseToBuildCommandCenter = gameState.chosenToBuild == UnitType.Terran_Command_Center;	
				if (gameStateNotDefense && chooseToBuildCommandCenter) {
                    boolean found = checkCommandCenter(); 
                    if (!found) {
                    	gameState.chosenTrainingFacility = null;
                		gameState.chosenToBuild = UnitType.None;
                		return State.FAILURE;
                    }
                }
                currentFacility.train(gameState.chosenUnit);	
                return State.SUCCESS;
            }
            return State.FAILURE;
            
        } catch (Exception e) {
            System.err.println(this.getClass().getSimpleName());
            e.printStackTrace();
            return State.ERROR;
        }
    }
	public boolean checkCommandCenter() {
		boolean found = false;
		for (MutablePair<UnitType, TilePosition> pairOfUnitTile : gameState.workerBuild.values()) {	
		    if (pairOfUnitTile.first == UnitType.Terran_Command_Center) {
		        found = true;
		        break;
		    }
		}
		return found;
	}
}
```

# 테스팅 (JUnit5 & Jacoco)

## StrategyManagerTest

```java
package ecgberht;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ecgberht.Strategies.*;
/*
Purpose: Test whether strategy manager with singleton pattern and other
methods in StrategyManager works well

Input: StrategyManager instance, gamestate variable, SubStrategies

Expected: The instance received by getInstance is always the same.

Verify that getUcbVal produces the correct output.

Verify that getSubStrategy returns the matching strategy.
*/
class StrategyManagerTest {
	StrategyManager manager = StrategyManager.getInstance();
	
	@Test
	void getInstanceTest() {
		StrategyManager manager1 = StrategyManager.getInstance();
		StrategyManager manager2 = StrategyManager.getInstance();
		assertNotNull(manager1);
		assertNotNull(manager2);
		assertSame(manager1,manager2); 
	}
	@Test
	void getSubStrategyTest() {
		 Strategy fullBio = new FullBio
	    		.FullBioBuilder("FullBio",15,10,2,3,0,2,4,36,32,15,2,2,0,0,0,false)
	    		.build();
	     Strategy proxyBBS = new ProxyBBS
	    		.ProxyBBSBuilder("ProxyBBS",0,0,0,2,0,0,0,400,8,100,0,0,0,0,0,false)
	    		.setProxy(true)
	    		.build();
	     Strategy bioMech = new BioMech
	    		.BioMechBuilder("BioMech",24,10,1,2,1,2,1,2,2,0,36,30,18,2,1,false)
	    		.build();
	     Strategy fullBioFE = new FullBioFE
	    		.FullBioFEBuilder("FullBioFE",15,10,2,3,0,2,4,36,28,8,2,2,0,0,0,false)
	    		.build();
	     Strategy bioMechFE = new BioMechFE
	    		.BioMechFEBuilder("BioMechFE",15,10,1,3,1,2,1,38,25,9,2,2,0,2,1,false)
	    		.build();
	     Strategy fullMech = new FullMech
	    		.FullMechBuilder("FullMech",15,10,1,1,2,1,1,28,30,8,1,2,1,2,1,false)
	    		.build();
	     Strategy bioGreedyFE = new BioGreedyFE
	    		.BioGreedyFEBuilder("BioGreedyFE", 15, 10, 2, 3, 0, 2, 3, 38, 25, 0, 2, 2, 0, 0, 0, true)
	    		.build();
	     Strategy mechGreedyFE = new MechGreedyFE
	    		.MechGreedyFEBuilder("MechGreedyFE",15,10,1,1,2,2,1,36,30,0,2,2,1,2,1,true).
	    		build();
	     Strategy bioMechGreedyFE = new BioMechGreedyFE
	    		.BioMechGreedyFEBuilder("BioMechGreedyFE",15,10,1,2,1,2,2,38,30,0,3,3,0,2,1,true)
	    		.build();
	     Strategy twoPortWraith = new TwoPortWraith
	    		.TwoPortWraithBuilder("TwoPortWraith",15,10,1,1,1,1,1,26,25,14,1,2,2,2,1,false)
	    		.setExtraSCVs(1)
	    		.setWorkerGas(3)
	    		.setHarass(false)
	    		.build();
	     Strategy proxyEightRax = new ProxyEightRax
	    		.ProxyEightRaxBuilder("ProxyEightRax",0,0,0,1,0,0,0,400,7,100,0,0,0,0,0,false)
	    		.setProxy(true)
	    		.build();
	     Strategy vultureRush = new VultureRush
	    		.VultureRushBuilder("VultureRush",15,10,1,1,3,1,1,24,5,14,1,2,1,2,1,false)
	    		.setNumFacForPort(2)
	    		.setWorkerGas(3)
	    		.build();
	     Strategy theNiteKat = new TheNitekat
	    		.TheNitekatBuilder("TheNitekat",30,30,0,1,2,3,1,26,5,25,2,2,0,3,1,false)
	    		.setExtraSCVs(1)
	    		.build();
	     Strategy joyORush = new JoyORush
	    		.JoyORushBuilder("JoyORush",30,30,0,1,2,3,1,26,5,25,2,2,0,3,1,false)
	    		.setExtraSCVs(1)
	    		.build();
	     Strategy fastCC = new FastCC
	    		.FastCCBuilder("14CC",15,10,1,2,1,2,2,38,30,0,3,3,0,2,1,true)
	    		.build();
	     
	     assertEquals(fullBio,manager.getSubStrategy("FullBio"));
	     assertEquals(proxyBBS,manager.getSubStrategy("ProxyBBS"));
	     assertEquals(bioMech,manager.getSubStrategy("BioMech"));
	     assertEquals(fullBioFE,manager.getSubStrategy("FullBioFE"));
	     assertEquals(bioMechFE,manager.getSubStrategy("BioMechFE"));
	     assertEquals(mechGreedyFE,manager.getSubStrategy("MechGreedyFE"));
	     assertEquals(twoPortWraith,manager.getSubStrategy("TwoPortWraith"));
	     assertEquals(proxyEightRax,manager.getSubStrategy("ProxyEightRax"));
	     assertEquals(vultureRush,manager.getSubStrategy("VultureRush"));
	     assertEquals(theNiteKat,manager.getSubStrategy("TheNitekat"));
	     assertEquals(joyORush,manager.getSubStrategy("JoyORush"));
	     assertEquals(fastCC,manager.getSubStrategy("14CC"));
	}
	
}
```

## Coverage 측정 결과

![jacoco](https://github.com/dbdbais/Ecgberht/assets/99540674/2ab75119-f42b-4ef9-a49c-63149067bda0)

