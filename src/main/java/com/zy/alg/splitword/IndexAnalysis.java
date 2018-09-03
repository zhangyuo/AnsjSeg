package com.zy.alg.splitword;

import com.zy.alg.domain.Result;
import com.zy.alg.domain.Term;
import com.zy.alg.recognition.AsianPersonRecognition;
import com.zy.alg.recognition.ForeignPersonRecognition;
import com.zy.alg.recognition.NumRecognition;
import com.zy.alg.recognition.UserDefineRecognition;
import com.zy.alg.util.AnsjReader;
import com.zy.alg.util.Graph;
import com.zy.alg.util.NameFix;
import com.zy.alg.util.TermUtil;
import org.nlpcn.commons.lang.tire.GetWord;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.util.ObjConver;

import java.io.Reader;
import java.util.*;

/**
 * 用于检索的分词方式
 *
 * @author ansj
 */
public class IndexAnalysis extends Analysis {

    @Override
    protected List<Term> getResult(final Graph graph) {
        Merger merger = new Merger() {

            @Override
            public List<Term> merger() {
                graph.walkPath();

                // 数字发现
                if (isNumRecognition && graph.hasNum) {
                    new NumRecognition().recognition(graph.terms);
                }

                // 用户自定义词典的识别
                userDefineRecognition(graph, forests);

                // 姓名识别
                if (graph.hasPerson && isNameRecognition) {
                    // 亚洲人名识别
                    new AsianPersonRecognition().recognition(graph.terms);
                    graph.walkPathByScore();
                    NameFix.nameAmbiguity(graph.terms);
                    // 外国人名识别
                    new ForeignPersonRecognition().recognition(graph.terms);
                    graph.walkPathByScore();
                }

                return result();
            }

            private void userDefineRecognition(final Graph graph, Forest... forests) {
                new UserDefineRecognition(TermUtil.InsertTermType.SKIP, forests).recognition(graph.terms);
                graph.rmLittlePath();
                graph.walkPathByScore();
            }

            /**
             * 检索的分词
             *
             * @return
             */
            private List<Term> result() {

                String temp = null;

                Set<String> set = new HashSet<String>();

                List<Term> result = new LinkedList<Term>();
                int length = graph.terms.length - 1;
                for (int i = 0; i < length; i++) {
                    if (graph.terms[i] != null) {
                        result.add(graph.terms[i]);
                        set.add(graph.terms[i].getName() + graph.terms[i].getOffe());
                    }
                }

                LinkedList<Term> last = new LinkedList<Term>();

                char[] chars = graph.chars;

                if (forests != null) {
                    for (Forest forest : forests) {
                        if (forest == null) {
                            continue;
                        }
                        GetWord word = forest.getWord(chars);
                        while ((temp = word.getAllWords()) != null) {
                            if (!set.contains(temp + word.offe)) {
                                set.add(temp + word.offe);
                                last.add(new Term(temp, word.offe, word.getParam(0), ObjConver.getIntValue(word.getParam(1))));
                            }
                        }
                    }
                }

                result.addAll(last);

                Collections.sort(result, new Comparator<Term>() {

                    @Override
                    public int compare(Term o1, Term o2) {
                        if (o1.getOffe() == o2.getOffe()) {
                            return o2.getName().length() - o1.getName().length();
                        } else {
                            return o1.getOffe() - o2.getOffe();
                        }
                    }
                });

                setRealName(graph, result);
                return result;
            }
        };

        return merger.merger();
    }

    public IndexAnalysis() {
        super();
    }

    public IndexAnalysis(Reader reader) {
        super.resetContent(new AnsjReader(reader));
    }

    public static Result parse(String str) {
        return new IndexAnalysis().parseStr(str);
    }

    public static Result parse(String str, Forest... forests) {
        return new IndexAnalysis().setForests(forests).parseStr(str);
    }

    public static Result parse1(String str, Forest forest, Forest... forests) {
        return new IndexAnalysis().setForests1(forest, forests).parseStr(str);
    }

}
