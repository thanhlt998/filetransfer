package filetransfer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThread extends Thread {
	private Socket socket;
	private BufferedReader reader;
	private BufferedOutputStream outputStream;
	private BufferedInputStream fileReader;

	public ClientThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			// set input, output stream to the client
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outputStream = new BufferedOutputStream(socket.getOutputStream());

			// read the filename
			String fileName = reader.readLine();
			System.out.println(
					"File name: " + fileName + " has been requested by " + socket.getInetAddress().getHostAddress());

			File file = new File(fileName);
			
			if (!file.exists()) {
				byte code = (byte) 0;
				outputStream.write(code);
				closeConnection();
			}
			else {
				byte code = (byte) 1;
				outputStream.write(code);
				
				fileReader = new BufferedInputStream(new FileInputStream(file));
				
				byte[] buffer = new byte[1024];
				
				int bytesRead = 0;
				
				while((bytesRead = fileReader.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
					
					outputStream.flush();
				}
				
				closeConnection();
				
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void closeConnection() throws IOException {
		if(outputStream != null) {
			outputStream.close();
		}
		
		if(reader != null) {
			reader.close();
		}
		
		if(fileReader != null) {
			fileReader.close();
		}
		
		if(outputStream != null) {
			socket.close();
		}
	}
}
