package br.ufpe.cin.mergers.util;

import java.io.File;

public class MergeConflict
{
  public String left;
  public String base;
  public String right;
  public String body;
  public String bodyInclBase = null;
  public int startLOC;
  public int endLOC;
  public File leftOriginFile;
  public File baseOriginFile;
  public File rightOriginFile;
  
  public MergeConflict(String leftConflictingContent, String rightConflictingContent)
  {
    this.left = leftConflictingContent;
    this.right = rightConflictingContent;
    this.body = 
    
      ("<<<<<<< MINE\n" + leftConflictingContent + "=======\n" + rightConflictingContent + ">>>>>>> YOURS");
  }
  
  public MergeConflict(String leftConflictingContent, String rightConflictingContent, int startLOC, int endLOC)
  {
    this.left = leftConflictingContent;
    this.right = rightConflictingContent;
    if (this.left.contains("|||||||"))
    {
      String[] temp = this.left.split("\\|\\|\\|\\|\\|\\|\\|");
      this.left = temp[0];
      this.base = temp[1].substring(temp[1].indexOf('\n') + 1);
      
      this.bodyInclBase = 
      
        ("<<<<<<< MINE\n" + this.left + "||||||| BASE\n" + this.base + "=======\n" + this.right + ">>>>>>> YOURS");
    }
    this.body = 
    
      ("<<<<<<< MINE\n" + leftConflictingContent + "=======\n" + rightConflictingContent + ">>>>>>> YOURS");
    this.startLOC = startLOC;
    this.endLOC = endLOC;
  }
  
  public MergeConflict(String leftConflictingContent, String rightConflictingContent, String message)
  {
    this.left = leftConflictingContent;
    this.right = rightConflictingContent;
    this.body = 
    
      ("<<<<<<< MINE\n" + leftConflictingContent + "======= " + message + " \n" + rightConflictingContent + ">>>>>>> YOURS");
  }
  
  public boolean contains(String leftPattern, String rightPattern)
  {
    if ((leftPattern.isEmpty()) || (rightPattern.isEmpty())) {
      return false;
    }
    leftPattern = leftPattern.replaceAll("\\r\\n|\\r|\\n", "").replaceAll("\\s+", "");
    rightPattern = rightPattern.replaceAll("\\r\\n|\\r|\\n", "").replaceAll("\\s+", "");
    String lefttrim = this.left.replaceAll("\\r\\n|\\r|\\n", "").replaceAll("\\s+", "");
    String righttrim = this.right.replaceAll("\\r\\n|\\r|\\n", "").replaceAll("\\s+", "");
    return (lefttrim.contains(leftPattern)) && (righttrim.contains(rightPattern));
  }
  
  public void setOriginFiles(File left, File base, File right)
  {
    this.leftOriginFile = left;
    this.rightOriginFile = right;
    this.baseOriginFile = base;
  }
  
  public String toString()
  {
    return this.body;
  }
}
