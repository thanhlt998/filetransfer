package udpfiletransfer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import org.apache.commons.validator.routines.InetAddressValidator;

public class Client {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		try {
			DatagramSocket clientSocket = new DatagramSocket(0);
			byte[] receiveData = new byte[2048];
			byte[] sendData = new byte[2048];
			DatagramPacket receivePacket = null;
			
			InetAddress serverAddress = null;
			
			clientSocket.setSoTimeout(10000);
			
			//Input ip and fileName
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String ipAddress = "";
			String fileName = "";
			
			//validate the ip address
			boolean isValid = false;
			InetAddressValidator validator = new InetAddressValidator();
			while(!isValid) {
				System.out.println("Enter the server IP Address: ");
				ipAddress = reader.readLine();
				isValid = validator.isValid(ipAddress);
				serverAddress = InetAddress.getByName(ipAddress);
			}
			
			System.out.println("Enter the filename: ");
			fileName = reader.readLine();
			
			sendData = fileName.trim().getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 9090);
			clientSocket.send(sendPacket);
			
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(receivePacket.getData());
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			FileInfo fileInfo = (FileInfo) objectInputStream.readObject();
//			objectInputStream.close();
			
			if(!fileInfo.isExist()) {
				System.out.println("File not existed!");
			}
			else {
				long beginTime = System.currentTimeMillis();
				BufferedOutputStream outputFile = new BufferedOutputStream(new FileOutputStream(new File("client-" + fileName)));
				
				for(int i = 0; i < fileInfo.getNoPiece() - 1; i++) {
					receivePacket = new DatagramPacket(receiveData, receiveData.length);
					clientSocket.receive(receivePacket);
					outputFile.write(receivePacket.getData());
				}
				
				receivePacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receivePacket);
				outputFile.write(receivePacket.getData(), 0, fileInfo.getLastPieceLength());
				
				outputFile.close();
				
				System.out.println("Download file successfully!");
				long endTime = System.currentTimeMillis();
				System.out.println("Time to download: " + (endTime - beginTime));
			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
