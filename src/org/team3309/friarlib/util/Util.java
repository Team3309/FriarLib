package org.team3309.friarlib.util;

import java.util.Vector;

public class Util {

	/**
	 * Returns the array of substrings obtained by dividing the given input
	 * string at each occurrence of the given delimiter.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String[] split(String input, String delimiter) {
		Vector node = new Vector();
		int index = input.indexOf(delimiter);
		while (index >= 0) {
			node.addElement(input.substring(0, index));
			input = input.substring(index + delimiter.length());
			index = input.indexOf(delimiter);
		}
		node.addElement(input);

		String[] retString = new String[node.size()];
		for (int i = 0; i < node.size(); ++i) {
			retString[i] = (String) node.elementAt(i);
		}

		return retString;
	}
}
