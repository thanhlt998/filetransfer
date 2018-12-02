package filetransfer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.commons.validator.routines.InetAddressValidator;

public class Client {
	public static void main(String[] args) {
		try {
			//input stream
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

			String ipAddress = "";
			String fileName = "";

			// validate the ip address
			boolean isValid = false;
			InetAddressValidator validator = new InetAddressValidator();
			while (!isValid) {
				System.out.println("Enter the server IP Address: ");
				ipAddress = reader.readLine();
				isValid = validator.isValid(ipAddress);
			}
			
			// enter the filename
			System.out.println("Enter the filename: ");
			fileName = reader.readLine();
			
			// connect to server through port 9090
			Socket socket = new Socket(ipAddress, 9090);
			
			// set input, output stream
			BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			// send the file name
			out.println(fileName);
			
			int code = inputStream.read();
			System.out.println(code);
			
			// file is existed
			if (code != 0) {
				// write to file stream
				long beginTime = System.currentTimeMillis();
				BufferedOutputStream outputFile = new BufferedOutputStream(
						new FileOutputStream(new File("client-" + fileName)));

				byte[] buffer = new byte[1024];

				int bytesRead = 0;

				while ((bytesRead = inputStream.read(buffer)) != -1) {
					System.out.print(".");

					outputFile.write(buffer, 0, bytesRead);
					outputFile.flush();
				}
				System.out.println();
				System.out.println("File: " + fileName + " successfully downloaded!");
				long endTime = System.currentTimeMillis();
				System.out.println("Time to download: " + (endTime - beginTime));
			} else {
				System.out.println("File is not present on the server!");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
