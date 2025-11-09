package blue.endless.pi.enigma;

import java.util.Optional;

import blue.endless.jankson.api.document.ArrayElement;
import blue.endless.jankson.api.document.ObjectElement;
import blue.endless.pi.enigma.wrapper.DoorInfo;
import blue.endless.pi.enigma.wrapper.RoomInfo;
import blue.endless.pi.enigma.wrapper.ScreenInfo;
import blue.endless.pi.enigma.wrapper.WorldInfo;

public class DoorConnectionLogic {
	
	public static void unstitchAll(WorldInfo world, RoomInfo room) {
		for(ScreenInfo screen : room.screens()) {
			ArrayElement doors = screen.json().getArray("DOORS");
			//System.out.println("Unstitching "+doors.size()+" doors for this screen");
			for(int i=0; i<doors.size(); i++) {
				ObjectElement obj = doors.getObject(i);
				DoorInfo door = new DoorInfo(world, room, screen, obj);
				Optional<DoorInfo> pair = door.getDestination();
				if (pair.isPresent()) {
					unstitch(door, pair.get());
				} else {
					//System.out.println("Pair is not present.");
				}
			}
		}
	}
	
	/**
	 * Unconditionally disconnects the specified doors.
	 */
	public static void unstitch(DoorInfo a, DoorInfo b) {
		downgradeDoorType(a, b);
		downgradeDoorType(b, a);
		a.clearDestination();
		b.clearDestination();
	}
	
	/**
	 * This downgrades the DoorType of toDowngrade, for example by turning missile and boss doors into blue doors. This
	 * defines the behavior that happens when rooms are dragged apart.
	 * @param toDowngrade the door to downgrade the type of
	 * @param oppositeDoor the destination door from toDowngrade
	 */
	public static void downgradeDoorType(DoorInfo toDowngrade, DoorInfo oppositeDoor) {
		//System.out.println("Downgrading door at "+toDowngrade.screen().x()+","+toDowngrade.screen().y()+" dir: "+toDowngrade.direction()+" from "+toDowngrade.type());
		switch(toDowngrade.type()) {
			case BLUE   -> toDowngrade.fixMapType();
			case COMBAT -> toDowngrade.fixMapType();
			case ZERO, MISSILE, BOSS, SUPER_MISSILE, POWER_BOMB -> toDowngrade.setType(DoorType.BLUE);
			
			case IMPASSABLE -> {
				// Impassable can only downgrade if oppositeDoor is in a Mother Brain room
				if (oppositeDoor.room().isBossRoom() && oppositeDoor.room().bossId() == EnemyType.MOTHER_BRAIN_ID) {
					toDowngrade.setType(DoorType.BLUE);
				}
			}
			case MOTHER_BRAIN -> {
				toDowngrade.setType(DoorType.BLUE);
			}
			
			case UNKNOWN_BLUE, UNKNOWN_CYAN -> toDowngrade.fixMapType();
			default -> {}
		}
	}
	
	public static void upgradeDoorType(DoorInfo toUpgrade, DoorInfo oppositeDoor) {
		switch(toUpgrade.type()) {
			case ZERO, BLUE, MISSILE, BOSS, SUPER_MISSILE, POWER_BOMB -> {
				// OK to zap this data.
				if (oppositeDoor.room().isBossRoom()) {
					if (oppositeDoor.room().bossId() == EnemyType.MOTHER_BRAIN_ID) {
						
						switch(oppositeDoor.type()) {
							case ZERO, BLUE, MISSILE, BOSS, SUPER_MISSILE, POWER_BOMB -> {
								// Upgrade most doors to yellow door
								toUpgrade.setType(DoorType.MOTHER_BRAIN);
							}
							case COMBAT -> {
								// We're the exit door into the escape. Become impassable
								toUpgrade.setType(DoorType.IMPASSABLE);
							}
							
							case IMPASSABLE, MOTHER_BRAIN, UNKNOWN_BLUE, UNKNOWN_CYAN -> {}
							default -> {}
						}
						
					} else {
						toUpgrade.setType(DoorType.BOSS);
					}
				}
				
			}
			case COMBAT, IMPASSABLE, MOTHER_BRAIN, UNKNOWN_BLUE, UNKNOWN_CYAN -> {} // Never upgrade
			default -> {}
		}
	}
	
	/**
	 * Tries to stitch the specified doors into a consistent connection. Door types will be adjusted to suit the
	 * circumstances, such as favoring purple doors going into boss rooms.
	 */
	public static boolean stitch(DoorInfo a, DoorInfo b) {
		upgradeDoorType(a, b);
		upgradeDoorType(b, a);
		a.setDestination(b);
		b.setDestination(a);
		
		return true;
	}
}
