package source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java_cup.runtime.Symbol;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import util.DetailedDumpSymbolTableVisitor;
import util.Log4JUtils;

public class ParserTest {

	static {
		DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
	}
	
	public static void main(String[] args) throws Exception {
		
		Logger log = Logger.getLogger(ParserTest.class);
		
		Reader br = null;
		try {
			File sourceCode = new File("test/parserTest.mj");
			log.info("Compiling source file: " + sourceCode.getAbsolutePath());
			
			br = new BufferedReader(new FileReader(sourceCode));
			Yylex lexer = new Yylex(br);
			
			MJParser p = new MJParser(lexer);
	        p.parse();  //pocetak parsiranja
	        
	        //Ovde ide ispis
	        
	        System.out.println(p.parserHelper.printParseCount());
	        Tab.dump(new DetailedDumpSymbolTableVisitor());
	        System.out.println("Dump complete.");
	        if(p.parserHelper.isErrorDetected()) {
	        	System.out.println("GRESKA!");
	        }
	        else {
	        	File objFile = new File("test/program.obj");
	        	if (objFile.exists())
	        		objFile.delete();
	        	Code.write(new FileOutputStream(objFile));
	        }
		} 
		finally {
			if (br != null) try { br.close(); } catch (IOException e1) { log.error(e1.getMessage(), e1); }
		}

	}
}
