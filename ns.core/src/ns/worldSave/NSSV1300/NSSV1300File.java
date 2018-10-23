package ns.worldSave.NSSV1300;

import ns.entities.Entity;
import ns.rivers.RiverList;
import ns.terrain.Terrain;
import ns.utils.GU;
import ns.world.World;
import ns.worldSave.EndObject;
import ns.worldSave.EntityData;
import ns.worldSave.NSSVFile;
import ns.worldSave.TerrainData;
import resources.In;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NSSV1300File extends NSSVFile {

	public NSSV1300File(In resource) {
		super(resource);
	}

	@Override
	protected World load(InputStream ins) {
		World world = null;
		try {
			ObjectInputStream stream = new ObjectInputStream(ins);
			Object o;
			List<Entity> entities = new ArrayList<>();
			Terrain terrain = null;
			RiverList rivers = new RiverList();
			while (true) {
				o = stream.readObject();
				if (o instanceof EntityData)
					entities.add(((EntityData) o).asInstance());
				else if (o instanceof TerrainData)
					terrain = ((TerrainData) o).asInstance();
				else if (o instanceof RiverList)
					rivers = (RiverList) o;
				else if (o instanceof EndObject)
					break;
			}
			world = new World(entities, Objects.requireNonNull(terrain));
			GU.time = ((EndObject) o).getGuTime();
			world.setRivers(rivers);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return world;
	}
}