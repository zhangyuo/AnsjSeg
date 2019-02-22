package com.zy.alg.util;

import com.zy.alg.library.UserDefineLibrary;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.util.FileFinder;
import org.nlpcn.commons.lang.util.IOUtil;
import org.nlpcn.commons.lang.util.ObjConver;
import org.nlpcn.commons.lang.util.logging.Log;
import org.nlpcn.commons.lang.util.logging.LogFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * 这个类储存一些公用变量.
 *
 * @author ansj
 */
public class MyStaticValue {

    public static final Log LOG = LogFactory.getLog(MyStaticValue.class);
    public static final String DIC_DEFAULT = "dic";
    /**
     * 用户自定义词典
     */
    public static final Map<String, Object> DIC = new HashMap<String, Object>();
    /**
     * 是否开启人名识别
     */
    public static Boolean isNameRecognition = true;
    /**
     * 是否开启数字识别
     */
    public static Boolean isNumRecognition = true;
    /**
     * 是否数字和量词合并
     */
    public static Boolean isQuantifierRecognition = true;
    /**
     * 是否显示真实词语
     */
    public static Boolean isRealName = false;

    public static String ambiguityLibrary = "zbjambiguity.dic";
    /**
     * 是否用户辞典不加载相同的词
     */
    public static boolean isSkipUserDefine = false;

    public static final Map<String, String> ENV = new HashMap<String, String>();

    static {
        /**
         * 配置文件变量
         */
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("ansj_library");
        } catch (Exception e) {
            try {
                File find = FileFinder.find("ansj_library.properties", 1);
                if (find != null && find.isFile()) {
                    rb = new PropertyResourceBundle(IOUtil.getReader(find.getAbsolutePath(), System.getProperty("file.encoding")));
                    LOG.info("load ansj_library not find in classPath ! i find it in " + find.getAbsolutePath() + " make sure it is your config!");
                }
            } catch (Exception e1) {
                LOG.warn("not find ansj_library.properties. reason: " + e1.getMessage());
            }
        }

        if (rb == null) {
            try {
                rb = ResourceBundle.getBundle("library");
            } catch (Exception e) {
                try {
                    File find = FileFinder.find("library.properties", 2);
                    if (find != null && find.isFile()) {
                        rb = new PropertyResourceBundle(IOUtil.getReader(find.getAbsolutePath(), System.getProperty("file.encoding")));
                        LOG.info("load library not find in classPath ! i find it in " + find.getAbsolutePath() + " make sure it is your config!");
                    }
                } catch (Exception e1) {
                    LOG.warn("not find library.properties. reason: " + e1.getMessage());
                }
            }
        }

        if (rb == null) {
            LOG.warn("not find library.properties in classpath use it by default !");
        } else {

            for (String key : rb.keySet()) {
                ENV.put(key, rb.getString(key));
                try {
                    String value = rb.getString(key);

                    LOG.info("init " + key + " to env value is : " + value);
                    Field field = MyStaticValue.class.getField(key);
                    field.set(null, ObjConver.conversion(rb.getString(key), field.getType()));
                } catch (Exception e) {
                }
            }

        }
    }

    /**
     * 得到默认的模型
     *
     * @return
     */
    public static Forest getDicForest() {
        return getDicForest(DIC_DEFAULT);
    }

    /**
     * 根据模型名称获取crf模型
     *
     * @param key
     * @return
     */
    public static Forest getDicForest(String key) {
        Object temp = DIC.get(key);

        if (temp == null) {
            LOG.warn("dic {} not found in config " + key);
            return null;
        } else if (temp instanceof String) {
            return initForest(key, (String) temp);
        } else {
            return (Forest) temp;
        }
    }

    /**
     * 用户自定义词典加载
     *
     * @param key
     * @param dicPath
     * @return
     */
    private synchronized static Forest initForest(String key, String dicPath) {
        Forest forest = new Forest();
        UserDefineLibrary.loadLibrary(forest, dicPath);
        DIC.put(key, forest);
        return forest;
    }

}
