package org.elasticsearch.index.analysis;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

/**
 */
public class PinyinTokenFilter extends TokenFilter {

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private String padding_char;
    private String first_letter;
    @Override
    public final boolean incrementToken() throws IOException {
        if (!input.incrementToken()) {
            return false;
        }
        String str = termAtt.toString();
        StringBuilder pinyin = new StringBuilder();
        if (first_letter.equals("prefix")) {
            pinyin.append(PinyinHelper.getShortPinyin(str));
            if (this.padding_char.length() > 0) {
                pinyin.append(this.padding_char); //TODO splitter
            }
            pinyin.append(PinyinHelper.convertToPinyinString(str, this.padding_char, PinyinFormat.WITHOUT_TONE));
        } else if (first_letter.equals("append")) {
            pinyin.append(PinyinHelper.convertToPinyinString(str, this.padding_char, PinyinFormat.WITHOUT_TONE));
                    pinyin.append(this.padding_char);
            pinyin.append(PinyinHelper.getShortPinyin(str));
        } else if (first_letter.equals("none")) {
            pinyin.append(PinyinHelper.convertToPinyinString(str, this.padding_char, PinyinFormat.WITHOUT_TONE));
        } else if (first_letter.equals("only")) {
            pinyin.append(PinyinHelper.getShortPinyin(str));
        }
        termAtt.setEmpty();
        termAtt.resizeBuffer(pinyin.length());
        termAtt.append(pinyin);
        termAtt.setLength(pinyin.length());
        return true;
    }

    public PinyinTokenFilter(TokenStream in, String padding_char, String first_letter) {
        super(in);
        this.padding_char = padding_char;
        this.first_letter = first_letter;
    }

    @Override
    public final void end() throws IOException {
        // set final offset
      super.end();
    }

    @Override
    public void reset() throws IOException {
        super.reset();
    }


}
