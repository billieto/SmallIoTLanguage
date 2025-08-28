import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class App
{
    public static void main (String[] args) throws Exception
    {
        String inputFile = "input.sil";
        CharStream input = CharStreams.fromFileName(inputFile);

        SilLexer lexer = new SilLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SilParser parser = new SilParser(tokens);
        
        ParseTree tree = parser.program();
        SilInterpreter interpreter = new SilInterpreter();
        interpreter.visit(tree);

        //System.out.println(tree.toStringTree(parser));
    }
}
