import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class RepositoryAnalyzer {

	//what keyword to look for
	static String whatToLookFor = "regression";

	
	public static void main(String[] args) throws NoHeadException, IOException, GitAPIException {
		
		String baseDir = "C:\\Users\\gianni\\Desktop\\work\\microservices";
		File file = new File(baseDir);
		String[] directories = file.list();
		
		BufferedWriter writer = new BufferedWriter( new FileWriter("C:\\Users\\gianni\\Desktop\\work\\microservices\\stats.txt"));
		PrintWriter printWriter = new PrintWriter(writer);
		
		printWriter.println(Arrays.toString(directories));
		
		
		for(String d : directories){
			String projectDir = baseDir+"\\"+d;
			File pdir = new File(projectDir);
			if(pdir.isDirectory()){
				BufferedReader br = new BufferedReader(new FileReader(projectDir+"\\"+"programmingLanguage.txt"));
				String line = br.readLine();
				br.close();
				String fileExtension = line;
				printWriter.println("analyzing repository: "+d);
				getRegressionIssuesInCommitMessages(projectDir, printWriter);
				getRegressionIssuesInComments(projectDir, fileExtension, printWriter);
			}
		}
		
		printWriter.close();
		writer.close();
		System.out.println("all done");
		
	}
	
	//gets comments is code and looks for regression
	private static void getRegressionIssuesInComments(String localDir, String fileExtension, PrintWriter printWriter) throws IOException {
		
		ArrayList<File> javaFiles = new ArrayList<File>();
		ArrayList<String> comments = new ArrayList<String>();

		finder(localDir, javaFiles, fileExtension);
		printWriter.println("."+fileExtension + " source code files found: "+javaFiles.size());
		for(File f : javaFiles){
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			int lineNumber = 0;
			while ((line = br.readLine()) != null) {
				if(line.toLowerCase().contains(whatToLookFor)){
					//single line comment
					comments.add("at line "+lineNumber+" of file "+f.getAbsolutePath()+". Source code: "+line);
				}
				lineNumber++;
			}
			br.close();
		}	
		printWriter.println(whatToLookFor+" in source code: ");
		
		for(String c : comments)
			printWriter.println(c);
		
		printWriter.println("Total: "+comments.size());
		printWriter.println();

	}
	
	//gets list of java files in directories and subdirectories
	public static void finder(String dirName, ArrayList<File> javaFiles, String fileExtension){
        
		File dir = new File(dirName);
		File[] filesList = dir.listFiles();
        for (File file : filesList) {
        	if (file.getName().endsWith("."+fileExtension)) {
        		javaFiles.add(file);
            } else if (file.isDirectory()) {
                finder(file.getAbsolutePath(), javaFiles, fileExtension);
            }
        }
        

    }

	//gets commit messages and looks for regression
	static public void getRegressionIssuesInCommitMessages(String localDir, PrintWriter printWriter) throws IOException, NoHeadException, GitAPIException {
	
	int commitsCount = 0;
	int regressionCount = 0;

    ArrayList<String> logMessages = new ArrayList<String>();

    Git git = Git.open(new File(localDir));
    Repository repository = git.getRepository();
    
    printWriter.println("On branch: "+repository.getBranch()); 
    Iterable<RevCommit> log = git.log().call();
    for (Iterator<RevCommit> iterator = log.iterator(); iterator.hasNext();) {
    	RevCommit rev = iterator.next();
    	logMessages.add(rev.getFullMessage());
    	commitsCount++;
    }
    
    printWriter.println(whatToLookFor+" in commit messages: ");
    for(String s : logMessages){
    	if(s.toLowerCase().contains(whatToLookFor)){
    		printWriter.println(s);
			regressionCount++;
    	}
    }
    printWriter.println("Total: "+regressionCount);

    printWriter.println("processed " + commitsCount + " commits.");

    repository.close();

	}
	
	
}
