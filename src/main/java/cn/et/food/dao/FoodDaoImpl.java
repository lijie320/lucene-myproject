package cn.et.food.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
@Repository
public class FoodDaoImpl {
	@Autowired
	private JdbcTemplate jdbc;
	
	//获取总行数
	public int foodCount(){
		String sql = "select count(*) as foodCount from food";
		return Integer.parseInt(jdbc.queryForList(sql).get(0).get("foodCount").toString());
	}
	
	/**
	 * 分页获取数据
	 * @param start  开始
	 * @param rows   总行数
	 * @return
	 */
	public List<Map<String, Object>> queryFood(int start,int rows){
		String sql = "select * from food limit "+start+","+rows;
		return jdbc.queryForList(sql);
	}
}
