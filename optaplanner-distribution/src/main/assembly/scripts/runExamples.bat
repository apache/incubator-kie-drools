@ECHO OFF

setLocal EnableDelayedExpansion
set mainClasspath=
for %%i in (binaries\*.jar) do (set mainClasspath=!mainClasspath!;%%i)
set mainClass=org.optaplanner.examples.app.OptaPlannerExamplesApp

echo "Usage: runExamples.bat"
echo "For example: runExamples.bat"
echo "Some notes:"
echo "- Java must be installed. Get the JRE (http://www.java.com) or the JDK."
echo "- For optimal performance, Java is recommended to be OpenJDK 7 or higher."
echo "- For JDK, the environment variable JAVA_HOME should be set to the JDK installation directory"
echo "  For example: set JAVA_HOME="C:\Program Files\Java\jdk1.6.0"
echo "- Working dir should be the directory of this script."
echo
echo "Starting examples app..."

rem You can use -Xmx128m or less too, but it might be slower
if exist "%JAVA_HOME%\bin\java.exe" (
    "%JAVA_HOME%\bin\java" -Xms256m -Xmx512m -server -cp %mainClasspath% %mainClass%
) else (
    java -Xms256m -Xmx512m -cp %mainClasspath% %mainClass%
    rem Prevent the terminal window to disappear before the user has seen the error message
    echo "Press any key to close this window."
    pause
)
