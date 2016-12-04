package bridgempp.bot.metawrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface MetaClass {
	String triggerPrefix() default "?$CLASSNAME ";
	String helpTopic() default "This is $CLASSNAME Meta Bot";
}
