package ns.components;

import ns.entities.Entity;
import ns.openglObjects.VAO;
import ns.world.World;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blueprint implements Serializable {

	private static final int MODEL = 0;
	private static final int BIOME_SPREAD = 1;
	private static final int MOVEMENT = 2;
	private static final int CUSTOM_COLORS = 3;
	private static final int FOOD = 4;
	private static final int LIFE = 5;

	private final Map<Integer, IComponent> components;
	private final String objectName;

	protected Blueprint(String objectName) {
		this(objectName, new HashMap<>());
	}

	public Blueprint(String objectName, Map<Integer, IComponent> components) {
		this.objectName = objectName;
		this.components = components;
	}

	private Blueprint withComponent(int id, IComponent c) {
		components.put(id, c);
		return this;
	}

	Blueprint withBiomeSpread(BiomeSpreadComponent c) {
		return withComponent(BIOME_SPREAD, c);
	}

	public BiomeSpreadComponent getBiomeSpread() {
		return (BiomeSpreadComponent) components.get(BIOME_SPREAD);
	}

	Blueprint withModel(ModelComponent c) {
		return withComponent(MODEL, c);
	}

	public ModelComponent getModel() {
		return (ModelComponent) components.get(MODEL);
	}

	Blueprint withMovement(MovementComponent c) {
		return withComponent(MOVEMENT, c);
	}

	private MovementComponent getMovement() {
		return (MovementComponent) components.get(MOVEMENT);
	}

	void withFoodComponent(FoodComponent component) {
		withComponent(FOOD, component);
	}

	public void move(Entity e, World w, boolean ableToMove) {
		MovementComponent moveC = getMovement();
		if (moveC != null) {
			moveC.update(e.getPosition(), e, this, w, ableToMove);
		}
	}

	Blueprint withCustomColors(CustomColorsComponent c) {
		return withComponent(CUSTOM_COLORS, c);
	}

	public CustomColorsComponent getCustomColors() {
		return (CustomColorsComponent) components.get(CUSTOM_COLORS);
	}

	public String getFolder() {
		return objectName;
	}

	public Blueprint withDefaultCustomColors() {
		List<Vector3f> cc = new ArrayList<>();
		if (objectName.equals("1000") || objectName.equals("1001") || objectName.equals("1002")
				|| objectName.equals("1006") || objectName.equals("1010")) {
			cc.add(new Vector3f(0f, 0.6f, 0f));
		} else if (objectName.equals("1004") || objectName.equals("1005")) {
			cc.add(new Vector3f(0.002494f, 0.350834f, 0.000000f));
			cc.add(new Vector3f(0.000000f, 0.307499f, 0.002174f));
		}
		return this.withCustomColors(new CustomColorsComponent(cc));
	}

	public int flags(int i) {
		switch (i) {
			case 0: // Model
				return Integer.parseInt(objectName) - 999;
			case 1: // Movement and biome spread 1
				MovementComponent mvm = getMovement();
				int front2 = 0;
				if (mvm != null)
					front2 = mvm.getConfig();
				BiomeSpreadComponent comp = getBiomeSpread();
				int back = 0;
				if (comp != null)
					back = comp.getBiome().getId();
				return (front2 << 4) | back;
			case 2: // Biome spread part 2
				BiomeSpreadComponent comp2 = getBiomeSpread();
				return comp2 != null ? (int) comp2.getMinMax().x : 0;
			case 3: // Biome spread part 3
				BiomeSpreadComponent comp3 = getBiomeSpread();
				return comp3 != null ? (int) comp3.getMinMax().y : 0;
			case 4: // Life part 1
				LifeComponent comp4 = getLifeComponent();
				return comp4 != null ? (((int) comp4.getRemainingLifespan()) & 0xFF00) >> 8 : 0;
			case 5: // Life part 2
				LifeComponent comp5 = getLifeComponent();
				return comp5 != null ? ((int) comp5.getRemainingLifespan()) & 0xFF : 0;
			case 6: // Life part 3
				LifeComponent comp6 = getLifeComponent();
				return comp6 != null ? (comp6 instanceof AnimalLifeComponent ?
						0x40 + (int) (((AnimalLifeComponent) comp6).REPR_TIME / 60f) : 0) & 0x3F : 0;
			default:
				return 0;
		}
	}

	public void writeTo(ObjectOutputStream out) throws IOException {
//		for (int i = 0; i < 7; i++) {
//			out.writeByte(flags(i));
//		}
//		if (getLifeComponent() instanceof AnimalLifeComponent)
//			out.writeObject(((AnimalLifeComponent) getLifeComponent()).getDna());
		out.writeObject(this);
	}

	public FoodComponent getFoodComponent() {
		return (FoodComponent) components.get(FOOD);
	}

	public LifeComponent getLifeComponent() {
		return (LifeComponent) components.get(LIFE);
	}

	void withLife(LifeComponent component) {
		withComponent(LIFE, component);
	}

	public void setMovementTarget(Vector3f target) {
		MovementComponent movementComponent = getMovement();
		if (movementComponent != null)
			movementComponent.setTarget(target);
	}

	public Blueprint copy() {
		Map<Integer, IComponent> componentMap = new HashMap<>();
		for (int i : components.keySet()) {
			componentMap.put(i, components.get(i).copy());
		}
		return new Blueprint(objectName, components);
	}

	public void setModel(VAO model) {
		this.getModel().setModel(model);
	}

	public boolean withinLimits(float height) {
		LifeComponent c = getLifeComponent();
		if (c != null && c instanceof AnimalLifeComponent) {
			Vector2f v =
					((AnimalLifeComponent) c).getDna().getHeightLimits();
			return height >= v.x && height <= v.y;
		} else return true;
	}
}