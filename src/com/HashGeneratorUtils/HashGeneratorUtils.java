package com.HashGeneratorUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

import com.deduplication.Chunk;
import com.deduplication.ChunkIndexTable;
import com.deduplication.FileProfile;
import com.deduplication.FileChunkMappings;

/**
 * @author Hua Li
 * This class is used to generate hash code for every chunk of a file
 */
public class HashGeneratorUtils {
	// Size of the chunk in KiloBytes
	// It has been tuned to gain best space savings, 
	private static int CHUNK_SIZE = 28;
	
	public static void genrateMD5(File file, FileProfile fpro) throws Exception {
		fpro.setChunkSize(CHUNK_SIZE*1024);
		hashFile(file, fpro, "MD5");
	}

	/**
	 * @param file: file to be chunked and hashed
	 * @param fpro
	 * @param algorithm: default algorithm is MD5
	 * @throws Exception
	 * It's the actual function responsible for hashing and chunking
	 */
	private static void hashFile(File file, FileProfile fpro, String algorithm) throws Exception {
		try (FileInputStream inputStream = new FileInputStream(file)) {
			//MessageDigest is the java built-in class to generate unique and reliable
			//fingerprint of data with different hash function
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			byte[] fileBuffer = new byte[(int) file.length()];
			byte[] chunkBuffer = new byte[CHUNK_SIZE*1024];
			int bytesRead = -1;
			boolean append = false;
			String chunkHash;
			ArrayList<Chunk> fileChunks = new ArrayList<Chunk>();
			
			int i = 0;
			int lastChunkSize = -1;
			//Read the chunk of file into a fix size buffer and generate 
			while ((bytesRead = inputStream.read(chunkBuffer)) != -1) {
				
				digest.update(chunkBuffer);
				byte[] hashedBytes = digest.digest();
				chunkHash = convertByteArrayToHexString(hashedBytes);
				//Get the instance of ChunkIndexTable which is a global hash map
				ChunkIndexTable index = ChunkIndexTable.getInstance();
				
				if(index.containsKey(chunkHash)){
					int count = Integer.parseInt(index.get(chunkHash)) + 1;
					index.put(chunkHash, Integer.toString(count));
				} else {
					index.put(chunkHash, Integer.toString(1));
					Chunk.saveChunkFile(chunkHash,chunkBuffer);
				}
				
				//Clear the array by filling it with 0
				java.util.Arrays.fill(chunkBuffer, 0,chunkBuffer.length-1,(byte)0);
				lastChunkSize = bytesRead;
				Chunk fileChunk = new Chunk();
				fileChunk.setId(chunkHash);
				//fileChunk.setNum(i);
				fileChunks.add(fileChunk);
				i=i+1;
			}
			
			fpro.setLastChunkSize(lastChunkSize);
			FileChunkMappings.writeFileMapping(fpro, fileChunks);

		} catch (NoSuchAlgorithmException | IOException ex) {
			throw new Exception("Could not generate hash from file", ex);
		}
	}

	/**
	 * @param hashedBytes
	 * @return Convert a byte array to a string
	 */
	private static String convertByteArrayToHexString(byte[] hashedBytes) {
		return DatatypeConverter.printHexBinary(hashedBytes);
	}
}