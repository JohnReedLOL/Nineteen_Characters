package src.map.editor;
import java.awt.Color;
import java.util.UUID;

import src.AddableThingEnum;
import src.Effect;
import src.enumHandler;
import src.model.map.constructs.Avatar;
import src.model.map.constructs.Bow;
import src.model.map.constructs.Item;
import src.model.map.constructs.Merchant;
import src.model.map.constructs.Monster;
import src.model.map.constructs.ObstacleRemovingItem;
import src.model.map.constructs.OneHandedSword;
import src.model.map.constructs.OneShotAreaEffectItem;
import src.model.map.constructs.OneWayTeleportItem;
import src.model.map.constructs.PermanentObstacleItem;
import src.model.map.constructs.Shield;
import src.model.map.constructs.Staff;
import src.model.map.constructs.TemporaryObstacleItem;
import src.model.map.constructs.Terrain;
import src.model.map.constructs.Trap;
import src.model.map.constructs.TwoHandedSword;
import src.model.map.constructs.Villager;
/**
 * Factory to generate things that can be added to the map
 * Public as it could be useful in save/load/run game methods later on.
 * @author mbregg
 *
 */
public class MapAddableFactory {
	private String aveString_;
	public String mostRecentAvatar(){
		if(aveString_!=null){return aveString_;}
		else{return "NO AVATAR ON MAP";}
	}
	public MapAddableFactory() {
		// TODO Auto-generated constructor stub
	}
	public MapAddable getAddable(String addable){
		return getAddable(enumHandler.stringCommandToAddable(addable));
	}
	public MapAddable getAddable(AddableThingEnum addable){
		if(addable == null){return null;}
		switch(addable){
		case MOUNTAIN_TERRAIN:
			Terrain mountain = new Terrain("mountain", '▨', true, false);
			return new TerrainAdder(mountain);
		case GRASS_TERRAIN:
			Terrain grass = new Terrain("grass", '▨', false, false);
			return new TerrainAdder(grass);
		case WATER_TERRAIN:
			Terrain water = new Terrain("water", '▨', false, true);
			return new TerrainAdder(water);
		case WATER_MOUNTAIN_TERRAIN:
			Terrain water_mountain = new Terrain("water-mountain", '▨', true,true);
			return new TerrainAdder(water_mountain);
		case OBSTACLE:
			PermanentObstacleItem obstacle = new PermanentObstacleItem("boulder", '■');
			return new ItemAdder(obstacle);
		case SKULL_DECAL:
			Terrain skull = new Terrain("skull",' ',false,false);
			skull.addDecal('☠',Color.black);
			return new TerrainAdder(skull);
		case GOLD_STAR_DECAL:
			Terrain GoldStar = new Terrain("GoldStar",' ',false,false);
			GoldStar.addDecal('★',Color.yellow);
			return new TerrainAdder(GoldStar);
		case RED_CROSS_DECAL:
			Terrain RedCross = new Terrain("RedCross",' ',false,false);
			RedCross.addDecal('✚',Color.red);
			return new TerrainAdder(RedCross);
		case VILLAGER_ENTITY:
			Villager villagerA = new Villager("villager1", '웃');
			villagerA.getStatsPack().increaseQuantityOfExperienceBy(200);
			return new EntityAdder(villagerA);
		case TRADER_ENTITY:
			Merchant merchant = new Merchant("merchant1", '웃');
			merchant.getStatsPack().increaseQuantityOfExperienceBy(1000);
			return new EntityAdder(merchant);
		case MONSTER_ENTITY:
			Monster monster = new Monster("monster1", '웃');
			monster.getStatsPack().increaseQuantityOfExperienceBy(300);
			return new EntityAdder(monster);
		case AVATAR_ENTITY:
			aveString_ = UUID.randomUUID().toString();//We use a unique name for each avatar.
			Avatar buddy = new Avatar(aveString_, '웃');
			return new AvatarAdder(buddy);
		case HURT_EFFECT_ITEM:
			OneShotAreaEffectItem heal = new OneShotAreaEffectItem("healer", 'h', Effect.HEAL, 10);
			return new ItemAdder(heal);
		case HEAL_EFFECT_ITEM:
			OneShotAreaEffectItem hurt = new OneShotAreaEffectItem("hurter", 'u', Effect.HURT, 10);
			return new ItemAdder(hurt);
		case LEVEL_UP_EFFECT_ITEM:
			OneShotAreaEffectItem level = new OneShotAreaEffectItem("leveler", 'l', Effect.LEVEL, 10);
			return new ItemAdder(level);
		case KILL_EFFECT_ENUM:
			OneShotAreaEffectItem kill = new OneShotAreaEffectItem("killer", 'k', Effect.KILL, 10);
			return new ItemAdder(kill);
		case SHIELD_ITEM:
			Item shield = new Shield("Shieldy",'O');
			return new ItemAdder(shield);
		case SWORD_ITEM:
			Item onehandedsword = new OneHandedSword("Excalibur", '|');
			return new ItemAdder(onehandedsword);
		case TWO_HAND_SWORD_ITEM:
			Item twohandedsword = new TwoHandedSword("Two_hander", '|');
			return new ItemAdder(twohandedsword);
		case BOW_ITEM:
			Item bow = new Bow("Bow",'D');
			return new ItemAdder(bow);
		case STAFF_ITEM:
			Item staff = new Staff("Staff",'i');
			return new ItemAdder(staff);
		case DOOR_KEY_ITEM:
			ObstacleRemovingItem key = new ObstacleRemovingItem("Key", 'K');
			TemporaryObstacleItem door = new TemporaryObstacleItem("Door", 'D', key);
			return new DoorKeyAdder(key, door);
		case TELEPORT_ITEM:
			Item teleport = new OneWayTeleportItem("tele", 'T', 0, 0);
			return new ItemAdder(teleport);
		case HEAL_TRAP:
			Trap trapHeal = new Trap("trapheal", 'b', Effect.HEAL, 2);
			return new ItemAdder(trapHeal);
		case HURT_TRAP:
			Trap trapHurt = new Trap("traphurt", 'b', Effect.HURT, 2);
			return new ItemAdder(trapHurt);
		case KILL_TRAP:
			Trap trapKill = new Trap("trapkill", 'b', Effect.KILL, 2);
			return new ItemAdder(trapKill);
		case LEVELUP_TRAP:
			Trap trapLevel = new Trap("trapLevel",'b',Effect.LEVEL,2);
			return new ItemAdder(trapLevel);

		default: return null;
		}
	}

}
