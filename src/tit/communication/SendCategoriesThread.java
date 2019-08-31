package tit.communication;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import tit.configuration.GeneralConfig;

public class SendCategoriesThread extends Thread {

	private Socket clientSocket;
	
	public SendCategoriesThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	@Override
	public void run() {
		try {
			sendCategories();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendCategories() throws ClassNotFoundException, IOException
	{
		BufferedOutputStream out = null;
		try
		{
			//Create output stream
			out = new BufferedOutputStream(clientSocket.getOutputStream());

			//			DBUtil dbUtil = new DBUtil();
			//			ArrayList<String> categories = dbUtil.getCategories();
			//
			StringBuffer strbuf = new StringBuffer();

			//Default category
			strbuf.append(GeneralConfig.randomCategory);
			//			for(String s : categories)
			//			{
			//				strbuf.append(",").append(s);
			//			}

			String stringToSend = strbuf.toString();
			stringToSend.substring(0,stringToSend.length() - 1);

			out.write(stringToSend.getBytes());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
//		finally 
//		{
//			if(out != null)
//			{
//				out.flush();
//				out.close();
//			}
//		}
	}
}
