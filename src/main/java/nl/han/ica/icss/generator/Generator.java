package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

public class Generator {

	public String generate(AST ast) {
		StringBuilder builder = new StringBuilder();
		ast.root.getChildren().forEach(node -> {
			if (node instanceof Stylerule){
				Stylerule styleruleNode = (Stylerule) node;
				styleruleNode.selectors.forEach(selector -> {
					if(selector instanceof ClassSelector){
						builder.append(((ClassSelector) selector).cls);
					}else if(selector instanceof IdSelector){
						builder.append(((IdSelector) selector).id);
					} else if (selector instanceof TagSelector) {
						builder.append(((TagSelector) selector).tag);
					}
				});
				builder.append("{ \n");
				styleruleNode.body.forEach(body -> {
					if (body instanceof Declaration){
						Declaration declaration = (Declaration) body;
						String value = getValue(declaration.expression);
						builder.append("  ")
								.append(declaration.property.name)
								.append(": ")
								.append(value)
								.append(";\n");
					}
				});
				builder.append("}\n");
			}
		});
		return builder.toString();
	}

	private String getValue(Expression expression){
		switch (expression.getClass().getSimpleName()){
			case "ColorLiteral":
				return ((ColorLiteral) expression).value;
			case "PixelLiteral":
				return ((PixelLiteral) expression).value + "px";
			case "BoolLiteral":
				return ((BoolLiteral) expression).value + "";
			case "ScalarLiteral":
				return ((ScalarLiteral) expression).value + "";
			case "PercentageLiteral":
				return ((PercentageLiteral)expression).value + "%";
			default: return null;
		}
	}
}
