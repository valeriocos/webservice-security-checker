package servlet;

import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class SecurityCheckerServlet
 */
@WebServlet("/SecurityCheckerServlet")
public class SecurityCheckerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final int BUFFER_SIZE = 4096;
	private int maxFileSize = 10000 * 1024;
	private int maxMemSize = 4 * 1024;
	private static final String SAVE_DIR = "C:" + File.separator + "uploadFiles";
	private static final String WORKSPACE_DIR = "workspace";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SecurityCheckerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		  
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		File projectFolder = null;
        if (isMultipart) {
        	// Create a factory for disk-based file items
        	FileItemFactory factory = new DiskFileItemFactory();

        	// Create a new file upload handler
        	ServletFileUpload upload = new ServletFileUpload(factory);
        	
        	// Create host folder
        	File hostFolder = new File(SAVE_DIR);
        	if (!hostFolder.exists()) {
        		hostFolder.mkdirs();
        	}
 
            try {
            	// Parse the request
            	List items = upload.parseRequest(request);
                Iterator iterator = items.iterator();
                //take only the first file
                while (iterator.hasNext()) {
                    FileItem item = (FileItem) iterator.next();
                    if (!item.isFormField()) {
                        String fileName = hostFolder + File.separator + item.getName();
                        
                        //deal with projects with the same name
                        projectFolder = createProjectFolder(stripExtension(fileName), 0);
                        File uploadedFile = new File(projectFolder + File.separator + item.getName());
                        item.write(uploadedFile);
                        
                        unzipProject(uploadedFile, projectFolder);
                        
                    }
                    
                    break;
                }
            } catch (Exception e1) {
                this.deleteFile(projectFolder);
            }
        }
	}
	
	
	private File createProjectFolder(String candidateName, int counter) {
		File projectPath = new File(candidateName + File.separator);
        if (!projectPath.exists()) {
            projectPath.mkdir();
            return projectPath;
        }
        else {
        	return createProjectFolder(stripNumbering(candidateName) + String.valueOf(counter), counter + 1);
        }
	}
	
	
	private void unzipProject(File zipProject, File destProject) {
		String errorMessage = null;
		String destDirectory = destProject.getPath();
		String zipFilePath = zipProject.getPath();
		try {
	        File destDir = new File(destDirectory + File.separator + WORKSPACE_DIR);
	        if (!destDir.exists()) {
	            destDir.mkdir();
	        }
	        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
	        ZipEntry entry = zipIn.getNextEntry();
	        // iterates over entries in the zip file
	        while (entry != null) {
	            String filePath = destDir + File.separator + entry.getName();
	            if (!entry.isDirectory()) {
	                extractFile(zipIn, filePath);
	            } else {
	                // if the entry is a directory, make the directory
	                File dir = new File(filePath);
	                dir.mkdir();
	            }
	            zipIn.closeEntry();
	            entry = zipIn.getNextEntry();
	        }
	        zipIn.close();
	        
		}
	    catch (Exception e) { deleteFile(destProject); }

    }
	
	
	private void extractFile(ZipInputStream zipIn, String filePath) throws Exception {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
	
	
	private String stripExtension(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}
	
	private String stripNumbering(String fileName) {
		return fileName.replaceAll("[0-9]+$", "");
	}
	
	
	private void deleteFile(File toDelete) {
		if (toDelete != null) {
        	try {
        		toDelete.delete();
        	}
        	catch (Exception e2) {
        		System.out.println("File does not exist or you are trying to read a file that has been deleted!");
        	}
        }
	}
	
}


