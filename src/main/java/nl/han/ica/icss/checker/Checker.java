package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.datastructures.implementations.HANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

public class Checker<T> {
	private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

	public void check(AST ast) {
		variableTypes = new HANLinkedList<>();
		enterScope();
		ast.root.getChildren().forEach(this::checkVariables);
		exitscope();
	}

	private void checkVariables(ASTNode n){
		if(n instanceof VariableAssignment){
			checkVarAssignment(n);
		}
		else if(n instanceof Stylerule){
			enterScope();
			n.getChildren().forEach(this::checkVariables);
			exitscope();
		}
		else if(n instanceof Declaration){
			checkDec(n);
		}
		else if (n instanceof Operation){
			checkColorBool((Operation) n);
		}
		else if (n instanceof IfClause){
			checkIfClause((IfClause) n);
		}
	}

	private void checkVarAssignment(ASTNode n){
		String name;
		ExpressionType expressionType;
		if (((VariableAssignment) n).expression instanceof Literal){
			name = (((VariableAssignment) n).name.name);
			expressionType = getLiteralType(((VariableAssignment) n).expression);
			insertInScope(name, expressionType);
		}else if (((VariableAssignment) n).expression instanceof Operation){
			checkColorBool((Operation) ((VariableAssignment) n).expression);
			if(((Operation) ((VariableAssignment) n).expression).lhs instanceof VariableReference){
				checkDec(((Operation) ((VariableAssignment) n).expression).lhs);
			}
			if(((Operation) ((VariableAssignment) n).expression).rhs instanceof VariableReference){
				checkDec(((Operation) ((VariableAssignment) n).expression).rhs);
			}

		}
		else if (((VariableAssignment) n).expression instanceof VariableReference){
			name = ((VariableAssignment) n).name.name;
			expressionType = getVariable(((VariableReference) ((VariableAssignment) n).expression).name);
			insertInScope(name, expressionType);
		}
	}

	private void checkDec(ASTNode n){
		var expression = ((Declaration) n).expression;
		if((expression.getClass() == VariableReference.class)){
			var varName = ((VariableReference) expression).getName();
			var found = false;
			for(int i = 0; i < variableTypes.getSize(); i++) {
				if (variableTypes.get(i).get(varName) != null) {
					found = true;
				}
			}
			if(!found){
				n.setError("Variable " + varName + " not defined within scope");
			}
		}
		checkVariables(((Declaration) n).expression);
	}

	private void checkColorBool(Operation n){
		if(n.lhs instanceof ColorLiteral || n.rhs instanceof ColorLiteral || n.lhs instanceof BoolLiteral || n.rhs instanceof BoolLiteral){
			n.setError("Colors and Booleans are not allowed in sums");
		}else if (n.lhs instanceof Operation){
			checkColorBool((Operation) n.lhs);
		}else if (n.rhs instanceof Operation) {
			checkColorBool((Operation) n.rhs);
		}
	}

	private void checkIfClause(IfClause n){
		if (n.getConditionalExpression() instanceof VariableReference){
			ExpressionType expressionType =  getVariable(((VariableReference) n.getConditionalExpression()).name);
			if (expressionType == null){
				n.setError("Variable " + ((VariableReference) n.getConditionalExpression()).name+ " not defined within scope");
			}
			else if (expressionType != ExpressionType.BOOL){
				n.setError("Condition is not a boolean");
			}
		}else if(!(n.getConditionalExpression() instanceof BoolLiteral)){
			n.setError("Conditionliteral is not a boolean");
		}
	}

	public void enterScope() {
		HashMap<String, ExpressionType> hashMap = new HashMap<>();
		variableTypes.addFirst(hashMap);
	}

	public void insertInScope(String varName, ExpressionType anytype) {
		HashMap<String, ExpressionType> temp = variableTypes.get(variableTypes.getSize()-1);
		temp.put(varName, anytype);
		variableTypes.delete(variableTypes.getSize()-1);
		variableTypes.addFirst(temp);
	}

	private void exitscope(){
		if(variableTypes.getSize() != 0){
			variableTypes.delete(variableTypes.getSize()-1);
		}
	}

	private ExpressionType getVariable(String name){
		for(int i = 0; i < variableTypes.getSize(); i++){
			HashMap<String, ExpressionType> temp = variableTypes.get(i);
			if(temp.get(name) != null){
				return temp.get(name);
			}
		}
		return null;
	}

	private ExpressionType getLiteralType(Expression expression){
		String literal = expression.getClass().getSimpleName();
		if (literal.equals("ColorLiteral")) {
			return ExpressionType.COLOR;
		}
		if (literal.equals("PixelLiteral")) {
			return ExpressionType.PIXEL;
		}

		if (literal.equals("BoolLiteral")) {
			return ExpressionType.BOOL;
		}
		if (literal.equals("PercentageLiteral")){
			return ExpressionType.PERCENTAGE;
		}
		if (literal.equals("ScalarLiteral")) {
			return ExpressionType.SCALAR;

		}
		return ExpressionType.UNDEFINED;
	}
}
