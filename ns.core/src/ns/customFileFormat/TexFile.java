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

package ns.customFileFormat;

import data.GameData;
import ns.exceptions.CorruptException;
import ns.exceptions.LoadingException;
import ns.openglObjects.Texture;
import ns.parallelComputing.TextureCreateRequest;
import ns.utils.GU;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import resources.In;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TexFile implements File {
	private final String location;

	public TexFile(String location) {
		this.location = location;
	}

	@Override
	public Texture load() throws LoadingException {
		In resource = GameData.getResourceAt(location, true);
		int id = 0;
		int width = 0;
		int height = 0;
		try {
			ByteBuffer pixels = null;
			if (resource.version() == 1) {
				BufferedReader reader = GU.open(resource);
				String line = reader.readLine();
				String[] pts = line.split(" ");
				width = Integer.parseInt(pts[0]);
				height = Integer.parseInt(pts[1]);
				pixels = BufferUtils.createByteBuffer(width * height * 4);
				for (int y = height - 1; y >= 0; y--) {
					line = reader.readLine();
					pts = line.split(" ");
					for (int x = 0; x < width; x++) {
						try {
							int pixel = Integer.parseInt(pts[x]);
							pixels.put((byte) ((pixel >> 16) & 0xFF));
							pixels.put((byte) ((pixel >> 8) & 0xFF));
							pixels.put((byte) (pixel & 0xFF));
							pixels.put((byte) ((pixel >> 24) & 0xFF));
						} catch (NumberFormatException e) {
							throw new CorruptException("File at " + location + " is corrupt(found: \"" + pts[x]
									+ "\" while expecting an int)");
						}
					}
				}
			} else if (resource.version() == 2) {
				InputStream is = resource.asInputStream();
				List<Byte> bytes = new ArrayList<>();
				while (true) {
					int in = is.read();
					if (in == 126)
						break;
					bytes.add((byte) in);
				}
				String line = "";
				for (int i = 0; i < bytes.size(); i++)
					line += (char) (byte) bytes.get(i);

				String[] pts = line.split(" ");
				width = Integer.parseInt(pts[0]);
				height = Integer.parseInt(pts[1]);

				pixels = BufferUtils.createByteBuffer(width * height * 4);
				for (int i = 0; i < width * height * 4; i++) {
					int val = is.read();
					pixels.put((byte) val);
				}
			} else if (resource.version() == 3) {
				InputStream is = resource.asInputStream();
				byte mb = (byte) is.read();
				boolean tbmi = false; // Two bites multiplication indicator
				if (mb == -1) {
					tbmi = true;
				}
				byte[] sizeData = new byte[8];
				is.read(sizeData, 0, 8);
				width = GU.readInt(sizeData[0], sizeData[1], sizeData[2], sizeData[3]);
				height = GU.readInt(sizeData[4], sizeData[5], sizeData[6], sizeData[7]);
				byte[] data = is.readAllBytes();
				byte[] toBufferData = new byte[4 * width * height];
				int toBufferDataPtr = 0;
				for (int i = 0; i < data.length; i++) {
					if ((!tbmi && data[i] == mb) || (tbmi && data[i] == 1 && data[i + 1] == 0)) {
						int mult = Byte.toUnsignedInt(tbmi ? data[i + 2] : data[i + 1]);
						for (int j = 1; j < mult; j++) {
							toBufferData[toBufferDataPtr++] = data[i - 4];
							toBufferData[toBufferDataPtr++] = data[i - 3];
							toBufferData[toBufferDataPtr++] = data[i - 2];
							toBufferData[toBufferDataPtr++] = data[i - 1];
						}
						i += (tbmi ? 2 : 1);
					} else
						toBufferData[toBufferDataPtr++] = data[i];
				}
				pixels = BufferUtils.createByteBuffer(4 * width * height);
				pixels.put(toBufferData);
			} else if (resource.version() == 4) {
				InputStream is = resource.asInputStream();
				byte[] sizeData = new byte[8];
				is.read(sizeData, 0, 8);
				width = GU.readInt(sizeData[0], sizeData[1], sizeData[2], sizeData[3]);
				height = GU.readInt(sizeData[4], sizeData[5], sizeData[6], sizeData[7]);

				pixels = BufferUtils.createByteBuffer(width * height * 4);
				for (int i = 0; i < width * height * 4; i++) {
					int val = is.read();
					pixels.put((byte) val);
				}
			}
			Objects.requireNonNull(pixels).flip();

			if (Thread.currentThread().getName().equals(GU.MAIN_THREAD_NAME)) {
				id = GL11.glGenTextures();
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA,
						GL11.GL_UNSIGNED_BYTE, pixels);

				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			} else {
				Texture target = new Texture(0, width, height);
				GU.sendRequestToMainThread(new TextureCreateRequest(target, pixels));
				return target;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (id == 0 ? null : new Texture(id, width, height));
	}
}