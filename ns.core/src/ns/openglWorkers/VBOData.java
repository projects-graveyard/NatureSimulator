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

package ns.openglWorkers;

import ns.openglObjects.VBO;
import ns.utils.GU;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

public class VBOData {
	private final float[] dataf;
	private final int[] datai;
	private final byte[] datab;

	private final int type;
	private final int gl_type;
	private int attNumber = -1;
	private boolean isIndices;
	private int dimensions;
	private int usage = GL15.GL_STATIC_DRAW;

	public VBOData(float[] data) {
		dataf = data;
		datai = null;
		datab = null;
		type = 0;
		gl_type = GL11.GL_FLOAT;
	}

	public VBOData(int[] data) {
		dataf = null;
		datai = data;
		datab = null;
		type = 1;
		gl_type = GL11.GL_INT;
	}

	public VBOData(byte[] data) {
		dataf = null;
		datai = null;
		datab = data;
		type = 2;
		gl_type = GL11.GL_BYTE;
	}

	public VBOData withUsage(int usage) {
		this.usage = usage;
		return this;
	}

	public VBOData withDimensions(int dimensions) {
		this.dimensions = dimensions;
		return this;
	}

	public VBOData isIndices(boolean isIndices) {
		this.isIndices = isIndices;
		return this;
	}

	public VBOData withAttributeNumber(int attributeNumber) {
		this.attNumber = attributeNumber;
		return this;
	}

	public void store(VBO vbo) {
		int type = (isIndices ? GL15.GL_ELEMENT_ARRAY_BUFFER : GL15.GL_ARRAY_BUFFER);
		GL15.glBindBuffer(type, vbo.getId());
		if (this.type == 0) {
			FloatBuffer data = storeDataInFloatBuffer(Objects.requireNonNull(dataf));
			GL15.glBufferData(type, data, usage);
			data.clear();
		} else if (this.type == 1) {
			IntBuffer data = storeDataInIntBuffer(Objects.requireNonNull(datai));
			GL15.glBufferData(type, data, usage);
			data.clear();
		} else if (this.type == 2) {
			ByteBuffer data = storeDataInByteBuffer(Objects.requireNonNull(datab));
			GL15.glBufferData(type, data, usage);
			data.clear();
		}
		if (type == GL15.GL_ARRAY_BUFFER) {
			GL20.glVertexAttribPointer(attNumber, dimensions, this.gl_type, false, 0, 0);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		}
	}

	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		return BufferUtils.createFloatBuffer(data.length).put(data).flip();
	}

	private IntBuffer storeDataInIntBuffer(int[] data) {
		return BufferUtils.createIntBuffer(data.length).put(data).flip();
	}

	private ByteBuffer storeDataInByteBuffer(byte[] data) {
		return BufferUtils.createByteBuffer(data.length).put(data).flip();
	}

	public boolean isIndices() {
		return isIndices;
	}

	public int getLength() {
		switch (type) {
			case 0:
				return Objects.requireNonNull(dataf).length;
			case 1:
				return Objects.requireNonNull(datai).length;
			case 2:
				return Objects.requireNonNull(datab).length;
		}
		System.out.println("Type " + type + " not found");
		return 0;
	}

	public int getDimensions() {
		return dimensions;
	}

	public int getAttributeNumber() {
		return attNumber;
	}

	public int getUsage() {
		return usage;
	}

	public int getStride() {
		return dimensions * GU.sizeof(gl_type);
	}

	public float[] getDataf() {
		return dataf;
	}

	public int[] getDatai() {
		return datai;
	}

	public byte[] getDatab() {
		return datab;
	}

	public Number[] getData() {
		Number[] arr = (dataf != null ? cast(dataf) : (datai != null ? cast(datai) : cast(datab)));
		System.out.println(arr.length);
		return arr;
	}

	private Number[] cast(float[] data) {
		Number[] array = new Number[data.length];
		for (int i = 0; i < array.length; i++)
			array[i] = data[i];
		return array;
	}

	private Number[] cast(int[] data) {
		Number[] array = new Number[data.length];
		for (int i = 0; i < array.length; i++)
			array[i] = data[i];
		return array;
	}

	private Number[] cast(byte[] data) {
		Number[] array = new Number[data.length];
		for (int i = 0; i < array.length; i++)
			array[i] = data[i];
		return array;
	}
}
