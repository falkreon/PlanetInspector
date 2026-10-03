package blue.endless.pi.enigma.domain;

import blue.endless.jankson.api.annotation.SerializedName;

public class ProgressionItem {
	public int x = 0;
	public int y = 0;
	public int sector = 0;
	@SerializedName("room_id")
	public int roomId = 0;
	public int item = 0;
	public int area = 0;
}
