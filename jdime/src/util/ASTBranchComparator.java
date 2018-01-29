package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jastadd.extendj.ast.Stmt;

import de.fosd.jdime.Main;
import de.fosd.jdime.artifact.ast.ASTNodeArtifact;
import de.fosd.jdime.config.merge.Revision;
import de.fosd.jdime.matcher.matching.Matching;
import de.fosd.jdime.strategy.StructuredStrategy;

/**
 * FPFN
 * Compares two given tree branches
 * @author Guilherme
 *
 */
public class ASTBranchComparator {

	public final static int LAST_COMMON_STMT_INDEX 			= 0;
	public final static int LAST_COMMON_NODE_INDEX 			= 1;
	public final static int LAST_COMMON_STMT_INDEX_LEFT	 	= 2;
	public final static int LAST_COMMON_STMT_INDEX_RIGHT 	= 3;


	public int countEditionsToDifferentPartsOfSameStmt(Revision baseRevision, ArrayList<ArrayList<ASTNodeArtifact>> branchesFromLeft, ArrayList<ArrayList<ASTNodeArtifact>> branchesFromRight){
		int editionsToDifferentPartsOfSameStmt = 0;
		for(ArrayList<ASTNodeArtifact> leftBranch : branchesFromLeft){
			for(ArrayList<ASTNodeArtifact> rightBranch : branchesFromRight){
				if(!isChangesInTheSameParentNode(leftBranch, rightBranch)){
					ArrayList<ASTNodeArtifact> results = findDeeperEqualNode(leftBranch, rightBranch);
					ASTNodeArtifact comonStmt = results.get(LAST_COMMON_STMT_INDEX);
					ASTNodeArtifact leftStmt  = results.get(LAST_COMMON_STMT_INDEX_LEFT);
					ASTNodeArtifact rightStmt = results.get(LAST_COMMON_STMT_INDEX_RIGHT);				
					if(isStmtValid(comonStmt)){
						Map<Revision, Matching<ASTNodeArtifact>> matches = comonStmt.getMatches();
						ASTNodeArtifact baseStmt = matches.get(baseRevision).getMatchingArtifact(comonStmt);
						if(isContentValid(leftStmt, baseStmt, rightStmt)){
							editionsToDifferentPartsOfSameStmt++;
							logFalseNegative(leftBranch,rightBranch, comonStmt,leftStmt,rightStmt);
						}
					}
				}
			}
		}
		return (editionsToDifferentPartsOfSameStmt/2);

	}

	private void logFalseNegative(ArrayList<ASTNodeArtifact> leftBranch, ArrayList<ASTNodeArtifact> rightBranch, ASTNodeArtifact node, ASTNodeArtifact left, ASTNodeArtifact right) {
		String leftBranchRepresentation  = leftBranch.toString();
		String rightBranchRepresentation = rightBranch.toString();
		String fnBranch 	= (printCommonBranch(node)).toString();
		String leftCode 	= left.prettyPrint();
		String rightCode 	= right.prettyPrint();
		String entry = (Main.CURRENT_FILE+";"+fnBranch+";"+rightBranchRepresentation+";"+leftBranchRepresentation+";"+leftCode+";"+rightCode);
		StructuredStrategy.ASTBranchesResult.LOG_EDITIONS_TO_DIFFERENT_PARTS_OF_SAME_STMT.add(entry);
	}

	private boolean isStmtValid(ASTNodeArtifact stmt) {
		return ((null != stmt) && !(stmt.getASTNode() instanceof org.jastadd.extendj.ast.Block));
	}

	private boolean isStmt(ASTNodeArtifact deeperEqualNode) {
		return (deeperEqualNode.getASTNode() instanceof Stmt);
	}

	private boolean isChangesInTheSameParentNode(ArrayList<ASTNodeArtifact> leftBranch, ArrayList<ASTNodeArtifact> rightBranch){
		if(leftBranch.size() != rightBranch.size()){
			return false;
		} else {
			for(int i = 0; i < leftBranch.size(); i++){
				ASTNodeArtifact left  = leftBranch.get(i);
				ASTNodeArtifact right = rightBranch.get(i);
				if(left.hasMatching(right)){
					//if(left.toString().equals(right.toString())){
					continue;
				} else {
					return false;
				}
			}
			return true;
		}
	}

	private ArrayList<ASTNodeArtifact> findDeeperEqualNode(ArrayList<ASTNodeArtifact> leftBranch, ArrayList<ASTNodeArtifact> rightBranch) {
		ASTNodeArtifact deeperEqualNode 	= null; 
		ASTNodeArtifact lastEqualStmt 		= null; 
		ASTNodeArtifact lastEqualStmtLeft 	= null; 
		ASTNodeArtifact lastEqualStmtRight	= null;

		List<ASTNodeArtifact> AUXlastEqualStmtLeftSubBranch 	= null; 
		List<ASTNodeArtifact> AUXlastEqualStmtRightSubBranch 	= null; 
		int limit = (leftBranch.size() < rightBranch.size()) ? leftBranch.size() : rightBranch.size();
		int index = 0;
		while(index < limit){
			ASTNodeArtifact left  = leftBranch.get(index);
			ASTNodeArtifact right = rightBranch.get(index);
			if(left.hasMatching(right)){
				if(!(left.getASTNode() instanceof org.jastadd.extendj.ast.Opt) && !(left.getASTNode() instanceof org.jastadd.extendj.ast.List)){
					deeperEqualNode = leftBranch.get(index);
					if(isStmt(deeperEqualNode)){
						lastEqualStmt 		= deeperEqualNode;
						lastEqualStmtLeft 	= leftBranch.get(index);
						lastEqualStmtRight 	= rightBranch.get(index);
						try{
							AUXlastEqualStmtLeftSubBranch  = leftBranch.subList(index+1, leftBranch.size()-1);
						} catch(IllegalArgumentException e){
							AUXlastEqualStmtLeftSubBranch = new LinkedList<ASTNodeArtifact>();
						}
						try{
							AUXlastEqualStmtRightSubBranch = rightBranch.subList(index+1, rightBranch.size()-1);
						} catch(IllegalArgumentException e){
							AUXlastEqualStmtLeftSubBranch = new LinkedList<ASTNodeArtifact>();
						}
					}
				}
				index++;
			} else {
				break;
			}
		}

		if((null != AUXlastEqualStmtLeftSubBranch) && (null != AUXlastEqualStmtRightSubBranch)){
			if(thereIsFutherBlock(AUXlastEqualStmtLeftSubBranch, AUXlastEqualStmtRightSubBranch)){
				lastEqualStmt = null;
			}
		}

		ArrayList<ASTNodeArtifact> result = new ArrayList<ASTNodeArtifact>();
		result.add(lastEqualStmt);
		result.add(deeperEqualNode);
		result.add(lastEqualStmtLeft);
		result.add(lastEqualStmtRight);
		return result;

	}

	private boolean thereIsFutherBlock(List<ASTNodeArtifact> subBranchLeft, List<ASTNodeArtifact> subBranchRight) {
		for(ASTNodeArtifact n : subBranchLeft){
			if(n.getASTNode() instanceof org.jastadd.extendj.ast.Block){
				return true;
			}
		}
		for(ASTNodeArtifact n : subBranchRight){
			if(n.getASTNode() instanceof org.jastadd.extendj.ast.Block){
				return true;
			}
		}
		return false;
	}

	private ArrayList<ASTNodeArtifact> printCommonBranch(ASTNodeArtifact deeperEqualNode){
		return rebuildAST(deeperEqualNode);

	}

	private ArrayList<ASTNodeArtifact> rebuildAST(ASTNodeArtifact node){
		ArrayList<ASTNodeArtifact> parents = new ArrayList<ASTNodeArtifact>();
		rebuild(node, parents);
		Collections.reverse(parents);
		parents.add(node);
		return parents;
	}

	private void rebuild(ASTNodeArtifact node, ArrayList<ASTNodeArtifact> parents){
		if(null == node.getParent()){
			return;
		} else {
			parents.add(node.getParent());
			rebuild(node.getParent(),parents);
		}
	}

	private  boolean isContentValid(ASTNodeArtifact left, ASTNodeArtifact base, ASTNodeArtifact right){
		String leftContent  	= ((left.prettyPrint()).replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
		String rightContent 	= ((right.prettyPrint()).replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
		if(null == base) return (!leftContent.equals(rightContent));
		else {	
			String baseContent  = ((base.prettyPrint()).replaceAll("\\r\\n|\\r|\\n","")).replaceAll("\\s+","");
			if(leftContent.equals(baseContent) || rightContent.equals(baseContent)) return false;
			else if(rightContent.equals(leftContent)) return false;
			else return true;
		}
	}
}
