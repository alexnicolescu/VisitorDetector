package classes;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;
import visitordetector.metamodel.entity.MClass;
import visitordetector.metamodel.entity.MMethod;

@RelationBuilder
public class MethodsWithDescendantsCasts implements IRelationBuilder<MMethod, MClass> {

	@Override
	public Group<MMethod> buildGroup(MClass arg0) {
		Group<MMethod> methods = new Group<>();
		List<MClass> descendants = arg0.subGroup().getElements();
		List<MMethod> methodsWithoutDuplicates = new LinkedList<>();
		for (MClass descendent : descendants) {
			try {
				this.removeDuplicates(descendent.methodsWithCasts().getElements(), methodsWithoutDuplicates);
			} catch (JavaModelException e) {
				System.err.println("MMethod - MClass -> MethodsWithDescendantsCasts:" + e.getMessage());
			}
		}
		methods.addAll(methodsWithoutDuplicates);
		return methods;
	}

	private List<MMethod> removeDuplicates(List<MMethod> methodsWithDuplicates, List<MMethod> methodsWithoutDuplicates)
			throws JavaModelException {
		boolean found = false;
		for (MMethod methodOne : methodsWithDuplicates) {
			for (MMethod methodTwo : methodsWithoutDuplicates) {
				if (compareMethods(methodOne, methodTwo)) {
					found = true;
					break;
				}
			}
			if (!found)
				methodsWithoutDuplicates.add(methodOne);
		}
		return methodsWithoutDuplicates;
	}

	private boolean compareMethods(MMethod mOne, MMethod mTwo) throws JavaModelException {
		if (!mOne.parentClassName().equals(mTwo.parentClassName()))
			return false;
		IMethod methodOne = mOne.getUnderlyingObject();
		IMethod methodTwo = mTwo.getUnderlyingObject();
		if (!methodOne.getElementName().equals(methodTwo.getElementName()))
			return false;
		if (!methodOne.getSignature().equals(methodTwo.getSignature()))
			return false;
		return true;
	}

}
