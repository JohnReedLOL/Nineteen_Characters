package src.map.editor;
import java.awt.Color;
import java.util.UUID;

import src.AddableThingEnum;
import src.Effect;
import src.enumHandler;
import src.model.constructs.Avatar;
import src.model.constructs.Merchant;
import src.model.constructs.Monster;
import src.model.constructs.Terrain;
import src.model.constructs.Villager;
import src.model.constructs.items.Bow;
import src.model.constructs.items.InvisibilitySerum;
import src.model.constructs.items.Item;
import src.model.constructs.items.KnightsSerum;
import src.model.constructs.items.ObstacleRemovingItem;
import src.model.constructs.items.OneHandedSword;
import src.model.constructs.items.OneShotAreaEffectItem;
import src.model.constructs.items.OneWayTeleportItem;
import src.model.constructs.items.PermanentObstacleItem;
import src.model.constructs.items.Shield;
import src.model.constructs.items.Staff;
import src.model.constructs.items.TemporaryObstacleItem;
import src.model.constructs.items.Trap;
import src.model.constructs.items.TwoHandedSword;
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
			Villager villagerA = new Villager("villager1", '♙');
			villagerA.getStatsPack().increaseQuantityOfExperienceBy(200);
			return new EntityAdder(villagerA);
		case TRADER_ENTITY:
			Merchant merchant = new Merchant("merchant1", '♖');
			merchant.getStatsPack().increaseQuantityOfExperienceBy(1000);
			return new EntityAdder(merchant);
		case MONSTER_ENTITY:
			Monster monster = new Monster("monster1", '♟');
			monster.getStatsPack().increaseQuantityOfExperienceBy(300);
			return new EntityAdder(monster);
		case AVATAR_ENTITY:
			aveString_ = UUID.randomUUID().toString();//We use a unique name for each avatar.
			Avatar buddy = new Avatar(aveString_, '♔');
			return new AvatarAdder(buddy);
		case KNIGHT_ENTITY:
			aveString_ = UUID.randomUUID().toString();//We use a unique name for each avatar.
			Avatar knight_buddy = new Avatar(aveString_, '♘');
			return new KnightAvatarAdder(knight_buddy);
		case FLIGHT_ENTITY:
			aveString_ = UUID.randomUUID().toString();//We use a unique name for each avatar.
			Avatar flight_buddy = new Avatar(aveString_, '♕');
			return new FlightAvatarAdder(flight_buddy);
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
			OneWayTeleportItem teleport = new OneWayTeleportItem("tele", 'T', 0, 0);
			return new TeleportAdder(teleport);
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
		case FLIGHT_POTION:
			InvisibilitySerum flying_serum = new InvisibilitySerum("Invisibility Serum", 'I');
			return new ItemAdder(flying_serum);
		case KNIGHT_POTION:
			KnightsSerum knight_serum = new KnightsSerum("Knight serum", 'N');
			return new ItemAdder(knight_serum);

		default: return null;
		}
	}

}
