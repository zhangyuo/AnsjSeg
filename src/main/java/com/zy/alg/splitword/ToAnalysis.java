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
import org.nlpcn.commons.lang.tire.domain.Forest;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * 标准分词
 *
 * @author ansj
 */
public class ToAnalysis extends Analysis {

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

                return getResult();
            }

            private void userDefineRecognition(final Graph graph, Forest... forests) {
                new UserDefineRecognition(TermUtil.InsertTermType.SKIP, forests).recognition(graph.terms);
                graph.rmLittlePath();
                graph.walkPathByScore();
            }

            private List<Term> getResult() {
                List<Term> result = new ArrayList<Term>();
                int length = graph.terms.length - 1;
                for (int i = 0; i < length; i++) {
                    if (graph.terms[i] != null) {
                        result.add(graph.terms[i]);
                    }
                }
                setRealName(graph, result);
                return result;
            }
        };
        return merger.merger();
    }

    public ToAnalysis() {
        super();
    }

    public ToAnalysis(Reader reader) {
        super.resetContent(new AnsjReader(reader));
    }

    public static Result parse(String str) {
        return new ToAnalysis().parseStr(str);
    }

    public static Result parse(String str, Forest... forests) {
        return new ToAnalysis().setForests(forests).parseStr(str);
    }

    public static Result parse1(String str, Forest forest, Forest... forests) {
        return new ToAnalysis().setForests1(forest, forests).parseStr(str);
    }


}
