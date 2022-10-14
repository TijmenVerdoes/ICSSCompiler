package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

public class Checker {

	private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

	public void check(AST ast) {
//		HashMap<String, ExpressionType> hashMap = new HashMap<>();
//		variableTypes.addFirst(hashMap);
//		ast.root.getChildren().forEach(n -> {
//			Expression expression = ((Declaration) n).expression;
//			checkExpressionBooleanOrColor((Operation) expression);
//
//			if (n instanceof VariableAssignment){
//				if (((VariableAssignment) n).expression instanceof Literal){
//
//				}
//			}
//		});
//	}
//
////	private boolean checkExpressionBooleanOrColor(Operation node){
////		Expression left = node.lhs;
////		Expression right = node.rhs;
////		if (left instanceof Operation){
////			return checkExpressionBooleanOrColor((Operation) left);
////		}
////		if (right instanceof Operation){
////			return checkExpressionBooleanOrColor((Operation) right);
////		}
////
////	}
//
//	private ExpressionType getExpressionType(Expression expression) {
//		if (expression instanceof Literal){
//			return getLiteralType(expression);
//		}else if (expression instanceof VariableReference){
//			//return expression.get;
//		}
//	}
//
//	private ExpressionType getLiteralType(Expression expression){
//		switch (expression.getClass().getSimpleName()){
//			case "ColorLiteralContext": return ExpressionType.COLOR;
//			case "PixelLiteralContext": return ExpressionType.PIXEL;
//			case "BoolLiteralContext": return ExpressionType.BOOL;
//			case "PercentageLiteralContext": return ExpressionType.PERCENTAGE;
//			case "ScalarLiteralContext": return ExpressionType.SCALAR;
//		}
//		return ExpressionType.UNDEFINED;
	}
}
