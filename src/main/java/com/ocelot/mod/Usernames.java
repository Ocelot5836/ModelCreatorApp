package com.ocelot.mod;

import java.io.StringReader;
import java.lang.reflect.Field;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mrcrayfish.device.api.utils.OnlineRequest;

/**
 * Contains some useful usernames of players from their UUID.
 * 
 * @author Ocelot5836
 */
public class Usernames {

	/** Ocelot5836's UUID */
	public static final String OCELOT5836_UUID = "86dc8a9f238e450280211d488095fd8a";
	/** MrCrayfish's UUID */
	public static final String MR_CRAYFISH_UUID = "62d17f0b524841f4befc2daa457fb266";

	/** Ocelot5836's Username from UUID */
	private static String ocelot5836 = "Ocelot5836";
	/** MrCrayfish's Username from UUID */
	private static String mrCrayfish = "MrCrayfish";

	public static void init() {
		getUsername(OCELOT5836_UUID, "ocelot5836");
		getUsername(MR_CRAYFISH_UUID, "mrCrayfish");
	}

	private static void getUsername(String uuid, String fieldName) {
		String url = "https://api.mojang.com/user/profiles/" + uuid.replace("-", "") + "/names";
		new Thread(() -> {
			OnlineRequest.getInstance().make(url, (success, response) -> {
				try {
					String username = "Player";
					if (success) {
						JsonReader reader = new JsonReader(new StringReader(response));
						JsonParser parser = new JsonParser();
						JsonArray json = parser.parse(reader).getAsJsonArray();
						username = json.get(json.size() - 1).getAsJsonObject().get("name").getAsString();
						reader.close();
					}

					Field field = Usernames.class.getDeclaredField(fieldName);
					field.set(null, username);
				} catch (Exception e) {
					ModelCreator.logger().error("Could not load username for uuid \'" + uuid + "\' from URL \'" + url + "\'", e);
				}
			});
		}).start();
	}

	public static String getOcelot5836Username() {
		return ocelot5836;
	}

	public static String getMrCrayfishUsername() {
		return mrCrayfish;
	}
}