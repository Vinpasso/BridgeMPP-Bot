package bots.CustomParrotBot;

public class LowerCaseMessageOptimization
{
	public static void optimize(CustomParrot parrot)
	{
		parrot.condition = parrot.condition.replaceAll("message\\.toLowerCase\\(\\)", "lowerCaseMessage");
		parrot.operation = parrot.operation.replaceAll("message\\.toLowerCase\\(\\)", "lowerCaseMessage");
	}

}
