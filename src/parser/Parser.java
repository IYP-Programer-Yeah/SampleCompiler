package parser;

import cg.CodeGenerator;
import scanner.ScannerWrapper;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Stack;

public class Parser {
	ScannerWrapper scanner;
	CodeGenerator cg;

	OutputStream log;
	OutputStream bin;

	PTBlock[][] parseTable;    // a 2D array of blocks, forming a parse table
	Stack<Integer> parseStack = new Stack<Integer>();
	String[] symbols;

	int symbolInLine = -1;
	int symbolInlineUseCount = 0;

	String additionalReductionSem = "@";

	String sourceName;
	/**
	 * Creates a new parser
	 *
	 * @param is         input stream from source text file
	 * @param log        log output stream to write the output there (if any)
	 * @param bin        binary output stream to write the output there (if any)
	 * @param symbols    symbols known by parser (tokens + graph nodes)
	 * @param parseTable all of the actions describing the parser behaviour
	 */
	public Parser(InputStream is, OutputStream log, OutputStream bin, String[] symbols, PTBlock[][] parseTable, String sourceName) {
		try {
			this.parseTable = parseTable;
			this.symbols = symbols;
			scanner = new ScannerWrapper(is);
			this.log = log;
			this.bin = bin;
			cg = new CodeGenerator(scanner, log, bin, sourceName);
			this.sourceName = sourceName;
		} catch (Exception e) {
			System.err.println("Parsing Error -> IOException at opening input stream");
		}
	}

	/**
	 * All the parsing operations is here.
	 * operations were defined in .npt file, and now they are loaded into parseTable
	 */
	public void parse() {
		try {
			int tokenID = nextTokenID();
			int currentNode = 0;   // start node
			boolean accepted = false;   // is input accepted by parser?
			while (!accepted) {
				// current token's text
				String tokenText = symbols[tokenID];
				// current block of parse table
				PTBlock ptb = parseTable[currentNode /* the node that parser is there */][tokenID /* the token that parser is receiving at current node */];

				switch (ptb.getAct()) {
					case PTBlock.ActionType.Error: {
						System.out.println("Error");
					}
					break;
					case PTBlock.ActionType.Shift: {
						doSemantics(ptb.getSem());
						tokenID = nextTokenID();
						currentNode = ptb.getIndex();  // index is pointing to Shift location for next node

					}
					break;

					case PTBlock.ActionType.Goto: {
						doSemantics(ptb.getSem());
						currentNode = ptb.getIndex();  // index is pointing to Goto location for next node
					}
					break;

					case PTBlock.ActionType.PushGoto: {
						parseStack.push(currentNode);
						currentNode = ptb.getIndex();  // index is pointing to Goto location for next node
					}
					break;

					case PTBlock.ActionType.Reduce: {
						if (parseStack.size() == 0) {
							throw new Exception("Compile Error trying to Reduce(Return) at token \"" + tokenText + " ; node@" + currentNode);
						}

						int graphToken = ptb.getIndex();    // index is the graphToken to be returned
						int preNode = parseStack.pop();     // last stored node in the parse stack

						doSemantics(additionalReductionSem);
						additionalReductionSem = "@";

						doSemantics(parseTable[preNode][graphToken].getSem());
						currentNode = parseTable[preNode][graphToken].getIndex(); // index is pointing to Goto location for next node
					}
					break;

					case PTBlock.ActionType.Accept: {
						accepted = true;
					}
					break;

				}
			}
		} catch (Exception e) {
			//System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private int nextTokenID() throws Exception {
		if (symbolInLine != -1 && symbolInlineUseCount < 4) {
			symbolInlineUseCount++;
			int temp = symbolInLine;
			symbolInLine = -1;
			return temp;
		}
		symbolInLine = -1;
		symbolInlineUseCount = 0;
		String t = null;
		try {
			t = scanner.nextToken();
		} catch (Exception e) {
			log.write(e.getMessage().getBytes());
		}

		int i;

		for (i = 0; i < symbols.length; i++)
			if (symbols[i].equals(t))
				return i;

		throw new Exception("Undefined token: " + t);
	}

	private void doSemantics(String semantics)
	{
		if (semantics.equals("NoSem"))
			return;
		semantics = semantics.replace("@","");
		if (semantics.contains(";")) {
			String[] microSems = semantics.split("[;]");
			for (String microSem : microSems)
				if (microSem.length()>0) {
					if (microSem.charAt(0) != '^')
						cg.doSemantic("@" + microSem);
					else
						additionalReductionSem += microSem.substring(1) + ";";
				}
		}
	}

	/**
	 * Used to write any needed output after the parsing is done.
	 */
	public void writeOutput() {
		// It is common that the code generator does it
		cg.writeOutput(log);
	}
}
