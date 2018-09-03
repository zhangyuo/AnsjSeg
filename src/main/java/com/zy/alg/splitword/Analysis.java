package com.zy.alg.splitword;

import com.zy.alg.domain.Result;
import com.zy.alg.domain.Term;
import com.zy.alg.domain.TermNature;
import com.zy.alg.domain.TermNatures;
import com.zy.alg.library.AmbiguityLibrary;
import com.zy.alg.library.DATDictionary;
import com.zy.alg.util.AnsjReader;
import com.zy.alg.util.Graph;
import com.zy.alg.util.LoadDic;
import com.zy.alg.util.MyStaticValue;
import org.nlpcn.commons.lang.tire.GetWord;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.domain.SmartForest;
import org.nlpcn.commons.lang.util.StringUtil;
import org.nlpcn.commons.lang.util.WordAlert;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.zy.alg.library.DATDictionary.status;

/**
 * 基本分词+人名识别
 *
 * @author ansj
 */
public abstract class Analysis {
    /**
     * 用来记录偏移量
     */
    public int offe;
    /**
     * 分词的类
     */
    private GetWordsImpl gwi = new GetWordsImpl();
    /**
     * 默认词典加载-可认为是通用用户自定义词典
     */
    public static Forest defaultDic = null;

    protected Forest[] forests = null;

    private Forest ambiguityForest = AmbiguityLibrary.get();

    // 是否开启人名识别
    protected Boolean isNameRecognition = true;

    // 是否开启数字识别
    protected Boolean isNumRecognition = true;

    // 是否数字和量词合并
    protected Boolean isQuantifierRecognition = true;

    // 是否显示真实词语
    protected Boolean isRealName = false;

    // 是否标记新词
    protected boolean isNewWord = true;

    /**
     * 文档读取流
     */
    private AnsjReader br;

    protected Analysis() {
        initDefaultDic("default.dic");
        this.forests = new Forest[]{defaultDic};
        this.isNameRecognition = MyStaticValue.isNameRecognition;
        this.isNumRecognition = MyStaticValue.isNumRecognition;
        this.isQuantifierRecognition = MyStaticValue.isQuantifierRecognition;
        this.isRealName = MyStaticValue.isRealName;
    }

    /**
     * 插入自定义词典
     *
     * @param defaultDicPath
     */
    public static void initDefaultDic(String defaultDicPath) {
        if (defaultDic == null) {
            defaultDic = new Forest();
        }
        try {
            LoadDic.insertDefaultDic(defaultDic, defaultDicPath, "1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private LinkedList<Term> terms = new LinkedList<Term>();

    /**
     * while 循环调用.直到返回为null则分词结束
     *
     * @return
     * @throws IOException
     */

    public Term next() throws IOException {
        Term term;
        if (!terms.isEmpty()) {
            term = terms.poll();
            term.updateOffe(offe);
            return term;
        }

        String temp = br.readLine();
        offe = br.getStart();
        while (StringUtil.isBlank(temp)) {
            if (temp == null) {
                return null;
            } else {
                temp = br.readLine();
            }

        }

        // 歧异处理字符串

        fullTerms(temp);

        if (!terms.isEmpty()) {
            term = terms.poll();
            term.updateOffe(offe);
            return term;
        }

        return null;
    }

    /**
     * 填充terms
     */
    private void fullTerms(String temp) {
        List<Term> result = analysisStr(temp);
        terms.addAll(result);
    }

    /**
     * 一整句话分词,用户设置的歧异优先
     *
     * @param temp
     * @return
     */
    private List<Term> analysisStr(String temp) {
        Graph gp = new Graph(temp);
        int startOffe = 0;

        if (this.ambiguityForest != null) {
            GetWord gw = new GetWord(this.ambiguityForest, gp.chars);
            String[] params;
            while ((gw.getFrontWords()) != null) {
                if (gw.offe > startOffe) {
                    analysis(gp, startOffe, gw.offe);
                }
                params = gw.getParams();
                startOffe = gw.offe;
                for (int i = 0; i < params.length; i += 2) {
                    gp.addTerm(new Term(params[i], startOffe, new TermNatures(new TermNature(params[i + 1], 1))));
                    startOffe += params[i].length();
                }
            }
        }
        if (startOffe < gp.chars.length) {
            analysis(gp, startOffe, gp.chars.length);
        }
        List<Term> result = this.getResult(gp);

        return result;
    }

    private void analysis(Graph gp, int startOffe, int endOffe) {
        int start;
        int end;
        char[] chars = gp.chars;

        String str;
        for (int i = startOffe; i < endOffe; i++) {
            switch (status(chars[i])) {
                case 4:
                    start = i;
                    end = 1;
                    while (++i < endOffe && status(chars[i]) == 4) {
                        end++;
                    }
                    str = WordAlert.alertEnglish(chars, start, end);
                    gp.addTerm(new Term(str, start, TermNatures.EN));
                    i--;
                    break;
                case 5:
                    start = i;
                    end = 1;
                    while (++i < endOffe && status(chars[i]) == 5) {
                        end++;
                    }
                    str = WordAlert.alertNumber(chars, start, end);
                    gp.addTerm(new Term(str, start, TermNatures.M));
                    i--;
                    break;
                default:
                    start = i;
                    end = i;

                    int status = 0;
                    do {
                        end = ++i;
                        if (i >= endOffe) {
                            break;
                        }
                        status = status(chars[i]);
                    } while (status < 4);

                    if (status > 3) {
                        i--;
                    }

                    gwi.setChars(chars, start, end);
                    int max = start;
                    while ((str = gwi.allWords()) != null) {
                        Term term = new Term(str, gwi.offe, gwi.getItem());
                        int len = term.getOffe() - max;
                        if (len > 0) {
                            for (; max < term.getOffe(); ) {
                                gp.addTerm(new Term(String.valueOf(chars[max]), max, TermNatures.NULL));
                                max++;
                            }
                        }
                        gp.addTerm(term);
                        max = term.toValue();
                    }

                    int len = end - max;
                    if (len > 0) {
                        for (; max < end; ) {
                            gp.addTerm(new Term(String.valueOf(chars[max]), max, TermNatures.NULL));
                            max++;
                        }
                    }

                    break;
            }
        }
    }

    /**
     * 将为标准化的词语设置到分词中
     *
     * @param graph
     * @param result
     */
    protected void setRealName(Graph graph, List<Term> result) {

        if (!MyStaticValue.isRealName) {
            return;
        }

        String str = graph.realStr;

        for (Term term : result) {
            term.setRealName(str.substring(term.getOffe(), term.getOffe() + term.getName().length()));
        }
    }

    /**
     * 一句话进行分词并且封装
     *
     * @param temp
     * @return
     */
    public Result parseStr(String temp) {
        return new Result(analysisStr(temp));
    }

    /**
     * 通过构造方法传入的reader直接获取到分词结果
     *
     * @return
     * @throws IOException
     */
    public Result parse() throws IOException {
        List<Term> list = new ArrayList<Term>();
        Term temp = null;
        while ((temp = next()) != null) {
            list.add(temp);
        }
        Result result = new Result(list);
        return result;
    }

    protected abstract List<Term> getResult(Graph graph);

    public abstract class Merger {
        public abstract List<Term> merger();
    }

    /**
     * 重置分词器
     *
     * @param br
     */
    public void resetContent(AnsjReader br) {
        this.offe = 0;
        this.br = br;
    }

    public void resetContent(Reader reader) {
        this.offe = 0;
        this.br = new AnsjReader(reader);
    }

    public void resetContent(Reader reader, int buffer) {
        this.offe = 0;
        this.br = new AnsjReader(reader, buffer);
    }

    public Forest getAmbiguityForest() {
        return ambiguityForest;
    }

    public Analysis setAmbiguityForest(Forest ambiguityForest) {
        this.ambiguityForest = ambiguityForest;
        return this;
    }

    public Analysis setForests(Forest... forests) {
        this.forests = forests;
        return this;
    }

    public Analysis setForests1(Forest forest, Forest... forests) {
        this.forests = new Forest[forests.length + 1];
        for (int i = 0; i < forests.length; i++) {
            this.forests[i] = forests[i];
        }
        this.forests[forests.length] = forest;
        return this;
    }

    public Analysis setIsNameRecognition(Boolean isNameRecognition) {
        this.isNameRecognition = isNameRecognition;
        return this;
    }

    public Analysis setIsNumRecognition(Boolean isNumRecognition) {
        this.isNumRecognition = isNumRecognition;
        return this;
    }

    public Analysis setIsQuantifierRecognition(Boolean isQuantifierRecognition) {
        this.isQuantifierRecognition = isQuantifierRecognition;
        return this;
    }

    public Analysis setIsRealName(Boolean isRealName) {
        this.isRealName = isRealName;
        return this;
    }

    protected void setIsNewWord(Term term) {

        if (!isNewWord) {
            return;
        }

        if (term.termNatures().id > 0) {
            return;
        }

        int id = DATDictionary.getId(term.getName());

        if (id > 0) {
            return;
        }

        if (forests != null) {
            for (int i = 0; i < forests.length; i++) {
                if (forests[i] == null) {
                    continue;
                }

                SmartForest<String[]> branch = forests[i].getBranch(term.getName());

                if (branch == null) {
                    continue;
                }

                if (branch.getStatus() > 1) {
                    return;
                }
            }
        }

        term.setNewWord(true);

    }

}
