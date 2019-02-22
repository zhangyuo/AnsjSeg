package com.zy.alg.service;

import com.zy.alg.domain.Result;
import com.zy.alg.domain.Term;
import com.zy.alg.library.CompanyAttrLibrary;
import com.zy.alg.library.DATDictionary;
import com.zy.alg.library.NatureLibrary;
import com.zy.alg.library.NgramLibrary;
import com.zy.alg.splitword.IndexAnalysis;
import com.zy.alg.splitword.ToAnalysis;
import com.zy.alg.util.LoadDic;
import org.nlpcn.commons.lang.tire.domain.Forest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnsjSegImpl implements AnsjSeg {
    private final String ambiguityDicPath = "ambiguity.dic";
    private final String stopDicPath = "stopword.dic";
    private final String coreDicPath = "core.dic";
    private final String companyPath = "company.data";
    private final String naturemapPath = "nature/nature.map";
    private final String naturetablePath = "nature/nature.table";
    private final String personPath = "person/person.dic";
    private final String personfrePath = "person/asian_name_freq.data";
    private final String bigramPath = "bigramdict.dic";

    public static AnsjSegImpl singleton;

    private Set<String> stopDic = new HashSet<String>();

    public static AnsjSegImpl getSingleton() throws IOException {
        if (singleton == null) {
            synchronized (AnsjSegImpl.class) {
                if (singleton == null) {
                    singleton = new AnsjSegImpl();
                    return singleton;
                }
            }
        }
        return singleton;
    }

    /**
     * 项目路径
     *
     * @throws IOException
     */
    private AnsjSegImpl() throws IOException {
        String typestr = "1";
        NatureLibrary.init(naturemapPath, naturetablePath, typestr);
        new DATDictionary(coreDicPath, personPath, personfrePath, typestr);
        NgramLibrary.init(bigramPath, typestr);
        CompanyAttrLibrary.init(companyPath, typestr);
        LoadDic.insertAmbiguityDic(ambiguityDicPath, typestr);
        LoadDic.insertStopDic(stopDic, stopDicPath, typestr);
    }

    /**
     * 外部路径
     *
     * @param modelFile
     * @throws IOException
     */
    public AnsjSegImpl(String modelFile) throws IOException {
        if (modelFile != null && !modelFile.endsWith(File.separator)) {
            modelFile += File.separator;
        }
        String typestr = "2";
        NatureLibrary.init(modelFile + naturemapPath, modelFile + naturetablePath, typestr);
        new DATDictionary(modelFile + coreDicPath, modelFile + personPath, modelFile + personfrePath, typestr);
        NgramLibrary.init(modelFile + bigramPath, typestr);
        CompanyAttrLibrary.init(modelFile + companyPath, typestr);
        LoadDic.insertAmbiguityDic(modelFile + ambiguityDicPath, typestr);
        LoadDic.insertStopDic(stopDic, modelFile + stopDicPath, typestr);
    }

    @Override
    public Result textTokenizer(String query, String typescene) {
        Result terms = null;
        if (typescene.equals("1")) {
            terms = ToAnalysis.parse(query);
        } else if (typescene.equals("2")) {
            Result terms1 = ToAnalysis.parse(query);
            Set<String> hadWords = new HashSet<String>();
            List<Term> terms2 = new ArrayList<Term>();
            for (Term term : terms1) {
                String word = term.getName();
                if (!hadWords.contains(word)) {
                    terms2.add(term);
                }
                hadWords.add(word);
            }
            terms = new Result(terms2);
        } else if (typescene.equals("3")) {
            terms = IndexAnalysis.parse(query);
        }

        return terms;
    }

    @Override
    public Result textTokenizerUser(String query, String typescene, Forest... forests) {
        Result terms = null;
        if (typescene.equals("1")) {
            terms = ToAnalysis.parse(query, forests);
        } else if (typescene.equals("2")) {
            terms = IndexAnalysis.parse(query, forests);
        }
        return terms;
    }
}
