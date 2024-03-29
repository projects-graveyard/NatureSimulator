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

package ns.copyGameData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

class CopyGameData {

	public static void main(String[] args) {
		copy(new File(System.getProperty("user.dir") + "/gameData"));
	}

	private static void copy(File file) {
		File target = new File(file.getAbsolutePath().replace("NatureSimulator/gameData/",
				"NS_Installer/src/ns/mainEngine/install/gameData/"));
		if (file.isDirectory()) {
			target.mkdir();
			for (File f : Objects.requireNonNull(file.listFiles()))
				copy(f);
		} else {
			try {
				FileInputStream fin = new FileInputStream(file);
				FileOutputStream fout = new FileOutputStream(target);
				fout.write(fin.readAllBytes());
				fin.close();
				fout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}