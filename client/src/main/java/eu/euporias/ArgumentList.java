package eu.euporias;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

public class ArgumentList {

	protected static enum ArgumentType{
		integer, string, file
	}
	
	protected static enum Argument{
		action(false), product, application, outcomeType, 
		mimeType, results(ArgumentType.file), param,
        stationLon, stationLat, stationName,
        forecastStartTime, stationId,
        secret(false),user(false),
        proxyUrl(false),proxyPort(false);

		private final ArgumentType argumentTye;
		private final Boolean toBeSent;		
		Argument(ArgumentType argumentType,Boolean toBeSent){
			this.argumentTye = argumentType;
			this.toBeSent = toBeSent;
		}
		Argument(ArgumentType argumentType){
			this(argumentType,true);
		}
		Argument(Boolean toBeSent){
			this(ArgumentType.string,toBeSent);
		}
		Argument(){
			this(ArgumentType.string,true);
		}
		public ArgumentType getArgumentTye() {
			return argumentTye;
		}
		public Boolean toBeSent() {
			return toBeSent;
		}
	}
	
	private static final Predicate<Argument> SENDABLE_PREDICATE = new Predicate<Argument>(){
		@Override
		public boolean apply(Argument input) {
			return input.toBeSent();
		}
	};
	
	private Map<Argument,String> arguments = new HashMap<Argument,String>();
	
	public String add(Argument argument,String value){
		return arguments.put(argument, value);
	}
	
	public Set<Map.Entry<Argument,String>> get(){
		return Maps.filterKeys(arguments,SENDABLE_PREDICATE).entrySet();
	}
	
	public String get(Argument argument){
		if(!arguments.containsKey(argument)){
			throw new IllegalArgumentException("Missing argument "+argument);
		}
		switch (argument.getArgumentTye()){
		case string:
			return arguments.get(argument);
		case integer:
			return Integer.valueOf(arguments.get(argument)).toString();
		case file:
			return base64(arguments.get(argument));
		default:
			break;
		}
		return arguments.get(argument);
	}
	
	private static String base64(String file){
		try{
			FileInputStream fileInputStreamReader = new FileInputStream(new File(file));
			byte[] bytes = new byte[(int)file.length()];
			fileInputStreamReader.read(bytes);
			fileInputStreamReader.close();
			return new String(Base64.encodeBase64(bytes));
		}catch(FileNotFoundException e){
			throw new IllegalArgumentException("File not found "+file);
		}catch(IOException e){
			throw new IllegalArgumentException("Problem reading file "+file);
		}
	}
}
