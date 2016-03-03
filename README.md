# asterisk-sip-as

Installation guide of the SIP AS 

# How to run the UAC

´´´
$ cd sipp-3.3/

$ sudo sipp -rtp_echo -mp 6000 -trace_err -trace_msg -rsa AS_ip_address:AsListeningPort -mb 5000 -d 5000 -s sut -sf uac_pcap.xml -l 1 -m 1 -nr UAS_ip_address:UasListeningPort
´´´

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