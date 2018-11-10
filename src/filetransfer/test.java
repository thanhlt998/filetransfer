package filetransfer;

import java.io.File;

public class test {
	public static void main(String[] args) {
		File file = new File("server-file.txt");
		System.out.println(file.exists());
	}
}
