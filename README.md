
## Overview
ds-sim is a discrete-event simulator that has been developed primarily for leveraging scheduling algorithm design. It adopts a minimalist design explicitly taking into account modularity in that it uses the client-server model. The client-side simulator acts as a job scheduler while the server-side simulator simulates everything else including users (job submissions) and servers (job execution).

---
## How to run a simulation
1. run server `$ ds-server [OPTION]...`
2. run client `$ ds-client [-a algorithm] [OPTION]...`

## Usage
`$ ds-server -c ds-config01.xml -v brief`

`$ ds-client -a bf`

## Usage of MyClient
Go to the folder containing the client files.

- to compile: 'javac MyClient.java'
- to run: 'java MyClient'

Do not run by clicking 'run' within the java file.

## Concerning Test Files
All tests, given test files, passes. Sometimes, the ds-server says 'NOT PASSED' but the 'diff' command gives the correct info that matches with the ds-client.

## IMPORTANT!!
Sometimes, system being slow, the xml parser is not able to read the newly updated file after running 'AUTH' command. So, try to run the TEST AGAIN! and it will run smoothly.

Moreover, my current 'ds-server' is in src folder and the corresponding 'ds-system.xml' too.

## XML PARSER/READER
The 'xmlReader.java' first reads the 'ds-system.xml' and sends the data to the parser/'xmlParser.java'. XML parser then formats the data and returns the largest server based on number of cores. 

'xmlReader.java' currently gets the 'ds-system.xml' from './ds-system.xml' directory. If this gives an error, please update the directory to the new 'ds-system.xml'. Normally, 'ds-system.xml' is created/updated when the client sends the 'AUTH' command and completes authorisation. The 'ds-system.xml' is always created where the current/running 'ds-server' file is located. In this case it is the 'src' folder in 'COMP3100' project.