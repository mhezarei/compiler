@echo off
REM Please adjust JFLEX_HOME to suit your needs
REM (please do not add a trailing backslash)

set JAVA_HOME=C:\Program Files\Java\jdk-10.0.1
set JFLEX_HOME=C:\Users\Hannah\Desktop\jflex-1.7.0\jflex-1.7.0
set JFLEX_VERSION=1.7.0

java -Xmx128m -jar "%JFLEX_HOME%"\lib\jflex-full-%JFLEX_VERSION%.jar %*
