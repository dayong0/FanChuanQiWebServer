package com.webserver.servlet;

import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * ���ڴ���ע��ҵ��
 * @author ta
 *
 */
public class RegServlet extends HttpServlet{
	public void service(HttpRequest request,HttpResponse response) {
		System.out.println("RegServlet:��ʼ����ע��...");
		/*
		 * 1:ͨ��request��ȡ�û��ύ��ע����Ϣ
		 * 2:����Ϣд��user.dat�ļ�����
		 * 3:����response��Ӧע����ҳ��
		 */
		String username 
			= request.getParameter("username");
		String password
			= request.getParameter("password");
		String nickname
			= request.getParameter("nickname");
		int age = Integer.parseInt(
			request.getParameter("age")	
		);
		System.out.println("username:"+username);
		System.out.println("password:"+password);
		System.out.println("nickname:"+nickname);
		System.out.println("age:"+age);
		
		/*
		 * ÿ���û�ռ��100�ֽ�
		 * �����û��������룬�ǳ�Ϊ�ַ�����32�ֽ�
		 * ������intֵ�̶���4�ֽڡ�
		 */
		try(
			RandomAccessFile raf 
				= new RandomAccessFile(
						"user.dat","rw");	
		) {
			//Ҫ�ȼ����û��Ƿ��Ѿ�������
			for(int i=0;i<raf.length()/100;i++) {
				raf.seek(i*100);
				byte[] data = new byte[32];
				raf.read(data);
				String name = new String(data,"UTF-8").trim();
				if(name.equals(username)) {
					//�ظ��û�
					forward("/myweb/have_user.html", request, response);
					return;
				}
			}
			
			
			raf.seek(raf.length());
			
			//д���û���
			byte[] data = username.getBytes("UTF-8");
			//�������鵽32�ֽ�
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			//д����
			data = password.getBytes("UTF-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			//д�ǳ�
			data = nickname.getBytes("UTF-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			//д����
			raf.writeInt(age);
			
			
			//��Ӧע��ɹ���ҳ����ͻ���
			forward("/myweb/reg_success.html", request, response);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		
		System.out.println("RegServlet:����ע�����!");
	}
}




