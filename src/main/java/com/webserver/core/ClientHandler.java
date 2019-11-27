package com.webserver.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;

import com.webserver.exception.EmptyRequestException;
import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import com.webserver.servlet.HttpServlet;

/**
 * ���̸߳�������ָ���ͻ��˵Ľ�������
 * ������̷�Ϊ����:
 * 1:׼������
 * 2:��������
 * 3:������Ӧ
 * @author ta
 *
 */
public class ClientHandler implements Runnable{
	private Socket socket;
	public ClientHandler(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		try {
			//1 ׼������
			HttpRequest request = new HttpRequest(socket);
			HttpResponse response = new HttpResponse(socket);
			/*
			 * 2 ��������
			 * 2.1:ͨ��request��ȡ�ͻ����������Դ
			 *     ��Ӧ�ĳ���·��requestURI��ֵ
			 * 2.2:��webappsĿ¼��ͨ����Ӧ�ĳ���·
			 *     ��Ѱ�Ҹ���Դ    
			 */
			String path = request.getRequestURI();
			System.out.println("path:"+path);
			
			
			//�����жϸ������Ƿ�����Ϊҵ��
			HttpServlet servlet 
				= ServerContext.getServlet(path);
			if(servlet!=null) {
				//����ҵ��
				servlet.service(request,response);
							
			}else {			
				File file = new File("./webapps"+path);
				//System.err.println(file.getAbsolutePath());
				//System.err.println(file.getPath());
				if(file.exists()) {
					System.out.println("����Դ���ҵ�!");
					
					response.setEntity(file);
		
					System.out.println("��Ӧ�ͻ������!");
				}else {
					System.out.println("����Դ������!");
					
					File notFound 
						= new File("./webapps/root/404.html");
					//����״̬����������
					response.setStatusCode(404);
					response.setStatusReason("NOT FOUND");
					
					//���������ļ�Ϊ404ҳ��
					response.setEntity(notFound);
									
				}
			}
			
			//3��Ӧ�ͻ���
			response.flush();
		} catch(EmptyRequestException e) {
			//������������󣬲����κδ���
			System.out.println("������...");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/*
			 * ������Ӧ��Ϻ���ͻ��˶Ͽ�����
			 * ���������HTTP1.0�ķ�ʽ��
			 * 1.1���������Ӻ���ж������
			 * ��Ӧ��������Ҫ�������Ϣͷ��
			 * ��Ӧͷ�Ĵ���
			 */
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
}





