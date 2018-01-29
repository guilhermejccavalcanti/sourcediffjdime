/**
 * Copyright (C) 2013-2014 Olaf Lessenich
 * Copyright (C) 2014-2015 University of Passau, Germany
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 *
 * Contributors:
 *     Olaf Lessenich <lessenic@fim.uni-passau.de>
 *     Georg Seibt <seibt@fim.uni-passau.de>
 */
package de.fosd.jdime.operations;

import java.util.List;
import java.util.logging.Logger;

import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeConflict;
import de.fosd.jdime.artifact.Artifact;
import de.fosd.jdime.artifact.ast.ASTNodeArtifact;
import de.fosd.jdime.config.merge.MergeContext;
import fpfn.Difference;
import fpfn.Difference.Type;
import fpfn.FPFNUtils;

/**
 * @author Olaf Lessenich
 *
 * @param <T>
 *            type of artifact
 */
public class ConflictOperation<T extends Artifact<T>> extends Operation<T> {

    private static final Logger LOG = Logger.getLogger(ConflictOperation.class.getCanonicalName());
    
    private T type;
    private T left;
    private T right;

    /**
     * Output Artifact.
     */
    private T target;

    private String leftCondition;
    private String rightCondition;

    /**
     * Class constructor.
     *
     * @param left left alternatives
     * @param right right alternatives
     * @param target target node
     */
    public ConflictOperation(final T left, final T right, final T target, final String leftCondition,
                             final String rightCondition) {
        super();
        this.left = left;
        this.right = right;
        this.target = target;

        if (leftCondition != null) {
            this.leftCondition = leftCondition;
        }

        if (rightCondition != null) {
            this.rightCondition = rightCondition;
        }
    }

    @Override
    public void apply(MergeContext context) {
        LOG.fine(() -> "Applying: " + this);

        if (target != null) {
            assert (target.exists());

            if (context.isConditionalMerge(left) && leftCondition != null && rightCondition != null) {
                LOG.fine("Create choice node");
                T choice;
                if (left.isChoice()) {
                    choice = left;
                } else {
                    choice = target.createChoiceArtifact(leftCondition, left);
                }

                assert (choice.isChoice());
                choice.addVariant(rightCondition, right);
                target.addChild(choice);
            } else {
                LOG.fine("Create conflict node");
                T conflict = target.createConflictArtifact(left, right);
                assert (conflict.isConflict());
                target.addChild(conflict);

				//FPFN
				if(conflict instanceof ASTNodeArtifact){
					ASTNodeArtifact conf = (ASTNodeArtifact) conflict;
					String confCode		 = conf.prettyPrint();
					if(!confCode.contains("This is a bug in JDime")){
						if(conf.isWithinMethod()){
							ASTNodeArtifact methodDecl = FPFNUtils.getMethodNode(conf);
							if(methodDecl != null && methodDecl.isMethod()){
								String mergedBodyContent   = FPFNUtils.getMethodBody(methodDecl);
								String signature 		   = FPFNUtils.extractSignature(mergedBodyContent);
								List<MergeConflict> mergeConflicts 	= FilesManager.extractMergeConflicts(confCode);
								for(Difference jfstmergeDiff: context.differences){ //filling differences with jdime's info
									if(FPFNUtils.areSignatureEqual(signature, jfstmergeDiff.signature))
										jfstmergeDiff.jdimeBody = mergedBodyContent;
								}
								for(MergeConflict mc: mergeConflicts){
									Difference diff = FPFNUtils.getOrCreateDifference(context.differences,signature,mc);
									diff.types.add(Type.SAME_POSITION);
									diff.jdimeConf = mc;
									diff.jdimeBody = mergedBodyContent;
									diff.signature = signature;
									//context.differences.add(diff);
								}
							}
						}
					}
				}
            }
        }
    }

    @Override
    public final String getName() {
        return "CONFLICT";
    }

    @Override
    public final String toString() {
        return getId() + ": " + getName() + " {" + left + "} <~~> {" + right
                + "}";
    }
}
