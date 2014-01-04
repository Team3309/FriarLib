/*
 * Copyright (c) 2014, FRC Team 3309 All rights reserved.
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
 */

package org.team3309.friarlib.constants;

import com.sun.squawk.microedition.io.FileConnection;

import javax.microedition.io.Connector;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;

public class ConstantsManager {

    static {
        try {
            loadConstantsFromFile("/Constants.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Hashtable constants = new Hashtable();

    /**
     * Add constant to the map
     *
     * @param c
     */
    protected static void addConstant(Constant c) {
        constants.put(c.getName(), c);
    }

    /**
     * Load constants from a txt file on the cRIO
     *
     * @param path
     * @throws IOException
     */
    public static void loadConstantsFromFile(String path) throws IOException {
        FileConnection fileConnection = (FileConnection) Connector.open("file:///" + path, Connector.READ);
        loadConstants(fileConnection.openInputStream());
    }

    public static void loadConstants(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        while ((line = reader.readLine()) != null) {
            line = line.trim().replace(" ", "").replace("\t", "");
            if (line.startsWith("#") || line.startsWith("//") || line.equals("")) {
                continue;
            }
            if (!line.contains("=")) {
                throw new IOException("Invalid format, line <" + line + "> does not contain equals sign");
            }
            String key = line.substring(0, line.indexOf("=")).trim();
            String value = line.substring(line.indexOf("=") + 1);

            //value is a list
            if (value.contains(",")) {
                String[] valStrings = split(value, ",");
                boolean successful = true;
                double[] val = new double[valStrings.length];
                for (int i = 0; i < valStrings.length; i++) {
                    if (valStrings[i].equals("")) {
                        System.err.println("Malformed line <" + line + "> empty string in array");
                        successful = false;
                        break;
                    }
                    val[i] = Double.parseDouble(valStrings[i]);
                }
                if (successful) {
                    if (constants.containsKey(key)) {
                        ((Constant) constants.get(key)).set(val);
                    } else {
                        System.err.println("Constant <" + key + "> will not be used");
                    }
                }
            } else {
                if (constants.containsKey(key)) {
                    if (value.equals("")) {
                        System.err.println("Malformed line <" + line + "> empty string as value");
                        continue;
                    }
                    if (value.equals("true"))
                        ((Constant) constants.get(key)).set(true);
                    else if (value.equals("false"))
                        ((Constant) constants.get(key)).set(false);
                    else
                        ((Constant) constants.get(key)).set(Double.parseDouble(value));
                } else {
                    System.err.println("Constant <" + key + "> will not be used");
                }
            }
        }
        inputStream.close();
    }


    /**
     * Returns the array of substrings obtained by dividing the given input
     * string at each occurrence of the given delimiter.
     */
    private static String[] split(String input, String delimiter) {
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
