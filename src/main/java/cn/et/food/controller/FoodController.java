package cn.et.food.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.et.food.dao.FoodDaoImpl;
import cn.et.food.utils.LuceneUtils;

@RestController
public class FoodController {

	@Autowired
	FoodDaoImpl fd;
	
	@GetMapping("/rearchFood")
	public List<Map> rearchIndex(String keyword) throws Exception{
		return LuceneUtils.search("foodname", keyword);
	}
	
	
	@GetMapping("/foodIndex")
	public String creatIndex(){
		//数据库查询数据 批量查询
		try {
			int foodcount = fd.foodCount();
			int startIndex = 0;
			int rows = 5;
			while(startIndex <= foodcount){
				//每次拉取的数据
				List<Map<String, Object>> queryFood = fd.queryFood(startIndex, rows);
				for (int i = 0; i < queryFood.size(); i++) {
					Map<String, Object> mso = queryFood.get(i);
					Document doc = new Document();
					Field field1 = new Field("foodid",mso.get("foodid").toString(),TextField.TYPE_STORED);
					Field field2 = new Field("foodname",mso.get("foodname").toString(),TextField.TYPE_STORED);
					Field field3 = new Field("money",mso.get("money").toString(),TextField.TYPE_STORED);
					doc.add(field1);
					doc.add(field2);
					doc.add(field3);
					LuceneUtils.write(doc);
				}
				//写入lucene索引中
				startIndex=startIndex+rows;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "0";
		}
		return "1";
	}
}
