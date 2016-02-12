
            export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:./lib
        java -classpath ims-communicator.jar:jmf.jar:$CLASSPATH:$JMFHOME -Dlog4j.configuration=sip-communicator.properties net.java.sip.communicator.SipCommunicator