/*
 * Copyright (C) 2018-2019  Dinu Blanovschi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ns.utils;

import ns.camera.ICamera;
import ns.terrain.Terrain;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class MousePicker {
	private static ICamera camera;

	private static Matrix4f projectionMatrix;
	private static Terrain terrain;
	private static Matrix4f viewMatrix;
	private static Vector3f currentRay = new Vector3f();
	private static Vector3f currentTerrainPoint;

	public static void init(ICamera cam, Terrain terrain) {
		camera = cam;
		projectionMatrix = cam.getProjectionMatrix();
		viewMatrix = Maths.createViewMatrix(camera);
		MousePicker.terrain = terrain;
	}

	private static Vector3f calculateMouseRay() {
		Vector2f normalizedCoords = GU.normalizedMousePos();
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		return toWorldCoords(eyeCoords);
	}

	private static Vector3f calculateTerrainPos() {
		Vector3f pos = new Vector3f(camera.getPosition());
		while (Math.abs(pos.x) < Terrain.SIZE / 2f && Math.abs(pos.z) < Terrain.SIZE / 2f && terrain.getHeight(pos.x, pos.z) < pos.y)
			Vector3f.add(pos, currentRay, pos);
		return pos;
	}

	public static Vector3f getCurrentRay() {
		return currentRay;
	}

	public static Vector3f getCurrentTerrainPoint() {
		return currentTerrainPoint;
	}

	private static Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
		Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}

	private static Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalise();
		return mouseRay;
	}

	public static void update() {
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calculateMouseRay();
		currentTerrainPoint = calculateTerrainPos();
	}
}