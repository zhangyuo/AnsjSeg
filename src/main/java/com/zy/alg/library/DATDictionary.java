package com.zy.alg.library;

import com.zy.alg.domain.AnsjItem;
import com.zy.alg.domain.PersonNatureAttr;
import com.zy.alg.domain.TermNature;
import com.zy.alg.domain.TermNatures;
import com.zy.alg.util.DicReader;
import org.nlpcn.commons.lang.dat.DoubleArrayTire;
import org.nlpcn.commons.lang.dat.Item;
import org.nlpcn.commons.lang.util.logging.Log;
import org.nlpcn.commons.lang.util.logging.LogFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 核心词典
 */
public class DATDictionary {

    private static final Log LOG = LogFactory.getLog(DATDictionary.class);

    /**
     * 核心词典
     */
    private static DoubleArrayTire DAT = null;

    /**
     * 数组长度
     */
    public static int arrayLength = 0;

    public DATDictionary(String modelFile, String personPath, String personfrePath, String typestr) {
        DAT = loadDAT(modelFile, personPath, personfrePath, typestr);
        arrayLength = DAT.arrayLength;
    }

    /**
     * 加载词典
     *
     * @return
     */
    private static DoubleArrayTire loadDAT(String modelFile, String personPath, String personfrePath, String typestr) {
        long start = System.currentTimeMillis();
        try {
            DoubleArrayTire dat;
            dat = DoubleArrayTire.loadText(DicReader.getInputStream(modelFile, typestr), AnsjItem.class);
            // 人名识别必备的
            personNameFull(dat, personPath, personfrePath, typestr);
            // 记录词典中的词语，并且清除部分数据
            for (Item item : dat.getDAT()) {
                if (item == null || item.getName() == null) {
                    continue;
                }
                if (item.getStatus() < 2) {
                    item.setName(null);
                    continue;
                }
            }
            LOG.info("init core library ok use time : " + (System.currentTimeMillis() - start));
            return dat;
        } catch (InstantiationException e) {
            LOG.warn("无法实例化", e);
        } catch (IllegalAccessException e) {
            LOG.warn("非法访问", e);
        } catch (NumberFormatException e) {
            LOG.warn("数字格式异常", e);
        } catch (IOException e) {
            LOG.warn("IO异常", e);
        }

        return null;
    }

    private static void personNameFull(DoubleArrayTire dat, String personPath, String personfrePath, String typestr) throws NumberFormatException, IOException {
        HashMap<String, PersonNatureAttr> personMap = new PersonAttrLibrary().getPersonMap(personPath, personfrePath, typestr);

        AnsjItem ansjItem = null;
        // 人名词性补录
        Set<Map.Entry<String, PersonNatureAttr>> entrySet = personMap.entrySet();
        char c = 0;
        String temp = null;
        for (Map.Entry<String, PersonNatureAttr> entry : entrySet) {
            temp = entry.getKey();

            if (temp.length() == 1 && (ansjItem = (AnsjItem) dat.getDAT()[temp.charAt(0)]) == null) {
                ansjItem = new AnsjItem();
                ansjItem.setBase(c);
                ansjItem.setCheck(-1);
                ansjItem.setStatus((byte) 3);
                ansjItem.setName(temp);
                dat.getDAT()[temp.charAt(0)] = ansjItem;
            } else {
                ansjItem = dat.getItem(temp);
            }

            if (ansjItem == null) {
                continue;
            }

            if ((ansjItem.termNatures) == null) {
                if (temp.length() == 1 && temp.charAt(0) < 256) {
                    ansjItem.termNatures = TermNatures.NULL;
                } else {
                    ansjItem.termNatures = new TermNatures(TermNature.NR);
                }
            }
            ansjItem.termNatures.setPersonNatureAttr(entry.getValue());
        }
    }

    public static int status(char c) {
        Item item = DAT.getDAT()[c];
        if (item == null) {
            return 0;
        }
        return item.getStatus();
    }

    /**
     * 判断一个词语是否在词典中
     *
     * @param word
     * @return
     */
    public static boolean isInSystemDic(String word) {
        Item item = DAT.getItem(word);
        return item != null && item.getStatus() > 1;
    }

    public static AnsjItem getItem(int index) {
        AnsjItem item = DAT.getItem(index);
        if (item == null) {
            return AnsjItem.NULL;
        }

        return item;
    }

    public static AnsjItem getItem(String str) {
        AnsjItem item = DAT.getItem(str);
        if (item == null || item.getStatus() < 2) {
            return AnsjItem.NULL;
        }

        return item;
    }

    public static int getId(String str) {
        return DAT.getId(str);
    }

}
