package org.team3309.friarlib.util;

import com.sun.squawk.microedition.io.FileConnection;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.Connector;

/**
 * This class is from Team 254's library.
 * Manages constant values used everywhere
 * in the robot code.
 * 
 * Copyright (c) 2013, Team 254 All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * @author brandon.gonzalez.451@gmail.com (Brandon Gonzalez)
 */
public abstract class ConstantsBase {
	@SuppressWarnings("rawtypes")
	private static final Vector constants = new Vector();
	private static final String CONSTANTS_FILE_PATH = "Constants.txt";

	/**
	 * Reads the constants file and overrides the values in this class for any
	 * constants it contains.
	 */
	public static void readConstantsFromFile() {
		DataInputStream constantsStream;
		FileConnection constantsFile;
		byte[] buffer = new byte[255];
		String content = "";

		try {
			// Read everything from the file into one string.
			constantsFile = (FileConnection) Connector.open("file:///"
					+ CONSTANTS_FILE_PATH, Connector.READ);
			constantsStream = constantsFile.openDataInputStream();
			while (constantsStream.read(buffer) != -1) {
				content += new String(buffer);
			}
			constantsStream.close();
			constantsFile.close();

			// Extract each line separately.
			String[] lines = Util.split(content, "\n");
			for (int i = 0; i < lines.length; i++) {
				// Extract the key and value.
				String[] line = Util.split(lines[i], "=");
				if (line.length != 2) {
					System.out.println("Error: invalid constants file line: "
							+ (lines[i].length() == 0 ? "(empty line)"
									: lines[i]));
					continue;
				}

				boolean found = false;
				// Search through the constants until we find one with the same
				// name.
				for (int j = 0; j < constants.size(); j++) {
					Constant constant = (Constant) constants.elementAt(j);
					if (constant.getName().compareTo(line[0]) == 0) {
						System.out.println("Setting " + constant.getName()
								+ " to " + Double.parseDouble(line[1]));
						constant.setVal(Double.parseDouble(line[1]));
						found = true;
						break;
					}
				}

				if (!found)
					System.out.println("Error: the constant doesn't exist: "
							+ lines[i]);
			}
		} catch (IOException e) {
			System.out
					.println("Constants.txt not found. Not overriding constants.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles an individual value used in the Constants class.
	 */
	public static class Constant {
		private String name;
		private double value;

		@SuppressWarnings("unchecked")
		public Constant(String name, double value) {
			this.name = name;
			this.value = value;
			constants.addElement(this);
		}

		public String getName() {
			return name;
		}

		public double getDouble() {
			return value;
		}

		public int getInt() {
			return (int) value;
		}

		public void setVal(double value) {
			this.value = value;
		}

		public String toString() {
			return name + ": " + value;
		}
	}
}