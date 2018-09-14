@echo off

set mavenInput="%*"

if "%*" == "" (
	echo No Maven arguments skipping maven build
) else (
	echo Running with user input: %mavenInput%
	echo Running maven build on available project

	call mvn -v >con

	cd ..

	for %%s in ("-model" "-kjar" "MYSERVICE_NAME_MARKER") do (

			cd *%%s
			echo ===============================================================================
            for %%I in (.) do echo %%~nxI
            echo ===============================================================================

			if exist "%M3_HOME%\bin\mvn.bat" (
				call %M3_HOME%\bin\mvn.bat %* >con
			) else (
				call mvn %* >con
			)

			cd ..

	)
)

goto :startapp

:startapp
	if not x%mavenInput:docker=%==x%mavenInput% (
		echo Launching the application as docker container...
		call docker run -d -p MYSERVICE_PORT_MARKER:MYSERVICE_PORT_MARKER --name MYSERVICE_NAME_MARKER apps/MYSERVICE_NAME_MARKER:MYSERVICE_VERSION_MARKER
	) else if not x%mavenInput:openshift=%==x%mavenInput% (
		echo Launching the application on OpenShift...
		call oc new-app MYSERVICE_NAME_MARKER:MYSERVICE_VERSION_MARKER
		call oc expose svc/MYSERVICE_NAME_MARKER
	) else (
		echo "Launching the application locally..."
		cd MYSERVICE_NAME_MARKER
		cd target
		for /f "delims=" %%x in ('dir /od /b *.jar') do set latestjar=%%x
		cd ..
		call java -jar target\%latestjar%
	)


:end
