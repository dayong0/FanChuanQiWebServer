package com.webserver.core;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.webserver.servlet.HttpServlet;

/**
 * ���ﱣ�����з������ص�������Ϣ
 * @author ta
 *
 */
public class ServerContext {
	private static Map<String,HttpServlet> servletMapping = new HashMap<>();
	
	static {
		initServletMapping();
	}
	/**
	 * ��ʼ������·�����ӦServlet�Ĺ�ϵ
	 */
	private static void initServletMapping(){
//		servletMapping.put("/myweb/reg", new RegServlet());
//		servletMapping.put("/myweb/login", new LoginServlet());
		/*
		 * ����conf/servlets.xml�ļ�
		 * ������ǩ�����е�<servlet>��ǩ��ȡ��
		 * ���ҽ�ÿ��<servlet>��ǩ�е�����:
		 * path��ֵ��Ϊkey
		 * className��ֵ���÷�����ض�Ӧ����
		 * ��ʵ��������ʵ�����Ķ�������Ϊ
		 * HttpServlet����Ϊvalue���뵽
		 * servletMapping��ɳ�ʼ����
		 */
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(
					new File("./conf/servlets.xml"));
			Element root = doc.getRootElement();
			List<Element> list = root.elements();
			for(Element servletEle : list) {
				String path 
					= servletEle.attributeValue("path");
				
				String className 
					= servletEle.attributeValue("className");
				Class cls = Class.forName(className);
				HttpServlet servlet 
					= (HttpServlet)cls.newInstance();
				
				servletMapping.put(path, servlet);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(servletMapping.size());
	} 
	/**
	 * ���ݸ���������·����ȡ��Ӧ��Servlet
	 * @param path
	 * @return
	 */
	public static HttpServlet getServlet(String path) {
		return servletMapping.get(path);
	}
}




