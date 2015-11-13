package org.elasticsearch.index.analysis;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.Version;

import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PinyinAbbreviationsTokenizer extends CharTokenizer {

    private static final int DEFAULT_BUFFER_SIZE = 256;

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private static Pattern pattern = Pattern.compile("^[\\u4e00-\\u9fa5]$");

    public PinyinAbbreviationsTokenizer(Reader reader) {
        this(reader, DEFAULT_BUFFER_SIZE);
    }

    public PinyinAbbreviationsTokenizer(Reader input, int bufferSize) {
        super(Version.LATEST,input);
        termAtt.resizeBuffer(bufferSize);
    }


    @Override
    public boolean isTokenChar(int c){
        Matcher matcher = pattern.matcher(String.valueOf(c));
            return  Character.isLetterOrDigit(c)|| matcher.matches();
    }

  @Override
  protected int normalize(int c) {
                    String[] strs = PinyinHelper.convertToPinyinArray((char) c, PinyinFormat.WITHOUT_TONE);
                    if (strs != null) {
                        termAtt.append(strs[0]);
                       return  strs[0].codePointAt(0);
                    }
      return c;
  }
}
