# ansj分词项目

## 准备工作

1、 jar包：
````
    需要jar包：org.nlpcn-1.7.7
````

## 接口说明

1、默认分词方法：
````
    /**
     * @param query     原句子
     * @param typescene 1.精准分词-不去重;2.精准分词-去重;3.索引分词
     * @return
     */
    Result textTokenizer(String query, String typescene);
````

2、自定义分词方法：
````
    /**
     * @param query
     * @param typescene 1.精准分词-不去重;2.索引分词
     * @param forests   用户自定义词典
     * @return
     */
    Result textTokenizerUser(String query, String typescene, Forest... forests);
````
