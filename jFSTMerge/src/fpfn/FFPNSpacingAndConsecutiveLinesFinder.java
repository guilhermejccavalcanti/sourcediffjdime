package fpfn;

import java.util.ArrayList;

import br.ufpe.cin.mergers.util.MergeConflict;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

public class FFPNSpacingAndConsecutiveLinesFinder {

	public static final String SSMERGE_SEPARATOR = "##FSTMerge##";

	public static final String DIFF3MERGE_SEPARATOR = "|||||||";

	private String body;

	private int consecutiveLines;

	private ArrayList<String> conflicts;
	
	private ArrayList<MergeConflict> mergeConflicts;



	public FFPNSpacingAndConsecutiveLinesFinder(FSTTerminal node){
		this.body = node.getBody();
		this.conflicts = splitConflictsInsideMethods();
	}

	public FFPNSpacingAndConsecutiveLinesFinder(FSTTerminal node,String mergetracking) {
		this.body = node.getBody();
		this.conflicts = splitConflictsInsideMethods();

	}

	public FFPNSpacingAndConsecutiveLinesFinder(String body){
		this.body = body;
		this.conflicts = splitConflictsInsideMethods();
	}

	public void checkFalsePositives(){
		if(conflicts.size() > 1){	
			for(String s : conflicts){
				this.auxCheckFalsePositives(s);
			}
		} else{
			this.auxCheckFalsePositives(conflicts.get(0));
		}	
	}

	public boolean checkConsecutiveLines(String[] splitConflictBody){
		boolean falsePositive = false;
		if(!splitConflictBody[0].equals("") && (!splitConflictBody[1].equals("") && !splitConflictBody[2].equals(""))) {
			String [] leftLines  = splitConflictBody[0].split("\n");
			String [] baseLines  = splitConflictBody[1].split("\n");
			String [] rightLines = splitConflictBody[2].split("\n");
			if(!baseLines[0].equals("")){
				String fixedElement =  baseLines[0];
				boolean foundOnLeft = this.searchFixedElement(fixedElement, leftLines);
				if(foundOnLeft){
					falsePositive = true;
					this.consecutiveLines++;
				}else{
					boolean foundOnRight = this.searchFixedElement(fixedElement, rightLines);
					if(foundOnRight){
						falsePositive = true;
						this.consecutiveLines++;
					}
				}
			}

		}
		return falsePositive;
	}

	public String [] splitConflictBody(String s){
		String [] splitBody = {"", "", ""};
		if(s.contains("|||||||")){
			String[] temp = s.split("\\|\\|\\|\\|\\|\\|\\|");

			String[] temp2 = temp[0].split("\n");
			splitBody[0] = extractLines(temp2);

			String [] baseRight = temp[1].split("=======");	
			temp2 = baseRight[0].split("\n");
			splitBody[1] = extractLines(temp2);
			temp2 = baseRight[1].split("\n");
			splitBody[2] = extractLines(temp2);
		}else{
			splitBody[1] = "";
			splitBody[0] = extractLines(s.split("=======")[0].split("\n"));
			splitBody[2] = extractLines(s.split("=======")[1].split("\n"));
		}

		return splitBody;
	}


	public int getConsecutiveLines() {
		return consecutiveLines;
	}

	public void setConsecutiveLines(int consecutiveLines) {
		this.consecutiveLines = consecutiveLines;
	}

	public static void main(String[] args) {
		/*String example = "public void m(){\n" +
				"<<<<<<< /Users/paolaaccioly/Desktop/Teste/jdimeTests/left/Example.java\n" +
				"        int a1;\n" +
				"||||||| /Users/paolaaccioly/Desktop/Teste/jdimeTests/base/Example.java\n" +
				"        int a;\n" +
				"=======\n" +
				"            int a;\n" +
				">>>>>>> /Users/paolaaccioly/Desktop/Teste/jdimeTests/right/Example.java\n" +
				"        int b;\n" +
				"        int c;\n" +
				"<<<<<<< /Users/paolaaccioly/Desktop/Teste/jdimeTests/left/Example.java\n" +
				"        int d1;\n" +
				"||||||| /Users/paolaaccioly/Desktop/Teste/jdimeTests/base/Example.java\n" +
				"        int d;\n" +
				"=======\n" +
				"        int d2;\n" +
				">>>>>>> /Users/paolaaccioly/Desktop/Teste/jdimeTests/right/Example.java\n" +
				"    }";
		String example2 = "hello world";
		System.out.println(example2.split("mamae")[0]);*/
		/*String s = "<<<<<<< /Users/paolaaccioly/Documents/testeConflictsAnalyzer/conflictsAnalyzer/fstmerge_tmp1437435093749/fstmerge_var1_6882939852718786152\n" +
				"		int x;" +
				"||||||| /Users/paolaaccioly/Documents/testeConflictsAnalyzer/conflictsAnalyzer/fstmerge_tmp1437435093749/fstmerge_base_7436445259957106246\n" +
				"=======\n" +
				"		int y;\n"+
				">>>>>>> /Users/paolaaccioly/Documents/testeConflictsAnalyzer/conflictsAnalyzer/fstmerge_tmp1437435093749/fstmerge_var2_5667963733764531246\n";
		 */
	}

	private boolean searchFixedElement(String fixedElement, String[] variant){
		boolean foundFixedElement = false;
		int i = 0;
		while(!foundFixedElement && i < variant.length){
			if(variant[i].equals(fixedElement)){
				foundFixedElement = true;
			}
			i++;
		}
		return foundFixedElement;
	}


	private void auxCheckFalsePositives(String s) {
		String [] splitConflictBody = this.splitConflictBody(s);
		boolean consecLines 		= false;
		consecLines 				= this.checkConsecutiveLines(splitConflictBody);
	}

	private ArrayList<String> splitConflictsInsideMethods(){
		ArrayList<String> conflicts = new ArrayList<String>();
		if(this.body.contains("<<<<<<<") && this.body.contains(">>>>>>>")){
			String [] temp = this.body.split("<<<<<<<");
			for(int i = 1; i < temp.length; i++){
				String temp2 = temp[i].split(">>>>>>>")[0];
				conflicts.add(temp2);
			}
		}else{
			conflicts.add(this.body);
		}
		return conflicts;
	}

	private String extractLines(String[] conflict) {
		String lines = "";
		if(conflict.length > 1){
			for(int i = 1; i < conflict.length; i++){
				if(i != conflict.length-1){
					lines = lines + conflict[i] + "\n";
				}else{
					lines = lines + conflict[i];
				}
			}
		}
		return lines;
	}
}
