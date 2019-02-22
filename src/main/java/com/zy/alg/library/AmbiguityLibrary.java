package com.zy.alg.library;

import com.zy.alg.domain.KV;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.domain.Value;
import org.nlpcn.commons.lang.tire.library.Library;
import org.nlpcn.commons.lang.util.logging.Log;
import org.nlpcn.commons.lang.util.logging.LogFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AmbiguityLibrary {

    private static final Log LOG = LogFactory.getLog(AmbiguityLibrary.class);

    /**
     * 同义词典
     */
    private static final Map<String, KV<String, Forest>> AMBIGUITY = new HashMap<String, KV<String, Forest>>();
    private static Forest AMBIGUITYTREE = null;

    public static final String DEFAULT = "ambiguity";

    static {
        if (AMBIGUITYTREE == null) {
            AMBIGUITYTREE = new Forest();
        }
    }

    /**
     * 获取系统默认词典
     *
     * @return
     */
    public static Forest get() {
        return get(DEFAULT);
    }

    /**
     * 根据key获取
     */
    public static Forest get(String key) {
        return AMBIGUITYTREE;
    }

    /**
     * 插入到树中呀
     *
     * @param key
     * @param split
     * @return
     */
    public static void insert(String key, String... split) {
        StringBuilder sb = new StringBuilder();
        if (split.length % 2 != 0) {
            LOG.error("init ambiguity  error in line :" + Arrays.toString(split) + " format err !");
            return;
        }
        for (int i = 0; i < split.length; i += 2) {
            sb.append(split[i]);
        }
        AMBIGUITYTREE.addBranch(sb.toString(), split);
    }

    public static void delete(String Line) {
        AMBIGUITYTREE.remove(Line);
    }

    /**
     * 插入到树种
     *
     * @param key
     * @param value
     */
    public static void insert(String key, Value value) {
        Forest forest = get(key);
        Library.insertWord(forest, value);
    }

    public static Set<String> keys() {
        return AMBIGUITY.keySet();
    }

    public static void putIfAbsent(String key, String path) {
        if (!AMBIGUITY.containsKey(key)) {
            AMBIGUITY.put(key, KV.with(path, (Forest) null));
        }
    }

}
