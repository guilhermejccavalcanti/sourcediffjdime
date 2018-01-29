package fpfn;

import java.util.ArrayList;
import java.util.Collections;

import de.fosd.jdime.artifact.ast.ASTNodeArtifact;


public class ASTBranchesResult {
	// ATTRIBUTES EDITED FOR EACH MERGED FILE
	private ArrayList<ArrayList<ASTNodeArtifact>> branchesFromLeft 		= new ArrayList<ArrayList<ASTNodeArtifact>>();
	private ArrayList<ArrayList<ASTNodeArtifact>> branchesFromRight 	= new ArrayList<ArrayList<ASTNodeArtifact>>();
	public int EDITIONS_TO_DIFFERENT_PARTS_OF_SAME_STMT = 0;
	public ArrayList<String> LOG_EDITIONS_TO_DIFFERENT_PARTS_OF_SAME_STMT = new ArrayList<String>();
	public int CONFS 	= 0;
	public int LOCS 	= 0;
	public int FILES 	= 0;

	public void buildASTResultFromLeft(ASTNodeArtifact node) {
		if(node.getRevision().toString().equals("left")){
			ArrayList<ASTNodeArtifact> leftParents = new ArrayList<ASTNodeArtifact>();
			build(node, leftParents);
			Collections.reverse(leftParents);
			//branchesFromLeft.add(leftParents);
			addIfNotContains(branchesFromLeft, leftParents);
		}
	}

	public void buildASTResultFromRight(ASTNodeArtifact node) {
		if(node.getRevision().toString().equals("right")){
			ArrayList<ASTNodeArtifact> rightParents = new ArrayList<ASTNodeArtifact>();
			build(node, rightParents);
			Collections.reverse(rightParents);
			//branchesFromRight.add(rightParents);
			addIfNotContains(branchesFromRight, rightParents);
		}		
	}

	private void build(ASTNodeArtifact node, ArrayList<ASTNodeArtifact> parents) {
		if (null == node.getParent()) {
			return;
		} else {
			parents.add(node.getParent());
			build(node.getParent(), parents);
		}
	}

	public ArrayList<ArrayList<ASTNodeArtifact>> getBranchesFromLeft() {
		return branchesFromLeft;
	}

	public ArrayList<ArrayList<ASTNodeArtifact>> getBranchesFromRight() {
		return branchesFromRight;
	}

	public void addIfNotContains(ArrayList<ArrayList<ASTNodeArtifact>> listOfBranches, ArrayList<ASTNodeArtifact> newBranch){
		for(ArrayList<ASTNodeArtifact> b : listOfBranches){
			if(b.toString().equals(newBranch.toString())){
				return;
			}
		}
		listOfBranches.add(newBranch);
	}
}
