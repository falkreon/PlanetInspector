package blue.endless.pi.enigma.wrapper;
import java.util.Optional;

import blue.endless.pi.enigma.Direction;

public record PlacedScreen(RoomInfo room, ScreenInfo screen) {
	public Optional<ElevatorInfo> getElevator(Direction d) {
		return screen.getElevator(d).map((it) -> new ElevatorInfo(room, screen, it));
	}
}
