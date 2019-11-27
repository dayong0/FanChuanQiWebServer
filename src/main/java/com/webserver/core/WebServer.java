package com.webserver.core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WebServer��ģ��Tomcat��һ��web����
 * web��������ͬʱ����������Ӧ�ã������ṩ��
 * ��ͻ���(ͨ���������)�����������Լ���������
 * ����ͻ��˵�Ӧ�ò㽻��(�漰��TCPЭ���Լ�HTTP
 * Э��)�����֧�֡�����web������ʹ�ó���Ա����
 * �ľ����Ƿ��ھ���WebӦ�õ�ҵ���ϡ�
 * 
 * webapp(����Ӧ��):�����������ݴ�������ҳ��
 * ͼƬ��������̬�ز��Լ�java������룬����
 * ��������ʱ�׳Ƶ�һ��"��վ"��ȫ�����ݡ�
 * 
 * 
 * TCPЭ�飬���ڴ���㣬������̨�����֮��ͨ��
 * ���紫�����ݵ�Э�顣
 * 
 * HTTPЭ�飬����Ӧ�ò㣬�涨��˫���������ݵ�
 * ��ʽ���Լ���������
 * 
 * 
 * @author ta
 *
 */
public class WebServer {
	private ServerSocket server;
	private ExecutorService threadPool;
	/**
	 * ��ʼ�������
	 */
	public WebServer() {
		try {
			System.out.println("�������������...");
			server = new ServerSocket(8088);
			threadPool = Executors.newFixedThreadPool(50);
			System.out.println("������������!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * ����˿�ʼ�����ķ���
	 */
	public void start() {
		try {
			while(true) {
				System.out.println("�ȴ��ͻ�������...");
				Socket socket = server.accept();
				System.out.println("һ���ͻ���������!");
				
				//����һ���̴߳���ÿͻ��˽���
				ClientHandler handler 
					= new ClientHandler(socket);
				
				threadPool.execute(handler);
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		WebServer server = new WebServer();
		server.start();
	}
}









