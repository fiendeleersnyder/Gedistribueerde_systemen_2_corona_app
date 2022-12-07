# Gedistribueerde_systemen_2_corona_app

Volgorde van mains opstarten: Registrar, Mixing Proxy, Matching Service, Doctor, User

In de 2e branch zit een probeersel om de mixing proxy werkende te krijgen. In de terminal van de mixing proxy krijgen we geen errors meer. In de terminal van de matching service krijgen we nu volgende error:

java.rmi.ConnectIOException: error during JRMP connection establishment; nested exception is: 
	javax.net.ssl.SSLHandshakeException: Remote host terminated the handshake
	at java.rmi/sun.rmi.transport.tcp.TCPChannel.createConnection(TCPChannel.java:308)
	at java.rmi/sun.rmi.transport.tcp.TCPChannel.newConnection(TCPChannel.java:204)
	at java.rmi/sun.rmi.server.UnicastRef.newCall(UnicastRef.java:344)
	at java.rmi/sun.rmi.registry.RegistryImpl_Stub.lookup(RegistryImpl_Stub.java:116)
	at MatchingService_implementation.<init>(MatchingService_implementation.java:18)
	at Main.startMatchingService(Main.java:11)
	at Main.main(Main.java:22)
Caused by: javax.net.ssl.SSLHandshakeException: Remote host terminated the handshake
	at java.base/sun.security.ssl.SSLSocketImpl.handleEOF(SSLSocketImpl.java:1709)
	at java.base/sun.security.ssl.SSLSocketImpl.decode(SSLSocketImpl.java:1508)
	at java.base/sun.security.ssl.SSLSocketImpl.readHandshakeRecord(SSLSocketImpl.java:1415)
	at java.base/sun.security.ssl.SSLSocketImpl.startHandshake(SSLSocketImpl.java:450)
	at java.base/sun.security.ssl.SSLSocketImpl.ensureNegotiated(SSLSocketImpl.java:915)
	at java.base/sun.security.ssl.SSLSocketImpl$AppOutputStream.write(SSLSocketImpl.java:1285)
	at java.base/java.io.BufferedOutputStream.flushBuffer(BufferedOutputStream.java:81)
	at java.base/java.io.BufferedOutputStream.flush(BufferedOutputStream.java:142)
	at java.base/java.io.DataOutputStream.flush(DataOutputStream.java:128)
	at java.rmi/sun.rmi.transport.tcp.TCPChannel.createConnection(TCPChannel.java:230)
	... 6 more
Caused by: java.io.EOFException: SSL peer shut down incorrectly
	at java.base/sun.security.ssl.SSLSocketInputRecord.read(SSLSocketInputRecord.java:483)
	at java.base/sun.security.ssl.SSLSocketInputRecord.readHeader(SSLSocketInputRecord.java:472)
	at java.base/sun.security.ssl.SSLSocketInputRecord.decode(SSLSocketInputRecord.java:160)
	at java.base/sun.security.ssl.SSLTransport.decode(SSLTransport.java:111)
	at java.base/sun.security.ssl.SSLSocketImpl.decode(SSLSocketImpl.java:1500)
	... 14 more

De error komt er op lijn 18 van de matchingservice implementatie. Op deze lijn wordt er verbinding gemaakt met de mixing proxy
