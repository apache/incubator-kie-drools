@echo off

rem Git clone the other repositories

call :initializeWorkingDirAndScriptDir

rem set startDateTime=`date +%s`

set gitUrlPrefix=https://github.com/kiegroup/
rem TODO dynamic gitUrlPrefix detection does not work on mac
rem cd $scriptDir
rem gitUrlPrefix=`git remote -v | grep --regex "^origin.*(fetch)$"`
rem gitUrlPrefix=`echo $gitUrlPrefix | sed 's/^origin\s*//g' | sed 's/droolsjbpm\-build\-bootstrap\.git\s*(fetch)$//g'`

set droolsjbpmOrganizationDir="%scriptDir%\..\.."
cd %droolsjbpmOrganizationDir%

for /F %%r in ('type %scriptDir%\repository-list.txt') do (
    echo.
    if exist %droolsjbpmOrganizationDir%\%%r ( 
        echo ===============================================================================
        echo This directory already exists: %%r
        echo ===============================================================================
    ) else (
        echo ===============================================================================
        echo Repository: %%r
        echo ===============================================================================
        call git clone %gitUrlPrefix%%%r.git %%r
        if "%ERRORLEVEL%" NEQ "0" (
            echo git clone failed
            goto:eof
        )
    )
)

rem ===============================================================================
rem This requieres SysInternals du command to be present (http://technet.microsoft.com/en-us/sysinternals/bb896651.aspx)
rem ===============================================================================
rem echo.
rem echo Disk size:

rem for /F %%r in ('type %scriptDir%\repository-list.txt') do (
rem     du -l 1 -q %%r 
rem )
rem ===============================================================================

rem set endDateTime=`date +%s`
rem set spentSeconds=`expr $endDateTime - $startDateTime`

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
