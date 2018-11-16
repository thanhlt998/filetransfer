package udpfiletransfer;

import java.io.File;
import java.io.Serializable;

public class FileInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private String fileName;
	private boolean isExist;
	private int noPiece;
	private int lastPieceLength;
	
	public FileInfo(File file, int bufferSize) {
		this.isExist = file.exists();
		this.fileName = file.getName();
		
		this.lastPieceLength = (int) file.length() % bufferSize;
		this.noPiece = (int) file.length() / bufferSize + (lastPieceLength > 0 ? 1 : 0);
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public boolean isExist() {
		return isExist;
	}
	
	public int getNoPiece() {
		return noPiece;
	}
	
	public int getLastPieceLength() {
		return lastPieceLength;
	}
}
