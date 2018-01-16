package cn.et.food.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;


public class LuceneUtils {
	// 索引所放入的地方
	static String dir = "D:\\index";
	// 创建分词器
	static Analyzer analyzer = new IKAnalyzer();

	public static List<Map> search(String field,String value) throws IOException, ParseException, InvalidTokenOffsetsException{
    	Directory directory = FSDirectory.open(new File(dir));
    	//读取索引库的存储目录
    	DirectoryReader ireader = DirectoryReader.open(directory);
    	//搜索类
    	IndexSearcher isearcher = new IndexSearcher(ireader);
    	//lucenec查询解析 用于指定查询属性名和分词器
    	QueryParser parser = new QueryParser(Version.LUCENE_47, field, analyzer);
    	//开始搜索
    	Query query = parser.parse(value);
    	
    	SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<font color=red>","</font>");
    	Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
    	//获取搜索的结果 指定返回document个数
    	ScoreDoc[] hits = isearcher.search(query, null, 10).scoreDocs;
    	List<Map> list = new ArrayList();
    	for (int i = 0; i < hits.length; i++) {
    		int id = hits[i].doc;
    		Document hitDoc = isearcher.doc(hits[i].doc);
    	    Map map = new HashMap();
    	    map.put("foodid", hitDoc.get("foodid"));
    	    String foodname = hitDoc.get("foodname");
    	    //将查询的的结果和搜索词匹配 匹配添加到前缀和后缀高亮
    	    TokenStream tokenStream = TokenSources.getAnyTokenStream(isearcher.getIndexReader(), id, "foodname", analyzer);
    	    //传入的第二个参数是查的值
    	    TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, foodname, false, 10);
    	    String foodnameHigh="";
    	    for (int j = 0; j < frag.length; j++) {
    	        if ((frag[j] != null) && (frag[j].getScore() > 0)) {
    	        	foodnameHigh=((frag[j].toString()));
    	        }
    	      }
    	    map.put("foodname", foodnameHigh);
    	    map.put("money", hitDoc.get("money"));
    	    list.add(map);
    	}
    	ireader.close();
    	directory.close();
    	return list;
    }
	
	
	public static void write(Document doc) throws IOException {
		// 索引库的存储目录
		Directory directory = FSDirectory.open(new File(dir));
		// 关联lucene版本当前分词器
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
		// 传入 写入目录和分词器
		IndexWriter iwriter = new IndexWriter(directory, config);
		iwriter.addDocument(doc);
		iwriter.commit();
		iwriter.close();
	}
}
