package com.webserver.http;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * HTTPЭ��涨֮����
 * @author ta
 *
 */
public class HttpContext {
	private static Map<String,String> mimeMapping = new HashMap<>();
	
	
	static {
		//��ʼ�����о�̬����
		initMimeMapping();
	}
	/**
	 * ��ʼ����Դ��׺��Content-Type��Ӧֵ
	 */
	private static void initMimeMapping() {
//		mimeMapping.put("html", "text/html");
//		mimeMapping.put("css", "text/css");
//		mimeMapping.put("js", "application/javascript");
//		mimeMapping.put("png", "image/png");
//		mimeMapping.put("jpg", "image/jpeg");
//		mimeMapping.put("gif", "image/gif");
		/*
		 * ����conf/web.xml�ļ�
		 * ������ǩ��������Ϊ<mime-mapping>��
		 * �ӱ�ǩ��ȡ�����������������:
		 * <extension>��ǩ�е��ı���Ϊkey
		 * <mime-type>��ǩ�е��ı���Ϊvalue
		 * ��ʼ��mimeMapping���Map��
		 * 
		 * ��ʼ����Ϻ�mimeMapping���Map��
		 * Ӧ����1000���Ԫ��
		 */
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new File("conf/web.xml"));
			Element root = doc.getRootElement();
			List<Element> list = root.elements("mime-mapping");
			for(Element e : list) {
				String key = e.elementTextTrim("extension");
				String value = e.elementTextTrim("mime-type");
				mimeMapping.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(mimeMapping.size());
	}
	
	/**
	 * ���ݸ�������Դ��׺����ȡ��Ӧ��Content-Type
	 * ��ֵ
	 * @param ext
	 * @return
	 */
	public static String getMimeType(String ext) {
		return mimeMapping.get(ext);
	}
	
	
	public static void main(String[] args) {
		String type = getMimeType("js");
		System.out.println(type);
	}
}








