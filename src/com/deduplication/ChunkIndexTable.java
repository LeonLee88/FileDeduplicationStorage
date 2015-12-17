package com.deduplication;

import java.awt.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Hua Li
 * This class is an implementation of LinkedHashMap interface, which is used as
 * a cache for hashes of all chunks. With this class we can have fast access to the
 * chuck's hash and eliminate the duplicated chunk. It's implemented with Singleton pattern.
 */
public class ChunkIndexTable extends LinkedHashMap<String, String> {
	//'uniqueInstance' is necessary to singleton design pattern
	private static ChunkIndexTable uniqueInstance;
	
	//chunkIndex.json is disk file to contain all the chunks hash code and the occurrence
	private String path = "chunkIndex.json";

	public ChunkIndexTable() {
		// key is hash of a chunk, value is the number of file which contains
		// that chunk
		this.load();
	}

	//Implementation of the getInstance of singleton design pattern
	public static ChunkIndexTable getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new ChunkIndexTable();
		}
		return uniqueInstance;
	}

	
	/**
	 * @param chunkHash
	 * @return Return true if the chunk is duplicated
	 */
	public boolean IsDuplicatedChunk(String chunkHash) {

		if (ChunkIndexTable.getInstance().containsKey(chunkHash)) {
			return true;
		}
		return false;
	}

	/**
	 * Read chunkIndex.json and load the global chunks's fingerprints and occurrence into the hashmap
	 */
	private void load() {
		byte[] encoded;

		try {

			encoded = Files.readAllBytes(Paths.get(path));
			String indexJsonStr = new String(encoded);
			JSONParser parser = new JSONParser();
			ContainerFactory containerFactory = new ContainerFactory() {
				public Map createObjectContainer() {
					return new LinkedHashMap();
				}

				public LinkedList creatArrayContainer() {
					return new LinkedList();
				}

			};

			LinkedHashMap<String, String> m = (LinkedHashMap) parser.parse(
					indexJsonStr, containerFactory);
			this.putAll(m);

		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * Write the key-value pair to disk as a json file
	 */
	public void Save() {
		StringWriter out = new StringWriter();
		try {
			FileWriter jsonFileWriter = new FileWriter(path);
			JSONObject indexJsonObject = new JSONObject(this.getInstance());
			jsonFileWriter.write(indexJsonObject.toJSONString());
			jsonFileWriter.flush();
			jsonFileWriter.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String jsonText = out.toString();
		System.out.print(jsonText);
	}

	/**
	 * @param chunks: Array list of the chunks
	 * @return true if it delete chunks and update the hash map successfully
	 */
	public static boolean DeleteChunks(ArrayList<Chunk> chunks) {

		// Check chunks in index table, increase counter or delete it

		for (Chunk chunk : chunks) {
			String chunkId = chunk.getId();// get hash
			if (ChunkIndexTable.getInstance().containsKey(chunkId)) {
				int i = Integer.parseInt(ChunkIndexTable.getInstance().get(
						chunkId));
				if (i >= 1) {
					i = i - 1;
					String s = String.valueOf(i);
					ChunkIndexTable.getInstance().put(chunkId, s);
				}
				if (i == 0) {
					String v = ChunkIndexTable.getInstance().get(chunkId);
					ChunkIndexTable.getInstance().remove(chunkId, v);
					File fileToDelete = new File("chunks/" + chunkId);
					if (fileToDelete.isFile()) {
						fileToDelete.delete();
					}
				}
			}
		}
		return true;
	}
}
