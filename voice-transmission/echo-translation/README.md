# Voice Transmission - Echo Translation Recipe

## Seting up environment on the RPi Device

In this section, you will be assisted with steps to prepare the environment on the Raspberry Pi Device, on top of which, you will be running the Voice Transmission sample. 

Set up the Voice Transmission – Echo Translation sample on the Raspberry Pi, that forms the Device side execution of the sample. The steps are detailed as follows:

1. Obtain the IP Address of the Raspberry Pi device and connect to it using SSH

2. Setup the Raspbian environment with Maven and Git if not installed already, by installing them as follows:

`sudo apt-get update
sudo apt-get install maven`

3. Git is present in the latest Raspbian OS and hence the users need not install it again. However, if the users have an older version of Raspbian and do not see Git being part of it, then, make use of the following command to install the Git automatically using:

`sudo apt-get install git-all`

4. Obtain the Voice Transmission sample by cloning the iot-cognitive-samples project using git clone as follows:

`git clone https://github.com/ibm-watson-iot/iot-cognitive-samples.git`

5. We are demonstrating the Voice Transmission – Echo Translation sample in this recipe. Hence, navigate to the source directory structure of ‘echo-translation’ within ‘voice-transmission’ directory, under iot-cognitive-samples-master directory structure, as shown below:

`cd iot-cognitive-samples/voice-transmission/echo-translation`

6. Build the Echo Translation Sample using the Maven command

`mvn clean package -Dmaven.test.skip=true`
 
Monitor the messages on the command prompt, as the build process progresses. Ensure that the build process is concluded successfully without any errors or issues. A successful build would have now generated a new directory, by name '_target/classes_', that holds all the compiled files.


## Edit the properties file 

In this section, you should familliarize yourself with the Device configuration parameters, whose values shall alter the course of this recipe, towards successful execution.

Now, on Raspberry Pi device, open up the Device properties file ‘device.properties’, located under ‘target/classes’. Edit the properties file, to update the Device Registration details, the Authentication credentials of Speech To Text service & Text To Speech service. Post editing the ‘device.properties’ file, the entries should look similar to the one shown below:

`
// Device Registration detail


Organization-ID = xxxxxx


Device-Type = iotsample-deviceType


Device-ID = Device02


Authentication-Method = token


Authentication-Token = xxxxxxxx

// Optional fields

Clean-Session = true

// Speech To Text Credentials

stt-username = xxxxxxxx-c7df-zzzz-a6fd-xyzxyzxyzxyz


stt-password = xyzxyzxyzxyz

// Text To Speech Credentials

tts-username = xxxxxxxx-c7df-zzzz-a6fd-xyzxyzxyzxyz


tts-password = xyzxyzxyzxyz
`


## Creating the Node-RED flow
 

This section briefs you on the steps that are needed to configure the Node-RED flow, that efficiently performs the Language Translation process.


An Node-RED application was created by you, as you covered the section ‘Create Node-RED application and Watson services in Bluemix’. Open up the Application URL on the browser, or click on the Application URL on the Bluemix Application. This step should help open the Node-RED Interface.

`http://<unique-application-name>.mybluemix.net`

The [Node-RED Flow](https://github.com/ibm-watson-iot/iot-cognitive-samples/blob/master/voice-transmission/echo-translation/Node-RED_Flow.txt) that makes up the Bluemix side execution, is made available on the [Github Repository](https://github.com/ibm-watson-iot/iot-cognitive-samples/blob/master/voice-transmission/echo-translation/Node-RED_Flow.txt). Copy the contents of the file and Import the Flow on to the Clipboard in the Node-RED application.

`Import --> Clipboard --> <Paste the Contents> --> <Click Import>`


##Initiating the Watson Voice translation
 

In this section, you shall be walked through the steps that shall help initiate the Voice Transmission – Echo Translation sample.

The following set of steps will kick start the execution of the Voice Transmission – Echo Translation sample:

Come back to the Raspberry Pi Device. Navigate to the source directory, where you had performed the Maven Build command, i.e 

`cd iot-cognitive-samples/voice-transmission/echo-translation`

Execute the following maven execution command, to execute the Echo Translation sample:

`mvn exec:java -Dexec.mainClass="com.ibm.watsoniot.FriendlyWatsonLanguageTranslator"`

