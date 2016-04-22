package bots.CustomParrotBot;

public class LowerCaseMessageOptimization
{
	public static void optimize(CustomParrot parrot)
	{
		parrot.condition = parrot.condition.replace("message.toLowerCase()", "lowerCaseMessage");
		parrot.operation = parrot.operation.replaceAll("message.toLowerCase()", "lowerCaseMessage");
	}

}
