# asterisk-sip-as

Installation guide of the SIP AS 

# How to run the UAC

```
$ cd sipp-3.3/

$ sudo sipp -rtp_echo -mp 6000 -trace_err -trace_msg -rsa AS_ip_address:AsListeningPort -mb 5000 -d 5000 -s sut -sf uac_pcap.xml -l 1 -m 1 -nr UAS_ip_address:UasListeningPort
```

replace "AS_ip_address" with 192.168.85.113
	"AsListeningPort" with 5060
	"UAS_ip_address" with 192.168.85.115
	"UasListeningPort" with 5060
	
	
	
	
# How to run the UAS

```
$ cd sipp-3.3/

$ sipp -rtp_echo -mp 6000 -p 5060  -mi local_ip_address -sf uas_0.xml -rsa AS_ip_address:AsListeningPort -l 1 -m 1 -nr UAC_ip_address:UacListeningPort

$ sudo sipp -rtp_echo -mp 6000 -trace_msg  -p 5060 -mi local_ip_address -sf uas.xml -rsa AS_ip_address:AsListeningPort -l 1 -m 1 -nr UAC_ip_address:UacListeningPort
```

replace "local_ip_address" with 192.168.85.115
	"AS_ip_address" with 192.168.85.113
	"AsListeningPort" with 5060
	"UAC_ip_address" with 192.168.85.112
	"UacListeningPort" with 5060
	
#multi-users multi-calls scenario

For the multi-users multi-calls scenario some files have been added.

Uac:

user.csv
It is a csv file containing the callers that have to be registered. The first line is the picking mode, it can be SEQUENTIAL, RANDOM or USER. The records into user.csv are picked in SEQUENTIAL mode. 

registerCaller.xml
It is a file xml containing the sipp scenario that registers the callers to Asterisk

```
sipp -rtp_echo -mp 6000 -p 5060  -mi 192.168.85.112 -sf registerCaller.xml -inf user.csv -rsa 192.168.85.113:5060 -l 1 -m 5  -nr 192.168.85.115:5060
```
Run this command to register callers to Asterisk

The uac has to run the following command in order to start the calls. 
```
$ sipp -rtp_echo -mp 6000 -p 5060  -mi 192.168.85.112 -sf uac_pcap_multi.xml -inf caller.csv -inf callee.csv  -rsa 192.168.85.113:5060 -l 1 -m 1 -nr 192.168.85.115:5060

```
Uac_pcap_multi is the uac scenario file; caller and callee are two files csv cointaining respectively the caller and the callee for every single calls, picked in random mode. Throughout the last two params (-l -m), it is possible to handle the number of concurrent calls and their maximum number.

Uas:

user.csv
It is a csv file containing the callees that have to be registered. The first line is the picking mode, it can be SEQUENTIAL, RANDOM or USER. The records into user.csv are picked in SEQUENTIAL mode. 

registerCallee.xml
It is a file xml containing the sipp scenario that registers the callees to Asterisk

```
sipp -rtp_echo -mp 6000 -p 5060  -mi 192.168.85.112 -sf registerCallee.xml -inf user.csv -rsa 192.168.85.113:5060 -l 1 -m 5  -nr 192.168.85.112:5060
```
Run this command to register callees to Asterisk.

The uas has to run the following command in order to start listening calls.

```
$ sudo sipp -rtp_echo -mp 6000 -trace_msg  -p 5060 -mi 192.168.85.115 -sf uas_multi.xml -s sut -rsa 192.168.85.113:5060 -l 2 -m 2 -nr  192.168.8
```





