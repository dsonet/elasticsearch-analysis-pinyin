package org.elasticsearch.plugin.analysis.pinyin;

import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.index.analysis.PinyinAnalysisBinderProcessor;
import org.elasticsearch.indices.analysis.PinyinIndicesAnalysisModule;
import org.elasticsearch.plugins.Plugin;

import java.util.Collection;
import java.util.Collections;

/**
 * The Pinyin Analysis plugin integrates jpinyin(https://github.com/dsonet/jpinyin) module into elasticsearch.
 */
public class AnalysisPinyinPlugin extends Plugin {

    @Override
    public String name() {
        return "analysis-pinyin";
    }

    @Override
    public String description() {
        return "Chinese to Pinyin convert support";
    }

    @Override
    public Collection<Module> nodeModules() {
        return Collections.<Module>singletonList(new PinyinIndicesAnalysisModule());
    }

    public void onModule(AnalysisModule module) {
        module.addProcessor(new PinyinAnalysisBinderProcessor());
    }
}