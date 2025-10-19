package blue.endless.pi;
import java.util.Optional;

import blue.endless.pi.gui.ElevatorInfo;
import blue.endless.pi.gui.RoomInfo;
import blue.endless.pi.gui.ScreenInfo;

public record PlacedScreen(RoomInfo room, ScreenInfo screen) {
	public Optional<ElevatorInfo> getElevator(Direction d) {
		return screen.getElevator(d).map((it) -> new ElevatorInfo(room, screen, it));
	}
}
