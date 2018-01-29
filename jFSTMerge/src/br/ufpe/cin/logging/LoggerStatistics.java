package br.ufpe.cin.logging;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.crypto.CryptoUtils;
import br.ufpe.cin.exceptions.CryptoException;
import br.ufpe.cin.exceptions.ExceptionUtils;
import br.ufpe.cin.exceptions.PrintException;
import br.ufpe.cin.mergers.util.MergeConflict;
import br.ufpe.cin.mergers.util.MergeContext;
import br.ufpe.cin.mergers.util.Source;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class LoggerStatistics
{
  public static int numberOfCriptographyFixAttempts = 0;
  
  static
  {
    if (!JFSTMerge.isCryptographed) {
      try
      {
        String logpath = System.getProperty("user.home") + File.separator + ".jfstmerge" + File.separator;
        
        logpath = logpath + "jfstmerge.statistics";
        File file = new File(logpath);
        CryptoUtils.decrypt(file, file);
        
        logpath = logpath + "jfstmerge.files";
        file = new File(logpath);
        CryptoUtils.decrypt(file, file);
      }
      catch (CryptoException localCryptoException) {}
    }
  }
  
  public static void logContext(String msg, MergeContext context)
    throws PrintException
  {
    try
    {
      initializeLogger();
      
      String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
      String logpath = System.getProperty("user.home") + File.separator + ".jfstmerge" + File.separator;
      String logentry = timeStamp + "," + msg + "\n";
      logpath = logpath + "jfstmerge.statistics";
      File statisticsLog = new File(logpath);
      if (JFSTMerge.isCryptographed) {
        CryptoUtils.decrypt(statisticsLog, statisticsLog);
      }
      FileUtils.write(statisticsLog, logentry, true);
      if (JFSTMerge.logFiles) {
        logFiles(timeStamp, context);
      }
      logSummary();
    }
    catch (CryptoException c)
    {
      String logpath = System.getProperty("user.home") + File.separator + ".jfstmerge" + File.separator;
      logpath = logpath + "jfstmerge.statistics";
      File log = new File(logpath);
      if (log.exists())
      {
        File log_defect = new File(logpath + "_defect" + System.currentTimeMillis());
        log.renameTo(log_defect);
      }
      if (numberOfCriptographyFixAttempts < 1)
      {
        numberOfCriptographyFixAttempts += 1;
        logContext(msg, context);
      }
    }
    catch (Exception e)
    {
      throw new PrintException(ExceptionUtils.getCauseMessage(e));
    }
  }
  
  public static void logScenario(String loggermsg)
    throws IOException
  {
    String logpath = System.getProperty("user.home") + File.separator + ".jfstmerge" + File.separator;
    new File(logpath).mkdirs();
    logpath = logpath + "jfstmerge.statistics.scenarios";
    
    String header = "revision,ssmergeconfs,ssmergeloc,ssmergerenamingconfs,ssmergedeletionconfs,ssmergetaeconfs,ssmergenereoconfs,ssmergeinitlblocksconfs,ssmergeacidentalconfs,unmergeconfs,unmergeloc,unmergetime,ssmergetime,unmergeduplicateddeclarationerrors,unmergeorderingconfs,equalconfs\n";
    
    File statisticsLog = new File(logpath);
    if (!statisticsLog.exists()) {
      FileUtils.write(statisticsLog, header, true);
    }
    FileUtils.write(statisticsLog, loggermsg, true);
  }
  
  public static void logConflicts(List<MergeConflict> conflicts, Source source)
    throws IOException
  {
    String logpath = System.getProperty("user.home") + File.separator + ".jfstmerge" + File.separator;
    new File(logpath).mkdirs();
    for (MergeConflict mc : conflicts)
    {
      String origin = (mc.leftOriginFile != null ? mc.leftOriginFile.getAbsolutePath() : "<empty left>") + 
        ";" + (mc.baseOriginFile != null ? mc.baseOriginFile.getAbsolutePath() : "<empty base>") + 
        ";" + (mc.rightOriginFile != null ? mc.rightOriginFile.getAbsolutePath() : "<empty right>");
      if (source == null)
      {
        File f = new File(logpath + "conflicts.equals");
        FileUtils.write(f, origin + '\n' + mc.body + '\n', true);
        break;
      }
      File f;
      switch (source)
      {
      case SEMISTRUCTURED: 
        f = new File(logpath + "conflicts.unstructured");
        FileUtils.write(f, origin + '\n' + mc.body + '\n', true);
        break;
      case UNSTRUCTURED: 
        f = new File(logpath + "conflicts.semistructured");
        FileUtils.write(f, origin + '\n' + mc.body + '\n', true);
      }
    }
  }
  
  private static void logSummary()
    throws IOException
  {
    try
    {
      String logpath = System.getProperty("user.home") + File.separator + ".jfstmerge" + File.separator;
      new File(logpath).mkdirs();
      File statistics = new File(logpath + "jfstmerge.statistics");
      if (statistics.exists())
      {
        int ssmergeconfs = 0;
        int ssmergeloc = 0;
        int ssmergerenamingconfs = 0;
        int ssmergedeletionconfs = 0;
        int ssmergetaeconfs = 0;
        int ssmergenereoconfs = 0;
        int ssmergeinitlblocksconfs = 0;
        int ssmergeacidentalconfs = 0;
        int unmergeconfs = 0;
        int unmergeloc = 0;
        long unmergetime = 0L;
        long ssmergetime = 0L;
        int duplicateddeclarationerrors = 0;
        int unmergeorderingconfs = 0;
        int equalconfs = 0;
        
        List<String> lines = Files.readAllLines(statistics.toPath());
        for (int i = 1; i < lines.size(); i++)
        {
          String[] columns = ((String)lines.get(i)).split(",");
          
          ssmergeconfs += Integer.valueOf(columns[2]).intValue();
          ssmergeloc += Integer.valueOf(columns[3]).intValue();
          ssmergerenamingconfs += Integer.valueOf(columns[4]).intValue();
          ssmergedeletionconfs += Integer.valueOf(columns[5]).intValue();
          ssmergetaeconfs += Integer.valueOf(columns[6]).intValue();
          ssmergenereoconfs += Integer.valueOf(columns[7]).intValue();
          ssmergeinitlblocksconfs += Integer.valueOf(columns[8]).intValue();
          ssmergeacidentalconfs += Integer.valueOf(columns[9]).intValue();
          unmergeconfs += Integer.valueOf(columns[10]).intValue();
          unmergeloc += Integer.valueOf(columns[11]).intValue();
          unmergetime += Long.parseLong(columns[12]);
          ssmergetime += Long.parseLong(columns[13]);
          duplicateddeclarationerrors += Integer.valueOf(columns[14]).intValue();
          unmergeorderingconfs += Integer.valueOf(columns[15]).intValue();
          equalconfs += Integer.valueOf(columns[16]).intValue();
        }
        if (JFSTMerge.isCryptographed) {
          CryptoUtils.encrypt(statistics, statistics);
        }
        int JAVA_FILES = lines.size() - 1;
        
        int FP_UN = unmergeorderingconfs;
        int FN_UN = duplicateddeclarationerrors;
        int FP_SS = ssmergerenamingconfs;
        int FN_SS = ssmergetaeconfs + ssmergenereoconfs + ssmergeinitlblocksconfs + ssmergeacidentalconfs;
        double M = ssmergetime / 1.0E9D;
        double N = unmergetime / 1.0E9D;
        
        StringBuilder summary = fillSummaryMsg(ssmergeconfs, ssmergeloc, 
          unmergeconfs, unmergeloc, equalconfs, JAVA_FILES, FP_UN, 
          FN_UN, FP_SS, FN_SS, M, N);
        
        File fsummary = new File(logpath + "jfstmerge.summary");
        if (!fsummary.exists()) {
          fsummary.createNewFile();
        }
        FileUtils.write(fsummary, summary.toString(), false);
      }
    }
    catch (CryptoException c)
    {
      String logpath = System.getProperty("user.home") + File.separator + ".jfstmerge" + File.separator;
      logpath = logpath + "jfstmerge.statistics";
      File log = new File(logpath);
      if (log.exists())
      {
        File log_defect = new File(logpath + "_defect" + System.currentTimeMillis());
        log.renameTo(log_defect);
      }
      if (numberOfCriptographyFixAttempts < 1)
      {
        numberOfCriptographyFixAttempts += 1;
        logSummary();
      }
    }
  }
  
  private static void logFiles(String timeStamp, MergeContext context)
    throws IOException
  {
    try
    {
      String logpath = System.getProperty("user.home") + File.separator + ".jfstmerge" + File.separator;
      new File(logpath).mkdirs();
      logpath = logpath + "jfstmerge.files";
      manageLogBuffer(logpath);
      File logfiles = new File(logpath);
      if (!logfiles.exists())
      {
        logfiles.createNewFile();
        if (JFSTMerge.isCryptographed) {
          CryptoUtils.encrypt(logfiles, logfiles);
        }
      }
      if (JFSTMerge.isCryptographed) {
        CryptoUtils.decrypt(logfiles, logfiles);
      }
      String leftcontent = context.getLeftContent();
      if (!leftcontent.isEmpty())
      {
        FileUtils.write(logfiles, timeStamp + "," + context.getLeft().getAbsolutePath() + "\n", true);
        FileUtils.write(logfiles, leftcontent + "\n", true);
        FileUtils.write(logfiles, "!@#$%\n", true);
      }
      String basecontent = context.getBaseContent();
      if (!basecontent.isEmpty())
      {
        FileUtils.write(logfiles, timeStamp + "," + context.getBase().getAbsolutePath() + "\n", true);
        FileUtils.write(logfiles, basecontent + "\n", true);
        FileUtils.write(logfiles, "!@#$%\n", true);
      }
      String rightcontent = context.getRightContent();
      if (!rightcontent.isEmpty())
      {
        FileUtils.write(logfiles, timeStamp + "," + context.getRight().getAbsolutePath() + "\n", true);
        FileUtils.write(logfiles, rightcontent + "\n", true);
        FileUtils.write(logfiles, "!@#$%\n", true);
      }
      if (JFSTMerge.isCryptographed) {
        CryptoUtils.encrypt(logfiles, logfiles);
      }
    }
    catch (CryptoException c)
    {
      String logpath = System.getProperty("user.home") + File.separator + ".jfstmerge" + File.separator;
      logpath = logpath + "jfstmerge.files";
      File log = new File(logpath);
      if (log.exists())
      {
        File log_defect = new File(logpath + "_defect" + System.currentTimeMillis());
        log.renameTo(log_defect);
      }
      if (numberOfCriptographyFixAttempts < 1)
      {
        numberOfCriptographyFixAttempts += 1;
        logFiles(timeStamp, context);
      }
    }
  }
  
  private static void initializeLogger()
    throws IOException, CryptoException
  {
    String logpath = System.getProperty("user.home") + File.separator + ".jfstmerge" + File.separator;
    new File(logpath).mkdirs();
    logpath = logpath + "jfstmerge.statistics";
    
    String header = "date,files,ssmergeconfs,ssmergeloc,ssmergerenamingconfs,ssmergedeletionconfs,ssmergetaeconfs,ssmergenereoconfs,ssmergeinitlblocksconfs,ssmergeacidentalconfs,unmergeconfs,unmergeloc,unmergetime,ssmergetime,unmergeduplicateddeclarationerrors,unmergeorderingconfs,equalconfs\n";
    if (!new File(logpath).exists())
    {
      File statisticsLog = new File(logpath);
      FileUtils.write(statisticsLog, header, true);
      if (JFSTMerge.isCryptographed) {
        CryptoUtils.encrypt(statisticsLog, statisticsLog);
      }
    }
  }
  
  private static void manageLogBuffer(String logpath)
    throws CryptoException
  {
    File log = new File(logpath);
    if (log.exists())
    {
      long logSizeMB = log.length() / 1048576L;
      if (logSizeMB >= 4L)
      {
        File newLog = new File(logpath + System.currentTimeMillis());
        log.renameTo(newLog);
      }
    }
  }
  
  private static StringBuilder fillSummaryMsg(int ssmergeconfs, int ssmergeloc, int unmergeconfs, int unmergeloc, int equalconfs, int JAVA_FILES, int FP_UN, int FN_UN, int FP_SS, int FN_SS, double M, double N)
  {
    StringBuilder summary = new StringBuilder();
    summary.append("s3m was invoked in " + JAVA_FILES + " JAVA files so far.\n");
    if ((FP_UN > 0) && (FN_UN > 0))
    {
      summary.append("In these files, you avoided at least " + FP_UN + " false positive(s),");
      summary.append(" and at least " + FN_UN + " false negative(s) in relation to unstructured merge.\n");
    }
    else if ((FP_UN == 0) && (FN_UN == 0))
    {
      summary.append("In these files, s3m did not find any occurrence of unstructured merge false positives and false negatives.\n");
    }
    else if ((FP_UN > 0) && (FN_UN == 0))
    {
      summary.append("In these files, you avoided at least " + FP_UN + " false positive(s), and s3m did not find any occurrence of unstructured merge false negatives.\n");
    }
    else if ((FP_UN == 0) && (FN_UN > 0))
    {
      summary.append("In these files, s3m did not find any occurrence of unstructured merge false positives, but you avoided at least " + FN_UN + " false negative(s) in relation to unstructured merge.\n");
    }
    summary.append("Conversely,");
    if (FN_SS == 0) {
      summary.append(" you had no extra false positives, nor potential extra false negatives.");
    } else if (FN_SS > 0) {
      summary.append(" you had no extra false positives, but you had at most " + FN_SS + " potential extra false negative(s).");
    }
    summary.append("\n\ns3m reported " + ssmergeconfs + " conflicts, totaling " + ssmergeloc + " conflicting LOC,");
    summary.append(" compared to " + unmergeconfs + " conflicts and " + unmergeloc + " conflicting LOC from unstructured merge.");
    
    summary.append("\n\nAltogether, ");
    if (ssmergeconfs != unmergeconfs)
    {
      if (ssmergeconfs < unmergeconfs) {
        summary.append("these numbers represent a reduction of " + String.format("%.2f", new Object[] { Double.valueOf((unmergeconfs - ssmergeconfs) / unmergeconfs * 100.0D) }) + "% in the number of conflicts by s3m.\n");
      } else if (ssmergeconfs > unmergeconfs) {
        summary.append("these numbers represent no reduction of conflicts by s3m.\n");
      }
    }
    else {
      summary.append("these numbers represent no difference in terms of number of reported conflicts.\n");
    }
    if (FP_UN > 0) {
      summary.append("A reduction of " + String.format("%.2f", new Object[] { Double.valueOf((FP_UN - 0) / FP_UN * 100.0D), Integer.valueOf(2) }) + "% in the number of false positives.\n");
    } else {
      summary.append("No difference in terms of false positives.\n");
    }
    if (FN_UN != FN_SS)
    {
      if (FN_UN > FN_SS) {
        summary.append("And a reduction of " + String.format("%.2f", new Object[] { Double.valueOf((FN_UN - FN_SS) / FN_UN * 100.0D), Integer.valueOf(2) }) + "% in the number of false negatives.");
      } else if (FN_SS > FN_UN) {
        summary.append("And no reduction of false negatives.");
      }
    }
    else {
      summary.append("And no difference in terms of false negatives.");
    }
    summary.append("\n\nFinally, s3m took " + new DecimalFormat("#.##").format(M) + " seconds, and unstructured merge " + new DecimalFormat("#.##").format(N) + " seconds to merge all these files.");
    
    summary.append("\n\n\n");
    summary.append("LAST TIME UPDATED: " + new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(Calendar.getInstance().getTime()));
    return summary;
  }
  
  public static void log(String msg)
    throws PrintException
  {
    try
    {
      String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(Calendar.getInstance().getTime());
      String logpath = System.getProperty("user.home") + File.separator + ".jfstmerge" + File.separator;
      new File(logpath).mkdirs();
      logpath = logpath + "jfstmerge.sucessfull";
      PrintWriter pw = new PrintWriter(new FileOutputStream(new File(logpath), true), true);
      pw.append(timeStamp + "," + msg + "\n");
      pw.close();
    }
    catch (Exception e)
    {
      throw new PrintException(ExceptionUtils.getCauseMessage(e));
    }
  }
}
