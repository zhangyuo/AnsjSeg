package com.zy.alg.demo;

import com.zy.alg.domain.Result;
import com.zy.alg.library.StopLibrary;
import com.zy.alg.service.AnsjSeg;
import com.zy.alg.service.AnsjSegImpl;
import com.zy.alg.util.LoadDic;
import org.nlpcn.commons.lang.tire.domain.Forest;

import java.io.IOException;

/**
 * @author zhangycqupt@163.com
 * @date 2018/09/01 19:09
 */
public class SegUserDemo {
    public static void main(String[] args) throws IOException {
        String text = "红酒（Red wine）是葡萄酒的一种，并不一定特指红葡萄酒。红酒的成分相当简单，是经自然" +
                "发酵酿造出来的果酒，含有最多的是葡萄汁，葡萄酒有许多分类方式。以成品颜色来说，可分为红葡萄酒、" +
                "白葡萄酒及粉红葡萄酒三类。其中红葡萄酒又可细分为干红葡萄酒、半干红葡萄酒、半甜红葡萄酒和" +
                "甜红葡萄酒，白葡萄酒则细分为干白葡萄酒、半干白葡萄酒、半甜白葡萄酒和甜白葡萄酒。" +
                "粉红葡萄酒也叫桃红酒、玫瑰红酒。杨梅酿制的叫做杨梅红酒。还有一种蓝莓酿制的蓝莓红酒。";
        AnsjSeg ansjSeg = AnsjSegImpl.getSingleton();
        Forest userDefineDic = new Forest();
        String path = "G:\\project\\中文分词\\java-seg-sdk\\src\\main\\resources\\userdefine.dic";
        LoadDic.insertUserDefineDic(userDefineDic, path);

        Result terms = ansjSeg.textTokenizerUser(text, "1", userDefineDic);
        System.out.println("精确分词-不去重：");
        System.out.println(terms);

        Result terms1 = ansjSeg.textTokenizerUser(text, "2", userDefineDic).recognition(StopLibrary.get());
        System.out.println("精确分词-去重(增加停用词)：");
        System.out.println(terms1);
    }
}
