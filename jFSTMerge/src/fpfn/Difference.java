package fpfn;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.mergers.util.MergeConflict;

/**
 * Class representing differences in behavior of jfstmerge and jdime.
 * Used for empirical purposes.
 * Namelly jfstmerge is (1) sensitive to editions in consecutive lines of code, jdime not (peharps)
 * (2) jdime is sensitive to editions at the same position of parent nodes, jdime not (peharps)
 * (3) jfstmerge is sensitive to editions to different parts of the same statement if in the same text area, jdime not
 * @author Guilherme
 *
 */
public class Difference {
	/**
	 * Differences are computed as follow. 
	 * We first run jfstmerge. For each method/constructor witho no conflict we create an EMPTY difference.
	 * For each conflict inside this method/cons. we create a difference classifying into a known Type. 
	 * As a result, there is a list of differences of a given class and its methods.
	 * We, then, run jdime passing the former list as input. As before, for each conflict inside this method/cons. we create a difference classifying into a known Type, but
	 * checking against the given list of jfstmerge. Therefore, we first attempt to fulfill jfstmerge's differences with jdime information. When there is no such matching, we
	 * create a new difference, this time, only with jdime information. 
	 * 
	 * 
	 * Example. Suppose that we identify a consecutive line conflict with jfstmerge. So, we create a difference with Type.CONSECUTIVE_LINES,  jfstmergeConf as the identified conflict, and jfstmergeBody 
	 * as the body of the method or constructor. When we run jdfime, there are two options: first, jdime reports a similar conflict, so we fullfil the previous difference jdimeConf with jdimes's conflict. Second, jdime does not report the conflict, so 
	 * jdimeConf remains null. In both cases, we set jdimeBody as the body of jdime's method or constructor.
	 */
	
	public String signature 			= null; //method or constructor for identification purposes, to which the difference belongs
	public List<Type> types 			= new ArrayList(); //types a difference might be
	public MergeConflict jfstmergeConf 	= null; //representation of the conflict by jfstmerge
	public MergeConflict jdimeConf 	   	= null; //representation of the conflict by jdime
	public String jfstmergeBody 		= null;
	public String jdimeBody 			= null;
	
	public enum Type{
		CONSECUTIVE_LINES,  //jfstmerge
		SAME_POSITION,		//jdime
		SAME_STATEMENT,     //jdime
		OTHER,				//jfstmerge
		EMPTY, 
		SPACING				//jfstmerge
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("TYPE\n");
		String types = "";
		for(Type t : this.types){types = types+" "+t+" ";}
		builder.append(types+"\n");
		builder.append("----------------------------\n");
		
		if(jfstmergeConf != null){builder.append("JFSTMERGE_CONF\n" + ((jfstmergeConf.bodyInclBase!=null)?jfstmergeConf.bodyInclBase:jfstmergeConf.body));
		} else {builder.append("JFSTMERGE_CONF\n<EMPTY>");}
		builder.append("\n----------------------------\n");

		if(jdimeConf != null){builder.append("JDIME_CONF\n" + jdimeConf.body);
		}else {	builder.append("JDIME_CONF\n<EMPTY>");
		}builder.append("\n----------------------------\n");

		if(jfstmergeBody != null){builder.append("JFSTMERGE_DECL\n" +jfstmergeBody);
		}else{builder.append("JFSTMERGE_DECL\n<EMPTY");
		}builder.append("\n----------------------------\n");

		if(jdimeBody != null){builder.append("JDIME_DECL\n" +jdimeBody);
		}else{builder.append("JDIME_DECL\n<EMPTY>");
		}builder.append("\n----------------------------\n");

		builder.append("\n############################\n");
		return builder.toString();
	}
	
	public String getTypeIntoString(){
		List<String> types = new ArrayList<String>();
		for(Type t : this.types) types.add(t.toString());
		if(types.size() > 2) types = types.subList(0, 2);
		String result = String.join("_", types);
		return result;
	}
	
	public static void main(String[] args) {
		Difference d = new Difference();
		d.types.add(Type.CONSECUTIVE_LINES);
		d.types.add(Type.SAME_POSITION);
		System.out.println(d.getTypeIntoString());
	}
}
