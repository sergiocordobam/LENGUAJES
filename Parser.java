/** Programming Languajes
	By: Carmen Carvajal
	Parser Class: this class verifies the source file syntax according to the grammar
**/
import java.io.*;

public class Parser{
	//token: (Token) used to process each token in the source code
	private Token token;
	//lexer: (Lexer) used to obtain each token in the source code
	private Lexer lexer;
	
	/**
		Constructor: 
			Creates the lexer to obtain each token in the source file
			Points the iterator to the first token
			Starts parsing at the start symbol according the the grammar
	**/
	public Parser(String fileName){
		try{
			//Creates the lexer
			lexer = new Lexer(fileName);
			//Points the iterator to the first token
			token = lexer.nextToken();
			//Starts the parser
			program();
		} catch (FileNotFoundException ex){
			System.out.println("File not found!");
			System.exit(1);
		}
	}
	/**
		Function recognize: verifies if the current token corresponds to the expected token 
		according with the grammar and move the tokenIterator to the next one.
		@param expected: (int) expected token code
	**/
	private void recognize(int expected){
		if (token.code == expected)
			//the token is the expected one, move the tokenIterator
			token = lexer.nextToken();
		else {
			//The current token is not expected, syntax error!
			System.out.println("Syntax error in line " + token.line);
			System.out.println("Expected: "  + expected + " found " + token.code);
			//STOP!
			System.exit(2);
		}
	}
	/**
		Function program: verifies the production 
			<program>::= program <funDefinitionList> endProgram
	**/
	public void program(){
		//checks for "program"
		recognize(Lexer.PROGRAM);
		//checks for <funDefinitionList>
		funDefinitionList();
		//checks for "endProgram"
		recognize(Lexer.ENDPROGRAM);
		//if EOf is found, no error is found!
		if (token.code == Lexer.EOF){
			System.out.println("No errors found!");
		}
	}
	
	/**
		Function funDefinitionList: verifies the production 
		<funDefinitionList>::=<funDefinition> { <funDefinition> }
	**/ 
	public void funDefinitionList()
	{
		//checks for <funDefinition>
		funDefinition();
		//verifies if there are more <funDefinition>
		while(lexer.getCurrentToken().code == Lexer.DEF){
			funDefinition();
		}
	}
	
	/**
		Function funDefinition: verifies the production 
		<funDefinition>::= def <variable> <lparen> [<varDefList>] <rparen>
								[<varDefList>]
								[<statementList>]
							enddef
	**/
	public void funDefinition(){
		//checks for "def"
		recognize(Lexer.DEF);
		//checks for <variable>
		recognizeVariable();
		//checks for "("
		recognize(Lexer.LPAREN);
		//verifies if there are parameters
		if (lexer.getCurrentToken().code != Lexer.RPAREN) // def main ( )           def main ( int a int b)
			varDefList();
		//checks for ")"
		recognize(Lexer.RPAREN);
		//ver is there are variables definition
		if (lexer.getCurrentToken().code == Lexer.INT)
			varDefList();
		//checks for <statementList>
		statementList();
		//checks for "enddef"
		recognize(Lexer.ENDDEF);
	}
	
	/**
		Function varDefList: verifies the production <varDefList>::=<varDef> {<varDef>}
	**/
	public void varDefList(){
		//checks for <varDef>
		varDef();
		//Verifies if there are more <varDef>
		while (lexer.getCurrentToken().code == Lexer.INT){
			varDef();
		}	
	}
	
	/**
		Function varDef: verifies the production <varDef>::=int variable
	**/
	public void varDef(){
		recognize(Lexer.INT);
		recognize(Lexer.VARIABLE);
	}
	/**
		Function statementList: verifies the production <statementList>::=<statement> {<statement>}
	**/
	public void statementList()
	{
		//checks for <statement>
		statement();
		//verifies if there are more <statement>
		while ((lexer.getCurrentToken().code != Lexer.ENDDEF) && (statement()));
	}

	/**
		Function statement: verifies the production <statement>::= read <variable> | print <variable> | call <variable> <lparen> [ <argumentList> ] <rparen>
	**/
	public boolean statement(){
		boolean r = false;
		//checks for read <variable>
		if (lexer.getCurrentToken().code == Lexer.READ){
			recognize(Lexer.READ);
			recognize(Lexer.VARIABLE);
			System.out.println("Read ok!");
			r=true;
		} //checks for print <variable>
		else if (lexer.getCurrentToken().code == Lexer.PRINT){
			recognize(Lexer.PRINT);
			recognize(Lexer.VARIABLE);
			System.out.println("Print ok!");
			r=true;
		} //checks for call <variable> <lparen> [argumentList] <rparen>|<assign>|<if statement>|<while>
		else if (lexer.getCurrentToken().code == Lexer.CALL){
			//checks for "call"
			recognize(Lexer.CALL);
			//checks for <variable>
			recognize(Lexer.VARIABLE);
			//checks for <lparen>
			recognize(Lexer.LPAREN);
			//verifies if there is an argument list
			if (lexer.getCurrentToken().code != Lexer.RPAREN)
				argumentList();
			//checks for <rparen>
			recognize(Lexer.RPAREN);
			System.out.println("call function ok!");
			//verifies if there is an argument list 
			r=true;			
		}
		else if (lexer.getCurrentToken().code==Lexer.ASSIGN){
			assign();			// error
			System.out.println("assign function ok!");
			r=true;
		}
		else if (lexer.getCurrentToken().code==Lexer.IF){
			ifStatement();
			System.out.println("if function ok!");
			r = true;
		}
		else if (lexer.getCurrentToken().code==Lexer.WHILE){
			While();
			//recognize(Lexer.ENDWHILE); error
			System.out.println("while function ok!");
			r = true;
		}
		return r;
	}
	
	/**
		Function argumentList: verifies the production <argumentList> ::= <argumentDef> { <argumentDef> }
	**/
	public void argumentList(){
		//Checks for <argumentDef>
		argumentDef();
		//verifies if there are more <argumentDef>
		while (lexer.getCurrentToken().code != Lexer.RPAREN)
			argumentDef();
	}
	
	/**
		Function argumentDef: verifies the production <argumentDef> ::= <variable>
	**/
	public void argumentDef(){
		recognize(Lexer.VARIABLE);
	}

	/**
		Function Expresion
	*/
	public void expresion(){
		//recognize(Lexer.LPAREN);	// cambie esto
		term();
		if(lexer.getCurrentToken().code == Lexer.ADD){
			recognize(Lexer.ADD);
			term();
		}
			
		//recognize(Lexer.RPAREN);
	}

	/**
		Function Term
	*/
	public void term(){
		factor();		// cambie esto
		if (lexer.getCurrentToken().code == Lexer.MULT){
			recognize(Lexer.MULT);
			factor();
		}
			
	}
	
	/**
		Function <factor>
	 */
	public void factor(){
		if(lexer.getCurrentToken().code == Lexer.LPAREN){
			recognize(Lexer.LPAREN);
			expresion();
			recognize(Lexer.RPAREN);
		}
		
		
		if (lexer.getCurrentToken().code == Lexer.VARIABLE){
			recognize(Lexer.VARIABLE);
		}
		
		else if(lexer.getCurrentToken().code == Lexer.CONSTANT){
			recognize(Lexer.CONSTANT);
		}
		
	} 
	/**
		Function <condition>;
	 */
	public void condition(){
		expresion();
		if(lexer.getCurrentToken().code == Lexer.EQUALS){	// Cambié esto
			recognize(Lexer.EQUALS);
		}else if(lexer.getCurrentToken().code == Lexer.DEQUALS){
			recognize(Lexer.DEQUALS);
		}
		expresion();
	}
	
	/**
		Function <if statement>
	 */
	public void ifStatement(){
		recognize(Lexer.IF);
		recognize(Lexer.LPAREN);
		condition();
		recognize(Lexer.RPAREN);
		statementList();
		recognize(Lexer.ENDIF);
		if(lexer.getCurrentToken().code == Lexer.ELSE){
			recognize(Lexer.ELSE);
			statementList();
		}
		}

	/**
		Function <while>
	 */
	public void While(){
		recognize(Lexer.WHILE);
		recognize(Lexer.LPAREN);
		condition();
		recognize(Lexer.RPAREN);
		statementList();
		recognize(Lexer.ENDWHILE);
	}

	/**
		Function <assign>
	*/
	public void assign(){
		varDef();
		recognize(Lexer.ASSIGN);
		expresion();
	}

	/**public void add(){
		varDefList();
		recognize(Lexer.ADD);
		varDefList();
	}

	public void mult(){
		varDefList();
		recognize(Lexer.MULT);
		varDefList();
	}
	*/
	
	/**
		Function recognizeVariable: verifies for variables
	*/
	public void recognizeVariable(){
		recognize(Lexer.VARIABLE);
	}
	/**
		Function <letter>
	 */
	public void letter(){
		for (int i = 0; i < token.text.length();i++){
			char a = token.text.charAt(i);
			if((a >= 'a' && a <= 'z')){
				recognize(Lexer.VARIABLE);
			}
		}
	}

	/**
	Function<var>
	 */
	public void variable(){
		letter();
		/*if(lexer.getCurrentToken().code == Lexer.VARIABLE){
			letter();
		}
		*/
	}

	/**
	Function<cons>
	 */
	public void cons(){
		digit();
		/*if(lexer.getCurrentToken().code == Lexer.CONSTANT){
			digit();
		}
		*/
	}
	/**
	function <digit>
	 */
	public void digit(){
		for (int i = 0; i < token.text.length();i++){
			char a = token.text.charAt(i);
			if((a >= '0' && a <= '9')){
                recognize(Lexer.CONSTANT);
            }
		}
	}
	
	
	public static void main(String args[])
	{
		try {
			String fileName = args[0];
			Parser parser = new Parser(fileName);
				} catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}



}
