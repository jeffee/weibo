/**
 * 对微博进行解析
 */
package com.weibo.parse;

import java.io.File;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.util.DefaultParserFeedback;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.weibo.common.FileProcess;
import com.weibo.common.Post;

/**
 * @author CHEN Kan
 * @date 2013年9月10日
 ***/

public class PostParse2 {

	public static void main(String[] args) {
		String info = FileProcess.readLine(new File("E:\\Sina\\weibo\\reposters\\1005053545286555\\info-1-1"));
		int index = info.indexOf("html")+7;
		
		info = info.substring(index,info.lastIndexOf("\""));
		info = info.replaceAll("\\\\n", "").replaceAll("\\\\t", "");
		//System.out.println(info);
		PostParse2.parse(info);

	}

	public static Post parse(String info) {
		Post post = new Post();
		Parser parser;
		try {
			info = info.replaceAll("\\\\", "");
			Lexer mLexer = new Lexer(new Page(info));
			parser = new Parser(mLexer, new DefaultParserFeedback(DefaultParserFeedback.QUIET));
	
			NodeFilter divFilter = new TagNameFilter("div");
			NodeFilter attrFilter = new HasAttributeFilter("class","WB_detail");
			NodeFilter filter = new AndFilter(divFilter, attrFilter);
			NodeList nodes = parser.extractAllNodesThatMatch(filter);
			System.out.println("*********************************"+nodes.size());
			for(int i=0;i<nodes.size();i++){
				Node node = (Node) nodes.elementAt(i);
				System.out.println(node.toHtml());
			}
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return post;
	}

	//private static void set
	
}
