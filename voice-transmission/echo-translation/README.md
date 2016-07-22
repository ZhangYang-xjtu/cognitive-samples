
## [Voice Transmission - Echo Translation Recipe](https://developer.ibm.com/recipes/tutorials/translated-echo-leverage-watson-iot-platform-to-translate-voice/)

### Introduction
The Echo Translation Recipe helps the user to understand the process of continuously streaming the audio in one international language and have it processed through the language translation service, only to hear back the audio stream in another international language.

The Echo Translation Sample is written in Java and can be cloned or downloaded on to a Raspberry Pi Device, laptop or a server that has built-in Mic or allows connectivity from USB Mic. The sample can be executed on both Linux and Windows platform (the sample has not yet been tested on Mac platform).

### Requirements
* Software Requirements:
     * Bluemix Account
     * Maven
     * Git
* Hardware Requirements:
     * Raspberry Pi Device with atleast 8 GB SD Card
     * Head-set with Mic - USB Port Connectivity only

### Bluemix Services
The Echo Translation Sample relies on four Watson services to be Up & Running on Bluemix. 
* [Speech To Text](https://console.ng.bluemix.net/catalog/services/speech-to-text/?cm_mmc=developerWorks-_-dWdevcenter-_-recipes-_-lp)
* [Language Translation](https://console.ng.bluemix.net/catalog/services/language-translation/?cm_mmc=developerWorks-_-dWdevcenter-_-recipes-_-lp)
* [Text To Speech](https://console.ng.bluemix.net/catalog/services/text-to-speech/?cm_mmc=developerWorks-_-dWdevcenter-_-recipes-_-lp)
* [Watson IoT Platform](https://console.ng.bluemix.net/catalog/services/internet-of-things-platform/?cm_mmc=developerWorks-_-dWdevcenter-_-recipes-_-lp)
* [Node-RED Starter](https://console.ng.bluemix.net/catalog/starters/node-red-starter/)


### Seting up environment on the RPi Device

In this section, you will be assisted with steps to prepare the environment on the Raspberry Pi Device, on top of which, you will be running the Voice Transmission sample. 

Set up the Voice Transmission – Echo Translation sample on the Raspberry Pi, that forms the Device side execution of the sample. The steps are detailed as follows:

1. Obtain the IP Address of the Raspberry Pi device and connect to it using SSH

1. Setup the Raspbian environment with Maven and Git if not installed already, by installing them as follows:

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


### Edit the properties file 

In this section, you should familliarize yourself with the Device configuration parameters, whose values shall alter the course of this recipe, towards successful execution.

Now, on Raspberry Pi device, open up the Device properties file ‘device.properties’, located under ‘target/classes’. Edit the properties file, to update the Device Registration details, the Authentication credentials of Speech To Text service & Text To Speech service. Post editing the ‘device.properties’ file, the entries should look similar to the one shown below:

`
// Device Registration detail


Organization-ID = xxxxxx

Device-Type = iotsample-deviceType

Device-ID = Device02

Authentication-Method = token

Authentication-Token = xxxxxxxx


// Speech To Text Credentials

stt-username = xxxxxxxx-c7df-zzzz-a6fd-xyzxyzxyzxyz

stt-password = xyzxyzxyzxyz


// Text To Speech Credentials

tts-username = xxxxxxxx-c7df-zzzz-a6fd-xyzxyzxyzxyz

tts-password = xyzxyzxyzxyz


// Optional fields

Clean-Session = true

`


### Creating the Node-RED flow
 

This section briefs you on the steps that are needed to configure the Node-RED flow, that efficiently performs the Language Translation process.


1. Once the Node-RED Starter application service is created successfully in Bluemix, Open up the Application URL on the browser, or click on the Application URL on the Bluemix Application. This step should help open the Node-RED Interface.

    `http://<unique-application-name>.mybluemix.net`

2. The [Node-RED Flow](https://github.com/ibm-watson-iot/iot-cognitive-samples/blob/master/voice-transmission/echo-translation/Node-RED_Flow.txt) that makes up the Bluemix side execution, is made available on the [Github Repository](https://github.com/ibm-watson-iot/iot-cognitive-samples/blob/master/voice-transmission/echo-translation/Node-RED_Flow.txt). Copy the contents of the file and Import the Flow on to the Clipboard in the Node-RED application.

    `Import --> Clipboard --> <Paste the Contents> --> <Click Import>`

3. With the Node-RED Flow successfully imported, edit the IBM IoT In & Out Nodes, to fill up the details of Device ID, Device Type and the API Authentication credentials. Similarly, update the Watson Language Translation node with its authentication details.


###Initiating the Watson Voice translation
 

In this section, you shall be walked through the steps that shall help initiate the Voice Transmission – Echo Translation sample.

The following set of steps will kick start the execution of the Voice Transmission – Echo Translation sample:

1. Come back to the Raspberry Pi Device. Navigate to the source directory, where you had performed the Maven Build command, i.e 

    `cd iot-cognitive-samples/voice-transmission/echo-translation`

2. Execute the following maven execution command, to execute the Echo Translation sample:

    `mvn exec:java -Dexec.mainClass="com.ibm.watsoniot.FriendlyWatsonLanguageTranslator"`

3. As the execution is in progress, you should get to see a set of messages at the prompt, indicating that, the connection to the Watson IoT Platform is successfu. It then waits for the Audio Stream from the User. Place the Mic at a convenient distance and speak through it, to initiate the Audio Streaming. Within a second or two, you should hear back the Translated Echo.
