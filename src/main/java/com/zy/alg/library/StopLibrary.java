package com.zy.alg.library;

/**
 * Created by octacon on 2018/3/29.
 */

import com.zy.alg.domain.KV;
import com.zy.alg.recognition.StopRecognition;
import org.nlpcn.commons.lang.util.logging.Log;
import org.nlpcn.commons.lang.util.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StopLibrary {
    private static final Log LOG = LogFactory.getLog();

    public static final String DEFAULT = "stop";
    private static StopRecognition DEFAULT_SR = null;

    /**
     * 用户自定义词典
     */
    private static final Map<String, KV<String, StopRecognition>> STOP = new HashMap<String,
            KV<String, StopRecognition>>();

    static {
        if (DEFAULT_SR == null) {
            DEFAULT_SR = new StopRecognition();
        }
    }

    /**
     * 词性过滤
     *
     * @param key
     * @param filterNatures
     */
    public static void insertStopNatures(String key, String... filterNatures) {
        StopRecognition fr = get(key);
        fr.insertStopNatures(filterNatures);
    }

    /**
     * 正则过滤
     *
     * @param key
     * @param regexes
     */
    public static void insertStopRegexes(String key, String... regexes) {
        StopRecognition fr = get(key);
        fr.insertStopRegexes(regexes);
    }

    /**
     * 增加停用词
     *
     * @param key
     * @param stopWords
     */
    public static void insertStopWords(String key, String... stopWords) {
        StopRecognition fr = get(key);
        fr.insertStopWords(stopWords);
    }

    /**
     * 增加停用词
     *
     * @param key
     * @param stopWords
     */
    public static void insertStopWords(String key, List<String> stopWords) {
        StopRecognition fr = get(key);
        fr.insertStopWords(stopWords);
    }

    public static StopRecognition get() {
        return get(DEFAULT);
    }

    /**
     * 根据模型名称获取crf模型
     *
     * @param key
     * @return
     */
    public static StopRecognition get(String key) {

        return DEFAULT_SR;

    }

    /**
     * 动态添加词典
     *
     * @param key
     * @param path
     */
    public static void putIfAbsent(String key, String path) {
        if (!STOP.containsKey(key)) {
            STOP.put(key, KV.with(path, (StopRecognition) null));
        }
    }

    public static Set<String> keys() {
        return STOP.keySet();
    }
}
