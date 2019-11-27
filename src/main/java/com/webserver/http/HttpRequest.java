package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.webserver.exception.EmptyRequestException;

/**
 * �������
 * �����ÿһ��ʵ�����ڱ�ʾ�ͻ��˷��͹�����
 * һ���������������
 * һ�����������������:�����У���Ϣͷ����Ϣ����
 * @author ta
 *
 */
public class HttpRequest {
	//�����������Ϣ
	//����ķ�ʽ
	private String method;
	//����·��
	private String uri;
	//Э��汾
	private String protocol;
	//uri�������󲿷�(?�������)
	private String requestURI;
	//uri���в�������(?�Ҳ�����)
	private String queryString;
	//��¼���������еľ���ÿһ������
	private Map<String,String> parameters = new HashMap<>();
	
	
	//��Ϣͷ�����Ϣ
	/*
	 * key:��Ϣͷ������
	 * value:��Ϣͷ��Ӧ��ֵ
	 */
	private Map<String,String> headers = new HashMap<>();
	
	
	
	//��Ϣ���������Ϣ
	
	
	//��������ص�����
	private Socket socket;
	private InputStream in;
	/**
	 * ���췽�������ڳ�ʼ���������
	 * @throws EmptyRequestException 
	 */
	public HttpRequest(Socket socket) throws EmptyRequestException {
		try {
			this.socket = socket;
			this.in = socket.getInputStream();
			/*
			 * ���������Ϊ����:
			 * 1:����������
			 * 2:������Ϣͷ
			 * 3:������Ϣ����
			 */
			System.out.println("HttpRequest:��ʼ��������...");
			parseRequestLine();
			parseHeaders();
			parseContent();
			System.out.println("HttpRequest:�����������!");
			
		} catch(EmptyRequestException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * ����������
	 * @throws EmptyRequestException 
	 */
	private void parseRequestLine() throws EmptyRequestException {
		System.out.println("HttpRequest:��ʼ����������");
		try {
			/*
			 * ͨ����������ȡ��һ���ַ�����
			 * һ�������еĵ�һ���ַ�����������
			 * �е����ݡ�
			 * ��ȡ���Ժ󣬰���" "(�ո�)���Ϊ������
			 * Ȼ�������������ݷֱ����õ������ж�Ӧ
			 * ������method,uri,protocol��
			 * 
			 * http://localhost:8088/index.html
			 */

			String line = readLine();
			//�жϸ������Ƿ�Ϊ������
			if("".equals(line)) {
				throw new EmptyRequestException();
			}
			
			
			
			String[] data = line.split("\\s");
			method = data[0];
			uri = data[1];
			protocol = data[2];
			
			parseURI();//��һ������uri
			
			System.out.println("method:"+method);// GET
			System.out.println("uri:"+uri);// /index.html
			System.out.println("protocol:"+protocol);// HTTP/1.1
			
		} catch(EmptyRequestException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("HttpRequest:�������������");
	}
	/**
	 * ��һ������uri
	 */
	private void parseURI() {
		System.out.println("HttpRequest:��һ������uri...");
		/*
		 * �����ڽ���֮ǰ��Ҫ���б�ǰuri��
		 * ���в������жϵ������ǿ�uri����
		 * �Ƿ���"?",����˵�����в���������
		 * ����û�в�����
		 * ���û�в�������ôֱ�ӽ�uri��ֵ��ֵ
		 * ��requestURI���ɡ�
		 * 
		 * ���в�������Ӧ���Ȱ���"?"��uri���
		 * Ȼ��"?"������ݸ�ֵ��requestURI,
		 * ��"?"�Ҳ����ݸ�ֵ��queryString
		 * ���Ž����������ٰ���"&"���Ϊÿһ���
		 * ����ÿ������ٰ���"="���Ϊ��������
		 * ����ֵ������������Ϊkey,ֵ��Ϊvalue
		 * ���浽parameters���Map����ɽ���
		 * 
		 * /myweb/reg?username=%E8%8C%8CXXXXXX
		 */
		if(uri.indexOf("?")!=-1) {
			String[] data = uri.split("\\?");
			requestURI = data[0];
			if(data.length>1) {
				queryString = data[1];
				//��queryString�а�����%XX����ת��
				try {
					queryString = URLDecoder.decode(
						queryString, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				parseParameters(queryString);
				
			}
		}else {
			requestURI = uri;
		}
		
		
		
		System.out.println("requestURI:"+requestURI);
		System.out.println("queryString:"+queryString);
		System.out.println("parameters:"+parameters);
		System.out.println("HttpRequest:����uri���!");
	}
	/**
	 * ��������,������һ���ַ���.
	 * ��ʽΪ:name=value&name=value...
	 * ���������Ĳ��������parameters���Map��
	 * @param line
	 */
	private void parseParameters(String line) {
		String[] data = line.split("&");
		for(String str : data) {
			String[] para = str.split("=");
			if(para.length>1) {
				parameters.put(para[0], para[1]);
			}else {
				parameters.put(para[0], null);
			}
		}
	}
	
	/**
	 * ������Ϣͷ
	 */
	private void parseHeaders() {
		System.out.println("HttpRequest:��ʼ������Ϣͷ");
		try {
			/*
			 * ѭ������readLine������ȡÿһ��
			 * �ַ�����ÿһ�о���һ����Ϣͷ��
			 * ���readLine�������ص��ַ�����
			 * һ�����ַ���ʱ��Ӧ��ֹͣѭ����ȡ
			 * ������(��Ϊ������ȡ����CRLF)
			 * 
			 * ��ȡ��ÿһ����Ϣͷ�����ǿ��԰���
			 * ": "(��:ð�ſո�)�����в�֣���
			 * ��Ϣͷ��������Ϊkey����Ϣͷ��ֵ��Ϊ
			 * value���浽headers���Map�������Ϣ
			 * ͷ�Ľ�������
			 * 
			 */
			while(true) {
				String line = readLine();
				if("".equals(line)) {
					break;
				}
				String[] data = line.split(":\\s");
				headers.put(data[0], data[1]);
			}
			
			
			System.out.println("headers:"+headers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("HttpRequest:������Ϣͷ���");
	}
	/**
	 * ������Ϣ����
	 */
	private void parseContent() {
		System.out.println("HttpRequest:��ʼ������Ϣ����");
		/*
		 * ������Ϣͷ�е�Content-Length���ж�
		 * ��ǰ�����Ƿ�����Ϣ����
		 */
		if(headers.containsKey("Content-Length")) {
			//���ȵõ����ĵĳ���(�ֽ���)
			int length = Integer.parseInt(
				headers.get("Content-Length")	
			);
			try {
				//��ȡ���������е��ֽ�
				byte[] data = new byte[length];
				in.read(data);
				
				//����Content-Typeָ������������������
				String type = headers.get("Content-Type");
				
				//�Ƿ�Ϊҳ��form���ύ���û��������Ϣ
				if("application/x-www-form-urlencoded".equals(type)) {
					/*
					 * �������ݾ���һ���ַ�����ԭGET������
					 * url��"?"�Ҳ�����
					 */
					String line = new String(data,"ISO8859-1");
					
					line = URLDecoder.decode(line,"UTF-8");
					
					parseParameters(line);
					
					
				//������������ӷ�֧�ж���������	
				}
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		
		
		
		
		
		System.out.println("HttpRequest:������Ϣ�������");
	}
	
	private String readLine() throws IOException {
		int d = -1;
		char c1='a',c2='a';
		StringBuilder builder = new StringBuilder();
		while((d = in.read())!=-1) {
			c2 = (char)d;
			if(c1==13&&c2==10) {
				break;
			}
			builder.append(c2);
			c1 = c2;
		}
		return builder.toString().trim();
	}
	public String getMethod() {
		return method;
	}
	public String getUri() {
		return uri;
	}
	public String getProtocol() {
		return protocol;
	}
	public String getHeader(String name) {
		return headers.get(name);
	}
	public String getRequestURI() {
		return requestURI;
	}
	public String getQueryString() {
		return queryString;
	}
	/**
	 * ���ݲ�������ȡ����ֵ
	 * @param name
	 * @return
	 */
	public String getParameter(String name) {
		return parameters.get(name);
	}
	
	
}







