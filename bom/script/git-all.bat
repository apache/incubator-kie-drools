@echo off

rem Runs a git command on all kiegroup repositories.

call :initializeWorkingDirAndScriptDir
 
if  "%*" == "" (
    echo.
    echo Usage:
    echo   %~n0%~x0 [arguments of git]
    echo For example:
    echo   %~n0%~x0 fetch
    echo   %~n0%~x0 pull --rebase
    echo   %~n0%~x0 commit -m"JIRAKEY-1 Fix typo"
    echo.
    goto:eof
)

rem set startDateTime=`date +%s`

set droolsjbpmOrganizationDir="%scriptDir%\..\.."
cd %droolsjbpmOrganizationDir%

for /F %%r in ('type %scriptDir%\repository-list.txt') do (
    echo.
    if exist %droolsjbpmOrganizationDir%\%%r ( 
        echo ===============================================================================
        echo Repository: %%r
        echo ===============================================================================
        cd %%r
        call git %*
        set returnCode=%ERRORLEVEL%
        cd ..
        if "%returnCode%" neq "0" (
            echo git failed: %returnCode%
            goto :end
        )
    ) else (
        echo ===============================================================================
        echo Missing Repository: %%r. Skipping
        echo ===============================================================================
    )
)

:end 

rem endDateTime=`date +%s`
rem spentSeconds=`expr $endDateTime - $startDateTime`

echo.
echo Total time: %spentSeconds%s
goto:eof

:initializeWorkingDirAndScriptDir 
    rem Set working directory and remove all symbolic links
    FOR /F %%x IN ('cd') DO set workingDir=%%x

    rem Go the script directory
    for %%F in (%~f0) do set scriptDir=%%~dpF
    rem strip trailing \
    set scriptDir=%scriptDir:~0,-1%
goto:eof
