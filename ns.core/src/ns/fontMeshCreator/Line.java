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

package ns.fontMeshCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a line of text during the loading of a text.
 *
 * @author Karl
 */
class Line {

	private final double maxLength;
	private final double spaceSize;

	private final List<Word> words = new ArrayList<>();
	private double currentLineLength = 0;

	/**
	 * Creates an empty line.
	 *
	 * @param spaceWidth - the screen-space width of a space character.
	 * @param fontSize   - the size of font being used.
	 * @param maxLength  - the screen-space maximum length of a line.
	 */
	protected Line(double spaceWidth, double fontSize, double maxLength) {
		this.spaceSize = spaceWidth * fontSize;
		this.maxLength = maxLength;
	}

	/**
	 * Attempt to add a word to the line. If the line can fit the word in
	 * without reaching the maximum line length then the word is added and the
	 * line length increased.
	 *
	 * @param word - the word to try to add.
	 * @return {@code true} if the word has successfully been added to the line.
	 */
	boolean attemptToAddWord(Word word) {
		double additionalLength = word.getWordWidth();
		additionalLength += !words.isEmpty() ? spaceSize : 0;
		if (currentLineLength + additionalLength <= maxLength) {
			words.add(word);
			currentLineLength += additionalLength;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return The max length of the line.
	 */
	protected double getMaxLength() {
		return maxLength;
	}

	/**
	 * @return The current screen-space length of the line.
	 */
	double getLineLength() {
		return currentLineLength;
	}

	/**
	 * @return The list of words in the line.
	 */
	List<Word> getWords() {
		return words;
	}

}
