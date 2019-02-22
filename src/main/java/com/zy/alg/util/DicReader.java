package com.zy.alg.util;

import org.nlpcn.commons.lang.util.logging.Log;
import org.nlpcn.commons.lang.util.logging.LogFactory;

import java.io.*;

/**
 * 加载词典用的类
 * 
 * @author ansj
 */
public class DicReader {

	private static final Log logger = LogFactory.getLog();

	/**
	 * 
	 * @param name
	 * @param type 1,默认路径 2，给定路径
	 * @return
	 */
	public static BufferedReader getReader(String name, String type) {
		// maven工程修改词典加载方式
		if(type.equals("1")){
			InputStream in = DicReader.class.getResourceAsStream("/" + name);
			try {
				return new BufferedReader(new InputStreamReader(in, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				logger.warn("不支持的编码", e);
			}
		}else{
			try{
				FileInputStream fin = new FileInputStream(new File(name));
				return new BufferedReader(new InputStreamReader(fin, "UTF-8"));
			}catch(IOException e){
				System.out.println(e);
			}
		}
		
		return null;
	}

	/**
	 * 
	 * @param name
	 * @param type 1,默认路径 2，给定路径
	 * @return
	 */
	public static InputStream getInputStream(String name, String type) {
		// maven工程修改词典加载方式
		InputStream in = null;
		if(type.equals("1")){
			in = DicReader.class.getResourceAsStream("/" + name);
		}else{
			try{
	        	in = new FileInputStream(name);
	        }catch(IOException e){
	        	System.out.println(e);
	        }
		}
        
		return in;
	}
}
