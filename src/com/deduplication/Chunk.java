package com.deduplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Hua Li
 * A class representation of a chunk
 */
public class Chunk {
	private String id;
	private int fileCounter;
	private byte[] data;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Integer getFileCounter() {
		return fileCounter;
	}

	public void setFileCounter(Integer fileCounter) {
		this.fileCounter = fileCounter;
	}
	
	public void setFileCounter(String fileCounterStr) {
		this.fileCounter = Integer.parseInt(fileCounterStr);
	}

	
	/**
	 * @param chunkHash
	 * @param chunkData
	 * Save chunk to disk
	 */
	public static void saveChunkFile(String chunkHash, byte[] chunkData) {
	    File file =new File("chunks");   //create folder when not exists    
	    if  (!file .exists()  && !file .isDirectory())      
	    {       
	        file .mkdir();    
	    } else	{   
	    }  
		try { 
			 FileOutputStream fos=new FileOutputStream("chunks/" + chunkHash);
	         fos.write(chunkData);
	         fos.flush();
	         fos.close(); 
		}
		catch(FileNotFoundException ex)   {
	         System.out.println("FileNotFoundException : " + ex);
		}
		catch(IOException ioe)  {
	         System.out.println("IOException : " + ioe);	
		}
	}
}
