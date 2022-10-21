package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.datastructures.implementations.HANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import org.checkerframework.checker.units.qual.A;

import javax.swing.text.Style;
import java.util.*;

public class Evaluator implements Transform {

	private IHANLinkedList<HashMap<String, Literal>> variableValues;

	public Evaluator() {
		variableValues = new HANLinkedList<>();
	}

	@Override
	public void apply(AST ast) {
		variableValues.addFirst(new HashMap<>());
		ast.root.getChildren().forEach(this::evaluate);
	}

	private void evaluate(ASTNode node){
		if (node instanceof VariableAssignment){
			insertVariable((VariableAssignment) node);
		}else if (node instanceof Stylerule){
			checkStylerule((Stylerule) node);
		}
	}

	private void insertVariable(VariableAssignment node){
		if(node.expression instanceof Literal) {
			variableValues.getFirst().put(node.name.name, (Literal) node.expression);
		}else{
			variableValues.getFirst().put(node.name.name, getLiteral((VariableReference) node.expression));
		}
	}

	private Literal getLiteral(VariableReference node){
		for(int i = 0; i < variableValues.getSize(); i++) {
			if(variableValues.get(i).get(node.name) != null){
				return variableValues.get(i).get(node.name);
			}
		}
		return null;
	}

	private void checkStylerule(Stylerule node){
		variableValues.addFirst(new HashMap<>());
		node.getChildren().forEach(n -> {
			if(n instanceof Declaration){
				toLiterals((Declaration) n);
			}else if(n instanceof IfClause){
				doIfClause((IfClause) n).forEach(node::addChild);
				node.removeChild(n);
			}
		});
		variableValues.delete(variableValues.getSize()-1);
	}

	private ArrayList<Declaration> doIfClause(IfClause node){
		ArrayList<Declaration> declarations = new ArrayList<>();

		BoolLiteral condition;
		if(node.getConditionalExpression() instanceof VariableReference){
			condition = (BoolLiteral) getLiteral((VariableReference) node.getConditionalExpression());
		}else{
			condition = (BoolLiteral) node.getConditionalExpression();
		}

		if(condition.value){
			node.getChildren().forEach(child -> {
				if(child instanceof Declaration){
					declarations.add((Declaration) child);
				}else if (child instanceof VariableAssignment){
					insertVariable((VariableAssignment) child);
				}else if(child instanceof IfClause){
					declarations.addAll(doIfClause((IfClause) child));
				}
			});
		}else if(node.getElseClause() != null){
			node.getElseClause().getChildren().forEach(child -> {
				if(child instanceof Declaration){
					declarations.add((Declaration) child);
				}else if (child instanceof VariableAssignment){
					insertVariable((VariableAssignment) child);
				}else if(child instanceof IfClause){
					declarations.addAll(doIfClause((IfClause) child));
				}
			});
		}

		for(Declaration declaration : declarations){
			toLiterals(declaration);
		}
		return declarations;
	}

	private void toLiterals(Declaration dec){
		if(dec.expression instanceof VariableReference){
			dec.expression = getLiteral(((VariableReference) dec.expression));
		}else if(dec.expression instanceof Operation){
			dec.expression = doOps((Operation) dec.expression);
		}
	}

	private Literal doOps(Operation node){
		Literal total;
		if (node instanceof MultiplyOperation){
			if(node.rhs instanceof Operation){
				((Operation) node.rhs).lhs = doMul((MultiplyOperation) node);
				total = doOps((Operation) node.rhs);
			}else{
				total = doMul((MultiplyOperation) node);
			}
		}else if (node instanceof AddOperation) {
			total = doPlusMin(node, false);
		}else {
			total = doPlusMin(node, true);
		}
		return total;
	}

	private Literal doMul(MultiplyOperation node){
		Literal lhs = getLitLhs(node);
		Literal rhs;

		if(node.rhs instanceof VariableReference){
			rhs = getLiteral((VariableReference) node.rhs);
		}else if(node.rhs instanceof Operation){
			rhs = (Literal) ((Operation) node.rhs).lhs;
		}else {
			rhs = (Literal) node.rhs;
		}

		return getLitMulValue(lhs, rhs);
	}

	private Literal getLitMulValue(Literal lhs, Literal rhs){
		switch(lhs.getClass().getSimpleName()){
			case "PixelLiteral":
				return new PixelLiteral(((PixelLiteral)lhs).value * ((ScalarLiteral) rhs).value);
			case "ScalarLiteral":
				switch (rhs.getClass().getSimpleName()){
					case "PixelLiteral":
						return new PixelLiteral(((ScalarLiteral)lhs).value * ((PixelLiteral) rhs).value);
					case "ScalarLiteral":
						return new ScalarLiteral(((ScalarLiteral)lhs).value * ((ScalarLiteral)rhs).value);
					case "PercentageLiteral":
						return new PercentageLiteral(((ScalarLiteral)lhs).value * ((PercentageLiteral)rhs).value);
				}
			case "PercentageLiteral":
				return new PercentageLiteral(((PercentageLiteral)lhs).value * ((ScalarLiteral)rhs).value);
			default: return null;
		}
	}

	private Literal doPlusMin(Operation node, Boolean isSub){
		Literal lhs = getLitLhs(node);
		Literal rhs = getLitRhsPlusMin(node);

		if(isSub){
			return getLitMinValue(lhs, rhs);
		}else {
			return getLitPlusValue(lhs, rhs);
		}
	}

	private Literal getLitPlusValue(Literal lhs, Literal rhs){
		switch (lhs.getClass().getSimpleName()){
			case "PixelLiteral":
				return new PixelLiteral(((PixelLiteral)lhs).value + ((PixelLiteral)rhs).value);
			case "ScalarLiteral":
				return new ScalarLiteral(((ScalarLiteral)lhs).value + ((ScalarLiteral)rhs).value);
			case "percentageLiteral":
				return new PercentageLiteral(((PercentageLiteral)lhs).value + ((PercentageLiteral)rhs).value);
			default:
				return null;
		}
	}

	private Literal getLitMinValue(Literal lhs, Literal rhs){
		switch (lhs.getClass().getSimpleName()){
			case "PixelLiteral":
				return new PixelLiteral(((PixelLiteral)lhs).value - ((PixelLiteral)rhs).value);
			case "ScalarLiteral":
				return new ScalarLiteral(((ScalarLiteral)lhs).value - ((ScalarLiteral)rhs).value);
			case "percentageLiteral":
				return new PercentageLiteral(((PercentageLiteral)lhs).value - ((PercentageLiteral)rhs).value);
			default:
				return null;
		}
	}

	private Literal getLitLhs(Operation node){
		if(node.lhs instanceof VariableReference){
			return getLiteral((VariableReference) node.lhs);
		}else {
			return (Literal) node.lhs;
		}
	}

	private Literal getLitRhsPlusMin(Operation node){
		if(node.rhs instanceof VariableReference){
			return getLiteral((VariableReference) node.rhs);
		}else if(node.rhs instanceof Operation){
			return doOps((Operation) node.rhs);
		}else {
			return (Literal) node.rhs;
		}
	}
}
