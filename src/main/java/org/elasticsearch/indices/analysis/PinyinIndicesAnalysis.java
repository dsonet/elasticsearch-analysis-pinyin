package org.elasticsearch.indices.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.analysis.*;

import java.io.File;

/**
 * Registers indices level analysis components so, if not explicitly configured,
 * will be shared among all indices.
 */
public class PinyinIndicesAnalysis extends AbstractComponent {
    private static final ESLogger log = Loggers.getLogger("pinyin-analyzer");
    @Inject
    public PinyinIndicesAnalysis(final Settings settings,
                                 IndicesAnalysisService indicesAnalysisService, Environment env) {
        super(settings);
        log.info("PinyinIndicesAnalysis initializing ...");
        indicesAnalysisService.analyzerProviderFactories().put("pinyin",
                new PreBuiltAnalyzerProviderFactory("pinyin", AnalyzerScope.INDICES, new PinyinAnalyzer(settings)));

        indicesAnalysisService.tokenizerFactories().put("pinyin",
                new PreBuiltTokenizerFactoryFactory(new TokenizerFactory() {
                    @Override
                    public String name() {
                        return "pinyin";
                    }

                    @Override
                    public Tokenizer create() {
                        return new PinyinTokenizer(settings.get("padding_char", " "), settings.get("first_letter", "none"));
                    }
                }));
        indicesAnalysisService.tokenizerFactories().put("pinyin_first_letter",
                new PreBuiltTokenizerFactoryFactory(new TokenizerFactory() {
                    @Override
                    public String name() {
                        return "pinyin_first_letter";
                    }

                    @Override
                    public Tokenizer create() {
                        return new PinyinAbbreviationsTokenizer();
                    }
                }));

        indicesAnalysisService.tokenFilterFactories().put("pinyin",
                new PreBuiltTokenFilterFactoryFactory(new TokenFilterFactory() {
                    @Override
                    public String name() {
                        return "pinyin";
                    }

                    @Override
                    public TokenStream create(TokenStream tokenStream) {
                        return new PinyinTokenFilter(tokenStream, settings.get("padding_char", " "), settings.get("first_letter", "none"));
                    }
                })
                );
        log.info("PinyinIndicesAnalysis initialized");
    }
}