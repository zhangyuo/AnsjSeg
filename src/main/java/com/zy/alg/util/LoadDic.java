package com.zy.alg.util;

import com.zy.alg.library.AmbiguityLibrary;
import com.zy.alg.library.StopLibrary;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.domain.Value;
import org.nlpcn.commons.lang.tire.library.Library;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 词典加载工具类
 */
public class LoadDic {

    public static void insertAmbiguityDic(String zbjdicPath, String type) throws IOException {
        BufferedReader ar = DicReader.getReader(zbjdicPath, type);
        ;
        String line;
        while ((line = ar.readLine()) != null) {
            String[] seg = line.split("\t");
            AmbiguityLibrary.insert(AmbiguityLibrary.DEFAULT, seg);
        }
        ar.close();

    }

    public static void deleteAmbiguityDic(String zbjdicPath, String type) throws IOException {
        BufferedReader ar = DicReader.getReader(zbjdicPath, type);
        ;
        String line;
        while ((line = ar.readLine()) != null) {
            AmbiguityLibrary.delete(line);
        }
        ar.close();

    }

    public static void insertStopDic(Set<String> stopDic, String stopdicPath, String type) throws IOException {
        BufferedReader ar = DicReader.getReader(stopdicPath, type);
        if (stopDic == null) stopDic = new HashSet<String>();
        String line;
        while ((line = ar.readLine()) != null) {
            stopDic.add(line);
            StopLibrary.insertStopWords(StopLibrary.DEFAULT, line);
        }
        ar.close();
    }

    /**
     * 对字典进行修改
     *
     * @param dicpath
     * @param scaneType
     * @return
     */
    public static boolean changeDictionary(String dicpath, String scaneType) {
        try {
            if (dicpath.equalsIgnoreCase("current-amb-ext.dic")) {
                //添加模糊词典
                LoadDic.insertAmbiguityDic(dicpath, scaneType);
            } else if (dicpath.equalsIgnoreCase("current-amb-disable.dic")) {
                //删除模糊词典
                LoadDic.deleteAmbiguityDic(dicpath, scaneType);
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * 添加默认词典
     *
     * @param dic            树
     * @param defaultDicPath 路径
     * @param type           1，默认路径， 2，外部制定路径
     * @throws IOException
     */
    public static void insertDefaultDic(Forest dic, String defaultDicPath, String type) throws
            IOException {
        BufferedReader ar = DicReader.getReader(defaultDicPath, type);
        String line;
        while ((line = ar.readLine()) != null) {
            String[] seg = line.split("\t");
            String word = seg[0];
            if (seg.length == 3) {
                Library.insertWord(dic, new Value(word, seg[1], seg[2]));
            } else if (seg.length == 2) {
                Library.insertWord(dic, new Value(word, seg[1], "2000"));
            } else if (seg.length == 1) {
                Library.insertWord(dic, new Value(word, "zbj", "2000"));
            }
        }
        ar.close();
    }

    /**
     * 插入用户自定义词典
     *
     * @param userDefineDic
     * @param path
     */
    public static void insertUserDefineDic(Forest userDefineDic, String path) {
        BufferedReader ar;
        try {
            ar = new BufferedReader(new InputStreamReader(new FileInputStream(path),
                    "utf-8"));
            String line;
            while ((line = ar.readLine()) != null) {
                String[] seg = line.split("\t");
                String word = seg[0];
                if (seg.length == 3) {
                    Library.insertWord(userDefineDic, new Value(word, seg[1], seg[2]));
                } else if (seg.length == 2) {
                    Library.insertWord(userDefineDic, new Value(word, seg[1], "2000"));
                } else if (seg.length == 1) {
                    Library.insertWord(userDefineDic, new Value(word, "zbj", "2000"));
                }
            }
            ar.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
