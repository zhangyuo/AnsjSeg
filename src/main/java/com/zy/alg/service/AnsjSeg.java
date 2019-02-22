package com.zy.alg.service;

import com.zy.alg.domain.Result;
import org.nlpcn.commons.lang.tire.domain.Forest;

/**
 * 文本分词器
 */
public interface AnsjSeg {
    /**
     * @param query     原句子
     * @param typescene 1.精准分词-不去重;2.精准分词-去重;3.索引分词
     * @return
     */
    Result textTokenizer(String query, String typescene);

    /**
     * @param query
     * @param typescene 1.精准分词-不去重;2.索引分词
     * @param forests   用户自定义词典
     * @return
     */
    Result textTokenizerUser(String query, String typescene, Forest... forests);
}
