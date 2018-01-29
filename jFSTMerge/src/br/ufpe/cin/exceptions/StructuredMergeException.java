package br.ufpe.cin.exceptions;

import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;

/**
 * In case of errors when performing structured merge (jdime). 
 * @author Guilherme
 */
public class StructuredMergeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6981593887690690881L;
	
	public StructuredMergeException(){
		super("Unexpected error while performing structured merge.");
	}
	
	public StructuredMergeException(String message){
		super("Unexpected error while performing structured merge:/n"
				+ message);
	}
	
	public StructuredMergeException(String incomingMessage, MergeContext context){
		super(detailErrorMessage(incomingMessage,context));
	}

	private static String detailErrorMessage(String incomingMessage,MergeContext context) {
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("Unexpected error while performing structured merge: ");
		messageBuilder.append(incomingMessage);
		messageBuilder.append("\nMERGING FILES: ");
		messageBuilder.append(((context.getLeft() != null)?context.getLeft().getAbsolutePath() :"<empty left>") + ";");
		messageBuilder.append(((context.getBase() != null)?context.getBase().getAbsolutePath() :"<empty base>") + ";");
		messageBuilder.append(((context.getRight()!= null)?context.getRight().getAbsolutePath():"<empty right>"));
		messageBuilder.append("\nLEFT FILE CONTENT:\n" + ((context.getLeft() != null)?FilesManager.readFileContent(context.getLeft()):"<empty left>"));
		messageBuilder.append("\nBASE FILE CONTENT:\n" + ((context.getBase() != null)?FilesManager.readFileContent(context.getBase()):"<empty base>"));
		messageBuilder.append("\nRIGHT FILE CONTENT:\n"+ ((context.getRight()!= null)?FilesManager.readFileContent(context.getRight()):"<empty right>"));
		return messageBuilder.toString();
	}
}
