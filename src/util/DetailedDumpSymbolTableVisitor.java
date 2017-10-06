package util;

import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.visitors.DumpSymbolTableVisitor;

public class DetailedDumpSymbolTableVisitor extends DumpSymbolTableVisitor {

	@Override
	public void visitObjNode(Obj objToVisit) {
		output.append("KIND: ");
		switch (objToVisit.getKind()) {
		case Obj.Con:  output.append("Con "); break;
		case Obj.Var:  output.append("Var "); break;
		case Obj.Type: output.append("Type "); break;
		case Obj.Meth: output.append("Meth "); break;
		case Obj.Fld:  output.append("Fld "); break;
		case Obj.Prog: output.append("Prog "); break;
		}
		
		output.append(" \tNAME: ");
		output.append(objToVisit.getName() + " \t");
		output.append(": ");
		
		if ((Obj.Var == objToVisit.getKind()) && "this".equalsIgnoreCase(objToVisit.getName()))
			output.append("");
		else
			objToVisit.getType().accept(this);
		
		output.append(", ");
		
		output.append("ADR/VAL: ");
		output.append(objToVisit.getAdr() + " \t");
		output.append(", ");
		output.append("LEVEL/FORM ARG CNT: ");
		output.append(objToVisit.getLevel() + " \t");
		output.append("FPPOS: ");
		output.append(objToVisit.getFpPos() + " \t");
		
		if (objToVisit.getKind() == Obj.Prog || objToVisit.getKind() == Obj.Meth) {
			output.append("\n");
			nextIndentationLevel();
		}
		

		for (Obj o : objToVisit.getLocalSymbols()) {
			output.append(currentIndent.toString());
			o.accept(this);
			output.append("\n");
		}
		
		if (objToVisit.getKind() == Obj.Prog || objToVisit.getKind() == Obj.Meth) 
			previousIndentationLevel();
	}
}
