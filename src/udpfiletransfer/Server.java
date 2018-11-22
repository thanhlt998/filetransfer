package udpfiletransfer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {
	public static void main(String[] args) {
		byte[] receiveData = new byte[2048];
		byte[] sendData = new byte[2048];
		
		try {
			DatagramSocket serverSocket = new DatagramSocket(9090);
			DatagramPacket sendPacket = null;

//			while (true) {
				// receive fileName
				DatagramPacket receivePacket = new DatagramPacket(new byte[2048], receiveData.length);
				serverSocket.receive(receivePacket);

				String fileName = new String(receivePacket.getData());
				System.out.println(fileName);
				
				// get the clientAddress and clientPort
				InetAddress clientAddress = receivePacket.getAddress();
				int clientPort = receivePacket.getPort();

				File file = new File(fileName.trim());
				FileInfo fileInfo = new FileInfo(file, sendData.length);
				
				// send the fileinfo to the client
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
				outputStream.writeObject(fileInfo);
				outputStream.flush();

				sendPacket = new DatagramPacket(byteArrayOutputStream.toByteArray(),
						byteArrayOutputStream.toByteArray().length, clientAddress, clientPort);
				serverSocket.send(sendPacket);
				
				// send file
				if(fileInfo.isExist()) {
					BufferedInputStream fileReader = new BufferedInputStream(new FileInputStream(file));

					int bytesRead = 0;
					while ((bytesRead = fileReader.read(sendData)) != -1) {
						sendPacket = new DatagramPacket(sendData, bytesRead, clientAddress, clientPort);
						serverSocket.send(sendPacket);
					}
//				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
