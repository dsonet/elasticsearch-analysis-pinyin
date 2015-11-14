package org.elasticsearch.index.analysis;

import com.github.stuxuhai.jpinyin.ChineseHelper;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.apache.lucene.analysis.util.CharTokenizer;

public class PinyinAbbreviationsTokenizer extends CharTokenizer {
    @Override
    public boolean isTokenChar(int c) {
        return Character.isLetterOrDigit(c) || ChineseHelper.isChinese(String.valueOf(c));
    }

    @Override
    protected int normalize(int c) {
        String[] strs = PinyinHelper.convertToPinyinArray((char) c, PinyinFormat.WITHOUT_TONE);
        if (strs != null) {
            return strs[0].codePointAt(0);
        }
        return c;
    }
}
