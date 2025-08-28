import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;

public class SilInterpreter extends SilBaseVisitor<Object>
{
    private Map<String, String> devices = new HashMap<>();
    private Map<String, Object> variables = new HashMap<>();

    @Override
    public Void visitDeviceDecl(SilParser.DeviceDeclContext ctx)
    {
        String deviceName = ctx.ID().getText();
        String deviceIp = ctx.STRING().getText().replace("\"", "");

        devices.put(deviceName, deviceIp);

        System.out.println("[DEVICE DECLERATION]: " + deviceName + " sececfully registered at " + deviceIp);

        return null;
    }

    @Override
    public Void visitVarDecl(SilParser.VarDeclContext ctx)
    {
        String varName = ctx.ID().getText();
        Object value = null;
        String tmp = null;

        if (ctx.number() != null)
        {
            tmp = ctx.number().getText();
        }
        else if (ctx.ask() != null)
        {
            tmp = visitAsk(ctx.ask());
        }

        if (tmp.contains(".")) // Float, i deal them as Double
        {
            value = Double.parseDouble(tmp);
        }
        else // Integer
        {
            value = Integer.parseInt(tmp);
        }

        variables.put(varName, value);

        return null;
    }

    @Override
    public Void visitTurn(SilParser.TurnContext ctx)
    {
        String ip = devices.get(ctx.ID().getText());

        if (ip.isEmpty())
        {
            System.out.println("[TURN]: Device not registered");

            return null;
        }

        String bool = ctx.SWITCHING().getText();
        String pin = ctx.GPIO().getText();
        String url = "http://" + ip + "/turn?action=" + bool + "&pin=" + pin;
        String response;

        if (bool.equals("on")) // Maybe i can do it with MQTT
        {
            try
            {
                response = HttpHelper.sendGet(url);
                System.out.println("[TURN]: Device response: " + response);
            }
            catch (Exception e)
            {
                System.out.println("[TURN]: " + e);
                //e.printStackTrace();
            }
        }
        else
        {
            try
            {
                response = HttpHelper.sendGet(url);
                System.out.println("[TURN]: Device response: " + response);
            }
            catch (Exception e)
            {
                System.out.println("[TURN]: " + e);
                //e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public Void visitPrint(SilParser.PrintContext ctx)
    {
        String ip = devices.get(ctx.ID().getText());

        if (ip.isEmpty())
        {
            System.out.println("[PRINT]: Device not registered");

            return null;
        }

        String msg = ctx.STRING().getText().replace("\"", "");

        try
        {
            String url = "http://" + ip + "/print?msg=" + java.net.URLEncoder.encode(msg, "UTF-8");
            String response = HttpHelper.sendGet(url);
            
            System.out.println("[PRINT]: Device response: " + response);
        }
        catch (Exception e)
        {
            System.out.println("[PRINT]: " + e);
            //e.printStackTrace();
        }

        return null;
    }

    @Override
    public String visitAsk(SilParser.AskContext ctx)
    {
        String ip = devices.get(ctx.ID().getText());

        if (ip.isEmpty())
        {
            System.out.println("[ASK]: Device not registered");

            return null;
        }

        String value = ctx.RETURN_VALUE().getText();
        String url = "http://" + ip + "/ask?value=" + value;

        try
        {
            String response = HttpHelper.sendGet(url);
            System.out.println("[ASK]: The " + value + " is " + response);

            return response;
        }
        catch (Exception e)
        {
            System.out.println("[ASK]: " + e);
            //e.printStackTrace();
        }

        return null;
    }

    @Override
    public Void visitIfStmt(SilParser.IfStmtContext ctx)
    {   
        Boolean condition = visitCondition(ctx.condition());

        if (condition)
        {
            visit(ctx.block(0));

            return null;
        }
        
        if (ctx.block(1) == null)
        {
            return null;
        }

        visit(ctx.block(1));

        return null;
    }

    public Boolean visitCondition(SilParser.ConditionContext ctx)
    {
        String comparator = ctx.comparator().getText();

        Object leftOp = evalOperand(ctx.getChild(0));
        Object rightOp = evalOperand(ctx.getChild(2));

        double leftValue = Double.parseDouble(leftOp.toString());
        double rightValue = Double.parseDouble(rightOp.toString());

        switch (comparator)
        {
            case "<":
                return leftValue < rightValue;
            case ">":
                return leftValue > rightValue;
            case "==":
                return leftValue == rightValue;
        }

        return false; // it will never reach but it cries
    }

    private Object evalOperand(ParseTree child)
    {
        if (child instanceof SilParser.NumberContext)
        {
            return Double.parseDouble(child.getText());
        }
        else if (child instanceof SilParser.AskContext)
        {
            return visitAsk((SilParser.AskContext) child);
        }
        else
        {
            // need to handle if ID is not in HashMap
            String varName = child.getText();
            Object varValue = variables.get(varName);

            if (varValue == null)
            {
                throw new RuntimeException("[If]: Variable " + varName + " is not declared");
            }
            
            return varValue;
        }
    }
}
