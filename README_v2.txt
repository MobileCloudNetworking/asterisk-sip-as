This file contains the instructions on HOW TO RUN the test on Bart (multi-user, multi-calls)

UAS_IP:   192.168.85.115

UAC_IP:   192.168.85.112

AS_IP:    192.168.85.113

*_MGW_IP: 192.168.85.114



------ *_MGW -------

In Asterisk Media-Gateway VM (*_MGW) two main files have been set, sip.conf and extensions.conf.
These two files contains the data of 500 users, 250 Alice and 250 Bob (Alice1, Alice2, ..., Alice250
and Bob1, Bob2, ..., Bob250).

To connect at ASTERISK CLI the command is:

asterisk -r 

Once in, to set the sip debugger, the command is:

set sip debug on

And the command to restart the core of Asterisk is:

core restart now



------ UAS -------

There are two phases of test, the REGISTRATION and the CALLS.
It is easy to understand that the REGISTRATION phase must be run before the CALL one
In the REGISTRATION phase the UAS has to register the callees, in order to do that a
user.csv file has been created containing 250 Bob user, the methodology of registration
is SEQUENTIAL and the sipp command to run is:

sipp -rtp_echo -mp 6000 -p PORT  -mi UAS_IP -sf registerCallee.xml -inf user.csv -rsa AS_IP:AS_PORT -m 250  -nr UAC_IP:UAC_PORT

please refer to sipp documentation in order to well tune every parameter like number of call per second (cps) how to increase/decrease that etc.

Once the REGISTRATION phase is done is the turn on the CALLS one, in our scenario the callee will wait for a call from the caller.
The command to run the CALLS phase is:

sudo sipp -rtp_echo -mp 6000 -trace_msg  -p PORT -mi UAS_IP -sf uas_multi.xml -s sut -rsa AS_IP:AS:PORT -m 150  -nr  UAC_IP:UAC_PORT

as before, refer to sipp documentation in order to properly tune the options parameter



------ UAC -------

Like UAS, the UAS must run the REGISTRATION phase before the CALLS one. This phase is very similar to the UAS one with the only
difference that the UAC will register the caller. Following the command:

sipp -rtp_echo -mp 6000 -p PORT  -mi UAC_IP -sf registerCaller.xml -inf user.csv -rsa AS_IP:AS_PORT -l 1 -m 5 -nr UAS_IP:UAS_PORT

In the CALLS two files are used, caller.csv and calee.csv, these two file contain respectively the callers and the callees for the single
call, randomly picked.
This is the command to run in order to start the CALL:

sipp -rtp_echo -mp 6000 -p PORT  -mi UAC_IP -sf uac_pcap_multi.xml -inf caller.csv -inf callee.csv  -rsa AS_IP:AS_PORT -l 1 -m 1 -nr UAS_IP:UAS_PORT

---------------------

Useful link

sipp documentation:

http://sipp.sourceforge.net/doc/reference.html
With particular focus on "Online Help" section, super focus on "Call rate options"
