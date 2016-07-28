/**
 *****************************************************************************
 * Copyright (c) 2016 IBM Corporation and other Contributors.

 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Sathiskumar Palaniappan - Initial Contribution
 * Prasanna Alur Mathada - Initial Contribution
 * Jose Paul - Initial Contribution
 *****************************************************************************
 */
package com.ibm.watsoniot;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import com.google.gson.JsonObject;
import com.ibm.iotf.client.app.ApplicationClient;
import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechAlternative;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

/**
 * A Java application developed using the Watson Developer Cloud Java SDK, 
 * streams the voice from the user and invokes the Watson Speech to Text(STT) service 
 * to convert the voice to text. 
 * 
 * The returned text then parsed and mapped to one of the above commands. 
 * In this sample, we showcase a simple mapping wherein if a sentence contains a word “Red”, 
 * then it maps to the Red color command. The command is then sent to the Watson IoT Platform 
 * through MQTT. T
 * he Java application uses the Watson IoT java client library for sending the commands to the 
 * Watson IoT Platform.
 */
public class BB8VoiceController {
	
	private final static String PROPERTIES_FILE_NAME = "/application.properties";
	
	private ApplicationClient myClient;
	/**
	 * Get the Device Type and Device Id on behalf the application will publish the event
	 */
	private String deviceType;
	private String deviceId;
	private String sttUsername;
	private String sttPassword;
	
	/**
	 * Create and connect the application to Watson IoT Platform based on the credentials 
	 * specified in application.properties file.
	 */
	private void createApplication() {
		/**
	     * Load device properties
	     */
		Properties props = new Properties();
		try {
			props.load(BB8VoiceController.class.getResourceAsStream(PROPERTIES_FILE_NAME));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			System.exit(-1);
		}		
		
		//Instantiate and connect to IBM Watson IoT Platform
		
		try {
			myClient = new ApplicationClient(props);
			/**
			 * Get the Device Type and Device Id on behalf the application will publish the event
			 */
			deviceType = trimedValue(props.getProperty("Device-Type"));
			deviceId = trimedValue(props.getProperty("Device-ID"));
			this.sttUsername = trimedValue(props.getProperty("stt-username"));
			this.sttPassword = trimedValue(props.getProperty("stt-password"));
			myClient.connect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		BB8VoiceController lt = new BB8VoiceController();
		lt.createApplication();
		lt.streamVoice();
		// Run for an hour
		Thread.sleep(1000 * 3600);
	}
	
	/**
	 * Possible commands,
	 * 1. Command to change the color of the BB8 to Red, Green or Blue
	 * 2. Command to Spin
	 * 3. Command to Run
	 */
	 private enum COMMAND {
		 RED("RED"),
		 BLUE("BLUE"),
		 GREEN("GREEN"),
		 SPIN("SPIN"),
		 RUN("RUN");
		 
		 private final String command;

		 /**
		  * @param colour
		  */
		 private COMMAND(final String command) {
			 this.command = command;
		 }
		 
		 static COMMAND getCommand(String transcript) {
			 String capTranscript = transcript.toUpperCase();
			 if(capTranscript.contains(COMMAND.RUN.toString())) {
				 return COMMAND.RUN;
			 } else if(capTranscript.contains("SPIN") || capTranscript.contains("IN")) {
				 return COMMAND.SPIN;
			 } else if(capTranscript.contains(COMMAND.RED.toString()) || capTranscript.contains("READ")
					 || capTranscript.contains("RIGHT") || capTranscript.contains("GOOD")) {
				 return COMMAND.RED;
			 } else if(capTranscript.contains(COMMAND.BLUE.toString())
					 || capTranscript.contains("WE") || capTranscript.contains("BLEW")) {
				 return COMMAND.BLUE;
			 } else if(capTranscript.contains(COMMAND.GREEN.toString()) || capTranscript.contains("BEAN")
					 ||capTranscript.contains("MEAN")) {
				 return COMMAND.GREEN;
			 }
			 return null;
		 }
	}
	
	/**
	 * Core method that streams the voice and sends the command to Watson IoT Platform
	 * @throws LineUnavailableException
	 */
	private void streamVoice() throws LineUnavailableException {
	    SpeechToText service = new SpeechToText();
	    service.setUsernameAndPassword(this.sttUsername, this.sttPassword);
	
	    // Signed PCM AudioFormat with 16kHz, 16 bit sample size, mono
	    int sampleRate = 16000;
	    AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
	    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
	
	    if (!AudioSystem.isLineSupported(info)) {
	      System.out.println("Line not supported");
	      System.exit(0);
	    }
	
	    TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
	    line.open(format);
	    line.start();
	
	    AudioInputStream audio = new AudioInputStream(line);
	
	    RecognizeOptions options = new RecognizeOptions.Builder()
	      .continuous(true)
	      .interimResults(true)
	      .contentType(HttpMediaType.AUDIO_RAW + "; rate=" + sampleRate)
	      .build();
	
	    service.recognizeUsingWebSocket(audio, options, new BaseRecognizeCallback() {
	      @Override
	      /**
	       * Called when the speech is converted to text.
	       */
	      public void onTranscription(final SpeechResults speechResults) {
	    	  List<Transcript> transcripts = speechResults.getResults();
	    	  for(int i = 0; i < transcripts.size(); i++) {
	    		  Transcript transcript = transcripts.get(i);
	    		  if(transcript.isFinal()) {
	    			  // Proceed only if its final
	    			  List<SpeechAlternative> alternatives = transcript.getAlternatives();
	    			  for(int j = 0; j < alternatives.size(); j++) {
	    				  System.out.println("Got tanscript: "+ alternatives.get(j).getTranscript());
	    				  //Map it to the available command
	    				  COMMAND c = COMMAND.getCommand(alternatives.get(j).getTranscript());
	    				  if(c != null) {
	    					  JsonObject data = new JsonObject();
	    					  data.addProperty("option", c.toString());
	    					  System.out.println("Publishing command: "+ data.toString());
	    					  // Publish to Watson IoT Platform
	    					  myClient.publishCommand(deviceType, deviceId, "execute", data);
	    				  }
		    		  }
		    	  }
		      }
	      }
	    });
	}
	
	
	private static String trimedValue(String value) {
		if(value != null) {
			return value.trim();
		}
		return value;
	}

}