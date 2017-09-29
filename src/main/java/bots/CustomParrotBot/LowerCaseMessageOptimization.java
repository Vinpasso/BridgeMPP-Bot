package bots.CustomParrotBot;

public class LowerCaseMessageOptimization
{
	public static void optimize(CustomParrot parrot)
	{
		parrot.setCondition(parrot.getCondition().replaceAll("message\\.toLowerCase\\(\\)", "lowerCaseMessage"));
		parrot.setOperation(parrot.getOperation().replaceAll("message\\.toLowerCase\\(\\)", "lowerCaseMessage"));
	}

}
