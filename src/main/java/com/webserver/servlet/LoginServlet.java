package com.webserver.servlet;

import java.io.RandomAccessFile;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
/**
 * ��¼ҵ��
 * @author ta
 *
 */
public class LoginServlet extends HttpServlet{
	public void service(HttpRequest request,HttpResponse response) {
		//1��ȡ��¼��Ϣ
		String username 
			= request.getParameter("username");
		String password 
			= request.getParameter("password");
		
		//2��֤��¼
		try (
			RandomAccessFile raf
				= new RandomAccessFile("user.dat","r");
		){
			for(int i=0;i<raf.length()/100;i++) {
				raf.seek(i*100);
				//��ȡ�û���
				byte[] data = new byte[32];
				raf.read(data);
				String name = new String(data,"UTF-8").trim();
				if(name.equals(username)) {
					//������
					raf.read(data);
					String pwd = new String(data,"UTF-8").trim();
					if(pwd.equals(password)) {
						//��¼�ɹ�
						forward("/myweb/login_success.html", request, response);
						return;
					}
					break;
				}
			}//forѭ������
			
			//����ߵ����ͳһ���õ�¼ʧ��
			forward("/myweb/login_fail.html",request,response);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}







