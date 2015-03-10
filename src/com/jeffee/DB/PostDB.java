/**
 * 将微博导入数据库中
 */
package com.jeffee.DB;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author CHEN Kan
 * @date 2013年9月22日
 ***/

public class PostDB {

	public static void main(String[] args) {
		String fName = "E:\\\\Sina\\\\spams\\\\allPost.sn";
		PostDB.inport(fName);
		
		/*String outFile = "E:\\\\Sina\\\\spams\\\\0922";
		String sql = "SELECT href from mydb.post where uid not in (3533865791,3545286555,3584492921)";
		PostIntoDB.export(outFile, sql);*/
	}

	private static String table = "mydb.Post";
	
	public static void export(String dFile, String sql){
		int index = sql.indexOf("from");
		String newSql = sql.substring(0, index)+" INTO OUTFILE '"+dFile+"' FIELDS TERMINATED BY ';' "+sql.substring(index);
		Connection conn = ConnPool.getConnection();
		Statement stmt;
		try {;
			stmt = conn.createStatement();
			stmt.execute(newSql);
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void inport(String fileName){
		String sql = "load data infile '"+fileName+"' ignore into table "+table+" fields terminated by ';';";
		Connection conn = ConnPool.getConnection();
		Statement stmt;
		try {;
			stmt = conn.createStatement();
			stmt.execute(sql);
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
