# Gedistribueerde_systemen_2_corona_app

Volgorde van mains opstarten: Registrar, Mixing Proxy, Matching Service, Doctor, User

In de 2e branch zit een probeersel om de mixing proxy werkende te krijgen. Hierbij krijgen we bij het opstarten van de matching service een error in de mixing proxy terminal 
namelijk: WARNING: RMI TCP Accept-2019: accept loop for ServerSocket[addr=0.0.0.0/0.0.0.0,localport=2019] throws
java.net.SocketException: java.security.NoSuchAlgorithmException: Error constructing implementation (algorithm: Default, provider: SunJSSE, class: sun.security.ssl.SSLContextImpl$DefaultSSLContext)

De matching service start ook net succesvol op waardoor de doctor niet kan worden opgestart
