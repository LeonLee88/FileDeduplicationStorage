package comandlinetool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.HashGeneratorUtils.HashGeneratorUtils;
import com.deduplication.Chunk;
import com.deduplication.ChunkIndexTable;
import com.deduplication.ChunkedFile;
import com.deduplication.FileChunkMappings;
import com.deduplication.FileProfile;

public class DBLS {

	public static void main(String[] args) {
			ChunkIndexTable.getInstance();
			InputStreamReader is=new InputStreamReader(System.in);
			BufferedReader br=new BufferedReader(is);
			try{
				while(true){
				System.out.println("##############################################################################################");
				System.out.println("########                           DBLS Command Line MENU                              #######");
				System.out.println("##############################################################################################");
				System.out.println("##                                                                                          ##");
				System.out.println("##                                    1. LIST FILES                                         ##");
				System.out.println("##                                    2. UPLOAD                                             ##");
				System.out.println("##                                    3. REMOVE                                             ##");
				System.out.println("##                                    4. DOWNLOAD                                           ##");
				System.out.println("##                                    5. QUIT                                               ##");
				System.out.println("##                                                                                          ##");			
				System.out.println("##############################################################################################");
				System.out.print(" Command # >> ");	
				
            		switch ((String) br.readLine()) {
						case "1": { jump1();break; }	//list file
						case "2": { jump2();break; }	//upload
						case "3": { jump3();break; }	//remove
						case "4": { jump4();break; }	//download
						case "5": { jump5();break; }	//quit&write json
            		} //switch end
				} //while end
			}catch(IOException e){
				System.out.println("System Error!");
				e.printStackTrace();
			}
//			finally{
//	            try{
//	                is.close();
//	                br.close();
//	            }catch(IOException e){
//	                System.out.println("Stream Close Error");
//	                e.printStackTrace();
//	            }
//	        }
			
	}	// main end
	
	public static void jump1() {
	// List File
		System.out.println("----------------------------------------------------------------------------------------------");
		System.out.println("                                        *FILE LIST*                                           ");
		System.out.println("**********************************************************************************************");
		System.out.printf(" # %s\t%-40s\t%-10s\t%s\n","No.","                    Name","Size","Time");
		System.out.println("----------------------------------------------------------------------------------------------");
		ArrayList<String> namelist = new ArrayList<String>(FileChunkMappings.getNameByFilename());
		ArrayList<String> sizelist = new ArrayList<String>(FileChunkMappings.getSizeByFilename());
		ArrayList<String> timelist = new ArrayList<String>(FileChunkMappings.getTimeByFilename());
		
		for(int i=0;i<namelist.size();i++){
            System.out.printf(" # %s\t%-40s\t%-10s\t%s\n",i+1,namelist.get(i),sizelist.get(i),timelist.get(i));
        }
		System.out.println("-----------------------------------     Back to MENU     -------------------------------------");
        System.out.println();
		return;
	}
	
	public static void jump2() {
	// Upload File	
		System.out.println("----------------------------------------------------------------------------------------------");
		InputStreamReader is=new InputStreamReader(System.in);
        BufferedReader br=new BufferedReader(is);
        String filename;
        try{
        	while(true){
        		System.out.println(" # UPLOAD: Please ENTER the file path (ENTER q to return to menu)");
        		System.out.print(" # File Path >> ");
        		filename=(String) br.readLine();
        		if(!filename.equals("q")){
        			uploadfile(filename);
        			System.out.println(" # Upload File Success!");
        			System.out.println();	
        		}else{
        			return;
        		}
        	}

        }catch(IOException e){
            System.out.println("System Error!");
            e.printStackTrace();
        }
//        finally{
//            try{
//                is.close();
//                br.close();
//            }catch(IOException e){
//                System.out.println("Stream Close Error");
//                e.printStackTrace();
//            }
//        } 
	} // jump2 end
	
	public static void jump3() {
	// Remove File
		System.out.println("----------------------------------------------------------------------------------------------");

		InputStreamReader is=new InputStreamReader(System.in);
        BufferedReader br=new BufferedReader(is);
        String filename;
        try{
        	while (true){
        		System.out.println(" # REMOVE: Please ENTER the filename (ENTER q to return to menu)");
        		System.out.print(" # Filename >> ");
        		filename=(String) br.readLine();
        		if(!filename.equals("q")){
        			removefile(filename);
        			System.out.println(" # Remove File Success!");
        			System.out.println();
        		}else{
        			return;
        		}
        	}

        }catch(IOException e){
            System.out.println("System Error!");
            e.printStackTrace();
        }
//        finally{
//            try{
//                is.close();
//                br.close();
//            }catch(IOException e){
//                System.out.println("Stream Close Error");
//                e.printStackTrace();
//            }
//        } 
	} // jump3 end
	
	public static void jump4() {
	// Download File
		System.out.println("----------------------------------------------------------------------------------------------");
		InputStreamReader is=new InputStreamReader(System.in);
        BufferedReader br=new BufferedReader(is);
        String filename;
        String filepath;
        try{
        	System.out.println(" # DOWNLOAD: Please ENTER the Source File Name");
        	System.out.print(" #   Source Filename >> ");
            filename=(String) br.readLine();
            System.out.println(" # DOWNLOAD: Please ENTER the Destination File Directory");
            System.out.print(" #   Dest. Directory >> ");    
            filepath=(String) br.readLine();
               downloadfile(filename,filepath);
       		System.out.println(" # Download File Success!");
       		System.out.println();
               return;
        }catch(IOException e){
            System.out.println("System Error!");
            e.printStackTrace();
        }
//        finally{
//            try{
//                is.close();
//                br.close();
//            }catch(IOException e){
//                System.out.println("Stream Close Error");
//                e.printStackTrace();
//            }
//        } 
	} // jump4 end
	
	public static void jump5() {
		ChunkIndexTable.getInstance().Save();
		System.out.println("####################################     Thank You!     ######################################");
		System.exit(0);	
	}
//********************************************
	
	
	public static void uploadfile(String name) {
		File file = new File(name);
		FileProfile fpro = new FileProfile(file);
		try {
			HashGeneratorUtils.genrateMD5(file, fpro);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return;
	} //uploadfile end
	
	
	   public static void removefile(String fileName){
		   try {			   
			   String fileId = FileChunkMappings.getIdByFilename(fileName);
			   ArrayList<Chunk> chunklist = FileChunkMappings
						.getChunksByFile(fileId);
				ChunkIndexTable.DeleteChunks(chunklist);
				FileChunkMappings.deleteFile(fileId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			return;
		} //removefile end	
	
	   
//   public static void deletefile2(String pathName){  
//	   // not in use, delete file from absolute path
//       File tempFile =new File( pathName.trim());  
//	   
//	   try {
//		   String fileName = tempFile.getName();
//		   String fileId = FileChunkMappings.getIdByFilename(fileName);
//		   ArrayList<Chunk> chunklist = FileChunkMappings
//					.getChunksByFile(fileId);
//			ChunkIndexTable.DeleteChunks(chunklist);
//			FileChunkMappings.deleteFile(fileId);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
//	} //deletefile2 end
   
	private static void downloadfile(String filename,String filepath) {
	// download file
		File fileToSave = new File(filepath);
		if (!fileToSave.exists()) {
			try {
				fileToSave.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			FileOutputStream fos = new FileOutputStream(
					fileToSave);
			fos.write(ChunkedFile.retriveFileData(FileChunkMappings.getIdByFilename(filename)));
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	} //downloadfile end

	
}
