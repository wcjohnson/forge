package shandalike.data;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import forge.StaticData;
import forge.deck.CardPool;
import forge.item.IPaperCard;
import forge.item.PaperCard;
import forge.util.FileUtil;
import shandalike.data.behavior.Behavior;
import shandalike.data.behavior.Behaviors;
import shandalike.data.effects.Effect;
import shandalike.data.effects.chain;
import shandalike.data.effects.debugPrint;
import shandalike.data.effects.enterMap;
import shandalike.data.effects.exitMap;
import shandalike.data.effects.script;
import shandalike.data.effects.selfDestruct;
import shandalike.data.entity.CollidablePawn;
import shandalike.data.entity.Timer;
import shandalike.data.entity.Entity;
import shandalike.data.entity.MobilePawn;
import shandalike.data.entity.Pawn;
import shandalike.data.entity.PlayerPawn;
import shandalike.data.entity.PointOfInterest;
import shandalike.data.entity.Positional;
import shandalike.data.entity.town.Town;

public class JSON {
	public static Gson gson;
	
	@SuppressWarnings("serial")
	public static class Template extends HashMap<String, Object> {
		
	}
	@SuppressWarnings("serial")
	public static class Templates extends HashMap<String, Template> {

	}
	
	
	private static void reifyTemplate(Templates templates, Template template, String templateName) {
		// Get the inherited template
		String templateInheritsName = null;
		try {
			templateInheritsName = (String)template.get("inherits");
		} catch(Exception e) {
			return;
		}
		// If no inherited template, no modification needed
		if(templateInheritsName == null) return;
		// Verify inherited template exists
		Template inheritedTemplate = templates.get(templateInheritsName);
		if(inheritedTemplate == null) {
			throw new RuntimeException(templateName + " inherits from missing template " + templateInheritsName);
		}
		// Reify the inherited template
		reifyTemplate(templates, inheritedTemplate, templateInheritsName);
		// Copy all missing keys from the inherited template onto this template
		for(Map.Entry<String, Object> entry: inheritedTemplate.entrySet())
			if(!template.containsKey(entry.getKey()))
				template.put(entry.getKey(), entry.getValue());
		// Remove the inherits key from this template
		template.remove("inherits");
	}
	
	public static Templates templatesFromJson(String json, Templates parentTemplates) {
		// Get current templates from file
		Templates currentTemplates = gson.fromJson(json, Templates.class);
		// Merge-in parent templates if they exist
		if(parentTemplates != null) {
			for(Entry<String, Template> e: parentTemplates.entrySet()) {
				currentTemplates.put(e.getKey(), e.getValue());
			}
		}
		// Reify the templates
		for(Entry<String, Template> e: currentTemplates.entrySet()) {
			reifyTemplate(currentTemplates, e.getValue(), e.getKey());
		}
		// Return templates
		return currentTemplates;
	}
	
	private static class Vec2Serializer implements JsonSerializer<Vector2> {
		@Override
		public JsonElement serialize(Vector2 src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}
	}
	private static class Vec2Deserializer implements JsonDeserializer<Vector2> {
		@Override
		public Vector2 deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			Vector2 vec = new Vector2();
			return vec.fromString(json.getAsString());
		}		
	}
	
	private static class RectangleSerializer implements JsonSerializer<Rectangle> {
		@Override
		public JsonElement serialize(Rectangle src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}
	}
	private static class RectangleDeserializer implements JsonDeserializer<Rectangle> {
		@Override
		public Rectangle deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			Rectangle vec = new Rectangle();
			return vec.fromString(json.getAsString());
		}		
	}
	
	private static class PaperCardSerializer implements JsonSerializer<PaperCard> {
		@Override
		public JsonElement serialize(PaperCard src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(
				src.getName() + "|" + src.getEdition() + "|" + src.getArtIndex() + "|" + (src.isFoil() ? 1 : 0)
			);
		}
	}
	private static class PaperCardDeserializer implements JsonDeserializer<PaperCard> {
		@Override
		public PaperCard deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			String[] part = json.getAsString().split("\\|");
			int artIndex = Integer.parseInt(part[2]);
			boolean isFoil = (part[3] == "1" ? true : false);
			
			IPaperCard pc = StaticData.instance().getCommonCards().getCard(part[0], part[1], artIndex);
			if(pc == null) throw new RuntimeException(String.format("Card %s not found", part[0]));
			
			return new PaperCard(pc.getRules(), part[1], pc.getRarity(), artIndex, isFoil);
		}
	}
	
	private static class CardPoolSerializer implements JsonSerializer<CardPool> {
		@Override
		public JsonElement serialize(CardPool src, Type typeOfSrc, JsonSerializationContext context) {
			JsonArray pool = new JsonArray();
			for(Entry<PaperCard, Integer> e: src.getItems().entrySet()) {
				JsonArray entry = new JsonArray();
				entry.add(e.getValue());
				entry.add(context.serialize(e.getKey(), PaperCard.class));
				pool.add(entry);
			}
			return pool;
		}
	}
	private static class CardPoolDeserializer implements JsonDeserializer<CardPool> {
		@Override
		public CardPool deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			CardPool pool = new CardPool();
			JsonArray entries = json.getAsJsonArray();
			for(JsonElement entry: entries) {
				int qty = entry.getAsJsonArray().get(0).getAsInt();
				PaperCard pc = context.deserialize(entry.getAsJsonArray().get(1), PaperCard.class);
				pool.add(pc, qty);
			}
			return pool;
		}
	}
	
	private static class BehaviorsSerializer implements JsonSerializer<Behaviors>, JsonDeserializer<Behaviors> {
		@Override
		public JsonElement serialize(Behaviors src, Type arg1, JsonSerializationContext context) {
			JsonArray out = new JsonArray();
			for(Behavior beh: src) {
				out.add(context.serialize(beh, Behavior.class));
			}
			return out;
		}

		@Override
		public Behaviors deserialize(JsonElement json, Type arg1, JsonDeserializationContext context)
				throws JsonParseException {
			Behaviors behaviors = new Behaviors();
			JsonArray entries = json.getAsJsonArray();
			for(JsonElement entry: entries) {
				Behavior beh = context.deserialize(entry, Behavior.class);
				behaviors.add(beh);
			}
			return behaviors;
		}
	}
	
	static {
		RuntimeTypeAdapterFactory<Entity> entityAdapter = RuntimeTypeAdapterFactory.of(Entity.class)
				.registerSubtype(Positional.class)
				.registerSubtype(Pawn.class)
				.registerSubtype(PlayerPawn.class)
				.registerSubtype(MobilePawn.class)
				.registerSubtype(CollidablePawn.class)
				.registerSubtype(PointOfInterest.class)
				.registerSubtype(Town.class)
				.registerSubtype(Timer.class);
		
		RuntimeTypeAdapterFactory<Effect> effectAdapter = RuntimeTypeAdapterFactory.of(Effect.class, "effect")
				.registerSubtype(chain.class)
				.registerSubtype(debugPrint.class)
				.registerSubtype(enterMap.class)
				.registerSubtype(exitMap.class)
				.registerSubtype(script.class)
				.registerSubtype(selfDestruct.class);
		
		gson = new GsonBuilder()
				.registerTypeAdapterFactory(entityAdapter)
				.registerTypeAdapterFactory(effectAdapter)
				.registerTypeAdapter(Vector2.class, new Vec2Serializer())
				.registerTypeAdapter(Vector2.class, new Vec2Deserializer())
				.registerTypeAdapter(Rectangle.class, new RectangleSerializer())
				.registerTypeAdapter(Rectangle.class, new RectangleDeserializer())
				.registerTypeAdapter(PaperCard.class, new PaperCardSerializer())
				.registerTypeAdapter(PaperCard.class, new PaperCardDeserializer())
				.registerTypeAdapter(CardPool.class, new CardPoolSerializer())
				.registerTypeAdapter(CardPool.class, new CardPoolDeserializer())
				.registerTypeAdapter(Behaviors.class, new BehaviorsSerializer())
				.setPrettyPrinting()
				.create();		
	}
	
	public static String loadFile(File f) {
		String json = null;
		try {
			json = FileUtil.readFileToString(f);
			if(json == null) return null;
		} catch(Exception e) {
			return null;
		}
		return json;
	}
	
	public static String loadFile(String fn) {
		return loadFile(new File(fn));
	}
	
	public static <T> T loadJson(File f, Class<T> clazz) {
		String json = loadFile(f);
		if(json == null) return null;
		return gson.fromJson(json, clazz);
	}
	
	public static <T> T loadJson(String fn, Class<T> clazz) {
		return loadJson(new File(fn), clazz);
	}
	
	public static <T> T loadJsonGeneric(File f, Type ty) {
		String json = loadFile(f);
		return gson.fromJson(json, ty);
	}
	
	public static <T> T loadJsonGeneric(String fn, Type ty) {
		return loadJsonGeneric(new File(fn), ty);
	}
	
	public static <T> T fromJson(String json, Class<T> clazz) {
		return gson.fromJson(json, clazz);
	}
	
	public static <T> T fromJson(String json, Type ty) {
		return gson.fromJson(json, ty);
	}
	
	public static Map<String,Object> fromJsonObject(String json) {
		if(json == null) return null;
		return gson.fromJson(
				json, 
				new TypeToken<Map<String,Object>>(){}.getType()
			);
	}
	
	public static String toJson(Object obj) {
		return gson.toJson(obj);
	}
	public static <T> String toJson(T obj, Class<T> clazz) {
		return gson.toJson(obj, clazz);
	}
}
