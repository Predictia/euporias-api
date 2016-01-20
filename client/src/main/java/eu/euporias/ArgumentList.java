package eu.euporias;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

public class ArgumentList {

	protected static enum ArgumentType{
		integer, string, file
	}
	
	protected static enum Argument{
		action(false), product, application, outcomeType, 
		mimeType, results(ArgumentType.file), param,
        stationLon, stationLat, stationName, variable,
        forecastStartTime, stationId,
        secret(false),user(false),
        proxyUrl(false),proxyPort(false),parameter, id;

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
	
	public String get(Argument argument) throws IOException {
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
	
	private static String base64(String urlStr) throws IOException {
		File file = new File(urlStr);
		if(file.exists()){
			return base64File(file.getAbsolutePath());
		}else{
			URL url = new URL(urlStr);
			File tmpFile = File.createTempFile("download-", "." + FilenameUtils.getExtension(urlStr));
			tmpFile.delete();
			tmpFile.deleteOnExit();
			downloadToFile(url, tmpFile);
			return base64File(tmpFile.getAbsolutePath());
		}
	}
	
	private static String base64File(String file) throws IOException {
		FileInputStream fis = new FileInputStream(new File(file));
		try{
			byte[] content = IOUtils.toByteArray(fis);
			return new String(Base64.encodeBase64(content));
		}finally{
			fis.close();
		}
	}
	
	private static void downloadToFile(URL source, File file) throws IOException{
		LOGGER.info("Downloading from " + source + " to file: " + file);
		ReadableByteChannel rbc = Channels.newChannel(source.openStream());
		FileOutputStream fos = new FileOutputStream(file);
		try{
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		}finally{
			fos.close();
		}		
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ArgumentList.class);
	
}
