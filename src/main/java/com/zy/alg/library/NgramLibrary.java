package com.zy.alg.library;

import com.zy.alg.domain.AnsjItem;
import com.zy.alg.domain.Term;
import com.zy.alg.util.DicReader;
import com.zy.alg.util.MyStaticValue;
import org.nlpcn.commons.lang.util.IOUtil;
import org.nlpcn.commons.lang.util.StringUtil;
import org.nlpcn.commons.lang.util.logging.Log;
import org.nlpcn.commons.lang.util.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * 两个词之间的关联
 *
 * @author ansj
 */
public class NgramLibrary {
    public static final Log LOG = LogFactory.getLog(MyStaticValue.class);

    public static void init(String modelFile, String typestr) {
        long start = System.currentTimeMillis();
        initBigramTables(modelFile, typestr);
        LogFactory.getLog(NgramLibrary.class).info("init ngram ok use time :" + (System.currentTimeMillis() - start));
    }

    public static void initBigramTables(String modelFile, String typestr) {


        try {
            BufferedReader reader = IOUtil.getReader(DicReader.getInputStream(modelFile, typestr), "UTF-8");

            String temp;
            String[] strs;
            int freq;
            while ((temp = reader.readLine()) != null) {
                if (StringUtil.isBlank(temp)) {
                    continue;
                }
                strs = temp.split("\t");
                freq = Integer.parseInt(strs[1]);
                strs = strs[0].split("@");
                AnsjItem fromItem = DATDictionary.getItem(strs[0]);

                AnsjItem toItem = DATDictionary.getItem(strs[1]);

                if (fromItem == AnsjItem.NULL && strs[0].contains("#")) {
                    fromItem = AnsjItem.BEGIN;
                }

                if (toItem == AnsjItem.NULL && strs[1].contains("#")) {
                    toItem = AnsjItem.END;
                }

                if (fromItem == AnsjItem.NULL || toItem == AnsjItem.NULL) {
                    continue;
                }

                if (fromItem.bigramEntryMap == null) {
                    fromItem.bigramEntryMap = new HashMap<Integer, Integer>();
                }

                fromItem.bigramEntryMap.put(toItem.getIndex(), freq);

            }
        } catch (NumberFormatException e) {
            LOG.warn("数字格式异常", e);
        } catch (UnsupportedEncodingException e) {
            LOG.warn("不支持的编码", e);
        } catch (IOException e) {
            LOG.warn("IO异常", e);
        }
    }

    /**
     * 查找两个词与词之间的频率
     *
     * @param from
     * @param to
     * @return
     */
    public static int getTwoWordFreq(Term from, Term to) {
        if (from.item().bigramEntryMap == null) {
            return 0;
        }
        Integer freq = from.item().bigramEntryMap.get(to.item().getIndex());
        if (freq == null) {
            return 0;
        } else {
            return freq;
        }
    }

}
