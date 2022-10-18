package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.datastructures.implementations.HANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.selectors.TagSelector;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

public class Checker<T> {
	private IHANLinkedList<HashMap<String, T>> variables;

	public void check(AST ast) {
		variables = new HANLinkedList<>();
		enterScope();
		ast.root.getChildren().forEach(this::checkVariables);
		exitscope();
	}

	private void checkVariables(ASTNode n){
		String name;
		T expressionType;
		if(n instanceof VariableAssignment){
			if (((VariableAssignment) n).expression instanceof Literal){
				checkScope(n);
			}else if (((VariableAssignment) n).expression instanceof VariableReference){
				name = ((VariableAssignment) n).name.name;
				expressionType = getVariable(((VariableReference) ((VariableAssignment) n).expression).name);
				insertInScope(name, (T) expressionType);
			}
		}
		else if(n instanceof Stylerule){
			enterScope();
			n.getChildren().forEach(this::checkVariables);
//			System.out.println("exit: " + variables);
			exitscope();
		}
		else if(n instanceof Declaration){
			var expression = ((Declaration) n).expression;
			if((expression.getClass() == VariableReference.class)){
				var varName = ((VariableReference) expression).getName();
				//System.out.println(varName);
				var found = false;
				for(int i = 0; i < variables.getSize(); i++) {
					if (variables.get(i).get(varName) != null) {
						found = true;
					}
				}
				if(!found){
					System.out.println("not found");
				}
			}
			checkVariables(((Declaration) n).expression);
		}
	}

	public void enterScope() {
		HashMap<String, T> hashMap = new HashMap<>();
		variables.addFirst(hashMap);
//		System.out.println("enter " + variables);
	}

	public void insertInScope(String varName, T anytype) {
		HashMap<String, T> temp = variables.getFirst();
		temp.put(varName, anytype);
		variables.removeFirst();
		variables.addFirst(temp);
		//System.out.println(variables);
	}

	private void exitscope(){
		if(variables.getSize() != 0){
			variables.delete(variables.getSize()-1);
		}
	}

	private void checkScope(ASTNode node){
		String name;
		ExpressionType expressionType;
		if (node instanceof VariableAssignment){
			VariableAssignment variableAssignment = (VariableAssignment) node;
			name = (variableAssignment.name.name);
			if (variableAssignment.expression instanceof Literal){
				expressionType = getLiteralType(variableAssignment.expression);
				insertInScope(name, (T) expressionType);
			}
		}
	}

	private T getVariable(String name){
		for(int i = 0; i < variables.getSize(); i++){
			HashMap<String, T> temp = variables.get(i);
			if(temp.get(name) != null){
				return temp.get(name);
			}
		}
		return null;
	}

	private void checkVariabelInScope(AST ast, int baseScope, int currentScope, IHANLinkedList<HashMap<String, ASTNode>> hashList) {
		if(hashList.getSize() - 1 - baseScope > -2) {
			if(hashList.getSize() - 1 - currentScope > -2) {
				HashMap<String, ASTNode> baseMap = hashList.get(hashList.getSize() - 1 - baseScope);
				HashMap<String, ASTNode> currentMap = hashList.get(hashList.getSize() - 1 - currentScope);

				//Checken of er überhaupt een variabelReferentie aanwezig is.
				boolean variabelAanwezig = false;
				for (int i = 0; i < baseMap.size(); i++) {
					if (baseMap.get(i) instanceof VariableReference) {
						variabelAanwezig = true;
					}
				}

				//Zo niet, kijk een scope verder.
				if (!variabelAanwezig) {
					baseScope += 1;
					currentScope += 1;
					checkVariabelInScope(ast, baseScope, currentScope, hashList);
					return;
				}

				boolean isDeclaredWholeScope = false;
				for (int i = 0; i < baseMap.size(); i++) {
					boolean isDeclared = false;
					for (int j = 0; j < currentMap.size(); j++) {
						if (baseMap.get(i) instanceof VariableReference && currentMap.get(j) instanceof VariableAssignment) {
							if(!(currentMap.get(j).getChildren().get(0).toString().equals(baseMap.get(i).toString())) && !isDeclared) {
								isDeclared = false;
							} else {
								isDeclared = true;
							}
						}
					}
					isDeclaredWholeScope = isDeclared;

					//Als variabele in base case ergens in dezelfde of hogere scopes is gedeclareerd, basescope + 1, vanaf daar vanaf de basescope en boven kijken.
					if(isDeclaredWholeScope) {
						baseScope += 1;
						checkVariabelInScope(ast, baseScope, baseScope, hashList);
					}
                    /*Als variabele in basescope nog niet gedeclareerd blijkt te zijn en we zijn in de hoogste scope aan het zoeken
                    en we hebben geen VariableAssignment gevonden die erbij past, setError.*/
					else if (hashList.getSize() - 1 - currentScope == -1) {
						ast.root.setError("Je mag niet refereren naar een variabel die nog niet is gedeclareerd of buiten de scope is gedeclareerd.");
					}
					//Als er over de hele currentscope niet de bijpassende VariableAssignment te vinden is en er is nog een scope hoger, currentScope++
					else {
						currentScope++;
						checkVariabelInScope(ast, baseScope, currentScope, hashList);
					}
				}
			}
		}
	}


//	private boolean checkExpressionBooleanOrColor(Operation node){
//		Expression left = node.lhs;
//		Expression right = node.rhs;
//		if (left instanceof Operation){
//			return checkExpressionBooleanOrColor((Operation) left);
//		}
//		if (right instanceof Operation){
//			return checkExpressionBooleanOrColor((Operation) right);
//		}
//
//	}



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
