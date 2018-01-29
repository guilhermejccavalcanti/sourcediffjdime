package br.ufpe.cin.statistics;

import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.files.FilesTuple;
import br.ufpe.cin.logging.LoggerStatistics;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.MergeScenario;
import br.ufpe.cin.mergers.util.Source;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Statistics
{
  public static void compute(MergeContext context)
    throws Exception
  {
    List<MergeConflict> semistructuredMergeConflicts = FilesManager.extractMergeConflicts(context.semistructuredOutput);
    List<MergeConflict> unstructuredMergeConflits = FilesManager.extractMergeConflicts(context.unstructuredOutput);
    
    context.semistructuredNumberOfConflicts = computeNumberOfConflicts(semistructuredMergeConflicts);
    context.unstructuredNumberOfConflicts = computeNumberOfConflicts(unstructuredMergeConflits);
    
    context.semistructuredMergeConflictsLOC = computeConflictsLOC(semistructuredMergeConflicts);
    context.unstructuredMergeConflictsLOC = computeConflictsLOC(unstructuredMergeConflits);
    
    context.orderingConflicts = (context.unstructuredNumberOfConflicts - context.semistructuredNumberOfConflicts + context.duplicatedDeclarationErrors - (context.typeAmbiguityErrorsConflicts + context.newElementReferencingEditedOneConflicts + context.initializationBlocksConflicts));
    context.orderingConflicts = (context.orderingConflicts > 0 ? context.orderingConflicts : 0);
    
    context.acidentalConflicts = (context.unstructuredNumberOfConflicts - context.equalConflicts - context.orderingConflicts);
    context.acidentalConflicts = (context.acidentalConflicts > 0 ? context.acidentalConflicts : 0);
    
    String filesMerged = (context.getLeft() != null ? context.getLeft().getAbsolutePath() : "<empty left>") + "#" + (
      context.getBase() != null ? context.getBase().getAbsolutePath() : "<empty base>") + "#" + (
      context.getRight() != null ? context.getRight().getAbsolutePath() : "<empty right>");
    String loggermsg = filesMerged + 
      "," + context.semistructuredNumberOfConflicts + 
      "," + context.semistructuredMergeConflictsLOC + 
      "," + context.renamingConflicts + 
      "," + context.deletionConflicts + 
      "," + context.typeAmbiguityErrorsConflicts + 
      "," + context.newElementReferencingEditedOneConflicts + 
      "," + context.initializationBlocksConflicts + 
      "," + context.acidentalConflicts + 
      "," + context.unstructuredNumberOfConflicts + 
      "," + context.unstructuredMergeConflictsLOC + 
      "," + context.unstructuredMergeTime + 
      "," + context.semistructuredMergeTime + 
      "," + context.duplicatedDeclarationErrors + 
      "," + context.orderingConflicts + 
      "," + context.equalConflicts;
    
    LoggerStatistics.logContext(loggermsg, context);
  }
  
  public static void compute(MergeScenario scenario)
    throws Exception
  {
    int semistructuredNumberOfConflicts = 0;
    int semistructuredMergeConflictsLOC = 0;
    int renamingConflicts = 0;
    int deletionConflicts = 0;
    int typeAmbiguityErrorsConflicts = 0;
    int newElementReferencingEditedOneConflicts = 0;
    int initializationBlocksConflicts = 0;
    int acidentalConflicts = 0;
    int unstructuredNumberOfConflicts = 0;
    int unstructuredMergeConflictsLOC = 0;
    long unstructuredMergeTime = 0L;
    long semistructuredMergeTime = 0L;
    int duplicatedDeclarationErrors = 0;
    int orderingConflicts = 0;
    int equalConflicts = 0;
    for (FilesTuple tuple : scenario.getTuples())
    {
      MergeContext context = tuple.getContext();
      if (context != null)
      {
        semistructuredNumberOfConflicts += context.semistructuredNumberOfConflicts;
        semistructuredMergeConflictsLOC += context.semistructuredMergeConflictsLOC;
        renamingConflicts += context.renamingConflicts;
        deletionConflicts += context.deletionConflicts;
        typeAmbiguityErrorsConflicts += context.typeAmbiguityErrorsConflicts;
        newElementReferencingEditedOneConflicts += context.newElementReferencingEditedOneConflicts;
        initializationBlocksConflicts += context.initializationBlocksConflicts;
        acidentalConflicts += context.acidentalConflicts;
        unstructuredNumberOfConflicts += context.unstructuredNumberOfConflicts;
        unstructuredMergeConflictsLOC += context.unstructuredMergeConflictsLOC;
        unstructuredMergeTime += context.unstructuredMergeTime;
        semistructuredMergeTime += context.semistructuredMergeTime;
        duplicatedDeclarationErrors += context.duplicatedDeclarationErrors;
        orderingConflicts += context.orderingConflicts;
        equalConflicts += context.equalConflicts;
      }
    }
    String loggermsg = scenario.getRevisionsFilePath() + 
      "," + semistructuredNumberOfConflicts + 
      "," + semistructuredMergeConflictsLOC + 
      "," + renamingConflicts + 
      "," + deletionConflicts + 
      "," + typeAmbiguityErrorsConflicts + 
      "," + newElementReferencingEditedOneConflicts + 
      "," + initializationBlocksConflicts + 
      "," + acidentalConflicts + 
      "," + unstructuredNumberOfConflicts + 
      "," + unstructuredMergeConflictsLOC + 
      "," + unstructuredMergeTime + 
      "," + semistructuredMergeTime + 
      "," + duplicatedDeclarationErrors + 
      "," + orderingConflicts + 
      "," + equalConflicts + '\n';
    
    LoggerStatistics.logScenario(loggermsg);
  }
  
  private static int computeNumberOfConflicts(List<MergeConflict> listofconflicts)
  {
    int numberOfConflicts = listofconflicts.size();
    return numberOfConflicts;
  }
  
  private static int computeConflictsLOC(List<MergeConflict> listofconflicts)
  {
    int conflictsloc = 0;
    for (MergeConflict mc : listofconflicts) {
      conflictsloc += ((List)new BufferedReader(new StringReader(mc.left)).lines().collect(Collectors.toList())).size() + ((List)new BufferedReader(new StringReader(mc.right)).lines().collect(Collectors.toList())).size();
    }
    return conflictsloc;
  }
  
  private static int computeEqualConflicts(List<MergeConflict> unstructuredMergeConflits, List<MergeConflict> semistructuredMergeConflicts)
  {
    int equalconfs = 0;
    for (MergeConflict mctxt : unstructuredMergeConflits) {
      for (MergeConflict mcssm : semistructuredMergeConflicts) {
        if (areEquivalentConflicts(mctxt, mcssm))
        {
          equalconfs++; break;
        }
      }
    }
    return equalconfs;
  }
  
  private static void computeDifferentConflicts(MergeContext context)
    throws IOException
  {
    Deque<MergeConflict> semistructuredMergeConflicts = new ArrayDeque();
    semistructuredMergeConflicts.addAll(FilesManager.extractMergeConflicts(context.semistructuredOutput));
    
    Deque<MergeConflict> unstructuredMergeConflits = new ArrayDeque();
    unstructuredMergeConflits.addAll(FilesManager.extractMergeConflicts(context.unstructuredOutput));
    
    List<MergeConflict> differentUnstructuredMergeConflicts = new ArrayList();
    List<MergeConflict> differentSemistructuredMergeConflicts = new ArrayList();
    List<MergeConflict> equalMergeConflicts = new ArrayList();
    for (MergeConflict confa : unstructuredMergeConflits)
    {
      confa.setOriginFiles(context.getLeft(), context.getBase(), context.getRight());
      boolean found = false;
      for (MergeConflict confb : semistructuredMergeConflicts) {
        if (areEquivalentConflicts(confa, confb))
        {
          equalMergeConflicts.add(confb);
          found = true;
          break;
        }
      }
      if (!found) {
        differentUnstructuredMergeConflicts.add(confa);
      }
    }
    for (MergeConflict confa : semistructuredMergeConflicts)
    {
      confa.setOriginFiles(context.getLeft(), context.getBase(), context.getRight());
      boolean found = false;
      for (MergeConflict confb : unstructuredMergeConflits) {
        if (areEquivalentConflicts(confa, confb))
        {
          found = true;
          break;
        }
      }
      if (!found) {
        differentSemistructuredMergeConflicts.add(confa);
      }
    }
    LoggerStatistics.logConflicts(equalMergeConflicts, null);
    LoggerStatistics.logConflicts(differentUnstructuredMergeConflicts, Source.UNSTRUCTURED);
    LoggerStatistics.logConflicts(differentSemistructuredMergeConflicts, Source.SEMISTRUCTURED);
  }
  
  private static boolean areEquivalentConflicts(MergeConflict confa, MergeConflict confb)
  {
    String bodya = FilesManager.getStringContentIntoSingleLineNoSpacing(confa.left + confa.right);
    String bodyb = FilesManager.getStringContentIntoSingleLineNoSpacing(confb.left + confb.right);
    if (bodya.equals(bodyb)) {
      return true;
    }
    return false;
  }
  
  public static void computeSucessfullMerges(File left, File base, File right, boolean successssmerge, boolean successjdime, boolean successtextual)
    throws Exception
  {
    String filesMerged = (left != null ? left.getAbsolutePath() : "<empty left>") + "#" + (
      base != null ? base.getAbsolutePath() : "<empty base>") + "#" + (
      right != null ? right.getAbsolutePath() : "<empty right>");
    String loggermsg = filesMerged + 
      "," + (!successtextual ? 0 : 1) + 
      "," + (!successssmerge ? 0 : 1) + 
      "," + (!successjdime ? 0 : 1);
    LoggerStatistics.log(loggermsg);
  }
}
