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
	echo "Launching the application in development mode - requires connection to controller (workbench)"
    cd MYSERVICE_NAME_MARKER
    cd target
    for /f "delims=" %%x in ('dir /od /b *.jar') do set latestjar=%%x
    cd ..
    call java -Dspring.profiles.active=dev -jar target\%latestjar%


:end
