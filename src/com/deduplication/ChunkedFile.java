package com.deduplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.nio.file.Files;
//import java.nio.file.Path;
//
//import javax.swing.text.html.HTMLDocument.Iterator;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Result;
//import javax.xml.transform.Source;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerConfigurationException;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

public class ChunkedFile {
	public static byte[] reafproInChunk(File file) {
		byte[] fileByteData = new byte[(int) file.length()];
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			fileInputStream.read(fileByteData);
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found.");
			e.printStackTrace();
		} catch (IOException e1) {
			System.out.println("Error Reading The File.");
			e1.printStackTrace();
		}
		return fileByteData;
	}

	public static void deleteFile(String fpath) throws IOException {
		//
	}

	public static byte[] retriveFileData(String fileId) {

		FileProfile fp = FileChunkMappings.getFileInformation(fileId);
		ArrayList<Chunk> chunks = fp.getChunks();
		if (fp != null && !chunks.isEmpty()) {
			byte[] data = new byte[fp.getLength().intValue()];
			int offset = 0;
			int len;
			File file;
			for (int i = 0; i < chunks.size(); i++) {
				Chunk chunk = chunks.get(i);

				if (i == chunks.size() - 1) {
					len = fp.getLastChunkSize();
				} else {
					len = fp.getChunkSize();

				}

				File chunkfile = new File("chunks/" + chunk.getId());
				try {
					FileInputStream inputStream = new FileInputStream(chunkfile);
					inputStream.read(data, offset, len);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				offset = offset + len;
			}

			return data;
		} else {
			return null;
		}
	}
}
