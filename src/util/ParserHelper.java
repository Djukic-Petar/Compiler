package util;

import java.util.*;

import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import source.MJParser;

public class ParserHelper {

	public static final int ASSIGN = 0,
							PLUSASSIGN = 1,
							MINUSASSIGN = 2,
							MULASSIGN = 3,
							DIVASSIGN = 4,
							MODASSIGN = 5,
							PLUS = 6,
							MINUS = 7,
							MUL = 8,
							DIV = 9,
							MOD = 10;

	private MJParser parser;
	public Struct boolType;
	public Obj designatorStatementDesignator;

	private int globalVarCnt = 0;
	private int localVarCnt = 0;
	private int globalConstCnt = 0;
	private int globalArrayCnt = 0;
	private Obj currentTypeDeclaration = null;
	private boolean mainFunctionDefined = false;
	private boolean errorDetected = false;
	private boolean returnFound = false;
	private Obj methodReturnType = Tab.noObj;

	public Stack<Obj> currentScopeStack = new Stack<>();
	public List<Obj> formalParamList = new ArrayList<>();
	public Stack<Obj> methodInvocationStack = new Stack<>();
	public Stack<Integer> formalParameterPositionStack = new Stack<>();
	
	public boolean isReturnFound() {
		return returnFound;
	}

	public boolean isCombinedOperator(int mulOpCode) {
		if(mulOpCode < 6) return true;
		else return false;
	}

	public void setReturnFound(boolean returnFound) {
		this.returnFound = returnFound;
	}

	public Obj getMethodReturnType() {
		return methodReturnType;
	}

	public void setMethodReturnType(Obj methodReturnType) {
		this.methodReturnType = methodReturnType;
	}

	public Obj getFormalParamType()
	{
		int paramNumber = formalParameterPositionStack.pop();
		Obj currentMethod = methodInvocationStack.peek();
		if(currentMethod.getName().equals("ord") || currentMethod.getName().equals("len") || currentMethod.getName().equals("chr"))		
			paramNumber--;

		formalParameterPositionStack.push(paramNumber + 1);
		

		Collection<Obj> methodLocals = currentMethod.getLocalSymbols();
		for(Obj cur : methodLocals)
		{
			if(cur.getFpPos() == paramNumber)
				return cur;
		}
		return Tab.noObj;
	}
	
	public String objToString(Obj obj)
	{
		String retVal = "";
		retVal += "Obj name = " + obj.getName() + "\n";
		retVal += "Obj adr = " + obj.getAdr() + "\n";
		
		retVal += "Obj kind = ";
		switch (obj.getKind()) {
		case Obj.Con:  retVal += "Con "; break;
		case Obj.Var:  retVal += "Var "; break;
		case Obj.Type: retVal += "Type "; break;
		case Obj.Meth: retVal += "Meth "; break;
		case Obj.Fld:  retVal += "Fld "; break;
		case Obj.Prog: retVal += "Prog "; break;
		}
		retVal += "\n";
		
		retVal += "Obj type = ";
		switch (obj.getType().getKind()) {
		case Struct.None:
			retVal += "notype";
			break;
		case Struct.Int:
			retVal += "int";
			break;
		case Struct.Char:
			retVal += "char";
			break;
		case Struct.Array:
			retVal += "Arr of ";
			
			switch (obj.getType().getElemType().getKind()) {
			case Struct.None:
				retVal += "notype";
				break;
			case Struct.Int:
				retVal += "int";
				break;
			case Struct.Char:
				retVal += "char";
				break;
			case Struct.Class:
				retVal += "Class";
				break;
			}
			break;
		case Struct.Class:
			retVal += "Class [nope";
			retVal += "]";
			break;
		}
		retVal += "\n";
		
		return retVal;
	}

	public void errorDetected() { errorDetected = true; }

	public boolean isErrorDetected() { return errorDetected; }
	
	public ParserHelper(MJParser myParser) {
		parser = myParser;
	}

	public Obj typeNameToObj(String typeName) {
		Obj temp = Tab.find(typeName);
		if(temp == Tab.noObj) {
			parser.report_error("Unknown type " + typeName + "!", null);
			return temp;
		} else if (temp.getKind() == Obj.Type) {
			return temp;
		} else {
			parser.report_error("Unknown type " + typeName + "! Name in use but not as type!", null);
			return temp;
		}
	}

	public Obj getCurrentTypeDeclaration() {
		return currentTypeDeclaration;
	}
	public void setCurrentTypeDeclaration(Obj currentTypeDeclaration) {
		this.currentTypeDeclaration = currentTypeDeclaration;
	}

	private boolean inProgram = false;
	private boolean inMain = false;
	
	public boolean isInProgram() { return inProgram; }
	public boolean isInMain() { return inMain; }
	
	public void enteringProgram() { inProgram = true; }
	public void exitingProgram() { inProgram = false; }
	
	public void enteringMain() { 
		inMain = true;
		mainFunctionDefined = true;
	}
	public void exitingMain() { inMain = false; }

	public boolean isMainDefined() { return mainFunctionDefined; }

	public void globalVarFound() { globalVarCnt++; }
	public void localVarFound() { localVarCnt++; }
	public void globalConstFound() { globalConstCnt++; }
	public void globalArrayFound() { globalArrayCnt++; }
	
	public int getGlobalVarCnt() {
		return globalVarCnt;
	}
	public int getLocalVarCnt() {
		return localVarCnt;
	}
	public int getGlobalConstCnt() {
		return globalConstCnt;
	}
	public int getGlobalArrayCnt() {
		return globalArrayCnt;
	}
	
	public String printParseCount() {
		String retVal = "";
		retVal += "Global variable count: " + globalVarCnt + "\n";
		retVal += "Local variable count: " + localVarCnt + "\n";
		retVal += "Global array count: " + globalArrayCnt + "\n";
		retVal += "Global constant count: " + globalConstCnt + "\n";
		return retVal;
	}
	
	public void initBool() {
		boolType = new Struct(Struct.Bool);
		Tab.currentScope.addToLocals(new Obj(Obj.Type, "bool", boolType));
	}

	public Obj evaluateNumConst(Integer constant) {
		Obj retVal = new Obj(Obj.Con, "Temp", Tab.intType);
		retVal.setAdr(constant.intValue());
		return retVal;
	}

	public Obj evaluateCharConst(Character constant) {
		Obj retVal = new Obj(Obj.Con, "Temp", Tab.charType);
		retVal.setAdr((int)constant.charValue());
		return retVal;
	}

	public Obj evaluateBoolConst(Boolean constant) {
		Obj retVal =  new Obj(Obj.Con, "Temp", boolType);
		retVal.setAdr(constant.booleanValue() ? 1 : 0);
		return retVal;
	}
}
