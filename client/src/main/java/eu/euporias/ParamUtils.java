package eu.euporias;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import eu.euporias.ArgumentList.Argument;

public class ParamUtils {

	private static final Logger logger = LoggerFactory.getLogger(ParamUtils.class);

	private static final String ARGUMENT_PREFIX = "--";	
	private static final String ARGUMENT_DELIMITER = "=";
	private static final Splitter ARGUMENT_SPLITTER = Splitter.on(ARGUMENT_DELIMITER);
	private static final String PARAM_DELIMITER = ":";
	private static final Splitter PARAM_SPLITTER= Splitter.on(PARAM_DELIMITER);
	
	protected final static Predicate<String> PARAMETER_PREDICATE = new Predicate<String>(){
		@Override
		public boolean apply(String input) {
			return input.startsWith(ARGUMENT_PREFIX+Argument.param.name());
		}		
	};
	
	protected static ArgumentList parameters(String[] args,Predicate<String> filter){
		ArgumentList params = new ArgumentList();
		for(String arg : args){
			if(filter.apply(arg)){
				List<String> param = Lists.newArrayList(ARGUMENT_SPLITTER.split(arg));
				if(param.size()==2){
					String argKey = param.get(0);
					String argValue = param.get(1);
					if((ARGUMENT_PREFIX+Argument.param.name()).equals(argKey)){
						List<String> value = Lists.newArrayList(PARAM_SPLITTER.split(argValue));
						if(value.size()==2){
							try{
								Argument Argument = eu.euporias.ArgumentList.Argument.valueOf(value.get(0));
								params.add(Argument,value.get(1));	
							}catch(Exception e){
								logger.warn("Ignoring the unknowkn parameter: "+value.get(0));
							}
						}else{
							logger.warn("Ignoring the invalid parameter: "+arg+". Expected sintax is "+ARGUMENT_PREFIX+Argument.param.name()+ARGUMENT_DELIMITER+"key"+PARAM_DELIMITER+"value");
						}
					}else{
						try{
							Argument Argument = eu.euporias.ArgumentList.Argument.valueOf(argKey.substring(2));
							params.add(Argument,argValue);	
						}catch(Exception e){
							logger.warn("Ignoring the unknowkn argument: "+argKey);	
						}						
					}
				}else{
					logger.warn("Ignoring the invalid argument: "+arg+". Expected sintax is "+ARGUMENT_PREFIX+"key"+ARGUMENT_DELIMITER+"value");
				}
			}
		}
		return params;
	}
}
