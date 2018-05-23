/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package familia.demo;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import javax.swing.filechooser.FileSystemView;
 

/**
 * 保留文件<br>
 * Translates a value using a lookup table. 
 * @since 1.0
 */
public class PropertiesTranslator {

    /** The mapping to be used in translation. */
    private final Map<CharSequence, CharSequence> lookupMap;
    /** The first character of each key in the lookupMap. */
    private final HashSet<Character> prefixSet;
    /** The length of the shortest key in the lookupMap. */
    private final int shortest;
    /** The length of the longest key in the lookupMap. */
    private final int longest;
    public PropertiesTranslator( ) {
    	 this.lookupMap = new HashMap<>();
         this.prefixSet = new HashSet<>();
         int currentShortest = Integer.MAX_VALUE;
         int currentLongest = 0;
       //桌面路径
 		System.setProperty("user.desktop", FileSystemView.getFileSystemView().getHomeDirectory().getPath());
		Properties ps = System.getProperties();
		for(Object key:ps.keySet())
		{ 
			lookupMap.put("{"+key+"}", ps.getProperty((String)key));
		} 
	 
		 for (final Map.Entry<CharSequence, CharSequence> pair : lookupMap.entrySet()) {
	            this.lookupMap.put(pair.getKey().toString(), pair.getValue().toString());
	            this.prefixSet.add(pair.getKey().charAt(0));
	            final int sz = pair.getKey().length();
	            if (sz < currentShortest) {
	                currentShortest = sz;
	            }
	            if (sz > currentLongest) {
	                currentLongest = sz;
	            }
	        }
	        this.shortest = currentShortest;
	        this.longest = currentLongest;
    }
    /**
     * Define the lookup table to be used in translation
     *
     * Note that, as of Lang 3.1 (the origin of this code), the key to the lookup
     * table is converted to a java.lang.String. This is because we need the key
     * to support hashCode and equals(Object), allowing it to be the key for a
     * HashMap. See LANG-882.
     *
     * @param lookupMap Map&lt;CharSequence, CharSequence&gt; table of translator
     *                  mappings
     */
    public PropertiesTranslator(final Map<CharSequence, CharSequence> lookupMap) {
        if (lookupMap == null) {
            throw new InvalidParameterException("lookupMap cannot be null");
        }
        this.lookupMap = new HashMap<>();
        this.prefixSet = new HashSet<>();
        int currentShortest = Integer.MAX_VALUE;
        int currentLongest = 0;

        for (final Map.Entry<CharSequence, CharSequence> pair : lookupMap.entrySet()) {
            this.lookupMap.put(pair.getKey().toString(), pair.getValue().toString());
            this.prefixSet.add(pair.getKey().charAt(0));
            final int sz = pair.getKey().length();
            if (sz < currentShortest) {
                currentShortest = sz;
            }
            if (sz > currentLongest) {
                currentLongest = sz;
            }
        }
        this.shortest = currentShortest;
        this.longest = currentLongest;
    }
    /**
     * Translate an input onto a Writer. This is intentionally final as its algorithm is
     * tightly coupled with the abstract method of this class.
     *
     * @param input CharSequence that is being translated
     * @param out Writer to translate the text to
     * @throws IOException if and only if the Writer produces an IOException
     */
    public final void translate(final CharSequence input, final Writer out) throws IOException {
         
        if (input == null) {
            return;
        }
        int pos = 0;
        final int len = input.length();
        while (pos < len) {
            final int consumed = translate(input, pos, out);
            if (consumed == 0) {
                // inlined implementation of Character.toChars(Character.codePointAt(input, pos))
                // avoids allocating temp char arrays and duplicate checks
                final char c1 = input.charAt(pos);
                out.write(c1);
                pos++;
                if (Character.isHighSurrogate(c1) && pos < len) {
                    final char c2 = input.charAt(pos);
                    if (Character.isLowSurrogate(c2)) {
                      out.write(c2);
                      pos++;
                    }
                }
                continue;
            }
            // contract with translators is that they have to understand codepoints
            // and they just took care of a surrogate pair
            for (int pt = 0; pt < consumed; pt++) {
                pos += Character.charCount(Character.codePointAt(input, pos));
            }
        }
    }
    /**
     * Helper for non-Writer usage.
     * @param input CharSequence to be translated
     * @return String output of translation
     */
    public final String translate(final CharSequence input) {
        if (input == null) {
            return null;
        }
        try {
            final StringWriter writer = new StringWriter(input.length() * 2);
            translate(input, writer);
            return writer.toString();
        } catch (final IOException ioe) {
            // this should never ever happen while writing to a StringWriter
            throw new RuntimeException(ioe);
        }
    }
    /**
     * {@inheritDoc}
     */
    public int translate(final CharSequence input, final int index, final Writer out) throws IOException {
        // check if translation exists for the input at position index
        if (prefixSet.contains(input.charAt(index))) {
            int max = longest;
            if (index + longest > input.length()) {
                max = input.length() - index;
            }
            // implement greedy algorithm by trying maximum match first
            for (int i = max; i >= shortest; i--) {
                final CharSequence subSeq = input.subSequence(index, index + i);
                final CharSequence result = lookupMap.get(subSeq.toString());

                if (result != null) {
                    out.write(result.toString());
                    return i;
                }
            }
        }
        return 0;
    }
}
