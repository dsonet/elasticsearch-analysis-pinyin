package org.elasticsearch.index.analysis;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by IntelliJ IDEA.
 * User: Medcl'
 * Date: 12-5-21
 * Time: 下午5:53
 */
public class PinyinTokenizer extends Tokenizer {

    private static final int DEFAULT_BUFFER_SIZE = 256;

    private boolean done = false;
    private int finalOffset;
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private String padding_char;
    private String first_letter;

    public PinyinTokenizer(Reader reader, String padding_char, String first_letter) {
        this(reader, DEFAULT_BUFFER_SIZE);
        this.padding_char = padding_char;
        this.first_letter = first_letter;
    }

    public PinyinTokenizer(Reader input, int bufferSize) {
        super(input);
        termAtt.resizeBuffer(bufferSize);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        clearAttributes();

        if (!done) {
            done = true;
            int upto = 0;
            char[] buffer = termAtt.buffer();
            while (true) {
                final int length = input.read(buffer, upto, buffer.length - upto);
                if (length == -1) break;
                upto += length;
                if (upto == buffer.length)
                    buffer = termAtt.resizeBuffer(1 + buffer.length);
            }
            termAtt.setLength(upto);
            String str = termAtt.toString();
            termAtt.setEmpty();

            //let's join them
            if (first_letter.equals("prefix")) {
                termAtt.append(PinyinHelper.getShortPinyin(str));
                    termAtt.append(this.padding_char);
                termAtt.append(PinyinHelper.convertToPinyinString(str, this.padding_char, PinyinFormat.WITHOUT_TONE));
            } else if (first_letter.equals("append")) {
                termAtt.append(PinyinHelper.convertToPinyinString(str, this.padding_char, PinyinFormat.WITHOUT_TONE));
                        termAtt.append(this.padding_char);
                termAtt.append(PinyinHelper.getShortPinyin(str));
            } else if (first_letter.equals("none")) {
                termAtt.append(PinyinHelper.convertToPinyinString(str, this.padding_char, PinyinFormat.WITHOUT_TONE));
            } else if (first_letter.equals("only")) {
                termAtt.append(PinyinHelper.getShortPinyin(str));
            }


            finalOffset = correctOffset(upto);
            offsetAtt.setOffset(correctOffset(0), finalOffset);
            return true;
        }
        return false;
    }

    @Override
    public final void end() {
        // set final offset
        offsetAtt.setOffset(finalOffset, finalOffset);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        this.done = false;
    }


}
