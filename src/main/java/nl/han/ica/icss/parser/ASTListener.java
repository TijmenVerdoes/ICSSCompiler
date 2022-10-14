package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.implementations.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private final AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private final IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}
	public AST getAST() {
		return ast;
	}

	//Stylesheet
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		ASTNode styleSheet = new Stylesheet();
		currentContainer.push(styleSheet);
	} 
	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		ast.setRoot((Stylesheet)currentContainer.pop());
	}

	//Variables
	@Override
	public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		currentContainer.push(new VariableAssignment());
	}

	@Override
	public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		currentContainer.peek().addChild(currentContainer.pop());
	}

	@Override
	public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
		currentContainer.push(new VariableReference(ctx.getText()));
	}

	@Override
	public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
		currentContainer.peek().addChild(currentContainer.pop());
	}

	//Stylerule
	@Override
	public void enterStyleRule(ICSSParser.StyleRuleContext ctx) {
		currentContainer.push(new Stylerule());
	}

	@Override
	public void exitStyleRule(ICSSParser.StyleRuleContext ctx) {
		currentContainer.peek().addChild(currentContainer.pop());
	}

	//Selectors
	@Override
	public void enterTagSelector(ICSSParser.TagSelectorContext ctx) {
		currentContainer.push(new TagSelector(ctx.getText()));
	}

	@Override
	public void exitTagSelector(ICSSParser.TagSelectorContext ctx) {
		currentContainer.peek().addChild(currentContainer.pop());
	}

	@Override
	public void enterClassSelector(ICSSParser.ClassSelectorContext ctx) {
		currentContainer.push(new ClassSelector(ctx.getText()));
	}

	@Override
	public void exitClassSelector(ICSSParser.ClassSelectorContext ctx) {
		currentContainer.peek().addChild(currentContainer.pop());
	}

	@Override
	public void enterIdSelector(ICSSParser.IdSelectorContext ctx) {
		currentContainer.push(new IdSelector(ctx.getText()));
	}

	@Override
	public void exitIdSelector(ICSSParser.IdSelectorContext ctx) {
		currentContainer.peek().addChild(currentContainer.pop());
	}

	//Property Name
	@Override
	public void enterPropertyName(ICSSParser.PropertyNameContext ctx) {
		currentContainer.push(new PropertyName(ctx.getText()));
	}

	@Override
	public void exitPropertyName(ICSSParser.PropertyNameContext ctx) {
		currentContainer.peek().addChild(currentContainer.pop());
	}


	//Declaration
	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		currentContainer.push(new Declaration());
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		currentContainer.peek().addChild(currentContainer.pop());
	}

	//Expression
	@Override
	public void enterExpression(ICSSParser.ExpressionContext ctx) {
		if (ctx.getChildCount() == 3){
			Expression expression = null;
			switch (ctx.getChild(1).getText()) {
				case "*": expression = new MultiplyOperation(); break;
				case "+": expression = new AddOperation(); break;
				case "-": expression = new SubtractOperation(); break;
			}
			currentContainer.push(expression);
		}
	}

	public void exitExpression(ICSSParser.ExpressionContext ctx) {
		if (ctx.MUL() != null | ctx.PLUS() != null | ctx.MIN() != null) {
			currentContainer.peek().addChild(currentContainer.pop());
		}
	}

	//Literals
	@Override
	public void enterColorliteral(ICSSParser.ColorliteralContext ctx) {
		currentContainer.push(new ColorLiteral(ctx.getText()));
	}

	@Override
	public void exitColorliteral(ICSSParser.ColorliteralContext ctx) {
		currentContainer.peek().addChild(currentContainer.pop());
	}

	@Override
	public void enterPixelliteral(ICSSParser.PixelliteralContext ctx) {
		currentContainer.push(new PixelLiteral(ctx.getText()));
	}

	@Override
	public void exitPixelliteral(ICSSParser.PixelliteralContext ctx) {
		currentContainer.peek().addChild(currentContainer.pop());
	}

	@Override
	public void enterBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
		currentContainer.push(new BoolLiteral(ctx.getText()));
	}

	@Override
	public void exitBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
		currentContainer.peek().addChild(currentContainer.pop());
	}

	@Override
	public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
		currentContainer.push(new ScalarLiteral(ctx.getText()));
	}

	@Override
	public void exitScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
		currentContainer.peek().addChild(currentContainer.pop());
	}

	@Override
	public void enterPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
		currentContainer.push(new PercentageLiteral(ctx.getText()));
	}

	@Override
	public void exitPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
		currentContainer.peek().addChild(currentContainer.pop());
	}

	//If Else
	@Override
	public void enterIfClause(ICSSParser.IfClauseContext ctx) {
		currentContainer.push(new IfClause());
	}

	@Override
	public void exitIfClause(ICSSParser.IfClauseContext ctx) {
		currentContainer.peek().addChild(currentContainer.pop());
	}

	@Override
	public void enterElseClause(ICSSParser.ElseClauseContext ctx) {
		currentContainer.push(new ElseClause());
	}

	@Override
	public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
		currentContainer.peek().addChild(currentContainer.pop());
	}
}

